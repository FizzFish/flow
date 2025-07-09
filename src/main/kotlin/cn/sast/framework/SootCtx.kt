package cn.sast.framework

import cn.sast.api.config.ExtSettings
import cn.sast.api.config.MainConfig
import cn.sast.api.config.SaConfig
import cn.sast.api.config.ScanFilter
import cn.sast.api.config.SrcPrecedence
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.ProjectMetrics
import cn.sast.api.util.IMonitor
import cn.sast.api.util.Kotlin_extKt
import cn.sast.api.util.OthersKt
import cn.sast.api.util.PhaseIntervalTimer
import cn.sast.api.util.Timer
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.Resource
import cn.sast.framework.compiler.EcjCompiler
import cn.sast.framework.graph.CGUtils
import cn.sast.framework.report.NullWrapperFileGenerator
import cn.sast.framework.report.ProjectFileLocator
import cn.sast.framework.rewrite.StringConcatRewriterTransform
import cn.sast.idfa.analysis.ProcessInfoView
import com.feysh.corax.config.api.ISootInitializeHandler
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.rendering.Theme
import driver.PTAFactory
import driver.PTAPattern
import java.io.File
import java.lang.reflect.Field
import java.nio.file.InvalidPathException
import java.time.LocalDateTime
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashSet
import java.util.LinkedList
import java.util.NoSuchElementException
import java.util.SortedSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.function.Function
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import java.util.Comparator
import kotlin.comparisons.compareValues
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentSet
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe
import org.utbot.framework.plugin.services.JdkInfo
import org.utbot.framework.plugin.services.JdkInfoService
import qilin.CoreConfig
import qilin.CoreConfig.CorePTAConfiguration
import qilin.core.PTA
import qilin.core.PTAScene
import qilin.core.pag.ValNode
import qilin.pta.PTAConfig
import qilin.util.MemoryWatcher
import qilin.util.PTAUtils
import soot.G
import soot.Main
import soot.MethodOrMethodContext
import soot.PackManager
import soot.PointsToAnalysis
import soot.PointsToAnalysis
import soot.Scene
import soot.Singletons
import soot.SootClass
import soot.SootMethod
import soot.Transform
import soot.Transformer
import soot.jimple.infoflow.AbstractInfoflow
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import soot.jimple.toolkits.pointer.DumbPointerAnalysis
import soot.options.Options
import soot.util.Chain

open class SootCtx(mainConfig: MainConfig) : ISootInitializeHandler {
    val mainConfig: MainConfig
    val monitor: IMonitor?
    private val _loadClassesTimer: Timer?
    private val _classesClassificationTimer: Timer?
    private val _cgConstructTimer: Timer?

    lateinit var cgAlgorithmProvider: CgAlgorithmProvider
        internal set

    val autoAppClassesLocator: ProjectFileLocator by lazy {
        ProjectFileLocator(
            mainConfig.getMonitor(),
            mainConfig.getAutoAppClasses(),
            null,
            mainConfig.getAutoAppTraverseMode(),
            false,
            16,
            null
        )
    }

    var callGraph: CallGraph
        get() = Scene.v().callGraph
        set(value) {
            Scene.v().callGraph = value
        }

    val sootMethodCallGraph: CallGraph
        get() {
            val cg = CallGraph()
            for (e in callGraph) {
                if (!e.isInvalid()) {
                    cg.addEdge(Edge(e.src() as MethodOrMethodContext, e.srcUnit(), e.tgt() as MethodOrMethodContext, e.kind()))
                }
            }
            return cg
        }

    val entryPoints: List<SootMethod>
        get() = Scene.v().entryPoints

    private inner class JarRelativePathComparator(private val ctx: SootCtx) : Comparator<IResFile> {
        override fun compare(a: IResFile, b: IResFile): Int {
            return compareValues(
                ctx.mainConfig.tryGetRelativePath(a).getRelativePath(),
                ctx.mainConfig.tryGetRelativePath(b).getRelativePath()
            )
        }
    }

    private object StringComparator : Comparator<String> {
        override fun compare(a: String, b: String): Int = a.compareTo(b)
    }

    init {
        this.mainConfig = mainConfig
        this.monitor = mainConfig.getMonitor()
        this._loadClassesTimer = monitor?.timer("loadClasses")
        this._classesClassificationTimer = monitor?.timer("classes.classification")
        this._cgConstructTimer = monitor?.timer("callgraph.construct")
    }

