package cn.sast.dataflow.interprocedural.analysis

import soot.G
import soot.RefType
import soot.Type
import soot.dexpler.typing.UntypedIntOrFloatConstant
import soot.dexpler.typing.UntypedLongOrDoubleConstant
import soot.jimple.ClassConstant
import soot.jimple.Constant
import soot.jimple.DoubleConstant
import soot.jimple.FloatConstant
import soot.jimple.IntConstant
import soot.jimple.LongConstant
import soot.jimple.NullConstant
import soot.jimple.StringConstant

public final val leastExpr: Boolean = true

public fun IValue.getBooleanValue(checkType: Boolean = true): Boolean? {
   label36:
   if (checkType && `$this$getBooleanValue`.typeIsConcrete() && !(`$this$getBooleanValue`.getType() == G.v().soot_BooleanType())) {
      return null;
   } else {
      val var3: Constant = if ((`$this$getBooleanValue` as? ConstVal) != null) (`$this$getBooleanValue` as? ConstVal).getV() else null;
      return if ((var3 as? IntConstant) != null) (var3 as? IntConstant).value == 1 else null;
   }
}

@JvmSynthetic
fun `getBooleanValue$default`(var0: IValue, var1: Boolean, var2: Int, var3: Any): java.lang.Boolean {
   if ((var2 and 1) != 0) {
      var1 = true;
   }

   return getBooleanValue(var0, var1);
}

public fun IValue.getIntValue(checkType: Boolean = true): Int? {
   if (checkType && `$this$getIntValue`.typeIsConcrete() && !(`$this$getIntValue`.getType() == G.v().soot_IntType())) {
      return null;
   } else {
      val var2: Constant = if ((`$this$getIntValue` as? ConstVal) != null) (`$this$getIntValue` as? ConstVal).getV() else null;
      return if ((var2 as? IntConstant) != null) (var2 as? IntConstant).value else null;
   }
}

@JvmSynthetic
fun `getIntValue$default`(var0: IValue, var1: Boolean, var2: Int, var3: Any): Int {
   if ((var2 and 1) != 0) {
      var1 = true;
   }

   return getIntValue(var0, var1);
}

public fun IValue.getLongValue(checkType: Boolean = true): Long? {
   if (checkType && `$this$getLongValue`.typeIsConcrete() && !(`$this$getLongValue`.getType() == G.v().soot_LongType())) {
      return null;
   } else {
      val var2: Constant = if ((`$this$getLongValue` as? ConstVal) != null) (`$this$getLongValue` as? ConstVal).getV() else null;
      return if ((var2 as? LongConstant) != null) (var2 as? LongConstant).value else null;
   }
}

@JvmSynthetic
fun `getLongValue$default`(var0: IValue, var1: Boolean, var2: Int, var3: Any): java.lang.Long {
   if ((var2 and 1) != 0) {
      var1 = true;
   }

   return getLongValue(var0, var1);
}

public fun IValue.getByteValue(checkType: Boolean = true): Byte? {
   if (checkType && `$this$getByteValue`.typeIsConcrete() && !(`$this$getByteValue`.getType() == G.v().soot_ByteType())) {
      return null;
   } else {
      val var2: Constant = if ((`$this$getByteValue` as? ConstVal) != null) (`$this$getByteValue` as? ConstVal).getV() else null;
      return if ((var2 as? IntConstant) != null) (byte)(var2 as? IntConstant).value else null;
   }
}

@JvmSynthetic
fun `getByteValue$default`(var0: IValue, var1: Boolean, var2: Int, var3: Any): java.lang.Byte {
   if ((var2 and 1) != 0) {
      var1 = true;
   }

   return getByteValue(var0, var1);
}

public fun IValue.getFloatValue(checkType: Boolean = true): Float? {
   if (checkType && `$this$getFloatValue`.typeIsConcrete() && !(`$this$getFloatValue`.getType() == G.v().soot_FloatType())) {
      return null;
   } else {
      val var2: Constant = if ((`$this$getFloatValue` as? ConstVal) != null) (`$this$getFloatValue` as? ConstVal).getV() else null;
      return if ((var2 as? FloatConstant) != null) (var2 as? FloatConstant).value else null;
   }
}

