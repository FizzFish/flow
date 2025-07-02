package cn.sast.dataflow.interprocedural.analysis

import soot.Type

/** 所有“JField”类型（名字 + soot.Type）的抽象父类 */
sealed class JFieldType {
   abstract val type: Type
   abstract val name: String
}
