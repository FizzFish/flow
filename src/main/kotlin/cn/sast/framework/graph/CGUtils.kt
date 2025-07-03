package cn.sast.framework.graph

import cn.sast.api.config.ExtSettings
import cn.sast.api.report.Counter
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import com.github.ajalt.mordant.rendering.Theme
import java.nio.file.Files
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mu.KLogger
import mu.KotlinLogging
import soot.Body
import soot.MethodOrMethodContext
import soot.PackManager
import soot.Scene
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.Type
import soot.UnitPatchingChain
import soot.VoidType
import soot.jimple.DynamicInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.Jimple
import soot.jimple.JimpleBody
import soot.jimple.Stmt
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import soot.jimple.toolkits.callgraph.ReachableMethods
import soot.util.Chain
import soot.util.queue.QueueReader

@SourceDebugExtension(["SMAP\nCGUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CGUtils.kt\ncn/sast/framework/graph/CGUtils\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,223:1\n1#2:224\n*E\n"])
public object CGUtils {
    public val missClasses: Counter<SootClass> = Counter()
    private val logger: KLogger = KotlinLogging.logger {}

    public fun rewriteJimpleBodyAfterCG() {
        val all = Scene.v().classes.snapshotIterator()
        BuildersKt.runBlocking(
            Dispatchers.Default,
            object : Function2<CoroutineScope, Continuation<Unit>, Any?> {
                private val $all = all
                var label = 0

                override fun invokeSuspend(result: Any?): Any? {
                    when (label) {
                        0 -> {
                            ResultKt.throwOnFailure(result)
                            val $this$runBlocking = this.L$0 as CoroutineScope
                            val sc = $all
                            val var3x = sc

                            while (var3x.hasNext()) {
                                val var8 = var3x.next() as SootClass
                                if (!var8.isPhantom) {
                                    val var6 = var8.methods

                                    for (sm in var6) {
                                        if (sm.hasActiveBody()) {
                                            BuildersKt.launch(
                                                $this$runBlocking,
                                                null,
                                                null,
                                                object : Function2<CoroutineScope, Continuation<Unit>, Any?> {
                                                    private val $sm = sm
                                                    var label = 0

                                                    override fun invokeSuspend(result: Any?): Any? {
                                                        when (label) {
                                                            0 -> {
                                                                ResultKt.throwOnFailure(result)
                                                                if (!$sm.hasActiveBody()) {
                                                                    return Unit
                                                                } else {
                                                                    try {
                                                                        val body = $sm.activeBody
                                                                        if (body == null) {
                                                                            return Unit
                                                                        }

                                                                        PackManager.v().getTransform("jb.rewriter").apply(body)
                                                                        PackManager.v().getTransform("jb.identityStmt2MethodParamRegion").apply(body)
                                                                        $sm.activeBody = body
                                                                    } catch (_: RuntimeException) {
                                                                    }

                                                                    return Unit
                                                                }
                                                            }
                                                            else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                                                        }
                                                    }

                                                    override fun create(value: Any?, completion: Continuation<*>): Continuation<Unit> {
                                                        return object : Function2<CoroutineScope, Continuation<Unit>, Any?> {
                                                            private val $sm = this@Function2.$sm
                                                            var label = 0

                                                            override fun invokeSuspend(result: Any?): Any? {
                                                                return this@Function2.invokeSuspend(result)
                                                            }

                                                            override fun create(value: Any?, completion: Continuation<*>): Continuation<Unit> {
                                                                return this
                                                            }

                                                            override fun invoke(p1: CoroutineScope, p2: Continuation<Unit>): Any? {
                                                                return invokeSuspend(Unit)
                                                            }
                                                        }
                                                    }

                                                    override fun invoke(p1: CoroutineScope, p2: Continuation<Unit>): Any? {
                                                        return invokeSuspend(Unit)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            return Unit
                        }
                        else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                    }
                }

                override fun create(value: Any?, completion: Continuation<*>): Continuation<Unit> {
                    val function = object : Function2<CoroutineScope, Continuation<Unit>, Any?> {
                        private val $all = this@Function2.$all
                        var label = 0

                        override fun invokeSuspend(result: Any?): Any? {
                            return this@Function2.invokeSuspend(result)
                        }

                        override fun create(value: Any?, completion: Continuation<*>): Continuation<Unit> {
                            return this
                        }

                        override fun invoke(p1: CoroutineScope, p2: Continuation<Unit>): Any? {
                            return invokeSuspend(Unit)
                        }
                    }
                    function.L$0 = value
                    return function
                }

                override fun invoke(p1: CoroutineScope, p2: Continuation<Unit>): Any? {
                    return invokeSuspend(Unit)
                }
            }
        )
    }

    public fun makeSpuriousMethodFromInvokeExpr() {
        val classes = Scene.v().applicationClasses

        for (appSc in classes) {
            if (!appSc.isPhantom) {
                val methods = appSc.methods

                for (sm in methods) {
                    if (sm.isConcrete && sm.source != null) {
                        try {
                            val body = sm.retrieveActiveBody()
                            if (body != null) {
                                val units = body.units
                                val iterator = units.iterator()

                                while (iterator.hasNext()) {
                                    val u = iterator.next()
                                    val stmt = u as? Stmt
                                    if (stmt != null) {
                                        if (stmt.containsInvokeExpr()) {
                                            val method = stmt.invokeExpr.method
                                        }

                                        if (stmt.containsFieldRef()) {
                                            val field = stmt.fieldRef.field
                                        }
                                    }
                                }
                            }
                        } catch (_: RuntimeException) {
                        }
                    }
                }
            }
        }
    }

    private fun CallGraph.forceAddCgEdge(src: SootMethod, srcUnit: Stmt, ie: InvokeExpr) {
        val tgt = ie.method
        if (srcUnit.invokeExpr !is DynamicInvokeExpr) {
            this.addEdge(Edge(src as MethodOrMethodContext, srcUnit, tgt as MethodOrMethodContext))
        }
    }

    public fun addCallEdgeForPhantomMethods() {
        val scene = Scene.v()
        val cg = scene.callGraph
        val reachableMethods = scene.reachableMethods
        reachableMethods.update()
        val listener = reachableMethods.listener() as Iterator<*>

        while (listener.hasNext()) {
            val src = (listener.next() as MethodOrMethodContext).method()
            if (src.hasActiveBody()) {
                val units = src.activeBody.units
                val iterator = units.iterator()

                while (iterator.hasNext()) {
                    val u = iterator.next()
                    val srcUnit = u as Stmt
                    if (u.containsInvokeExpr()) {
                        val ie = srcUnit.invokeExpr
                        if (!cg.edgesOutOf(u).hasNext()) {
                            val declaringClass = ie.methodRef.declaringClass
                            if (declaringClass != null) {
                                if (declaringClass.isPhantom && !scene.isExcluded(declaringClass)) {
                                    val className = declaringClass.name
                                    if (!className.startsWith("soot.dummy")) {
                                        missClasses.count(declaringClass)
                                    }
                                }
                            }

                            forceAddCgEdge(cg, src, srcUnit, ie)
                        }
                    }
                }
            }
        }
    }

    public fun flushMissedClasses(outputDir: IResDirectory) {
        val out = outputDir.resolve("phantom_dependence_classes.txt").toFile()
        if (missClasses.isNotEmpty()) {
            logger.warn { flushMissedClasses$lambda$1(out) }
            missClasses.writeResults(out)
        } else {
            Files.deleteIfExists(out.path)
        }
    }

    public fun removeInvalidMethodBody(scene: Scene) {
        val classes = scene.classes.iterator()

        while (classes.hasNext()) {
            for (sm in classes.next().methods) {
                if (sm.hasActiveBody() && sm.activeBody.units.isEmpty()) {
                    sm.activeBody = null
                    sm.setPhantom(true)
                }
            }
        }
    }

    public fun fixInvalidInterface(scene: Scene) {
        val classes = scene.classes.iterator()

        while (classes.hasNext()) {
            val sc = classes.next() as SootClass
            val interfaces = sc.interfaces.snapshotIterator()

            while (interfaces.hasNext()) {
                val i = interfaces.next() as SootClass
                if (!i.isInterface) {
                    logger.warn { fixInvalidInterface$lambda$2(i, sc) }

                    try {
                        sc.removeInterface(i)
                    } catch (e: Exception) {
                        logger.warn(e) { fixInvalidInterface$lambda$3(i, sc) }
                    }
                }
            }
        }
    }

    public fun removeLargeClasses(scene: Scene) {
        val skipClassByMaximumMethods = ExtSettings.INSTANCE.skip_large_class_by_maximum_methods
        val skipClassByMaximumFields = ExtSettings.INSTANCE.skip_large_class_by_maximum_fields
        if (skipClassByMaximumMethods > 0 || skipClassByMaximumFields > 0) {
            val classes = scene.classes.snapshotIterator()

            while (classes.hasNext()) {
                val sc = classes.next() as SootClass
                var removeIt = false
                if (skipClassByMaximumMethods > 0 && sc.methodCount > skipClassByMaximumMethods) {
                    removeIt = true
                    logger.warn { removeLargeClasses$lambda$4(sc, skipClassByMaximumMethods) }
                }

                if (skipClassByMaximumFields > 0 && sc.fieldCount > skipClassByMaximumFields) {
                    removeIt = true
                    logger.warn { removeLargeClasses$lambda$5(sc, skipClassByMaximumFields) }
                }

                if (removeIt) {
                    scene.removeClass(sc)
                }
            }
        }
    }

    public fun fixScene(scene: Scene) {
        removeInvalidMethodBody(scene)
        fixInvalidInterface(scene)
    }

    public fun createSootMethod(
        name: String,
        argsTypes: List<Type>,
        returnType: Type,
        declaringClass: SootClass,
        graphBody: JimpleBody,
        isStatic: Boolean = true
    ): SootMethod {
        val method = SootMethod(name, argsTypes, returnType, if (isStatic) 8 else 0)
        declaringClass.addMethod(method)
        method.activeBody = graphBody
        return method
    }

    public fun getOrCreateClass(scene: Scene, className: String): SootClass {
        var mainClass = scene.getSootClassUnsafe(className, false)
        if (mainClass == null) {
            mainClass = scene.makeSootClass(className).apply {
                resolvingLevel = 3
            }
            scene.addClass(mainClass)
        }

        return mainClass
    }

    public fun createDummyMain(
        scene: Scene,
        dummyClassName: String = "dummyMainClass",
        methodName: String = "fakeMethod"
    ): SootClass {
        val jimple = Jimple.v()
        val dummyClass = getOrCreateClass(scene, dummyClassName)
        dummyClass.setApplicationClass()
        val body = jimple.newBody()
        body.units.add(jimple.newNopStmt())
        createSootMethod(methodName, emptyList(), VoidType.v(), dummyClass, body, false)
        return dummyClass
    }

    @JvmStatic
    fun flushMissedClasses$lambda$1(out: IResFile): Any {
        return Theme.Default.warning(
            "Incomplete analysis! The num of ${missClasses.size()} dependent classes cannot be found here. check: ${out.absolute.normalize}"
        )
    }

    @JvmStatic
    fun fixInvalidInterface$lambda$2(i: SootClass, sc: SootClass): Any {
        return "$i is not a interface. but contains in interfaces of $sc"
    }

    @JvmStatic
    fun fixInvalidInterface$lambda$3(i: SootClass, sc: SootClass): Any {
        return "remove interface $i from $sc failed"
    }

    @JvmStatic
    fun removeLargeClasses$lambda$4(sc: SootClass, skipClassByMaximumMethods: Int): Any {
        return "Remove large class: $sc which is too large. Limit the class methods count should less than $skipClassByMaximumMethods"
    }

    @JvmStatic
    fun removeLargeClasses$lambda$5(sc: SootClass, skipClassByMaximumFields: Int): Any {
        return "Remove big class: $sc which is too large. Limit the class fields count should less than $skipClassByMaximumFields"
    }

    @JvmStatic
    fun logger$lambda$8() {
    }
}