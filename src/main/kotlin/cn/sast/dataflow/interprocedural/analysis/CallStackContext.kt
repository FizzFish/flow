package cn.sast.dataflow.interprocedural.analysis

import soot.SootMethod
import soot.Unit

/**
 * 记录一次调用链上的栈帧信息。
 *
 * @param caller    上一栈帧，或 `null` 表示入口
 * @param callSite  调用点语句
 * @param method    当前被调方法
 * @param deep      深度（root=0）
 */
class CallStackContext(
   val caller: CallStackContext?,
   val callSite: Unit,
   val method: SootMethod,
   val deep: Int
) {

   override fun toString(): String =
      buildString {
         append("at ")
         append(caller?.method ?: "root")
         append(" line: ")
         append(callSite.javaSourceStartLineNumber)
         append(": ")
         append(callSite)
         append(" invoke -> ")
         append(method)
         append('\n')
      }
}
