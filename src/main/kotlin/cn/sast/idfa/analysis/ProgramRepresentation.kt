package cn.sast.idfa.analysis

import soot.toolkits.graph.DirectedGraph

// 移除冗余的 public 修饰符，去掉未使用的类型参数 M 和 N
interface ProgramRepresentation {
   // 移除冗余的 public 和 abstract 修饰符，去掉函数体
   fun getControlFlowGraph(method: Any): DirectedGraph<Any>?

   // 移除冗余的 public 和 abstract 修饰符，去掉函数体
   fun getSummaryControlFlowGraph(method: Any): DirectedGraph<Any>?

   // 移除冗余的 public 和 abstract 修饰符，去掉函数体
   fun isCall(node: Any): Boolean

   // 移除冗余的 public 和 abstract 修饰符，去掉函数体
   fun isAnalyzable(method: Any): Boolean

   // 移除冗余的 public 和 abstract 修饰符，去掉函数体
   fun getCalleesOfCallAt(callerMethod: Any, callNode: Any): Set<Any>

   // 移除冗余的 public 和 abstract 修饰符，去掉函数体
   fun isSkipCall(node: Any): Boolean

   // 移除冗余的 public 和 abstract 修饰符，去掉函数体
   fun setOwnerStatement(u: Any, owner: Any)

   // 移除冗余的 public 和 abstract 修饰符，去掉函数体
   fun setOwnerStatement(g: Iterable<Any>, owner: Any)
}