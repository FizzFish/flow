package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import cn.sast.dataflow.interprocedural.check.HeapFactoryKt
import cn.sast.dataflow.interprocedural.check.callback.ICallCBImpl
import cn.sast.dataflow.interprocedural.check.checker.CheckerModelingKt
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementHashMap
import cn.sast.dataflow.util.SootUtilsKt
import soot.ArrayType
import soot.ByteType
import soot.G
import soot.SootField
import soot.Type
import soot.jimple.Constant
import soot.jimple.StringConstant

public fun ICallCBImpl<IHeapValues<IValue>, Builder<IValue>>.getByteArray(strValueObject: IValue): ByteArray? {
   val var2: IData = (`$this$getByteArray`.getOut() as IFact.Builder).getValueData(strValueObject, BuiltInModelT.Array);
   return if ((var2 as? IArrayHeapKV) != null) (var2 as? IArrayHeapKV).getByteArray(`$this$getByteArray`.getHf()) else null;
}

public fun ICallCBImpl<IHeapValues<IValue>, Builder<IValue>>.getStringFromObject(obj: IHeapValues<IValue>): IOpCalculator<IValue> {
   val byteType: ByteType = G.v().soot_ByteType();
   val arrayType: ArrayType = ArrayType.v(byteType as Type, 1);
   val coderField: SootField = SootUtilsKt.getOrMakeField("java.lang.String", "coder", byteType as Type);
   val c: IOpCalculator = `$this$getStringFromObject`.getHf()
      .resolveOp(
         `$this$getStringFromObject`.getEnv(),
         HeapFactoryKt.getValueField(`$this$getStringFromObject`, obj, SootUtilsKt.getOrMakeField("java.lang.String", "value", arrayType as Type)),
         HeapFactoryKt.getValueField(`$this$getStringFromObject`, obj, coderField)
      );
   c.resolve(WStringKt::getStringFromObject$lambda$0);
   val var11: IOpCalculator = `$this$getStringFromObject`.getHf().resolveOp(`$this$getStringFromObject`.getEnv(), obj);
   var11.resolve(WStringKt::getStringFromObject$lambda$2);
   c.getRes().add(var11.getRes().build());
   c.getRes().add(obj);
   return c;
}

fun `getStringFromObject$lambda$0`(`$this_getStringFromObject`: ICallCBImpl, `$this$getStr`: IOpCalculator, res: IHeapValues.Builder, var3: Array<CompanionV>): Boolean {
   val var4: CompanionV = var3[0];
   val var10000: java.lang.Byte = FactValuesKt.getByteValue(var3[1].getValue() as IValue, true);
   if (var10000 != null) {
      val coderInt: Byte = var10000;
      val var9: ByteArray = getByteArray(`$this_getStringFromObject`, var4.getValue() as IValue);
      if (var9 == null) {
         return false;
      } else {
         val str: java.lang.String = if (coderInt == WString.Companion.getLATIN1_BYTE())
            new java.lang.String(var9, Charsets.UTF_8)
            else
            new java.lang.String(var9, Charsets.UTF_16);
         val var10001: AbstractHeapFactory = `$this_getStringFromObject`.getHf();
         val var10002: HeapValuesEnv = `$this_getStringFromObject`.getEnv();
         val var10003: AbstractHeapFactory = `$this_getStringFromObject`.getHf();
         val var10004: StringConstant = StringConstant.v(str);
         res.add(
            var10001.push(var10002, var10003.newConstVal(var10004 as Constant, `$this_getStringFromObject`.getHf().getVg().getSTRING_TYPE() as Type))
               .dataSequenceToSeq(var4)
               .popHV()
         );
         return true;
      }
   } else {
      return false;
   }
}

fun `getStringFromObject$lambda$2`(`$this_getStringFromObject`: ICallCBImpl, `$this$getStr`: IOpCalculator, res: IHeapValues.Builder, var3: Array<CompanionV>): Boolean {
   val set: IData = (`$this_getStringFromObject`.getOut() as IFact.Builder).getValueData(var3[0].getValue(), CheckerModelingKt.getKeyAttribute());
   val attributeMap: ImmutableElementHashMap = set as? ImmutableElementHashMap;
   val var11: IHeapValues.Builder = `$this_getStringFromObject`.getHf().emptyBuilder();
   if (attributeMap != null) {
      val var10000: IHeapValues = attributeMap.get(`$this_getStringFromObject`.getHf(), "str-fragment");
      if (var10000 != null) {
         val it: IHeapValues = var10000;

         for (CompanionV v : it) {
            if (v.getValue() is ConstVal) {
               var11.add(it);
            }
         }
      }
   }

   res.add(var11.build());
   return true;
}
