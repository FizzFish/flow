package cn.sast.framework.report

import app.cash.sqldelight.TransactionWithoutReturn
import app.cash.sqldelight.Transacter.DefaultImpls
import cn.sast.api.config.ChapterFlat
import cn.sast.api.config.CheckerInfo
import cn.sast.api.config.CheckerInfoGenResult
import cn.sast.api.config.CheckerPriorityConfig
import cn.sast.api.config.Tag
import cn.sast.common.Resource
import cn.sast.framework.report.sqldelight.Database
import cn.sast.framework.report.sqldelight.Rule
import cn.sast.framework.report.sqldelight.RuleMapping
import cn.sast.framework.report.sqldelight.RuleMappingQueries
import cn.sast.framework.report.sqldelight.RuleQueries
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger

@SourceDebugExtension(["SMAP\nSqliteDiagnostics.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SqliteDiagnostics.kt\ncn/sast/framework/report/RuleAndRuleMapping\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,813:1\n1202#2,2:814\n1230#2,4:816\n1202#2,2:820\n1230#2,4:822\n1628#2,2:826\n1630#2:829\n1628#2,3:830\n774#2:833\n865#2,2:834\n1863#2,2:836\n774#2:838\n865#2,2:839\n1863#2,2:841\n1#3:828\n*S KotlinDebug\n*F\n+ 1 SqliteDiagnostics.kt\ncn/sast/framework/report/RuleAndRuleMapping\n*L\n137#1:814,2\n137#1:816,4\n147#1:820,2\n147#1:822,4\n151#1:826,2\n151#1:829\n194#1:830,3\n208#1:833\n208#1:834,2\n208#1:836,2\n209#1:838\n209#1:839,2\n209#1:841,2\n*E\n"])
class RuleAndRuleMapping(private val checkerInfo: CheckerInfoGenResult, private val ruleSortYaml: Path) {
    private val rules: Set<Rule>
    private val ruleMapping: Set<RuleMapping>
    var id2checkerMap: Map<String, Rule>
        internal set

    init {
        id2checkerMap = LinkedHashMap()
        val rulesAndRuleMappingsPair = parseRuleAndRuleMapping(ruleSortYaml)
        rules = rulesAndRuleMappingsPair.first.toMutableSet()
        ruleMapping = rulesAndRuleMappingsPair.second.toMutableSet()
        id2checkerMap = rules.associateBy { it.name }
    }

    private fun parseRuleAndRuleMapping(ruleSortYaml: Path): Pair<Set<Rule>, Set<RuleMapping>> {
        val ruleWithSortNumber = if (!Files.exists(ruleSortYaml, *emptyArray())) {
            logger.warn { "The ruleSortYaml $ruleSortYaml is not exists" }
            null
        } else {
            CheckerPriorityConfig.deserialize(Resource.fileOf(ruleSortYaml))
                .getRuleWithSortNumber(checkerInfo.chapters)
                .associate { (it.value as ChapterFlat).ruleId to it }
        }

        val checkerInfoList = checkerInfo.checkerInfoList
        val ruleMappingSet = LinkedHashSet<RuleMapping>()
        val ruleSet = LinkedHashSet<Rule>()

        for (checker in checkerInfoList) {
            val name = checker.checker_id
            val ruleSortNumber = ruleWithSortNumber?.get(name) as? IndexedValue<*>
            if (ruleWithSortNumber != null && ruleSortNumber == null) {
                logger.warn { "can't find the category '${checker.category["zh-CN"]}' of $name in $ruleSortYaml" }
            }

            val chapterFlat = checker.chapterFlat
            ruleMappingSet.addAll(getCurrentRuleMappingSet(checker, name))

            ruleSet.add(
                Rule(
                    name = name,
                    nameEn = checker.name["en-US"] ?: "None",
                    nameCn = checker.name["zh-CN"] ?: "None",
                    severity = checker.severity,
                    language = "Java",
                    precision = checker.precision,
                    reCall = checker.reCall,
                    likelihood = checker.likelihood,
                    impact = checker.impact,
                    impl = checker.impl,
                    abstractEn = checker.abstract["en-US"] ?: "None",
                    abstractCn = checker.abstract["zh-CN"] ?: "None",
                    descriptionEn = checker.description["en-US"] ?: "None",
                    descriptionCn = checker.description["zh-CN"] ?: "None",
                    categoryEn = checker.category["en-US"] ?: "None",
                    categoryCn = checker.category["zh-CN"] ?: "None",
                    sortNumber = ruleSortNumber?.index?.toLong() ?: (checkerInfoList.size + 1L),
                    chapterCategory = chapterFlat?.category,
                    chapterSeverity = chapterFlat?.severity,
                    chapterRuleId = chapterFlat?.ruleId,
                    implemented = if (checker.implemented != null) {
                        if (checker.implemented) 1L else 0L
                    } else null
                )
            )
        }

        return Pair(ruleSet, ruleMappingSet)
    }

    private fun getCurrentRuleMappingSet(row: CheckerInfo, name: String): Set<RuleMapping> {
        return row.tags.mapTo(LinkedHashSet()) { tag ->
            RuleMapping(name, tag.standard, tag.rule)
        }
    }

    fun insert(database: Database) {
        insert(database, null)
    }

    fun insert(database: Database, filter: Set<String>?) {
        database.transaction {
            val filteredRules = rules.filter { filter == null || filter.contains(it.name) }
            database.ruleQueries.apply {
                filteredRules.forEach { insert(it) }
            }

            val filteredMappings = ruleMapping.filter { filter == null || filter.contains(it.rule_name) }
            database.ruleMappingQueries.apply {
                filteredMappings.forEach { insert(it) }
            }
        }
    }

    companion object {
        private val logger: KLogger = TODO("Initialize logger")
    }
}