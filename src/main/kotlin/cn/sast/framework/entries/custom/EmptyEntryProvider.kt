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

            override val entries: Set<SootMethod> = emptySet()
            override val name: String = "(empty entries provider)"

            override fun needConstructCallGraph(sootCtx: SootCtx) {
                sootCtx.callGraph = CallGraph()
            }

        })
    }

    override fun startAnalyse() = Unit
    override fun endAnalyse() = Unit
}