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

public class AbsoluteFilePathQueries(driver: SqlDriver) : TransacterImpl(driver) {
    public fun <T : Any> verify_absolute_file_path(mapper: (String, Long) -> T): ExecutableQuery<T> {
        return Verify_absolute_file_pathQuery(this) { cursor ->
            `verify_absolute_file_path$lambda$0`(Function2 { a, b -> mapper(a, b) }, cursor)
        }
    }

    public fun verify_absolute_file_path(): ExecutableQuery<AbsoluteFilePath> {
        return this.verify_absolute_file_path { file_abs_path, __file_id ->
            `verify_absolute_file_path$lambda$1`(file_abs_path, __file_id)
        }
    }

    public fun <T : Any> selectAll(mapper: (String, Long) -> T): Query<T> {
        return QueryKt.Query(
            77310330,
            arrayOf("AbsoluteFilePath"),
            this.getDriver(),
            "AbsoluteFilePath.sq",
            "selectAll",
            "SELECT AbsoluteFilePath.file_abs_path, AbsoluteFilePath.__file_id\nFROM AbsoluteFilePath"
        ) { cursor ->
            `selectAll$lambda$2`(Function2 { a, b -> mapper(a, b) }, cursor)
        }
    }

    public fun selectAll(): Query<AbsoluteFilePath> {
        return this.selectAll { file_abs_path, __file_id ->
            `selectAll$lambda$3`(file_abs_path, __file_id)
        }
    }

    public fun insert(AbsoluteFilePath: AbsoluteFilePath) {
        this.getDriver()
            .execute(365662308, "INSERT OR IGNORE INTO AbsoluteFilePath (file_abs_path, __file_id) VALUES (?, ?)", 2) { stmt ->
                `insert$lambda$4`(AbsoluteFilePath, stmt)
            }
        this.notifyQueries(365662308) { emitter ->
            `insert$lambda$5`(Function1 { emitter(it) })
        }
    }

    @JvmStatic
    private fun `verify_absolute_file_path$lambda$0`(`$mapper`: Function2<String, Long, Any>, cursor: SqlCursor): Any {
        val var10001: String = cursor.getString(0)
        val var10002: Long = cursor.getLong(1)
        return `$mapper`.invoke(var10001, var10002)
    }

    @JvmStatic
    private fun `verify_absolute_file_path$lambda$1`(file_abs_path: String, __file_id: Long): AbsoluteFilePath {
        return AbsoluteFilePath(file_abs_path, __file_id)
    }

    @JvmStatic
    private fun `selectAll$lambda$2`(`$mapper`: Function2<String, Long, Any>, cursor: SqlCursor): Any {
        val var10001: String = cursor.getString(0)
        val var10002: Long = cursor.getLong(1)
        return `$mapper`.invoke(var10001, var10002)
    }

    @JvmStatic
    private fun `selectAll$lambda$3`(file_abs_path: String, __file_id: Long): AbsoluteFilePath {
        return AbsoluteFilePath(file_abs_path, __file_id)
    }

    @JvmStatic
    private fun `insert$lambda$4`(`$AbsoluteFilePath`: AbsoluteFilePath, `$this$execute`: SqlPreparedStatement) {
        `$this$execute`.bindString(0, `$AbsoluteFilePath`.file_abs_path)
        `$this$execute`.bindLong(1, `$AbsoluteFilePath`.__file_id)
    }

    @JvmStatic
    private fun `insert$lambda$5`(emit: Function1<String, Unit>) {
        emit.invoke("AbsoluteFilePath")
    }

    private inner class Verify_absolute_file_pathQuery<T>(
        private val this$0: AbsoluteFilePathQueries,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult$default(
                this$0 as Transacter, false, { transaction ->
                    `execute$lambda$0`(this$0, Function1 { cursor -> mapper(cursor) }, transaction)
                }, 1, null
            ) as QueryResult<R>
        }

        override fun toString(): String {
            return "AbsoluteFilePath.sq:verify_absolute_file_path"
        }

        companion object {
            @JvmStatic
            private fun `execute$lambda$0`(
                `this$0`: AbsoluteFilePathQueries,
                `$mapper`: Function1<SqlCursor, QueryResult<*>>,
                `$this$transactionWithResult`: TransactionWithReturn
            ): QueryResult<*> {
                return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
                    `this$0`.getDriver(),
                    -1503538388,
                    "SELECT AbsoluteFilePath.file_abs_path, AbsoluteFilePath.__file_id FROM AbsoluteFilePath WHERE __file_id NOT IN (SELECT id FROM File)",
                    `$mapper`,
                    0,
                    null,
                    16,
                    null
                )
            }
        }
    }
}