    open fun configureCallGraph(options: Options) {
        cgAlgorithmProvider = configureCallGraph(
            options,
            mainConfig.getCallGraphAlgorithm(),
            mainConfig.getApponly(),
            mainConfig.getEnableReflection()
        )
    }

    open fun configureCallGraph(
        options: Options,
        callGraphAlgorithm: String,
        appOnly: Boolean,
        enableReflection: Boolean
    ): CgAlgorithmProvider {
        val appOnlySootOptionValue = "apponly:${if (appOnly) "true" else "false"}"
        options.set_ignore_resolving_levels(true)
        logger.info { "using cg algorithm: $callGraphAlgorithm with $appOnly" }

        return when {
            callGraphAlgorithm.equals("SPARK", true) -> {
                options.setPhaseOption("cg.spark", "on")
                options.setPhaseOption("cg.spark", "on-fly-cg:true")
                options.setPhaseOption("cg.spark", appOnlySootOptionValue)
                CgAlgorithmProvider.Soot
            }
            callGraphAlgorithm.startsWith("GEOM", true) -> {
                options.setPhaseOption("cg.spark", "on")
                options.setPhaseOption("cg.spark", appOnlySootOptionValue)
                AbstractInfoflow.setGeomPtaSpecificOptions()
                when (callGraphAlgorithm.substringAfter("-", "")) {
                    "HeapIns" -> options.setPhaseOption("cg.spark", "geom-encoding:HeapIns")
                    "PtIns" -> options.setPhaseOption("cg.spark", "geom-encoding:PtIns")
                    else -> throw IllegalStateException("$callGraphAlgorithm is incorrect")
                }
                CgAlgorithmProvider.Soot
            }
            callGraphAlgorithm.equals("CHA", true) -> {
                options.setPhaseOption("cg.cha", "on")
                options.setPhaseOption("cg.cha", appOnlySootOptionValue)
                CgAlgorithmProvider.Soot
            }
            callGraphAlgorithm.equals("RTA", true) -> {
                options.setPhaseOption("cg.spark", "on")
                options.setPhaseOption("cg.spark", "rta:true")
                options.setPhaseOption("cg.spark", "on-fly-cg:false")
                options.setPhaseOption("cg.spark", appOnlySootOptionValue)
                CgAlgorithmProvider.Soot
            }
            callGraphAlgorithm.equals("VTA", true) -> {
                options.setPhaseOption("cg.spark", "on")
                options.setPhaseOption("cg.spark", "vta:true")
                options.setPhaseOption("cg.spark", appOnlySootOptionValue)
                CgAlgorithmProvider.Soot
            }
            else -> {
                PTAConfig.v().ptaConfig.apply {
                    ptaPattern = PTAPattern(callGraphAlgorithm)
                    singleentry = false
                    ctxDebloating = true
                    stringConstants = true
                }
                CgAlgorithmProvider.QiLin
            }
        }.also {
            if (enableReflection) {
                options.setPhaseOption("cg", "types-for-invoke:true")
            }
            options.setPhaseOption("cg.spark", "set-impl:hybrid")
            options.setPhaseOption("jb", "model-lambdametafactory:false")
            options.setPhaseOption("jb.ulp", "off")
        }
    }

