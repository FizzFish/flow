package cn.sast.api.report

import cn.sast.api.util.ComparatorUtilsKt
import cn.sast.common.GLB
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.IChecker
import com.feysh.corax.config.api.IRule
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.BugMessage.Env
import com.feysh.corax.config.api.report.Region
import java.util.ArrayList
import kotlin.enums.EnumEntries
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.json.JsonBuilder

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/Report\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1628#2,3:452\n1628#2,3:455\n1628#2,3:458\n1628#2,3:461\n1#3:464\n*S KotlinDebug\n*F\n+ 1 Report.kt\ncn/sast/api/report/Report\n*L\n330#1:452,3\n331#1:455,3\n332#1:458,3\n333#1:461,3\n*E\n"])
data class Report internal constructor(
    val bugResFile: IBugResInfo,
    val region: Region,
    val message: Map<Language, String>,
    val check_name: String,
    val detector_name: String,
    val type: String,
    val standard: Set<IRule>,
    val severity: String? = null,
    val analyzer_name: String? = null,
    val category: String? = null,
    val analyzer_result_file_path: String? = null,
    val checkType: CheckType,
    var pathEvents: MutableList<BugPathEvent> = ArrayList(),
    var bug_path_positions: MutableList<BugPathPosition> = ArrayList(),
    var notes: MutableList<BugPathEvent> = ArrayList(),
    var macro_expansions: MutableList<MacroExpansion> = ArrayList()
) : Comparable<Report>, IReportHashAble {

    val classes: MutableSet<IBugResInfo>
        get() {
            val ret = mutableSetOf(bugResFile)
            pathEvents.forEach { ret.add(it.getClassname()) }
            bug_path_positions.forEach { ret.add(it.getClassname()) }
            notes.forEach { ret.add(it.getClassname()) }
            macro_expansions.forEach { ret.add(it.getClassname()) }
            return ret
        }

    init {
        GLB.INSTANCE.plusAssign(checkType)
        pathEvents.add(BugPathEvent(message, bugResFile, region, null, 8, null))
    }

    override fun reportHash(c: IReportHashCalculator): String {
        return "${bugResFile.reportHash(c)}:${region} ${check_name},${detector_name},${type},${category},${severity},${analyzer_name}} "
    }

    private fun getReportHashContextFree(c: IReportHashCalculator, ret: MutableList<String>) {
        ret.add(reportHash(c))
    }

    private fun getReportHashPathSensitive(c: IReportHashCalculator, ret: MutableList<String>) {
        val lastEvent = pathEvents.lastOrNull()
        if (lastEvent != null) {
            ret.add(lastEvent.reportHash(c))
        }
    }

    private fun getReportHashDiagnosticMessage(c: IReportHashCalculator, ret: MutableList<String>, onlyHeadTail: Boolean = false) {
        if (onlyHeadTail) {
            pathEvents.forEach { ret.add(it.reportHashWithMessage(c)) }
        } else {
            pathEvents.firstOrNull()?.let { ret.add(it.reportHashWithMessage(c)) }
            if (pathEvents.size >= 2) {
                pathEvents.lastOrNull()?.let { ret.add(it.reportHashWithMessage(c)) }
            }
        }
    }

    fun reportHash(hashCalculator: IReportHashCalculator, hashType: HashType): String {
        val ret = ArrayList<String>()
        when (hashType) {
            HashType.CONTEXT_FREE -> getReportHashContextFree(hashCalculator, ret)
            HashType.PATH_SENSITIVE -> {
                getReportHashContextFree(hashCalculator, ret)
                getReportHashPathSensitive(hashCalculator, ret)
            }
            HashType.DIAGNOSTIC_MESSAGE -> {
                getReportHashContextFree(hashCalculator, ret)
                getReportHashDiagnosticMessage(hashCalculator, ret, false)
            }
        }
        return ReportKt.toHex(ReportKt.md5(ret.joinToString("|")))
    }

    override fun compareTo(other: Report): Int {
        ComparatorUtilsKt.compareToNullable(analyzer_name, other.analyzer_name)?.let { return it }
        bugResFile.compareTo(other.bugResFile).let { if (it != 0) return it }
        Intrinsics.compare(region.startLine, other.region.startLine).let { if (it != 0) return it }
        ComparatorUtilsKt.compareToMap(message.toSortedMap(), other.message.toSortedMap()).let { if (it != 0) return it }
        check_name.compareTo(other.check_name).let { if (it != 0) return it }
        detector_name.compareTo(other.detector_name).let { if (it != 0) return it }
        type.compareTo(other.type).let { if (it != 0) return it }
        compareValuesBy(this.checkType, other.checkType) { it }.let { if (it != 0) return it }
        ComparatorUtilsKt.compareTo({ a, b -> a.compareTo(b) }, standard, other.standard).let { if (it != 0) return it }
        Intrinsics.compare(region.startColumn, other.region.startColumn).let { if (it != 0) return it }
        ComparatorUtilsKt.compareToNullable(severity, other.severity)?.let { return it }
        ComparatorUtilsKt.compareToNullable(category, other.category)?.let { return it }
        ComparatorUtilsKt.compareToNullable(analyzer_result_file_path, other.analyzer_result_file_path)?.let { return it }
        ComparatorUtilsKt.compareToCollection(pathEvents, other.pathEvents).let { if (it != 0) return it }
        ComparatorUtilsKt.compareToCollection(bug_path_positions, other.bug_path_positions).let { if (it != 0) return it }
        ComparatorUtilsKt.compareToCollection(notes, other.notes).let { if (it != 0) return it }
        ComparatorUtilsKt.compareToCollection(macro_expansions, other.macro_expansions).let { if (it != 0) return it }
        return 0
    }

    fun copy(
        bugResFile: IBugResInfo = this.bugResFile,
        region: Region = this.region,
        message: Map<Language, String> = this.message,
        check_name: String = this.check_name,
        detector_name: String = this.detector_name,
        type: String = this.type,
        standard: Set<IRule> = this.standard,
        severity: String? = this.severity,
        analyzer_name: String? = this.analyzer_name,
        category: String? = this.category,
        analyzer_result_file_path: String? = this.analyzer_result_file_path,
        checkType: CheckType = this.checkType,
        pathEvents: MutableList<BugPathEvent> = this.pathEvents,
        bug_path_positions: MutableList<BugPathPosition> = this.bug_path_positions,
        notes: MutableList<BugPathEvent> = this.notes,
        macro_expansions: MutableList<MacroExpansion> = this.macro_expansions
    ) = Report(
        bugResFile,
        region,
        message,
        check_name,
        detector_name,
        type,
        standard,
        severity,
        analyzer_name,
        category,
        analyzer_result_file_path,
        checkType,
        pathEvents.toMutableList(),
        bug_path_positions.toMutableList(),
        notes.toMutableList(),
        macro_expansions.toMutableList()
    )

    override fun toString() = "Report(bugResFile=$bugResFile, region=$region, message=$message, check_name=$check_name, detector_name=$detector_name, type=$type, standard=$standard, severity=$severity, analyzer_name=$analyzer_name, category=$category, analyzer_result_file_path=$analyzer_result_file_path, checkType=$checkType, pathEvents=$pathEvents, bug_path_positions=$bug_path_positions, notes=$notes, macro_expansions=$macro_expansions)"

    override fun hashCode(): Int {
        var result = bugResFile.hashCode()
        result = 31 * result + region.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + check_name.hashCode()
        result = 31 * result + detector_name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + standard.hashCode()
        result = 31 * result + (severity?.hashCode() ?: 0)
        result = 31 * result + (analyzer_name?.hashCode() ?: 0)
        result = 31 * result + (category?.hashCode() ?: 0)
        result = 31 * result + (analyzer_result_file_path?.hashCode() ?: 0)
        result = 31 * result + checkType.hashCode()
        result = 31 * result + pathEvents.hashCode()
        result = 31 * result + bug_path_positions.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + macro_expansions.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Report) return false

        return bugResFile == other.bugResFile &&
            region == other.region &&
            message == other.message &&
            check_name == other.check_name &&
            detector_name == other.detector_name &&
            type == other.type &&
            standard == other.standard &&
            severity == other.severity &&
            analyzer_name == other.analyzer_name &&
            category == other.category &&
            analyzer_result_file_path == other.analyzer_result_file_path &&
            checkType == other.checkType &&
            pathEvents == other.pathEvents &&
            bug_path_positions == other.bug_path_positions &&
            notes == other.notes &&
            macro_expansions == other.macro_expansions
    }

    @JvmStatic
    fun JsonBuilder.`deprecatedRuleCategoryMap$lambda$46$lambda$44`() {
        this.setUseArrayPolymorphism(true)
        this.setPrettyPrint(true)
    }

    companion object {
        private val deprecatedRuleCategoryMap: Map<String, String> = TODO("Initialize properly")
        var hashVersion: Int = 0
            internal set

        private fun getFinalReportCheckerName(checker: IChecker): String = checker.javaClass.name

        fun of(
            info: SootInfoCache?,
            file: IBugResInfo,
            region: Region,
            checkType: CheckType,
            env: Env,
            pathEvents: List<BugPathEvent> = emptyList()
        ): Report {
            val pathEventsMutable = if (env is AbstractBugEnv && env.appendEvents.isNotEmpty()) {
                env.appendEvents.mapNotNull { it(BugPathEventEnvironment(info)) }
            } else null

            val message = ReportKt.bugMessage(checkType, env)
            val checkName = CheckType2StringKind.Companion.checkType2StringKind.convert(checkType)
            val checkerName = getFinalReportCheckerName(checkType.checker)
            val typeStr = checkType.toString()
            val categoryStr = categoryStr(checkType)
            val standards = checkType.standards
            val finalPathEvents = pathEventsMutable?.let { pathEvents + it } ?: pathEvents

            return Report(
                file,
                region,
                message,
                checkName,
                checkerName,
                typeStr,
                standards,
                null,
                null,
                categoryStr,
                null,
                checkType,
                finalPathEvents.toMutableList()
            )
        }

        private fun CheckType.categoryStr(): String {
            return when (hashVersion) {
                1 -> deprecatedRuleCategoryMap[CheckType2StringKind.RuleDotTYName.convert(this)] ?: "unknown"
                2 -> CheckType2StringKind.Companion.checkType2StringKind.convert(this)
                else -> throw IllegalStateException("Bad hash version: ${hashVersion}")
            }
        }
    }

    enum class HashType {
        CONTEXT_FREE,
        PATH_SENSITIVE,
        DIAGNOSTIC_MESSAGE;

        companion object {
            @JvmStatic
            fun getEntries(): EnumEntries<HashType> = entries
        }
    }
}