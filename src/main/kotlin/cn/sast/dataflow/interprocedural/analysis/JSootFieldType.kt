package cn.sast.dataflow.interprocedural.analysis

import soot.SootField
import soot.Type

/**
 * 直接封装 SootField 的字段类型实现。
 */
class JSootFieldType(
   val sootField: SootField
) : JFieldType() {

   override val type: Type
      get() = sootField.type

   override val name: String
      get() = sootField.name

   /* ---------- Object ---------- */

   override fun hashCode(): Int = name.hashCode()
   override fun equals(other: Any?): Boolean =
      (other as? JFieldType)?.name == name

   override fun toString(): String = name
}
