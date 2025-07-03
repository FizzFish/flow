package cn.sast.framework.report

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.collections.Map

public class ReportConfig private constructor(
    public val reportName: String,
    public val reportType: String,
    public val reportParams: Map<String, Any>,
    public val isAsync: Boolean
) {
    public class Builder {
        private var reportName: String = ""
        private var reportType: String = ""
        private var reportParams: Map<String, Any> = emptyMap()
        private var isAsync: Boolean = false

        public fun setReportName(reportName: String): Builder {
            this.reportName = reportName
            return this
        }

        public fun setReportType(reportType: String): Builder {
            this.reportType = reportType
            return this
        }

        public fun setReportParams(reportParams: Map<String, Any>): Builder {
            this.reportParams = reportParams
            return this
        }

        public fun setIsAsync(isAsync: Boolean): Builder {
            this.isAsync = isAsync
            return this
        }

        public fun build(): ReportConfig = ReportConfig(
            reportName = reportName,
            reportType = reportType,
            reportParams = reportParams,
            isAsync = isAsync
        )
    }

    public companion object {
        public fun builder(): Builder = Builder()
    }
}