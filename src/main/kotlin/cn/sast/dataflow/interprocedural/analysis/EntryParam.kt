package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IValue.Kind
import java.util.LinkedHashMap
import kotlinx.collections.immutable.PersistentList
import soot.SootMethod
import soot.Type

public open class EntryParam(type: Type, method: SootMethod, paramIndex: Int) : IValue, IFieldManager<EntryParam> {
   public open val type: Type
   public final val method: SootMethod
   public final val paramIndex: Int
   private final val fieldObjects: MutableMap<PersistentList<JFieldType>, PhantomField<EntryParam>>

   init {
      this.type = type;
      this.method = method;
      this.paramIndex = paramIndex;
      this.fieldObjects = new LinkedHashMap<>();
   }

   public constructor(method: SootMethod, paramIndex: Int)  {
      val var3: Type = if (paramIndex != -1) method.getParameterType(paramIndex) else method.getDeclaringClass().getType() as Type;
      this(var3, method, paramIndex);
   }

   public override fun getPhantomFieldMap(): MutableMap<PersistentList<JFieldType>, PhantomField<EntryParam>> {
      return this.fieldObjects;
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other == null) {
         return false;
      } else if (other !is EntryParam) {
         return false;
      } else if (!(this.method == (other as EntryParam).method)) {
         return false;
      } else if (this.paramIndex != (other as EntryParam).paramIndex) {
         return false;
      } else {
         return this.getType() == (other as EntryParam).getType();
      }
   }

   public override fun hashCode(): Int {
      return 31 * (31 * (31 * 1 + this.method.hashCode()) + Integer.hashCode(this.paramIndex)) + this.getType().hashCode();
   }

   public override fun typeIsConcrete(): Boolean {
      return false;
   }

   public override fun copy(type: Type): IValue {
      return new EntryParam(type, this.method, this.paramIndex);
   }

   public override fun isNullConstant(): Boolean {
      return false;
   }

   public override fun getKind(): Kind {
      return IValue.Kind.Entry;
   }

   public override fun toString(): String {
      return "param<${this.paramIndex}>_${this.getType()}";
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

   override fun getPhantomField(field: JFieldType): PhantomField<EntryParam> {
      return IFieldManager.DefaultImpls.getPhantomField(this, field);
   }

   override fun getPhantomField(ap: PersistentList<? extends JFieldType>): PhantomField<EntryParam> {
      return IFieldManager.DefaultImpls.getPhantomField(this, ap);
   }
}
