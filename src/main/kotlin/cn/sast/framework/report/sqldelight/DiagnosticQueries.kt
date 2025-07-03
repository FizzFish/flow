package cn.sast.framework.report.sqldelight

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.QueryKt
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.TransactionWithReturn
import app.cash.sqldelight.TransactionWithoutReturn
import app.cash.sqldelight.Transacter.DefaultImpls
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import cn.sast.framework.report.sqldelight.diagnostic.Verify_file
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function19
import kotlin.jvm.functions.Function2

public class DiagnosticQueries(driver: SqlDriver) : TransacterImpl(driver) {
    public fun id(
        rule_name: String,
        _rule_short_description_zh: String?,
        __file_id: Long?,
        _file_abs_path: String,
        severity: String?,
        precision: String?,
        likelihood: String?,
        impact: String?,
        technique: String?,
        analysis_scope: String?,
        line_content: String?,
        __note_array_hash_id: Long,
        __control_flow_array_hash_id: Long?,
        __macro_note_set_hash_id: Long?
    ): ExecutableQuery<Long> {
        return IdQuery(
            this,
            rule_name,
            _rule_short_description_zh,
            __file_id,
            _file_abs_path,
            severity,
            precision,
            likelihood,
            impact,
            technique,
            analysis_scope,
            line_content,
            __note_array_hash_id,
            __control_flow_array_hash_id,
            __macro_note_set_hash_id,
            DiagnosticQueries::id$lambda$0
        )
    }

    public fun <T : Any> verify_rule_name(mapper: (Long, String) -> T): ExecutableQuery<T> {
        return Verify_rule_nameQuery(this, mapper)
    }

    public fun verify_rule_name(): ExecutableQuery<Verify_rule_name> {
        return this.verify_rule_name(DiagnosticQueries::verify_rule_name$lambda$2)
    }

    public fun <T : Any> verify_file(mapper: (Long, Long?) -> T): ExecutableQuery<T> {
        return Verify_fileQuery(this, mapper)
    }

    public fun verify_file(): ExecutableQuery<Verify_file> {
        return this.verify_file(DiagnosticQueries::verify_file$lambda$4)
    }

    public fun <T : Any> verify_note_path(mapper: (Long, Long) -> T): ExecutableQuery<T> {
        return Verify_note_pathQuery(this, mapper)
    }

    public fun verify_note_path(): ExecutableQuery<Verify_note_path> {
        return this.verify_note_path(DiagnosticQueries::verify_note_path$lambda$6)
    }

    public fun <T : Any> verify_control_flow_path(mapper: (Long, Long) -> T): ExecutableQuery<T> {
        return Verify_control_flow_pathQuery(this, mapper)
    }

    public fun verify_control_flow_path(): ExecutableQuery<Verify_control_flow_path> {
        return this.verify_control_flow_path(DiagnosticQueries::verify_control_flow_path$lambda$8)
    }

    public fun <T : Any> verify_macro(mapper: (Long, Long) -> T): ExecutableQuery<T> {
        return Verify_macroQuery(this, mapper)
    }

    public fun verify_macro(): ExecutableQuery<Verify_macro> {
        return this.verify_macro(DiagnosticQueries::verify_macro$lambda$10)
    }

    public fun <T : Any> selectAll(
        mapper: (
            Long,
            String,
            String?,
            Long?,
            String,
            Long,
            Long,
            String,
            String,
            String?,
            String?,
            String?,
            String?,
            String?,
            String?,
            String?,
            Long,
            Long?,
            Long?
        ) -> T
    ): Query<T> {
        return QueryKt.Query(
            2051098827,
            arrayOf("Diagnostic"),
            this.driver,
            "Diagnostic.sq",
            "selectAll",
            "SELECT Diagnostic.id, Diagnostic.rule_name, Diagnostic._rule_short_description_zh, Diagnostic.__file_id, Diagnostic._file_abs_path, Diagnostic._line, Diagnostic._column, Diagnostic._message_en, Diagnostic._message_zh, Diagnostic.severity, Diagnostic.precision, Diagnostic.likelihood, Diagnostic.impact, Diagnostic.technique, Diagnostic.analysis_scope, Diagnostic.line_content, Diagnostic.__note_array_hash_id, Diagnostic.__control_flow_array_hash_id, Diagnostic.__macro_note_set_hash_id\nFROM Diagnostic",
            mapper
        )
    }

