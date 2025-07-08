
package cn.sast.framework.engine

/* 
 *  PreAnalysisImpl.kt — hand‑reconstructed from de‑compiled byte‑code.
 *  The goal is feature‑parity with the original while restoring valid,
 *  idiomatic Kotlin that compiles with kotlinx‑coroutines 1.8+ and soot‑upstream.
 *
 *  ⚠  Many external symbols (e.g. CheckerUnit, SourceFileCheckPoint,
 *  SafeAnalyzeUtil, etc.) live elsewhere in the project.  This file
 *  therefore does **not** stand alone but should compile inside the full
 *  code‑base after the other fixed sources provided earlier are placed on
 *  the class‑path.
 */

import cn.sast.api.AnalyzerEnv
import cn.sast.api.config.*
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.FileResInfo
import cn.sast.api.util.*
import cn.sast.common.*
import cn.sast.coroutines.caffine.impl.FastCacheImpl
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.result.IPreAnalysisResultCollector
import cn.sast.framework.util.SafeAnalyzeUtil
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.cache.coroutines.FastCache
import com.feysh.corax.commons.NullableLateinit
import com.feysh.corax.config.api.*
import com.feysh.corax.config.api.PreAnalysisApi.Result
import com.feysh.corax.config.api.report.Region
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.IMatchItem
import com.feysh.corax.config.api.rules.ProcessRule.IMatchTarget
import com.feysh.corax.config.api.utils.UtilsKt
import com.github.javaparser.Position
import com.github.javaparser.ast.nodeTypes.NodeWithRange
import io.vertx.core.impl.ConcurrentHashSet
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import mu.KLogger
import mu.KotlinLogging
import soot.*
import soot.jimple.InstanceInvokeExpr
import soot.jimple.Stmt
import soot.jimple.toolkits.callgraph.CallGraph
import soot.util.HashMultiMap
import soot.util.MultiMap
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt

