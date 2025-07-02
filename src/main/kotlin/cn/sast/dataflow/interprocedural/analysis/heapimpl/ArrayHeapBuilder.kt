package cn.sast.dataflow.interprocedural.analysis.heapimpl

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapDataBuilder
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IHeapValuesFactory
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.ReferenceContext
import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder
import soot.ArrayType

public abstract class ArrayHeapBuilder<V> : HeapDataBuilder<Integer, V> {
   public final val type: ArrayType
   public final val allSize: Builder<Any>
   public final val size: Int?

   public final var initializedValue: CompanionV<Any>?
      internal set

   open fun ArrayHeapBuilder(
      element: kotlinx.collections.immutable.PersistentMapBuilder<Integer, IHeapValues<V>>,
      unreferenced: IHeapValuesBuilder<V>?,
      type: ArrayType,
      allSize: IHeapValuesBuilder<V>,
      size: Int?,
      initializedValue: CompanionV<V>?
   ) {
      super(element, unreferenced);
      this.type = type;
      this.allSize = allSize;
      this.size = size;
      this.initializedValue = initializedValue;
      if (!this.allSize.isNotEmpty()) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      }
   }

   public open fun getValue(hf: IHeapValuesFactory<Any>, key: Int): IHeapValues<Any>? {
      val initializedValue: CompanionV = this.initializedValue;
      var var10000: IHeapValues = super.getValue(hf, key);
      if (var10000 == null) {
         label19: {
            if (this.size != null) {
               val var5: Int = this.size;
               val var4: Int = this.getMap().size();
               if (var5 != null) {
                  if (var5 == var4) {
                     break label19;
                  }
               }
            }

            if (initializedValue != null) {
               return hf.single(initializedValue);
            }
         }

         var10000 = null;
      }

      return var10000;
   }

   public open fun set(hf: IHeapValuesFactory<Any>, env: HeapValuesEnv, key: Int?, update: IHeapValues<Any>?, append: Boolean) {
      if (!(ArrayHeapKVKt.isValidKey(key, this.size) == false)) {
         super.set(hf, env, key, update, append);
      }
   }

   public override fun cloneAndReNewObjects(re: IReNew<Any>) {
      super.cloneAndReNewObjects(re.context(ReferenceContext.ArrayElement.INSTANCE));
      this.allSize.cloneAndReNewObjects(re.context(ReferenceContext.ArraySize.INSTANCE));
      val initializedValue: CompanionV = this.initializedValue;
      if (this.initializedValue != null) {
         val k: Any = this.initializedValue.getValue();
         var var10000: CompanionV = (CompanionV)re.checkNeedReplace(k);
         if (var10000 == null) {
            var10000 = (CompanionV)k;
         }

         var10000 = re.context(ReferenceContext.ArrayInitialized.INSTANCE).checkNeedReplace(initializedValue);
         if (var10000 == null) {
            var10000 = initializedValue;
         }

         var newCompV: CompanionV = var10000;
         if (k == var10000 && var10000 === initializedValue) {
            return;
         }

         if (!(var10000.getValue() == var10000)) {
            newCompV = var10000.copy(var10000);
         }

         this.initializedValue = newCompV;
      }
   }

   public fun clearAllIndex() {
   }
}
