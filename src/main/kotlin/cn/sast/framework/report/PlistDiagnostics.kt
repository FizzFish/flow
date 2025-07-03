package cn.sast.framework.report

import cn.sast.api.config.MainConfig
import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.BugPathPosition
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.api.report.IReportHashCalculator
import cn.sast.api.report.Report
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.ResourceKt
import cn.sast.framework.report.ReportConsumer.MetaData
import cn.sast.framework.report.metadata.AnalysisMetadata
import cn.sast.framework.result.OutputType
import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.NSNumber
import com.dd.plist.NSObject
import com.dd.plist.NSString
import com.dd.plist.XMLPropertyListWriter
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.cache.analysis.SootLineToMethodMapFactory
import com.feysh.corax.cache.analysis.SootMethodAndRange
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import com.github.javaparser.Range
import com.github.javaparser.ast.body.BodyDeclaration
import java.io.IOException
import java.nio.file.attribute.FileTime
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.Locale
import java.util.Optional
import java.util.Map.Entry
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import soot.SootClass
import soot.SootMethod

class PlistDiagnostics(
    val mainConfig: MainConfig,
    val info: SootInfoCache?,
    outputDir: IResDirectory
) : ReportConsumer(OutputType.PLIST, outputDir), IFileReportConsumer, IMetadataVisitor {

    override val metadata: MetaData
        get() = MetaData("CoraxJava plist report", "1.0", "CoraxJava")

    val hashCalculator: IReportHashCalculator = object : IReportHashCalculator(this) {
        override fun from(clazz: SootClass): String = clazz.name

        override fun from(method: SootMethod): String = method.signature

        override fun fromAbsPath(absolutePath: IResource): String =
            if (this@PlistDiagnostics.mainConfig.hashAbspathInPlist) {
                absolutePath.toString()
            } else {
                this@PlistDiagnostics.mainConfig.tryGetRelativePathFromAbsolutePath(absolutePath).relativePath
            }

        override fun fromPath(path: IResource): String = IReportHashCalculator.DefaultImpls.fromPath(this, path)
    }

    override suspend fun flush(reports: List<Report>, filename: String, locator: IProjectFileLocator) {
        val fullPath = outputDir.resolve(getReportFileName(filename))
        val root = PlistDiagnosticImpl(metadata, locator).getRoot(reports) ?: return

        try {
            BuildersKt.withContext(Dispatchers.IO) {
                XMLPropertyListWriter.write(root, fullPath.path)
            }
        } catch (e: IOException) {
            // Ignore
        }

        logger.trace { "Create/modify plist file: '$fullPath'" }
    }

    override fun visit(analysisMetadata: AnalysisMetadata) {
        ResourceKt.writeText(
            outputDir.resolve("metadata.json").toFile(),
            analysisMetadata.toJson(),
            null,
            2
        )
    }

    private fun getReportFileName(fileName: String): String {
        val analyzerName = metadata.analyzerName.toLowerCase(Locale.getDefault())
        return "$fileName_$analyzerName.plist"
    }

    override fun close() {}

    companion object {
        private val hardcodeModifiedTime: FileTime
        private val logger: KLogger
    }

    @SourceDebugExtension([
        "SMAP\nPlistDiagnostics.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PlistDiagnostics.kt\ncn/sast/framework/report/PlistDiagnostics\$PlistDiagnosticImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 4 Region.kt\ncom/feysh/corax/config/api/report/Region\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 6 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 7 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,237:1\n1#2:238\n1#2:247\n1#2:251\n1#2:264\n1#2:281\n381#3,7:239\n59#4:246\n57#4:248\n1619#5:249\n1863#5:250\n1864#5:252\n1620#5:253\n1611#5,9:254\n1863#5:263\n1864#5:265\n1620#5:266\n1611#5,9:271\n1863#5:280\n1864#5:282\n1620#5:283\n37#6,2:267\n37#6,2:269\n37#6,2:284\n216#7,2:286\n*S KotlinDebug\n*F\n+ 1 PlistDiagnostics.kt\ncn/sast/framework/report/PlistDiagnostics\$PlistDiagnosticImpl\n*L\n65#1:247\n138#1:251\n158#1:264\n182#1:281\n54#1:239,7\n65#1:246\n65#1:248\n138#1:249\n138#1:250\n138#1:252\n138#1:253\n158#1:254,9\n158#1:263\n158#1:265\n158#1:266\n182#1:271,9\n182#1:280\n182#1:282\n182#1:283\n173#1:267,2\n174#1:269,2\n186#1:284,2\n188#1:286,2\n*E\n"
    ])
    inner class PlistDiagnosticImpl(
        private val metadata: MetaData,
        private val classToFileName: IProjectFileLocator
    ) {
        private val fileToIndex: MutableMap<String, Int> = LinkedHashMap()

        private fun classToFileIndex(classInfo: IBugResInfo): Int? {
            val file = classToFileName.get(classInfo, EmptyWrapperFileGenerator.INSTANCE) ?: return null
            val path = file.expandRes(this@PlistDiagnostics.outputDir).absolute.normalize.path.toString()
            return fileToIndex.getOrPut(path) { fileToIndex.size }
        }

        private fun getLocation(classInfo: IBugResInfo, line: Int, column: Int): NSDictionary? {
            return classToFileIndex(classInfo)?.let { fileIndex ->
                NSDictionary().apply {
                    put("line", NSNumber(line))
                    put("col", NSNumber(column))
                    put("file", NSNumber(fileIndex))
                }
            }
        }

        private fun getRange(classInfo: IBugResInfo, region: Region): NSArray? {
            if (region.startLine < 0) return null
            
            val startLoc = getLocation(classInfo, region.startLine, region.startColumn) ?: return null
            val endLoc = getLocation(classInfo, region.endLine, region.endColumn) ?: return null
            return NSArray(arrayOf(startLoc, endLoc))
        }

        private fun getFuncRange(classInfo: IBugResInfo, line: Int): NSArray? {
            if (classInfo !is ClassResInfo) return null

            val sc = (classInfo as ClassResInfo).sc
            info?.let {
                it.getMemberAtLine(sc, line)?.range?.let { range ->
                    if (range.isPresent) {
                        val r = range.get()
                        return getFuncRange(Pair(r.begin.line, r.end.line), classInfo)
                    }
                }
            }

            val methodAndRange = SootLineToMethodMapFactory.getSootMethodAtLine(sc, line, false)
            methodAndRange?.let {
                val (start, end) = it.range
                return getFuncRange(Pair(start - 1, end + 1), classInfo)
            }

            return null
        }

        private fun getFuncRange(range: Pair<Int, Int>, classInfo: IBugResInfo): NSArray? {
            val (startLn, endLn) = range
            if (startLn < 0 || endLn < 0) return null

            val startLoc = getLocation(classInfo, startLn, -1) ?: return null
            val endLoc = getLocation(classInfo, endLn, -1) ?: return null
            return NSArray(arrayOf(startLoc, endLoc))
        }

        private fun getNote(event: BugPathEvent): NSDictionary? {
            return getLocation(event.classname, event.region.startLine, event.region.startColumn)?.let { loc ->
                NSDictionary().apply {
                    put("kind", NSString("event"))
                    put("location", loc)
                    put("depth", NSNumber(0))
                    put("message", NSString(event.message[Language.EN]))
                    put("message_zh", NSString(event.message[Language.ZH]))
                    
                    getFuncRange(event.classname, event.region.startLine)?.let {
                        put("ranges_of_func_source", NSArray(arrayOf(it)))
                    }
                    
                    getRange(event.classname, event.region)?.let {
                        put("ranges", NSArray(arrayOf(it)))
                    }
                }
            }
        }

        private fun getControlEdge(
            fromClass: IBugResInfo,
            fromRange: Region,
            toClass: IBugResInfo,
            toRange: Region
        ): NSDictionary? {
            val fromRangeArray = getRange(fromClass, fromRange) ?: return null
            val toRangeArray = getRange(toClass, toRange) ?: return null

            return NSDictionary().apply {
                put("kind", NSString("control"))
                put("edges", NSArray(arrayOf(
                    NSDictionary().apply {
                        put("start", fromRangeArray)
                        put("end", toRangeArray)
                    }
                )))
            }
        }

        private fun getBugPathEventRange(event: BugPathEvent): Region = event.region

        private fun getDiagnostic(hashCalculator: IReportHashCalculator, report: Report): NSDictionary? {
            val path = report.pathEvents.mapNotNull { getNote(it) }.toMutableList()

            if (report.bug_path_positions.isNotEmpty()) {
                for (i in 0 until report.bug_path_positions.size - 1) {
                    val pos1 = report.bug_path_positions[i]
                    val pos2 = report.bug_path_positions[i + 1]
                    if (pos1.region != null && pos2.region != null) {
                        getControlEdge(pos1.classname, pos1.region, pos2.classname, pos2.region)?.let {
                            path.add(it)
                        }
                    }
                }
            } else if (report.pathEvents.size > 1) {
                for (i in 0 until report.pathEvents.size - 1) {
                    val event1 = report.pathEvents[i]
                    val event2 = report.pathEvents[i + 1]
                    val range1 = getBugPathEventRange(event1)
                    val range2 = getBugPathEventRange(event2)
                    if (range1 != range2) {
                        getControlEdge(event1.classname, range1, event2.classname, range2)?.let {
                            path.add(it)
                        }
                    }
                }
            }

            val notes = report.notes.mapNotNull { getNote(it) }
            val location = getLocation(report.bugResFile, report.region.startLine, report.region.startColumn) ?: return null

            return NSDictionary().apply {
                put("location", location)
                put("issue_hash_content_of_line_in_context", 
                    NSString(report.reportHash(hashCalculator, Report.HashType.DIAGNOSTIC_MESSAGE)))
                put("issue_hash_location_bug_type", 
                    NSString(report.reportHash(hashCalculator, Report.HashType.CONTEXT_FREE)))
                put("check_name", NSString(report.check_name))
                put("detector_name", NSString(report.detector_name))
                put("description", NSString(report.message[Language.EN]))
                put("description_zh", NSString(report.message[Language.ZH]))
                put("category", NSString(report.category ?: "unknown"))
                put("type", NSString(report.type))
                put("notes", NSArray(notes.toTypedArray()))
                put("path", NSArray(path.toTypedArray()))
            }
        }

        fun getRoot(reports: List<Report>): NSDictionary? {
            val diagnostics = reports.mapNotNull { report ->
                getDiagnostic(this@PlistDiagnostics.hashCalculator, report).also {
                    if (it == null) {
                        logger.warn { "Failed create plist report for: $report" }
                    }
                }
            }

            if (diagnostics.isEmpty()) return null

            return NSDictionary().apply {
                put("diagnostics", NSArray(diagnostics.toTypedArray()))
                
                val filesArray = NSArray(fileToIndex.size)
                fileToIndex.forEach { (path, index) ->
                    filesArray[index] = NSString(path)
                }
                put("files", filesArray)
                
                put("metadata", NSDictionary().apply {
                    put("generated_by", NSDictionary().apply {
                        put("name", NSString(metadata.toolName))
                        put("version", NSString(metadata.toolVersion))
                    })
                    put("analyzer", NSDictionary().apply {
                        put("name", NSString(metadata.analyzerName))
                    })
                })
            }
        }
    }
}