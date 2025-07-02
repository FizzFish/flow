package cn.sast.dataflow.interprocedural.override.lang.util

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.check.ArraySpace
import cn.sast.dataflow.interprocedural.check.ArraySpaceBuilder
import cn.sast.dataflow.interprocedural.check.OverrideModel
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import soot.G
import soot.IntType
import soot.Type

@SourceDebugExtension(["SMAP\nWHashMap.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WHashMap.kt\ncn/sast/dataflow/interprocedural/override/lang/util/WHashMap\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,182:1\n1863#2,2:183\n*S KotlinDebug\n*F\n+ 1 WHashMap.kt\ncn/sast/dataflow/interprocedural/override/lang/util/WHashMap\n*L\n49#1:183,2\n*E\n"])
public class WHashMap : SummaryHandlePackage<IValue> {
   public override fun ACheckCallAnalysis.register() {
      val var20: java.lang.Iterable;
      for (Object element$iv : var20) {
         `$this$register`.evalCallAtCaller(`element$iv` as java.lang.String, WHashMap::register$lambda$2$lambda$1);
      }

      `$this$register`.evalCallAtCaller("<java.util.HashMap: void clear()>", WHashMap::register$lambda$4);
      `$this$register`.evalCallAtCaller("<java.util.HashMap: java.lang.Object get(java.lang.Object)>", WHashMap::register$lambda$6);
      `$this$register`.evalCallAtCaller("<java.util.HashMap: java.lang.Object getOrDefault(java.lang.Object,java.lang.Object)>", WHashMap::register$lambda$8);
      `$this$register`.evalCall("<java.util.HashMap: java.lang.Object put(java.lang.Object,java.lang.Object)>", WHashMap::register$lambda$10);
   }

   @JvmStatic
   fun `register$mapGetModel`(`$this_register`: ACheckCallAnalysis, mapData: IData<IValue>, key: IValue): IHeapValues<IValue> {
      if (FactValuesKt.isNull(key) == true) {
      }

      if (key.getType() == `$this_register`.getHf().getVg().getSTRING_TYPE()) {
         val var10000: java.lang.String = FactValuesKt.getStringValue(key, false);
         if (var10000 == null) {
            return null;
         } else {
            return (mapData as ArraySpace).get(`$this_register`.getHf(), Math.abs(var10000.hashCode()));
         }
      } else {
         return null;
      }
   }

   @JvmStatic
   fun `register$lambda$2$lambda$1$lambda$0`(
      `$this_evalCallAtCaller`: CallerSiteCBImpl.EvalCall, `$this$resolve`: IOpCalculator, var2: IHeapValues.Builder, var3: Array<CompanionV>
   ): Boolean {
      val self: CompanionV = var3[0];
      val var10000: AbstractHeapFactory = `$this_evalCallAtCaller`.getHf();
      val var10001: HeapValuesEnv = `$this_evalCallAtCaller`.getEnv();
      val var10002: AbstractHeapFactory = `$this_evalCallAtCaller`.getHf();
      val var10003: HeapValuesEnv = `$this_evalCallAtCaller`.getEnv();
      val var10004: IntType = G.v().soot_IntType();
      `$this_evalCallAtCaller`.getOut()
         .setValueData(
            `$this_evalCallAtCaller`.getEnv(),
            (IValue)self.getValue(),
            OverrideModel.HashMap,
            ArraySpace.Companion
               .v(
                  `$this_evalCallAtCaller`.getHf(),
                  `$this_evalCallAtCaller`.getEnv(),
                  ExtensionsKt.persistentHashMapOf(),
                  `$this_evalCallAtCaller`.getHf().empty(),
                  `$this_evalCallAtCaller`.getHf().getVg().getOBJ_ARRAY_TYPE(),
                  var10000.push(var10001, var10002.newSummaryVal(var10003, var10004 as Type, "mapSize")).popHV()
               )
         );
      return true;
   }

