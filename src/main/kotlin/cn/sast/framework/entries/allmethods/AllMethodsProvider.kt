package cn.sast.framework.entries.allmethods

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import soot.Scene
import soot.SootClass
import soot.SootMethod

public class AllMethodsProvider(classes: Collection<SootClass> = Scene.v().applicationClasses as Collection<SootClass>) : IEntryPointProvider {
    public val classes: Collection<SootClass>
    public override val iterator: Flow<AnalyzeTask>

    init {
        this.classes = classes
        this.iterator = FlowKt.flow(Function2<FlowCollector<AnalyzeTask>, Continuation<Unit>, Any?> { `$this$flow`, continuation ->
            val var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED()
            when (continuation.label) {
                0 -> {
                    val task = object : AnalyzeTask(this@AllMethodsProvider) {
                        private val entries: Set<SootMethod>
                        private val components: Void? = null
                        private val name: String

                        init {
                            val filteredClasses = classes.filter { it.isInScene }
                            val methods = ArrayList<SootMethod>()
                            for (clazz in filteredClasses) {
                                methods.addAll(clazz.methods)
                            }
                            entries = methods.toSet()
                            name = "(entries size: ${entries.size})"
                        }

                        override fun getEntries(): Set<SootMethod> = entries
                        override fun getComponents(): Void? = components
                        override fun getName(): String = name
                        override fun needConstructCallGraph(sootCtx: SootCtx) {
                            sootCtx.constructCallGraph()
                        }
                    }

                    continuation.label = 1
                    if (`$this$flow`.emit(task, continuation) == var3) {
                        return@Function2 var3
                    }
                }
                1 -> Unit
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

    constructor() : this(Scene.v().applicationClasses)
}