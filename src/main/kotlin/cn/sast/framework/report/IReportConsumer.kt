package cn.sast.framework.report

import cn.sast.framework.result.OutputType
import java.io.Closeable

/**
 * 结果输出“消费端”统一接口。
 *
 * 一个实现通常负责把分析结果写入：
 * * 控制台 / 日志
 * * 数据库
 * * JSON / SARIF / CSV  等文件
 */
interface IReportConsumer : Closeable {
    /** 消费器产出的结果类型（枚举见 [OutputType]） */
    val type: OutputType

    /** 初始化阶段（可在此打开文件／连接等）——缺省什么也不做 */
    suspend fun init() {}

    /**
     * 消费分析结果。<br/>
     * 默认实现什么也不做；子类按需覆盖。
     */
    suspend fun run(locator: IProjectFileLocator) {}
}
