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
import kotlin.jvm.functions.Function5

public class NotePathQueries(driver: SqlDriver) : TransacterImpl(driver) {
    public fun <T : Any> verify_note(mapper: (Long, Long, Long?, Long?, Long) -> T): ExecutableQuery<T> {
        return Verify_noteQuery(this, mapper)
    }

    public fun verify_note(): ExecutableQuery<NotePath> {
        return this.verify_note { __note_array_hash_id, note_sequence, note_stack_depth, note_is_key_event, __note_id ->
            NotePath(__note_array_hash_id, note_sequence, note_stack_depth, note_is_key_event, __note_id)
        }
    }

    public fun <T : Any> selectAll(mapper: (Long, Long, Long?, Long?, Long) -> T): Query<T> {
        return QueryKt.Query(
            -1170787845,
            arrayOf("NotePath"),
            this.driver,
            "NotePath.sq",
            "selectAll",
            "SELECT NotePath.__note_array_hash_id, NotePath.note_sequence, NotePath.note_stack_depth, NotePath.note_is_key_event, NotePath.__note_id\nFROM NotePath",
            { cursor -> 
                mapper(
                    cursor.getLong(0)!!,
                    cursor.getLong(1)!!,
                    cursor.getLong(2),
                    cursor.getLong(3),
                    cursor.getLong(4)!!
                )
            }
        )
    }

    public fun selectAll(): Query<NotePath> {
        return this.selectAll { __note_array_hash_id, note_sequence, note_stack_depth, note_is_key_event, __note_id ->
            NotePath(__note_array_hash_id, note_sequence, note_stack_depth, note_is_key_event, __note_id)
        }
    }

    public fun insert(NotePath: NotePath) {
        this.driver.execute(
            -476332157,
            "INSERT OR IGNORE INTO NotePath (__note_array_hash_id, note_sequence, note_stack_depth, note_is_key_event, __note_id)\nVALUES (?, ?, ?, ?, ?)",
            5
        ) { stmt ->
            stmt.bindLong(0, NotePath.get__note_array_hash_id())
            stmt.bindLong(1, NotePath.getNote_sequence())
            stmt.bindLong(2, NotePath.getNote_stack_depth())
            stmt.bindLong(3, NotePath.getNote_is_key_event())
            stmt.bindLong(4, NotePath.get__note_id())
        }
        this.notifyQueries(-476332157) { emit -> emit("NotePath") }
    }

    private inner class Verify_noteQuery<T>(
        private val this$0: NotePathQueries,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult$default(
                this$0 as Transacter,
                false,
                { transaction ->
                    this$0.driver.executeQuery(
                        -1676993121,
                        "SELECT NotePath.__note_array_hash_id, NotePath.note_sequence, NotePath.note_stack_depth, NotePath.note_is_key_event, NotePath.__note_id FROM NotePath WHERE __note_id NOT IN (SELECT id FROM Note)",
                        mapper
                    )
                },
                1,
                null
            ) as QueryResult<R>
        }

        override fun toString(): String {
            return "NotePath.sq:verify_note"
        }
    }
}