    open fun constructCallGraph(cgAlgorithm: CgAlgorithmProvider, appOnly: Boolean, record: Boolean = true) {
        val scene = Scene.v()
        releaseCallGraph()
        onBeforeCallGraphConstruction()

        if (!mainConfig.getSkipClass()) {
            val applicationClasses = scene.applicationClasses
            if (applicationClasses.isEmpty() || applicationClasses.none { !OthersKt.isSyntheticComponent(it) }) {
                throw IllegalStateException(
                    "application classes must not be empty. check your --auto-app-classes, --process, --source-path, --class-path options"
                )
            }
        }

        scene.getOrMakeFastHierarchy()
        val timerSnapshot = _cgConstructTimer?.start()
        LoggingKt.info(logger).logMethod.invoke { "Constructing the call graph [$cgAlgorithm] ..." }

        val startTime = LocalDateTime.now()
        var total = false
        val res = ObjectRef<Maybe<*>>().apply { element = Maybe.empty() }

        try {
            try {
                monitor?.timer("cgAlgorithm:$cgAlgorithm")?.let { timer ->
                    timer.start().use {
                        when (cgAlgorithm) {
                            CgAlgorithmProvider.Soot -> PackManager.v().getPack("cg").apply()
                            CgAlgorithmProvider.QiLin -> {
                                PTAUtils.setAppOnly(appOnly)
                                CoreConfig.v().ptaConfig.apply {
                                    printAliasInfo = ExtSettings.INSTANCE.getPrintAliasInfo()
                                    castNeverFailsOfPhantomClass = ExtSettings.INSTANCE.getCastNeverFailsOfPhantomClass()
                                }
                                ValNode.UseRoaringPointsToSet = ExtSettings.INSTANCE.getUseRoaringPointsToSet()
                                val pta = PTAFactory.createPTA(PTAConfig.v().ptaConfig.ptaPattern)
                                pta.cgb.reachableMethods
                                pta.run()
                                scene.pointsToAnalysis = pta
                                PTAUtils.clear()
                                pta.pag.small()
                            }
                        }
                    }
                } ?: run {
                    when (cgAlgorithm) {
                        CgAlgorithmProvider.Soot -> PackManager.v().getPack("cg").apply()
                        CgAlgorithmProvider.QiLin -> {
                            PTAUtils.setAppOnly(appOnly)
                            CoreConfig.v().ptaConfig.apply {
                                printAliasInfo = ExtSettings.INSTANCE.getPrintAliasInfo()
                                castNeverFailsOfPhantomClass = ExtSettings.INSTANCE.getCastNeverFailsOfPhantomClass()
                            }
                            ValNode.UseRoaringPointsToSet = ExtSettings.INSTANCE.getUseRoaringPointsToSet()
                            val pta = PTAFactory.createPTA(PTAConfig.v().ptaConfig.ptaPattern)
                            pta.cgb.reachableMethods
                            pta.run()
                            scene.pointsToAnalysis = pta
                            PTAUtils.clear()
                            pta.pag.small()
                        }
                    }
                }
            } finally {
                logger.info { "After build CG: Process information: ${ProcessInfoView.Companion.getGlobalProcessInfo().getProcessInfoText()}" }
            }
        } catch (t: Throwable) {
            LoggingKt.info(logger).logMethod.invoke { "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): Constructing the call graph [$cgAlgorithm] ... :: EXCEPTION :: " }
            total = true
            throw t
        } finally {
            if (!total) {
                if (res.element.hasValue) {
                    LoggingKt.info(logger).logMethod.invoke { "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): Constructing the call graph [$cgAlgorithm] ... " }
                } else {
                    LoggingKt.info(logger).logMethod.invoke { "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): Constructing the call graph [$cgAlgorithm] ... <Nothing>" }
                }
            }
        }

        _cgConstructTimer?.stop(timerSnapshot)
        CGUtils.INSTANCE.addCallEdgeForPhantomMethods()
        showPta()
        scene.releaseReachableMethods()
        CGUtils.INSTANCE.fixScene(scene)

        if (record) {
            val (appMethodsWithBody, appMethodsTotal) = activeBodyMethods(scene.applicationClasses)
            monitor?.projectMetrics?.apply {
                setApplicationMethodsHaveBody(appMethodsWithBody)
                setApplicationMethods(appMethodsTotal)
            }

            val (libMethodsWithBody, libMethodsTotal) = activeBodyMethods(scene.libraryClasses)
            monitor?.projectMetrics?.apply {
                setLibraryMethodsHaveBody(libMethodsWithBody)
                setLibraryMethods(libMethodsTotal)
            }
        }

        onAfterCallGraphConstruction()
    }

    open fun constructCallGraph() {
        constructCallGraph(cgAlgorithmProvider, mainConfig.getApponly(), false)
    }

    fun showPta() {
        val pta = Scene.v().pointsToAnalysis
        if (pta is DumbPointerAnalysis) {
            logger.warn { "PointsToAnalysis of scene is DumbPointerAnalysis!!!" }
            Scene.v().pointsToAnalysis = pta
        }

        logger.info { "PointsToAnalysis of scene is ${pta.javaClass.simpleName}" }
        logger.info { "Call Graph has been constructed with ${Scene.v().callGraph.size()} edges." }
        showClasses(Scene.v(), "After PTA: ") { Theme.default.info(it) }
    }

