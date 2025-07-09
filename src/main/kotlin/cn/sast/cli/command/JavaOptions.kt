package cn.sast.cli.command

import cn.sast.api.config.MainConfig
import cn.sast.framework.EntryPointCreatorFactory
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.component.HybridUnReachThenComponent
import cn.sast.framework.entries.custom.CustomEntryProvider
import cn.sast.framework.entries.custom.HybridCustomThenComponent
import cn.sast.framework.entries.java.UnReachableEntryProvider
import cn.sast.framework.entries.javaee.JavaEeEntryProvider
import cn.sast.framework.report.ProjectFileLocator
import com.github.ajalt.clikt.parameters.options.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import soot.jimple.infoflow.InfoflowConfiguration

/**
 * 纯 Java / Java-EE 目标分析选项
 */
class JavaOptions : TargetOptions("Java target options"),
    IClassAnalyzeOptionGroup {

    /** 自定义入口点（方法签名或文件路径），可多次出现 */
    val customEntryPoint: List<String> by option(
        "--entry-point", "-e",
        help = "Method signature(s) or a file containing signatures"
    ).multiple()

    /** InfoFlow 配置（默认空配置即可） */
    override val infoFlowConfig: InfoflowConfiguration by lazy { InfoflowConfiguration() }

    /** 生成 Dummy Main：顺序调用组件生命周期方法 */
    val makeComponentDummyMain: Boolean by option(
        "--component-dummy-main",
        help = "Generate dummy main method calling lifecycle methods"
    ).flag(default = false)

    /** 禁用 Java-EE 组件发现 */
    val disableJavaEEComponent: Boolean by option(
        "--disable-javaee-component",
        help = "Disable Java-EE lifecycle component creation"
    ).flag(default = false)

    /* ---------- TargetOptions 实现 ---------- */

    override fun getProvider(
        sootCtx: SootCtx,
        locator: ProjectFileLocator
    ): IEntryPointProvider {
        val entries = EntryPointCreatorFactory
            .getEntryPointFromArgs(customEntryPoint)
            .invoke()

        return when {
            makeComponentDummyMain ->
                if (entries.isNotEmpty())
                    HybridCustomThenComponent(sootCtx, entries)
                else
                    HybridUnReachThenComponent(sootCtx)

            entries.isNotEmpty() -> CustomEntryProvider(entries)

            disableJavaEEComponent -> UnReachableEntryProvider(sootCtx)

            else -> runBlocking {
                JavaEeEntryProvider.buildAsync(sootCtx, locator)
            }
        }
    }

    override fun configureMainConfig(mainConfig: MainConfig) { /* 无额外修改 */ }

    override fun initSoot(sootCtx: SootCtx, locator: ProjectFileLocator) {
        sootCtx.configureSoot()
        sootCtx.constructSoot(locator)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
