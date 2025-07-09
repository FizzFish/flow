package cn.sast.framework.report.metadata

import cn.sast.api.report.ClassResInfo
import cn.sast.common.*
import cn.sast.coroutines.MultiWorkerQueue
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.report.NullWrapperFileGenerator
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.jacoco.core.analysis.ICounter
import soot.Scene
import soot.SootClass
import soot.jimple.infoflow.collect.ConcurrentHashSet
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.lineSequence

/**
 * 计算并生成 [AnalysisMetadata]
 */
class MetadataGenerator(
    private val projectRoot:          String?,
    private val multipleProjectRoot:  List<String>,
    private val outputPath:           IResDirectory,
    private val sourcePaths:          List<String>,
    private val coveredCounter:       ICounter,
    private val successfulFiles:      Set<IResFile>,
    private val failedFiles:          Set<IResFile>
) {

    private val logger = KotlinLogging.logger {}
    private val resultSourceFiles: MutableMap<String, String> = LinkedHashMap()

    /** 记录“报告 → 源文件”映射（写 SARIF / PLIST 时调用） */
    fun updateResultSourceMapping(result: String, source: String) {
        resultSourceFiles[
            outputPath.resolve(result).absoluteNormalize.path.toString()
        ] = source
    }

    /** 主入口：并行统计代码行数 + 文件数并生成元数据 */
    suspend fun getMetadata(locator: IProjectFileLocator): AnalysisMetadata = coroutineScope {
        val lineCounter   = AtomicInteger()
        val fileCounter   = AtomicInteger()
        val visited       = ConcurrentHashSet<String>()
        val workerQueue   = MultiWorkerQueue(
            name          = "Metadata",
            threadNum     = OS.maxThreadNum,
            worker        = { sc: SootClass ->
                locator.get(ClassResInfo(sc), NullWrapperFileGenerator)?.let { file ->
                    if (visited.add(file.absolutePath)) {
                        fileCounter.incrementAndGet()
                        lineCounter.addAndGet(file.path.lineSequence().count())
                    }
                }
            }
        )

        // 分派任务
        Scene.v().applicationClasses
            .filterNot(Scene.v()::isExcluded)
            .forEach(workerQueue::dispatch)

        workerQueue.join()
        workerQueue.close()

        generateMetaData(fileCounter.get(), lineCounter.get())
    }

    /** 失败时只生成覆盖率 / 环境信息 */
    fun generateFailedMetaData(): AnalysisMetadata = generateMetaData(0, 0)

    /* ------------------------------------------------------------------ */

    private fun generateMetaData(fileCnt: Int, lineCnt: Int): AnalysisMetadata {
        val coverage = Counter(
            missed  = coveredCounter.missedCount,
            covered = coveredCounter.coveredCount
        )
        val sortedSrcPaths   = sourcePaths.sorted()
        val osNameLC         = System.getProperty("os.name").lowercase(Locale.ROOT)

        val analyzerStats = AnalyzerStatistics(
            failed            = failedFiles.size,
            failedSources     = failedFiles.map { Resource.getOriginFileFromExpandPath(it).pathString }.sorted(),
            successful        = successfulFiles.size,
            successfulSources = successfulFiles.map { Resource.getOriginFileFromExpandPath(it).pathString }.sorted(),
            version           = ""  // 版本留空或自行填写
        )

        val analyzers = mapOf("corax" to Analyzer(analyzerStats))
        val command   = OS.getCommandLine() ?: listOf(System.getProperty("java.home").orEmpty())

        val tool = Tool(
            analyzers          = analyzers,
            command            = command,
            name               = "corax",
            outputPath         = outputPath.absoluteNormalize.path.toString(),
            projectRoot        = projectRoot.orEmpty(),
            multipleProjectRoot= multipleProjectRoot,
            resultSourceFiles  = resultSourceFiles.toSortedMap(),
            workingDirectory   = System.getProperty("user.dir")
        )

        logger.info { "Metadata: Java executable file path: ${OS.javaExecutableFilePath}" }

        return AnalysisMetadata(
            fileCount      = fileCnt,
            lineCount      = lineCnt,
            codeCoverage   = coverage,
            numOfReportDir = 1,
            sourcePaths    = sortedSrcPaths,
            osName         = osNameLC,
            tools          = listOf(tool)
        )
    }
}
