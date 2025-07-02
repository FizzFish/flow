package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IHeapValuesFactory
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder
import kotlinx.collections.immutable.PersistentMap

public open class ObjectKeyHashMapBuilder(keys: Builder<IValue>,
   fields: kotlinx.collections.immutable.PersistentMap.Builder<IValue, IHeapValues<IValue>>,
   unreferenced: Builder<IValue>?
) : ImmutableElementHashMapBuilder(fields, unreferenced) {
   public final val keys: Builder<IValue>

   init {
      this.keys = keys;
   }

   public open fun set(hf: IHeapValuesFactory<IValue>, env: HeapValuesEnv, key: IValue?, update: IHeapValues<IValue>?, append: Boolean) {
      throw new IllegalStateException("key must be CompanionV".toString());
   }

   public fun set(hf: IHeapValuesFactory<IValue>, env: HeapValuesEnv, key: CompanionV<IValue>, update: IHeapValues<IValue>?, append: Boolean) {
      super.set(hf, env, key.getValue(), update, append);
      this.keys.add(key);
   }

   public fun set(hf: IHeapValuesFactory<IValue>, env: HeapValuesEnv, key: IHeapValues<IValue>, update: IHeapValues<IValue>?, append: Boolean) {
      for (CompanionV k : key) {
         this.set(hf, env, k, update, append);
      }
   }

   public override fun build(): ImmutableElementHashMap<IValue, IValue> {
      val var10002: IHeapValues = this.keys.build();
      val var10003: PersistentMap = this.getMap().build();
      val var10004: IHeapValues.Builder = this.getUnreferenced();
      return new ObjectKeyHashMap(var10002, var10003, if (var10004 != null) var10004.build() else null);
   }
}
