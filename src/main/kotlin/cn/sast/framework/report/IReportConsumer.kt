package cn.sast.framework.report

import cn.sast.framework.result.OutputType
import java.io.Closeable
import kotlin.coroutines.Continuation

public interface IReportConsumer : Closeable {
    public val type: OutputType

    public abstract suspend fun init() {
    }

    public open suspend fun run(locator: IProjectFileLocator) {
    }

    internal object DefaultImpls {
        @JvmStatic
        fun run(`$this`: IReportConsumer, locator: IProjectFileLocator, `$completion`: Continuation<in Unit>): Any? {
            return Unit
        }
    }
}