    suspend fun findClassesInnerJar(locator: ProjectFileLocator): Map<String, MutableSet<IResFile>> {
        return coroutineScope {
            val md5Map = ConcurrentHashMap<String, IResFile>()
            val md5Group = ConcurrentHashMap<String, MutableSet<IResFile>>()

            locator.getByFileExtension("class").forEach { clz ->
                if (clz.isJarScheme()) {
                    try {
                        val jar = Resource.INSTANCE.fileOf(clz.schemePath)
                        launch {
                            val md5 = jar.getMd5()
                            md5Group.computeIfAbsent(md5) { ConcurrentHashMap.newKeySet() }.add(jar)
                        }
                    } catch (e: Exception) {
                        logger.error(e) { "extract jar file ${clz.path} with error: ${e.message}" }
                        logger.debug(e) { "extract jar file ${clz.path}" }
                    }
                }
            }

            md5Group
        }
    }

    suspend fun findClassesInnerJarUnderAutoAppClassPath(): Set<IResFile> {
        require(mainConfig.getAutoAppClasses().isNotEmpty()) { "Check failed." }

        val result = findClassesInnerJar(autoAppClassesLocator)
        val jars = result.values.map { files ->
            files.minByOrNull { mainConfig.tryGetRelativePath(it).getRelativePath() }!!
        }.sortedWith(JarRelativePathComparator(this))

        return jars.mapNotNull { jar ->
            when {
                jar.isFileScheme() -> jar
                jar.isJarScheme() -> try {
                    jar.expandRes(mainConfig.getOutput_dir()).toFile()
                } catch (e: InvalidPathException) {
                    logger.error(e) { "Bad archive file: $jar" }
                    null
                }
                else -> {
                    logger.error { "unknown scheme: ${jar.uri}" }
                    null
                }
            }
        }.toCollection(LinkedHashSet())
    }

    override fun configure(options: Options) {
        if (mainConfig.getSrc_precedence() == SrcPrecedence.prec_java && !mainConfig.isAndroidScene()) {
            val autoAppClasses = mainConfig.getOutput_dir().resolve("gen-classes").toDirectory().apply {
                deleteDirectoryRecursively()
                mkdirs()
            }

            val compiler = EcjCompiler(
                mainConfig.getProcessDir().toPersistentSet(),
                mainConfig.getClasspath(),
                autoAppClasses,
                mainConfig.getEcj_options(),
                mainConfig.getUseDefaultJavaClassPath(),
                null,
                null,
                false,
                null,
                null,
                992,
                null
            )

            if (!compiler.compile()) {
                logger.error { "\n\n!!! There are some errors in source code compilation !!!\n\n" }
            }

            require(autoAppClasses.listPathEntries().isNotEmpty()) {
                "\n\n!!! no class file found under $autoAppClasses !!!\n\n"
            }

            mainConfig.setProcessDir(mainConfig.getProcessDir().add(autoAppClasses))
            mainConfig.setClasspath(compiler.getCollectClassPath().toPersistentSet())
        }

        autoAppClassesLocator.update()
        if (mainConfig.getAutoAppClasses().isNotEmpty() && !mainConfig.isAndroidScene() && !mainConfig.getSkipClass()) {
            runBlocking {
                mainConfig.setProcessDir(mainConfig.getProcessDir().addAll(findClassesInnerJarUnderAutoAppClassPath()))
            }
        }

        options.apply {
            set_verbose(true)
            set_allow_phantom_elms(true)
            set_whole_program(mainConfig.getWhole_program())
            set_src_prec(mainConfig.getSrc_precedence().sootFlag)
            set_prepend_classpath(mainConfig.getPrepend_classpath())
            set_no_bodies_for_excluded(mainConfig.getNo_bodies_for_excluded())
            set_include_all(true)
            set_allow_phantom_refs(mainConfig.getAllow_phantom_refs())
            set_ignore_classpath_errors(false)
            set_throw_analysis(mainConfig.getThrow_analysis())
            set_process_multiple_dex(mainConfig.getProcess_multiple_dex())
            set_field_type_mismatches(2)
            set_full_resolver(true)
            classes().addAll(mainConfig.getAppClasses())
            set_app(false)
            set_search_dex_in_archives(true)
            set_process_dir(mainConfig.getSoot_process_dir().toList())
            set_output_format(mainConfig.getOutput_format())
            mainConfig.getSoot_output_dir().apply {
                deleteDirectoryRecursively()
                mkdirs()
            }
            set_output_dir(mainConfig.getSoot_output_dir().absolutePath)
            set_keep_offset(false)
            set_keep_line_number(mainConfig.getEnableLineNumbers())
            set_ignore_resolution_errors(true)

            if (mainConfig.getForceAndroidJar()) {
                set_force_android_jar(mainConfig.getAndroidPlatformDir())
            } else {
                set_android_jars(mainConfig.getAndroidPlatformDir())
            }
            logger.info { "android platform dir: ${mainConfig.getAndroidPlatformDir()}" }

            if (mainConfig.isAndroidScene()) {
                set_throw_analysis(3)
            }

            if (mainConfig.getEnableOriginalNames()) {
                setPhaseOption("jb", "use-original-names:true")
            }

            set_debug(false)
            set_verbose(false)
            set_validate(false)
        }

        configureCallGraph(options)
        PackManager.v().getPack("jb").add(Transform("jb.rewriter", StringConcatRewriterTransform()))
        mainConfig.getSaConfig()?.sootConfig?.configure(options)
    }

