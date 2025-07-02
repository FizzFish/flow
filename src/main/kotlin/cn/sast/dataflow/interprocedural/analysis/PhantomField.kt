package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.plus
import soot.Type

/**
 * 由 [base] + 字段链 [accessPath] 动态派生出的“虚字段”抽象值。
 */
open class PhantomField<V : IValue>(
   override val type: Type,
   private val base: IFieldManager<*>,
   val accessPath: PersistentList<JFieldType>
) : IValue {

   constructor(base: IFieldManager<*>, accessPath: PersistentList<JFieldType>) :
           this(accessPath.last().type, base, accessPath)

   /* ---------- IValue ---------- */

   override fun typeIsConcrete(): Boolean = false
   override fun isNullConstant(): Boolean = false
   override fun getKind(): IValue.Kind = IValue.Kind.Summary
   override fun copy(type: Type): IValue = PhantomField(type, base, accessPath)

   /* ---------- 扩展 ---------- */

   fun getPhantomField(field: JFieldType): PhantomField<IValue> =
      base.getPhantomField(accessPath + field)

   /* ---------- Object ---------- */

   override fun equals(other: Any?): Boolean =
      other is PhantomField<*> &&
              base == other.base &&
              accessPath == other.accessPath &&
              type == other.type

   override fun hashCode(): Int =
      31 * (31 * base.hashCode() + accessPath.hashCode()) + type.hashCode()

   override fun toString(): String =
      buildString {
         append("PhantomObject_").append(type).append('_')
         append(base).append('.')
         append(accessPath.joinToString(".") { it.name })
      }
}