   @JvmStatic
   fun CallerSiteCBImpl.EvalCall.`register$lambda$2$lambda$1`(): Unit {
      `$this$evalCallAtCaller`.getHf()
         .resolveOp(`$this$evalCallAtCaller`.getEnv(), `$this$evalCallAtCaller`.arg(-1))
         .resolve(WHashMap::register$lambda$2$lambda$1$lambda$0);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `register$lambda$4$lambda$3`(
      `$this_evalCallAtCaller`: CallerSiteCBImpl.EvalCall, `$this$resolve`: IOpCalculator, var2: IHeapValues.Builder, var3: Array<CompanionV>
   ): Boolean {
      val self: CompanionV = var3[0];
      val var10000: AbstractHeapFactory = `$this_evalCallAtCaller`.getHf();
      val var10001: HeapValuesEnv = `$this_evalCallAtCaller`.getEnv();
      val var10002: AbstractHeapFactory = `$this_evalCallAtCaller`.getHf();
      val var10003: HeapValuesEnv = `$this_evalCallAtCaller`.getEnv();
      val var10004: IntType = G.v().soot_IntType();
      `$this_evalCallAtCaller`.getOut()
         .setValueData(
            `$this_evalCallAtCaller`.getEnv(),
            (IValue)self.getValue(),
            OverrideModel.HashMap,
            ArraySpace.Companion
               .v(
                  `$this_evalCallAtCaller`.getHf(),
                  `$this_evalCallAtCaller`.getEnv(),
                  ExtensionsKt.persistentHashMapOf(),
                  `$this_evalCallAtCaller`.getHf().empty(),
                  `$this_evalCallAtCaller`.getHf().getVg().getOBJ_ARRAY_TYPE(),
                  var10000.push(var10001, var10002.newSummaryVal(var10003, var10004 as Type, "mapSize")).popHV()
               )
         );
      return true;
   }

   @JvmStatic
   fun CallerSiteCBImpl.EvalCall.`register$lambda$4`(): Unit {
      val self: IHeapValues = `$this$evalCallAtCaller`.getThis();
      if (!self.isSingle()) {
         `$this$evalCallAtCaller`.setEvalAble(false);
         return Unit.INSTANCE;
      } else {
         `$this$evalCallAtCaller`.getHf().resolveOp(`$this$evalCallAtCaller`.getEnv(), self).resolve(WHashMap::register$lambda$4$lambda$3);
         return Unit.INSTANCE;
      }
   }

   @JvmStatic
   fun `register$lambda$6$lambda$5`(
      `$this_evalCallAtCaller`: CallerSiteCBImpl.EvalCall,
      `$this_register`: ACheckCallAnalysis,
      `$this$get`: IOpCalculator,
      res: IHeapValues.Builder,
      var4: Array<CompanionV>
   ): Boolean {
      val map: CompanionV = var4[0];
      val key: CompanionV = var4[1];
      val var10000: IData = `$this_evalCallAtCaller`.getOut().getValueData((IValue)map.getValue(), OverrideModel.HashMap);
      if (var10000 == null) {
         return false;
      } else {
         val var10001: IHeapValues = register$mapGetModel(`$this_register`, var10000, key.getValue() as IValue);
         if (var10001 == null) {
            return false;
         } else {
            res.add(var10001);
            return true;
         }
      }
   }

   @JvmStatic
   fun `register$lambda$6`(`$this_register`: ACheckCallAnalysis, `$this$evalCallAtCaller`: CallerSiteCBImpl.EvalCall): Unit {
      val map: IHeapValues = `$this$evalCallAtCaller`.getThis();
      val calculator: IOpCalculator = `$this$evalCallAtCaller`.getHf().resolveOp(`$this$evalCallAtCaller`.getEnv(), map, `$this$evalCallAtCaller`.arg(0));
      if (!map.isSingle()) {
         `$this$evalCallAtCaller`.setEvalAble(false);
         return Unit.INSTANCE;
      } else {
         calculator.resolve(WHashMap::register$lambda$6$lambda$5);
         if (calculator.isFullySimplified() && !calculator.getRes().isEmpty()) {
            `$this$evalCallAtCaller`.setReturn(calculator.getRes().build());
            return Unit.INSTANCE;
         } else {
            `$this$evalCallAtCaller`.setEvalAble(false);
            return Unit.INSTANCE;
         }
      }
   }

   @JvmStatic
   fun `register$lambda$8$lambda$7`(
      `$this_evalCallAtCaller`: CallerSiteCBImpl.EvalCall,
      `$this_register`: ACheckCallAnalysis,
      `$this$get`: IOpCalculator,
      res: IHeapValues.Builder,
      var4: Array<CompanionV>
   ): Boolean {
      val map: CompanionV = var4[0];
      val key: CompanionV = var4[1];
      val defaultValue: CompanionV = var4[2];
      val var10000: IData = `$this_evalCallAtCaller`.getOut().getValueData((IValue)map.getValue(), OverrideModel.HashMap);
      if (var10000 == null) {
         return false;
      } else {
         var var10001: IHeapValues = register$mapGetModel(`$this_register`, var10000, key.getValue() as IValue);
         if (var10001 == null) {
            var10001 = `$this_evalCallAtCaller`.getHf().single(defaultValue);
         }

         res.add(var10001);
         return true;
      }
   }

   @JvmStatic
   fun `register$lambda$8`(`$this_register`: ACheckCallAnalysis, `$this$evalCallAtCaller`: CallerSiteCBImpl.EvalCall): Unit {
      val map: IHeapValues = `$this$evalCallAtCaller`.getThis();
      if (!map.isSingle()) {
         `$this$evalCallAtCaller`.setEvalAble(false);
         return Unit.INSTANCE;
      } else {
         val calculator: IOpCalculator = `$this$evalCallAtCaller`.getHf()
            .resolveOp(`$this$evalCallAtCaller`.getEnv(), map, `$this$evalCallAtCaller`.arg(0), `$this$evalCallAtCaller`.arg(1));
         calculator.resolve(WHashMap::register$lambda$8$lambda$7);
         if (calculator.isFullySimplified() && !calculator.getRes().isEmpty()) {
            `$this$evalCallAtCaller`.setReturn(calculator.getRes().build());
            return Unit.INSTANCE;
         } else {
            `$this$evalCallAtCaller`.setEvalAble(false);
            return Unit.INSTANCE;
         }
      }
   }

   @JvmStatic
   fun `register$lambda$10$lambda$9`(
      `$this_evalCall`: CalleeCBImpl.EvalCall, `$value`: IHeapValues, `$this$put`: IOpCalculator, res: IHeapValues.Builder, var4: Array<CompanionV>
   ): Boolean {
      val map: CompanionV = var4[0];
      val key: CompanionV = var4[1];
      val var10000: IData = `$this_evalCall`.getOut().getValueData((IValue)map.getValue(), OverrideModel.HashMap);
      if (var10000 == null) {
         return false;
      } else {
         val builder: ArraySpaceBuilder = (var10000 as ArraySpace).builder();
         if ((key.getValue() as IValue).getType() == `$this_evalCall`.getHf().getVg().getSTRING_TYPE()) {
            val keyStr: java.lang.String = FactValuesKt.getStringValue(key.getValue() as IValue, false);
            if (keyStr == null) {
               builder.set(`$this_evalCall`.getHf(), `$this_evalCall`.getEnv(), null, `$value`, true);
            } else {
               builder.set(`$this_evalCall`.getHf(), `$this_evalCall`.getEnv(), Math.abs(keyStr.hashCode()), `$value`, true);
            }

            `$this_evalCall`.getOut().setValueData(`$this_evalCall`.getEnv(), (IValue)map.getValue(), OverrideModel.HashMap, builder.build());
            res.add((var10000 as ArraySpace).getElement(`$this_evalCall`.getHf()));
            return true;
         } else {
            builder.set(`$this_evalCall`.getHf(), `$this_evalCall`.getEnv(), null, `$value`, true);
            return true;
         }
      }
   }

   @JvmStatic
   fun CalleeCBImpl.EvalCall.`register$lambda$10`(): Unit {
      val map: IHeapValues = `$this$evalCall`.getThis();
      if (!map.isSingle()) {
         `$this$evalCall`.setEvalAble(false);
         return Unit.INSTANCE;
      } else {
         val key: IHeapValues = `$this$evalCall`.arg(0);
         val value: IHeapValues = `$this$evalCall`.arg(1);
         val calculator: IOpCalculator = `$this$evalCall`.getHf().resolveOp(`$this$evalCall`.getEnv(), map, key);
         calculator.resolve(WHashMap::register$lambda$10$lambda$9);
         if (!calculator.isFullySimplified()) {
            `$this$evalCall`.setEvalAble(false);
            return Unit.INSTANCE;
         } else {
            `$this$evalCall`.setReturn(calculator.getRes().build());
            return Unit.INSTANCE;
         }
      }
   }

   public companion object {
      public fun v(): WHashMap {
         return new WHashMap();
      }
   }
}
