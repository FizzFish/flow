package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapKVData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.analysis.heapimpl.ArrayHeapBuilder
import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.dataflow.interprocedural.check.ArraySpace
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import com.feysh.corax.config.api.utils.UtilsKt
import kotlin.reflect.KCallable
import kotlinx.collections.immutable.ExtensionsKt
import soot.ArrayType
import soot.G
import soot.IntType
import soot.NullType
import soot.PrimType
import soot.RefType
import soot.Type

public class WSystem : SummaryHandlePackage<IValue> {
   public final val arrayType: ArrayType = ArrayType.v(G.v().soot_ByteType() as Type, 1)

   public override fun ACheckCallAnalysis.register() {
      `$this$register`.evalCall(UtilsKt.getSootSignature(<unrepresentable>.INSTANCE as KCallable<?>), WSystem::register$lambda$1);
      `$this$register`.evalCall(UtilsKt.getSootSignature(<unrepresentable>.INSTANCE as KCallable<?>), WSystem::register$lambda$4);
   }

   @JvmStatic
   fun `register$lambda$1$lambda$0`(`$this_evalCall`: CalleeCBImpl.EvalCall, `$this$resolve`: IOpCalculator, res: IHeapValues.Builder, var3: Array<CompanionV>): Boolean {
      res.add(
         `$this_evalCall`.getHf()
            .push(`$this_evalCall`.getEnv(), `$this_evalCall`.getHf().toConstVal(var3[0].hashCode()))
            .markOfReturnValueOfMethod(`$this_evalCall`)
            .pop()
      );
      return true;
   }