    public fun selectAll(): Query<Diagnostic> {
        return this.selectAll(DiagnosticQueries::selectAll$lambda$12)
    }

    public fun insert(
        rule_name: String,
        _rule_short_description_zh: String?,
        __file_id: Long?,
        _file_abs_path: String,
        _line: Long,
        _column: Long,
        _message_en: String,
        _message_zh: String,
        severity: String?,
        precision: String?,
        likelihood: String?,
        impact: String?,
        technique: String?,
        analysis_scope: String?,
        line_content: String?,
        __note_array_hash_id: Long,
        __control_flow_array_hash_id: Long?,
        __macro_note_set_hash_id: Long?
    ) {
        DefaultImpls.transaction(this as Transacter, false) {
            insert$lambda$14(
                this@DiagnosticQueries,
                rule_name,
                _rule_short_description_zh,
                __file_id,
                _file_abs_path,
                _line,
                _column,
                _message_en,
                _message_zh,
                severity,
                precision,
                likelihood,
                impact,
                technique,
                analysis_scope,
                line_content,
                __note_array_hash_id,
                __control_flow_array_hash_id,
                __macro_note_set_hash_id,
                it
            )
        }
        this.notifyQueries(949328563) { emit ->
            insert$lambda$15(emit)
        }
    }

    @JvmStatic
    private fun id$lambda$0(cursor: SqlCursor): Long {
        return cursor.getLong(0)!!
    }

    @JvmStatic
    private fun verify_rule_name$lambda$1(mapper: Function2<Long, String, Any>, cursor: SqlCursor): Any {
        return mapper.invoke(cursor.getLong(0)!!, cursor.getString(1)!!)
    }

    @JvmStatic
    private fun verify_rule_name$lambda$2(id: Long, rule_name: String): Verify_rule_name {
        return Verify_rule_name(id, rule_name)
    }

    @JvmStatic
    private fun verify_file$lambda$3(mapper: Function2<Long, Long?, Any>, cursor: SqlCursor): Any {
        return mapper.invoke(cursor.getLong(0)!!, cursor.getLong(1))
    }

    @JvmStatic
    private fun verify_file$lambda$4(id: Long, __file_id: Long?): Verify_file {
        return Verify_file(id, __file_id)
    }

    @JvmStatic
    private fun verify_note_path$lambda$5(mapper: Function2<Long, Long, Any>, cursor: SqlCursor): Any {
        return mapper.invoke(cursor.getLong(0)!!, cursor.getLong(1)!!)
    }

    @JvmStatic
    private fun verify_note_path$lambda$6(id: Long, __note_array_hash_id: Long): Verify_note_path {
        return Verify_note_path(id, __note_array_hash_id)
    }

    @JvmStatic
    private fun verify_control_flow_path$lambda$7(mapper: Function2<Long, Long, Any>, cursor: SqlCursor): Any {
        return mapper.invoke(cursor.getLong(0)!!, cursor.getLong(1)!!)
    }

    @JvmStatic
    private fun verify_control_flow_path$lambda$8(id: Long, __control_flow_array_hash_id: Long): Verify_control_flow_path {
        return Verify_control_flow_path(id, __control_flow_array_hash_id)
    }

    @JvmStatic
    private fun verify_macro$lambda$9(mapper: Function2<Long, Long, Any>, cursor: SqlCursor): Any {
        return mapper.invoke(cursor.getLong(0)!!, cursor.getLong(1)!!)
    }

    @JvmStatic
    private fun verify_macro$lambda$10(id: Long, __macro_note_set_hash_id: Long): Verify_macro {
        return Verify_macro(id, __macro_note_set_hash_id)
    }

