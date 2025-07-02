package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.PersistentMap.Builder

internal class HeapValuesBuilder(orig: HeapValues, map: Builder<IValue, CompanionV<IValue>> = orig.getMap().builder()) : AbstractHeapValuesBuilder(orig, map) {
   public open val orig: HeapValues

   init {
      this.orig = orig;
   }

   public override fun build(): IHeapValues<IValue> {
      return if (this.getMap().build() === this.getOrig().getMap()) this.getOrig() else new HeapValues(this.getMap().build());
   }
}
