package cn.sast.framework.report.coverage

import com.feysh.corax.cache.AnalysisKey

/**
 * AnalysisCache 的 **Key**：按类名索引 [ClassSourceInfo].
 */
data class ClassSourceOfSCKey(val className: String)
    : AnalysisKey(ClassSourceOfSCFactory.key)
