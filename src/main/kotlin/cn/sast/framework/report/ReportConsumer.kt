package cn.sast.framework.report

import cn.sast.common.IResDirectory
import cn.sast.framework.result.OutputType
import kotlin.coroutines.Continuation

public abstract class ReportConsumer(
    public open val type: OutputType,
    public final val outputDir: IResDirectory
) : IReportConsumer {
    init {
        outputDir.mkdirs()
    }

    public abstract val metadata: MetaData

    public override suspend fun init() {
        return init$suspendImpl(this, `$completion`)
    }

    override fun run(locator: IProjectFileLocator, `$completion`: Continuation<in Unit>): Any? {
        return IReportConsumer.DefaultImpls.run(this, locator, `$completion`)
    }

    public data class MetaData(
        val toolName: String,
        val toolVersion: String,
        val analyzerName: String
    ) {
        public operator fun component1(): String = toolName

        public operator fun component2(): String = toolVersion

        public operator fun component3(): String = analyzerName

        public fun copy(
            toolName: String = this.toolName,
            toolVersion: String = this.toolVersion,
            analyzerName: String = this.analyzerName
        ): MetaData {
            return MetaData(toolName, toolVersion, analyzerName)
        }

        public override fun toString(): String {
            return "MetaData(toolName=$toolName, toolVersion=$toolVersion, analyzerName=$analyzerName)"
        }

        public override fun hashCode(): Int {
            return (toolName.hashCode() * 31 + toolVersion.hashCode()) * 31 + analyzerName.hashCode()
        }

        public override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is MetaData) return false
            
            return toolName == other.toolName &&
                toolVersion == other.toolVersion &&
                analyzerName == other.analyzerName
        }
    }
}