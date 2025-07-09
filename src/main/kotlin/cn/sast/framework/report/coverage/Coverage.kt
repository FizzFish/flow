package cn.sast.framework.report.coverage

import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.framework.report.AbstractFileIndexer
import cn.sast.framework.report.IProjectFileLocator
import com.feysh.corax.cache.*
import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import mu.KotlinLogging
import org.jacoco.core.analysis.*
import org.jacoco.core.data.*
import org.jacoco.core.internal.analysis.ClassAnalyzer
import org.jacoco.core.internal.analysis.ClassCoverageImpl
import org.jacoco.core.internal.analysis.Instruction
import org.jacoco.core.internal.instr.InstrSupport
import org.jacoco.core.internal.instr.Instrumenter
import org.jacoco.report.*
import org.jacoco.report.html.HTMLFormatter
import org.objectweb.asm.ClassReader
import java.io.File
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import kotlin.io.path.absolutePathString
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

/**
 * 收集「命中行」并输出 JaCoCo HTML/Exec 报告的统一类。
 *
 * ⚠️ 说明
 * ---
 * * **ClassSourceInfo** 来自先前文件：包含 className / byteArray 等；
 * * **AnalysisCache** 负责将“类名 → ClassSourceInfo”关联缓存；
 * * 线程安全，可跨协程调用。
 */
open class Coverage {

    /* ────────────────────────── 基础数据结构 ────────────────────────── */

    private val logger = KotlinLogging.logger {}

    /** 收集开始时间（第一次调用 [cover] 时初始化） */
    private var startTimestamp: Instant? = null

    /** ASM/JaCoCo 用来复用字符串，节省内存 */
    val stringPool = StringPool()

    /** className → [ClassCoverage] */
    private val classCoverageMap: MutableMap<String, ClassCoverage> =
        ConcurrentHashMap(1_024)

    /** 等待异步 cover 的队列（高并发场景下比 Channel 更节省内存） */
    private val coverQueue: ConcurrentLinkedQueue<JacocoCover> = ConcurrentLinkedQueue()

    /** 异步缓存：ClassSourceInfo → 解析后的 [ClassCoverage] */
    private val cache: AsyncLoadingCache<ClassSourceInfo, ClassCoverage?> =
        AnalysisCache.defaultBuilder().buildAsync { src, exec ->
            analyzeInBackground(src, exec)
        }

    /** 按需惰性计算的执行数据 & 覆盖 Builder */
    var coverageBuilderPair: Pair<ExecutionDataStore, CoverageBuilder>? = null
        internal set

    /* ──────────────────────────── API：记录命中 ─────────────────────────── */

    /** 直接用 ClassSourceInfo（已拿到字节码）标记行命中 */
    suspend fun cover(clazz: ClassSourceInfo, line: Int) {
        val classCov = analyzeClass(clazz) ?: return
        classCov.cover(line)
    }

    /** 通过类名获取字节码再标记行命中（IO/Cache） */
    suspend fun cover(className: String, line: Int) {
        val src: ClassSourceInfo = AnalysisCache.getAsync(
            ClassSourceOfSCKey(className), Dispatchers.Default
        ).await() ?: return
        cover(src, line)
    }

    /** 直接用字节码数组 */
    suspend fun cover(clazzBytes: ByteArray, line: Int) =
        cover(ClassSourceInfo(clazzBytes), line)

    /** 高并发、低延迟场景：把记录放入队列，异步批量解析 */
    fun coverByQueue(className: String, line: Int) =
        coverQueue.add(JacocoCover(className, line))

    /* ────────────────────────── 核心：class → coverage ───────────────────────── */

    private suspend fun analyzeClass(source: ClassSourceInfo): ClassCoverage? {
        if (startTimestamp == null) startTimestamp = Instant.now()
        classCoverageMap[source.className]?.let { return it }

        val parsed = cache.get(source).await() ?: return null
        classCoverageMap.putIfAbsent(source.className, parsed)
        return parsed
    }

    /** 在 cache 线程池解析字节码 */
    private fun analyzeInBackground(
        source: ClassSourceInfo,
        exec: java.util.concurrent.Executor
    ): java.util.concurrent.CompletableFuture<ClassCoverage?> =
        kotlinx.coroutines.future.future(
            scope = CoroutineScope(Executors.newSingleThreadExecutor { r ->
                Thread(r, "jacoco-analyze").apply { isDaemon = true }
            }),
            context = Dispatchers.Default
        ) {
            try {
                val cr = InstrSupport.classReaderFor(source.byteArray)
                val classCov = ClassCoverage(source.className, source.byteArray, source.jacocoClassId)
                cr.accept(createAnalyzingVisitor(classCov, source.jacocoClassId, cr.className), 0)
                classCov
            } catch (e: Exception) {
                logger.debug(e) { "class parse failed: ${source.className}" }
                null
            }
        }

