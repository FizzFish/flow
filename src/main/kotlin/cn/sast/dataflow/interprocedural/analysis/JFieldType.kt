package cn.sast.dataflow.interprocedural.analysis

import soot.Type

public sealed class JFieldType protected constructor() {
   public abstract val type: Type
   public abstract val name: String
}
