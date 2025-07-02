package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.HeapKVData
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapKVData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import kotlinx.collections.immutable.PersistentMap

public class HeapCollection<E>(element: PersistentMap<Int, IHeapValues<Any>>, unreferenced: IHeapValues<Any>?) : HeapKVData(element, unreferenced) {
   public open fun isValidKey(key: Int?): Boolean? {
      return true;
   }

   public override fun getName(): String {
      return "Collection";
   }

   public override fun builder(): IHeapKVData.Builder<Int, Any> {
      val var10002: kotlinx.collections.immutable.PersistentMap.Builder = this.getMap().builder();
      val var10003: IHeapValues = this.getUnreferenced();
      return (new HeapCollectionBuilder<>(var10002, if (var10003 != null) var10003.builder() else null)) as IHeapKVDataBuilder<Integer, E>;
   }

   public override fun cloneAndReNewObjects(re: IReNew<Any>): IData<Any> {
      val b: IHeapKVData.Builder = this.builder();
      b.cloneAndReNewObjects(re);
      return b.build();
   }
}
