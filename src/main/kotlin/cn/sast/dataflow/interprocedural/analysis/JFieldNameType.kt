package cn.sast.dataflow.interprocedural.analysis

import soot.Type

/**
 * 仅由 *字段名 + Soot Type* 构成的字段描述。
 */
class JFieldNameType(
   private val fieldName: String,
   override val type: Type
) : JFieldType() {

   override val name: String
      get() = fieldName

   /* ---------- Object ---------- */

   override fun toString(): String = name
   override fun hashCode(): Int    = name.hashCode()
   override fun equals(other: Any?): Boolean =
      (other as? JFieldType)?.name == name
}
