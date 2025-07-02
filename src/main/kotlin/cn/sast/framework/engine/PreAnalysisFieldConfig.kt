package cn.sast.framework.engine

import com.feysh.corax.config.api.IPreAnalysisFieldConfig

internal class PreAnalysisFieldConfig(appOnly: Boolean = true, ignoreProjectConfigProcessFilter: Boolean = false) : PreAnalysisAbsConfig(null, false, 3),
   IPreAnalysisFieldConfig {
   public open var appOnly: Boolean
      internal final set

   public open var ignoreProjectConfigProcessFilter: Boolean
      internal final set

   init {
      this.appOnly = appOnly;
      this.ignoreProjectConfigProcessFilter = ignoreProjectConfigProcessFilter;
   }

   fun PreAnalysisFieldConfig() {
      this(false, false, 3, null);
   }
}
