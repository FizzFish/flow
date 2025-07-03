package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementHashMap

public class PathFactoryImpl : PathFactory<IValue> {
    public open fun setModelData(env: HeapValuesEnv, obj: IValue, mt: Any, data: IData<IValue>): IData<IValue> {
        return data.cloneAndReNewObjects(object : IReNew<IValue> {
            private val `$data`: IData<IValue> = data
            private val `$env`: HeapValuesEnv = env
            private val `$obj`: IValue = obj
            private val `$mt`: Any = mt

            override fun checkNeedReplace(c: CompanionV<IValue>): CompanionV<IValue>? {
                if (this.`$data` is ImmutableElementHashMap) {
                    val p: ModelBind = ModelBind.Companion.v(this.`$env`, this.`$obj`, this.`$mt`, this.`$data`, (c as PathCompanionV).getPath())
                    if (c is CompanionValueOfConst) {
                        return CompanionValueOfConst((c as CompanionValueOfConst).getValue(), p, (c as CompanionValueOfConst).getAttr())
                    }

                    if (c is CompanionValueImpl1) {
                        return CompanionValueImpl1((c as CompanionValueImpl1).getValue(), p)
                    }
                }

                return null
            }

            override fun checkNeedReplace(old: IValue): IValue {
                return IReNew.DefaultImpls.checkNeedReplace(this, old)
            }

            override fun context(value: Any): IReNew<IValue> {
                return IReNew.DefaultImpls.context(this, value)
            }
        })
    }

    public override fun updatePath(env: HeapValuesEnv, data: IHeapValues<IValue>, newPath: (IValue, IPath) -> IPath): IHeapValues<IValue> {
        return data.cloneAndReNewObjects(object : IReNew<IValue> {
            private val `$newPath`: (IValue, IPath) -> IPath = newPath

            override fun checkNeedReplace(c: CompanionV<IValue>): CompanionV<IValue>? {
                val p: IPath = this.`$newPath`.invoke(c.getValue(), (c as PathCompanionV).getPath())
                if (c is CompanionValueOfConst) {
                    return CompanionValueOfConst((c as CompanionValueOfConst).getValue(), p, (c as CompanionValueOfConst).getAttr())
                } else {
                    return CompanionValueImpl1((c as CompanionValueImpl1).getValue(), p)
                }
            }

            override fun checkNeedReplace(old: IValue): IValue {
                return IReNew.DefaultImpls.checkNeedReplace(this, old)
            }

            override fun context(value: Any): IReNew<IValue> {
                return IReNew.DefaultImpls.context(this, value)
            }
        })
    }
}