package cn.sast.cli.command

import cn.sast.api.config.MainConfig
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.report.ProjectFileLocator
import com.github.ajalt.clikt.parameters.groups.OptionGroup

public abstract class TargetOptions(name: String) : OptionGroup(name, null, 2, null) {
    public val name: String = name

    public abstract fun getProvider(sootCtx: SootCtx, locator: ProjectFileLocator): IEntryPointProvider

    public abstract fun configureMainConfig(mainConfig: MainConfig)

    public abstract fun initSoot(sootCtx: SootCtx, locator: ProjectFileLocator)
}