package cn.sast.framework.report

import cn.sast.api.config.MainConfig
import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.IBugResInfo
import cn.sast.api.report.Report
import cn.sast.api.report.ReportKt
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.OS
import cn.sast.framework.report.ReportConsumer.MetaData
import cn.sast.framework.report.sarif.ArtifactLocation
import cn.sast.framework.report.sarif.CodeFlow
import cn.sast.framework.report.sarif.FlowLocation
import cn.sast.framework.report.sarif.FlowLocationWrapper
import cn.sast.framework.report.sarif.Location
import cn.sast.framework.report.sarif.Message
import cn.sast.framework.report.sarif.MessageStrings
import cn.sast.framework.report.sarif.PhysicalLocation
import cn.sast.framework.report.sarif.Result
import cn.sast.framework.report.sarif.Rule
import cn.sast.framework.report.sarif.Run
import cn.sast.framework.report.sarif.SarifLog
import cn.sast.framework.report.sarif.ThreadFlow
import cn.sast.framework.report.sarif.Tool
import cn.sast.framework.report.sarif.ToolComponent
import cn.sast.framework.report.sarif.TranslationToolComponent
import cn.sast.framework.result.OutputType
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import java.util.*
import java.util.Map.Entry
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger

public open class SarifDiagnostics(outputDir: IResDirectory, type: OutputType = OutputType.SARIF) : ReportConsumer(type, outputDir), IFileReportConsumer {
    public open val metadata: MetaData
        get() = MetaData("corax", "1.0", "CoraxJava")

    public open fun getSarifDiagnosticsImpl(metadata: MetaData, locator: IProjectFileLocator): SarifDiagnosticsImpl {
        return SarifDiagnosticsImpl(this, metadata, locator)
    }

    public override suspend fun flush(reports: List<Report>, filename: String, locator: IProjectFileLocator) {
        return flush$suspendImpl(this, reports, filename, locator, null)
    }

    private fun getReportFileName(fileName: String): String {
        val var2: String = this.metadata.analyzerName
        val var10001: Locale = Locale.getDefault()
        val var3: String = var2.lowercase(var10001)
        return "$fileName_$var3.sarif"
    }

    public override fun close() {
    }

    @JvmStatic
    fun `flush$lambda$0`(`$fullPath`: IResource): Any {
        return "Create/modify plist file: '${`$fullPath`}'"
    }

    @JvmStatic
    fun `logger$lambda$1`() {
    }

    public companion object {
        private val logger: KLogger
    }

