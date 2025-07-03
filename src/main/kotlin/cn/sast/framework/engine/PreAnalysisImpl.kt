package cn.sast.framework.engine

import cn.sast.api.AnalyzerEnv
import cn.sast.api.config.MainConfig
import cn.sast.api.config.MainConfigKt
import cn.sast.api.config.PreAnalysisConfig
import cn.sast.api.config.PreAnalysisCoroutineScope
import cn.sast.api.config.SaConfig
import cn.sast.api.config.ScanFilter
import cn.sast.api.incremental.IncrementalAnalyze
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.FileResInfo
import cn.sast.api.util.IMonitor
import cn.sast.api.util.Kotlin_extKt
import cn.sast.api.util.OthersKt
import cn.sast.api.util.SootUtilsKt
import cn.sast.api.util.Timer
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.Resource
import cn.sast.common.ResourceImplKt
import cn.sast.coroutines.caffine.impl.FastCacheImpl
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.result.IPreAnalysisResultCollector
import cn.sast.framework.util.SafeAnalyzeUtil
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.cache.coroutines.FastCache
import com.feysh.corax.commons.NullableLateinit
import com.feysh.corax.config.api.BugMessage
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.CheckerUnit
import com.feysh.corax.config.api.IAnalysisDepends
import com.feysh.corax.config.api.ICheckPoint
import com.feysh.corax.config.api.IClassCheckPoint
import com.feysh.corax.config.api.IClassMatch
import com.feysh.corax.config.api.IFieldCheckPoint
import com.feysh.corax.config.api.IFieldMatch
import com.feysh.corax.config.api.IInvokeCheckPoint
import com.feysh.corax.config.api.IMethodCheckPoint
import com.feysh.corax.config.api.IMethodMatch
import com.feysh.corax.config.api.INodeWithRange
import com.feysh.corax.config.api.IPreAnalysisClassConfig
import com.feysh.corax.config.api.IPreAnalysisConfig
import com.feysh.corax.config.api.IPreAnalysisFieldConfig
import com.feysh.corax.config.api.IPreAnalysisFileConfig
import com.feysh.corax.config.api.IPreAnalysisInvokeConfig
import com.feysh.corax.config.api.IPreAnalysisMethodConfig
import com.feysh.corax.config.api.ISourceFileCheckPoint
import com.feysh.corax.config.api.PreAnalysisApi
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.BugMessage.Env
import com.feysh.corax.config.api.PreAnalysisApi.Result
import com.feysh.corax.config.api.report.Region
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.IMatchItem
import com.feysh.corax.config.api.rules.ProcessRule.IMatchTarget
import com.feysh.corax.config.api.utils.UtilsKt
import com.github.javaparser.Position
import com.github.javaparser.ast.nodeTypes.NodeWithRange
import io.vertx.core.impl.ConcurrentHashSet
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function0
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Intrinsics.Kotlin
import kotlin.reflect.KCallable
import kotlinx.coroutines.AwaitKt
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.SemaphoreKt
import mu.KLogger
import soot.RefType
import soot.Scene
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.SootMethodRef
import soot.Type
import soot.Value
import soot.jimple.InstanceInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.Stmt
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import soot.tagkit.Host
import soot.util.Chain
import soot.util.HashMultiMap
import soot.util.MultiMap

