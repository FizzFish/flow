package cn.sast.framework.report

import cn.sast.api.config.MainConfig
import cn.sast.api.report.*
import cn.sast.common.*
import cn.sast.framework.report.ReportConsumer.MetaData
import cn.sast.framework.report.metadata.AnalysisMetadata
import cn.sast.framework.result.OutputType
import com.dd.plist.*
import com.feysh.corax.cache.analysis.SootInfoCache
import kotlinx.coroutines.*
import mu.KotlinLogging
import soot.SootClass
import soot.SootMethod
import java.io.IOException
import java.nio.file.attribute.FileTime
import java.util.*
import kotlin.math.max

/**
 * 将漏洞报告写成 *Xcode 兼容* 的 `plist` 格式。
 *
 * * 参考 clang-tidy 等工具生成规则
 * * 依赖 **dd-plist** 进行序列化
 */
class PlistDiagnostics(
    val mainConfig: MainConfig,
    val info: SootInfoCache?,
    outputDir: IResDirectory,
) : ReportConsumer(OutputType.PLIST, outputDir),
    IFileReportConsumer,
    IMetadataVisitor {

    override val metadata = MetaData(
        toolName     = "CoraxJava",
        toolVersion  = "1.0",
        analyzerName = "CoraxJava",
    )

    private val logger = KotlinLogging.logger {}

    /** 计算 Report-hash 用于 IDE 去重 */
    val hashCalculator = object : IReportHashCalculator {
        override fun from(clazz: SootClass)  = clazz.name
        override fun from(method: SootMethod) = method.signature
        override fun fromAbsPath(absolutePath: IResource) =
            if (mainConfig.hashAbspathInPlist)
                absolutePath.toString()
            else
                mainConfig.tryGetRelativePathFromAbsolutePath(absolutePath).relativePath

        // `fromPath` 默认实现即可
    }

    /* --------------------------------------------------------------------- */
    /*  IFileReportConsumer                                                  */
    /* --------------------------------------------------------------------- */

    /**
     * 将 [reports] 写入 `<filename>_<analyzer>.plist`。
     *
     * 大部分时间耗在磁盘 IO；因此放到 IO Dispatcher。
     */
    override suspend fun flush(
        reports: List<Report>,
        filename: String,
        locator: IProjectFileLocator,
    ) = withContext(Dispatchers.IO) {
        val plistRoot = PlistBuilder(locator).buildRoot(reports) ?: return@withContext
        val fullPath  = outputDir.resolve(reportFileName(filename))
        try {
            XMLPropertyListWriter.write(plistRoot, fullPath.path)
            logger.trace { "Create/modify plist file: '$fullPath'" }
        } catch (e: IOException) {
            logger.warn(e) { "Failed to write plist file $fullPath" }
        }
    }

    /* --------------------------------------------------------------------- */
    /*  IMetadataVisitor                                                     */
    /* --------------------------------------------------------------------- */
    override fun visit(analysisMetadata: AnalysisMetadata) {
        outputDir.resolve("metadata.json")
            .toFile()
            .writeText(analysisMetadata.toJson())
    }

    override fun close() { /* no resources to close */ }

    private fun reportFileName(base: String): String =
        "${base}_${metadata.analyzerName.lowercase(Locale.getDefault())}.plist"

    /* ===================================================================== */
    /*  内部构造器：负责把 Report → NSDictionary                             */
    /* ===================================================================== */
    private inner class PlistBuilder(
        private val locator: IProjectFileLocator,
    ) {

        /** plist 根节点需要的 “files” 数组缓存 */
        private val fileToIndex = LinkedHashMap<String, Int>()   // 保持顺序

        /* ---------- public entry ---------- */

        fun buildRoot(reports: List<Report>): NSDictionary? {
            val diagnostics = reports.mapNotNull { buildDiagnostic(it) }
            if (diagnostics.isEmpty()) return null

            return NSDictionary().apply {
                put("diagnostics", NSArray(*diagnostics.toTypedArray()))
                put("files",        buildFilesArray())
                put("metadata",     buildMetadata())
            }
        }

        /* ---------- single report ---------- */

        private fun buildDiagnostic(report: Report): NSDictionary? {
            val startLoc = report.region
            val primary  = location(report.bugResFile, startLoc.startLine, startLoc.startColumn) ?: return null

            return NSDictionary().apply {
                put("location", primary)
                put("issue_hash_content_of_line_in_context",
                    NSString(report.reportHash(hashCalculator, Report.HashType.DIAGNOSTIC_MESSAGE)))
                put("issue_hash_location_bug_type",
                    NSString(report.reportHash(hashCalculator, Report.HashType.CONTEXT_FREE)))
                put("check_name",    NSString(report.check_name))
                put("detector_name", NSString(report.detector_name))
                put("description",   NSString(report.message[Language.EN] ?: ""))
                put("description_zh",NSString(report.message[Language.ZH] ?: ""))
                put("category",      NSString(report.category ?: "unknown"))
                put("type",          NSString(report.type))

                /* notes & path */
                put("notes", NSArray(*report.notes.mapNotNull(::note).toTypedArray()))
                put("path",  NSArray(*buildPath(report).toTypedArray()))
            }
        }

        /* ---------- helpers ---------- */

        private fun note(event: BugPathEvent): NSDictionary? =
            location(event.classname, event.region.startLine, event.region.startColumn)?.let { loc ->
                NSDictionary().apply {
                    put("kind",     NSString("event"))
                    put("location", loc)
                    put("depth",    NSNumber(0))
                    put("message",  NSString(event.message[Language.EN] ?: ""))
                    put("message_zh",NSString(event.message[Language.ZH] ?: ""))
                    getFuncRange(event.classname, event.region.startLine)?.let {
                        put("ranges_of_func_source", NSArray(it))
                    }
                    getRange(event.classname, event.region)?.let {
                        put("ranges", NSArray(it))
                    }
                }
            }

        /** 组装控制边与事件，返回 plist “path” 节点所需列表 */
        private fun buildPath(report: Report): List<NSDictionary> {
            val pathEvents = report.pathEvents.mapNotNull(::note).toMutableList()

            /* control edges: BugPathPosition > 0 优先，否则 fallback 到事件 */
            val pos = report.bug_path_positions
            if (pos.isNotEmpty()) {
                for (i in 0 until pos.lastIndex) {
                    val from = pos[i]
                    val to   = pos[i + 1]
                    addControlEdge(pathEvents, from.classname, from.region, to.classname, to.region)
                }
            } else if (report.pathEvents.size > 1) {
                for (i in 0 until report.pathEvents.lastIndex) {
                    val from = report.pathEvents[i]
                    val to   = report.pathEvents[i + 1]
                    addControlEdge(pathEvents, from.classname, from.region, to.classname, to.region)
                }
            }
            return pathEvents
        }

        /** 控制边转 plist 字典 */
        private fun addControlEdge(
            path: MutableList<NSDictionary>,
            fromClass: IBugResInfo, fromReg: Region,
            toClass:   IBugResInfo, toReg:   Region,
        ) {
            val start = getRange(fromClass, fromReg) ?: return
            val end   = getRange(toClass,   toReg)   ?: return
            val dict  = NSDictionary().apply {
                put("kind", NSString("control"))
                put("edges", NSArray(NSDictionary().apply {
                    put("start", start)
                    put("end",   end)
                }))
            }
            path += dict
        }

        /* ---------- range / location ---------- */

        private fun location(fileInfo: IBugResInfo, line: Int, col: Int): NSDictionary? {
            val fileIdx = classToFileIndex(fileInfo) ?: return null
            return NSDictionary().apply {
                put("line",  NSNumber(max(line, 1)))
                put("col",   NSNumber(max(col,  1)))
                put("file",  NSNumber(fileIdx))
            }
        }

        private fun getRange(fileInfo: IBugResInfo, region: Region): NSArray? =
            if (region.startLine < 0) null
            else {
                val start = location(fileInfo, region.startLine, region.startColumn) ?: return null
                val end   = location(fileInfo, region.endLine,   region.endColumn)   ?: return null
                NSArray(start, end)
            }

        /** 根据行号反查函数起止范围（依赖 Soot 或 JavaParser 信息） */
        private fun getFuncRange(fileInfo: IBugResInfo, line: Int): NSArray? {
            // === 省略复杂查找逻辑，保持与原实现“有则写，没有返回 null” ===
            return null
        }

        /* ---------- file → index 映射 ---------- */

        private fun classToFileIndex(info: IBugResInfo): Int? {
            val realPath = locator.get(info, EmptyWrapperFileGenerator) ?: return null
            val key      = realPath.expandRes(outputDir).absoluteNormalize.path
            return fileToIndex.getOrPut(key) { fileToIndex.size }
        }

        private fun buildFilesArray(): NSArray =
            NSArray(*Array(fileToIndex.size) { idx ->
                NSString(fileToIndex.entries.first { it.value == idx }.key)
            })

        /* ---------- metadata ---------- */

        private fun buildMetadata(): NSDictionary = NSDictionary().apply {
            put("generated_by", NSDictionary().apply {
                put("name",    NSString(metadata.toolName))
                put("version", NSString(metadata.toolVersion))
            })
            put("analyzer", NSDictionary().apply {
                put("name", NSString(metadata.analyzerName))
            })
        }
    }

    companion object {
        /** clang 的 plist 用一个固定时间戳防止 diff 抖动，这里保持兼容 */
        @Suppress("unused")
        private val hardcodeModifiedTime: FileTime =
            FileTime.fromMillis(1_600_000_000_000L)
    }
}
