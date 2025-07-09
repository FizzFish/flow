package cn.sast.framework.report

import cn.sast.framework.report.metadata.AnalysisMetadata

/**
 * 分析元数据访问器（Visitor）。
 */
interface IMetadataVisitor {
    /** 访问一份元数据；默认空实现。 */
    fun visit(analysisMetadata: AnalysisMetadata) {}
}
