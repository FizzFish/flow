package cn.sast.dataflow.infoflow.provider

import com.feysh.corax.config.api.*
import mu.KLogger
import soot.Scene
import soot.SootClass
import soot.SootField
import soot.Type
import java.util.*

class FieldFinder(
   private val baseTypes: Set<String>?,
   private val acc: List<IClassField>
) {

   constructor(baseTypes: String?, acc: List<IClassField>) : this(
      baseTypes?.let { setOf(it) },
      acc
   )

   fun sootFields(): List<AccessPath> = find()

   fun find(): List<AccessPath> {
      if (acc.isEmpty()) {
         return listOf(AccessPath(emptyList(), false))
      }

      val res = mutableListOf<AccessPath>()
      val workList: Queue<Task> = LinkedList()
      workList.add(Task(emptyList(), acc))

      while (workList.isNotEmpty()) {
         val cur = workList.poll()
         val field = cur.right.first()
         when (field) {
            is ClassField -> {
               val sf = getSootField(field)
               sf?.let {
                  val remaining = cur.right.drop(1)
                  if (remaining.isEmpty()) {
                     res.add(AccessPath(cur.left + it, false))
                  } else {
                     workList.add(Task(cur.left + it, remaining))
                  }
               }
            }
            is SubFields -> {
               if (cur.right.size != 1) {
                  throw IllegalArgumentException("oops: $cur")
               }
               res.add(AccessPath(cur.left, true))
            }
            is BuiltInField, is AttributeName -> {
               res.add(AccessPath(cur.left, false))
            }
         }
      }
      return res
   }

   data class AccessPath(
      val sootField: List<SootField>,
      val subFields: Boolean
   )

   private data class Task(
      val left: List<SootField>,
      val right: List<IClassField>
   )

   companion object {
      private val logger: KLogger? = null
   }
}

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