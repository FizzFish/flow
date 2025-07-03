package cn.sast.framework.report.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.QueryKt
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.FunctionN

public class RuleQueries(driver: SqlDriver) : TransacterImpl(driver) {
    public fun <T : Any> selectAll(
        mapper: (
            String,
            String,
            String,
            String?,
            String?,
            String,
            String?,
            String?,
            String?,
            String?,
            String?,
            String?,
            String?,
            Long?,
            Long?,
            String?,
            String?,
            String,
            String,
            Long?,
            String?,
            String?,
            String?,
            String?,
            String,
            String?,
            String,
            String
        ) -> T
    ): Query<T> {
        return QueryKt.Query(
            177633238,
            arrayOf("Rule"),
            this.getDriver(),
            "Rule.sq",
            "selectAll",
            "SELECT Rule.name, Rule.short_description_en, Rule.short_description_zh, Rule.severity, Rule.priority, Rule.language, Rule.precision, Rule.recall, Rule.likelihood, Rule.impact, Rule.technique, Rule.analysis_scope, Rule.performance, Rule.configurable, Rule.implemented, Rule.static_analyzability, Rule.c_allocated_target, Rule.category_en, Rule.category_zh, Rule.rule_sort_number, Rule.chapter_name_1, Rule.chapter_name_2, Rule.chapter_name_3, Rule.chapter_name_4, Rule.description_en, Rule.description_zh, Rule.document_en, Rule.document_zh\nFROM Rule",
            RuleQueries::selectAll$lambda$0
        )
    }

    public fun selectAll(): Query<Rule> {
        TODO("FIXME — unrepresentable instance")
    }

    public fun insert(rule: Rule) {
        this.getDriver()
            .execute(
                -1201750136,
                "INSERT OR IGNORE INTO Rule (name, short_description_en, short_description_zh, severity, priority, language, precision, recall, likelihood, impact, technique, analysis_scope, performance, configurable, implemented, static_analyzability, c_allocated_target, category_en, category_zh, rule_sort_number, chapter_name_1, chapter_name_2, chapter_name_3, chapter_name_4, description_en, description_zh, document_en, document_zh) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                28,
                RuleQueries::insert$lambda$1
            )
        this.notifyQueries(-1201750136, RuleQueries::insert$lambda$2)
    }

    @JvmStatic
    private fun selectAll$lambda$0(mapper: FunctionN, cursor: SqlCursor): Any {
        val values = arrayOfNulls<Any?>(28)
        values[0] = cursor.getString(0)
        values[1] = cursor.getString(1)
        values[2] = cursor.getString(2)
        values[3] = cursor.getString(3)
        values[4] = cursor.getString(4)
        values[5] = cursor.getString(5)
        values[6] = cursor.getString(6)
        values[7] = cursor.getString(7)
        values[8] = cursor.getString(8)
        values[9] = cursor.getString(9)
        values[10] = cursor.getString(10)
        values[11] = cursor.getString(11)
        values[12] = cursor.getString(12)
        values[13] = cursor.getLong(13)
        values[14] = cursor.getLong(14)
        values[15] = cursor.getString(15)
        values[16] = cursor.getString(16)
        values[17] = cursor.getString(17)
        values[18] = cursor.getString(18)
        values[19] = cursor.getLong(19)
        values[20] = cursor.getString(20)
        values[21] = cursor.getString(21)
        values[22] = cursor.getString(22)
        values[23] = cursor.getString(23)
        values[24] = cursor.getString(24)
        values[25] = cursor.getString(25)
        values[26] = cursor.getString(26)
        values[27] = cursor.getString(27)
        return mapper.invoke(*values)
    }

    @JvmStatic
    private fun insert$lambda$1(rule: Rule, statement: SqlPreparedStatement) {
        statement.bindString(0, rule.name)
        statement.bindString(1, rule.short_description_en)
        statement.bindString(2, rule.short_description_zh)
        statement.bindString(3, rule.severity)
        statement.bindString(4, rule.priority)
        statement.bindString(5, rule.language)
        statement.bindString(6, rule.precision)
        statement.bindString(7, rule.recall)
        statement.bindString(8, rule.likelihood)
        statement.bindString(9, rule.impact)
        statement.bindString(10, rule.technique)
        statement.bindString(11, rule.analysis_scope)
        statement.bindString(12, rule.performance)
        statement.bindLong(13, rule.configurable)
        statement.bindLong(14, rule.implemented)
        statement.bindString(15, rule.static_analyzability)
        statement.bindString(16, rule.c_allocated_target)
        statement.bindString(17, rule.category_en)
        statement.bindString(18, rule.category_zh)
        statement.bindLong(19, rule.rule_sort_number)
        statement.bindString(20, rule.chapter_name_1)
        statement.bindString(21, rule.chapter_name_2)
        statement.bindString(22, rule.chapter_name_3)
        statement.bindString(23, rule.chapter_name_4)
        statement.bindString(24, rule.description_en)
        statement.bindString(25, rule.description_zh)
        statement.bindString(26, rule.document_en)
        statement.bindString(27, rule.document_zh)
    }

    @JvmStatic
    private fun insert$lambda$2(emit: Function1<in String, Unit>) {
        emit.invoke("Rule")
    }
}