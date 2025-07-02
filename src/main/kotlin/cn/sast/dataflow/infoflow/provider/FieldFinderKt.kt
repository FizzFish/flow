package cn.sast.dataflow.infoflow.provider

import com.feysh.corax.config.api.ClassField
import soot.Scene
import soot.SootClass
import soot.SootField
import soot.Type

fun getSootField(field: ClassField): SootField? {
   val className = field.declaringClassType ?: return null
   val clazz: SootClass = Scene.v().getSootClassUnsafe(className, false) ?: return null

   return if (field.fieldType == null) {
      clazz.getFieldByNameUnsafe(field.fieldName)
   } else {
      val type: Type = Scene.v().getTypeUnsafe(field.fieldType)
         ?: return clazz.getFieldByNameUnsafe(field.fieldName)
      clazz.getFieldUnsafe(field.fieldName, type)
   }
}
