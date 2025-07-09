package cn.sast.framework.report

import cn.sast.api.config.MainConfig
import cn.sast.api.report.*
import cn.sast.common.*
import cn.sast.framework.report.ReportConsumer.MetaData
import cn.sast.framework.report.sarif.*
import cn.sast.framework.result.OutputType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * 将 [Report] 列表写为 **SARIF v2.1** 日志文件。
 *
 * *最小可运行* 实现：如需更丰富字段，可继承并覆写
 * [getSarifDiagnosticsImpl] / [SarifDiagnosticsImpl]。
 */
open class SarifDiagnostics(
    outputDir: IResDirectory,
    type: OutputType = OutputType.SARIF
) : ReportConsumer(type, outputDir), IFileReportConsumer {

    override val metadata: MetaData = MetaData(
        toolName = "CoraxJava",
        toolVersion = "1.0",
        analyzerName = "CoraxJava"
    )

    /* ------------------------------------------------------------------
     *  API – 供子类定制
     * ---------------------------------------------------------------- */

    protected open fun getSarifDiagnosticsImpl(
        metadata: MetaData,
        locator: IProjectFileLocator
    ): SarifDiagnosticsImpl = SarifDiagnosticsImpl(metadata, locator)

    /* ------------------------------------------------------------------
     *  IFileReportConsumer
     * ---------------------------------------------------------------- */

    override suspend fun flush(
        reports: List<Report>,
        filename: String,
        locator: IProjectFileLocator
    ) {
        val impl = getSarifDiagnosticsImpl(metadata, locator)
        val sarif = impl.getSarifLog(reports)

        val fullPath = outputDir
            .resolve(getReportFileName(filename))
            .toFile()
        withContext(Dispatchers.IO) {
            fullPath.parentFile.mkdirs()
            fullPath.writeText(sarif.toJson())        // 假设 SarifLog 有扩展函数 toJson()
        }
        logger.trace { "Create/modify SARIF file: '$fullPath'" }
    }

    override fun close() = Unit

    /* ------------------------------------------------------------------ */

    private fun getReportFileName(base: String): String =
        "${base}_${metadata.analyzerName.lowercase(Locale.getDefault())}.sarif"

    /* ================================================================
     *  内部：SARIF 构建实现
     * ================================================================ */

    open inner class SarifDiagnosticsImpl(
        protected val metadata: MetaData,
        protected val locator: IProjectFileLocator
    ) {

        /* ---------- rule(index) 管理 ---------- */

        private val ruleToIndex = LinkedHashMap<MultiLangRule, Int>()
        private lateinit var sortedRule: List<MultiLangRule>

        /* ---------- 快捷工具 ---------- */

        protected fun ArtifactLocation(file: IResFile): ArtifactLocation =
            ArtifactLocation(
                uri = file.expandRes(outputDir).absoluteNormalize.path.toUri().toString()
            )

        protected fun PhysicalLocation(info: IBugResInfo, region: Region): PhysicalLocation? =
            locator.get(info, EmptyWrapperFileGenerator)?.let { file ->
                PhysicalLocation(
                    artifactLocation = ArtifactLocation(file),
                    region = cn.sast.framework.report.sarif.Region(region)
                )
            }

        /* ---------- Message / Language ---------- */

        private fun preferredMessage(
            msg: Map<Language, String>
        ): Message = Message(
            text = ReportKt.preferredMessage(
                msg
            ) {
                "Engine error: no such message of preferred languages ${MainConfig.preferredLanguages}"
            }
        )

        /* ---------- Single BugPath → ThreadFlow ---------- */

        private fun threadFlow(events: List<BugPathEvent>): ThreadFlow {
            val flowLocs = events.mapNotNull { ev ->
                PhysicalLocation(ev.classname, ev.region)?.let { pl ->
                    FlowLocationWrapper(
                        FlowLocation(message = preferredMessage(ev.message), physicalLocation = pl)
                    )
                }
            }
            return ThreadFlow(flowLocs)
        }

        /* ---------- Report → Result ---------- */

        private fun toResult(report: Report): Result? {
            // 1) Rule Index
            val multi = MultiLangRule(
                id = report.check_name,
                name = report.check_name,
                messageStrings = report.message.mapValues { (_, v) ->
                    MessageStrings(Message(v))
                }
            )
            val index = ruleToIndex.getOrPut(multi) { ruleToIndex.size }

            // 2) Primary location
            val primaryLoc = PhysicalLocation(report.bugResFile, report.region)
                ?: return null

            return Result(
                ruleId = report.check_name,
                ruleIndex = index,
                message = preferredMessage(report.message),
                locations = listOf(Location(primaryLoc)),
                codeFlows = listOf(
                    CodeFlow(
                        threadFlows = listOf(threadFlow(report.pathEvents))
                    )
                )
            )
        }

        /* ---------- Run / Tool ---------- */

        private fun tool(): Tool {
            // 按 ruleToIndex 排序
            sortedRule = ruleToIndex
                .toList()
                .sortedBy { (_, idx) -> idx }
                .map { (rule, _) -> rule }
            val rulesSarif = sortedRule.map { rule ->
                Rule(
                    id = rule.id,
                    name = rule.name,
                    fullDescription = preferredMessage(rule.messageStrings.mapValues { it.value.text }),
                    messageStrings = rule.messageStrings
                )
            }
            return Tool(
                driver = ToolComponent(
                    name = metadata.toolName,
                    informationUri = metadata.toolName,
                    version = metadata.toolVersion,
                    rules = rulesSarif
                )
            )
        }

        open fun getRun(reports: List<Report>): Run =
            Run(
                tool = tool(),
                results = reports.mapNotNull(::toResult)
            )

        fun getSarifLog(reports: List<Report>): SarifLog =
            SarifLog(
                schema = SarifMetadata.schema,
                version = SarifMetadata.version,
                runs = listOf(getRun(reports))
            )
    }

    /* ---------- DTO for multi-language rule ---------- */
    data class MultiLangRule(
        val id: String,
        val name: String,
        val messageStrings: Map<Language, MessageStrings>
    )

    /* ---------- 静态 ---------- */

    private companion object SarifMetadata {
        const val schema: String = "https://raw.githubusercontent.com/oasis-tcs/sarif-spec/master/Schemata/sarif-schema-2.1.0.json"
        const val version: String = "2.1.0"

        val logger = KotlinLogging.logger {}
    }
}
