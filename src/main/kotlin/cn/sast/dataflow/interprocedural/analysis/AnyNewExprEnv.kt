package cn.sast.dataflow.interprocedural.analysis

import soot.SootMethod
import soot.Unit

/**
 * `new` 表达式的运行环境（方法 + 语句）。
 */
class AnyNewExprEnv(
   val method: SootMethod,
   u: Unit
) : HeapValuesEnv(u)
