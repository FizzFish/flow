package cn.sast.cli.command

import cn.sast.api.config.MainConfig
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.custom.EmptyEntryProvider
import cn.sast.framework.report.ProjectFileLocator
import soot.Scene
import soot.options.Options

class SrcAnalyzeOptions : TargetOptions("Src Analyze Options") {

    override fun getProvider(
        sootCtx: SootCtx,
        locator: ProjectFileLocator
    ): IEntryPointProvider = EmptyEntryProvider

    override fun configureMainConfig(mainConfig: MainConfig) {
        mainConfig.skipClass = true
    }

    override fun initSoot(
        sootCtx: SootCtx,
        locator: ProjectFileLocator
    ) {
        // 配置 Soot，专注源码级别分析
        with(Options.v()) {
            set_process_dir(emptyList())
            set_src_prec(2)
            set_prepend_classpath(true)
            set_whole_program(true)
            set_no_bodies_for_excluded(true)
            set_include_all(false)
            set_allow_phantom_refs(true)
            set_ignore_resolving_levels(true)
            setPhaseOption("cg.spark", "on")
            setPhaseOption("cg", "types-for-invoke:true")
            setPhaseOption("jb.sils", "enabled:false")
        }
        Scene.v().loadNecessaryClasses()
    }
}
