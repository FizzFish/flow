package cn.sast.framework.report

import cn.sast.api.report.Report

/**
 * 面向“**单文件输出**”的结果消费端。
 *
 * 与 [IReportConsumer] 的差别：额外暴露 [flush]，方便把
 * 多条 [Report] 合并写入一个文件。
 */
interface IFileReportConsumer : IReportConsumer {

    /**
     * 将 [reports] 刷入 **单个文件** `filename`。<br/>
     * 默认空实现，子类按需覆盖。
     */
    suspend fun flush(
        reports: List<Report>,
        filename: String,
        locator: IProjectFileLocator
    ) {}
}
