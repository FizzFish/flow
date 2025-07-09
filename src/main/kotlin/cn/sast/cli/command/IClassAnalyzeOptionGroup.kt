package cn.sast.cli.command

import soot.jimple.infoflow.InfoflowConfiguration

/**
 * 暴露 [InfoflowConfiguration] 的标记接口，
 * 供后续数据流分析组件统一获取配置。
 */
interface IClassAnalyzeOptionGroup {
    val infoFlowConfig: InfoflowConfiguration
}
