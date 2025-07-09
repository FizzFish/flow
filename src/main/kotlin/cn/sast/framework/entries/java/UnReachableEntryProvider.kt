package cn.sast.framework.entries.java

import cn.sast.dataflow.callgraph.TargetReachableMethods
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mu.KLogger
import mu.KotlinLogging
import soot.EntryPoints
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.jimple.toolkits.callgraph.Sources
import java.util.concurrent.atomic.AtomicInteger
import soot.MethodOrMethodContext

class UnReachableEntryProvider(
    private val ctx: SootCtx,
    val exclude: MutableSet<String> = mutableSetOf()
) : IEntryPointProvider {

    private val logger: KLogger = KotlinLogging.logger {}

    override val iterator: Flow<IEntryPointProvider.AnalyzeTask> = flow {
        val methods = getEntryMethods().filter { it.signature !in exclude }.toSet()
        emit(object : IEntryPointProvider.AnalyzeTask {
            override val entries: Set<SootMethod> = methods
            override val components: Set<SootClass> = emptySet()
            override val name = "UnReachableEntryProvider(entries=${methods.size})"
            override val methodsMustAnalyze: Set<SootMethod>
                get() = super.methodsMustAnalyze.filter { it.signature !in exclude }.toSet()

            override fun needConstructCallGraph(sootCtx: SootCtx) {}
        })
    }

    fun getEntryMethods(): Set<SootMethod> {
        val scene = Scene.v()
        val reachMethods = scene.applicationClasses
            .flatMap { if (it.isInScene) it.methods else emptyList() }
            .filter { !scene.isExcluded(it.declaringClass.name) || scene.isIncluded(it.declaringClass.name) }
        logger.info { "reach methods num: ${reachMethods.size}" }
        return Companion.getEntryPoints(ctx, reachMethods)
    }

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
        fun getEntryPoints(ctx: SootCtx, methodsToFind: List<SootMethod>): MutableSet<SootMethod> {
            ctx.releaseCallGraph()
            logger.info { "auto make the entry points by UnReachableMethodsFinder." }
            logger.info { "reach methods num: ${methodsToFind.size}" }

            val scene = Scene.v()
            scene.entryPoints = methodsToFind
            ctx.constructCallGraph()
            val cg = ctx.sootMethodCallGraph
            val reachable = TargetReachableMethods(cg, methodsToFind)
            reachable.update()

            val res = mutableSetOf<SootMethod>()
            val counter = AtomicInteger()
            for (cur in reachable.listener()) {
                if (!Sources(cg.edgesInto(cur)).hasNext() && cur.method().isConcrete) {
                    res += cur.method()
                }
                counter.incrementAndGet()
            }

            res += EntryPoints.v().all()
            logger.info { "unreachable entry methods num :${res.size}" }
            if (res.isEmpty()) logger.warn { "no entry points" }
            return res
        }
    }
}