@Suppress("TooManyFunctions", "LongParameterList", "LargeClass")
class PreAnalysisImpl(
    private val mainConfig: MainConfig,
    private val locator: IProjectFileLocator,
    private val cg: CallGraph,
    private val info: SootInfoCache,
    private val resultCollector: IPreAnalysisResultCollector,
    val scene: Scene,
) : PreAnalysisCoroutineScope, IAnalysisDepends {

    /* ---------------------------------------------------------------- *
     *  Fields & derived, lazily‑computed properties                    *
     * ---------------------------------------------------------------- */
    private val analyzedClasses: MutableSet<SootClass> = ConcurrentHashSet()
    private val analyzedSourceFiles: MutableSet<IResFile> = ConcurrentHashSet()

    protected open val fastCache: FastCache
        get() = FastCacheImpl.INSTANCE

    private val scanFilter: ScanFilter = mainConfig.scanFilter
    private val monitor: IMonitor? = mainConfig.monitor

    /** Complete universe (excluding synthetic/external) */
    private val allClasses: Set<SootClass>
    private val allMethods: List<SootMethod>
    private val allFields: List<SootField>

    /** Application‑only slices */
    private val appOnlyClasses: List<SootClass>
    private val appOnlyMethods: List<SootMethod>
    private val appOnlyFields: List<SootField>

    private val changeFileBasedIncAnalysis: IncrementalAnalyzeByChangeFiles? =
        mainConfig.incrementAnalyze as? IncrementalAnalyzeByChangeFiles
    private val dg = changeFileBasedIncAnalysis?.simpleDeclAnalysisDependsGraph

    private val preAnalysisConfig: PreAnalysisConfig? = mainConfig.saConfig?.preAnalysisConfig
    private val cancelAnalysisInErrorCount: Int = preAnalysisConfig?.cancelAnalysisInErrorCount ?: 10

    /**
     *  Coroutine [scope] passed in by the orchestrator (e.g. PreAnalysis facade).
     *  Stored in a [NullableLateinit] so that we get a clean runtime error if
     *  someone forgets to initialise it before use.
     */
    private val scopeLateInit: NullableLateinit<CoroutineScope> =
        NullableLateinit("scope is not initialised yet")

    override var scope: CoroutineScope
        get() = scopeLateInit.getValue(this, PreAnalysisImpl::scope)
        set(value) = scopeLateInit.setValue(this, PreAnalysisImpl::scope, value)

    /* ---- Global semaphores to keep memory + CPU under control -------- */

    private val globalNormalAnalyzeSemaphore by lazy {
        Semaphore(mainConfig.parallelsNum * 2)
    }

    private val globalResourceSemaphore by lazy {
        Semaphore(preAnalysisConfig?.largeFileSemaphorePermits ?: 3)
    }

    private val filesWhichHitSizeThreshold: MutableSet<IResource> = Kotlin_extKt.concurrentHashSetOf()
    private val maximumFileSizeThresholdWarnings: Int =
        preAnalysisConfig?.maximumFileSizeThresholdWarnings ?: 20

    private val invokePointData by lazy { buildInvokePointData() }

    /* Paths ---------------------------------------------------------------- */

    val outputPath: Path
        get() = mainConfig.output_dir.path

    val fullCanonicalPathString: String
        get() = Resource.INSTANCE.of(outputPath).absolute.normalize().toString()

    /* Logger ---------------------------------------------------------------- */

    private val logger: KLogger = KotlinLogging.logger {}

    /* --------------------------------------------------------------------- *
     *  Initialisation                                                       *
     * --------------------------------------------------------------------- */
    init {
        val classes = scene.classes
            .filter { clazz -> !scene.isExcluded(clazz) && !OthersKt.isSyntheticComponent(clazz) }
            .toSet()

        allClasses = classes
        allMethods = classes.flatMap { it.methods }
        allFields  = classes.flatMap { it.fields }

        appOnlyClasses = classes.filter { it.isApplicationClass }
        appOnlyMethods = appOnlyClasses.flatMap { it.methods }
        appOnlyFields  = appOnlyClasses.flatMap { it.fields }
    }

    /* --------------------------------------------------------------------- *
     *  Helpers                                                               *
     * --------------------------------------------------------------------- */

    /** Builds the [InvokePointData] index once up front for fast look‑up. */
    private fun buildInvokePointData(): InvokePointData {
        val objectType: RefType = Scene.v().getRefTypeUnsafe("java.lang.Object")
            ?: return InvokePointData(HashMultiMap(), emptySet())

        val targetsToEdges: MultiMap<SootMethod, InvokeCheckPoint> = HashMultiMap()
        val allPoints = LinkedHashSet<InvokeCheckPoint>()

        cg.iterator().forEach { edge ->
            val src = edge.src() ?: return@forEach
            if (src.declaringClass !in appOnlyClasses) return@forEach

            val tgt       = edge.tgt()
            val callSite  = edge.srcUnit() as? Stmt ?: return@forEach
            val invokeExpr = callSite.invokeExpr as? InstanceInvokeExpr ?: return@forEach
            val declaredReceiverType = invokeExpr.base.type
            if (declaredReceiverType == objectType) return@forEach

            val subSignature = tgt.subSignature
            val declaringClass = tgt.declaringClass

            var checkPoint: InvokeCheckPoint? = null
            SootUtilsKt.findMethodOrNull(declaringClass, subSignature).forEach { candidate ->
                if (candidate != tgt && candidate.isConcrete) return@forEach
                if (checkPoint == null) {
                    checkPoint = InvokeCheckPoint(
                        info,
                        src,
                        callSite,
                        declaredReceiverType,
                        invokeExpr.methodRef,
                        tgt,
                        invokeExpr,
                    ).also { allPoints += it }
                }
                targetsToEdges.put(candidate, checkPoint)
            }
        }

        return InvokePointData(targetsToEdges, allPoints)
    }

    /* --------------------------------------------------------------------- *
     *  IPreAnalysisConfig.skip helpers                                       *
     * --------------------------------------------------------------------- */

    private fun IPreAnalysisConfig.skip(file: Path): Boolean =
        skip(origAction = "PreAnalysis:Process", scanFilter.processRegex.clazzRules) { scanFilter[file] }

    private fun IPreAnalysisConfig.skip(sc: SootClass): Boolean =
        skip(origAction = "PreAnalysis:Process", scanFilter.processRegex.clazzRules) { scanFilter[sc] }

    private fun IPreAnalysisConfig.skip(sf: SootField): Boolean =
        skip(origAction = "PreAnalysis:Process", scanFilter.processRegex.clazzRules) { scanFilter[sf] }

    private fun IPreAnalysisConfig.skip(sm: SootMethod): Boolean =
        skip(origAction = "PreAnalysis:Process", scanFilter.processRegex.clazzRules) { scanFilter[sm] }

    private inline fun IPreAnalysisConfig.skip(
        origAction: String,
        rule: List<IMatchItem>,
        target: () -> IMatchTarget,
    ): Boolean {
        if (!ignoreProjectConfigProcessFilter &&
            ScanFilter.getActionOf(scanFilter, rule, origAction, target()) == ProcessRule.ScanAction.Skip
        ) return true

        if (processRules.isEmpty()) return false
        return ProcessRule.matches(processRules, target()).second == ProcessRule.ScanAction.Skip
    }

    /* --------------------------------------------------------------------- *
     *  API surface backing helpers                                           *
     * --------------------------------------------------------------------- */

    fun chooseSemaphore(fileSize: Long): Semaphore {
        val threshold = preAnalysisConfig?.largeFileSize ?: return globalNormalAnalyzeSemaphore
        return if (fileSize > threshold) globalResourceSemaphore else globalNormalAnalyzeSemaphore
    }

    /** Map target methods -> set of [InvokeCheckPoint] */
    fun invokeCheckPoints(atInvoke: List<SootMethod>?): Set<InvokeCheckPoint> =
        if (atInvoke != null)
            atInvoke.flatMapTo(LinkedHashSet()) { invokePointData.targetsToEdges[it] ?: emptySet() }
        else
            invokePointData.allPoint

    /* --------------------------------------------------------------------- *
     *  Misc                                                                  *
     * --------------------------------------------------------------------- */

    override fun uninitializedScope() = scopeLateInit.uninitialized()

    /* --  IAnalysisDepends  ------------------------------------------------ */

    override fun toDecl(target: Any): XDecl =
        MainConfigKt.simpleIAnalysisDepends(mainConfig).toDecl(target)

    override infix fun XDecl.dependsOn(dep: XDecl) =
        MainConfigKt.simpleIAnalysisDepends(mainConfig).dependsOn(this, dep)

    override infix fun Collection<XDecl>.dependsOn(deps: Collection<XDecl>) =
        MainConfigKt.simpleIAnalysisDepends(mainConfig).dependsOn(this, deps)

    /* --------------------------------------------------------------------- *
     *  Data class holding invoke‑site index                                  *
     * --------------------------------------------------------------------- */

    data class InvokePointData(
        val targetsToEdges: MultiMap<SootMethod, InvokeCheckPoint>,
        val allPoint: Set<InvokeCheckPoint>,
    )
}
