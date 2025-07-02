package cn.sast.dataflow.interprocedural.override.lang.util

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IDiff
import cn.sast.dataflow.interprocedural.analysis.IDiffAble
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentList

@SourceDebugExtension(["SMAP\nWArrayList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WArrayList.kt\ncn/sast/dataflow/interprocedural/override/lang/util/ListSpace\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,344:1\n1#2:345\n1863#3,2:346\n*S KotlinDebug\n*F\n+ 1 WArrayList.kt\ncn/sast/dataflow/interprocedural/override/lang/util/ListSpace\n*L\n219#1:346,2\n*E\n"])
public data class ListSpace(list: PersistentList<IHeapValues<IValue>> = ExtensionsKt.persistentListOf(), unreferenced: IHeapValues<IValue>? = null) :
   IData<IValue> {
   public final val list: PersistentList<IHeapValues<IValue>>
   public final val unreferenced: IHeapValues<IValue>?
   private final var hashCode: Int?

   init {
      this.list = list;
      this.unreferenced = unreferenced;
      if (this.unreferenced != null && !this.unreferenced.isNotEmpty()) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      }
   }

   public override fun reference(res: MutableCollection<IValue>) {
      for (IHeapValues e : this.list) {
         e.reference(res);
      }
   }

   public open fun builder(): ListSpaceBuilder {
      return new ListSpaceBuilder(this.list.builder(), if (this.unreferenced != null) this.unreferenced.builder() else null);
   }

   public override fun computeHash(): Int {
      return 31 * (31 * 1 + this.list.hashCode()) + (if (this.unreferenced != null) this.unreferenced.hashCode() else 0) + 1231;
   }

   public override fun diff(cmp: IDiff<IValue>, that: IDiffAble<out Any?>) {
      if (this != that) {
         if (that is ListSpace) {
            val var3: java.util.Iterator = (this.list as java.lang.Iterable).iterator();
            var var4: Int = 0;

            while (var3.hasNext()) {
               val it: Int = var4++;
               val var6: IHeapValues = var3.next() as IHeapValues;
               if (it >= (that as ListSpace).list.size()) {
                  break;
               }

               var6.diff(cmp, (that as ListSpace).list.get(it) as IDiffAble<? extends Object>);
            }

            if ((that as ListSpace).unreferenced != null) {
               if (this.unreferenced != null) {
                  this.unreferenced.diff(cmp, (that as ListSpace).unreferenced);
               }
            }
         }
      }
   }

   public override fun hashCode(): Int {
      var hash: Int = this.hashCode;
      if (this.hashCode == null) {
         hash = this.computeHash();
         this.hashCode = hash;
      }

      return hash;
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ListSpace) {
         return false;
      } else if (this.hashCode() != (other as ListSpace).hashCode()) {
         return false;
      } else {
         return this.list == (other as ListSpace).list && this.unreferenced == (other as ListSpace).unreferenced;
      }
   }

   public fun getAllElement(hf: AbstractHeapFactory<IValue>): IHeapValues<IValue> {
      val res: IHeapValues.Builder = hf.emptyBuilder();

      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         res.add(var6 as IHeapValues);
      }

      if (this.unreferenced != null) {
         res.add(this.unreferenced);
      }

      return res.build();
   }

   public operator fun get(hf: AbstractHeapFactory<IValue>, index: Int?): IHeapValues<IValue>? {
      if (index != null && this.unreferenced == null) {
         return if (index < this.list.size() && index >= 0) this.list.get(index) as IHeapValues else null;
      } else {
         return this.getAllElement(hf);
      }
   }

   public override fun cloneAndReNewObjects(re: IReNew<IValue>): IData<IValue> {
      val b: ListSpaceBuilder = this.builder();
      b.cloneAndReNewObjects(re);
      return b.build();
   }

   public operator fun component1(): PersistentList<IHeapValues<IValue>> {
      return this.list;
   }

   public operator fun component2(): IHeapValues<IValue>? {
      return this.unreferenced;
   }

   public fun copy(list: PersistentList<IHeapValues<IValue>> = this.list, unreferenced: IHeapValues<IValue>? = this.unreferenced): ListSpace {
      return new ListSpace(list, unreferenced);
   }

   public override fun toString(): String {
      return "ListSpace(list=${this.list}, unreferenced=${this.unreferenced})";
   }

   fun ListSpace() {
      this(null, null, 3, null);
   }
}
