package cn.sast.dataflow.interprocedural.analysis.heapimpl

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.IHeapKVData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IHeapValuesFactory
import soot.ArrayType

public interface IArrayHeapKV<K, V> : IHeapKVData<K, V> {
   public val type: ArrayType

   public abstract fun getArrayLength(): IHeapValues<Any> {
   }

   public abstract fun getElement(hf: AbstractHeapFactory<Any>): IHeapValues<Any> {
   }

   public abstract fun getArray(hf: IHeapValuesFactory<Any>): Array<Any>? {
   }

   public abstract fun getByteArray(hf: IHeapValuesFactory<Any>): ByteArray? {
   }
}
