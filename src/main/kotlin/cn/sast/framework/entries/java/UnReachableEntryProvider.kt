package cn.sast.framework.entries.java

import cn.sast.dataflow.callgraph.TargetReachableMethods
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import mu.KLogger
import soot.EntryPoints
import soot.MethodOrMethodContext
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Sources
import soot.util.Chain
import soot.util.queue.QueueReader

@SourceDebugExtension(["SMAP\nUnReachableEntryProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UnReachableEntryProvider.kt\ncn/sast/framework/entries/java/UnReachableEntryProvider\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,79:1\n1368#2:80\n1454#2,5:81\n774#2:86\n865#2,2:87\n*S KotlinDebug\n*F\n+ 1 UnReachableEntryProvider.kt\ncn/sast/framework/entries/java/UnReachableEntryProvider\n*L\n59#1:80\n59#1:81,5\n59#1:86\n59#1:87,2\n*E\n"])
open class UnReachableEntryProvider(
    private val ctx: SootCtx,
    val exclude: MutableSet<String> = LinkedHashSet()
) : IEntryPointProvider {
    override val iterator: Flow<AnalyzeTask> = FlowKt.flow(Function2<FlowCollector<AnalyzeTask>, Continuation<Unit>, Any?> { flowCollector, continuation ->
        object : Continuation<Unit> {
            var label = 0
            var L$0: Any? = null

            override val context = continuation.context

            override fun resumeWith(result: Result<Unit>) {
                when (label) {
                    0 -> {
                        ResultKt.throwOnFailure(result)
                        L$0 = flowCollector
                        val entryMethods = this@UnReachableEntryProvider.getEntryMethods()
                        val destination = LinkedHashSet<SootMethod>()
                        for (element in entryMethods) {
                            if (!exclude.contains(element.signature)) {
                                destination.add(element)
                            }
                        }
                        val methods = destination
                        val task = object : AnalyzeTask {
                            private val entries = methods
                            private val components = emptySet<SootClass>()
                            private val name = "(entries size: ${methods.size})"

                            override fun getEntries(): Set<SootMethod> = entries
                            override fun getMethodsMustAnalyze(): Set<SootMethod> {
                                val defaultMethods = AnalyzeTask.DefaultImpls.getMethodsMustAnalyze(this)
                                val result = LinkedHashSet<SootMethod>()
                                for (method in defaultMethods) {
                                    if (!this@UnReachableEntryProvider.exclude.contains(method.signature)) {
                                        result.add(method)
                                    }
                                }
                                return result
                            }
                            override fun getComponents(): Set<SootClass> = components
                            override fun getName(): String = name
                            override fun needConstructCallGraph(sootCtx: SootCtx) {}
                            override fun getAdditionalEntries(): Set<SootMethod> = 
                                AnalyzeTask.DefaultImpls.getAdditionalEntries(this)
                        }
                        label = 1
                        if (flowCollector.emit(task, this) == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                            return
                        }
                    }
                    1 -> {
                        ResultKt.throwOnFailure(result)
                    }
                    else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                }
                continuation.resumeWith(Result.success(Unit))
            }
        }
    })

    fun getEntryMethods(): Set<SootMethod> {
        val scene = Scene.v()
        val reachClasses = scene.applicationClasses
        logger.info { "reach classes num: ${reachClasses.size}" }
        val methods = ArrayList<SootMethod>().apply {
            for (clazz in reachClasses) {
                addAll(clazz.methods)
            }
        }
        val filteredMethods = ArrayList<SootMethod>().apply {
            for (method in methods) {
                val className = method.declaringClass.name
                if (!scene.isExcluded(className) || scene.isIncluded(className)) {
                    add(method)
                }
            }
        }
        return Companion.getEntryPoints(ctx, filteredMethods)
    }

    override fun startAnalyse() {
        IEntryPointProvider.DefaultImpls.startAnalyse(this)
    }

    override fun endAnalyse() {
        IEntryPointProvider.DefaultImpls.endAnalyse(this)
    }

    companion object {
        private val logger: KLogger = TODO("Initialize logger")

        fun getEntryPoints(ctx: SootCtx, methodsToFind: List<SootMethod>): MutableSet<SootMethod> {
            ctx.releaseCallGraph()
            logger.info { "auto make the entry points by UnReachableMethodsFinder." }
            logger.info { "reach methods num: ${methodsToFind.size}" }
            val scene = Scene.v()
            val defaultEntries = EntryPoints.v().all()
            scene.setEntryPoints(methodsToFind)
            ctx.constructCallGraph()
            val cg = ctx.sootMethodCallGraph
            val reachable = TargetReachableMethods(cg, methodsToFind)
            reachable.update()
            val result = LinkedHashSet<SootMethod>()
            val iter = reachable.listener()
            while (iter.hasNext()) {
                val cur = iter.next() as MethodOrMethodContext
                if (!Sources(cg.edgesInto(cur)).hasNext() && cur.method().isConcrete) {
                    result.add(cur.method())
                }
            }
            result.addAll(defaultEntries)
            logger.info { "unreachable entry methods num :${result.size}" }
            if (result.isEmpty()) {
                logger.warn("no entry points")
            }
            return result
        }
    }
}