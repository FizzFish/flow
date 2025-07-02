package cn.sast.framework.report

import cn.sast.framework.report.metadata.AnalysisMetadata

public interface IMetadataVisitor {
   public abstract fun visit(analysisMetadata: AnalysisMetadata) {
   }
}
