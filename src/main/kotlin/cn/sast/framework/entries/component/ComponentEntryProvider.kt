package cn.sast.framework.entries.component

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import mu.KLogger
import soot.Scene
import soot.SootClass
import soot.SootMethod

public class ComponentEntryProvider(
    private val ctx: SootCtx,
    private val entries: Collection<String>
) : IEntryPointProvider {
    public val method: Set<SootMethod>
        get() {
            val var10000 = ComponentEntryPointCreator(entries).createDummyMain()
            if (!var10000.declaringClass.isInScene) {
                Scene.v().addClass(var10000.declaringClass)
            }

            var10000.declaringClass.setApplicationClass()
            return setOf(var10000)
        }

    override val iterator: Flow<AnalyzeTask>

    init {
        iterator = FlowKt.flow(Function2<FlowCollector<AnalyzeTask>, Continuation<Unit>, Any> { `$this$flow`, continuation ->
            object : Continuation<Unit> {
                var label = 0
                var L$0: Any? = null

                override fun invokeSuspend(result: Any): Any {
                    when (label) {
                        0 -> {
                            ResultKt.throwOnFailure(result)
                            L$0 = `$this$flow`
                            label = 1
                            val task = object : AnalyzeTask(this@ComponentEntryProvider) {
                                private val entries = this@ComponentEntryProvider.method
                                private val components: Set<SootClass> = TODO("FIXME — components initialization")

                                override fun getEntries(): Set<SootMethod> = entries
                                override fun getComponents(): Set<SootClass> = components
                                override fun getName(): String = "(entries size: ${entries.size})"
                                override fun needConstructCallGraph(sootCtx: SootCtx) {
                                    sootCtx.constructCallGraph()
                                }
                                override fun getMethodsMustAnalyze(): Set<SootMethod> = 
                                    AnalyzeTask.DefaultImpls.getMethodsMustAnalyze(this)
                                override fun getAdditionalEntries(): Set<SootMethod> = 
                                    AnalyzeTask.DefaultImpls.getAdditionalEntries(this)
                            }
                            if (`$this$flow`.emit(task, this) == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                                return IntrinsicsKt.getCOROUTINE_SUSPENDED()
                            }
                        }
                        1 -> {
                            ResultKt.throwOnFailure(result)
                        }
                        else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                    }
                    return Unit
                }

                override fun create(value: Any, completion: Continuation<*>): Continuation<Unit> {
                    return object : Continuation<Unit> {
                        init {
                            L$0 = value
                        }
                        override fun invokeSuspend(result: Any): Any = 
                            this@Function2.invokeSuspend(result)
                    }
                }

                override fun invoke(p1: FlowCollector<AnalyzeTask>, p2: Continuation<Unit>): Any {
                    return create(p1, p2).invokeSuspend(Unit)
                }
            }.invokeSuspend(Unit)
        })
    }

    override fun startAnalyse() {
        IEntryPointProvider.DefaultImpls.startAnalyse(this)
    }

    override fun endAnalyse() {
        IEntryPointProvider.DefaultImpls.endAnalyse(this)
    }

    @JvmStatic
    fun `logger$lambda$0`() {
    }

    public companion object {
        private val logger: KLogger = TODO("FIXME — logger initialization")
    }
}