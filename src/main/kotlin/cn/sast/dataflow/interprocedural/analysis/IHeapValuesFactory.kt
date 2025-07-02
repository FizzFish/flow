package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder

public interface IHeapValuesFactory<V> {
   public abstract fun empty(): IHeapValues<Any> {
   }

   public open fun emptyBuilder(): Builder<Any> {
   }

   public abstract fun single(v: CompanionV<Any>): IHeapValues<Any> {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun <V> emptyBuilder(`$this`: IHeapValuesFactory<V>): IHeapValuesBuilder<V> {
         return `$this`.empty().builder();
      }
   }
}
