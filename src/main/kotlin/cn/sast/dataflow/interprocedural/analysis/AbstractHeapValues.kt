package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder
import cn.sast.dataflow.util.Printer
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.PersistentMap
import soot.jimple.Constant
import soot.jimple.IntConstant

@SourceDebugExtension(["SMAP\nPointsToGraphAbstract.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PointsToGraphAbstract.kt\ncn/sast/dataflow/interprocedural/analysis/AbstractHeapValues\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,1045:1\n1#2:1046\n1#2:1049\n1619#3:1047\n1863#3:1048\n1864#3:1050\n1620#3:1051\n*S KotlinDebug\n*F\n+ 1 PointsToGraphAbstract.kt\ncn/sast/dataflow/interprocedural/analysis/AbstractHeapValues\n*L\n251#1:1049\n251#1:1047\n251#1:1048\n251#1:1050\n251#1:1051\n*E\n"])
public sealed class AbstractHeapValues<V> protected constructor(map: PersistentMap<Any, CompanionV<Any>> = ExtensionsKt.persistentHashMapOf()) : IHeapValues<V> {
   public final val map: PersistentMap<Any, CompanionV<Any>>

   public final var hashCode: Int?
      internal set

   public open val single: CompanionV<Any>
      public open get() {
         if (!this.isSingle()) {
            throw new IllegalArgumentException(("error size of $this").toString());
         } else {
            return this.iterator().next();
         }
      }


   public open val size: Int
      public open get() {
         return this.map.size();
      }


   public open val values: ImmutableSet<Any>
      public open get() {
         return this.map.keySet() as ImmutableSet<V>;
      }


   public open val valuesCompanion: ImmutableSet<CompanionV<Any>>
      public open get() {
         val r: java.util.Set = new LinkedHashSet();

         for (CompanionV e : this) {
            r.add(e);
         }

         return ExtensionsKt.toImmutableSet(r);
      }


   init {
      this.map = map;
   }

   public fun computeHash(): Int {
      return 31 * 1 + this.map.hashCode();
   }

   public override fun hashCode(): Int {
      var h: Int = this.hashCode;
      if (this.hashCode == null) {
         h = this.computeHash();
         this.hashCode = h;
      }

      return h;
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is AbstractHeapValues) {
         return false;
      } else if (this.hashCode() != (other as AbstractHeapValues).hashCode()) {
         return false;
      } else {
         return this.map == (other as AbstractHeapValues).map;
      }
   }

   public override fun diff(cmp: IDiff<Any>, that: IDiffAble<out Any?>) {
      if (that is AbstractHeapValues) {
         for (Object k : CollectionsKt.intersect(this.map.keySet(), ((AbstractHeapValues)that).map.keySet())) {
            var var10001: Any = (this.map as java.util.Map).get(k);
            var10001 = var10001 as CompanionV;
            val var10002: Any = ((that as AbstractHeapValues).map as java.util.Map).get(k);
            cmp.diff((CompanionV<V>)var10001, var10002 as CompanionV<? extends Object>);
         }
      }
   }

   public override fun reference(res: MutableCollection<Any>) {
      res.addAll(this.map.keySet());
   }

   public override fun isNotEmpty(): Boolean {
      return !(this.map as java.util.Map).isEmpty();
   }

   public override fun isEmpty(): Boolean {
      return this.map.isEmpty();
   }

   public override fun isSingle(): Boolean {
      return this.map.size() == 1;
   }

   public override operator fun plus(rhs: IHeapValues<Any>): IHeapValues<Any> {
      if (rhs.isEmpty()) {
         return this;
      } else if (this.isEmpty()) {
         return rhs;
      } else {
         val var2: IHeapValues.Builder = this.builder();
         var2.add(rhs);
         return var2.build();
      }
   }

   public override operator fun plus(rhs: CompanionV<Any>): IHeapValues<Any> {
      val var2: IHeapValues.Builder = this.builder();
      var2.add(rhs);
      return var2.build();
   }

   public override fun map(c: Builder<Any>, transform: (CompanionV<Any>) -> CompanionV<Any>) {
      for (CompanionV e : this) {
         c.add(transform.invoke(e) as CompanionV<V>);
      }
   }

   public override fun flatMap(c: Builder<Any>, transform: (CompanionV<Any>) -> Collection<CompanionV<Any>>) {
      for (CompanionV e : this) {
         for (CompanionV t : (java.util.Collection)transform.invoke(e)) {
            c.add(t);
         }
      }
   }

   public override fun getAllIntValue(must: Boolean): MutableSet<Int>? {
      val `$this$mapNotNullTo$iv`: java.lang.Iterable = this.map.keySet();
      val `destination$iv`: java.util.Collection = new LinkedHashSet();

      for (Object element$iv$iv : $this$mapNotNullTo$iv) {
         val var13: Constant = if ((`element$iv$iv` as? ConstVal) != null) (`element$iv$iv` as? ConstVal).getV() else null;
         val num: Int = if ((var13 as? IntConstant) != null) (var13 as? IntConstant).value else null;
         if (must && num == null) {
            return null;
         }

         if (num != null) {
            `destination$iv`.add(num);
         }
      }

      return `destination$iv` as MutableSet<Int>;
   }

   public override operator fun iterator(): Iterator<CompanionV<Any>> {
      val mi: java.util.Iterator = (this.map as java.util.Map).entrySet().iterator();
      return new java.util.Iterator<CompanionV<V>>(mi) {
         {
            this.$mi = `$mi`;
         }

         @Override
         public boolean hasNext() {
            return this.$mi.hasNext();
         }

         public CompanionV<V> next() {
            return this.$mi.next().getValue();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }
      };
   }

   public override fun cloneAndReNewObjects(re: IReNew<Any>): IHeapValues<Any> {
      val b: IHeapValues.Builder = this.builder();
      b.cloneAndReNewObjects(re);
      return b.build();
   }

   public override fun toString(): String {
      return Printer.Companion.nodes2String(this.map.values());
   }

   override fun getMaxInt(must: Boolean): Int? {
      return IHeapValues.DefaultImpls.getMaxInt(this, must);
   }
}
