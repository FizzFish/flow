package cn.sast.framework.report.coverage

import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.framework.report.IProjectFileLocator
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.cache.AnalysisKey
import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import java.io.Closeable
import java.io.File
import java.io.OutputStream
import java.io.Reader
import java.lang.reflect.Field
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.time.Instant
import java.util.Arrays
import java.util.BitSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.UUID
import java.util.Map.Entry
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.function.IntConsumer
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorsKt
import kotlinx.coroutines.future.FutureKt
import mu.KLogger
import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.analysis.IBundleCoverage
import org.jacoco.core.analysis.ICoverageVisitor
import org.jacoco.core.analysis.ISourceFileCoverage
import org.jacoco.core.data.ExecutionData
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.core.data.ExecutionDataWriter
import org.jacoco.core.data.IExecutionDataVisitor
import org.jacoco.core.data.SessionInfo
import org.jacoco.core.data.SessionInfoStore
import org.jacoco.core.internal.analysis.ClassAnalyzer
import org.jacoco.core.internal.analysis.ClassCoverageImpl
import org.jacoco.core.internal.analysis.Instruction
import org.jacoco.core.internal.analysis.StringPool
import org.jacoco.core.internal.flow.ClassProbesAdapter
import org.jacoco.core.internal.flow.ClassProbesVisitor
import org.jacoco.core.internal.flow.MethodProbesVisitor
import org.jacoco.core.internal.instr.InstrSupport
import org.jacoco.report.DirectorySourceFileLocator
import org.jacoco.report.FileMultiReportOutput
import org.jacoco.report.IMultiReportOutput
import org.jacoco.report.IReportVisitor
import org.jacoco.report.ISourceFileLocator
import org.jacoco.report.InputStreamSourceFileLocator
import org.jacoco.report.MultiSourceFileLocator
import org.jacoco.report.html.HTMLFormatter
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.tree.MethodNode
import soot.jimple.infoflow.collect.ConcurrentHashSet

@SourceDebugExtension(["SMAP\nCoverage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Coverage.kt\ncn/sast/framework/report/coverage/Coverage\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,490:1\n1863#2,2:491\n1863#2,2:493\n*S KotlinDebug\n*F\n+ 1 Coverage.kt\ncn/sast/framework/report/coverage/Coverage\n*L\n363#1:491,2\n420#1:493,2\n*E\n"])
open class Coverage {
    private var startTimestamp: Instant? = null
    val stringPool: StringPool = StringPool()
    val classCoverageMap: MutableMap<String, ClassCoverage> = ConcurrentHashMap(1000)
    private val coverQueue: ConcurrentHashSet<JacocoCover> = ConcurrentHashSet()
    private val cache: AsyncLoadingCache<ClassSourceInfo, ClassCoverage?>

    var coverageBuilderPair: Pair<ExecutionDataStore, CoverageBuilder>? = null
        internal set

    suspend fun cover(clazz: ClassSourceInfo, line: Int) {
        return coroutineScope {
            val classCoverage = analyzeClass(clazz)
            classCoverage?.cover(line)
        }
    }

    suspend fun cover(className: String, line: Int) {
        val sourceInfo = AnalysisCache.G.INSTANCE.getAsync(ClassSourceOfSCKey(className), Dispatchers.Default) as? ClassSourceInfo ?: return
        cover(sourceInfo, line)
    }

    suspend fun cover(clazz: ByteArray, line: Int) {
        cover(ClassSourceInfo(clazz), line)
    }

    fun coverByQueue(className: String, line: Int) {
        coverQueue.add(JacocoCover(className, line))
    }

    private fun createAnalyzingVisitor(
        classCoverage: ClassCoverage,
        classId: Long,
        className: String
    ): ClassVisitor {
        val coverage = ClassCoverageImpl(className, classId, false)
        val builder = object : InstructionsBuilder(classCoverage) {
            override fun addProbe(probeId: Int, branch: Int) {
                super.addProbe(probeId, branch)
                classCoverage.addProbe(currentInsn, probeId)
            }
        }
        return ClassProbesAdapter(
            object : ClassAnalyzer(coverage, builder, classCoverage, stringPool) {
                override fun visitMethod(
                    access: Int,
                    name: String,
                    desc: String,
                    signature: String?,
                    exceptions: Array<String>?
                ): MethodProbesVisitor {
                    return object : MethodAnalyzer(builder) {
                        override fun accept(methodNode: MethodNode, methodVisitor: MethodVisitor) {
                            super.accept(methodNode, methodVisitor)
                        }
                    }
                }

                override fun visitTotalProbeCount(count: Int) {
                    super.visitTotalProbeCount(count)
                    classCoverage.setCount(count)
                }
            },
            false
        )
    }