    public data class MultiLangRule(
        val id: String,
        val name: String,
        val messageStrings: Map<Language, MessageStrings>
    ) {
        public fun copy(
            id: String = this.id,
            name: String = this.name,
            messageStrings: Map<Language, MessageStrings> = this.messageStrings
        ): MultiLangRule {
            return MultiLangRule(id, name, messageStrings)
        }

        override fun toString(): String {
            return "MultiLangRule(id=$id, name=$name, messageStrings=$messageStrings)"
        }

        override fun hashCode(): Int {
            return (id.hashCode() * 31 + name.hashCode()) * 31 + messageStrings.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            } else if (other !is MultiLangRule) {
                return false
            } else {
                val var2 = other
                if (id != var2.id) {
                    return false
                } else if (name != var2.name) {
                    return false
                } else {
                    return messageStrings == var2.messageStrings
                }
            }
        }
    }

    @SourceDebugExtension(["SMAP\nSarifDiagnostics.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SarifDiagnostics.kt\ncn/sast/framework/report/SarifDiagnostics$SarifDiagnosticsImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,146:1\n1557#2:147\n1628#2,2:148\n1630#2:151\n1797#2,3:152\n1246#2,4:157\n1557#2:168\n1628#2,3:169\n1053#2:172\n1557#2:173\n1628#2,3:174\n1#3:150\n462#4:155\n412#4:156\n381#4,7:161\n*S KotlinDebug\n*F\n+ 1 SarifDiagnostics.kt\ncn/sast/framework/report/SarifDiagnostics$SarifDiagnosticsImpl\n*L\n59#1:147\n59#1:148,2\n59#1:151\n92#1:152,3\n103#1:157,4\n114#1:168\n114#1:169,3\n118#1:172\n118#1:173\n118#1:174,3\n103#1:155\n103#1:156\n104#1:161,7\n*E\n"])
    public open inner class SarifDiagnosticsImpl(
        private val metadata: MetaData,
        private val locator: IProjectFileLocator
    ) {
        private val ruleToIndex: MutableMap<MultiLangRule, Int> = LinkedHashMap()
        private lateinit var sortedRuleToIndex: List<MultiLangRule>

        public open val file2uri: String
            get() = this@SarifDiagnostics.outputDir.expandRes().absolute.normalize.path.toUri().toString()

        public open val absPathMapToFolder: String
            get() {
                val it = StringsKt.removePrefix(
                    StringsKt.removePrefix(this@SarifDiagnostics.outputDir.absolute.normalize.toString(), "/"), "\\"
                )
                return StringsKt.replace(
                    StringsKt.replace(
                        StringsKt.replace(
                            if (OS.INSTANCE.isWindows()) StringsKt.replace(it, ":", "", false) else it, "\\", "/", false
                        ),
                        "//",
                        "/",
                        false
                    ),
                    "!",
                    "",
                    false
                )
            }

        private val preferredMessage: String
            get() = ReportKt.preferredMessage(this@SarifDiagnosticsImpl.metadata, SarifDiagnosticsImpl::_get_preferredMessage_$lambda$1)

        private fun getTool(): Tool {
            val var10000 = this.metadata.analyzerName
            val var10001 = this.metadata.toolName
            val var10002 = this.metadata.toolVersion
            var var10003 = this.sortedRuleToIndex
            if (!this::sortedRuleToIndex.isInitialized) {
                Intrinsics.throwUninitializedPropertyAccessException("sortedRuleToIndex")
                var10003 = null
            }

            val destination = ArrayList<Rule>(var10003?.size ?: 10)

            for (item in var10003!!) {
                destination.add(
                    Rule(
                        item.id,
                        item.name,
                        ReportKt.preferredMessage(
                            item.messageStrings,
                            SarifDiagnosticsImpl::getTool$lambda$4$lambda$3$lambda$2
                        )
                    )
                )
            }

            return Tool(ToolComponent(var10000, var10001, var10002, destination))
        }

        private fun getTranslations(): List<TranslationToolComponent> {
            return emptyList()
        }

        public open fun getArtifactLocation(file: IResFile): ArtifactLocation {
            return ArtifactLocation(this.getFile2uri(file), null, 2, null)
        }

        public open fun getPhysicalLocation(classInfo: IBugResInfo, region: Region): PhysicalLocation? {
            val var10000 = this.locator.get(classInfo, EmptyWrapperFileGenerator.INSTANCE)
            return if (var10000 != null) PhysicalLocation(this.getArtifactLocation(var10000), cn.sast.framework.report.sarif.Region(region)) else null
        }

        private fun getPhysicalLocation(report: Report): PhysicalLocation? {
            return this.getPhysicalLocation(report.bugResFile, report.region)
        }

        private fun getPhysicalLocation(event: BugPathEvent): PhysicalLocation? {
            return this.getPhysicalLocation(event.classname, event.region)
        }

        private fun getThreadFlow(events: List<BugPathEvent>): ThreadFlow {
            var accumulator = emptyList<FlowLocationWrapper>()

            for (event in events) {
                val location = this.getPhysicalLocation(event)
                if (location != null) {
                    accumulator = accumulator + FlowLocationWrapper(
                        FlowLocation(Message(this.preferredMessage, null, 2, null), location)
                    )
                }
            }

            return ThreadFlow(accumulator)
        }

        private fun getResult(report: Report): Result? {
            var var10000 = report.check_name
            val map = LinkedHashMap<Language, MessageStrings>(report.message.size)

            for ((key, value) in report.message) {
                map[key] = MessageStrings(Message(value as String, null, 2, null))
            }

            val rule = MultiLangRule(var10000, "", map)
            val ruleIndex = this.ruleToIndex.getOrPut(rule) { this.ruleToIndex.size }

            val location = this.getPhysicalLocation(report) ?: return null
            return Result(
                report.check_name,
                ruleIndex,
                null,
                listOf(Location(location)),
                listOf(CodeFlow(listOf(this.getThreadFlow(report.pathEvents)))),
                4,
                null
            )
        }

        private fun getResults(reports: List<Report>): List<Result?> {
            val destination = ArrayList<Result?>(reports.size)
            for (item in reports) {
                destination.add(this.getResult(item))
            }
            return destination
        }

        public open fun getRun(reports: List<Report>): Run {
            val results = this.getResults(reports).filterNotNull()
            val sortedRules = this.ruleToIndex.toList().sortedWith { a, b ->
                a.second.compareTo(b.second)
            }.map { it.first }
            this.sortedRuleToIndex = sortedRules
            return Run(this.getTool(), null, results, this.getTranslations(), 2, null)
        }

        public fun getSarifLog(reports: List<Report>): SarifLog {
            return SarifLog(
                "https://raw.githubusercontent.com/oasis-tcs/sarif-spec/master/Schemata/sarif-schema-2.1.0.json",
                "2.1.0",
                listOf(this.getRun(reports))
            )
        }

        @JvmStatic
        fun `_get_preferredMessage_$lambda$1`(): String {
            return "Engine error: no such message of preferred languages ${MainConfig.Companion.preferredLanguages}"
        }

        @JvmStatic
        fun `getTool$lambda$4$lambda$3$lambda$2`(): MessageStrings {
            return MessageStrings(
                Message("Engine error: no such message of preferred languages ${MainConfig.Companion.preferredLanguages}", null, 2, null)
            )
        }
    }

    private object SarifMetadata {
        public const val schema: String = "https://raw.githubusercontent.com/oasis-tcs/sarif-spec/master/Schemata/sarif-schema-2.1.0.json"
        public const val version: String = "2.1.0"
    }
}