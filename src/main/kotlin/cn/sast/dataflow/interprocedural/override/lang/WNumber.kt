package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.FieldUtil
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JSootFieldType
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl.EvalCall
import cn.sast.dataflow.util.SootUtilsKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import soot.ByteType
import soot.DoubleType
import soot.FloatType
import soot.G
import soot.IntType
import soot.IntegerType
import soot.LongType
import soot.PrimType
import soot.RefType
import soot.ShortType
import soot.SootField
import soot.Type
import soot.jimple.AnyNewExpr
import soot.jimple.Constant
import soot.jimple.Jimple
import soot.jimple.NewExpr
import soot.jimple.NumericConstant
import soot.jimple.StringConstant

@SourceDebugExtension(["SMAP\nWNumber.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WNumber.kt\ncn/sast/dataflow/interprocedural/override/lang/WNumber\n+ 2 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n*L\n1#1,263:1\n44#2:264\n44#2:265\n44#2:266\n*S KotlinDebug\n*F\n+ 1 WNumber.kt\ncn/sast/dataflow/interprocedural/override/lang/WNumber\n*L\n62#1:264\n146#1:265\n178#1:266\n*E\n"])
public class WNumber : SummaryHandlePackage<IValue> {
   private fun ACheckCallAnalysis.registerToValue(clzName: String, valueFieldType: PrimType, valueField: SootField, toType: PrimType) {
      val handle: Function1 = WNumber::registerToValue$lambda$1;
      `$this$registerToValue`.evalCall("<$clzName: $toType $toTypeValue()>", handle);
      val var10003: java.lang.String = toType.toString();
      `$this$registerToValue`.evalCall("<$clzName: $toType to${StringsKt.capitalize(var10003)}()>", handle);
   }

   public fun EvalCall.parseString(hint: String, sIdx: Int, radixIdx: Int, resType: PrimType): IOpCalculator<IValue> {
      val valueOp: IOpCalculator = `$this$parseString`.getHf()
         .resolveOp(`$this$parseString`.getEnv(), `$this$parseString`.arg(sIdx), `$this$parseString`.arg(radixIdx));
      valueOp.resolve(WNumber::parseString$lambda$2);
      valueOp.putSummaryIfNotConcrete(resType as Type, "return");
      return valueOp;
   }

   public fun EvalCall.parseStringFloating(hint: String, sIdx: Int, resType: PrimType): IOpCalculator<IValue> {
      val valueOp: IOpCalculator = `$this$parseStringFloating`.getHf().resolveOp(`$this$parseStringFloating`.getEnv(), `$this$parseStringFloating`.arg(sIdx));
      valueOp.resolve(WNumber::parseStringFloating$lambda$3);
      valueOp.putSummaryIfNotConcrete(resType as Type, "return");
      return valueOp;
   }

   public fun ACheckCallAnalysis.registerValueOf(clzName: String, valueFieldType: PrimType) {
      val valueField: SootField = SootUtilsKt.getOrMakeField(clzName, "value", valueFieldType as Type);
      val newExpr: NewExpr = Jimple.v().newNewExpr(RefType.v(clzName));
      if (valueFieldType !is IntegerType && valueFieldType !is LongType && valueFieldType !is FloatType && valueFieldType !is DoubleType) {
         throw new IllegalStateException(("error type of $valueFieldType").toString());
      } else {
         `$this$registerValueOf`.evalCall("<$clzName: $clzName valueOf($valueFieldType)>", WNumber::registerValueOf$lambda$4);
         `$this$registerValueOf`.registerWrapper("<$clzName: $clzName valueOf(java.lang.String)>", true);
         var var10003: java.lang.String = valueFieldType.toString();
         `$this$registerValueOf`.registerWrapper("<$clzName: $valueFieldType parse${StringsKt.capitalize(var10003)}(java.lang.String)>", true);
         if (valueFieldType !is FloatType && valueFieldType !is DoubleType) {
            var10003 = valueFieldType.toString();
            `$this$registerValueOf`.evalCall(
               "<$clzName: $valueFieldType parse${StringsKt.capitalize(var10003)}(java.lang.String,int)>", WNumber::registerValueOf$lambda$6
            );
            `$this$registerValueOf`.evalCall("<$clzName: $clzName valueOf(java.lang.String,int)>", WNumber::registerValueOf$lambda$7);
         } else {
            var10003 = valueFieldType.toString();
            `$this$registerValueOf`.evalCall(
               "<$clzName: $valueFieldType parse${StringsKt.capitalize(var10003)}(java.lang.String)>", WNumber::registerValueOf$lambda$5
            );
         }
      }
   }