   @JvmStatic
   fun CalleeCBImpl.EvalCall.`register$lambda$1`(): Unit {
      val unop: IOpCalculator = `$this$evalCall`.getHf().resolveOp(`$this$evalCall`.getEnv(), `$this$evalCall`.arg(0));
      unop.resolve(WSystem::register$lambda$1$lambda$0);
      val var4: IntType = G.v().soot_IntType();
      unop.putSummaryIfNotConcrete(var4 as Type, "return");
      `$this$evalCall`.setReturn(unop.getRes().build());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `register$lambda$4$lambda$2`(
      `$this_evalCall`: CalleeCBImpl.EvalCall, `$append`: Boolean, `$this$resolve`: IOpCalculator, res: IHeapValues.Builder, var4: Array<CompanionV>
   ): Boolean {
      val src: CompanionV = var4[0];
      val srcPos: CompanionV = var4[1];
      val dest: CompanionV = var4[2];
      val destPos: CompanionV = var4[3];
      val length: CompanionV = var4[4];
      var var10000: IArrayHeapKV = `$this_evalCall`.getOut().getArray((IValue)src.getValue());
      if (var10000 == null) {
         return false;
      } else {
         val arrSrc: IArrayHeapKV = var10000;
         var10000 = `$this_evalCall`.getOut().getArray((IValue)dest.getValue());
         if (var10000 == null) {
            return false;
         } else {
            val var20: Int = FactValuesKt.getIntValue(srcPos.getValue() as IValue, true);
            if (var20 != null) {
               val intSrcPos: Int = var20;
               val var21: Int = FactValuesKt.getIntValue(destPos.getValue() as IValue, true);
               if (var21 != null) {
                  val intDestPos: Int = var21;
                  val var22: Int = FactValuesKt.getIntValue(length.getValue() as IValue, true);
                  if (var22 != null) {
                     val intLength: Int = var22;
                     if (intLength >= 20) {
                        return false;
                     } else {
                        val b: IHeapKVData.Builder = var10000.builder();

                        for (int i = 0; i < intLength; i++) {
                           val var23: IHeapValues = arrSrc.get(`$this_evalCall`.getHf(), i + intSrcPos);
                           if (var23 == null) {
                              return false;
                           }

                           b.set(
                              `$this_evalCall`.getHf(),
                              `$this_evalCall`.getEnv(),
                              i + intDestPos,
                              `$this_evalCall`.getHf().push(`$this_evalCall`.getEnv(), var23).dataElementCopyToSequenceElement(var23).pop(),
                              `$append`
                           );
                        }

                        `$this_evalCall`.getOut().setValueData(`$this_evalCall`.getEnv(), (IValue)dest.getValue(), BuiltInModelT.Array, b.build());
                        return true;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   @JvmStatic
   fun CalleeCBImpl.EvalCall.`register$lambda$4`(): Unit {
      val srcP: IHeapValues = `$this$evalCall`.arg(0);
      val srcPosP: IHeapValues = `$this$evalCall`.arg(1);
      val destP: IHeapValues = `$this$evalCall`.arg(2);
      val destPosP: IHeapValues = `$this$evalCall`.arg(3);
      val lengthP: IHeapValues = `$this$evalCall`.arg(4);
      val op: IOpCalculator = `$this$evalCall`.getHf().resolveOp(`$this$evalCall`.getEnv(), srcP, srcPosP, destP, destPosP, lengthP);
      val var22: Boolean = !srcP.isSingle() || !srcPosP.isSingle() || !destP.isSingle() || !destPosP.isSingle() || !lengthP.isSingle();
      val orig: IFact = `$this$evalCall`.getOut().build();
      op.resolve(WSystem::register$lambda$4$lambda$2);
      if (!op.isFullySimplified()) {
         val builder: IFact.Builder = orig.builder();
         val multiDst: Boolean = !destP.isSingle();

         for (CompanionV dst : destP) {
            val cb: Type = (dst.getValue() as IValue).getType();
            val var28: ArrayType = cb as? ArrayType;
            if ((cb as? ArrayType) != null) {
               var var29: IArrayHeapKV;
               if (multiDst) {
                  var29 = builder.getArray(dst.getValue());
                  if (var29 == null) {
                     val var30: AbstractHeapFactory = `$this$evalCall`.getHf();
                     val var35: HeapValuesEnv = `$this$evalCall`.getEnv();
                     val var10002: AbstractHeapFactory = `$this$evalCall`.getHf();
                     val var10003: HeapValuesEnv = `$this$evalCall`.getEnv();
                     val var10004: IntType = G.v().soot_IntType();
                     var29 = ArraySpace.Companion
                        .v(
                           `$this$evalCall`.getHf(),
                           `$this$evalCall`.getEnv(),
                           ExtensionsKt.persistentHashMapOf(),
                           `$this$evalCall`.getHf().empty(),
                           var28,
                           var30.push(var35, var10002.newSummaryVal(var10003, var10004 as Type, "arraySize")).popHV()
                        );
                  }
               } else {
                  val var31: AbstractHeapFactory = `$this$evalCall`.getHf();
                  val var36: HeapValuesEnv = `$this$evalCall`.getEnv();
                  val var39: AbstractHeapFactory = `$this$evalCall`.getHf();
                  val var42: HeapValuesEnv = `$this$evalCall`.getEnv();
                  val var45: IntType = G.v().soot_IntType();
                  var29 = ArraySpace.Companion
                     .v(
                        `$this$evalCall`.getHf(),
                        `$this$evalCall`.getEnv(),
                        ExtensionsKt.persistentHashMapOf(),
                        `$this$evalCall`.getHf().empty(),
                        var28,
                        var31.push(var36, var39.newSummaryVal(var42, var45 as Type, "arraySize")).popHV()
                     );
               }

               val var26: IHeapKVData.Builder = var29.builder();
               val var32: ArrayHeapBuilder = var26 as? ArrayHeapBuilder;
               if ((var26 as? ArrayHeapBuilder) != null) {
                  val var24: ArrayHeapBuilder = var32;
                  var32.clearAllIndex();

                  for (CompanionV src : srcP) {
                     var arrSrc: IArrayHeapKV = builder.getArray(var27.getValue());
                     if (arrSrc == null) {
                        val baseType: Type = (var27.getValue() as IValue).getType();
                        if (baseType !is PrimType && baseType !is RefType && baseType !is NullType) {
                           continue;
                        }

                        val var33: AbstractHeapFactory = `$this$evalCall`.getHf();
                        var var37: HeapValuesEnv = `$this$evalCall`.getEnv();
                        var var40: AbstractHeapFactory = `$this$evalCall`.getHf();
                        var var43: HeapValuesEnv = `$this$evalCall`.getEnv();
                        val var46: ArrayType = (var27.getValue() as IValue).getType().makeArrayType();
                        val summary: IHeapValues = var33.push(var37, var40.newSummaryVal(var43, var46 as Type, "arraySize")).popHV();
                        val var34: AbstractHeapFactory = `$this$evalCall`.getHf();
                        var37 = `$this$evalCall`.getEnv();
                        var40 = `$this$evalCall`.getHf();
                        var43 = `$this$evalCall`.getEnv();
                        val var47: IntType = G.v().soot_IntType();
                        arrSrc = ArraySpace.Companion
                           .v(
                              `$this$evalCall`.getHf(),
                              `$this$evalCall`.getEnv(),
                              ExtensionsKt.persistentHashMapOf(),
                              summary,
                              `$this$evalCall`.getHf().getVg().getOBJ_ARRAY_TYPE(),
                              var34.push(var37, var40.newSummaryVal(var43, var47 as Type, "summary")).popHV()
                           );
                        builder.setValueData(`$this$evalCall`.getEnv(), var27.getValue(), BuiltInModelT.Array, arrSrc);
                     }

                     var24.set(`$this$evalCall`.getHf(), `$this$evalCall`.getEnv(), null, arrSrc.getElement(`$this$evalCall`.getHf()), true);
                  }

                  builder.setValueData(`$this$evalCall`.getEnv(), dst.getValue(), BuiltInModelT.Array, var24.build());
               }
            }
         }

         `$this$evalCall`.setOut(builder);
      }

      return Unit.INSTANCE;
   }

   public companion object {
      public fun v(): WSystem {
         return new WSystem();
      }
   }
}
