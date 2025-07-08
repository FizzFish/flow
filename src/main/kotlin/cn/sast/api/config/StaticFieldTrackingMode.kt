package cn.sast.api.config

/**
 * 控制全局静态字段数据流分析粒度。
 */
enum class StaticFieldTrackingMode {
    /** 上下文 & 流敏感 */
    ContextFlowSensitive,

    /** 仅上下文敏感 */
    ContextFlowInsensitive,

    /** 不跟踪静态字段 */
    None
}
