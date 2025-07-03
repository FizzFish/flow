package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.ICalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder
import java.util.LinkedHashSet
import soot.RefType
import soot.Type

public abstract class CalculatorBase(
    public final val env: HeapValuesEnv,
    public final val cf: AbstractHeapFactory<IValue>
) : ICalculator<IValue> {
    public final var count: Int = 0
        internal set

    public final val calculateLimit: Int = 24
    public final val unHandle: MutableSet<Any> = LinkedHashSet()

    public open var res: Builder<IValue> = cf.empty().builder()
        internal set

    public override fun putSummaryIfNotConcrete(type: Type, special: Any) {
        if (!this.isFullySimplified()) {
            this.putSummaryValue(type, special)
        }
    }

    public override fun putSummaryValue(type: Type, special: Any) {
        if (type is RefType) {
            this.res.add(this.cf.push(this.env, this.cf.getNullConst()).markOfCantCalcAbstractResultValue().pop())
        }

        this.res.add(this.cf.push(this.env, this.cf.newSummaryVal(this.env, type, special)).markOfCantCalcAbstractResultValue().pop())
    }

    public override fun isFullySimplified(): Boolean {
        return this.count < this.calculateLimit && this.unHandle.isEmpty()
    }
}