package cn.sast.framework.entries.custom

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import cn.sast.framework.entries.component.ComponentEntryProvider
import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import soot.SootClass
import soot.SootMethod

public class HybridCustomThenComponent(ctx: SootCtx, entries: Set<SootMethod>) : CustomEntryProvider(entries) {
    public open val iterator: Flow<AnalyzeTask>

    init {
        this.iterator = FlowKt.flow(Function2 { flowCollector: FlowCollector<AnalyzeTask>, continuation: Continuation<Unit> ->
            object : Continuation<Any?> {
                var label = 0
                val this$0 = this@HybridCustomThenComponent
                val $ctx = ctx
                val $entries = entries

                override fun invokeSuspend(result: Any?): Any? {
                    val suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED()
                    when (label) {
                        0 -> {
                            ResultKt.throwOnFailure(result)
                            val flow = flowCollector
                            val iterator = this$0.getMethod()
                            val ctx = $ctx
                            val destination = ArrayList<SootMethod>(iterator.size)

                            for (item in iterator) {
                                destination.add((item as SootMethod).signature)
                            }

                            val component = ComponentEntryProvider(ctx, destination)
                            val nestedFlow = FlowKt.flow(Function2 { nestedFlowCollector: FlowCollector<AnalyzeTask>, nestedContinuation: Continuation<Unit> ->
                                object : Continuation<Any?> {
                                    var label = 0
                                    val $component = component
                                    val $entries = entries

                                    override fun invokeSuspend(nestedResult: Any?): Any? {
                                        val nestedSuspended = IntrinsicsKt.getCOROUTINE_SUSPENDED()
                                        when (label) {
                                            0 -> {
                                                ResultKt.throwOnFailure(nestedResult)
                                                val nestedFlow = nestedFlowCollector
                                                val task = object : AnalyzeTask {
                                                    private val entries: Set<SootMethod> = $component.getMethod()
                                                    private val components: Set<SootClass> = emptySet()

                                                    override fun getEntries(): Set<SootMethod> = entries
                                                    override fun getComponents(): Set<SootClass> = components
                                                    override fun getMethodsMustAnalyze(): Set<SootMethod> = $entries
                                                    override fun getName(): String = "(entries size: ${$entries.size})"
                                                    override fun needConstructCallGraph(sootCtx: SootCtx) {
                                                        sootCtx.constructCallGraph()
                                                    }
                                                    override fun getAdditionalEntries(): Set<SootMethod> {
                                                        return AnalyzeTask.DefaultImpls.getAdditionalEntries(this)
                                                    }
                                                }
                                                label = 1
                                                if (nestedFlow.emit(task, this) === nestedSuspended) {
                                                    return nestedSuspended
                                                }
                                            }
                                            1 -> {
                                                ResultKt.throwOnFailure(nestedResult)
                                            }
                                            else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                                        }
                                        return Unit
                                    }
                                }
                            })

                            label = 1
                            if (FlowKt.emitAll(flow, nestedFlow, this) === suspended) {
                                return suspended
                            }
                        }
                        1 -> {
                            ResultKt.throwOnFailure(result)
                        }
                        else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                    }
                    return Unit
                }
            }
        })
    }
}