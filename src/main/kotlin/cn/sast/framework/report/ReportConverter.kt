package cn.sast.framework.report

import cn.sast.api.config.MainConfig
import cn.sast.api.config.ScanFilter
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.ProjectMetrics
import cn.sast.api.report.Report
import cn.sast.api.util.IMonitor
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.ResourceKt
import cn.sast.framework.report.coverage.JacocoCompoundCoverage
import cn.sast.idfa.progressbar.ProgressBarExt
import kotlinx.coroutines.*
import mu.KotlinLogging
import soot.Scene
import soot.SootClass
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import kotlin.io.path.exists

/**
 * 将内存中的 [Report] 列表写出到多个 [IReportConsumer]，
 * 同时产出“缺少类定义的源码文件”辅助清单。
 */
class ReportConverter(
    private val mainConfig: MainConfig,
    private val progressBarExt: ProgressBarExt = ProgressBarExt(0, 0, 3)
) {
    private val logger = KotlinLogging.logger {}

    /* -------------------------------------------------------------------- */
    /*  收集源文件                                                           */
    /* -------------------------------------------------------------------- */

    /** 过滤掉 Gradle/KTS、package-info 等无需统计的源码 */
    private fun filterSourceFiles(sources: Collection<IResFile?>): Set<IResFile> =
        sources.filterNotNull()
            .filter { f ->
                val name = f.name
                when {
                    // *.kts 并且文件名含 gradle -> 跳过
                    f.extension == "kts" && name.contains("gradle", ignoreCase = true) -> false
                    // package-info.java / package-info.kt -> 跳过
                    name.contains("package-info", ignoreCase = true)                    -> false
                    // ScanFilter 显式跳过
                    ScanFilter.getActionOf(
                        mainConfig.scanFilter,
                        filePath = f.path
                    ) == ScanFilter.ScanAction.Skip                                           -> false
                    else -> true
                }
            }
            .toSet()

    /**
     * 遍历所有 **Java/Kotlin** 扩展，异步收集项目源码。
     * `IProjectFileLocator` 已对文件系统做了索引缓存，调用代价很小。
     */
    private suspend fun findAllSourceFiles(
        locator: IProjectFileLocator
    ): Set<IResFile> = coroutineScope {
        val tasks = ResourceKt.javaExtensions.map { ext ->
            async {
                locator.getByFileExtension(ext)
                    .filter { mainConfig.autoAppSrcInZipScheme || it.isFileScheme() }
                    .toSet()
            }
        }
        tasks.awaitAll().flatten().toSet()
    }

    /* -------------------------------------------------------------------- */
    /*  源码 ↔ SootClass 匹配                                               */
    /* -------------------------------------------------------------------- */

    /**
     * 统计哪些源码文件 **未能** 在 Soot Scene 中找到对应类。
     *
     * @return Pair(first = 已匹配类的源码, second = 未匹配源码)
     */
    private fun reportSourceFileWhichClassNotFound(
        allSourceFiles: Set<IResFile>,
        outputDir: IResDirectory,
        locator: IProjectFileLocator
    ): Pair<Set<IResFile>, Set<IResFile>> {

        /* ---- 1) 更新项目指标 ---- */
        (mainConfig.monitor ?: IMonitor.EMPTY).projectMetrics
            ?.totalSourceFileNum = allSourceFiles.size.toLong()

        /* ---- 2) 收集 _非 phantom_ 的类对应源码 ---- */
        val sootClasses = buildList {
            addAll(Scene.v().applicationClasses)
            addAll(Scene.v().libraryClasses)
        }.filterNot(SootClass::isPhantom)

        val foundSourceCodes = sootClasses
            .mapNotNull { locator.get(ClassResInfo(it), NullWrapperFileGenerator) }
            .toSet()

        /* ---- 3) 计算缺失集合 ---- */
        val missing = allSourceFiles - foundSourceCodes
        val reportFile = outputDir.resolve("source_files_which_class_not_found.txt").toFile()

        if (missing.isNotEmpty()) {
            logger.warn {
                "Incomplete analysis! ${missing.size} source files not matched with any class. " +
                        "See: ${reportFile.absoluteNormalize}"
            }

            reportFile.parentFile.mkdirs()
            OutputStreamWriter(
                Files.newOutputStream(reportFile.toPath()),
                StandardCharsets.UTF_8
            ).use { writer ->
                missing.sortedBy { it.absoluteNormalize.path }
                    .forEach { writer.write(it.absoluteNormalize.path + "\n") }
            }
        } else {
            Files.deleteIfExists(reportFile.toPath())
        }

        return foundSourceCodes to missing
    }

    /* -------------------------------------------------------------------- */
    /*  顶层协程——协调各消费端                                               */
    /* -------------------------------------------------------------------- */

    suspend fun flush(
        locator:    IProjectFileLocator,
        coverage:   JacocoCompoundCoverage,
        consumers:  List<IReportConsumer>,
        reports:    Collection<Report>,
        outputDir:  IResDirectory
    ) = coroutineScope {

        /* 1) 源码收集 + 匹配 */
        val allSourceFiles = findAllSourceFiles(locator)
        val filteredSource = filterSourceFiles(allSourceFiles)
        val (matched, unmatched) = reportSourceFileWhichClassNotFound(
            filteredSource, outputDir, locator
        )

        progressBarExt.total = reports.size.toLong()

        /* 2) 初始化所有输出器 */
        consumers.forEach { it.init() }

        /* 3) 并行执行各输出器 */
        consumers.map { consumer ->
            launch {
                when (consumer) {
                    is IFileReportConsumer -> consumer.flush(reports.toList(), "diagnostics", locator)
                    else                   -> consumer.run(locator)
                }
            }
        }.joinAll()

        /* 4) 打印简单统计 */
        logger.info {
            "Report converted. matched=${matched.size}, unmatched=${unmatched.size}, totalConsumer=${consumers.size}"
        }
    }
}

/**
 * **Kotlin 反射小工具**：
 * 通过字段名读取私有属性；若不存在返回 `null`。
 *
 * 用法：
 * ```kotlin
 * val counter: Int? = someObj.getField("counter")
 * ```
 */
@Suppress("UNCHECKED_CAST")
@JvmSynthetic
inline fun <reified T> Any.getField(name: String): T? =
    runCatching {
        val field = javaClass.getDeclaredField(name).apply { isAccessible = true }
        field.get(this) as? T
    }.getOrNull()
