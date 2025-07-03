package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JOperatorHV

internal data class JOperatorHVImpl(
    override val heapFactory: AbstractHeapFactory<IValue>,
    override val env: HeapValuesEnv,
    private val value: IHeapValues<IValue>
) : JOperatorHV<IValue> {
    override val pf: PathFactory<IValue> = heapFactory.pathFactory

    override fun pop(): IHeapValues<IValue> {
        return value
    }

    override fun <K> setKVValue(mt: Any, lhs: CompanionV<IValue>, key: K?): JOperatorHV<IValue> {
        return copy(value = pf.updatePath(env, value, Companion::setKVValue$lambda$0))
    }

    override fun <K> getKVValue(mt: Any, rhs: CompanionV<IValue>, key: K?): JOperatorHV<IValue> {
        return copy(value = pf.updatePath(env, value, Companion::getKVValue$lambda$1))
    }

    override fun assignLocal(lhs: Any, rhsValue: IHeapValues<IValue>): JOperatorHV<IValue> {
        return copy(value = pf.updatePath(env, value, Companion::assignLocal$lambda$2))
    }

    override fun markOfArrayLength(rhs: CompanionV<IValue>): JOperatorHV<IValue> {
        return this
    }

    override fun dataElementCopyToSequenceElement(sourceElement: IHeapValues<IValue>): JOperatorHV<IValue> {
        return this
    }

    private operator fun component3(): IHeapValues<IValue> {
        return value
    }

    override fun toString(): String {
        return "JOperatorHVImpl(heapFactory=$heapFactory, env=$env, value=$value)"
    }

    override fun hashCode(): Int {
        return (heapFactory.hashCode() * 31 + env.hashCode()) * 31 + value.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JOperatorHVImpl) return false
        
        return heapFactory == other.heapFactory &&
               env == other.env &&
               value == other.value
    }

    companion object {
        @JvmStatic
        fun `setKVValue$lambda$0`(`this$0`: JOperatorHVImpl, `$lhs`: CompanionV<*>, `$mt`: Any, `$key`: Any, v: IValue, valuePath: IPath): IPath {
            val var10000 = SetEdgePath.Companion
            val var10001 = `this$0`.env
            val var10002 = `$lhs`.getValue() as IValue
            return SetEdgePath.Companion.v$default(var10000, var10001, var10002, (`$lhs` as PathCompanionV).path, `$mt`, `$key`, v, valuePath, null, 128, null)
        }

        @JvmStatic
        fun `getKVValue$lambda$1`(`this$0`: JOperatorHVImpl, `$rhs`: CompanionV<*>, `$mt`: Any, `$key`: Any, v: IValue, valuePath: IPath): IPath {
            val var10000 = GetEdgePath.Companion
            val var10001 = `this$0`.env
            val var10002 = `$rhs`.getValue() as IValue
            return GetEdgePath.Companion.v$default(var10000, var10001, var10002, (`$rhs` as PathCompanionV).path, `$mt`, `$key`, v, valuePath, null, 128, null)
        }

        @JvmStatic
        fun `assignLocal$lambda$2`(`this$0`: JOperatorHVImpl, `$lhs`: Any, v: IValue, valuePath: IPath): IPath {
            return AssignLocalPath.Companion.v(`this$0`.env, `$lhs`, v, valuePath)
        }
    }
}