package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.FieldUtil
import cn.sast.dataflow.interprocedural.analysis.HeapKVData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IHeapValuesFactory
import cn.sast.dataflow.interprocedural.analysis.JFieldType
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.PersistentMap
import soot.RefType

@SourceDebugExtension(["SMAP\nFIeldHeap.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FIeldHeap.kt\ncn/sast/dataflow/interprocedural/check/heapimpl/FieldHeapKV\n+ 2 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n*L\n1#1,56:1\n50#2:57\n*S KotlinDebug\n*F\n+ 1 FIeldHeap.kt\ncn/sast/dataflow/interprocedural/check/heapimpl/FieldHeapKV\n*L\n27#1:57\n*E\n"])
public abstract class FieldHeapKV<V> : HeapKVData<JFieldType, V> {
   public final val clz: RefType

   open fun FieldHeapKV(clz: RefType, fields: PersistentMap<JFieldType, ? extends IHeapValues<V>>, unreferenced: IHeapValues<V>?) {
      super(fields, unreferenced);
      this.clz = clz;
   }

   public open fun isValidKey(key: JFieldType?): Boolean? {
      return true;
   }

   public open fun get(hf: IHeapValuesFactory<Any>, key: JFieldType?): IHeapValues<Any>? {
      return if (key != null) this.getMap().get(key) as IHeapValues else this.getFromNullKey(hf);
   }

   public open fun ppKey(key: JFieldType): String {
      val `this_$iv`: FieldUtil = FieldUtil.INSTANCE;
      return key.getName();
   }

   public override fun computeHash(): Int {
      return 31 * super.computeHash() + this.clz.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (!super.equals(other)) {
         return false;
      } else {
         return other is FieldHeapKV && this.clz == (other as FieldHeapKV).clz;
      }
   }

   public override fun hashCode(): Int {
      return super.hashCode();
   }
}
