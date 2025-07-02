package cn.sast.dataflow.interprocedural.analysis

/** 需要排除的常见 `subSignature` 列表 */
val excludeSubSignature: Set<String> = setOf(
   "java.lang.String toString()",
   "boolean equals(java.lang.Object)",
   "int hashCode()",
   "java.lang.Object clone()"
)
