package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.HeapKVData
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentMap

public open class ImmutableElementHashMap<K, V>(fields: PersistentMap<Any, IHeapValues<Any>> = ExtensionsKt.persistentHashMapOf(),
   unreferenced: IHeapValues<Any>? = null
) : HeapKVData(fields, unreferenced) {
   public override operator fun equals(other: Any?): Boolean {
      if (!super.equals(other)) {
         return false;
      } else {
         return other is ImmutableElementHashMap;
      }
   }

   public override fun hashCode(): Int {
      return super.hashCode();
   }

   public override fun getName(): String {
      return "ImmHashMap";
   }

   public override fun isValidKey(key: Any?): Boolean? {
      return true;
   }

   public override fun cloneAndReNewObjects(re: IReNew<Any>): IData<Any> {
      val b: ImmutableElementHashMapBuilder = this.builder();
      b.cloneAndReNewObjects(re);
      return b.build();
   }

   public open fun builder(): ImmutableElementHashMapBuilder<Any, Any> {
      val var10002: kotlinx.collections.immutable.PersistentMap.Builder = this.getMap().builder();
      val var10003: IHeapValues = this.getUnreferenced();
      return new ImmutableElementHashMapBuilder<>(var10002, if (var10003 != null) var10003.builder() else null);
   }

   open fun ImmutableElementHashMap() {
      this(null, null, 3, null);
   }
}