    suspend fun analyzeClass(source: ClassSourceInfo): ClassCoverage? {
        if (startTimestamp == null) {
            startTimestamp = Instant.now()
        }

        classCoverageMap[source.className]?.let { return it }

        val classCoverage = cache.get(source).await()
        if (classCoverage != null) {
            classCoverageMap[source.className] = classCoverage
        }
        return classCoverage
    }

    fun getSessionInfo(): SessionInfo {
        val timestamp = startTimestamp ?: Instant.now()
        return SessionInfo(UUID.randomUUID().toString(), timestamp.epochSecond, Instant.now().epochSecond)
    }

    fun getSessionInfoStore(): SessionInfoStore {
        return SessionInfoStore().apply {
            visitSessionInfo(getSessionInfo())
        }
    }

    suspend fun flushExecutionDataFile(
        sessionInfoStore: SessionInfoStore,
        executionDataStore: ExecutionDataStore,
        dumpFile: IResFile
    ) {
        dumpFile.mkdirs()
        withContext(Dispatchers.IO) {
            Files.newOutputStream(dumpFile.path).use { outputStream ->
                ExecutionDataWriter(outputStream).apply {
                    sessionInfoStore.infos.forEach { visitSessionInfo(it) }
                    executionDataStore.accept(this)
                }
            }
        }
    }

    suspend fun processCoverQueueData(): Boolean {
        if (coverQueue.isEmpty()) return false

        coverQueue.forEach { (className, line) ->
            cover(className, line)
        }
        coverQueue.clear()
        return false
    }

    suspend fun computeCoverageBuilder(): Pair<ExecutionDataStore, CoverageBuilder> {
        coverageBuilderPair?.let { return it }

        val executionDataStore = ExecutionDataStore()
        val coverageBuilder = CoverageBuilder()

        classCoverageMap.values.forEach { classCoverage ->
            val probes = BooleanArray(classCoverage.count)
            classCoverage.probes.stream().forEach { probes[it] = true }
            try {
                executionDataStore.put(
                    ExecutionData(
                        classCoverage.classId,
                        classCoverage.className.replace(".", "/"),
                        probes
                    )
                )
            } catch (e: Exception) {
                logger.error(e) { "An error occurred: class=${classCoverage.className}" }
            }
        }

        val result = executionDataStore to makeCoverageBuilder(executionDataStore)
        coverageBuilderPair = result
        return result
    }

    @Throws(java.io.IOException::class)
    suspend fun createReport(
        coverageBuilder: CoverageBuilder,
        sessionInfoStore: SessionInfoStore,
        executionDataStore: ExecutionDataStore,
        mLocator: MultiSourceFileLocator,
        reportDirectory: IResDirectory
    ) {
        val bundleCoverage = coverageBuilder.getBundle("CoraxCoverage")
        createReport(sessionInfoStore, executionDataStore, bundleCoverage, mLocator, reportDirectory.file)
    }

    @Throws(java.io.IOException::class)
    suspend fun createReport(
        coverageBuilder: CoverageBuilder,
        sessionInfoStore: SessionInfoStore,
        executionDataStore: ExecutionDataStore,
        sourceDirectory: List<ISourceFileLocator>,
        reportDirectory: IResDirectory
    ) {
        val mLocator = MultiSourceFileLocator(4).apply {
            sourceDirectory.forEach { add(it) }
        }
        createReport(coverageBuilder, sessionInfoStore, executionDataStore, mLocator, reportDirectory)
    }

    @Throws(java.io.IOException::class)
    suspend fun createReport(
        sessionInfoStore: SessionInfoStore,
        executionDataStore: ExecutionDataStore,
        bundleCoverage: IBundleCoverage,
        locator: ISourceFileLocator,
        reportDirectory: File
    ) {
        if (!reportDirectory.exists()) {
            reportDirectory.mkdir()
        }

        withContext(Dispatchers.IO) {
            HTMLFormatter().createVisitor(FileMultiReportOutput(reportDirectory)).use { visitor ->
                visitor.visitInfo(sessionInfoStore.infos, executionDataStore.contents)
                visitor.visitBundle(bundleCoverage, locator)
                visitor.visitEnd()
            }
        }
        logger.info { "Jacoco coverage html reports: ${reportDirectory.resolve("index.html").absoluteFile.normalize()}" }
    }

    fun makeCoverageBuilder(executionDataStore: ExecutionDataStore): CoverageBuilder {
        val coverageBuilder = CoverageBuilder()
        val analyzer = Analyzer(executionDataStore, coverageBuilder)
        classCoverageMap.values.forEach { classCoverage ->
            analyzer.analyzeClass(classCoverage.byteArray, classCoverage.className)
        }
        return coverageBuilder
    }

