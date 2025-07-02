package cn.sast.dataflow.analysis.constant

import soot.Local
import soot.Value
import soot.jimple.*
import java.util.HashMap

class FlowMap(
    private val delegateMap: MutableMap<Local, CPValue> = HashMap()
) {

    operator fun get(local: Local): CPValue =
        delegateMap.computeIfAbsent(local) { CPValue.undef }

    fun put(local: Local, value: CPValue): CPValue? =
        delegateMap.put(local, value)

    fun keySet(): Set<Local> = delegateMap.keys

    fun copyFrom(other: FlowMap) {
        delegateMap.clear()
        delegateMap.putAll(other.delegateMap)
    }

    fun computeValue(value: Value): CPValue {
        return when (value) {
            is Local -> get(value)
            is IntConstant -> CPValue.makeConstant(value.value)
            is BinopExpr -> {
                val op1 = computeValue(value.op1)
                val op2 = computeValue(value.op2)
                if (op1 === CPValue.undef && op2 === CPValue.undef) CPValue.undef
                else if (op1 === CPValue.undef || op2 === CPValue.undef) CPValue.nac
                else if (op1 != CPValue.nac && op2 != CPValue.nac) {
                    try {
                        when (value) {
                            is AddExpr -> CPValue.makeConstant(op1.value!! + op2.value!!)
                            is SubExpr -> CPValue.makeConstant(op1.value!! - op2.value!!)
                            is MulExpr -> CPValue.makeConstant(op1.value!! * op2.value!!)
                            is DivExpr -> CPValue.makeConstant(op1.value!! / op2.value!!)
                            is EqExpr -> CPValue.makeConstant(op1.value!! == op2.value!!)
                            is NeExpr -> CPValue.makeConstant(op1.value!! != op2.value!!)
                            is GeExpr -> CPValue.makeConstant(op1.value!! >= op2.value!!)
                            is GtExpr -> CPValue.makeConstant(op1.value!! > op2.value!!)
                            is LeExpr -> CPValue.makeConstant(op1.value!! <= op2.value!!)
                            is LtExpr -> CPValue.makeConstant(op1.value!! < op2.value!!)
                            else -> CPValue.nac
                        }
                    } catch (e: ArithmeticException) {
                        CPValue.nac
                    }
                } else CPValue.nac
            }

            else -> CPValue.nac
        }
    }

    override fun equals(other: Any?): Boolean =
        other is FlowMap && this.delegateMap == other.delegateMap

    override fun hashCode(): Int = delegateMap.hashCode()

    override fun toString(): String = delegateMap.toString()

    companion object {
        fun meet(map1: FlowMap, map2: FlowMap): FlowMap {
            val result = FlowMap()
            val locals = map1.keySet() + map2.keySet()
            for (local in locals) {
                result.put(local, CPValue.meetValue(map1[local], map2[local]))
            }
            return result
        }
    }
}