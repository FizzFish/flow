package cn.sast.cli.command

import cn.sast.api.config.MainConfig
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.custom.EmptyEntryProvider
import cn.sast.framework.report.ProjectFileLocator
import soot.Scene
import soot.options.Options

class SrcAnalyzeOptions : TargetOptions("Src Analyze Options") {
    override fun getProvider(sootCtx: SootCtx, locator: ProjectFileLocator): IEntryPointProvider {
        return EmptyEntryProvider.INSTANCE
    }

    override fun configureMainConfig(mainConfig: MainConfig) {
        mainConfig.setSkipClass(true)
    }

    override fun initSoot(sootCtx: SootCtx, locator: ProjectFileLocator) {
        val options = Options.v()
        options.set_process_dir(emptyList())
        options.set_src_prec(2)
        options.set_prepend_classpath(true)
        options.set_whole_program(true)
        options.set_no_bodies_for_excluded(true)
        options.set_include_all(false)
        options.set_allow_phantom_refs(true)
        options.set_ignore_resolving_levels(true)
        options.setPhaseOption("cg.spark", "on")
        options.setPhaseOption("cg", "types-for-invoke:true")
        options.setPhaseOption("jb.sils", "enabled:false")
        Scene.v().loadNecessaryClasses()
    }
}