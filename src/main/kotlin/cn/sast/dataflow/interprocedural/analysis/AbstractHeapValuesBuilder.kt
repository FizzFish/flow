package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.util.Printer
import java.util.Map.Entry
import kotlinx.collections.immutable.PersistentMap.Builder

public sealed class AbstractHeapValuesBuilder<V> protected constructor(orig: AbstractHeapValues<Any>, map: Builder<Any, CompanionV<Any>>) :
   IHeapValues.Builder<V> {
   public open val orig: AbstractHeapValues<Any>
   public final val map: Builder<Any, CompanionV<Any>>

   init {
      this.orig = orig;
      this.map = map;
   }

   public override fun isNotEmpty(): Boolean {
      return !(this.map as java.util.Map).isEmpty();
   }

   public override fun isEmpty(): Boolean {
      return this.map.isEmpty();
   }

   public override fun add(elements: IHeapValues<Any>): cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<Any> {
      if (elements is AbstractHeapValues) {
         for (CompanionV e : (AbstractHeapValues)elements) {
            this.add(e);
         }
      }

      return this;
   }

   public override fun add(element: CompanionV<Any>): cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<Any> {
      val k: Any = element.getValue();
      val existV: CompanionV = this.map.get(k) as CompanionV;
      if (existV == null) {
         (this.map as java.util.Map).put(k, element);
      } else {
         (this.map as java.util.Map).put(k, existV.union(element));
      }

      return this;
   }

   public override fun cloneAndReNewObjects(re: IReNew<Any>) {
      for (Entry var4 : ((java.util.Map)this.map.build()).entrySet()) {
         val k: Any = var4.getKey();
         val v: CompanionV = var4.getValue() as CompanionV;
         var var10000: CompanionV = (CompanionV)re.checkNeedReplace(k);
         if (var10000 == null) {
            var10000 = (CompanionV)k;
         }

         var10000 = re.context(new ReferenceContext.ObjectValues(k)).checkNeedReplace(v);
         if (var10000 == null) {
            var10000 = v;
         }

         var newValue: CompanionV = var10000;
         if (!(k == var10000) || var10000 != v) {
            if (!(var10000.getValue() == var10000)) {
               newValue = var10000.copy(var10000);
            }

            (this.map as java.util.Map).put(var10000, newValue);
            if (!(k == var10000)) {
               this.map.remove(k);
            }
         }
      }
   }

   public override fun toString(): String {
      return Printer.Companion.nodes2String(this.map.values());
   }
}
