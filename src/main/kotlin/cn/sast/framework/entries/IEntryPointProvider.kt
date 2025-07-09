package cn.sast.framework.entries

import cn.sast.framework.SootCtx
import kotlinx.coroutines.flow.Flow
import soot.Scene
import soot.SootClass
import soot.SootMethod

/**
 * Core abstraction that yields one or more [AnalyzeTask] describing how the analysis should start.
 */
interface IEntryPointProvider {
    /** Stream of analysis tasks emitted by this provider. */
    val iterator: Flow<AnalyzeTask>

    /** Optional life-cycle callback – invoked before the first task. */
    fun startAnalyse() {}

    /** Optional life-cycle callback – invoked after the last task. */
    fun endAnalyse() {}

    //----------------------------------------------------------------------------------------------------------------
    interface AnalyzeTask {
        /** Set of methods considered *entry points* for the data-flow / taint analysis. */
        val entries: Set<SootMethod>
        /** Additional entry points discovered after the fact (rare). */
        val additionalEntries: Set<SootMethod> get() = emptySet()
        /** Components (classes) a framework should keep alive while analysing [entries]. */
        val components: Set<SootClass> get() = emptySet()
        /** Human-readable identifier shown in logs. */
        val name: String

        /** Whether the caller must ensure a call-graph exists before visiting [entries]. */
        fun needConstructCallGraph(sootCtx: SootCtx) {}

        /** Methods that must always be analysed even if not reachable from [entries]. */
        val methodsMustAnalyze: Set<SootMethod>
            get() = Scene.v().applicationClasses
                .filter { it.isInScene }
                .flatMap { it.methods }
                .toSet()
    }
}