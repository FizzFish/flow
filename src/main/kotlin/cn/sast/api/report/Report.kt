package cn.sast.api.report

import cn.sast.api.util.*
import cn.sast.common.GLB
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.*
import com.feysh.corax.config.api.report.Region
import cn.sast.api.config.MainConfig
import com.feysh.corax.config.api.*
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8
/**
 * 报告核心实体。
 *
 * * `bugResFile`：主定位文件/类
 * * `pathEvents` 首位自动包含主定位
 */
data class Report(
    val bugResFile: IBugResInfo,
    val region: Region,
    val message: Map<Language, String>,
    val checkName: String,
    val detectorName: String,
    val type: String,
    val standard: Set<IRule>,
    val severity: String? = null,
    val analyzerName: String? = null,
    val category: String? = null,
    val analyzerResultFile: String? = null,
    val checkType: CheckType,
    val pathEvents: MutableList<BugPathEvent> = mutableListOf(),
    val bugPathPositions: MutableList<BugPathPosition> = mutableListOf(),
    val notes: MutableList<BugPathEvent> = mutableListOf(),
    val macroExpansions: MutableList<MacroExpansion> = mutableListOf()
) : Comparable<Report>, IReportHashAble {

    /** 涉及到的所有类 / 文件 */
    val classes: Set<IBugResInfo> by lazy {
        buildSet {
            add(bugResFile)
            pathEvents.forEach { add(it.classname) }
            bugPathPositions.forEach { add(it.classname) }
            notes.forEach { add(it.classname) }
            macroExpansions.forEach { add(it.classname) }
        }
    }

    init {
        GLB += checkType
        pathEvents += BugPathEvent(message, bugResFile, region)
    }

    /* ---------- Hash ---------- */

    override fun reportHash(c: IReportHashCalculator): String =
        "${bugResFile.reportHash(c)}:$region,$checkName,$detectorName,$type,$category,$severity,$analyzerName"

    fun reportHash(
        calc: IReportHashCalculator,
        kind: HashType
    ): String = when (kind) {
        HashType.CONTEXT_FREE ->
            md5Hex(reportHash(calc))
        HashType.PATH_SENSITIVE ->
            md5Hex("${reportHash(calc)}|${pathEvents.last().reportHash(calc)}")
        HashType.DIAGNOSTIC_MESSAGE -> {
            val msgs = buildList {
                add(reportHash(calc))
                pathEvents.firstOrNull()?.let { add(it.reportHashWithMessage(calc)) }
                if (pathEvents.size >= 2)
                    pathEvents.last().let { add(it.reportHashWithMessage(calc)) }
            }
            md5Hex(msgs.joinToString("|"))
        }
    }

    /* ---------- Comparable ---------- */

    override fun compareTo(other: Report): Int =
        analyzerName.compareToNullable(other.analyzerName)
            .takeIf { it != 0 } ?: bugResFile.compareTo(other.bugResFile)
            .takeIf { it != 0 } ?: region.startLine.compareTo(other.region.startLine)
            .takeIf { it != 0 } ?: message.compareToMap(other.message)
            .takeIf { it != 0 } ?: checkName.compareTo(other.checkName)
            .takeIf { it != 0 } ?: detectorName.compareTo(other.detectorName)
            .takeIf { it != 0 } ?: type.compareTo(other.type)
//            .takeIf { it != 0 } ?: checkType.compare(other.checkType)
//            .takeIf { it != 0 } ?: standard.compareToSet(other.standard)
            .takeIf { it != 0 } ?: region.startColumn.compareTo(other.region.startColumn)
            .takeIf { it != 0 } ?: severity.compareToNullable(other.severity)
            .takeIf { it != 0 } ?: category.compareToNullable(other.category)
            .takeIf { it != 0 } ?: analyzerResultFile.compareToNullable(other.analyzerResultFile)
            .takeIf { it != 0 } ?: pathEvents.compareToCollection(other.pathEvents)
            .takeIf { it != 0 } ?: bugPathPositions.compareToCollection(other.bugPathPositions)
            .takeIf { it != 0 } ?: notes.compareToCollection(other.notes)
            .takeIf { it != 0 } ?: macroExpansions.compareToCollection(other.macroExpansions)

    /* ---------- Companion / Factory ---------- */

    companion object {

        var hashVersion: Int = 0

        /** 过渡期映射表（如需） */
        private val deprecatedRuleCategoryMap: Map<String, String> = emptyMap()

        fun of(
            cache: SootInfoCache?,
            file: IBugResInfo,
            region: Region,
            checkType: CheckType,
            env: BugMessage.Env,
            extraEvents: List<BugPathEvent> = emptyList()
        ): Report {
            val msgMap = checkType.bugMessage(env)
            val checkerName = checkType.checker::class.java.simpleName
            val kind = CheckType2StringKind.active.convert(checkType)

            // 追加 lazy 事件
            val added = if (env is AbstractBugEnv && env.appendEvents.isNotEmpty()) {
                env.appendEvents.mapNotNull { it(BugPathEventEnvironment(cache)) }
            } else emptyList()

            return Report(
                bugResFile = file,
                region = region,
                message = msgMap,
                checkName = kind,
                detectorName = checkerName,
                type = checkType.toString(),
                standard = checkType.standards,
                category = categoryStr(checkType),
                checkType = checkType,
                pathEvents = (extraEvents + added).toMutableList()
            )
        }

        private fun categoryStr(ct: CheckType): String = when (hashVersion) {
            1 -> deprecatedRuleCategoryMap[CheckType2StringKind.RuleDotTYName.convert(ct)] ?: "unknown"
            2 -> CheckType2StringKind.active.convert(ct)
            else -> "unknown"
        }

        /* ---- helpers ---- */
        private fun md5Hex(s: String): String = md5(s).toHex()
    }

    enum class HashType { CONTEXT_FREE, PATH_SENSITIVE, DIAGNOSTIC_MESSAGE }
}


fun md5(str: String): ByteArray =
    MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))

fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

/* ---------- CheckType helpers ---------- */

fun CheckType.bugMessage(lang: Language, env: BugMessage.Env): String =
    bugMessage[lang]?.msg?.invoke(env) ?: "$lang missing for $this"

fun CheckType.bugMessage(env: BugMessage.Env): Map<Language, String> =
    Language.entries.associateWith { bugMessage(it, env) }

/* ---------- prefer message by language order ---------- */

fun <T> Map<Language, T>.preferred(default: () -> T): T {
    for (lang in MainConfig.preferredLanguages) {
        this[lang]?.let { return it }
    }
    return default()
}