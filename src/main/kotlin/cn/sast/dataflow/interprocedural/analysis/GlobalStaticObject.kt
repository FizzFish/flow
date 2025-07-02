package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IValue.Kind
import java.util.LinkedHashMap
import kotlinx.collections.immutable.PersistentList
import soot.Type

public open class GlobalStaticObject : IValue, IFieldManager<GlobalStaticObject> {
   private final val fieldObjects: MutableMap<PersistentList<JFieldType>, PhantomField<GlobalStaticObject>> = (new LinkedHashMap()) as java.util.Map
   public open val type: Type

   public override fun getPhantomFieldMap(): MutableMap<PersistentList<JFieldType>, PhantomField<GlobalStaticObject>> {
      return this.fieldObjects;
   }

   public override fun toString(): String {
      return "GlobalStaticObject";
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other == null) {
         return false;
      } else {
         return other is GlobalStaticObject;
      }
   }

   public override fun hashCode(): Int {
      return 30864;
   }

   public override fun typeIsConcrete(): Boolean {
      return true;
   }

   public override fun isNullConstant(): Boolean {
      return false;
   }

   public override fun getKind(): Kind {
      return IValue.Kind.Entry;
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

   override fun copy(type: Type): IValue {
      return IValue.DefaultImpls.copy(this, type);
   }

   override fun getPhantomField(field: JFieldType): PhantomField<GlobalStaticObject> {
      return IFieldManager.DefaultImpls.getPhantomField(this, field);
   }

   override fun getPhantomField(ap: PersistentList<? extends JFieldType>): PhantomField<GlobalStaticObject> {
      return IFieldManager.DefaultImpls.getPhantomField(this, ap);
   }
}