    fun configureSootClassPath(options: Options) {
        val cp = mainConfig.get_soot_classpath().toSortedSet()
        Scene.v().sootClassPath = null
        options.set_soot_classpath(cp.joinToString(File.pathSeparator))
    }

    override fun configureAfterSceneInit(scene: Scene, options: Options) {
        configureSootClassPath(options)
    }

    override fun configure(scene: Scene) {
        logger.info { "Initializing Soot Scene..." }
        scene.addBasicClass("java.lang.String", 3)
        scene.addBasicClass("java.lang.StringLatin1", 3)
        scene.addBasicClass("java.util.Arrays", 3)
        scene.addBasicClass("java.lang.Math", 3)
        scene.addBasicClass("java.lang.StringCoding", 3)
        mainConfig.getSaConfig()?.sootConfig?.configure(scene)
    }

    override fun configure(main: Main) {
        main.autoSetOptions()
        mainConfig.getSaConfig()?.sootConfig?.configure(main)
    }

    fun classesClassification(scene: Scene, locator: ProjectFileLocator?) {
        val timerSnapshot = _classesClassificationTimer?.start()
        require(!mainConfig.getSkipClass()) { "Check failed." }

        var findAny = false
        val autoAppClasses = mainConfig.getAutoAppClasses()
        showClasses(scene, "Before classes classification: ")

        scene.classes.forEach { sc ->
            if (!sc.isPhantom) {
                val sourceFile = autoAppClassesLocator.get(ClassResInfo.of(sc), NullWrapperFileGenerator.INSTANCE)
                val origAction = when {
                    autoAppClasses.isNotEmpty() -> when {
                        sourceFile == null || (!mainConfig.getAutoAppSrcInZipScheme() && !sourceFile.isFileScheme()) -> {
                            if (sc.isApplicationClass) sc.setLibraryClass()
                            "library"
                        }
                        else -> {
                            findAny = true
                            sc.setApplicationClass()
                            "application"
                        }
                    }
                    else -> null
                }

                val action = origAction ?: when {
                    sc.isApplicationClass -> "application"
                    sc.isLibraryClass -> "library"
                    else -> "phantom"
                }

                when (mainConfig.getScanFilter().getActionOf(
                    if (locator?.get(ClassResInfo.of(sc), NullWrapperFileGenerator.INSTANCE) != null) {
                        "(src exists) $action"
                    } else {
                        "(src not exists) $action"
                    },
                    sc
                )) {
                    ScanFilter.Action.APPLICATION -> sc.setApplicationClass()
                    ScanFilter.Action.LIBRARY -> sc.setLibraryClass()
                    ScanFilter.Action.PHANTOM -> {}
                }
            }
        }

        _classesClassificationTimer?.stop(timerSnapshot)
        if (autoAppClasses.isNotEmpty() && !findAny) {
            logger.error {
                "\n\n\nSince $autoAppClasses has no source files corresponding to the classes, " +
                "the classifier is unable to classify based on the location of the source code\n\n"
            }
        }

        showClasses(scene, "After classes classification: ")
    }

