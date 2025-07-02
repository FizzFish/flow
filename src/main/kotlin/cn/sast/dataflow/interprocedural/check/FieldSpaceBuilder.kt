package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.JFieldType
import cn.sast.dataflow.interprocedural.check.heapimpl.FieldHeapBuilder
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentMap.Builder
import soot.RefType

public class FieldSpaceBuilder<V>(orig: FieldSpace<Any>,
   clz: RefType,
   fields: Builder<JFieldType, IHeapValues<Any>>,
   unreferenced: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<Any>?
) : FieldHeapBuilder(clz, fields, unreferenced) {
   public final val orig: FieldSpace<Any>

   init {
      this.orig = orig;
   }

   public override fun build(): IData<Any> {
      val newMap: PersistentMap = this.getMap().build();
      val var10000: IHeapValues.Builder = this.getUnreferenced();
      val newUn: IHeapValues = if (var10000 != null) var10000.build() else null;
      return if (newMap === this.orig.getMap() && newUn === this.orig.getUnreferenced()) this.orig else new FieldSpace<>(this.getClz(), newMap, newUn);
   }
}