   public fun ACheckCallAnalysis.registerEquals(clzName: String, valueField: SootField) {
      val classType: RefType = RefType.v(clzName);
      `$this$registerEquals`.registerWrapper("<$clzName: boolean equals(java.lang.Object)>", false);
   }

   public override fun ACheckCallAnalysis.register() {
      for (Pair var4 : CollectionsKt.listOf(
         new Pair[]{
            TuplesKt.to("java.lang.Integer", G.v().soot_IntType()),
            TuplesKt.to("java.lang.Long", G.v().soot_LongType()),
            TuplesKt.to("java.lang.Short", G.v().soot_ShortType()),
            TuplesKt.to("java.lang.Byte", G.v().soot_ByteType()),
            TuplesKt.to("java.lang.Float", G.v().soot_FloatType()),
            TuplesKt.to("java.lang.Double", G.v().soot_DoubleType())
         }
      )) {
         val c: java.lang.String = var4.component1() as java.lang.String;
         val valueOfType: PrimType = var4.component2() as PrimType;
         `$this$register`.registerClassAllWrapper(c);
         val valueField: SootField = SootUtilsKt.getOrMakeField(c, "value", valueOfType as Type);
         val var10005: ByteType = G.v().soot_ByteType();
         this.registerToValue(`$this$register`, c, valueOfType, valueField, var10005 as PrimType);
         val var9: ShortType = G.v().soot_ShortType();
         this.registerToValue(`$this$register`, c, valueOfType, valueField, var9 as PrimType);
         val var10: IntType = G.v().soot_IntType();
         this.registerToValue(`$this$register`, c, valueOfType, valueField, var10 as PrimType);
         val var11: LongType = G.v().soot_LongType();
         this.registerToValue(`$this$register`, c, valueOfType, valueField, var11 as PrimType);
         val var12: FloatType = G.v().soot_FloatType();
         this.registerToValue(`$this$register`, c, valueOfType, valueField, var12 as PrimType);
         val var13: DoubleType = G.v().soot_DoubleType();
         this.registerToValue(`$this$register`, c, valueOfType, valueField, var13 as PrimType);
         this.registerValueOf(`$this$register`, c, valueOfType);
         this.registerEquals(`$this$register`, c, valueField);
      }
   }

   @JvmStatic
   fun `registerToValue$lambda$1$lambda$0`(
      `$toType`: PrimType, `$this`: CalleeCBImpl.EvalCall, `$this$resolve`: IOpCalculator, res: IHeapValues.Builder, var4: Array<CompanionV>
   ): Boolean {
      val op: IValue = var4[0].getValue() as IValue;
      val var9: Constant = if ((op as? ConstVal) != null) (op as? ConstVal).getV() else null;
      val var10000: NumericConstant = var9 as? NumericConstant;
      if ((var9 as? NumericConstant) == null) {
         return false;
      } else {
         val var10: Constant = cn.sast.api.util.SootUtilsKt.castTo(var10000, `$toType` as Type);
         if (var10 == null) {
            return false;
         } else {
            res.add(`$this`.getHf().push(`$this`.getEnv(), `$this`.getHf().newConstVal(var10, `$toType` as Type)).markOfCastTo(`$toType`).pop());
            return true;
         }
      }
   }

