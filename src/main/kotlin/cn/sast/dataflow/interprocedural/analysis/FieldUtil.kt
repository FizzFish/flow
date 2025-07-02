package cn.sast.dataflow.interprocedural.analysis

import com.feysh.corax.config.api.ClassField
import kotlin.jvm.internal.SourceDebugExtension
import soot.Scene
import soot.SootField
import soot.Type

@SourceDebugExtension(["SMAP\nIFact.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,507:1\n1#2:508\n*E\n"])
public object FieldUtil {
   public inline fun of(field: SootField): JSootFieldType {
      return new JSootFieldType(field);
   }

   public inline fun of(field: ClassField): JFieldNameType? {
      val var10000: java.lang.String = field.getFieldType();
      val var7: Type = if (var10000 != null) Scene.v().getTypeUnsafe(var10000, true) else null;
      val var8: JFieldNameType = new JFieldNameType;
      val var10002: java.lang.String = field.getFieldName();
      var var10003: Type = var7;
      if (var7 == null) {
         var10003 = Scene.v().getObjectType() as Type;
      }

      var8./* $VF: Unable to resugar constructor */<init>(var10002, var10003);
      return var8;
   }

   public inline fun typeOf(field: JFieldType): Type {
      return field.getType();
   }

   public inline fun nameOf(field: JFieldType): String {
      return field.getName();
   }
}
