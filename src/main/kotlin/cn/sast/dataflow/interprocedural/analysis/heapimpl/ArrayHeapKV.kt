package cn.sast.dataflow.interprocedural.analysis.heapimpl

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapKVData
import cn.sast.dataflow.interprocedural.analysis.IDiff
import cn.sast.dataflow.interprocedural.analysis.IDiffAble
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IHeapValuesFactory
import kotlinx.collections.immutable.PersistentMap
import soot.ArrayType

public abstract class ArrayHeapKV<V> : HeapKVData<Integer, V>, IArrayHeapKV<Integer, V> {
   public final val allSize: IHeapValues<Any>
   public open val type: ArrayType
   public final val size: Int?
   public final val initializedValue: CompanionV<Any>?

   open fun ArrayHeapKV(
      element: PersistentMap<Integer, ? extends IHeapValues<V>>,
      unreferenced: IHeapValues<V>?,
      allSize: IHeapValues<V>,
      type: ArrayType,
      size: Int?,
      initializedValue: CompanionV<V>?
   ) {
      super(element, unreferenced);
      this.allSize = allSize;
      this.type = type;
      this.size = size;
      this.initializedValue = initializedValue;
      if (!this.allSize.isNotEmpty()) {
         throw new IllegalArgumentException("array length value set is empty".toString());
      }
   }

   public override fun getName(): String {
      return "${this.getType().getElementType()}[${this.size}]";
   }

   public open fun isValidKey(key: Int?): Boolean? {
      return ArrayHeapKVKt.isValidKey(key, this.size);
   }

   public override fun diff(cmp: IDiff<Any>, that: IDiffAble<out Any?>) {
      if (that is ArrayHeapKV) {
         this.allSize.diff(cmp, (that as ArrayHeapKV).allSize);
         if (this.initializedValue != null && (that as ArrayHeapKV).initializedValue != null) {
            cmp.diff(this.initializedValue, (that as ArrayHeapKV).initializedValue);
         }
      }

      super.diff(cmp, that);
   }

   public open fun getValue(hf: IHeapValuesFactory<Any>, key: Int): IHeapValues<Any>? {
      var var10000: IHeapValues = super.getValue(hf, key);
      if (var10000 == null) {
         if (this.size != null) {
            val var4: Int = this.size;
            val var3: Int = this.getMap().size();
            if (var4 != null) {
               if (var4 == var3) {
                  var10000 = this.getUnreferenced();
                  if (var10000 != null && var10000.isNotEmpty() && this.initializedValue != null) {
                     return hf.single(this.initializedValue);
                  }
               }
            }
         }

         var10000 = null;
      }

      return var10000;
   }

   public open fun get(hf: IHeapValuesFactory<Any>, key: Int?): IHeapValues<Any>? {
      return if (this.isValidKey(key) == false) null else super.get(hf, key);
   }

   public override fun getArrayLength(): IHeapValues<Any> {
      return this.allSize;
   }

   public override fun computeHash(): Int {
      return 31 * super.computeHash() + this.allSize.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (!super.equals(other)) {
         return false;
      } else {
         return other is ArrayHeapKV && this.allSize == (other as ArrayHeapKV).allSize;
      }
   }

   public override fun hashCode(): Int {
      return super.hashCode();
   }

   public open fun ppKey(key: Int): String {
      return java.lang.String.valueOf(key);
   }

   public override fun getFromNullKey(hf: IHeapValuesFactory<Any>): IHeapValues<Any> {
      val r: IHeapValues = super.getFromNullKey(hf);
      if (this.size != null) {
         val var10000: Int = this.size;
         val var3: Int = this.getMap().size();
         if (var10000 != null) {
            if (var10000 == var3) {
               return r;
            }
         }
      }

      return if (this.initializedValue != null) r.plus(hf.single(this.initializedValue)) else r;
   }
}