   @JvmStatic
   fun `registerToValue$lambda$1`(`$valueField`: SootField, `$toType`: PrimType, var2: CalleeCBImpl.EvalCall): Unit {
      val var10000: IFact.Builder = var2.getOut();
      val var10001: HeapValuesEnv = var2.getEnv();
      val var10003: Int = -1;
      val value: FieldUtil = FieldUtil.INSTANCE;
      IFact.Builder.DefaultImpls.getField$default(var10000, var10001, "value", var10003, new JSootFieldType(`$valueField`), false, 16, null);
      val var7: IOpCalculator = var2.getHf().resolveOp(var2.getEnv(), var2.getOut().getTargets("value"));
      var7.resolve(WNumber::registerToValue$lambda$1$lambda$0);
      var7.putSummaryIfNotConcrete(`$toType` as Type, var2.getHf().getVg().getRETURN_LOCAL());
      IFact.Builder.DefaultImpls.assignNewExpr$default(
         var2.getOut(), var2.getEnv(), var2.getHf().getVg().getRETURN_LOCAL(), var7.getRes().build(), false, 8, null
      );
      var2.getOut().kill("value");
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `parseString$lambda$2`(
      `$this_parseString`: CalleeCBImpl.EvalCall,
      `$resType`: PrimType,
      `$hint`: java.lang.String,
      `$this$resolve`: IOpCalculator,
      res: IHeapValues.Builder,
      var5: Array<CompanionV>
   ): Boolean {
      val cop: CompanionV = var5[0];
      val cradix: CompanionV = var5[1];
      val str: IValue = cop.getValue() as IValue;
      val var10000: Int = FactValuesKt.getIntValue(cradix.getValue() as IValue, true);
      if (var10000 == null) {
         return false;
      } else {
         val radixNm: Int = var10000;
         val var18: java.lang.String;
         if (str is ConstVal) {
            val var13: Constant = (str as ConstVal).getV();
            val var17: StringConstant = var13 as? StringConstant;
            if ((var13 as? StringConstant) == null) {
               return false;
            }

            var18 = var17.value;
            if (var17.value == null) {
               return false;
            }
         } else {
            val var19: ByteArray = WStringKt.getByteArray(`$this_parseString`, str);
            if (var19 == null) {
               return false;
            }

            var18 = StringsKt.decodeToString(var19);
         }

         val sc: java.lang.String = var18;

         var var14: NumericConstant;
         try {
            var14 = cn.sast.api.util.SootUtilsKt.cvtNumericConstant(sc, radixNm, `$resType` as Type);
         } catch (var16: NumberFormatException) {
            return false;
         }

         if (var14 == null) {
            return false;
         } else {
            res.add(
               `$this_parseString`.getHf()
                  .push(`$this_parseString`.getEnv(), `$this_parseString`.getHf().newConstVal(var14 as Constant, `$resType` as Type))
                  .markOfParseString(`$hint`, cop)
                  .pop()
            );
            return true;
         }
      }
   }

   @JvmStatic
   fun `parseStringFloating$lambda$3`(
      `$this_parseStringFloating`: CalleeCBImpl.EvalCall,
      `$resType`: PrimType,
      `$hint`: java.lang.String,
      `$this$resolve`: IOpCalculator,
      res: IHeapValues.Builder,
      var5: Array<CompanionV>
   ): Boolean {
      val cop: CompanionV = var5[0];
      val str: IValue = var5[0].getValue() as IValue;
      val var14: java.lang.String;
      if (str is ConstVal) {
         val var10: Constant = (str as ConstVal).getV();
         val var10000: StringConstant = var10 as? StringConstant;
         if ((var10 as? StringConstant) == null) {
            return false;
         }

         var14 = var10000.value;
         if (var10000.value == null) {
            return false;
         }
      } else {
         val var15: ByteArray = WStringKt.getByteArray(`$this_parseStringFloating`, str);
         if (var15 == null) {
            return false;
         }

         var14 = StringsKt.decodeToString(var15);
      }

      val sc: java.lang.String = var14;

      var var11: NumericConstant;
      try {
         var11 = cn.sast.api.util.SootUtilsKt.cvtNumericConstant(sc, -1, `$resType` as Type);
      } catch (var13: NumberFormatException) {
         return false;
      }

      if (var11 == null) {
         return false;
      } else {
         res.add(
            `$this_parseStringFloating`.getHf()
               .push(`$this_parseStringFloating`.getEnv(), `$this_parseStringFloating`.getHf().newConstVal(var11 as Constant, `$resType` as Type))
               .markOfParseString(`$hint`, cop)
               .pop()
         );
         return true;
      }
   }

   @JvmStatic
   fun `registerValueOf$lambda$4`(`$newExpr`: NewExpr, `$valueField`: SootField, `$this$evalCall`: CalleeCBImpl.EvalCall): Unit {
      val var10000: AbstractHeapFactory = `$this$evalCall`.getHf();
      var var10001: HeapValuesEnv = `$this$evalCall`.getEnv();
      val var10002: AbstractHeapFactory = `$this$evalCall`.getHf();
      val var10003: AnyNewExprEnv = `$this$evalCall`.getNewEnv();
      IFact.Builder.DefaultImpls.assignNewExpr$default(
         `$this$evalCall`.getOut(),
         `$this$evalCall`.getEnv(),
         `$this$evalCall`.getHf().getVg().getRETURN_LOCAL(),
         var10000.push(var10001, var10002.anyNewVal(var10003, `$newExpr` as AnyNewExpr)).markOfNewExpr(`$newExpr` as AnyNewExpr).popHV(),
         false,
         8,
         null
      );
      val var6: IFact.Builder = `$this$evalCall`.getOut();
      var10001 = `$this$evalCall`.getEnv();
      val var8: java.lang.String = `$this$evalCall`.getHf().getVg().getRETURN_LOCAL();
      val `this_$iv`: FieldUtil = FieldUtil.INSTANCE;
      IFact.Builder.DefaultImpls.setField$default(var6, var10001, var8, new JSootFieldType(`$valueField`), 0, false, 16, null);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `registerValueOf$lambda$5`(`this$0`: WNumber, `$clzName`: java.lang.String, `$valueFieldType`: PrimType, `$this$evalCall`: CalleeCBImpl.EvalCall): Unit {
      IFact.Builder.DefaultImpls.assignNewExpr$default(
         `$this$evalCall`.getOut(),
         `$this$evalCall`.getEnv(),
         `$this$evalCall`.getHf().getVg().getRETURN_LOCAL(),
         `this$0`.parseStringFloating(`$this$evalCall`, `$clzName`, 0, `$valueFieldType`).getRes().build(),
         false,
         8,
         null
      );
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `registerValueOf$lambda$6`(`this$0`: WNumber, `$clzName`: java.lang.String, `$valueFieldType`: PrimType, `$this$evalCall`: CalleeCBImpl.EvalCall): Unit {
      IFact.Builder.DefaultImpls.assignNewExpr$default(
         `$this$evalCall`.getOut(),
         `$this$evalCall`.getEnv(),
         `$this$evalCall`.getHf().getVg().getRETURN_LOCAL(),
         `this$0`.parseString(`$this$evalCall`, `$clzName`, 0, 1, `$valueFieldType`).getRes().build(),
         false,
         8,
         null
      );
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `registerValueOf$lambda$7`(
      `$newExpr`: NewExpr,
      `this$0`: WNumber,
      `$clzName`: java.lang.String,
      `$valueFieldType`: PrimType,
      `$valueField`: SootField,
      `$this$evalCall`: CalleeCBImpl.EvalCall
   ): Unit {
      val var10000: AbstractHeapFactory = `$this$evalCall`.getHf();
      var var10001: HeapValuesEnv = `$this$evalCall`.getEnv();
      val var10002: AbstractHeapFactory = `$this$evalCall`.getHf();
      val var10003: AnyNewExprEnv = `$this$evalCall`.getNewEnv();
      IFact.Builder.DefaultImpls.assignNewExpr$default(
         `$this$evalCall`.getOut(),
         `$this$evalCall`.getEnv(),
         `$this$evalCall`.getHf().getVg().getRETURN_LOCAL(),
         var10000.push(var10001, var10002.anyNewVal(var10003, `$newExpr` as AnyNewExpr)).markOfNewExpr(`$newExpr` as AnyNewExpr).popHV(),
         false,
         8,
         null
      );
      val eval: IOpCalculator = `this$0`.parseString(`$this$evalCall`, `$clzName`, 0, 1, `$valueFieldType`);
      val var10: IFact.Builder = `$this$evalCall`.getOut();
      var10001 = `$this$evalCall`.getEnv();
      val var12: java.lang.String = `$this$evalCall`.getHf().getVg().getRETURN_LOCAL();
      val `this_$iv`: FieldUtil = FieldUtil.INSTANCE;
      var10.setFieldNew(var10001, var12, new JSootFieldType(`$valueField`), eval.getRes().build());
      return Unit.INSTANCE;
   }

   public companion object {
      public fun v(): WNumber {
         return new WNumber();
      }
   }
}
