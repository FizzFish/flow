package cn.sast.framework.entries.custom

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import java.util.Arrays
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import mu.KLogger
import soot.SootClass
import soot.SootMethod

public open class CustomEntryProvider(entries: Set<SootMethod>) : IEntryPointProvider {
    private val entries: Set<SootMethod>

    public val method: Set<SootMethod>
        get() {
            if (this.entries.isEmpty()) {
                logger.warn("No entry points")
            }

            logger.info(::_get_method_$lambda$0)
            return this.entries
        }

    public override val iterator: Flow<AnalyzeTask>

    init {
        this.entries = entries
        this.iterator = FlowKt.flow(Function2<FlowCollector<AnalyzeTask>, Continuation<*>, Any?> { `$this$flow`, continuation ->
            val var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED()
            when (continuation.label) {
                0 -> {
                    ResultKt.throwOnFailure(continuation.result)
                    val task = object : AnalyzeTask(this@CustomEntryProvider) {
                        private val entries: Set<SootMethod> = method
                        private val components: Set<SootClass> = emptySet()

                        override fun getEntries(): Set<SootMethod> = entries
                        override fun getMethodsMustAnalyze(): Set<SootMethod> = getEntries()
                        override fun getComponents(): Set<SootClass> = components
                        override fun getName(): String = "(entries size: ${getEntries().size})"
                        override fun needConstructCallGraph(sootCtx: SootCtx) {
                            sootCtx.constructCallGraph()
                        }
                        override fun getAdditionalEntries(): Set<SootMethod> = 
                            IEntryPointProvider.AnalyzeTask.DefaultImpls.getAdditionalEntries(this)
                    }
                    continuation.label = 1
                    if (`$this$flow`.emit(task, continuation) === var3) {
                        return@Function2 var3
                    }
                }
                1 -> {
                    ResultKt.throwOnFailure(continuation.result)
                }
                else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
            }
            Unit
        })
    }

    override fun startAnalyse() {
        IEntryPointProvider.DefaultImpls.startAnalyse(this)
    }

    override fun endAnalyse() {
        IEntryPointProvider.DefaultImpls.endAnalyse(this)
    }

    @JvmStatic
    private fun _get_method_$lambda$0(this$0: CustomEntryProvider): Any {
        val var2 = arrayOf<Any>(this$0.entries.size)
        return String.format("custom entry methods num :%d", Arrays.copyOf(var2, var2.size))
    }

    @JvmStatic
    private fun logger$lambda$1() {
        return Unit
    }

    public companion object {
        private val logger: KLogger
    }
}