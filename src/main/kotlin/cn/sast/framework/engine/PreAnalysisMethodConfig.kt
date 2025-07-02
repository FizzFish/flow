package cn.sast.framework.engine

import com.feysh.corax.config.api.IPreAnalysisMethodConfig

internal class PreAnalysisMethodConfig(appOnly: Boolean = true, ignoreProjectConfigProcessFilter: Boolean = false) : PreAnalysisAbsConfig(null, false, 3),
   IPreAnalysisMethodConfig {
   public open var appOnly: Boolean
      internal final set

   public open var ignoreProjectConfigProcessFilter: Boolean
      internal final set

   init {
      this.appOnly = appOnly;
      this.ignoreProjectConfigProcessFilter = ignoreProjectConfigProcessFilter;
   }

   fun PreAnalysisMethodConfig() {
      this(false, false, 3, null);
   }
}
