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

public open class PointsToGraph(
    hf: AbstractHeapFactory<IValue>,
    vg: IVGlobal,
    callStack: CallStackContext,
    slots: PersistentMap<Any, IHeapValues<IValue>>,
    heap: PersistentMap<IValue, PersistentMap<Any, IData<IValue>>>,
    calledMethods: PersistentSet<SootMethod>
) : PointsToGraphAbstract<IValue>(hf, vg, callStack, slots, heap, calledMethods) {
    public override fun builder(): Builder<IValue> {
        return PointsToGraphBuilder(
            this as PointsToGraphAbstract<IValue>,
            this.getHf(),
            this.getVg(),
            this.getCallStack(),
            this.getSlots().builder(),
            this.getHeap().builder(),
            this.getCalledMethods().builder()
        )
    }

    public override fun hasChange(
        context: Context<SootMethod, Unit, IFact<IValue>>,
        new: IProblemIteratorTerminal<IValue>
    ): FixPointStatus {
        if (new !is IFact<*>) {
            throw IllegalArgumentException("Failed requirement.")
        } else if (!new.isValid()) {
            return FixPointStatus.HasChange
        } else if (new !is PointsToGraphAbstract<*>) {
            throw IllegalArgumentException("Failed requirement.")
        } else if (this === new) {
            return FixPointStatus.Fixpoint
        } else {
            val oldSlots = this.getSlots()
            if (oldSlots != (new as PointsToGraphAbstract<*>).getSlots()) {
                return FixPointStatus.HasChange
            } else {
                var needWiden = false

                for (local in oldSlots) {
                    val oldTarget = this.getTargets(local)
                    val newTarget = (new as PointsToGraphAbstract<*>).getTargets(local)
                    val valueOld = oldTarget.getValues() as Collection<*>
                    val valueNew = newTarget.getValues() as Collection<*>
                    val var10000 = valueOld.firstOrNull() as? IValue
                    if ((var10000?.type) is PrimType) {
                        val hashSet = IntegerInterval.invoke(valueOld)
                        val change = IntegerInterval.invoke(valueNew)
                        if (hashSet.widening != change.widening) {
                            return FixPointStatus.HasChange
                        }

                        if (hashSet.widening) {
                            continue
                        }

                        val max = ExtSettings.INSTANCE.getDataFlowIteratorIsFixPointSizeLimit()
                        if (max > 0 && (change.constants.size > max || hashSet.constants.size > max)) {
                            needWiden = true
                        }
                    }

                    val var17 = TCustomHashSet(FactValuesHashingStrategy.INSTANCE)
                    var17.addAll(valueOld)
                    if (var17.addAll(valueNew)) {
                        if (needWiden) {
                            return FixPointStatus.NeedWideningOperators
                        }

                        return FixPointStatus.HasChange
                    }
                }

                return FixPointStatus.Fixpoint
            }
        }
    }

    private class IntegerInterval(
        val constants: Collection<ConstVal>,
        val widening: Boolean
    ) {
        companion object {
            @SourceDebugExtension(["SMAP\nPointsToGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/PointsToGraph$IntegerInterval$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,612:1\n808#2,11:613\n1755#2,3:624\n*S KotlinDebug\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/PointsToGraph$IntegerInterval$Companion\n*L\n63#1:613,11\n63#1:624,3\n*E\n"])
            operator fun invoke(numbers: Collection<IValue>): IntegerInterval {
                val element = ArrayList<ConstVal>()

                for (elementIv in numbers) {
                    if (elementIv is ConstVal) {
                        element.add(elementIv)
                    }
                }

                val var10000 = element as List<ConstVal>
                val var18 = if (numbers.isEmpty()) {
                    false
                } else {
                    numbers.any { elementIvx ->
                        (elementIvx as? SummaryValue)?.special == WideningPrimitive.INSTANCE
                    }
                }

                return IntegerInterval(var10000, var18)
            }
        }
    }
}