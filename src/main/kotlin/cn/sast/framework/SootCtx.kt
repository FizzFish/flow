package cn.sast.framework

import cn.sast.api.config.ExtSettings
import cn.sast.api.config.MainConfig
import cn.sast.api.config.ScanFilter
import cn.sast.api.config.SrcPrecedence
import cn.sast.api.report.ClassResInfo
import cn.sast.api.util.IMonitor
import cn.sast.api.util.Timer
import cn.sast.api.util.isSyntheticComponent
import cn.sast.common.IResFile
import cn.sast.common.Resource
import cn.sast.framework.compiler.EcjCompiler
import cn.sast.framework.graph.CGUtils
import cn.sast.framework.incremental.IncrementalAnalyzeImplByChangeFiles
import cn.sast.framework.report.NullWrapperFileGenerator
import cn.sast.framework.report.ProjectFileLocator
import cn.sast.framework.rewrite.LibraryClassPatcher
import cn.sast.framework.rewrite.StringConcatRewriterTransform
import cn.sast.idfa.analysis.ProcessInfoView
import com.feysh.corax.config.api.ISootInitializeHandler
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.rendering.Theme
import driver.PTAFactory
import driver.PTAPattern
import mu.KLogger
import org.utbot.common.Maybe
import org.utbot.common.elapsedSecFrom
import qilin.CoreConfig
import qilin.core.PTAScene
import qilin.core.pag.ValNode
import qilin.pta.PTAConfig
import qilin.util.PTAUtils
import soot.*
import soot.jimple.infoflow.AbstractInfoflow
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import soot.jimple.toolkits.pointer.DumbPointerAnalysis
import soot.options.Options
import soot.util.Chain
import java.io.File
import java.lang.reflect.Field
import java.nio.file.InvalidPathException
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.collections.immutable.toPersistentSet


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
            mainConfig.monitor,
            mainConfig.autoAppClasses,
            null,
            mainConfig.autoAppTraverseMode,
            false,
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
                ctx.mainConfig.tryGetRelativePath(a).relativePath,
                ctx.mainConfig.tryGetRelativePath(b).relativePath
            )
        }
    }

    private object StringComparator : Comparator<String> {
        override fun compare(a: String, b: String): Int = a.compareTo(b)
    }

    init {
        this.mainConfig = mainConfig
        this.monitor = mainConfig.monitor
        this._loadClassesTimer = monitor?.timer("loadClasses")
        this._classesClassificationTimer = monitor?.timer("classes.classification")
        this._cgConstructTimer = monitor?.timer("callgraph.construct")
    }

    open fun configureCallGraph(options: Options) {
        cgAlgorithmProvider = configureCallGraph(
            options,
            mainConfig.callGraphAlgorithm,
            mainConfig.appOnly,
            mainConfig.enableReflection
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

        if (!mainConfig.skipClass) {
            val applicationClasses = scene.applicationClasses
            if (applicationClasses.isEmpty() || applicationClasses.none { !it.isSyntheticComponent }) {
                throw IllegalStateException(
                    "application classes must not be empty. check your --auto-app-classes, --process, --source-path, --class-path options"
                )
            }
        }

        scene.getOrMakeFastHierarchy()
        val timerSnapshot = _cgConstructTimer?.start()
//        logger.info { "Constructing the call graph [$cgAlgorithm] ..." }

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
                                    printAliasInfo = ExtSettings.printAliasInfo
                                    castNeverFailsOfPhantomClass = ExtSettings.castNeverFailsOfPhantomClass
                                }
                                ValNode.UseRoaringPointsToSet = ExtSettings.useRoaringPointsToSet
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
                                printAliasInfo = ExtSettings.printAliasInfo
                                castNeverFailsOfPhantomClass = ExtSettings.castNeverFailsOfPhantomClass
                            }
                            ValNode.UseRoaringPointsToSet = ExtSettings.useRoaringPointsToSet
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
                logger.info { "After build CG: Process information: ${ProcessInfoView.globalProcessInfo.processInfoText}" }
            }
        } catch (t: Throwable) {
            logger.info { "Finished (in ${elapsedSecFrom(startTime)}): Constructing the call graph [$cgAlgorithm] ... :: EXCEPTION :: " }
            total = true
            throw t
        } finally {
            if (!total) {
                if (res.element.hasValue) {
                    logger.info { "Finished (in ${elapsedSecFrom(startTime)}): Constructing the call graph [$cgAlgorithm] ... " }
                } else {
                    logger.info { "Finished (in ${elapsedSecFrom(startTime)}): Constructing the call graph [$cgAlgorithm] ... <Nothing>" }
                }
            }
        }

        if (timerSnapshot != null) {
            _cgConstructTimer?.stop(timerSnapshot)
        }
        CGUtils.addCallEdgeForPhantomMethods()
        showPta()
        scene.releaseReachableMethods()
        CGUtils.fixScene(scene)

        if (record) {
            val (appMethodsWithBody, appMethodsTotal) = scene.applicationClasses.activeBodyMethods()
            monitor?.projectMetrics?.apply {
                this.applicationMethodsHaveBody = appMethodsWithBody
                this.applicationMethods = appMethodsTotal
            }

            val (libMethodsWithBody, libMethodsTotal) = scene.applicationClasses.activeBodyMethods()
            monitor?.projectMetrics?.apply {
                this.libraryClasses = libMethodsWithBody
                this. libraryMethods = libMethodsTotal
            }
        }

        onAfterCallGraphConstruction()
    }

    open fun constructCallGraph() {
        constructCallGraph(cgAlgorithmProvider, mainConfig.appOnly, false)
    }

    fun showPta() {
        val pta = Scene.v().pointsToAnalysis
        if (pta is DumbPointerAnalysis) {
            logger.warn { "PointsToAnalysis of scene is DumbPointerAnalysis!!!" }
            Scene.v().pointsToAnalysis = pta
        }

        logger.info { "PointsToAnalysis of scene is ${pta.javaClass.simpleName}" }
        logger.info { "Call Graph has been constructed with ${Scene.v().callGraph.size()} edges." }
        showClasses(Scene.v(), "After PTA: ") { Theme.Default.info(it) }
    }

    suspend fun findClassesInnerJar(locator: ProjectFileLocator): Map<String, MutableSet<IResFile>> {
        return coroutineScope {
            val md5Map = ConcurrentHashMap<String, IResFile>()
            val md5Group = ConcurrentHashMap<String, MutableSet<IResFile>>()

            locator.getByFileExtension("class").forEach { clz ->
                if (clz.isJarScheme) {
                    try {
                        val jar = Resource.fileOf(clz.schemePath)
                        launch {
                            val md5 = jar.md5
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
        require(mainConfig.autoAppClasses.isNotEmpty()) { "Check failed." }

        val result = findClassesInnerJar(autoAppClassesLocator)
        val jars = result.values.map { files ->
            files.minByOrNull { mainConfig.tryGetRelativePath(it)) }!!
        }.sortedWith(JarRelativePathComparator(this))

        return jars.mapNotNull { jar ->
            when {
                jar.isFileScheme -> jar
                jar.isJarScheme -> try {
                    jar.expandRes(mainConfig.output_dir).toFile()
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
        if (mainConfig.src_precedence == SrcPrecedence.prec_java && !mainConfig.isAndroidScene) {
            val autoAppClasses = mainConfig.output_dir.resolve("gen-classes").toDirectory().apply {
                deleteDirectoryRecursively()
                mkdirs()
            }

            val compiler = EcjCompiler(
                mainConfig.processDir,
                mainConfig.classpath,
                autoAppClasses,
                mainConfig.ecj_options,
                mainConfig.useDefaultJavaClassPath,
            )

            if (!compiler.compile()) {
                logger.error { "\n\n!!! There are some errors in source code compilation !!!\n\n" }
            }

            require(autoAppClasses.listPathEntries().isNotEmpty()) {
                "\n\n!!! no class file found under $autoAppClasses !!!\n\n"
            }

            mainConfig.processDir.add(autoAppClasses)
            mainConfig.classpath = (compiler.collectedClasspath).toPersistentSet()
        }

        autoAppClassesLocator.update()
        if (mainConfig.autoAppClasses.isNotEmpty() && !mainConfig.isAndroidScene && !mainConfig.skipClass) {
            runBlocking {
                mainConfig.processDir.addAll(findClassesInnerJarUnderAutoAppClassPath())
            }
        }

        options.apply {
            set_verbose(true)
            set_allow_phantom_elms(true)
            set_whole_program(mainConfig.whole_program)
            set_src_prec(mainConfig.src_precedence.sootFlag)
            set_prepend_classpath(mainConfig.prepend_classpath)
            set_no_bodies_for_excluded(mainConfig.no_bodies_for_excluded)
            set_include_all(true)
            set_allow_phantom_refs(mainConfig.allow_phantom_refs)
            set_ignore_classpath_errors(false)
            set_throw_analysis(mainConfig.throw_analysis)
            set_process_multiple_dex(mainConfig.process_multiple_dex)
            set_field_type_mismatches(2)
            set_full_resolver(true)
            classes().addAll(mainConfig.appClasses)
            set_app(false)
            set_search_dex_in_archives(true)
            set_process_dir(mainConfig.getSoot_process_dir().toList())
            set_output_format(mainConfig.output_format)
            mainConfig.output_dir.apply {
                deleteDirectoryRecursively()
                mkdirs()
            }
            set_output_dir(mainConfig.output_dir.absolutePath)
            set_keep_offset(false)
            set_keep_line_number(mainConfig.enableLineNumbers)
            set_ignore_resolution_errors(true)

            if (mainConfig.forceAndroidJar == true) {
                set_force_android_jar(mainConfig.androidPlatformDir)
            } else {
                set_android_jars(mainConfig.androidPlatformDir)
            }
            logger.info { "android platform dir: ${mainConfig.androidPlatformDir}" }

            if (mainConfig.isAndroidScene) {
                set_throw_analysis(3)
            }

            if (mainConfig.enableOriginalNames) {
                setPhaseOption("jb", "use-original-names:true")
            }

            set_debug(false)
            set_verbose(false)
            set_validate(false)
        }

        configureCallGraph(options)
        PackManager.v().getPack("jb").add(Transform("jb.rewriter", StringConcatRewriterTransform()))
        mainConfig.saConfig?.sootConfig?.configure(options)
    }

    fun configureSootClassPath(options: Options) {
        val cp = mainConfig.sootClasspath().toSortedSet()
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
        mainConfig.saConfig?.sootConfig?.configure(scene)
    }

    override fun configure(main: Main) {
        main.autoSetOptions()
        mainConfig.saConfig?.sootConfig?.configure(main)
    }

    fun classesClassification(scene: Scene, locator: ProjectFileLocator?) {
        val timerSnapshot = _classesClassificationTimer?.start()
        require(!mainConfig.skipClass) { "Check failed." }

        var findAny = false
        val autoAppClasses = mainConfig.autoAppClasses
        showClasses(scene, "Before classes classification: ")

        scene.classes.forEach { sc ->
            if (!sc.isPhantom) {
                val sourceFile = autoAppClassesLocator.get(ClassResInfo.of(sc), NullWrapperFileGenerator)
                val origAction = when {
                    autoAppClasses.isNotEmpty() -> when {
                        sourceFile == null || (!mainConfig.autoAppSrcInZipScheme && !sourceFile.isFileScheme) -> {
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
                val origStr: String = if (locator?.get(ClassResInfo.of(sc), NullWrapperFileGenerator) != null) {
                    "(src exists) $action"
                } else {
                    "(src not exists) $action"
                }
                val orig = mainConfig.scanFilter.getActionOf(origStr, sc, null)
                when (orig){
                    ScanAction.Process -> sc.setApplicationClass()
                    ScanAction.Skip -> sc.setLibraryClass()
                    ScanAction.Keep -> {}
                }
            }
        }

        if (timerSnapshot != null) {
            _classesClassificationTimer?.stop(timerSnapshot)
        }
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
        val active = total.filter { it.hasActiveBody() }
        return active.size to total.size
    }

    fun Chain<SootClass>.show(): String {
        val (active, total) = activeBodyMethods()
        return if (total == 0) "empty" else "$size($total*${"%.2f".format(active.toFloat() / total)})"
    }

    fun showClasses(
        scene: Scene,
        prefix: String = "",
        style: TextStyle
    ) = showClasses(scene, prefix) { msg -> style(msg) }

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
        G.v().set_SourceLocator(SourceLocatorPlus(mainConfig))

        when (cgAlgorithmProvider) {
            CgAlgorithmProvider.QiLin -> {
                Scene.v().addBasicClass("java.lang.ClassLoader", 3)
                Scene.v().addBasicClass("java.lang.ref.Finalizer", 3)
                PTAScene.v().addBasicClasses()
            }

            CgAlgorithmProvider.Soot -> {}
        }

        if (!this.mainConfig.skipClass) {

            val msg = "Loading Necessary Classes..."
            logger.info() { "Started: $msg" }

            val startTime = LocalDateTime.now()
            var result: Maybe<Unit> = Maybe.empty()

            try {
                _loadClassesTimer?.let { timer ->
                    val snapshot = timer.start()
                    try {
                        scene.loadNecessaryClasses(false)
                    } finally {
                        timer.stop(snapshot)
                    }
                } ?: run {
                    scene.loadNecessaryClasses(false)
                }

                monitor?.timer("classesClassification")?.let { timer ->
                    val snapshot = timer.start()
                    try {
                        classesClassification(scene, locator)
                    } finally {
                        timer.stop(snapshot)
                    }
                } ?: run {
                    classesClassification(scene, locator)
                }

                _loadClassesTimer?.let { timer ->
                    val snapshot = timer.start()
                    try {
                        for (appClass in scene.applicationClasses) {
                            if (!appClass.isPhantom) {
                                scene.loadClass(appClass.name, 3)
                            }
                        }
                    } finally {
                        timer.stop(snapshot)
                    }
                } ?: run {
                    for (appClass in scene.applicationClasses) {
                        if (!appClass.isPhantom) {
                            scene.loadClass(appClass.name, 3)
                        }
                    }
                }

                logger.info { "Classes loaded." }
                result = Maybe(Unit)
                result.getOrThrow()

                logger.info() {
                    val elapsed = elapsedSecFrom(startTime)
                    "Finished (in $elapsed): $msg"
                }

                showClasses(scene, style = Theme.Default.warning)

            } catch (t: Throwable) {
                logger.info() {
                    val elapsed = elapsedSecFrom(startTime)
                    "Finished (in $elapsed): $msg :: EXCEPTION ::"
                }
                throw t
            }

        } else {
            scene.loadBasicClasses()
            scene.loadDynamicClasses()
            scene.doneResolving()
            CGUtils.createDummyMain(scene)
        }

        logger.info { "After Loading Classes: ${ProcessInfoView.globalProcessInfo.processInfoText}" }
        CGUtils.removeLargeClasses(scene)
        CGUtils.makeSpuriousMethodFromInvokeExpr()
        PackManager.v().getPack("wjpp").apply()
        LibraryClassPatcher().patchLibraries()

        monitor?.projectMetrics?.apply {
            applicationClasses = (scene.applicationClasses.size)
            libraryClasses = (scene.libraryClasses.size)
            phantomClasses = (scene.phantomClasses.size)
        }

        (mainConfig.incrementAnalyze as? IncrementalAnalyzeImplByChangeFiles)?.update(scene, locator)
        logger.info { "After Rewrite Classes: ${ProcessInfoView.globalProcessInfo.processInfoText}" }
    }

    open fun configureSoot() {
        Companion.restAll()
        mainConfig.validate()
        val options = Options.v().apply {
            configure(this)
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
        mainConfig.saConfig?.sootConfig?.onBeforeCallGraphConstruction(Scene.v(), Options.v())
    }

    open fun onAfterCallGraphConstruction() {
        mainConfig.saConfig?.sootConfig?.onAfterCallGraphConstruction(callGraph, Scene.v(), Options.v())

        logger.info {
            val startTime = LocalDateTime.now()
            var alreadyLogged = false
            val res = ObjectRef<Maybe<*>>().apply { element = Maybe.empty() }

            try {
                res.element = Maybe(Unit)
                if (res.element.hasValue) {
                    logger.info { "Finished (in ${elapsedSecFrom(startTime)}: Rewrite soot scene " }
                } else {
                    logger.info { "Finished (in ${elapsedSecFrom(startTime)}: Rewrite soot scene <Nothing>" }
                }
            } catch (t: Throwable) {
                logger.info { "Finished (in ${elapsedSecFrom(startTime)}: Rewrite soot scene :: EXCEPTION :: " }
                alreadyLogged = true
                throw t
            } finally {
                if (!alreadyLogged) {
                    if (res.element.hasValue) {
                        logger.info { "Finished (in ${elapsedSecFrom(startTime)}: Rewrite soot scene " }
                    } else {
                        logger.info { "Finished (in ${elapsedSecFrom(startTime)}: Rewrite soot scene <Nothing>" }
                    }
                }
            }
        }
    }

    override fun onBeforeCallGraphConstruction(scene: Scene, options: Options) {
        mainConfig.saConfig?.sootConfig?.onBeforeCallGraphConstruction( scene, options)
    }

    override fun onAfterCallGraphConstruction(cg: CallGraph, scene: Scene, options: Options) {
        mainConfig.saConfig?.sootConfig?.onAfterCallGraphConstruction(cg, scene, options)
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