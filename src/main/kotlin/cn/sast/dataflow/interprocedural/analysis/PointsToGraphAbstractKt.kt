package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.persistentHashMapOf

/* ---------- 扩展：遍历 / 映射 ---------- */

inline fun <V> IHeapValues<V>.forEachCompanion(
   action: (CompanionV<V>) -> Unit
) {
   for (e in this) action(e)
}

inline fun <K, V> Map<out K, IHeapValues<V>>.mapTo(
   destination: IHeapValues.Builder<V>,
   transform: (Map.Entry<K, IHeapValues<V>>) -> IHeapValues<V>
): IHeapValues.Builder<V> {
   for (item in entries) destination.add(transform(item))
   return destination
}

/** 空的 PersistentMap 工具，避免 import clutter */
internal fun <K, V> emptyPersistentMap(): PersistentMap<K, V> =
   persistentHashMapOf()