@JvmSynthetic
fun `getFloatValue$default`(var0: IValue, var1: Boolean, var2: Int, var3: Any): java.lang.Float {
   if ((var2 and 1) != 0) {
      var1 = true;
   }

   return getFloatValue(var0, var1);
}

public fun IValue.getDoubleValue(checkType: Boolean = true): Double? {
   if (checkType && `$this$getDoubleValue`.typeIsConcrete() && !(`$this$getDoubleValue`.getType() == G.v().soot_DoubleType())) {
      return null;
   } else {
      val var2: Constant = if ((`$this$getDoubleValue` as? ConstVal) != null) (`$this$getDoubleValue` as? ConstVal).getV() else null;
      return if ((var2 as? DoubleConstant) != null) (var2 as? DoubleConstant).value else null;
   }
}

@JvmSynthetic
fun `getDoubleValue$default`(var0: IValue, var1: Boolean, var2: Int, var3: Any): java.lang.Double {
   if ((var2 and 1) != 0) {
      var1 = true;
   }

   return getDoubleValue(var0, var1);
}

public fun IValue.getStringValue(checkType: Boolean = true): String? {
   if (checkType && `$this$getStringValue`.typeIsConcrete() && !(`$this$getStringValue`.getType() == RefType.v("java.lang.String"))) {
      return null;
   } else {
      val var2: Constant = if ((`$this$getStringValue` as? ConstVal) != null) (`$this$getStringValue` as? ConstVal).getV() else null;
      return if ((var2 as? StringConstant) != null && (var2 as? StringConstant).value != null) (var2 as? StringConstant).value else null;
   }
}

@JvmSynthetic
fun `getStringValue$default`(var0: IValue, var1: Boolean, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      var1 = true;
   }

   return getStringValue(var0, var1);
}

public fun IValue.getClassValue(checkType: Boolean = true): Type? {
   if (checkType && `$this$getClassValue`.typeIsConcrete() && !(`$this$getClassValue`.getType() == RefType.v("java.lang.Class"))) {
      return null;
   } else {
      val var2: Constant = if ((`$this$getClassValue` as? ConstVal) != null) (`$this$getClassValue` as? ConstVal).getV() else null;
      val var10000: ClassConstant = var2 as? ClassConstant;
      if ((var2 as? ClassConstant) != null) {
         val var3: Type = var10000.toSootType();
         if (var3 != null) {
            return var3;
         }
      }

      return null;
   }
}

@JvmSynthetic
fun `getClassValue$default`(var0: IValue, var1: Boolean, var2: Int, var3: Any): Type {
   if ((var2 and 1) != 0) {
      var1 = true;
   }

   return getClassValue(var0, var1);
}

public inline fun IValue.getAnyValue(res: (Any?) -> Unit) {
   val v: Constant = if ((`$this$getAnyValue` as? ConstVal) != null) (`$this$getAnyValue` as? ConstVal).getV() else null;
   if (v != null) {
      if (v is IntConstant) {
         res.invoke((v as IntConstant).value);
      } else if (v is StringConstant) {
         res.invoke((v as StringConstant).value);
      } else if (v is LongConstant) {
         res.invoke((v as LongConstant).value);
      } else if (v is NullConstant) {
         res.invoke(null);
      } else if (v is DoubleConstant) {
         res.invoke((v as DoubleConstant).value);
      } else if (v is FloatConstant) {
         res.invoke((v as FloatConstant).value);
      } else if (v is ClassConstant) {
         res.invoke((v as ClassConstant).value);
      } else if (v is UntypedIntOrFloatConstant) {
         res.invoke((v as UntypedIntOrFloatConstant).value);
      } else if (v is UntypedLongOrDoubleConstant) {
         res.invoke((v as UntypedLongOrDoubleConstant).value);
      }
   }
}

public fun IValue.isNull(): Boolean? {
   return if (`$this$isNull` is ConstVal) (`$this$isNull` as ConstVal).getV() is NullConstant else null;
}