    /** 创建 ASM visitor -> 填充 line/probe 对应关系 */
    private fun createAnalyzingVisitor(
        classCoverage: ClassCoverage,
        classId: Long,
        className: String
    ) = ClassProbesAdapter(
        ClassAnalyzer(
            ClassCoverageImpl(className, classId, false),
            object : InstructionsBuilder() {
                override fun addProbe(probeId: Int, branch: Int) {
                    super.addProbe(probeId, branch)
                    currentInsn?.let { classCoverage.addProbe(it, probeId) }
                }
            },
            classCoverage,
            stringPool
        ),
        false
    )

    /* ────────────────────────── flush & 报告生成 ───────────────────────── */

    /** 解析队列中积压的 cover 记录 */
    private suspend fun processCoverQueueData(): Boolean {
        if (coverQueue.isEmpty()) return false
        while (true) {
            val (name, ln) = coverQueue.poll() ?: break
            cover(name, ln)
        }
        return true
    }

    /** 计算（或复用缓存）ExecutionDataStore + CoverageBuilder */
    suspend fun computeCoverageBuilder(): Pair<ExecutionDataStore, CoverageBuilder> {
        if (!processCoverQueueData() && coverageBuilderPair != null)
            return coverageBuilderPair!!

        val execStore = ExecutionDataStore()
        classCoverageMap.values.forEach { cc ->
            val probes = BooleanArray(cc.count).also { bs ->
                cc.probes.stream().forEach { bs[it] = true }
            }
            execStore.put(ExecutionData(cc.classId, cc.className.replace('.', '/'), probes))
        }
        val builder = makeCoverageBuilder(execStore)
        return execStore to builder.also { coverageBuilderPair = it }
    }

    /** 把 ExecutionDataStore + CoverageBuilder 写入 HTML 报告 */
    suspend fun flushCoverage(
        locator: IProjectFileLocator,
        outputDir: IResDirectory,
        encoding: Charset = Charsets.UTF_8
    ) = withContext(Dispatchers.IO) {
        val (execStore, builder) = computeCoverageBuilder()
        val mLocator = getMultiSourceFileLocator(locator, encoding)
        val sessionStore = SessionInfoStore().apply { visitSessionInfo(getSessionInfo()) }
        createReport(builder, sessionStore, execStore, mLocator, outputDir)
    }

    /* ─────────────────────────── report 工具方法 ────────────────────────── */

    fun getSessionInfo(): SessionInfo = SessionInfo(
        UUID.randomUUID().toString(),
        startTimestamp?.epochSecond ?: Instant.now().epochSecond,
        Instant.now().epochSecond
    )

    private suspend fun createReport(
        builder: CoverageBuilder,
        sessionStore: SessionInfoStore,
        execStore: ExecutionDataStore,
        srcLocator: ISourceFileLocator,
        outDir: IResDirectory
    ) = withContext(Dispatchers.IO) {
        outDir.mkdirs()
        val visitor: IReportVisitor = HTMLFormatter()
            .createVisitor(FileMultiReportOutput(outDir.file))
        visitor.visitInfo(sessionStore.infos, execStore.contents)
        visitor.visitBundle(builder.getBundle("CoraxCoverage"), srcLocator)
        visitor.visitEnd()
        logger.info { "Jacoco HTML report: ${outDir.resolve("index.html").file.absolutePath}" }
    }

    fun makeCoverageBuilder(execStore: ExecutionDataStore): CoverageBuilder =
        CoverageBuilder().also { Analyzer(execStore, it).apply {
            classCoverageMap.values.forEach { cc ->
                analyzeClass(cc.byteArray, cc.className)
            }
        } }

    fun getMultiSourceFileLocator(
        locator: IProjectFileLocator,
        encoding: Charset
    ): MultiSourceFileLocator = MultiSourceFileLocator(4).apply {
        add(DirectorySourceFileLocator(File("."), encoding.name(), 4))
        add(JacocoSourceLocator(locator, encoding.name(), 4))
    }

    /* ────────────────────── 内部数据结构 / util ────────────────────── */

    /** 行号 → probeId 集合；以及整体命中的 probe 位图 */
    class ClassCoverage(
        val className: String,
        val byteArray: ByteArray,
        val classId: Long
    ) {
        private val lineMap: MutableMap<Int, MutableSet<Int>> = LinkedHashMap(64)
        var count: Int = -1
            internal set
        val probes: BitSet = BitSet()

        fun addProbe(instruction: Instruction, probeId: Int) {
            var insn: Instruction? = instruction
            while (insn != null) {
                val ln = insn.line
                if (ln >= 0) lineMap.getOrPut(ln) { LinkedHashSet() }.add(probeId)
                insn = insn.predecessor
            }
        }

        fun cover(line: Int) = lineMap[line]?.forEach { probes.set(it) }
    }

    data class JacocoCover(val className: String, val line: Int)

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