    fun getMultiSourceFileLocator(locator: IProjectFileLocator, encoding: Charset): MultiSourceFileLocator {
        return MultiSourceFileLocator(4).apply {
            add(DirectorySourceFileLocator(File("."), encoding.name(), 4))
            add(JacocoSourceLocator(locator))
        }
    }

    open suspend fun flushCoverage(locator: IProjectFileLocator, outputDir: IResDirectory, encoding: Charset) {
        val (executionDataStore, coverageBuilder) = computeCoverageBuilder()
        val sessionInfoStore = getSessionInfoStore()
        val mLocator = getMultiSourceFileLocator(locator, encoding)
        createReport(coverageBuilder, sessionInfoStore, executionDataStore, mLocator, outputDir)
    }

    fun calculateSourceCoverage(coverage: CoverageBuilder, mLocator: MultiSourceFileLocator): SourceCoverage {
        val sourceCoverage = LinkedHashMap<String, SourceCoverage.JavaSourceCoverage>()
        coverage.sourceFiles.forEach { srcCov ->
            val sourceKey = "${srcCov.packageName}/${srcCov.name}"
            try {
                mLocator.getSourceFile(srcCov.packageName, srcCov.name)?.use { reader ->
                    val lineCount = reader.readLines().size
                    sourceCoverage[sourceKey] = SourceCoverage.JavaSourceCoverage(lineCount, srcCov)
                }
            } catch (e: Exception) {
                logger.error("Source file $sourceKey cannot be read!", e)
            }
        }
        return SourceCoverage(sourceCoverage)
    }

    suspend fun calculateSourceCoverage(locator: IProjectFileLocator, encoding: Charset): SourceCoverage {
        val (_, coverageBuilder) = computeCoverageBuilder()
        return calculateSourceCoverage(coverageBuilder, getMultiSourceFileLocator(locator, encoding))
    }

    companion object {
        private val logger: KLogger = TODO("Initialize logger")

        @JvmStatic
        private fun cacheLoader(
            this$0: Coverage,
            source: ClassSourceInfo,
            executor: Executor
        ): CompletableFuture<ClassCoverage> {
            val classCoverage = ClassCoverage(source.className, source.byteArray, source.jacocoClassId)
            return FutureKt.future(
                CoroutineScope(ExecutorsKt.from(executor)),
                block = suspend {
                    try {
                        val reader = InstrSupport.classReaderFor(source.byteArray)
                        reader.accept(
                            this$0.createAnalyzingVisitor(classCoverage, source.jacocoClassId, reader.className),
                            0
                        )
                        classCoverage
                    } catch (e: Exception) {
                        null
                    }
                }
            )
        }
    }

    @SourceDebugExtension(["SMAP\nCoverage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Coverage.kt\ncn/sast/framework/report/coverage/Coverage$ClassCoverage\n+ 2 ReportConverter.kt\ncn/sast/framework/report/ReportConverterKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,490:1\n38#2,3:491\n381#3,7:494\n*S KotlinDebug\n*F\n+ 1 Coverage.kt\ncn/sast/framework/report/coverage/Coverage$ClassCoverage\n*L\n110#1:491,3\n118#1:494,7\n*E\n"])
    class ClassCoverage(
        val className: String,
        val byteArray: ByteArray,
        val classId: Long
    ) {
        private val lineMap: MutableMap<Int, MutableSet<Int>> = LinkedHashMap(100)
        var count: Int = 0
            internal set
        var probes: BitSet = BitSet(0)
            get() {
                if (field.size() == 0 && count >= 0) {
                    field = BitSet(count)
                }
                return field
            }
            private set

        private fun getPredecessor(instruction: Instruction): Instruction? {
            return try {
                instruction.javaClass.getDeclaredField("predecessor").apply {
                    isAccessible = true
                }.get(instruction) as? Instruction
            } catch (e: Exception) {
                null
            }
        }

        fun addProbe(instruction: Instruction, probeId: Int) {
            var insn: Instruction? = instruction
            while (insn != null) {
                val line = insn.line
                if (line >= 0) {
                    lineMap.getOrPut(line) { LinkedHashSet() }.add(probeId)
                }
                insn = getPredecessor(insn)
            }
        }

        fun cover(line: Int) {
            lineMap[line]?.forEach { probes.set(it) }
        }
    }

    internal data class JacocoCover(
        val className: String,
        val line: Int
    ) {
        fun component1(): String = className
        fun component2(): Int = line

        fun copy(className: String = this.className, line: Int = this.line): JacocoCover {
            return JacocoCover(className, line)
        }

        override fun toString(): String {
            return "JacocoCover(className=$className, line=$line)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as JacocoCover
            if (className != other.className) return false
            if (line != other.line) return false
            return true
        }

        override fun hashCode(): Int {
            var result = className.hashCode()
            result = 31 * result + line
            return result
        }
    }
}