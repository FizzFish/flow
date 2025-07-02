package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IValue.Kind
import java.util.LinkedHashMap
import kotlinx.collections.immutable.PersistentList
import soot.Type
import soot.Unit

public open class SummaryValue private constructor(type: Type, unit: Unit, special: Any?) : IValue, IFieldManager<SummaryValue> {
   public open val type: Type
   public final val unit: Unit
   public final val special: Any?
   private final val fieldObjects: MutableMap<PersistentList<JFieldType>, PhantomField<SummaryValue>>

   public final var hashCode: Int?
      internal set

   init {
      this.type = type;
      this.unit = unit;
      this.special = special;
      this.fieldObjects = new LinkedHashMap<>();
   }

   public open fun clone(): SummaryValue {
      return new SummaryValue(this.getType(), this.unit, this.special);
   }

   public override fun toString(): String {
      return "Summary_${this.getType()}_${this.unit}_${this.special}_(${this.hashCode()})";
   }

   public override fun typeIsConcrete(): Boolean {
      return false;
   }

   public override fun copy(type: Type): IValue {
      return new SummaryValue(type, this.unit, this.special);
   }

   public override fun isNullConstant(): Boolean {
      return false;
   }

   public override fun getKind(): Kind {
      return IValue.Kind.Summary;
   }

   public override fun getPhantomFieldMap(): MutableMap<PersistentList<JFieldType>, PhantomField<SummaryValue>> {
      return this.fieldObjects;
   }

   public fun hash(): Int {
      return if (!FactValuesKt.getLeastExpr())
         System.identityHashCode(this)
         else
         31 * (31 * (31 * 1 + this.unit.hashCode()) + (if (this.special != null) this.special.hashCode() else 0)) + this.getType().hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (!FactValuesKt.getLeastExpr()) {
         return this === other;
      } else if (this === other) {
         return true;
      } else if (other == null) {
         return false;
      } else if (other !is SummaryValue) {
         return false;
      } else if (this.hashCode != null && (other as SummaryValue).hashCode != null && !(this.hashCode == (other as SummaryValue).hashCode)) {
         return false;
      } else if (!(this.unit == (other as SummaryValue).unit)) {
         return false;
      } else if (!(this.special == (other as SummaryValue).special)) {
         return false;
      } else {
         return this.getType() == (other as SummaryValue).getType();
      }
   }

   public override fun hashCode(): Int {
      var h: Int = this.hashCode;
      if (this.hashCode == null) {
         h = this.hash();
         this.hashCode = h;
      }

      return h;
   }

   override fun isNormal(): Boolean {
      return IValue.DefaultImpls.isNormal(this);
   }

   override fun objectEqual(b: IValue): java.lang.Boolean? {
      return IValue.DefaultImpls.objectEqual(this, b);
   }

   override fun getPhantomField(field: JFieldType): PhantomField<SummaryValue> {
      return IFieldManager.DefaultImpls.getPhantomField(this, field);
   }

   override fun getPhantomField(ap: PersistentList<? extends JFieldType>): PhantomField<SummaryValue> {
      return IFieldManager.DefaultImpls.getPhantomField(this, ap);
   }

   public companion object {
      public fun v(ty: Type, unit: Unit, special: Any?): SummaryValue {
         return new SummaryValue(ty, unit, special, null);
      }
   }
}
