package cn.sast.dataflow.interprocedural.check

import cn.sast.api.config.ExtSettings
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CallStackContext
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FactValuesHashingStrategy
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IProblemIteratorTerminal
import cn.sast.dataflow.interprocedural.analysis.IVGlobal
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.PointsToGraphAbstract
import cn.sast.dataflow.interprocedural.analysis.SummaryValue
import cn.sast.dataflow.interprocedural.analysis.WideningPrimitive
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.idfa.analysis.Context
import cn.sast.idfa.analysis.FixPointStatus
import gnu.trove.set.hash.TCustomHashSet
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import soot.PrimType
import soot.SootMethod
import soot.Unit

public open class PointsToGraph(hf: AbstractHeapFactory<IValue>,
   vg: IVGlobal,
   callStack: CallStackContext,
   slots: PersistentMap<Any, IHeapValues<IValue>>,
   heap: PersistentMap<IValue, PersistentMap<Any, IData<IValue>>>,
   calledMethods: PersistentSet<SootMethod>
) : PointsToGraphAbstract(hf, vg, callStack, slots, heap, calledMethods) {
   public override fun builder(): Builder<IValue> {
      return new PointsToGraphBuilder(
         this as PointsToGraphAbstract<IValue>,
         this.getHf(),
         this.getVg(),
         this.getCallStack(),
         this.getSlots().builder(),
         this.getHeap().builder(),
         this.getCalledMethods().builder()
      );
   }

   public override fun hasChange(context: Context<SootMethod, Unit, IFact<IValue>>, new: IProblemIteratorTerminal<IValue>): FixPointStatus {
      if (var2 !is IFact) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else if (!(var2 as IFact).isValid()) {
         return FixPointStatus.HasChange;
      } else if (var2 !is PointsToGraphAbstract) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else if (this === var2) {
         return FixPointStatus.Fixpoint;
      } else {
         val oldSlots: java.util.Set = this.getSlots();
         if (!(oldSlots == (var2 as PointsToGraphAbstract).getSlots())) {
            return FixPointStatus.HasChange;
         } else {
            var needWiden: Boolean = false;

            for (Object local : oldSlots) {
               val oldTarget: IHeapValues = this.getTargets(local);
               val newTarget: IHeapValues = (var2 as PointsToGraphAbstract).getTargets(local);
               val valueOld: java.util.Collection = oldTarget.getValues() as java.util.Collection;
               val valueNew: java.util.Collection = newTarget.getValues() as java.util.Collection;
               val var10000: IValue = CollectionsKt.firstOrNull(valueOld) as IValue;
               if ((if (var10000 != null) var10000.getType() else null) is PrimType) {
                  val hashSet: PointsToGraph.IntegerInterval = PointsToGraph.IntegerInterval.Companion.invoke(valueOld);
                  val change: PointsToGraph.IntegerInterval = PointsToGraph.IntegerInterval.Companion.invoke(valueNew);
                  if (hashSet.getWidening() != change.getWidening()) {
                     return FixPointStatus.HasChange;
                  }

                  if (hashSet.getWidening()) {
                     continue;
                  }

                  val max: Int = ExtSettings.INSTANCE.getDataFlowIteratorIsFixPointSizeLimit();
                  if (max > 0 && (change.getConstants().size() > max || hashSet.getConstants().size() > max)) {
                     needWiden = true;
                  }
               }

               val var17: TCustomHashSet = new TCustomHashSet(FactValuesHashingStrategy.Companion.getINSTANCE());
               var17.addAll(valueOld);
               if (var17.addAll(valueNew)) {
                  if (needWiden) {
                     return FixPointStatus.NeedWideningOperators;
                  }

                  return FixPointStatus.HasChange;
               }
            }

            return FixPointStatus.Fixpoint;
         }
      }
   }

   private class IntegerInterval(constants: Collection<ConstVal>, widening: Boolean) {
      public final val constants: Collection<ConstVal>
      public final val widening: Boolean

      init {
         this.constants = constants;
         this.widening = widening;
      }

      @SourceDebugExtension(["SMAP\nPointsToGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/PointsToGraph$IntegerInterval$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,612:1\n808#2,11:613\n1755#2,3:624\n*S KotlinDebug\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/PointsToGraph$IntegerInterval$Companion\n*L\n63#1:613,11\n63#1:624,3\n*E\n"])
      public companion object {
         public operator fun invoke(numbers: Collection<IValue>): cn.sast.dataflow.interprocedural.check.PointsToGraph.IntegerInterval {
            var `$this$any$iv`: java.lang.Iterable = numbers;
            val `element$iv`: java.util.Collection = new ArrayList();

            for (Object element$iv$iv : $this$filterIsInstance$iv) {
               if (`element$iv$iv` is ConstVal) {
                  `element$iv`.add(`element$iv$iv`);
               }
            }

            val var10000: java.util.Collection = `element$iv` as java.util.List;
            `$this$any$iv` = numbers;
            var var18: Boolean;
            if ((numbers as java.util.Collection).isEmpty()) {
               var18 = false;
            } else {
               label44: {
                  for (Object element$ivx : $this$filterIsInstance$iv) {
                     val var16: IValue = `element$ivx` as IValue;
                     if ((if ((`element$ivx` as IValue as? SummaryValue) != null) (`element$ivx` as IValue as? SummaryValue).getSpecial() else null)
                        == WideningPrimitive.INSTANCE) {
                        var18 = true;
                        break label44;
                     }
                  }

                  var18 = false;
               }
            }

            return new PointsToGraph.IntegerInterval(var10000, var18);
         }
      }
   }
}
