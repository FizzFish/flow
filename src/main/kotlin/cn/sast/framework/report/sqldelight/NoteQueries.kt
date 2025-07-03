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
import cn.sast.framework.report.sqldelight.note.Verify_file
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function11
import kotlin.jvm.functions.Function2

public class NoteQueries(driver: SqlDriver) : TransacterImpl(driver) {
    public fun id(
        kind: String,
        display_hint: String,
        __file_id: Long,
        _file_abs_path: String,
        line: Long,
        column: Long?,
        message_en: String,
        message_zh: String,
        __notices_region_id: Long?,
        __func_region_id: Long?
    ): ExecutableQuery<Long> {
        return IdQuery(
            kind,
            display_hint,
            __file_id,
            _file_abs_path,
            line,
            column,
            message_en,
            message_zh,
            __notices_region_id,
            __func_region_id,
            ::id$lambda$0
        )
    }

    public fun <T : Any> verify_file(mapper: (Long, Long) -> T): ExecutableQuery<T> {
        return Verify_fileQuery(this, Function2 { cursor -> verify_file$lambda$1(mapper, cursor) })
    }

    public fun verify_file(): ExecutableQuery<Verify_file> {
        return this.verify_file(::verify_file$lambda$2)
    }

    public fun <T : Any> selectAll(mapper: (Long, String, String, Long, String, Long, Long?, String, String, Long?, Long?) -> T): Query<T> {
        return QueryKt.Query(
            1298079392,
            arrayOf("Note"),
            this.getDriver(),
            "Note.sq",
            "selectAll",
            "SELECT Note.id, Note.kind, Note.display_hint, Note.__file_id, Note._file_abs_path, Note.line, Note.column, Note.message_en, Note.message_zh, Note.__notices_region_id, Note.__func_region_id\nFROM Note",
            Function11 { cursor -> selectAll$lambda$3(mapper, cursor) }
        )
    }

    public fun selectAll(): Query<Note> {
        return this.selectAll(::selectAll$lambda$4)
    }

    public fun insert(
        kind: String,
        display_hint: String,
        __file_id: Long,
        _file_abs_path: String,
        line: Long,
        column: Long?,
        message_en: String,
        message_zh: String,
        __notices_region_id: Long?,
        __func_region_id: Long?
    ) {
        DefaultImpls.transaction$default(this as Transacter, false, { transaction ->
            insert$lambda$6(
                this,
                kind,
                display_hint,
                __file_id,
                _file_abs_path,
                line,
                column,
                message_en,
                message_zh,
                __notices_region_id,
                __func_region_id,
                transaction
            )
        }, 1, null)
        this.notifyQueries(-681403138, { emit -> insert$lambda$7(emit) })
    }

    @JvmStatic
    private fun id$lambda$0(cursor: SqlCursor): Long {
        return cursor.getLong(0)!!
    }

    @JvmStatic
    private fun verify_file$lambda$1(mapper: (Long, Long) -> Any, cursor: SqlCursor): Any {
        return mapper(cursor.getLong(0)!!, cursor.getLong(1)!!)
    }

    @JvmStatic
    private fun verify_file$lambda$2(id: Long, __file_id: Long): Verify_file {
        return Verify_file(id, __file_id)
    }

    @JvmStatic
    private fun selectAll$lambda$3(mapper: (Long, String, String, Long, String, Long, Long?, String, String, Long?, Long?) -> Any, cursor: SqlCursor): Any {
        return mapper(
            cursor.getLong(0)!!,
            cursor.getString(1)!!,
            cursor.getString(2)!!,
            cursor.getLong(3)!!,
            cursor.getString(4)!!,
            cursor.getLong(5)!!,
            cursor.getLong(6),
            cursor.getString(7)!!,
            cursor.getString(8)!!,
            cursor.getLong(9),
            cursor.getLong(10)
        )
    }

    @JvmStatic
    private fun selectAll$lambda$4(
        id: Long,
        kind: String,
        display_hint: String,
        __file_id: Long,
        _file_abs_path: String,
        line: Long,
        column: Long?,
        message_en: String,
        message_zh: String,
        __notices_region_id: Long?,
        __func_region_id: Long?
    ): Note {
        return Note(id, kind, display_hint, __file_id, _file_abs_path, line, column, message_en, message_zh, __notices_region_id, __func_region_id)
    }

