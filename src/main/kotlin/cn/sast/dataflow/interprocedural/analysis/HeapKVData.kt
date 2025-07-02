package cn.sast.dataflow.interprocedural.analysis

import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.PersistentMap

@SourceDebugExtension(["SMAP\nHeapKVData.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HeapKVData.kt\ncn/sast/dataflow/interprocedural/analysis/HeapKVData\n+ 2 PointsToGraphAbstract.kt\ncn/sast/dataflow/interprocedural/analysis/PointsToGraphAbstractKt\n*L\n1#1,248:1\n380#2,3:249\n*S KotlinDebug\n*F\n+ 1 HeapKVData.kt\ncn/sast/dataflow/interprocedural/analysis/HeapKVData\n*L\n111#1:249,3\n*E\n"])
public abstract class HeapKVData<K, V> : IHeapKVData<K, V> {
   public final val map: PersistentMap<Any, IHeapValues<Any>>
   public final val unreferenced: IHeapValues<Any>?
   private final var hashCode: Int?

   open fun HeapKVData(map: PersistentMap<K, ? extends IHeapValues<V>>, unreferenced: IHeapValues<V>?) {
      this.map = map;
      this.unreferenced = unreferenced;
      if (this.unreferenced != null) {
         this.unreferenced.isNotEmpty();
      }
   }

   public override fun reference(res: MutableCollection<Any>) {
      for (Entry f : ((java.util.Map)this.map).entrySet()) {
         (f.getValue() as IHeapValues).reference(res);
      }

      if (this.unreferenced != null) {
         this.unreferenced.reference(res);
      }
   }

   public override fun diff(cmp: IDiff<Any>, that: IDiffAble<out Any?>) {
      if (that is HeapKVData) {
         for (Object k : CollectionsKt.intersect(this.map.keySet(), ((HeapKVData)that).map.keySet())) {
            var var10000: IHeapValues = (IHeapValues)(this.map as java.util.Map).get(k);
            var10000 = var10000;
            val var10002: Any = ((that as HeapKVData).map as java.util.Map).get(k);
            var10000.diff(cmp, var10002 as IDiffAble<? extends Object>);
         }

         if (this.unreferenced != null && (that as HeapKVData).unreferenced != null) {
            this.unreferenced.diff(cmp, (that as HeapKVData).unreferenced);
         }
      }
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is HeapKVData) {
         return false;
      } else if (this.hashCode() != (other as HeapKVData).hashCode()) {
         return false;
      } else {
         return this.map == (other as HeapKVData).map && this.unreferenced == (other as HeapKVData).unreferenced;
      }
   }

   public override fun computeHash(): Int {
      return 31 * (31 * 1 + this.map.hashCode()) + (if (this.unreferenced != null) this.unreferenced.hashCode() else 0);
   }

   public override fun hashCode(): Int {
      var hash: Int = this.hashCode;
      if (this.hashCode == null) {
         hash = this.computeHash();
         this.hashCode = hash;
      }

      return hash;
   }

   public abstract fun isValidKey(key: Any?): Boolean? {
   }

   public open fun getFromNullKey(hf: IHeapValuesFactory<Any>): IHeapValues<Any> {
      val b: IHeapValues.Builder = hf.emptyBuilder();

      for (Entry item$iv : ((java.util.Map)this.map).entrySet()) {
         b.add(`item$iv`.getValue() as IHeapValues<V>);
      }

      if (this.unreferenced != null) {
         b.add(this.unreferenced);
      }

      return b.build();
   }

   public open fun getValue(hf: IHeapValuesFactory<Any>, key: Any): IHeapValues<Any>? {
      return this.map.get(key) as IHeapValues<V>;
   }

   public override fun get(hf: IHeapValuesFactory<Any>, key: Any?): IHeapValues<Any>? {
      val var10000: IHeapValues;
      if (key != null) {
         val exist: IHeapValues = this.getValue(hf, (K)key);
         var10000 = if (exist != null) (if (this.unreferenced != null) exist.plus(this.unreferenced) else exist) else this.unreferenced;
      } else {
         var10000 = this.getFromNullKey(hf);
      }

      return var10000;
   }

   public open fun ppKey(key: Any): String {
      return key.toString();
   }

   public open fun ppValue(value: IHeapValues<Any>): String {
      return value.toString();
   }

   public override fun toString(): String {
      val sb: StringBuilder = new StringBuilder(this.getName()).append(" ");

      for (Entry var3 : ((java.util.Map)this.map).entrySet()) {
         val k: Any = var3.getKey();
         val v: IHeapValues = var3.getValue() as IHeapValues;
         val value: IHeapValues = if (this.unreferenced == null) v else v.plus(this.unreferenced);
         sb.append(this.ppKey((K)k)).append("->").append(this.ppValue(value)).append(" ; ");
      }

      if (this.map.isEmpty()) {
         sb.append("unreferenced: ${this.unreferenced}");
      }

      val var10000: java.lang.String = sb.toString();
      return var10000;
   }

   public abstract fun getName(): String {
   }
}