    fun Chain<SootClass>.activeBodyMethods(): Pair<Int, Int> {
        val methods = flatMap { it.methods }
        val total = methods.filter { !it.isAbstract }
        val active = total.filter { it.hasActiveBody }
        return active.size to total.size
    }

    fun Chain<SootClass>.show(): String {
        val (active, total) = activeBodyMethods()
        return if (total == 0) "empty" else "$size($total*${"%.2f".format(active.toFloat() / total)})"
    }

    fun showClasses(scene: Scene, prefix: String = "", fx: (String) -> String = { it }) {
        logger.info {
            fx(
                "$prefix" +
                "applicationClasses: ${scene.applicationClasses.show()}. " +
                "libraryClasses: ${scene.libraryClasses.show()}. " +
                "phantomClasses: ${scene.phantomClasses.show()}. " +
                "classes: ${scene.classes.show()}"
            )
        }
    }

    open fun loadClasses(scene: Scene, locator: ProjectFileLocator?) {
        G.v().sourceLocator = SourceLocatorPlus(mainConfig)

        when (cgAlgorithmProvider) {
            CgAlgorithmProvider.QiLin -> {
                Scene.v().addBasicClass("java.lang.ClassLoader", 3)
                Scene.v().addBasicClass("java.lang.ref.Finalizer", 3)
                PTAScene.v().addBasicClasses()
            }
        }

        if (!mainConfig.getSkipClass()) {
            logger.info { "\nsoot exclude ${getExcludedPackages(scene)}" }
            logger.info { "\nsoot classpath:\n ${getClassPathInfo(scene)}" }
            logger.info { "\nsoot process_dir:\n ${getProcessDirInfo()}" }

            LoggingKt.info(logger).run {
                val startTime = LocalDateTime.now()
                var alreadyLogged = false
                val res = ObjectRef<Maybe<*>>().apply { element = Maybe.empty() }

                try {
                    _loadClassesTimer?.start()?.use {
                        scene.loadNecessaryClasses(false)
                    } ?: scene.loadNecessaryClasses(false)

                    _classesClassificationTimer?.start()?.use {
                        classesClassification(scene, locator)
                    } ?: classesClassification(scene, locator)

                    _loadClassesTimer?.start()?.use {
                        scene.applicationClasses
                            .filter { !it.isPhantom }
                            .forEach { scene.loadClass(it.name, 3) }
                    } ?: scene.applicationClasses
                        .filter { !it.isPhantom }
                        .forEach { scene.loadClass(it.name, 3) }

                    res.element = Maybe(Unit)
                    if (res.element.hasValue) {
                        logMethod.invoke { "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}: Loading Necessary Classes... " }
                    } else {
                        logMethod.invoke { "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): Loading Necessary Classes... <Nothing>" }
                    }
                } catch (t: Throwable) {
                    logMethod.invoke { "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): Loading Necessary Classes... :: EXCEPTION :: " }
                    alreadyLogged = true
                    throw t
                } finally {
                    if (!alreadyLogged) {
                        if (res.element.hasValue) {
                            logMethod.invoke { "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): Loading Necessary Classes... " }
                        } else {
                            logMethod.invoke { "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): Loading Necessary Classes... <Nothing>" }
                        }
                    }
                }
            }

            logger.info { "Load classes done" }
            showClasses(scene, "After Loading Classes: ", Theme.default.warning)
        } else {
            scene.loadBasicClasses()
            scene.loadDynamicClasses()
            scene.doneResolving()
            CGUtils.INSTANCE.createDummyMain(scene)
        }

        logger.info { "After Loading Classes: ${ProcessInfoView.Companion.getGlobalProcessInfo().getProcessInfoText()}" }
        CGUtils.INSTANCE.removeLargeClasses(scene)
        CGUtils.INSTANCE.makeSpuriousMethodFromInvokeExpr()
        PackManager.v().getPack("wjpp").apply()
        LibraryClassPatcher().patchLibraries()

        monitor?.projectMetrics?.apply {
            setApplicationClasses(scene.applicationClasses.size)
            setLibraryClasses(scene.libraryClasses.size)
            setPhantomClasses(scene.phantomClasses.size)
        }

        (mainConfig.getIncrementAnalyze() as? IncrementalAnalyzeImplByChangeFiles)?.update(scene, locator)
        logger.info { "After Rewrite Classes: ${ProcessInfoView.Companion.getGlobalProcessInfo().getProcessInfoText()}" }
    }

