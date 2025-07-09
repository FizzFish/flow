package cn.sast.framework.report

import cn.sast.common.IResDirectory
import cn.sast.framework.result.OutputType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.Continuation

/**
 * 抽象“结果消费端”——所有报告写出器共同基类
 */
abstract class ReportConsumer(
    /** 消费器输出类型（JSON、CSV、PLIST …） */
    final override val type: OutputType,
    /** 结果写入目录（自动创建） */
    val outputDir: IResDirectory,
) : IReportConsumer {

    /** 每个子类声明自己的元数据 */
    abstract val metadata: MetaData

    init {
        outputDir.mkdirs()
    }

    /** 默认无初始化逻辑，可覆写 */
    override suspend fun init() { /* no-op */ }

    /**
     * 默认直接调用 [IReportConsumer.run] 的接口默认实现，
     * 如需 *单个文件* 输出，可在子类重写。
     */
    override suspend fun run(locator: IProjectFileLocator) =
        IReportConsumer.DefaultImpls.run(this, locator)

    /** 元数据结构 —— 会写入各类报告头部 */
    data class MetaData(
        val toolName:     String,
        val toolVersion:  String,
        val analyzerName: String,
    )
}
