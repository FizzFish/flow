package cn.sast.cli.command

import cn.sast.api.config.MainConfig
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.report.ProjectFileLocator
import com.github.ajalt.clikt.parameters.groups.OptionGroup

/**
 * 基类：不同“分析目标”选项组的共同逻辑。
 *
 * 子类通常会：
 * 1. 通过 CLI 选项修改 [MainConfig]；
 * 2. 初始化 Soot；
 * 3. 提供匹配目标的 [IEntryPointProvider]。
 */
abstract class TargetOptions(name: String) : OptionGroup(name) {

    /** 返回本次分析应使用的入口点提供器 */
    abstract fun getProvider(
        sootCtx: SootCtx,
        locator: ProjectFileLocator
    ): IEntryPointProvider

    /** 允许子类在 Soot 初始化之前修改全局配置 */
    abstract fun configureMainConfig(mainConfig: MainConfig)

    /** 针对具体目标执行 Soot 初始化（如补全 classpath、加载框架等） */
    abstract fun initSoot(
        sootCtx: SootCtx,
        locator: ProjectFileLocator
    )
}
