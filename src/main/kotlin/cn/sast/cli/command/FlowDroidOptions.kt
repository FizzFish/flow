@file:Suppress("unused")   // 选项字段可能由反射读取

package cn.sast.cli.command

import cn.sast.api.config.MainConfig
import cn.sast.dataflow.infoflow.InfoflowConfigurationExt
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.*
import soot.jimple.infoflow.InfoflowConfiguration
import soot.jimple.infoflow.InfoflowConfiguration.*
import java.io.File

/**
 * FlowDroid 引擎相关 CLI 选项。
 *
 * > **注意**
 * >   1. 只有在 `--flowdroid` 打开后，才会真正使用 FlowDroid 做污点分析；
 * >   2. 本类同时负责把 CLI 参数同步到 [InfoflowConfiguration]。
 */
class FlowDroidOptions(
    /** 是否在 CLI 帮助里隐藏本组选项 */
    isHidden: Boolean = false
) : OptionGroup("FlowDroid options") {

    // -------------------------------------------------------------------------
    // 基本开关
    // -------------------------------------------------------------------------

    val enableFlowDroid by option("--flowdroid", help = "Enable FlowDroid engine")
        .flag(default = false)
        .takeIf { isHidden }?.hidden() ?: run { /* keep visible */ }

    val baseDirectory by option("--fd-base-dir",
        help = "Base directory for FlowDroid temporary / output files"
    ).default("")

    val noPathAgnosticResults     by option("--fd-no-path-agnostic").flag(default = false, hidden = isHidden)
    val oneResultPerAccessPath    by option("--fd-one-result-per-ap").flag(default = false, hidden = isHidden)
    val mergeNeighbors            by option("--fd-merge-neighbors").flag(default = false, hidden = isHidden)
    val stopAfterFirstKFlows      by option("--fd-stop-after-k").int().default(0).hidden(isHidden)
    val inspectSources            by option("--fd-inspect-sources").flag(default = false, hidden = isHidden)
    val inspectSinks              by option("--fd-inspect-sinks").flag(default = false, hidden = isHidden)

    // -------------------------------------------------------------------------
    // 引擎 / 枚举选项
    // -------------------------------------------------------------------------

    private inline fun <reified T : Enum<T>> enumChoice() =
        choice(*enumValues<T>().associateBy { it.name.lowercase() }.toList().toTypedArray(), ignoreCase = true)

    val implicitFlowMode          by option("--fd-implicit-flow").enumChoice<ImplicitFlowMode>()
        .default(ImplicitFlowMode.ArrayAccesses).hidden(isHidden)

    val sootIntegrationMode       by option("--fd-soot-mode").enumChoice<SootIntegrationMode>()
        .default(SootIntegrationMode.CreateNewInstance).hidden(isHidden)

    val callgraphAlgorithm        by option("--fd-cg-algorithm").enumChoice<CallgraphAlgorithm>()
        .default(CallgraphAlgorithm.AutomaticSelection).hidden(isHidden)

    val aliasingAlgorithm         by option("--fd-aliasing").enumChoice<AliasingAlgorithm>()
        .default(AliasingAlgorithm.FlowSensitive).hidden(isHidden)

    val dataFlowDirection         by option("--fd-direction").enumChoice<DataFlowDirection>()
        .default(DataFlowDirection.Forwards).hidden(isHidden)

    val codeEliminationMode       by option("--fd-code-elim").enumChoice<CodeEliminationMode>()
        .default(CodeEliminationMode.NoCodeElimination).hidden(isHidden)

    // -------------------------------------------------------------------------
    // 各类布尔/数值细节选项
    // -------------------------------------------------------------------------

    val disableFlowSensitiveAliasing by option("--fd-disable-fs-aliasing").flag(default = false, hidden = isHidden)
    val disableExceptionTracking     by option("--fd-disable-exception-track").flag(default = false, hidden = isHidden)
    val disableArrayTracking         by option("--fd-disable-array-track").flag(default = false, hidden = isHidden)
    val disableArraySizeTainting     by option("--fd-disable-array-size").flag(default = false, hidden = isHidden)
    val disableTypeChecking          by option("--fd-disable-type-check").flag(default = false, hidden = isHidden)
    val ignoreFlowsInSystemPackages  by option("--fd-ignore-system").flag(default = false, hidden = isHidden)
    val writeOutputFiles             by option("--fd-write-files").flag(default = false, hidden = isHidden)
    val disableLogSourcesAndSinks    by option("--fd-disable-log-src-sink").flag(default = false, hidden = isHidden)
    val enableReflection             by option("--fd-enable-reflection").flag(default = false, hidden = isHidden)
    val disableLineNumbers           by option("--fd-disable-line-num").flag(default = false, hidden = isHidden)
    val disableTaintAnalysis         by option("--fd-disable-taint").flag(default = false, hidden = isHidden)
    val incrementalResultReporting   by option("--fd-incremental-report").flag(default = false, hidden = isHidden)
    val dataFlowTimeout              by option("--fd-timeout").long().default(0L).hidden(isHidden)
    val oneSourceAtATime             by option("--fd-one-source").flag(default = false, hidden = isHidden)
    val sequentialPathProcessing     by option("--fd-seq-path").flag(default = false, hidden = isHidden)

    val pathReconstructionMode       by option("--fd-path-recon").enumChoice<PathReconstructionMode>()
        .default(PathReconstructionMode.Precise).hidden(isHidden)

    val pathBuildingAlgorithm        by option("--fd-path-build").enumChoice<PathBuildingAlgorithm>()
        .default(PathBuildingAlgorithm.ContextSensitive).hidden(isHidden)

    val maxCallStackSize             by option("--fd-max-stack").int().default(30).hidden(isHidden)
    val maxPathLength                by option("--fd-max-path-length").int().default(75).hidden(isHidden)
    val maxPathsPerAbstraction       by option("--fd-max-paths-per-abst").int().default(15).hidden(isHidden)
    val pathReconstructionTimeout    by option("--fd-path-recon-timeout").long().default(0L).hidden(isHidden)
    val pathReconstructionBatchSize  by option("--fd-path-recon-batch").int().default(5).hidden(isHidden)

    val accessPathLength             by option("--fd-ap-length").int().default(25).hidden(isHidden)
    val useRecursiveAccessPaths      by option("--fd-ap-recursive").flag(default = false, hidden = isHidden)
    val useThisChainReduction        by option("--fd-ap-this-chain").flag(default = false, hidden = isHidden)
    val useSameFieldReduction        by option("--fd-ap-same-field").flag(default = false, hidden = isHidden)

    val disableSparseOpt             by option("--fd-disable-sparse-opt").flag(default = false, hidden = isHidden)

    val maxJoinPointAbstractions     by option("--fd-max-join-abst").int().default(-1).hidden(isHidden)
    val maxAbstractionPathLength     by option("--fd-max-abst-path").int().default(-1).hidden(isHidden)
    val maxCalleesPerCallSite        by option("--fd-max-callees").int().default(-1).hidden(isHidden)

    val dataFlowSolver               by option("--fd-solver").enumChoice<DataFlowSolver>()
        .default(DataFlowSolver.ContextFlowSensitive).hidden(isHidden)

    init {
        /* Clikt4: 可直接修改 OptionGroup.hidden */
        hidden = isHidden
    }

    // -------------------------------------------------------------------------
    // 向 InfoflowConfiguration 映射
    // -------------------------------------------------------------------------

    /**
     * 把 CLI 选项应用到 [InfoflowConfiguration]。
     */
    fun applyTo(info: InfoflowConfiguration, mainCfg: MainConfig) {
        setGlobalStaticOptions()

        /* 顶层参数 --------------------------------------------------------------------- */
        info.apply {
            oneSourceAtATime               = this@FlowDroidOptions.oneSourceAtATime
            stopAfterFirstKFlows           = stopAfterFirstKFlows
            inspectSources                 = inspectSources
            inspectSinks                   = inspectSinks
            implicitFlowMode               = implicitFlowMode
            staticFieldTrackingMode        = mainCfg.staticFieldTrackingMode      // 与原 getCvt 效果等价
            sootIntegrationMode            = sootIntegrationMode
            isFlowSensitiveAliasing        = !disableFlowSensitiveAliasing
            enableExceptionTracking        = !disableExceptionTracking
            enableArrayTracking            = !disableArrayTracking
            enableArraySizeTainting        = !disableArraySizeTainting
            callgraphAlgorithm             = callgraphAlgorithm
            aliasingAlgorithm              = aliasingAlgorithm
            dataFlowDirection              = dataFlowDirection
            enableTypeChecking             = !disableTypeChecking
            ignoreFlowsInSystemPackages    = ignoreFlowsInSystemPackages
            excludeSootLibraryClasses      = mainCfg.apponly
            maxThreadNum                   = mainCfg.parallelsNum
            writeOutputFiles               = writeOutputFiles
            codeEliminationMode            = codeEliminationMode
            logSourcesAndSinks             = !disableLogSourcesAndSinks
            enableReflection               = enableReflection
            enableLineNumbers              = !disableLineNumbers
            enableOriginalNames            = true
            taintAnalysisEnabled           = !disableTaintAnalysis
            incrementalResultReporting     = incrementalResultReporting
            dataFlowTimeout                = dataFlowTimeout
            memoryThreshold                = mainCfg.memoryThreshold
            isPathAgnosticResults          = !noPathAgnosticResults
        }

        /* 路径相关参数 ------------------------------------------------------------------ */
        info.pathConfiguration.apply {
            sequentialPathProcessing       = this@FlowDroidOptions.sequentialPathProcessing
            pathReconstructionMode         = pathReconstructionMode
            pathBuildingAlgorithm          = pathBuildingAlgorithm
            maxCallStackSize               = maxCallStackSize
            maxPathLength                  = maxPathLength
            maxPathsPerAbstraction         = maxPathsPerAbstraction
            pathReconstructionTimeout      = this@FlowDroidOptions.pathReconstructionTimeout
            pathReconstructionBatchSize    = pathReconstructionBatchSize
        }

        /* 访问路径 (AccessPath) 参数 ------------------------------------------------------ */
        info.accessPathConfiguration.apply {
            accessPathLength               = this@FlowDroidOptions.accessPathLength
            useRecursiveAccessPaths        = useRecursiveAccessPaths
            useThisChainReduction          = useThisChainReduction
            useSameFieldReduction          = useSameFieldReduction
        }

        /* 求解器参数 -------------------------------------------------------------------- */
        info.solverConfiguration.apply {
            maxJoinPointAbstractions       = maxJoinPointAbstractions
            maxAbstractionPathLength       = maxAbstractionPathLength
            maxCalleesPerCallSite          = maxCalleesPerCallSite
            dataFlowSolver                 = dataFlowSolver
        }
    }

    /**
     * 生成 InfoflowConfiguration 扩展选项（非官方 FlowDroid 代码）。
     */
    fun buildExtConfig(): InfoflowConfigurationExt =
        InfoflowConfigurationExt().apply { useSparseOpt = !disableSparseOpt }

    // -------------------------------------------------------------------------
    // 内部工具
    // -------------------------------------------------------------------------

    /** 设置 InfoflowConfiguration 的 _全局静态_ 选项 */
    private fun setGlobalStaticOptions() {
        InfoflowConfiguration.setBaseDirectory(File(baseDirectory).absolutePath)
        InfoflowConfiguration.setOneResultPerAccessPath(oneResultPerAccessPath)
        InfoflowConfiguration.setMergeNeighbors(mergeNeighbors)
    }
}

/**
 * 将项目自定义的 [MyMode] 枚举映射到 FlowDroid 的
 * [FDMode]，保持名称与语义一致。
 */
val cn.sast.api.config.StaticFieldTrackingMode.cvt: StaticFieldTrackingMode
    get() = when (this) {
        cn.sast.api.config.StaticFieldTrackingMode.ContextFlowSensitive   -> StaticFieldTrackingMode.ContextFlowSensitive
        cn.sast.api.config.StaticFieldTrackingMode.ContextFlowInsensitive -> StaticFieldTrackingMode.ContextFlowInsensitive
        cn.sast.api.config.StaticFieldTrackingMode.None                   -> StaticFieldTrackingMode.None
    }