    @JvmStatic
    private fun selectAll$lambda$11(
        mapper: Function19<
            Long, String, String?, Long?, String, Long, Long, String, String, 
            String?, String?, String?, String?, String?, String?, String?, 
            Long, Long?, Long?, Any
        >, 
        cursor: SqlCursor
    ): Any {
        return mapper.invoke(
            cursor.getLong(0)!!,
            cursor.getString(1)!!,
            cursor.getString(2),
            cursor.getLong(3),
            cursor.getString(4)!!,
            cursor.getLong(5)!!,
            cursor.getLong(6)!!,
            cursor.getString(7)!!,
            cursor.getString(8)!!,
            cursor.getString(9),
            cursor.getString(10),
            cursor.getString(11),
            cursor.getString(12),
            cursor.getString(13),
            cursor.getString(14),
            cursor.getString(15),
            cursor.getLong(16)!!,
            cursor.getLong(17),
            cursor.getLong(18)
        )
    }

    @JvmStatic
    private fun selectAll$lambda$12(
        id: Long,
        rule_name: String,
        _rule_short_description_zh: String?,
        __file_id: Long?,
        _file_abs_path: String,
        _line: Long,
        _column: Long,
        _message_en: String,
        _message_zh: String,
        severity: String?,
        precision: String?,
        likelihood: String?,
        impact: String?,
        technique: String?,
        analysis_scope: String?,
        line_content: String?,
        __note_array_hash_id: Long,
        __control_flow_array_hash_id: Long?,
        __macro_note_set_hash_id: Long?
    ): Diagnostic {
        return Diagnostic(
            id,
            rule_name,
            _rule_short_description_zh,
            __file_id,
            _file_abs_path,
            _line,
            _column,
            _message_en,
            _message_zh,
            severity,
            precision,
            likelihood,
            impact,
            technique,
            analysis_scope,
            line_content,
            __note_array_hash_id,
            __control_flow_array_hash_id,
            __macro_note_set_hash_id
        )
    }

    @JvmStatic
    private fun insert$lambda$14$lambda$13(
        rule_name: String,
        _rule_short_description_zh: String?,
        __file_id: Long?,
        _file_abs_path: String,
        _line: Long,
        _column: Long,
        _message_en: String,
        _message_zh: String,
        severity: String?,
        precision: String?,
        likelihood: String?,
        impact: String?,
        technique: String?,
        analysis_scope: String?,
        line_content: String?,
        __note_array_hash_id: Long,
        __control_flow_array_hash_id: Long?,
        __macro_note_set_hash_id: Long?,
        statement: SqlPreparedStatement
    ) {
        statement.bindString(0, rule_name)
        statement.bindString(1, _rule_short_description_zh)
        statement.bindLong(2, __file_id)
        statement.bindString(3, _file_abs_path)
        statement.bindLong(4, _line)
        statement.bindLong(5, _column)
        statement.bindString(6, _message_en)
        statement.bindString(7, _message_zh)
        statement.bindString(8, severity)
        statement.bindString(9, precision)
        statement.bindString(10, likelihood)
        statement.bindString(11, impact)
        statement.bindString(12, technique)
        statement.bindString(13, analysis_scope)
        statement.bindString(14, line_content)
        statement.bindLong(15, __note_array_hash_id)
        statement.bindLong(16, __control_flow_array_hash_id)
        statement.bindLong(17, __macro_note_set_hash_id)
    }

