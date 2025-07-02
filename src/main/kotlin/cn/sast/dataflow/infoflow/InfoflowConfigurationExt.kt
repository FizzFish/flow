package cn.sast.dataflow.infoflow

/**
 * 额外的全局配置。
 *
 * @property useSparseOpt            是否启用 Sparse 优化（默认 true）
 * @property missingSummariesFile    缺失摘要输出路径（可为 null）
 */
data class InfoflowConfigurationExt(
   val useSparseOpt: Boolean = true,
   val missingSummariesFile: String? = null
)
