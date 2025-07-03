package cn.sast.framework.report.sqldelight

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.QueryKt
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.TransactionWithReturn
import app.cash.sqldelight.Transacter.DefaultImpls
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function3

public class RuleMappingQueries(driver: SqlDriver) : TransacterImpl(driver) {
    public fun <T : Any> verify_rule_name(mapper: (String, String?, String?) -> T): ExecutableQuery<T> {
        return Verify_rule_nameQuery(this) { cursor ->
            mapper(cursor.getString(0)!!, cursor.getString(1), cursor.getString(2))
        }
    }

    public fun verify_rule_name(): ExecutableQuery<RuleMapping> {
        return verify_rule_name { rule_name, standard_name, standard_rule ->
            RuleMapping(rule_name, standard_name, standard_rule)
        }
    }

    public fun <T : Any> selectAll(mapper: (String, String?, String?) -> T): Query<T> {
        return QueryKt.Query(
            1383017866,
            arrayOf("RuleMapping"),
            this.driver,
            "RuleMapping.sq",
            "selectAll",
            "SELECT RuleMapping.rule_name, RuleMapping.standard_name, RuleMapping.standard_rule\nFROM RuleMapping"
        ) { cursor ->
            mapper(cursor.getString(0)!!, cursor.getString(1), cursor.getString(2))
        }
    }

    public fun selectAll(): Query<RuleMapping> {
        return selectAll { rule_name, standard_name, standard_rule ->
            RuleMapping(rule_name, standard_name, standard_rule)
        }
    }

    public fun insert(RuleMapping: RuleMapping) {
        driver.execute(
            -1057683884,
            "INSERT OR IGNORE INTO RuleMapping (rule_name, standard_name, standard_rule)\nVALUES (?, ?, ?)",
            3
        ) { stmt ->
            stmt.bindString(0, RuleMapping.rule_name)
            stmt.bindString(1, RuleMapping.standard_name)
            stmt.bindString(2, RuleMapping.standard_rule)
        }
        notifyQueries(-1057683884) { emitter ->
            emitter("RuleMapping")
        }
    }

    private inner class Verify_rule_nameQuery<T>(
        private val this$0: RuleMappingQueries,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult(
                this$0,
                false
            ) {
                driver.executeQuery(
                    -976712076,
                    "SELECT RuleMapping.rule_name, RuleMapping.standard_name, RuleMapping.standard_rule FROM RuleMapping WHERE rule_name NOT IN (SELECT name FROM Rule)",
                    mapper
                )
            } as QueryResult<R>
        }

        override fun toString(): String {
            return "RuleMapping.sq:verify_rule_name"
        }
    }
}