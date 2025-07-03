package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import soot.Unit
import soot.jimple.internal.JNopStmt

internal class HeapValuesEnvImpl(node: Unit) : HeapValuesEnv(node) {
    constructor(p: IPath) : this(p.getNode())

    companion object {
        val phantomUnit: JNopStmt = TODO("FIXME — initialize phantomUnit")
    }
}