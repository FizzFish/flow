package cn.sast.framework.engine

import com.feysh.corax.config.api.IPreAnalysisConfig
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.IMatchItem

internal abstract class PreAnalysisAbsConfig : IPreAnalysisConfig {
   public open var processRules: List<IMatchItem>
      internal final set

   public open var incrementalAnalyze: Boolean
      internal final set

   open fun PreAnalysisAbsConfig(processRules: MutableList<ProcessRule.IMatchItem>, incrementalAnalyze: Boolean) {
      this.processRules = processRules;
      this.incrementalAnalyze = incrementalAnalyze;
   }

   open fun PreAnalysisAbsConfig() {
      this(null, false, 3, null);
   }
}
