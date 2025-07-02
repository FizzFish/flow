package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.PersistentList

/**
 * 能够为自身生成 **PhantomField** 的抽象接口。
 */
interface IFieldManager<V : IValue> : IValue {

   /** 字段链 → PhantomField 的缓存表 */
   fun getPhantomFieldMap(): MutableMap<PersistentList<JFieldType>, PhantomField<V>>

   /* ---------- 默认工具 ---------- */

   fun getPhantomField(field: JFieldType): PhantomField<V> =
      getPhantomField(kotlinx.collections.immutable.persistentListOf(field))

   fun getPhantomField(ap: PersistentList<JFieldType>): PhantomField<V> =
      getPhantomFieldMap().getOrPut(ap) { PhantomField(this, ap) }
}
