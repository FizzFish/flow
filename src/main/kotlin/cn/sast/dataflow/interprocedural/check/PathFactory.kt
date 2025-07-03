package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues

public abstract class PathFactory<V> {
    public abstract fun setModelData(env: HeapValuesEnv, obj: Any, mt: Any, data: IData<Any>): IData<Any>

    public abstract fun updatePath(
        env: HeapValuesEnv, 
        data: IHeapValues<Any>, 
        newPath: (Any, IPath) -> IPath
    ): IHeapValues<Any>
}