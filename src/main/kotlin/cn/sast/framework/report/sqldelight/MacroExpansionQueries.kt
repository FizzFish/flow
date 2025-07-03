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
import kotlin.jvm.functions.Function2

class MacroExpansionQueries(driver: SqlDriver) : TransacterImpl(driver) {
    fun <T : Any> verify_note(mapper: (Long, Long) -> T): ExecutableQuery<T> {
        return Verify_noteQuery(this, ::verify_note$lambda$0)
    }

    fun verify_note(): ExecutableQuery<MacroExpansion> {
        return this.verify_note(::verify_note$lambda$1)
    }

    fun <T : Any> selectAll(mapper: (Long, Long) -> T): Query<T> {
        return QueryKt.Query(
            1160581151,
            arrayOf("MacroExpansion"),
            this.driver,
            "MacroExpansion.sq",
            "selectAll",
            "SELECT MacroExpansion.__macro_note_set_hash_id, MacroExpansion.__macro_note_id\nFROM MacroExpansion",
            ::selectAll$lambda$2
        )
    }

    fun selectAll(): Query<MacroExpansion> {
        return this.selectAll(::selectAll$lambda$3)
    }

    fun insert(MacroExpansion: MacroExpansion) {
        this.driver.execute(
            -1356123169,
            "INSERT OR IGNORE INTO MacroExpansion (__macro_note_set_hash_id, __macro_note_id)\nVALUES (?, ?)",
            2,
            { stmt -> insert$lambda$4(MacroExpansion, stmt) }
        )
        this.notifyQueries(-1356123169) { emit -> insert$lambda$5(emit) }
    }

    companion object {
        @JvmStatic
        fun verify_note$lambda$0(mapper: Function2<*, *, *>, cursor: SqlCursor): Any {
            val var10001 = cursor.getLong(0)
            val var10002 = cursor.getLong(1)
            return mapper.invoke(var10001, var10002)
        }

        @JvmStatic
        fun verify_note$lambda$1(__macro_note_set_hash_id: Long, __macro_note_id: Long): MacroExpansion {
            return MacroExpansion(__macro_note_set_hash_id, __macro_note_id)
        }

        @JvmStatic
        fun selectAll$lambda$2(mapper: Function2<*, *, *>, cursor: SqlCursor): Any {
            val var10001 = cursor.getLong(0)
            val var10002 = cursor.getLong(1)
            return mapper.invoke(var10001, var10002)
        }

        @JvmStatic
        fun selectAll$lambda$3(__macro_note_set_hash_id: Long, __macro_note_id: Long): MacroExpansion {
            return MacroExpansion(__macro_note_set_hash_id, __macro_note_id)
        }

        @JvmStatic
        fun insert$lambda$4(MacroExpansion: MacroExpansion, this$execute: SqlPreparedStatement) {
            this$execute.bindLong(0, MacroExpansion.__macro_note_set_hash_id)
            this$execute.bindLong(1, MacroExpansion.__macro_note_id)
        }

        @JvmStatic
        fun insert$lambda$5(emit: Function1<*, *>) {
            emit.invoke("MacroExpansion")
        }
    }

    private inner class Verify_noteQuery<T>(
        private val this$0: MacroExpansionQueries,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult$default(
                this$0 as Transacter,
                false,
                { execute$lambda$0(this$0, mapper, it) },
                1,
                null
            ) as QueryResult<R>
        }

        override fun toString(): String {
            return "MacroExpansion.sq:verify_note"
        }

        companion object {
            @JvmStatic
            fun execute$lambda$0(
                this$0: MacroExpansionQueries,
                mapper: Function1<*, *>,
                this$transactionWithResult: TransactionWithReturn
            ): QueryResult<*> {
                return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
                    this$0.driver,
                    -555923005,
                    "SELECT MacroExpansion.__macro_note_set_hash_id, MacroExpansion.__macro_note_id FROM MacroExpansion WHERE __macro_note_id NOT IN (SELECT id FROM Note)",
                    mapper,
                    0,
                    null,
                    16,
                    null
                )
            }
        }
    }
}