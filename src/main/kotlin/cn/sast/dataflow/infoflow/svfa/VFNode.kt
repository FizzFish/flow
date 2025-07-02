package cn.sast.dataflow.infoflow.svfa

import soot.Value
import soot.Unit as SootUnit   // 避免与 kotlin.Unit 冲突

/**
 * `(value, stmt)` 的二元组，用作数据流图节点。
 */
internal data class VFNode(
   val ap: Value,
   val stmt: SootUnit
)
