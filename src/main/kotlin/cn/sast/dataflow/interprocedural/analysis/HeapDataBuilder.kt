package cn.sast.dataflow.interprocedural.analysis

import java.util.Map.Entry
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentMap.Builder
import mu.KLogger

public abstract class HeapDataBuilder<K, V> : IHeapKVData.Builder<K, V> {
   public final val map: Builder<Any, IHeapValues<Any>>

   public final var unreferenced: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<Any>?
      internal set

   open fun HeapDataBuilder(map: Builder<K, IHeapValues<V>>, unreferenced: IHeapValuesBuilder<V>?) {
      this.map = map;
      this.unreferenced = unreferenced;
   }

   public open fun getValue(hf: IHeapValuesFactory<Any>, key: Any): IHeapValues<Any>? {
      return this.map.get(key) as IHeapValues<V>;
   }

   public override fun set(hf: IHeapValuesFactory<Any>, env: HeapValuesEnv, key: Any?, update: IHeapValues<Any>?, append: Boolean) {
      if (update != null && !update.isEmpty()) {
         if (key == null) {
            if (this.unreferenced != null) {
               val var10000: IHeapValues.Builder = this.unreferenced;
               var10000.add(update);
            } else {
               this.unreferenced = update.builder();
            }
         } else {
            val exist: IHeapValues = this.getValue(hf, (K)key);
            (this.map as java.util.Map).put(key, if (append && exist != null) update.plus(exist) else update);
         }
      } else {
         logger.debug(HeapDataBuilder::set$lambda$0);
      }
   }

   public override fun union(hf: AbstractHeapFactory<Any>, that: IData<Any>) {
      if (that !is HeapKVData) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else {
         if (this.unreferenced == null) {
            if ((that as HeapKVData).getUnreferenced() != null) {
               val var10001: IHeapValues = (that as HeapKVData).getUnreferenced();
               this.unreferenced = var10001.builder();
            }
         } else if ((that as HeapKVData).getUnreferenced() != null) {
            val var10000: IHeapValues.Builder = this.unreferenced;
            val var10: IHeapValues = (that as HeapKVData).getUnreferenced();
            var10000.add(var10);
         }

         if (this.map != (that as HeapKVData).getMap()) {
            val var9: PersistentMap = (that as HeapKVData).getMap();

            for (Entry var4 : ((java.util.Map)var9).entrySet()) {
               val k: Any = var4.getKey();
               val v: IHeapValues = var4.getValue() as IHeapValues;
               val exist: IHeapValues = this.map.get(k) as IHeapValues;
               if (exist == null) {
                  (this.map as java.util.Map).put(k, v);
               } else {
                  (this.map as java.util.Map).put(k, v.plus(exist));
               }
            }
         }
      }
   }

   public fun updateFrom(hf: AbstractHeapFactory<Any>, that: IData<Any>) {
      if (that !is HeapKVData) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else {
         if (this.unreferenced == null) {
            if ((that as HeapKVData).getUnreferenced() != null) {
               val var10001: IHeapValues = (that as HeapKVData).getUnreferenced();
               this.unreferenced = var10001.builder();
            }
         } else if ((that as HeapKVData).getUnreferenced() != null) {
            val var10000: IHeapValues.Builder = this.unreferenced;
            val var9: IHeapValues = (that as HeapKVData).getUnreferenced();
            var10000.add(var9);
         }

         if (this.map != (that as HeapKVData).getMap()) {
            val var8: PersistentMap = (that as HeapKVData).getMap();

            for (Entry var4 : ((java.util.Map)var8).entrySet()) {
               (this.map as java.util.Map).put(var4.getKey(), var4.getValue() as IHeapValues);
            }
         }
      }
   }

   public override fun toString(): String {
      return this.build().toString();
   }

   public override fun cloneAndReNewObjects(re: IReNew<Any>) {
      for (Entry var4 : ((java.util.Map)this.map.build()).entrySet()) {
         val k: Any = var4.getKey();
         (this.map as java.util.Map).put(k, (var4.getValue() as IHeapValues).cloneAndReNewObjects(re.context(new ReferenceContext.KVPosition(k))));
      }

      if (this.unreferenced != null) {
         this.unreferenced.cloneAndReNewObjects(re.context(ReferenceContext.KVUnreferenced.INSTANCE));
      }
   }

   @JvmStatic
   fun `set$lambda$0`(`$update`: IHeapValues): Any {
      return "ignore update is $`$update`";
   }

   @JvmStatic
   fun `logger$lambda$1`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      public final val logger: KLogger
   }
}
