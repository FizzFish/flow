package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.ImmutableSet

public interface IHeapValues<V> : IDiffAble<V> {
   public val size: Int
   public val values: ImmutableSet<Any>
   public val valuesCompanion: ImmutableSet<CompanionV<Any>>
   public val single: CompanionV<Any>

   public abstract fun reference(res: MutableCollection<Any>) {
   }

   public abstract operator fun plus(rhs: IHeapValues<Any>): IHeapValues<Any> {
   }

   public abstract operator fun plus(rhs: CompanionV<Any>): IHeapValues<Any> {
   }

   public abstract fun isNotEmpty(): Boolean {
   }

   public abstract fun isEmpty(): Boolean {
   }

   public abstract fun builder(): cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<Any> {
   }

   public abstract fun map(c: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<Any>, transform: (CompanionV<Any>) -> CompanionV<Any>) {
   }

   public abstract fun flatMap(
      c: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<Any>,
      transform: (CompanionV<Any>) -> Collection<CompanionV<Any>>
   ) {
   }

   public abstract fun isSingle(): Boolean {
   }

   public abstract fun getAllIntValue(must: Boolean): MutableSet<Int>? {
   }

   public open fun getMaxInt(must: Boolean): Int? {
   }

   public abstract operator fun iterator(): Iterator<CompanionV<Any>> {
   }

   public abstract fun cloneAndReNewObjects(re: IReNew<Any>): IHeapValues<Any> {
   }

   public interface Builder<V> {
      public abstract fun isEmpty(): Boolean {
      }

      public abstract fun isNotEmpty(): Boolean {
      }

      public abstract fun add(elements: IHeapValues<Any>): cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<Any> {
      }

      public abstract fun add(element: CompanionV<Any>): cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<Any> {
      }

      public abstract fun build(): IHeapValues<Any> {
      }

      public abstract fun cloneAndReNewObjects(re: IReNew<Any>) {
      }
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun <V> getMaxInt(`$this`: IHeapValues<V>, must: Boolean): Int? {
         val var10000: java.util.Set = `$this`.getAllIntValue(must);
         return if (var10000 != null) CollectionsKt.maxOrNull(var10000) as Int else null;
      }
   }
}
