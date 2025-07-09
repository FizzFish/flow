package cn.sast.framework.report

/**
 * 通用 “值 + 自增 ID” 容器。
 * 常在批量写入 SQL 时返回主键与行对象配对。
 */
data class ValueWithId<T>(
    val id: Long,
    val value: T
)
