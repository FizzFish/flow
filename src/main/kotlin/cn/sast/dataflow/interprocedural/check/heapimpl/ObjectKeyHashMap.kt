package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.HeapValues
import cn.sast.dataflow.interprocedural.analysis.HeapValuesBuilder
import cn.sast.dataflow.interprocedural.analysis.IDiff
import cn.sast.dataflow.interprocedural.analysis.IDiffAble
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.PersistentMap

public class ObjectKeyHashMap(keys: IHeapValues<IValue>, fields: PersistentMap<IValue, IHeapValues<IValue>>, unreferenced: IHeapValues<IValue>?) : ImmutableElementHashMap(
      fields, unreferenced
   ) {
   public final val keys: IHeapValues<IValue>

   public final val values: IHeapValues<IValue>
      public final get() {
         val var2: HeapValuesBuilder = new HeapValues(null, 1, null).builder();
         val it: HeapValuesBuilder = var2;

         for (IHeapValues v : (ImmutableCollection)this.getMap().values()) {
            it.add(v);
         }

         return var2.build();
      }


   init {
      this.keys = keys;
   }

   public override fun diff(cmp: IDiff<IValue>, that: IDiffAble<out Any?>) {
      if (that is ObjectKeyHashMap) {
         this.keys.diff(cmp, (that as ObjectKeyHashMap).keys);
         this.getValues().diff(cmp, (that as ObjectKeyHashMap).getValues());
      }

      super.diff(cmp, that);
   }

   public override fun computeHash(): Int {
      return 31 * 1 + super.computeHash();
   }

   public override fun hashCode(): Int {
      return super.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (!super.equals(other)) {
         return false;
      } else {
         return other is ObjectKeyHashMap && this.keys == (other as ObjectKeyHashMap).keys;
      }
   }

   public override fun builder(): ImmutableElementHashMapBuilder<IValue, IValue> {
      val var10002: IHeapValues.Builder = this.keys.builder();
      val var10003: kotlinx.collections.immutable.PersistentMap.Builder = this.getMap().builder();
      val var10004: IHeapValues = this.getUnreferenced();
      return new ObjectKeyHashMapBuilder(var10002, var10003, if (var10004 != null) var10004.builder() else null);
   }
}
