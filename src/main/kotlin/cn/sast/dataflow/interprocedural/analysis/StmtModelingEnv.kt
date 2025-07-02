package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.check.ModelingStmtInfo
import soot.Unit

/**
 * 为一条建模语句绑定运行期信息。
 */
class StmtModelingEnv(
   u: Unit,
   val info: ModelingStmtInfo
) : HeapValuesEnv(u)
