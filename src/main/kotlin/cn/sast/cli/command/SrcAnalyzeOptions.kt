package cn.sast.cli.command

import cn.sast.api.config.MainConfig
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.custom.EmptyEntryProvider
import cn.sast.framework.report.ProjectFileLocator
import soot.Scene
import soot.options.Options

public class SrcAnalyzeOptions : TargetOptions("Src Analyze Options") {
   public override fun getProvider(sootCtx: SootCtx, locator: ProjectFileLocator): IEntryPointProvider {
      return EmptyEntryProvider.INSTANCE;
   }

   public override fun configureMainConfig(mainConfig: MainConfig) {
      mainConfig.setSkipClass(true);
   }

   public override fun initSoot(sootCtx: SootCtx, locator: ProjectFileLocator) {
      val var3: Options = Options.v();
      var3.set_process_dir(CollectionsKt.emptyList());
      var3.set_src_prec(2);
      var3.set_prepend_classpath(true);
      var3.set_whole_program(true);
      var3.set_no_bodies_for_excluded(true);
      var3.set_include_all(false);
      var3.set_allow_phantom_refs(true);
      var3.set_ignore_resolving_levels(true);
      var3.setPhaseOption("cg.spark", "on");
      var3.setPhaseOption("cg", "types-for-invoke:true");
      var3.setPhaseOption("jb.sils", "enabled:false");
      Scene.v().loadNecessaryClasses();
   }
}