@SourceDebugExtension(["SMAP\nPreAnalysisImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/PreAnalysisImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,760:1\n774#2:761\n865#2,2:762\n1368#2:764\n1454#2,5:765\n1368#2:770\n1454#2,5:771\n774#2:776\n865#2,2:777\n1368#2:779\n1454#2,5:780\n1368#2:785\n1454#2,5:786\n1557#2:792\n1628#2,3:793\n1454#2,5:796\n1#3:791\n*S KotlinDebug\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/PreAnalysisImpl\n*L\n82#1:761\n82#1:762,2\n83#1:764\n83#1:765,5\n84#1:770\n84#1:771,5\n86#1:776\n86#1:777,2\n87#1:779\n87#1:780,5\n88#1:785\n88#1:786,5\n466#1:792\n466#1:793,3\n543#1:796,5\n*E\n"])
class PreAnalysisImpl(
    val mainConfig: MainConfig,
    val locator: IProjectFileLocator,
    val cg: CallGraph,
    val info: SootInfoCache,
    private val resultCollector: IPreAnalysisResultCollector,
    val scene: Scene
) : PreAnalysisCoroutineScope, IAnalysisDepends {
    val analyzedClasses: MutableSet<SootClass> = ConcurrentHashSet<SootClass>()
    val analyzedSourceFiles: MutableSet<IResFile> = ConcurrentHashSet<IResFile>()

    open val fastCache: FastCache
        get() = FastCacheImpl.INSTANCE

    private val scanFilter: ScanFilter = mainConfig.scanFilter
    private val monitor: IMonitor? = mainConfig.monitor
    private val allClasses: Set<SootClass>
    private val allMethods: List<SootMethod>
    private val allFields: List<SootField>
    private val appOnlyClasses: List<SootClass>
    private val appOnlyMethods: List<SootMethod>
    private val appOnlyFields: List<SootField>
    private val changeFileBasedIncAnalysis: IncrementalAnalyzeByChangeFiles? = mainConfig.incrementAnalyze as? IncrementalAnalyzeByChangeFiles
    private val dg: SimpleDeclAnalysisDependsGraph? = changeFileBasedIncAnalysis?.simpleDeclAnalysisDependsGraph
    private val preAnalysisConfig: PreAnalysisConfig? = mainConfig.saConfig?.preAnalysisConfig
    private val cancelAnalysisInErrorCount: Int = preAnalysisConfig?.cancelAnalysisInErrorCount ?: 10
    private val scopeLateInit: NullableLateinit<CoroutineScope> = NullableLateinit("scope is not initialized yet")

    open var scope: CoroutineScope
        get() = scopeLateInit.getValue(this, PreAnalysisImpl::scope)
        set(value) {
            scopeLateInit.setValue(this, PreAnalysisImpl::scope, value)
        }

    private val globalNormalAnalyzeSemaphore by lazy { createNormalAnalyzeSemaphore() }
    private val globalResourceSemaphore by lazy { createResourceSemaphore() }
    private val filesWhichHitSizeThreshold: MutableSet<IResource> = Kotlin_extKt.concurrentHashSetOf()
    private val maximumFileSizeThresholdWarnings: Int = preAnalysisConfig?.maximumFileSizeThresholdWarnings ?: 20
    private val OrigAction: String = "PreAnalysis: Process"
    private val invokePointData by lazy { createInvokePointData() }

    val outputPath: Path
        get() = mainConfig.output_dir.path

    val fullCanonicalPathString: String
        get() = Resource.INSTANCE.of(outputPath).absolute.normalize.toString()

    init {
        val classes = scene.classes.filter { clazz ->
            !scene.isExcluded(clazz) && !OthersKt.isSyntheticComponent(clazz)
        }.toSet()
        
        allClasses = classes
        allMethods = classes.flatMap { it.methods }
        allFields = classes.flatMap { it.fields }
        appOnlyClasses = classes.filter { it.isApplicationClass }
        appOnlyMethods = appOnlyClasses.flatMap { it.methods }
        appOnlyFields = appOnlyClasses.flatMap { it.fields }
    }

    private fun createNormalAnalyzeSemaphore(permit: Int = mainConfig.parallelsNum * 2): Semaphore {
        return Semaphore(permit)
    }

    private fun createResourceSemaphore(permit: Int = preAnalysisConfig?.largeFileSemaphorePermits ?: 3): Semaphore {
        return createNormalAnalyzeSemaphore(permit)
    }

    private fun createInvokePointData(): InvokePointData {
        val objectType = Scene.v().getRefTypeUnsafe("java.lang.Object") ?: 
            return InvokePointData(HashMultiMap(), emptySet())

        val targetsToEdges = HashMultiMap<SootMethod, InvokeCheckPoint>()
        val allPoint = LinkedHashSet<InvokeCheckPoint>()

        cg.iterator().forEach { edge ->
            val src = edge.src()
            if (src != null && appOnlyClasses.contains(src.declaringClass)) {
                val tgt = edge.tgt()
                val callSite = edge.srcUnit() as? Stmt
                val invokeExpr = callSite?.invokeExpr as? InstanceInvokeExpr
                val declaredReceiverType = invokeExpr?.base?.type

                if (declaredReceiverType != objectType) {
                    val subSignature = tgt.subSignature
                    val declaringClass = tgt.declaringClass
                    var invokeCheckPoint: InvokeCheckPoint? = null

                    SootUtilsKt.findMethodOrNull(declaringClass, subSignature).forEach { find ->
                        if (find == tgt || !find.isConcrete) {
                            if (invokeCheckPoint == null) {
                                invokeCheckPoint = InvokeCheckPoint(
                                    info,
                                    src,
                                    callSite,
                                    declaredReceiverType,
                                    invokeExpr?.methodRef,
                                    tgt,
                                    invokeExpr
                                )
                                allPoint.add(invokeCheckPoint)
                            }
                            targetsToEdges.put(find, invokeCheckPoint)
                        }
                    }
                }
            }
        }

        return InvokePointData(targetsToEdges, allPoint)
    }

    override fun uninitializedScope() {
        scopeLateInit.uninitialized()
    }

    override fun <T> Result<T?>.nonNull(): Result<T> {
        return object : Result<T>(this) {
            private val asyncResult = scope.async {
                await().filterNotNull()
            }

            override val asyncResult: Deferred<List<T>> = asyncResult
        }
    }

    private fun getPhaseTimer(unit: CheckerUnit, apiName: String): Timer? {
        return monitor?.timer("PreAnalysis:$apiName.At:${UtilsKt.getSootTypeName(unit.clazz)}")
    }

    context(CheckerUnit)
    override fun <T> atClass(
        classes: Collection<SootClass>,
        config: (IPreAnalysisClassConfig) -> Unit,
        block: (IClassCheckPoint, Continuation<T>) -> Any?
    ): Result<T> {
        val t = getPhaseTimer(this@CheckerUnit, "atClass")
        val conf = PreAnalysisClassConfig().apply(config)
        return object : Result<T>(this, t, classes, conf, block) {
            override val asyncResult: Deferred<List<T>> = scope.async {
                classes.mapNotNull { clazz ->
                    if (!skip(clazz)) {
                        block(ClassCheckPoint(clazz, info), this)
                    } else null
                }
            }
        }
    }

    context(CheckerUnit)
    override fun <T> atMethod(
        methods: Collection<SootMethod>,
        config: (IPreAnalysisMethodConfig) -> Unit,
        block: (IMethodCheckPoint, Continuation<T>) -> Any?
    ): Result<T> {
        val t = getPhaseTimer(this@CheckerUnit, "atMethod")
        val conf = PreAnalysisMethodConfig().apply(config)
        return object : Result<T>(this, t, methods, conf, block) {
            override val asyncResult: Deferred<List<T>> = scope.async {
                methods.mapNotNull { method ->
                    if (!skip(method)) {
                        block(MethodCheckPoint(method, info), this)
                    } else null
                }
            }
        }
    }

    context(CheckerUnit)
    override fun <T> atField(
        fields: Collection<SootField>,
        config: (IPreAnalysisFieldConfig) -> Unit,
        block: (IFieldCheckPoint, Continuation<T>) -> Any?
    ): Result<T> {
        val t = getPhaseTimer(this@CheckerUnit, "atField")
        val conf = PreAnalysisFieldConfig().apply(config)
        return object : Result<T>(this, t, fields, conf, block) {
            override val asyncResult: Deferred<List<T>> = scope.async {
                fields.mapNotNull { field ->
                    if (!skip(field)) {
                        block(FieldCheckPoint(field, info), this)
                    } else null
                }
            }
        }
    }

    context(CheckerUnit)
    override fun <T> atInvoke(
        targets: List<SootMethod>?,
        config: (IPreAnalysisInvokeConfig) -> Unit,
        block: (IInvokeCheckPoint, Continuation<T>) -> Any?
    ): Result<T> {
        val t = getPhaseTimer(this@CheckerUnit, "atInvoke")
        val conf = PreAnalysisInvokeConfig().apply(config)
        return object : Result<T>(this, t, targets, conf, block) {
            override val asyncResult: Deferred<List<T>> = scope.async {
                invokeCheckPoints(targets).mapNotNull { point ->
                    block(point, this)
                }
            }
        }
    }

    fun chooseSemaphore(fileSize: Long): Semaphore {
        val threshold = preAnalysisConfig?.largeFileSize ?: return globalNormalAnalyzeSemaphore
        return if (fileSize > threshold) globalResourceSemaphore else globalNormalAnalyzeSemaphore
    }

    context(CheckerUnit)
    override fun <T> atSourceFile(
        files: (Continuation<Iterator<IResFile>>) -> Any?,
        config: (IPreAnalysisFileConfig) -> Unit,
        block: (ISourceFileCheckPoint, Continuation<T>) -> Any?
    ): Result<T> {
        val t = getPhaseTimer(this@CheckerUnit, "atSourceFile")
        val conf = PreAnalysisFileConfig().apply(config)
        val safeAnalyzeUtil = SafeAnalyzeUtil(cancelAnalysisInErrorCount)

        return object : Result<T>(this, files, conf, t, block) {
            override val asyncResult: Deferred<List<T>> = scope.async {
                val iterator = files(this) as Iterator<IResFile>
                iterator.asSequence().mapNotNull { file ->
                    if (!scope.isActive) return@async emptyList()
                    if (!file.isFile || !file.exists || skip(file.path)) return@mapNotNull null
                    
                    if (conf.incrementalAnalyze) {
                        dg?.shouldReAnalyzeTarget(file)?.takeIf { it == ProcessRule.ScanAction.Skip }?.let { return@mapNotNull null }
                    }

                    if (conf.skipFilesInArchive && MainConfigKt.skipResourceInArchive(mainConfig, file)) {
                        return@mapNotNull null
                    }

                    val fileSize = Files.size(file.path)
                    if (preAnalysisConfig?.fileSizeThresholdExceeded(file.extension, fileSize) == true) {
                        if (filesWhichHitSizeThreshold.add(file)) {
                            val message = "File size threshold (${preAnalysisConfig.largeFileSize}) exceeded for $file (size: $fileSize)"
                            if (filesWhichHitSizeThreshold.size < maximumFileSizeThresholdWarnings) {
                                logger.warn { message }
                            } else {
                                if (filesWhichHitSizeThreshold.size == maximumFileSizeThresholdWarnings) {
                                    logger.warn { "File size threshold exceeded ........ (more than $maximumFileSizeThresholdWarnings. check log: ${AnalyzerEnv.INSTANCE.lastLogFile})" }
                                }
                                logger.debug { message }
                            }
                        }
                    }

                    val semaphore = chooseSemaphore(fileSize)
                    safeAnalyzeUtil.safeRunInSceneAsync(this@CheckerUnit) {
                        semaphore.withPermit {
                            block(SourceFileCheckPoint(file), this)
                        }
                    }
                }.toList()
            }
        }
    }

    context(CheckerUnit)
    override fun <T> atClass(clazz: IClassMatch, config: (IPreAnalysisClassConfig) -> Unit, block: (IClassCheckPoint, Continuation<T>) -> Any?): Result<T> {
        return atClass(clazz.matched(scene), config, block)
    }

    context(CheckerUnit)
    override fun <T> atAnyClass(config: (IPreAnalysisClassConfig) -> Unit, block: (IClassCheckPoint, Continuation<T>) -> Any?): Result<T> {
        val conf = PreAnalysisClassConfig().apply(config)
        return atClass(if (conf.appOnly) appOnlyClasses else allClasses, config, block)
    }

    context(CheckerUnit)
    override fun <T> atMethod(method: IMethodMatch, config: (IPreAnalysisMethodConfig) -> Unit, block: (IMethodCheckPoint, Continuation<T>) -> Any?): Result<T> {
        return atMethod(method.matched(scene), config, block)
    }

    context(CheckerUnit)
    override fun <T> atAnyMethod(config: (IPreAnalysisMethodConfig) -> Unit, block: (IMethodCheckPoint, Continuation<T>) -> Any?): Result<T> {
        val conf = PreAnalysisMethodConfig().apply(config)
        return atMethod(if (conf.appOnly) appOnlyMethods else allMethods, config, block)
    }

    context(CheckerUnit)
    override fun <T> atField(field: IFieldMatch, config: (IPreAnalysisFieldConfig) -> Unit, block: (IFieldCheckPoint, Continuation<T>) -> Any?): Result<T> {
        return atField(field.matched(scene), config, block)
    }

    context(CheckerUnit)
    override fun <T> atAnyField(config: (IPreAnalysisFieldConfig) -> Unit, block: (IFieldCheckPoint) -> T): Result<T> {
        val conf = PreAnalysisFieldConfig().apply(config)
        return atField(if (conf.appOnly) appOnlyFields else allFields, config) { point, _ -> block(point) }
    }

    context(CheckerUnit)
    override fun <T> atInvoke(callee: IMethodMatch, config: (IPreAnalysisInvokeConfig) -> Unit, block: (IInvokeCheckPoint, Continuation<T>) -> Any?): Result<T> {
        return atInvoke(callee.matched(scene), config, block)
    }

    context(CheckerUnit)
    override fun <T> atAnyInvoke(config: (IPreAnalysisInvokeConfig) -> Unit, block: (IInvokeCheckPoint, Continuation<T>) -> Any?): Result<T> {
        return atInvoke(null, config, block)
    }

    context(CheckerUnit)
    override fun <T> atSourceFile(
        path: Path,
        config: (IPreAnalysisFileConfig) -> Unit,
        block: (ISourceFileCheckPoint, Continuation<T>) -> Any?
    ): Result<T> {
        return atSourceFile({ cont ->
            listOf(Resource.INSTANCE.fileOf(path)).iterator()
        }, config, block)
    }

    context(CheckerUnit)
    override fun <T> atAnySourceFile(
        extension: String?,
        filename: String?,
        config: (IPreAnalysisFileConfig) -> Unit,
        block: (ISourceFileCheckPoint, Continuation<T>) -> Any?
    ): Result<T> {
        return atSourceFile({ cont ->
            findFiles(extension, filename, cont)
        }, config, block)
    }

    override fun report(checkType: CheckType, file: Path, region: Region, env: (Env) -> Unit) {
        val fileInfo = FileResInfo(Resource.INSTANCE.fileOf(file))
        val info = DefaultEnv(region.mutable, fileInfo.reportFileName)
        env(info)
        resultCollector.report(checkType, PreAnalysisReportEnv(fileInfo, info))
    }

    override fun report(checkType: CheckType, sootHost: Host, env: (Env) -> Unit) {
        val point = when (sootHost) {
            is SootClass -> ClassCheckPoint(sootHost, info)
            is SootMethod -> MethodCheckPoint(sootHost, info)
            is SootField -> FieldCheckPoint(sootHost, info)
            else -> {
                logger.error { "SootHost type in report(,sootHost: ${sootHost.javaClass},) is not support yet! only support types: SootClass, SootMethod, SootField" }
                return
            }
        }
        report(point, checkType, env)
    }

    context(CheckerUnit)
    override fun <T> runInSceneAsync(block: (Continuation<T>) -> Any?): Deferred<T?> {
        val safeAnalyzeUtil = SafeAnalyzeUtil(cancelAnalysisInErrorCount)
        return scope.async {
            safeAnalyzeUtil.safeRunInSceneAsync(this@CheckerUnit, block)
        }
    }

    override fun <P> P.report(checkType: CheckType, env: (Env) -> Unit) where P : ICheckPoint, P : INodeWithRange {
        val info = (this as CheckPoint).getEnv()
        env(info)
        resultCollector.report(checkType, PreAnalysisReportEnv((this as CheckPoint).file, info))
    }

    override fun archivePath(archiveFile: Path, entry: String): Path {
        return Resource.INSTANCE.archivePath(archiveFile, entry)
    }

    override fun getZipEntry(innerFilePath: Path): Pair<Path, String>? {
        val file = Resource.INSTANCE.of(innerFilePath)
        return file.zipEntry?.let { file.schemePath to it }
    }

    override fun Path.getShadowFile(copyDest: Path?): File {
        val file = Resource.INSTANCE.of(this)
        val destDir = copyDest?.let { 
            Resource.INSTANCE.dirOf(it).takeIf { it.isFileScheme } 
        } ?: mainConfig.output_dir
        return file.expandRes(destDir).file
    }

    override fun globPath(glob: String): List<Path>? {
        return ResourceImplKt.globPath(glob)?.map { it.path }
    }

    suspend fun findFiles(extension: String?, filename: String?): Sequence<IResFile> {
        return when {
            extension != null -> locator.getByFileExtension(extension)
            filename != null -> locator.getByFileName(filename)
            else -> locator.getAllFiles()
        }
    }

    private fun IPreAnalysisConfig.skip(file: Path): Boolean {
        return skip(OrigAction, scanFilter.processRegex.clazzRules, lazy { scanFilter.get(file) })
    }

    private fun IPreAnalysisConfig.skip(sc: SootClass): Boolean {
        return skip(OrigAction, scanFilter.processRegex.clazzRules, lazy { scanFilter.get(sc) })
    }

    private fun IPreAnalysisConfig.skip(sf: SootField): Boolean {
        return skip(OrigAction, scanFilter.processRegex.clazzRules, lazy { scanFilter.get(sf) })
    }

    private fun IPreAnalysisConfig.skip(sm: SootMethod): Boolean {
        return skip(OrigAction, scanFilter.processRegex.clazzRules, lazy { scanFilter.get(sm) })
    }

    private fun IPreAnalysisConfig.skip(
        origAction: String? = OrigAction,
        rule: List<IMatchItem>,
        target: Lazy<IMatchTarget>
    ): Boolean {
        if (!ignoreProjectConfigProcessFilter && 
            ScanFilter.getActionOf(scanFilter, rule, origAction, target.value) == ProcessRule.ScanAction.Skip) {
            return true
        }
        if (processRules.isEmpty()) return false
        return ProcessRule.matches(processRules, target.value).second == ProcessRule.ScanAction.Skip
    }

    fun invokeCheckPoints(atInvoke: List<SootMethod>?): Set<InvokeCheckPoint> {
        return if (atInvoke != null) {
            atInvoke.flatMapTo(LinkedHashSet()) { method ->
                invokePointData.targetsToEdges[method] ?: emptySet()
            }
        } else {
            invokePointData.allPoint
        }
    }

    override fun processPreAnalysisUnits(completion: Continuation<Unit>): Any? {
        return PreAnalysisCoroutineScope.DefaultImpls.processPreAnalysisUnits(this, completion)
    }

    override fun <T> runInScene(context_receiver_0: CheckerUnit, block: (Continuation<T>) -> Any): Job {
        return PreAnalysisCoroutineScope.DefaultImpls.runInScene(this, context_receiver_0, block)
    }

    override fun <T> atMethod(
        context_receiver_0: CheckerUnit,
        method: KCallable<*>,
        config: (IPreAnalysisMethodConfig) -> Unit,
        block: (IMethodCheckPoint, Continuation<T>) -> Any
    ): PreAnalysisApi.Result<T> {
        return PreAnalysisCoroutineScope.DefaultImpls.atMethod(this, context_receiver_0, method, config, block)
    }

    override fun <T> atInvoke(
        context_receiver_0: CheckerUnit,
        callee: KCallable<*>,
        config: (IPreAnalysisInvokeConfig) -> Unit,
        block: (IInvokeCheckPoint, Continuation<T>) -> Any
    ): PreAnalysisApi.Result<T> {
        return PreAnalysisCoroutineScope.DefaultImpls.atInvoke(this, context_receiver_0, callee, config, block)
    }

    override fun <P : ICheckPoint> P.report(
        checkType: CheckType,
        region: Region,
        env: (Env) -> Unit
    ) where P : INodeWithRange {
        PreAnalysisCoroutineScope.DefaultImpls.report(this, checkType, region, env)
    }

    override fun ISourceFileCheckPoint.report(
        checkType: CheckType,
        region: Region,
        env: (Env) -> Unit
    ) {
        PreAnalysisCoroutineScope.DefaultImpls.report(this, checkType, region, env)
    }

    override fun ISourceFileCheckPoint.report(
        checkType: CheckType,
        cpgRegion: de.fraunhofer.aisec.cpg.sarif.Region,
        env: (Env) -> Unit
    ) {
        PreAnalysisCoroutineScope.DefaultImpls.report(this, checkType, cpgRegion, env)
    }

    override fun ISourceFileCheckPoint.report(
        checkType: CheckType,
        jpsStart: Position,
        jpsEnd: Position?,
        env: (Env) -> Unit
    ) {
        PreAnalysisCoroutineScope.DefaultImpls.report(this, checkType, jpsStart, jpsEnd, env)
    }

    override fun ISourceFileCheckPoint.report(
        checkType: CheckType,
        regionNode: NodeWithRange<*>,
        env: (Env) -> Unit
    ) {
        PreAnalysisCoroutineScope.DefaultImpls.report(this, checkType, regionNode, env)
    }

    override fun report(
        checkType: CheckType,
        sootHost: Host,
        region: Region?,
        env: (Env) -> Unit
    ) {
        PreAnalysisCoroutineScope.DefaultImpls.report(this, checkType, sootHost, region, env)
    }

    override fun toDecl(target: Any): XDecl {
        return MainConfigKt.simpleIAnalysisDepends(mainConfig).toDecl(target)
    }

    override infix fun XDecl.dependsOn(dep: XDecl) {
        MainConfigKt.simpleIAnalysisDepends(mainConfig).dependsOn(this, dep)
    }

    override infix fun Collection<XDecl>.dependsOn(deps: Collection<XDecl>) {
        MainConfigKt.simpleIAnalysisDepends(mainConfig).dependsOn(this, deps)
    }

    companion object {
        val kLogger: KLogger = TODO("Initialize logger")
    }

    data class InvokePointData(
        val targetsToEdges: MultiMap<SootMethod, InvokeCheckPoint>,
        val allPoint: Set<InvokeCheckPoint>
    ) {
        operator fun component1() = targetsToEdges
        operator fun component2() = allPoint
        
        fun copy(
            targetsToEdges: MultiMap<SootMethod, InvokeCheckPoint> = this.targetsToEdges,
            allPoint: Set<InvokeCheckPoint> = this.allPoint
        ) = InvokePointData(targetsToEdges, allPoint)

        override fun toString() = "InvokePointData(targetsToEdges=$targetsToEdges, allPoint=$allPoint)"
        
        override fun hashCode() = targetsToEdges.hashCode() * 31 + allPoint.hashCode()
        
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is InvokePointData) return false
            return targetsToEdges == other.targetsToEdges && allPoint == other.allPoint
        }
    }
}