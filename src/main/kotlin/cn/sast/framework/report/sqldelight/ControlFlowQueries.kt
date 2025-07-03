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
import cn.sast.framework.report.sqldelight.controlFlow.Verify_file
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function7

public class ControlFlowQueries(driver: SqlDriver) : TransacterImpl(driver) {
    public fun id(
        __file_id: Long,
        _file_abs_path: String,
        message_en: String?,
        message_zh: String?,
        __edge_from_region_id: Long,
        __edge_to_region_id: Long
    ): ExecutableQuery<Long> {
        return IdQuery(
            this,
            __file_id,
            _file_abs_path,
            message_en,
            message_zh,
            __edge_from_region_id,
            __edge_to_region_id,
            ::id$lambda$0
        )
    }

    public fun <T : Any> verify_file(mapper: (Long, Long) -> T): ExecutableQuery<T> {
        return Verify_fileQuery(this, ::verify_file$lambda$1)
    }

    public fun verify_file(): ExecutableQuery<Verify_file> {
        return this.verify_file(::verify_file$lambda$2)
    }

    public fun <T : Any> selectAll(
        mapper: (Long, Long, String, String?, String?, Long, Long) -> T
    ): Query<T> {
        return QueryKt.Query(
            590154801,
            arrayOf("ControlFlow"),
            this.getDriver(),
            "ControlFlow.sq",
            "selectAll",
            "SELECT ControlFlow.id, ControlFlow.__file_id, ControlFlow._file_abs_path, ControlFlow.message_en, ControlFlow.message_zh, ControlFlow.__edge_from_region_id, ControlFlow.__edge_to_region_id\nFROM ControlFlow",
            ::selectAll$lambda$3
        )
    }

    public fun selectAll(): Query<ControlFlow> {
        return this.selectAll(::selectAll$lambda$4)
    }

    public fun insert(
        __file_id: Long,
        _file_abs_path: String,
        message_en: String?,
        message_zh: String?,
        __edge_from_region_id: Long,
        __edge_to_region_id: Long
    ) {
        DefaultImpls.transaction$default(this as Transacter, false, ::insert$lambda$6, 1, null)
        this.notifyQueries(1615921421, ::insert$lambda$7)
    }

    @JvmStatic
    private fun id$lambda$0(cursor: SqlCursor): Long {
        return cursor.getLong(0)
    }

    @JvmStatic
    private fun verify_file$lambda$1(`$mapper`: Function2<Long, Long, Any>, cursor: SqlCursor): Any {
        val var10001 = cursor.getLong(0)
        val var10002 = cursor.getLong(1)
        return `$mapper`.invoke(var10001, var10002)
    }

    @JvmStatic
    private fun verify_file$lambda$2(id: Long, __file_id: Long): Verify_file {
        return Verify_file(id, __file_id)
    }

    @JvmStatic
    private fun selectAll$lambda$3(
        `$mapper`: Function7<Long, Long, String, String?, String?, Long, Long, Any>,
        cursor: SqlCursor
    ): Any {
        val var10001 = cursor.getLong(0)
        val var10002 = cursor.getLong(1)
        val var10003 = cursor.getString(2)
        val var10004 = cursor.getString(3)
        val var10005 = cursor.getString(4)
        val var10006 = cursor.getLong(5)
        val var10007 = cursor.getLong(6)
        return `$mapper`.invoke(var10001, var10002, var10003, var10004, var10005, var10006, var10007)
    }

    @JvmStatic
    private fun selectAll$lambda$4(
        id: Long,
        __file_id: Long,
        _file_abs_path: String,
        message_en: String?,
        message_zh: String?,
        __edge_from_region_id: Long,
        __edge_to_region_id: Long
    ): ControlFlow {
        return ControlFlow(id, __file_id, _file_abs_path, message_en, message_zh, __edge_from_region_id, __edge_to_region_id)
    }

    @JvmStatic
    private fun insert$lambda$6$lambda$5(
        `$__file_id`: Long,
        `$_file_abs_path`: String,
        `$message_en`: String?,
        `$message_zh`: String?,
        `$__edge_from_region_id`: Long,
        `$__edge_to_region_id`: Long,
        `$this$execute`: SqlPreparedStatement
    ) {
        `$this$execute`.bindLong(0, `$__file_id`)
        `$this$execute`.bindString(1, `$_file_abs_path`)
        `$this$execute`.bindString(2, `$message_en`)
        `$this$execute`.bindString(3, `$message_zh`)
        `$this$execute`.bindLong(4, `$__edge_from_region_id`)
        `$this$execute`.bindLong(5, `$__edge_to_region_id`)
    }

