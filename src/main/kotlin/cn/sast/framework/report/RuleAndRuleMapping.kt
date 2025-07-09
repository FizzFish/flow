package cn.sast.framework.report

import app.cash.sqldelight.TransactionWithoutReturn
import cn.sast.api.config.*
import cn.sast.common.Resource
import cn.sast.framework.report.sqldelight.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists

/**
 * 负责把 `checker_info.yaml` + `rule_sort.yaml`
 * 解析为 Rule / RuleMapping，并写入 SqlDelight 数据库。
 */
class RuleAndRuleMapping(
    private val checkerInfo: CheckerInfoGenResult,
    private val ruleSortYaml: Path
) {
    /* ---------- 解析得到的结果 ---------- */
    private val rules:        Set<Rule>
    private val ruleMappings: Set<RuleMapping>

    /** 便于快速按 ruleId 查 Rule */
    var id2checkerMap: Map<String, Rule> = emptyMap()
        private set

    private val logger = KotlinLogging.logger {}

    init {
        val (ruleSet, mappingSet) = parseRuleAndRuleMapping(ruleSortYaml)
        rules        = ruleSet
        ruleMappings = mappingSet
        id2checkerMap = rules.associateBy { it.name }
    }

    /* =================================================================== */
    /*  写入数据库                                                          */
    /* =================================================================== */
    fun insert(
        database: Database,
        filter: Set<String>? = null       // 允许只写入指定子集
    ) {
        database.transaction {
            rules.filter { filter == null || it.name in filter }
                .forEach { database.ruleQueries.insert(it) }

            ruleMappings.filter { filter == null || it.rule_name in filter }
                .forEach { database.ruleMappingQueries.insert(it) }
        }
    }

    /* =================================================================== */
    /*  YAML → Rule / RuleMapping                                          */
    /* =================================================================== */
    private fun parseRuleAndRuleMapping(
        ruleSortYaml: Path
    ): Pair<Set<Rule>, Set<RuleMapping>> {
        if (!ruleSortYaml.exists()) {
            logger.warn { "The ruleSortYaml $ruleSortYaml does not exist" }
        }

        /* ---- 1) 读取排序配置（可选） ---- */
        val sortIndexMap: Map<String, Int> = runCatching {
            val cfg = CheckerPriorityConfig.deserialize(Resource.fileOf(ruleSortYaml))
            cfg.ruleWithSortNumber(checkerInfo.chapters)
                .mapIndexed { idx, pair -> pair.ruleId to idx + 1 }
                .toMap()
        }.getOrElse { emptyMap() }

        /* ---- 2) 组装 Rule & RuleMapping ---- */
        val rules    = mutableSetOf<Rule>()
        val mappings = mutableSetOf<RuleMapping>()

        checkerInfo.checkerInfoList.forEach { ci ->
            val ruleId = ci.checker_id
            val sortNo = sortIndexMap[ruleId] ?: (checkerInfo.checkerInfoList.size + 1)

            // ---- Rule ----
            rules += Rule(
                name              = ruleId,
                name_en           = ci.name["en-US"] ?: "None",
                name_zh           = ci.name["zh-CN"] ?: "None",
                severity          = ci.severity,
                category_en       = ci.category["en-US"] ?: "None",
                category_zh       = ci.category["zh-CN"] ?: "None",
                sort_no           = sortNo.toLong(),
                abstract_en       = ci.abstract_["en-US"] ?: "None",
                abstract_zh       = ci.abstract_["zh-CN"] ?: "None",
                description_en    = ci.description["en-US"] ?: "None",
                description_zh    = ci.description["zh-CN"] ?: "None",
                // 其余列保持默认 / NULL
            )

            // ---- RuleMapping ----
            ci.tags.forEach { tag ->
                mappings += RuleMapping(
                    rule_name = ruleId,
                    standard  = tag.standard,
                    rule      = tag.rule
                )
            }
        }

        return rules to mappings
    }

    /* =================================================================== */
    /*  扩展：将结构序列化为 JSON（调试辅助）                                */
    /* =================================================================== */
    fun toJson(pretty: Boolean = true): String =
        Json { prettyPrint = pretty }.encodeToString(
            Json.serializersModule.serializer(),        // 泛型写法：Any
            mapOf(
                "rules"        to rules,
                "ruleMappings" to ruleMappings
            )
        )
}
