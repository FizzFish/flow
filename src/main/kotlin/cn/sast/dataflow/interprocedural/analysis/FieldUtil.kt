package cn.sast.dataflow.interprocedural.analysis

import com.feysh.corax.config.api.ClassField
import soot.Scene
import soot.SootField
import soot.Type

/** 字段工具：在 Soot / Corax 类型之间做轻量转换 */
object FieldUtil {

   /** Soot 字段 → JSootFieldType */
   fun of(field: SootField): JSootFieldType =
      JSootFieldType(field)

   /** Corax ClassField → JFieldNameType（可能取不到类型则返回 null） */
   fun of(field: ClassField): JFieldNameType? {
      val typeStr = field.fieldType
      val sootType: Type = typeStr?.let { Scene.v().getTypeUnsafe(it, true) }
         ?: Scene.v().objectType
      return JFieldNameType(field.fieldName, sootType)
   }

   fun typeOf(field: JFieldType): Type = field.type
   fun nameOf(field: JFieldType): String = field.name
}
