package cn.sast.framework.report.metadata

import cn.sast.api.report.ClassResInfo
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.OS
import cn.sast.common.Resource
import cn.sast.common.ResourceKt
import cn.sast.coroutines.MultiWorkerQueue
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.report.NullWrapperFileGenerator
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import org.jacoco.core.analysis.ICounter
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import soot.Scene
import soot.SootClass
import soot.jimple.infoflow.collect.ConcurrentHashSet

@SourceDebugExtension(["SMAP\nMetadataGenerator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MetadataGenerator.kt\ncn/sast/framework/report/metadata/MetadataGenerator\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,104:1\n1557#2:105\n1628#2,3:106\n1557#2:109\n1628#2,3:110\n1#3:113\n*S KotlinDebug\n*F\n+ 1 MetadataGenerator.kt\ncn/sast/framework/report/metadata/MetadataGenerator\n*L\n80#1:105\n80#1:106,3\n82#1:109\n82#1:110,3\n*E\n"])
class MetadataGenerator(
    private val projectRoot: String?,
    private val multipleProjectRoot: List<String>,
    private val outputPath: IResDirectory,
    private val sourcePaths: List<String>,
    private val coveredCounter: ICounter,
    private val successfulFiles: Set<IResFile>,
    private val failedFiles: Set<IResFile>
) {
    private val resultSourceFiles: MutableMap<String, String> = LinkedHashMap()

    fun updateResultSourceMapping(result: String, source: String) {
        resultSourceFiles[outputPath.resolve(result).absolute.normalize.path.toString()] = source
    }

    suspend fun getMetadata(locator: IProjectFileLocator): AnalysisMetadata {
        return suspendCoroutine { continuation ->
            val fileCount = AtomicInteger(0)
            val lineCount = AtomicInteger(0)
            val visited = ConcurrentHashSet<String>()
            
            val task = MultiWorkerQueue(
                "Metadata",
                OS.INSTANCE.maxThreadNum,
                Function2<SootClass, Continuation<Unit>, Any?> { sootClass, cont ->
                    val resFile = locator.get(ClassResInfo(sootClass), NullWrapperFileGenerator.INSTANCE)
                    if (resFile != null && visited.add(resFile.absolutePath)) {
                        fileCount.incrementAndGet()
                        lineCount.addAndGet(
                            ResourceKt.lineSequence(resFile) { it.count() }
                        )
                    }
                    Unit
                }
            )

            Scene.v().applicationClasses
                .filterNot { Scene.v().isExcluded(it) }
                .forEach { task.dispatch(it) }

            task.join(continuation)
            task.close()
            logger.info { "Metadata: Java executable file path: ${OS.INSTANCE.javaExecutableFilePath}" }
            generateMetaData(fileCount.get(), lineCount.get())
        }
    }

    fun generateFailedMetaData(): AnalysisMetadata {
        return generateMetaData(0, 0)
    }

    private fun generateMetaData(fileCount: Int, lineCount: Int): AnalysisMetadata {
        val counter = Counter(coveredCounter.missedCount, coveredCounter.coveredCount)
        val sortedSourcePaths = sourcePaths.sorted()
        val osName = System.getProperty("os.name").lowercase(Locale.ROOT)
        
        val failedFilePaths = failedFiles.map { 
            Resource.INSTANCE.getOriginFileFromExpandPath(it).pathString 
        }.sorted()
        
        val successfulFilePaths = successfulFiles.map { 
            Resource.INSTANCE.getOriginFileFromExpandPath(it).pathString 
        }.sorted()

        val analyzer = Analyzer(
            AnalyzerStatistics(
                failedFiles.size,
                failedFilePaths,
                successfulFiles.size,
                successfulFilePaths,
                ""
            ),
            emptyMap()
        )

        val javaCommand = OS.INSTANCE.commandLine ?: 
            System.getProperty("java.home")?.let { listOf(it) } ?: emptyList()

        return AnalysisMetadata(
            fileCount,
            lineCount,
            counter,
            1,
            sortedSourcePaths,
            osName,
            listOf(
                Tool(
                    mapOf("corax" to analyzer),
                    javaCommand,
                    "corax",
                    outputPath.absolute.normalize.path.toString(),
                    projectRoot ?: "",
                    multipleProjectRoot,
                    resultSourceFiles.toSortedMap(),
                    System.getProperty("user.dir")
                )
            )
        )
    }

    companion object {
        private val logger: KLogger
    }
}