    @JvmStatic
    private fun insert$lambda$6$lambda$5(
        kind: String,
        display_hint: String,
        __file_id: Long,
        _file_abs_path: String,
        line: Long,
        column: Long?,
        message_en: String,
        message_zh: String,
        __notices_region_id: Long?,
        __func_region_id: Long?,
        statement: SqlPreparedStatement
    ) {
        statement.bindString(0, kind)
        statement.bindString(1, display_hint)
        statement.bindLong(2, __file_id)
        statement.bindString(3, _file_abs_path)
        statement.bindLong(4, line)
        if (column == null) {
            statement.bindNull(5)
        } else {
            statement.bindLong(5, column)
        }
        statement.bindString(6, message_en)
        statement.bindString(7, message_zh)
        if (__notices_region_id == null) {
            statement.bindNull(8)
        } else {
            statement.bindLong(8, __notices_region_id)
        }
        if (__func_region_id == null) {
            statement.bindNull(9)
        } else {
            statement.bindLong(9, __func_region_id)
        }
    }

    @JvmStatic
    private fun insert$lambda$6(
        this$0: NoteQueries,
        kind: String,
        display_hint: String,
        __file_id: Long,
        _file_abs_path: String,
        line: Long,
        column: Long?,
        message_en: String,
        message_zh: String,
        __notices_region_id: Long?,
        __func_region_id: Long?,
        transaction: TransactionWithoutReturn
    ) {
        this$0.getDriver().execute(
            -1993383633,
            "INSERT OR IGNORE INTO Note(kind, display_hint, __file_id, _file_abs_path, line, column, message_en, message_zh, __notices_region_id, __func_region_id)\n    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            10
        ) { statement ->
            insert$lambda$6$lambda$5(
                kind,
                display_hint,
                __file_id,
                _file_abs_path,
                line,
                column,
                message_en,
                message_zh,
                __notices_region_id,
                __func_region_id,
                statement
            )
        }
    }

    @JvmStatic
    private fun insert$lambda$7(emit: (String) -> Unit) {
        emit("Note")
    }

    private inner class IdQuery(
        val kind: String,
        val display_hint: String,
        val __file_id: Long,
        val _file_abs_path: String,
        val line: Long,
        val column: Long?,
        val message_en: String,
        val message_zh: String,
        val __notices_region_id: Long?,
        val __func_region_id: Long?,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<Long>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult$default(
                this@NoteQueries as Transacter,
                false,
                { transaction ->
                    execute$lambda$1(this@NoteQueries, this, mapper, transaction)
                },
                1,
                null
            ) as QueryResult<R>
        }

        override fun toString(): String {
            return "Note.sq:id"
        }

        companion object {
            @JvmStatic
            private fun execute$lambda$1$lambda$0(
                query: IdQuery,
                statement: SqlPreparedStatement
            ) {
                statement.bindString(0, query.kind)
                statement.bindString(1, query.display_hint)
                statement.bindLong(2, query.__file_id)
                statement.bindString(3, query._file_abs_path)
                statement.bindLong(4, query.line)
                if (query.column == null) {
                    statement.bindNull(5)
                } else {
                    statement.bindLong(5, query.column)
                }
                statement.bindString(6, query.message_en)
                statement.bindString(7, query.message_zh)
                if (query.__notices_region_id == null) {
                    statement.bindNull(8)
                } else {
                    statement.bindLong(8, query.__notices_region_id)
                }
                if (query.__func_region_id == null) {
                    statement.bindNull(9)
                } else {
                    statement.bindLong(9, query.__func_region_id)
                }
            }

            @JvmStatic
            private fun execute$lambda$1(
                this$0: NoteQueries,
                query: IdQuery,
                mapper: (SqlCursor) -> QueryResult<*>,
                transaction: TransactionWithReturn
            ): QueryResult<*> {
                return this$0.getDriver().executeQuery(
                    null,
                    """
                    |SELECT id FROM Note WHERE kind = ? AND display_hint = ? AND __file_id = ? AND _file_abs_path = ? AND line = ? AND column ${if (query.column == null) "IS" else "="} ?
                    |    AND message_en = ? AND message_zh = ? AND __notices_region_id ${if (query.__notices_region_id == null) "IS" else "="} ? AND __func_region_id ${if (query.__func_region_id == null) "IS" else "="} ?
                    """.trimMargin(),
                    mapper,
                    10
                ) { statement ->
                    execute$lambda$1$lambda$0(query, statement)
                }
            }
        }
    }

    private inner class Verify_fileQuery<T>(
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult$default(
                this@NoteQueries as Transacter,
                false,
                { transaction ->
                    execute$lambda$0(this@NoteQueries, mapper, transaction)
                },
                1,
                null
            ) as QueryResult<R>
        }

        override fun toString(): String {
            return "Note.sq:verify_file"
        }

        companion object {
            @JvmStatic
            private fun execute$lambda$0(
                this$0: NoteQueries,
                mapper: (SqlCursor) -> QueryResult<*>,
                transaction: TransactionWithReturn
            ): QueryResult<*> {
                return this$0.getDriver().executeQuery(
                    1014184654,
                    "SELECT id, __file_id FROM Note WHERE __file_id NOT IN (SELECT id FROM File)",
                    mapper,
                    0
                )
            }
        }
    }
}