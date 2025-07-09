package cn.sast.framework.graph

import cn.sast.api.config.ExtSettings
import cn.sast.api.report.Counter
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import kotlinx.coroutines.*
import mu.KotlinLogging
import soot.*
import soot.jimple.DynamicInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.Jimple
import soot.jimple.JimpleBody
import soot.jimple.Stmt
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import soot.jimple.toolkits.callgraph.ReachableMethods
import soot.util.queue.QueueReader
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap

/**
 * Collection of *utility* methods used after Soot call‑graph generation to
 * fix/patch common issues (phantom methods, invalid interfaces, huge classes…).
 */
object CGUtils {

    val missClasses: Counter<SootClass> = Counter()
    private val logger = KotlinLogging.logger {}

    // ---------------------------------------------------------------------
    //  1. Rewrite Jimple bodies so that post‑CG transforms can re‑run safely
    // ---------------------------------------------------------------------
    fun rewriteJimpleBodyAfterCG() = runBlocking(Dispatchers.Default) {
        Scene.v().applicationClasses.parallelStream().forEach { sc ->
            sc.methods.filter { it.hasActiveBody() }.forEach { sm ->
                launch {
                    try {
                        val body = sm.activeBody
                        PackManager.v().getTransform("jb.rewriter").apply(body)
                        PackManager.v().getTransform("jb.identityStmt2MethodParamRegion").apply(body)
                        sm.activeBody = body
                    } catch (ignored: RuntimeException) {
                        // keep calm & carry on – broken bodies will be removed later
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    //  2. Create synthetic target methods/fields referenced via reflection…
    // ---------------------------------------------------------------------
    fun makeSpuriousMethodFromInvokeExpr() {
        Scene.v().applicationClasses.forEach { sc ->
            sc.methods.filter { it.isConcrete && it.source != null }.forEach { sm ->
                try {
                    val body = sm.retrieveActiveBody() ?: return@forEach
                    body.units.forEach { u ->
                        val stmt = u as? Stmt ?: return@forEach
                        if (stmt.containsInvokeExpr()) stmt.invokeExpr.method // touch to ensure resolution
                        if (stmt.containsFieldRef())   stmt.fieldRef.field
                    }
                } catch (_: RuntimeException) {
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    //  3. Add CG edges for phantom invokes so inter‑proc analysis sees them
    // ---------------------------------------------------------------------
    fun addCallEdgeForPhantomMethods() {
        val scene = Scene.v()
        val cg = scene.callGraph
        val reachable = scene.reachableMethods.apply { update() }
        val listener: QueueReader = reachable.listener()
        while (listener.hasNext()) {
            val src = listener.next().method()
            if (!src.hasActiveBody()) continue
            src.activeBody.units.forEach { u ->
                val stmt = u as Stmt
                if (!stmt.containsInvokeExpr()) return@forEach
                val ie = stmt.invokeExpr
                if (!cg.edgesOutOf(stmt).hasNext()) {
                    val tgtClass = ie.methodRef.declaringClass
                    if (tgtClass.isPhantom && !Scene.v().isExcluded(tgtClass) && !tgtClass.name.startsWith("soot.dummy")) {
                        missClasses.count(tgtClass)
                    }
                    cg.addEdge(Edge(src, stmt, ie.method()))
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    //  4. Misc scene cleaners / validators
    // ---------------------------------------------------------------------
    fun flushMissedClasses(outputDir: IResDirectory) {
        val outFile = outputDir.resolve("phantom_dependence_classes.txt").toFile()
        if (missClasses.isNotEmpty()) {
            logger.warn { "Incomplete analysis! ${missClasses.size()} phantom classes collected. See: ${outFile.absolute.normalize()}" }
            missClasses.writeResults(outFile)
        } else {
            Files.deleteIfExists(outFile.path)
        }
    }

    fun removeInvalidMethodBody(scene: Scene) {
        scene.classes.forEach { sc ->
            sc.methods.filter { it.hasActiveBody() && it.activeBody.units.isEmpty }.forEach { sm ->
                sm.setActiveBody(null)
                sm.isPhantom = true
            }
        }
    }

    fun fixInvalidInterface(scene: Scene) {
        scene.classes.forEach { sc ->
            sc.interfaces.snapshotIterator().forEachRemaining { i ->
                if (!i.isInterface) {
                    logger.warn { "$i is not an interface but appears in ${sc.name}" }
                    try { sc.removeInterface(i) } catch (e: Exception) {
                        logger.warn(e) { "remove interface $i from $sc failed" }
                    }
                }
            }
        }
    }

    fun removeLargeClasses(scene: Scene) {
        val maxMethods = ExtSettings.INSTANCE.skip_large_class_by_maximum_methods
        val maxFields  = ExtSettings.INSTANCE.skip_large_class_by_maximum_fields
        if (maxMethods <= 0 && maxFields <= 0) return
        scene.classes.snapshotIterator().forEachRemaining { sc ->
            var remove = false
            if (maxMethods > 0 && sc.methodCount > maxMethods) {
                remove = true; logger.warn { "Remove large class $sc – too many methods ($maxMethods)" }
            }
            if (maxFields > 0 && sc.fieldCount > maxFields) {
                remove = true; logger.warn { "Remove large class $sc – too many fields ($maxFields)" }
            }
            if (remove) scene.removeClass(sc)
        }
    }

    fun fixScene(scene: Scene) {
        removeInvalidMethodBody(scene)
        fixInvalidInterface(scene)
    }

    // ---------------------------------------------------------------------
    //  5. Helper short‑cuts for building synthetic artefacts
    // ---------------------------------------------------------------------
    fun createSootMethod(
        name: String,
        argTypes: List<Type>,
        returnType: Type,
        declaringClass: SootClass,
        body: JimpleBody,
        isStatic: Boolean = true,
    ): SootMethod = SootMethod(name, argTypes, returnType, if (isStatic) Modifier.STATIC else 0).apply {
        declaringClass.addMethod(this)
        activeBody = body
    }

    fun getOrCreateClass(scene: Scene, className: String): SootClass =
        scene.getSootClassUnsafe(className, false) ?: scene.makeSootClass(className).apply {
            resolvingLevel = SootClass.BODIES
            scene.addClass(this)
        }

    fun createDummyMain(
        scene: Scene,
        dummyClassName: String = "dummyMainClass",
        methodName: String = "fakeMethod",
    ): SootClass {
        val j = Jimple.v()
        val dummy = getOrCreateClass(scene, dummyClassName).apply { setApplicationClass() }
        val body = j.newBody().apply { units.add(j.newNopStmt()) }
        createSootMethod(methodName, emptyList(), VoidType.v(), dummy, body, isStatic = false)
        return dummy
    }
}
