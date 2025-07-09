package cn.sast.framework.entries.custom

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import soot.SootClass
import soot.SootMethod
import soot.jimple.toolkits.callgraph.CallGraph

/**
 * Emits exactly one task with an empty entry set and a fresh (empty) call​‑graph.
 */
object EmptyEntryProvider : IEntryPointProvider {

    override val iterator: Flow<IEntryPointProvider.AnalyzeTask> = flow {
        emit(object : IEntryPointProvider.AnalyzeTask {

            override fun getEntries(): Set<SootMethod> = emptySet()
            override fun getComponents(): Set<SootClass> = emptySet()
            override fun getName(): String = "EmptyEntryProvider"

            override fun needConstructCallGraph(sootCtx: SootCtx) {
                sootCtx.setCallGraph(CallGraph())
            }

            override fun getMethodsMustAnalyze(): Set<SootMethod> = emptySet()
            override fun getAdditionalEntries(): Set<SootMethod> = emptySet()
        })
    }

    override fun startAnalyse() = Unit
    override fun endAnalyse() = Unit
}