    @JvmStatic
    private fun insert$lambda$6(
        `this$0`: ControlFlowQueries,
        `$__file_id`: Long,
        `$_file_abs_path`: String,
        `$message_en`: String?,
        `$message_zh`: String?,
        `$__edge_from_region_id`: Long,
        `$__edge_to_region_id`: Long,
        `$this$transaction`: TransactionWithoutReturn
    ) {
        `this$0`.getDriver()
            .execute(
                -1877672578,
                "INSERT OR IGNORE INTO ControlFlow(__file_id, _file_abs_path, message_en, message_zh, __edge_from_region_id, __edge_to_region_id)\n    VALUES (?, ?, ?, ?, ?, ?)",
                6,
                ::insert$lambda$6$lambda$5
            )
    }

    @JvmStatic
    private fun insert$lambda$7(emit: Function1<String, Unit>) {
        emit.invoke("ControlFlow")
    }

    private inner class IdQuery<T>(
        private val this$0: ControlFlowQueries,
        val __file_id: Long,
        val _file_abs_path: String,
        val message_en: String?,
        val message_zh: String?,
        val __edge_from_region_id: Long,
        val __edge_to_region_id: Long,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult$default(
                this$0 as Transacter,
                false,
                { transaction -> execute$lambda$1(this$0, this, mapper, transaction) },
                1,
                null
            ) as QueryResult<R>
        }

        override fun toString(): String {
            return "ControlFlow.sq:id"
        }

        companion object {
            @JvmStatic
            private fun execute$lambda$1$lambda$0(
                `this$0`: IdQuery<*>,
                `$this$executeQuery`: SqlPreparedStatement
            ) {
                `$this$executeQuery`.bindLong(0, `this$0`.__file_id)
                `$this$executeQuery`.bindString(1, `this$0`._file_abs_path)
                `$this$executeQuery`.bindString(2, `this$0`.message_en)
                `$this$executeQuery`.bindString(3, `this$0`.message_zh)
                `$this$executeQuery`.bindLong(4, `this$0`.__edge_from_region_id)
                `$this$executeQuery`.bindLong(5, `this$0`.__edge_to_region_id)
            }

            @JvmStatic
            private fun execute$lambda$1(
                `this$0`: ControlFlowQueries,
                `this$1`: IdQuery<*>,
                `$mapper`: Function1<SqlCursor, QueryResult<*>>,
                `$this$transactionWithResult`: TransactionWithReturn
            ): QueryResult<*> {
                return `this$0`.getDriver()
                    .executeQuery(
                        null,
                        "SELECT id FROM ControlFlow WHERE __file_id = ? AND _file_abs_path = ? AND message_en ${if (`this$1`.message_en == null) "IS" else "="} ? AND message_zh ${if (`this$1`.message_zh == null) "IS" else "="} ? AND __edge_from_region_id = ? AND __edge_to_region_id = ?",
                        `$mapper`,
                        6,
                        { stmt -> execute$lambda$1$lambda$0(`this$1`, stmt) }
                    )
            }
        }
    }

    private inner class Verify_fileQuery<T>(
        private val this$0: ControlFlowQueries,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult$default(
                this$0 as Transacter,
                false,
                { transaction -> execute$lambda$0(this$0, mapper, transaction) },
                1,
                null
            ) as QueryResult<R>
        }

        override fun toString(): String {
            return "ControlFlow.sq:verify_file"
        }

        companion object {
            @JvmStatic
            private fun execute$lambda$0(
                `this$0`: ControlFlowQueries,
                `$mapper`: Function1<SqlCursor, QueryResult<*>>,
                `$this$transactionWithResult`: TransactionWithReturn
            ): QueryResult<*> {
                return `this$0`.getDriver().executeQuery(
                    2004744159,
                    "SELECT id, __file_id FROM ControlFlow WHERE __file_id NOT IN (SELECT id FROM File)",
                    `$mapper`,
                    0,
                    null
                )
            }
        }
    }
}