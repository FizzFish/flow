package cn.sast.cli.command

import cn.sast.api.config.MainConfig
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.report.ProjectFileLocator
import com.github.ajalt.clikt.parameters.groups.OptionGroup

public abstract class TargetOptions : OptionGroup {
   public final val name: String

   open fun TargetOptions(name: java.lang.String) {
      super(name, null, 2, null);
      this.name = name;
   }

   public abstract fun getProvider(sootCtx: SootCtx, locator: ProjectFileLocator): IEntryPointProvider {
   }

   public abstract fun configureMainConfig(mainConfig: MainConfig) {
   }

   public abstract fun initSoot(sootCtx: SootCtx, locator: ProjectFileLocator) {
   }
}
