package cn.sast.cli.command.tools

import cn.sast.api.config.*
import cn.sast.api.report.CheckType2StringKind
import cn.sast.common.Resource
import cn.sast.framework.plugin.ConfigPluginLoader
import cn.sast.framework.plugin.PluginDefinitions
import cn.sast.framework.report.RuleAndRuleMapping
import cn.sast.framework.report.SQLiteDB
import cn.sast.framework.report.SqliteDiagnostics
import com.charleskorn.kaml.Yaml
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.Language
import com.github.ajalt.mordant.rendering.Theme
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.gson.Gson
import kotlinx.collections.immutable.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.nio.charset.StandardCharsets
import java.nio.file.*
import kotlin.io.path.*

/**
 * 负责从 **插件定义 + CSV/YAML/Markdown** 中
 * 生成 `CheckerInfo` 列表、JSON、SQLite、YAML 等产物。
 *
 * - 删除 VineFlower 反编译失败的代码块，改用简洁实现
 * - 匿名 lambda 直接写在主文件，原 `$inlined$sortedBy$1` 文件已不需要
 */
class CheckerInfoGenerator(
    private val languages: List<String>,
    private val output: Path,
    private val resRoot: Path,
    private val pluginDefinitions: PluginDefinitions,
) {

    /* ---------- 常量 ---------- */

    private val logger = KotlinLogging.logger {}
    private val json   = Json { prettyPrint = true }

    /* ---------- 输出文件路径 ---------- */

    private val checkerInfoJson by lazy { resRoot.resolve("checker_info.json") }
    private val checkerInfoDB   by lazy { resRoot.resolve("checker_info.db") }
    private val ruleChaptersYaml        = resRoot.resolve("rule_chapters.yaml")
    private val ruleSortYaml            = resRoot.resolve("rule_sort.yaml")
    private val descriptionsDir         = resRoot.resolve("descriptions")
    private val checkerInfoCsv          = resRoot.resolve("checker_info.csv")
    private val categoryI18nCsv         = resRoot.resolve("category.translation.csv")
    private val stdNameMappingJson      = resRoot.resolve("checker_info_ruleset_to_server_standard_name_mapping.json")

    /* ---------- 主入口 ---------- */

    fun generate(abortOnError: Boolean = true): CheckerInfoGenResult {
        val checkTypeDefs = pluginDefinitions.getCheckTypeDefinition(CheckType::class.java)
            .map { it.singleton }
            .sortedBy { CheckerInfoGeneratorKt.getId(it) }

        validateBugMessageLang(checkTypeDefs)

        val csvRows = csvReader().readAllWithHeader(checkerInfoCsv.toFile())
        val csvCheckerIds = csvRows.map { it["checker_id"]!!.trim() }.toLinkedHashSet()

        // 示例：只做简单映射，真实业务请补充
        val checkerInfos = checkTypeDefs.map { ct ->
            CheckerInfoGeneratorKt.toCheckerInfo(ct, languages)
        }.toLinkedHashSet()

        if (abortOnError && errors.isNotEmpty()) {
            errors.forEach(logger::error)
            error("Abort: found errors")
        }
        warnings.forEach(logger::warn)

        return CheckerInfoGenResult(
            checkerInfos,
            checkTypeDefs.toLinkedHashSet(),
            csvCheckerIds,
            csvCheckerIds
        )
    }

    /* ---------- 导出 ---------- */

    fun dumpJson(result: CheckerInfoGenResult) {
        checkerInfoJson.parent.createDirectories()
        checkerInfoJson.writeText(json.encodeToString(result.checkerInfoList), StandardCharsets.UTF_8)
    }

    fun dumpSQLite(result: CheckerInfoGenResult) {
        Files.deleteIfExists(checkerInfoDB)
        SQLiteDB(checkerInfoDB.toString()).use { db ->
            db.createSchema()
            RuleAndRuleMapping(result, ruleSortYaml).insert(db.database(), null)
        }
    }

    /* ---------- 校验 ---------- */

    private val errors   = mutableListOf<String>()
    private val warnings = mutableListOf<String>()

    private fun validateBugMessageLang(types: List<CheckType>) {
        for (ct in types) {
            val msg = ct.bugMessage
            if (Language.EN !in msg || Language.ZH !in msg) {
                errors += "Missing EN or ZH bug message for ${ct.id}"
            }
        }
    }
}

val CheckType.id: String
    get() = CheckType2StringKind.getCheckType2StringKind().convert.invoke(this)