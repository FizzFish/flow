package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.ImmutableSet

/** 堆值集合 */
interface IHeapValues<V> : IDiffAble<V>, Iterable<CompanionV<V>> {

   val size: Int
   val values: ImmutableSet<V>
   val valuesCompanion: ImmutableSet<CompanionV<V>>
   val single: CompanionV<V>

   fun reference(res: MutableCollection<Any>)

   operator fun plus(rhs: IHeapValues<V>): IHeapValues<V>
   operator fun plus(rhs: CompanionV<V>): IHeapValues<V>

   fun isNotEmpty(): Boolean
   fun isEmpty(): Boolean = !isNotEmpty()

   fun builder(): Builder<V>

   fun map(c: Builder<V>, transform: (CompanionV<V>) -> CompanionV<V>)
   fun flatMap(
      c: Builder<V>,
      transform: (CompanionV<V>) -> Collection<CompanionV<V>>
   )

   fun isSingle(): Boolean
   fun getAllIntValue(must: Boolean): MutableSet<Int>?
   fun getMaxInt(must: Boolean): Int? =
      getAllIntValue(must)?.maxOrNull()

   override fun iterator(): Iterator<CompanionV<V>>
   fun cloneAndReNewObjects(re: IReNew<V>): IHeapValues<V>

   /* ---------- Builder ---------- */

   interface Builder<V> {
      fun isEmpty(): Boolean
      fun isNotEmpty(): Boolean

      fun add(elements: IHeapValues<V>): Builder<V>
      fun add(element: CompanionV<V>): Builder<V>

      fun build(): IHeapValues<V>
      fun cloneAndReNewObjects(re: IReNew<V>)
   }
}