    @JvmStatic
    private fun insert$lambda$14(
        this$0: DiagnosticQueries,
        rule_name: String,
        _rule_short_description_zh: String?,
        __file_id: Long?,
        _file_abs_path: String,
        _line: Long,
        _column: Long,
        _message_en: String,
        _message_zh: String,
        severity: String?,
        precision: String?,
        likelihood: String?,
        impact: String?,
        technique: String?,
        analysis_scope: String?,
        line_content: String?,
        __note_array_hash_id: Long,
        __control_flow_array_hash_id: Long?,
        __macro_note_set_hash_id: Long?,
        transaction: TransactionWithoutReturn
    ) {
        this$0.driver.execute(
            1771685284,
            """
            INSERT OR IGNORE INTO Diagnostic(rule_name, _rule_short_description_zh,
                                     __file_id, _file_abs_path,
                                     _line, _column, _message_en, _message_zh,
                                     severity, precision, likelihood, impact, technique, analysis_scope,
                                     line_content,
                                     __note_array_hash_id, __control_flow_array_hash_id, __macro_note_set_hash_id
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            18
        ) { statement ->
            insert$lambda$14$lambda$13(
                rule_name,
                _rule_short_description_zh,
                __file_id,
                _file_abs_path,
                _line,
                _column,
                _message_en,
                _message_zh,
                severity,
                precision,
                likelihood,
                impact,
                technique,
                analysis_scope,
                line_content,
                __note_array_hash_id,
                __control_flow_array_hash_id,
                __macro_note_set_hash_id,
                statement
            )
        }
    }

    @JvmStatic
    private fun insert$lambda$15(emit: Function1<String, Unit>) {
        emit.invoke("Diagnostic")
    }

    private inner class IdQuery<T>(
        private val this$0: DiagnosticQueries,
        val rule_name: String,
        val _rule_short_description_zh: String?,
        val __file_id: Long?,
        val _file_abs_path: String,
        val severity: String?,
        val precision: String?,
        val likelihood: String?,
        val impact: String?,
        val technique: String?,
        val analysis_scope: String?,
        val line_content: String?,
        val __note_array_hash_id: Long,
        val __control_flow_array_hash_id: Long?,
        val __macro_note_set_hash_id: Long?,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult(this$0 as Transacter, false) { transaction ->
                execute$lambda$1(this$0, this, mapper, transaction)
            }
        }

        override fun toString(): String {
            return "Diagnostic.sq:id"
        }

        companion object {
            @JvmStatic
            private fun execute$lambda$1$lambda$0(
                this$0: IdQuery<*>,
                statement: SqlPreparedStatement
            ) {
                statement.bindString(0, this$0.rule_name)
                statement.bindString(1, this$0._rule_short_description_zh)
                statement.bindLong(2, this$0.__file_id)
                statement.bindString(3, this$0._file_abs_path)
                statement.bindString(4, this$0.severity)
                statement.bindString(5, this$0.precision)
                statement.bindString(6, this$0.likelihood)
                statement.bindString(7, this$0.impact)
                statement.bindString(8, this$0.technique)
                statement.bindString(9, this$0.analysis_scope)
                statement.bindString(10, this$0.line_content)
                statement.bindLong(11, this$0.__note_array_hash_id)
                statement.bindLong(12, this$0.__control_flow_array_hash_id)
                statement.bindLong(13, this$0.__macro_note_set_hash_id)
            }

            @JvmStatic
            private fun execute$lambda$1(
                this$0: DiagnosticQueries,
                this$1: IdQuery<*>,
                mapper: Function1<SqlCursor, QueryResult<*>>,
                transaction: TransactionWithReturn
            ): QueryResult<*> {
                return this$0.driver.executeQuery(
                    null,
                    """
                    |SELECT id FROM Diagnostic WHERE rule_name = ? AND _rule_short_description_zh ${if (this$1._rule_short_description_zh == null) "IS" else "="} ? AND __file_id ${if (this$1.__file_id == null) "IS" else "="} ? AND _file_abs_path = ?
                    |    AND severity ${if (this$1.severity == null) "IS" else "="} ? AND precision ${if (this$1.precision == null) "IS" else "="} ? AND likelihood ${if (this$1.likelihood == null) "IS" else "="} ? AND impact ${if (this$1.impact == null) "IS" else "="} ? AND technique ${if (this$1.technique == null) "IS" else "="} ? AND analysis_scope ${if (this$1.analysis_scope == null) "IS" else "="} ? AND line_content ${if (this$1.line_content == null) "IS" else "="} ?
                    |    AND __note_array_hash_id = ? AND __control_flow_array_hash_id ${if (this$1.__control_flow_array_hash_id == null) "IS" else "="} ? AND __macro_note_set_hash_id ${if (this$1.__macro_note_set_hash_id == null) "IS" else "="} ?
                    """.trimMargin(),
                    mapper,
                    14
                ) { statement ->
                    execute$lambda$1$lambda$0(this$1, statement)
                }
            }
        }
    }

    private inner class Verify_control_flow_pathQuery<T>(
        private val this$0: DiagnosticQueries,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult(this$0 as Transacter, false) { transaction ->
                execute$lambda$0(this$0, mapper, transaction)
            }
        }

        override fun toString(): String {
            return "Diagnostic.sq:verify_control_flow_path"
        }

        companion object {
            @JvmStatic
            private fun execute$lambda$0(
                this$0: DiagnosticQueries,
                mapper: Function1<SqlCursor, QueryResult<*>>,
                transaction: TransactionWithReturn
            ): QueryResult<*> {
                return this$0.driver.executeQuery(
                    -545866471,
                    "SELECT id, __control_flow_array_hash_id FROM Diagnostic WHERE __control_flow_array_hash_id IS NOT NULL AND __control_flow_array_hash_id NOT IN (SELECT __control_flow_array_hash_id FROM ControlFlowPath)",
                    mapper,
                    0
                )
            }
        }
    }

    private inner class Verify_fileQuery<T>(
        private val this$0: DiagnosticQueries,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult(this$0 as Transacter, false) { transaction ->
                execute$lambda$0(this$0, mapper, transaction)
            }
        }

        override fun toString(): String {
            return "Diagnostic.sq:verify_file"
        }

        companion object {
            @JvmStatic
            private fun execute$lambda$0(
                this$0: DiagnosticQueries,
                mapper: Function1<SqlCursor, QueryResult<*>>,
                transaction: TransactionWithReturn
            ): QueryResult<*> {
                return this$0.driver.executeQuery(
                    2056148857,
                    "SELECT id, __file_id FROM Diagnostic WHERE __file_id NOT IN (SELECT id FROM File)",
                    mapper,
                    0
                )
            }
        }
    }

    private inner class Verify_macroQuery<T>(
        private val this$0: DiagnosticQueries,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult(this$0 as Transacter, false) { transaction ->
                execute$lambda$0(this$0, mapper, transaction)
            }
        }

        override fun toString(): String {
            return "Diagnostic.sq:verify_macro"
        }

        companion object {
            @JvmStatic
            private fun execute$lambda$0(
                this$0: DiagnosticQueries,
                mapper: Function1<SqlCursor, QueryResult<*>>,
                transaction: TransactionWithReturn
            ): QueryResult<*> {
                return this$0.driver.executeQuery(
                    996722865,
                    "SELECT id, __macro_note_set_hash_id FROM Diagnostic WHERE __macro_note_set_hash_id IS NOT NULL AND __macro_note_set_hash_id NOT IN (SELECT __macro_note_set_hash_id FROM MacroExpansion)",
                    mapper,
                    0
                )
            }
        }
    }

    private inner class Verify_note_pathQuery<T>(
        private val this$0: DiagnosticQueries,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult(this$0 as Transacter, false) { transaction ->
                execute$lambda$0(this$0, mapper, transaction)
            }
        }

        override fun toString(): String {
            return "Diagnostic.sq:verify_note_path"
        }

        companion object {
            @JvmStatic
            private fun execute$lambda$0(
                this$0: DiagnosticQueries,
                mapper: Function1<SqlCursor, QueryResult<*>>,
                transaction: TransactionWithReturn
            ): QueryResult<*> {
                return this$0.driver.executeQuery(
                    -1475379785,
                    "SELECT id, __note_array_hash_id FROM Diagnostic WHERE __note_array_hash_id NOT IN (SELECT __note_array_hash_id FROM NotePath)",
                    mapper,
                    0
                )
            }
        }
    }

    private inner class Verify_rule_nameQuery<T>(
        private val this$0: DiagnosticQueries,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult(this$0 as Transacter, false) { transaction ->
                execute$lambda$0(this$0, mapper, transaction)
            }
        }

        override fun toString(): String {
            return "Diagnostic.sq:verify_rule_name"
        }

        companion object {
            @JvmStatic
            private fun execute$lambda$0(
                this$0: DiagnosticQueries,
                mapper: Function1<SqlCursor, QueryResult<*>>,
                transaction: TransactionWithReturn
            ): QueryResult<*> {
                return this$0.driver.executeQuery(
                    264932435,
                    "SELECT id, rule_name FROM Diagnostic WHERE rule_name NOT IN (SELECT name FROM Rule)",
                    mapper,
                    0
                )
            }
        }
    }
}