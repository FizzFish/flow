package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapKVData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.JFieldType
import cn.sast.dataflow.interprocedural.check.heapimpl.FieldHeapKV
import kotlinx.collections.immutable.PersistentMap
import soot.RefType

public class FieldSpace<V>(clz: RefType, fields: PersistentMap<JFieldType, IHeapValues<Any>>, unreferenced: IHeapValues<Any>?) : FieldHeapKV(
      clz, fields, unreferenced
   ) {
   public override fun builder(): IHeapKVData.Builder<JFieldType, Any> {
      val var10003: RefType = this.getClz();
      val var10004: kotlinx.collections.immutable.PersistentMap.Builder = this.getMap().builder();
      val var10005: IHeapValues = this.getUnreferenced();
      return (new FieldSpaceBuilder<>(this, var10003, var10004, if (var10005 != null) var10005.builder() else null)) as IHeapKVDataBuilder<JFieldType, V>;
   }

   public override fun getName(): String {
      return "field(${this.getClz()})";
   }

   public override fun cloneAndReNewObjects(re: IReNew<Any>): IData<Any> {
      val b: IHeapKVData.Builder = this.builder();
      b.cloneAndReNewObjects(re);
      return b.build();
   }
}
