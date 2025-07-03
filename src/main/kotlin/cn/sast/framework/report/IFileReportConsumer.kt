package cn.sast.framework.report

import cn.sast.api.report.Report
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt

public interface IFileReportConsumer : IReportConsumer {
    public abstract suspend fun flush(reports: List<Report>, filename: String, locator: IProjectFileLocator)

    internal class DefaultImpls {
        @JvmStatic
        fun run(`$this`: IFileReportConsumer, locator: IProjectFileLocator, `$completion`: Continuation<Unit>): Any? {
            val var10000: Any = IReportConsumer.DefaultImpls.run(`$this`, locator, `$completion`)
            return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit
        }
    }
}