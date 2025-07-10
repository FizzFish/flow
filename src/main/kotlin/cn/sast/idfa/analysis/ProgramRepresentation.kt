package cn.sast.idfa.analysis

import soot.toolkits.graph.DirectedGraph

interface ProgramRepresentation<M, N> {
   fun getControlFlowGraph(method: M): DirectedGraph<N>?

   fun getSummaryControlFlowGraph(method: M): DirectedGraph<N>?

   fun isCall(node: N): Boolean

   fun isAnalyzable(method: M): Boolean

   fun getCalleesOfCallAt(callerMethod: M, callNode: N): Set<M>?

   fun isSkipCall(node: N): Boolean

   fun setOwnerStatement(unit: N, owner: M)

   fun setOwnerStatement(iterable: Iterable<N>, method: M)
}