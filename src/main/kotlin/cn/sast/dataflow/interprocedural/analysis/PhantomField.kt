package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IValue.Kind
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.PersistentList
import soot.Type

@SourceDebugExtension(["SMAP\nFactValues.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FactValues.kt\ncn/sast/dataflow/interprocedural/analysis/PhantomField\n+ 2 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n*L\n1#1,589:1\n49#2:590\n50#2:591\n*S KotlinDebug\n*F\n+ 1 FactValues.kt\ncn/sast/dataflow/interprocedural/analysis/PhantomField\n*L\n295#1:590\n298#1:591\n*E\n"])
public open class PhantomField<V extends IValue>(type: Type, base: IFieldManager<Any>, accessPath: PersistentList<JFieldType>) : IValue {
   public open val type: Type
   public final val base: IFieldManager<Any>
   public final val accessPath: PersistentList<JFieldType>

   init {
      this.type = type;
      this.base = base;
      this.accessPath = accessPath;
   }

   public constructor(base: IFieldManager<Any>, accessPath: PersistentList<JFieldType>)  {
      val `this_$iv`: FieldUtil = FieldUtil.INSTANCE;
      this((CollectionsKt.last(accessPath as java.util.List) as JFieldType).getType(), base, accessPath);
   }

   public override fun toString(): String {
      return "PhantomObject_${this.getType()}_${this.base}.${CollectionsKt.joinToString$default(
         this.accessPath as java.lang.Iterable, null, null, null, 0, null, PhantomField::toString$lambda$0, 31, null
      )}";
   }

   public override fun typeIsConcrete(): Boolean {
      return false;
   }

   public override fun copy(type: Type): IValue {
      return new PhantomField<>(type, this.base, this.accessPath);
   }

   public override fun isNullConstant(): Boolean {
      return false;
   }

   public override fun getKind(): Kind {
      return IValue.Kind.Summary;
   }

   public fun getPhantomField(field: JFieldType): PhantomField<Any> {
      return this.base.getPhantomField(this.accessPath.add(field));
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other == null) {
         return false;
      } else if (other !is PhantomField) {
         return false;
      } else if (!(this.base == (other as PhantomField).base)) {
         return false;
      } else if (!(this.accessPath == (other as PhantomField).accessPath)) {
         return false;
      } else {
         return this.getType() == (other as PhantomField).getType();
      }
   }

   public override fun hashCode(): Int {
      return 31 * (31 * (31 * 1 + this.base.hashCode()) + this.accessPath.hashCode()) + this.getType().hashCode();
   }

   override fun isNormal(): Boolean {
      return IValue.DefaultImpls.isNormal(this);
   }

   override fun objectEqual(b: IValue): java.lang.Boolean? {
      return IValue.DefaultImpls.objectEqual(this, b);
   }

   override fun clone(): IValue {
      return IValue.DefaultImpls.clone(this);
   }

   @JvmStatic
   fun `toString$lambda$0`(it: JFieldType): java.lang.CharSequence {
      val `this_$iv`: FieldUtil = FieldUtil.INSTANCE;
      return it.getName();
   }
}
