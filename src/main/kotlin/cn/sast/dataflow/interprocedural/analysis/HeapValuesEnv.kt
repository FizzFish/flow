package cn.sast.dataflow.interprocedural.analysis

import soot.Unit

/**
 * 绑定到某条 Jimple 语句的环境，供运算时存取附加信息。
 */
abstract class HeapValuesEnv(val node: Unit) {

   override fun toString(): String =
      "Env *${node.javaSourceStartLineNumber} $node"
}
