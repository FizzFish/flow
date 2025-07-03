package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder

public class OpCalculator(
    env: HeapValuesEnv,
    cf: AbstractHeapFactory<IValue>,
    vararg ops: IHeapValues<IValue>?
) : CalculatorBase(env, cf), IOpCalculator<IValue> {
    public val ops: Array<out IHeapValues<IValue>?>

    init {
        this.ops = ops
    }

    public override fun resolve(
        fx: (IOpCalculator<IValue>, Builder<IValue>, Array<CompanionV<IValue>>) -> Boolean
    ): IOpCalculator<IValue> {
        val sz: Int = this.ops.size
        var valueArray: Int = 0

        val iter = Array<Int>(sz) { 0 }
        while (valueArray < sz) {
            val currentOp: IHeapValues<IValue>? = this.ops[valueArray]
            if (currentOp == null) {
                return this
            }

            val op: Int = currentOp.getSize()
            if (op == 0) {
                return this
            }

            iter[valueArray] = op
            valueArray++
        }

        val sizeArray: Array<Int> = iter
        var var16: Int = 0

        val counter = Array<Array<CompanionV<IValue>>>(sz) { 
            Array(sizeArray[var16++]) { TODO("FIXME — CompanionV initialization") } 
        }

        val var15: Array<Array<CompanionV<IValue>>> = counter
        var var18: Int = 0

        val var20 = Array<Iterator<*>>(sz) { 
            this.ops[var18++]!!.iterator() 
        }

        val var17: Array<Iterator<*>> = var20
        var var21: Int = 0

        val var23 = Array<Int>(sz) { 0 }

        val var19: Array<Int> = var23
        var var24: Int = 0

        val var26 = Array<CompanionV<IValue>>(sz) { 
            val value = var17[var24].next() as CompanionV<IValue>
            var15[var24][0] = value
            var24++
            value
        }

        val var22: Array<CompanionV<IValue>> = var26

        while (this.getCount() < this.getCalculateLimit()) {
            val var25: Boolean = fx.invoke(this, this.getRes(), var22)
            this.setCount(this.getCount() + 1)
            if (!var25) {
                this.getUnHandle().add(var22)
            }

            var var28: Int = 0

            while (true) {
                if (var19[var28] == sizeArray[var28] - 1) {
                    var19[var28] = 0
                    val var31: CompanionV<IValue> = var15[var28][0]
                    var22[var28] = var31
                    if (++var28 != sz) {
                        continue
                    }
                } else {
                    var19[var28] = var19[var28] + 1
                    val x: Int = var19[var28]
                    var var29: CompanionV<IValue> = var15[var28][x] ?: run {
                        val next = var17[var28].next() as CompanionV<IValue>
                        var15[var28][x] = next
                        next
                    }
                    var22[var28] = var29
                }

                if (var28 == sz) {
                    return this
                }
                break
            }
        }

        return this
    }
}