    open fun configureSoot() {
        Companion.restAll()
        mainConfig.validate()
        val options = Options.v().apply {
            configure(options)
            require(instance_soot_Scene == null) {
                "Soot should not be initialized in clinit or init. check your plugins"
            }
        }

        val scene = Scene.v().apply { configure(this) }
        configureAfterSceneInit(scene, options)
        Main.v().apply { configure(this) }
    }

    open fun constructSoot(locator: ProjectFileLocator? = null) {
        loadClasses(Scene.v(), locator)
    }

    open fun releaseCallGraph(scene: Scene, g: G) {
        scene.releaseCallGraph()
        scene.releasePointsToAnalysis()
        scene.releaseReachableMethods()
        g.resetSpark()
    }

    open fun releaseCallGraph() {
        releaseCallGraph(Scene.v(), G.v())
    }

    open fun onBeforeCallGraphConstruction() {
        mainConfig.getSaConfig()?.sootConfig?.onBeforeCallGraphConstruction(Scene.v(), Options.v())
    }

    open fun onAfterCallGraphConstruction() {
        mainConfig.getSaConfig()?.sootConfig?.onAfterCallGraphConstruction(callGraph, Scene.v(), Options.v())

        LoggingKt.info(logger).run {
            val startTime = LocalDateTime.now()
            var alreadyLogged = false
            val res = ObjectRef<Maybe<*>>().apply { element = Maybe.empty() }

            try {
                res.element = Maybe(Unit)
                if (res.element.hasValue) {
                    logMethod.invoke { "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}: Rewrite soot scene " }
                } else {
                    logMethod.invoke { "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}: Rewrite soot scene <Nothing>" }
                }
            } catch (t: Throwable) {
                logMethod.invoke { "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}: Rewrite soot scene :: EXCEPTION :: " }
                alreadyLogged = true
                throw t
            } finally {
                if (!alreadyLogged) {
                    if (res.element.hasValue) {
                        logMethod.invoke { "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}: Rewrite soot scene " }
                    } else {
                        logMethod.invoke { "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}: Rewrite soot scene <Nothing>" }
                    }
                }
            }
        }
    }

    override fun onBeforeCallGraphConstruction(scene: Scene, options: Options) {
        ISootInitializeHandler.DefaultImpls.onBeforeCallGraphConstruction(this, scene, options)
    }

    override fun onAfterCallGraphConstruction(cg: CallGraph, scene: Scene, options: Options) {
        ISootInitializeHandler.DefaultImpls.onAfterCallGraphConstruction(this, cg, scene, options)
    }

    companion object {
        private val logger: KLogger = TODO("Initialize logger")

        val instance_soot_Scene: Scene?
            get() = Singletons::class.java.getDeclaredField("instance_soot_Scene").apply {
                isAccessible = true
            }.get(G.v()) as? Scene

        fun restAll() {
            G.reset()
            PTAScene.reset()
        }

        private fun getExcludedPackages(scene: Scene): LinkedList<String> {
            TODO("Implement getExcludedPackages")
        }

        private fun getClassPathInfo(scene: Scene): String {
            TODO("Implement getClassPathInfo")
        }

        private fun getProcessDirInfo(): String {
            TODO("Implement getProcessDirInfo")
        }
    }
}

/**
 * Reflection-based extension property that exposes Scene’s private
 * `excludedPackages` field as a mutable [LinkedList] of package names.
 */
val Scene.excludedPackages: LinkedList<String>
    get() {
        val field: Field = Scene::class.java.getDeclaredField("excludedPackages").apply {
            isAccessible = true
        }
        return field.get(this) as LinkedList<String>
    }

/**
 * Convenience extension to obtain a [SootCtx] bound to this [Scene].
 * (Assumes `SootCtx` has an appropriate constructor.)
 */
fun Scene.sootCtx(cfg: MainConfig): SootCtx =
    SootCtx(cfg)