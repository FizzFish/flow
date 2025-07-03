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

public class ControlFlowPathQueries(driver: SqlDriver) : TransacterImpl(driver) {
    public fun <T : Any> verify_control_flow(mapper: (Long, Long, Long) -> T): ExecutableQuery<T> {
        return Verify_control_flowQuery(this, mapper)
    }

    public fun verify_control_flow(): ExecutableQuery<ControlFlowPath> {
        return this.verify_control_flow(::verify_control_flow$lambda$1)
    }

    public fun <T : Any> selectAll(mapper: (Long, Long, Long) -> T): Query<T> {
        return QueryKt.Query(
            -180228340,
            arrayOf("ControlFlowPath"),
            this.getDriver(),
            "ControlFlowPath.sq",
            "selectAll",
            "SELECT ControlFlowPath.__control_flow_array_hash_id, ControlFlowPath.control_flow_sequence, ControlFlowPath.__control_flow_id\nFROM ControlFlowPath",
            mapper
        )
    }

    public fun selectAll(): Query<ControlFlowPath> {
        return this.selectAll(::selectAll$lambda$3)
    }

    public fun insert(ControlFlowPath: ControlFlowPath) {
        this.getDriver()
            .execute(
                -947157998,
                "INSERT OR IGNORE INTO ControlFlowPath (__control_flow_array_hash_id, control_flow_sequence, __control_flow_id)\nVALUES (?, ?, ?)",
                3
            ) { stmt ->
                stmt.bindLong(0, ControlFlowPath.get__control_flow_array_hash_id())
                stmt.bindLong(1, ControlFlowPath.getControl_flow_sequence())
                stmt.bindLong(2, ControlFlowPath.get__control_flow_id())
            }
        this.notifyQueries(-947157998) { emit -> emit("ControlFlowPath") }
    }

    @JvmStatic
    private fun verify_control_flow$lambda$0(mapper: Function3<Long, Long, Long, Any>, cursor: SqlCursor): Any {
        val var10001 = cursor.getLong(0)
        val var10002 = cursor.getLong(1)
        val var10003 = cursor.getLong(2)
        return mapper.invoke(var10001, var10002, var10003)
    }

    @JvmStatic
    private fun verify_control_flow$lambda$1(__control_flow_array_hash_id: Long, control_flow_sequence: Long, __control_flow_id: Long): ControlFlowPath {
        return ControlFlowPath(__control_flow_array_hash_id, control_flow_sequence, __control_flow_id)
    }

    @JvmStatic
    private fun selectAll$lambda$2(mapper: Function3<Long, Long, Long, Any>, cursor: SqlCursor): Any {
        val var10001 = cursor.getLong(0)
        val var10002 = cursor.getLong(1)
        val var10003 = cursor.getLong(2)
        return mapper.invoke(var10001, var10002, var10003)
    }

    @JvmStatic
    private fun selectAll$lambda$3(__control_flow_array_hash_id: Long, control_flow_sequence: Long, __control_flow_id: Long): ControlFlowPath {
        return ControlFlowPath(__control_flow_array_hash_id, control_flow_sequence, __control_flow_id)
    }

    private inner class Verify_control_flowQuery<T>(
        private val this$0: ControlFlowPathQueries,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        public override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult$default(
                this$0 as Transacter, false, { transaction ->
                    execute$lambda$0(this$0, mapper, transaction)
                }, 1, null
            ) as QueryResult<R>
        }

        public override fun toString(): String {
            return "ControlFlowPath.sq:verify_control_flow"
        }

        @JvmStatic
        private fun execute$lambda$0(
            this$0: ControlFlowPathQueries,
            mapper: Function1<SqlCursor, QueryResult<*>>,
            transaction: TransactionWithReturn
        ): QueryResult<*> {
            return SqlDriver.DefaultImpls.executeQuery$default(
                this$0.getDriver(),
                1682752654,
                "SELECT ControlFlowPath.__control_flow_array_hash_id, ControlFlowPath.control_flow_sequence, ControlFlowPath.__control_flow_id FROM ControlFlowPath WHERE __control_flow_id NOT IN (SELECT id FROM ControlFlow)",
                mapper,
                0,
                null,
                16,
                null
            )
        }
    }
}