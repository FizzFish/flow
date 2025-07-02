package cn.sast.dataflow.infoflow.svfa

/**
 * 表示一个 [Value] 在语句中的位置：
 * - **ParamAndThis**：`this` 或形参
 * - **Left**        ：赋值左侧（定义）
 * - **Right**       ：赋值右侧（使用 / 读取）
 * - **Arg**         ：实参
 */
internal enum class ValueLocation {
   ParamAndThis,
   Left,
   Right,
   Arg
}
