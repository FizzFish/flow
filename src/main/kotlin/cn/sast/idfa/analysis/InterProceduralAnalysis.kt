package cn.sast.idfa.analysis

// 移除冗余的 public 修饰符，去掉未使用的类型形参 M、N、A
abstract class InterProceduralAnalysis(val reverse: Boolean) {
   // 移除冗余的 public 修饰符，抽象函数不应有函数体
   abstract fun boundaryValue(entryPoint: Any): Any

   abstract fun copy(src: Any): Any

   abstract fun doAnalysis(entries: Collection<Any>)

   abstract fun meet(op1: Any, op2: Any): Any

   abstract fun shallowMeet(op1: Any, op2: Any): Any

   abstract fun merge(local: Any, ret: Any): Any

   abstract fun programRepresentation(): ProgramRepresentation<Any, Any>

   abstract fun bottomValue(): Any
}