package cn.sast.dataflow.interprocedural.analysis

import soot.Unit

/** 某条语句 [node] 发生时所处的分析环境（携带 [ctx] 上下文） */
class HookEnv(
   val ctx: AIContext,
   node: Unit
) : HeapValuesEnv(node)
