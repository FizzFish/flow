package cn.sast.cli.command

import cn.sast.api.config.MainConfig
import cn.sast.common.IResource
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.apk.ApkLifeCycleComponent
import cn.sast.framework.report.ProjectFileLocator
import kotlinx.collections.immutable.PersistentSet
import mu.KotlinLogging
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration
import java.io.File

/**
 * Android 目标构建相关选项。
 *
 * * 与原版保持 API 向后兼容 *
 */
class AndroidOptions(
    /** Android 平台目录 (`$ANDROID_HOME/platforms`) */
    private val androidPlatformDir: File,

    /** 是否让 FlowDroid “一次只分析一个组件” */
    private val oneComponentAtATime: Boolean = false
) : TargetOptions("Android Target Options"), IClassAnalyzeOptionGroup {

    /* ---------- 公开读取 ---------- */

    val infoFlowConfig: InfoflowAndroidConfiguration by lazy(::buildInfoFlowConfig)

    /** 解析后的单 APK 文件绝对路径 */
    private fun targetApkFile(mc: MainConfig): String {
        val process = mc.processDir
        require(process.size == 1) { "processDir must be a single apk file, but: $process" }
        return process.first().absolutePath
    }

    /* ---------- TargetOptions 实现 ---------- */

    override fun getProvider(sootCtx: SootCtx, locator: ProjectFileLocator): IEntryPointProvider {
        val mc = sootCtx.mainConfig
        return ApkLifeCycleComponent(infoFlowConfig, mc, targetApkFile(mc))
    }

    override fun configureMainConfig(mainConfig: MainConfig) {
        mainConfig.androidPlatformDir = androidPlatformDir.absolutePath
    }

    override fun initSoot(sootCtx: SootCtx, locator: ProjectFileLocator) {
        val mc = sootCtx.mainConfig
        val apk = targetApkFile(mc)
        val androidJar = mc.getAndroidJarClasspath(apk)
            ?: error("Cannot find android.jar in $androidPlatformDir for $apk")
        mc.classpath += androidJar
        sootCtx.configureSoot()
        sootCtx.constructSoot(locator)
    }

    /* ---------- 内部 ---------- */

    private fun buildInfoFlowConfig(): InfoflowAndroidConfiguration = InfoflowAndroidConfiguration().apply {
        analysisFileConfig.apply {
            // 这些字段后续 FlowDroid 会再覆盖，这里占位即可
            targetAPKFile       = "unused"
            sourceSinkFile      = "unused"
            androidPlatformDir  = "unused"
            additionalClasspath = "unused"
            outputFile          = "unused"
        }

        callbackConfig.apply {
            enableCallbacks         = true
            callbackAnalyzer         = InfoflowAndroidConfiguration.CallbackAnalyzer.Default
            filterThreadCallbacks    = true
            maxCallbacksPerComponent = 100
            callbackAnalysisTimeout  = 0
            maxAnalysisCallbackDepth = -1
            serializeCallbacks       = false
            callbacksFile            = ""
        }

        iccConfig.apply {
            iccModel              = null
            isIccResultsPurify    = true
        }

        isOneComponentAtATime = oneComponentAtATime
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
