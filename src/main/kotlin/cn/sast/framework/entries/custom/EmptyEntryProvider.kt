package cn.sast.framework.entries.custom

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import soot.SootClass
import soot.SootMethod
import soot.jimple.toolkits.callgraph.CallGraph

public object EmptyEntryProvider : IEntryPointProvider {
    override val iterator: Flow<AnalyzeTask> =
        FlowKt.flow(Function2<FlowCollector<AnalyzeTask>, Continuation<Unit>, Any?> { `$this$flow`, continuation ->
            val var3x: Any? = IntrinsicsKt.getCOROUTINE_SUSPENDED()
            when (continuation.label) {
                0 -> {
                    val var10001 = object : AnalyzeTask {
                        private val entries: Set<SootMethod> = emptySet()
                        private val components: Set<SootClass> = emptySet()

                        override fun getEntries(): Set<SootMethod> = entries
                        override fun getMethodsMustAnalyze(): Set<SootMethod> = getEntries()
                        override fun getComponents(): Set<SootClass> = components
                        override fun getName(): String = "(empty entries provider)"
                        override fun needConstructCallGraph(sootCtx: SootCtx) {
                            sootCtx.setCallGraph(CallGraph())
                        }
                        override fun getAdditionalEntries(): Set<SootMethod> = 
                            AnalyzeTask.DefaultImpls.getAdditionalEntries(this)
                    }
                    
                    continuation.label = 1
                    if (`$this$flow`.emit(var10001, continuation) === var3x) {
                        return@Function2 var3x
                    }
                }
                1 -> Unit
                else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
            }
            Unit
        })

    override fun startAnalyse() {
        IEntryPointProvider.DefaultImpls.startAnalyse(this)
    }

    override fun endAnalyse() {
        IEntryPointProvider.DefaultImpls.endAnalyse(this)
    }
}