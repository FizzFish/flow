package cn.sast.dataflow.infoflow.svfa

import soot.*
import soot.jimple.*
import soot.jimple.infoflow.data.Abstraction
import soot.jimple.infoflow.data.AccessPathFragment

/**
 * （Value, [field]） 的简易二元组，用来抽象 Access-Path 的 *首段*。
 *
 * - 如果 [field] 为 `null`，则仅代表 **base**；
 * - 否则表示 `value.field` 这一段。
 */
data class AP(val value: Value, val field: SootField? = null) {

   /** 返回去掉 field 的 base 形态（若本来就没有 field 则原样返回） */
   fun base(): AP = if (field == null) this else copy(field = null)

   override fun toString(): String =
      if (field != null) "${value}.${field.name}" else value.toString()

   /* ---------- 静态工具 ---------- */

   companion object {

      /** 对于 `static` 字段，统一用一个哑元值做 base。 */
      private val staticValue: Value = IntConstant.v(0)

      /** 从 *Jimple* 表达式抽取首段 AP；取不到时返回 `null`. */
      operator fun get(value: Value): AP? {
         var base: Value? = null
         var field: SootField? = null

         when (value) {
            is StaticFieldRef -> {
               base = staticValue
               field = value.field
            }
            is Local           -> base = value
            is InstanceFieldRef -> {
               base  = value.base
               field = value.field
            }
            is ArrayRef        -> base = value.base as? Local
            is LengthExpr      -> base = value.op
            is NewArrayExpr    -> base = value.size
         }
         return base?.let { AP(it, field) }
      }

      /** 从 FlowDroid 的 [Abstraction] 中抽取首段 AP（一定成功） */
      operator fun get(abs: Abstraction): AP {
         val base = abs.accessPath.plainValue as Value
         val frag: AccessPathFragment? = abs.accessPath.firstFragment
         return AP(base, frag?.field)
      }
   }
}
