package cn.sast.dataflow.interprocedural.override.lang.util

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.ReferenceContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.PersistentList.Builder

@SourceDebugExtension(["SMAP\nWArrayList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WArrayList.kt\ncn/sast/dataflow/interprocedural/override/lang/util/ListSpaceBuilder\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,344:1\n1863#2,2:345\n1#3:347\n*S KotlinDebug\n*F\n+ 1 WArrayList.kt\ncn/sast/dataflow/interprocedural/override/lang/util/ListSpaceBuilder\n*L\n281#1:345,2\n*E\n"])
public data class ListSpaceBuilder(list: Builder<IHeapValues<IValue>>, unreferenced: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<IValue>?) :
   IData.Builder<IValue> {
   public final val list: Builder<IHeapValues<IValue>>
   private final var unreferenced: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<IValue>?

   init {
      this.list = list;
      this.unreferenced = unreferenced;
   }

   public override fun union(hf: AbstractHeapFactory<IValue>, that: IData<IValue>) {
      if (that !is ListSpace) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else if (this.unreferenced != null) {
         if ((that as ListSpace).getUnreferenced() != null) {
            val var10: IHeapValues.Builder = this.unreferenced;
            var10.add((that as ListSpace).getUnreferenced());
         } else {
            val var11: IHeapValues.Builder = this.unreferenced;
            var11.add((that as ListSpace).getAllElement(hf));
         }
      } else if ((that as ListSpace).getUnreferenced() != null) {
         this.unreferenced = (that as ListSpace).getUnreferenced().builder();
         val var10000: IHeapValues.Builder = this.unreferenced;
         var10000.add(this.getAllElement(hf));
         this.list.clear();
      } else if (this.list.size() != (that as ListSpace).getList().size()) {
         val var8: IHeapValues.Builder = this.getAllElement(hf).builder();
         var8.add((that as ListSpace).getAllElement(hf));
         this.unreferenced = var8;
         this.list.clear();
      } else {
         val unreferenced: java.util.Iterator = ((that as ListSpace).getList() as java.lang.Iterable).iterator();
         var var4: Int = 0;

         while (unreferenced.hasNext()) {
            val i: Int = var4++;
            this.list.set(i, (this.list.get(i) as IHeapValues).plus(unreferenced.next() as IHeapValues));
         }
      }
   }

   private fun getAllElement(hf: AbstractHeapFactory<IValue>): IHeapValues<IValue> {
      val res: IHeapValues.Builder = hf.emptyBuilder();

      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         res.add(var6 as IHeapValues);
      }

      if (this.unreferenced != null) {
         res.add(this.unreferenced.build());
      }

      return res.build();
   }

   public override fun cloneAndReNewObjects(re: IReNew<IValue>) {
      val var3: java.util.Iterator = (this.list.build() as java.lang.Iterable).iterator();
      var var4: Int = 0;

      while (var3.hasNext()) {
         val k: Int = var4++;
         this.list.set(k, (var3.next() as IHeapValues).cloneAndReNewObjects(re.context(new ReferenceContext.KVPosition(k))));
      }

      if (this.unreferenced != null) {
         this.unreferenced.cloneAndReNewObjects(re.context(ReferenceContext.KVUnreferenced.INSTANCE));
      }
   }

   public override fun build(): IData<IValue> {
      var var4: IHeapValues.Builder = this.unreferenced;
      if (this.unreferenced != null) {
         if (this.unreferenced.isEmpty()) {
            var4 = null;
         }
      }

      return new ListSpace(this.list.build(), if (var4 != null) var4.build() else null);
   }

   public fun add(value: IHeapValues<IValue>) {
      if (this.unreferenced != null) {
         val var10000: IHeapValues.Builder = this.unreferenced;
         var10000.add(value);
      } else {
         this.list.add(value);
      }
   }

   public fun clear(value: IHeapValues<IValue>) {
      this.unreferenced = null;
      this.list.clear();
   }

   public fun addAll(hf: AbstractHeapFactory<IValue>, b: ListSpace) {
      if (this.unreferenced == null && b.getUnreferenced() == null) {
         this.list.addAll(b.getList() as java.util.Collection);
      } else {
         this.union(hf, b);
      }
   }

   public fun remove(hf: AbstractHeapFactory<IValue>, index: Int?): IHeapValues<IValue> {
      if (index == null) {
         if (this.unreferenced == null) {
            this.unreferenced = hf.emptyBuilder();
         }

         var var10000: IHeapValues.Builder = this.unreferenced;
         var10000.add(this.getAllElement(hf));
         this.list.clear();
         var10000 = this.unreferenced;
         return var10000.build();
      } else if (this.unreferenced == null) {
         return if (index < this.list.size() && index >= 0) this.list.remove(index) as IHeapValues else this.getAllElement(hf);
      } else {
         return this.getAllElement(hf);
      }
   }

   public operator fun component1(): Builder<IHeapValues<IValue>> {
      return this.list;
   }

   private operator fun component2(): cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<IValue>? {
      return this.unreferenced;
   }

   public fun copy(
      list: Builder<IHeapValues<IValue>> = this.list,
      unreferenced: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<IValue>? = this.unreferenced
   ): ListSpaceBuilder {
      return new ListSpaceBuilder(list, unreferenced);
   }

   public override fun toString(): String {
      return "ListSpaceBuilder(list=${this.list}, unreferenced=${this.unreferenced})";
   }

   public override fun hashCode(): Int {
      return this.list.hashCode() * 31 + (if (this.unreferenced == null) 0 else this.unreferenced.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ListSpaceBuilder) {
         return false;
      } else {
         val var2: ListSpaceBuilder = other as ListSpaceBuilder;
         if (!(this.list == (other as ListSpaceBuilder).list)) {
            return false;
         } else {
            return this.unreferenced == var2.unreferenced;
         }
      }
   }
}
