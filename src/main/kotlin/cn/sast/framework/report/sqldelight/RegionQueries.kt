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
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function6

public class RegionQueries(driver: SqlDriver) : TransacterImpl(driver) {
    public fun id(__file_id: Long, start_line: Long, start_column: Long?, end_line: Long?, end_column: Long?): ExecutableQuery<Long> {
        return IdQuery(__file_id, start_line, start_column, end_line, end_column, ::id$lambda$0)
    }

    public fun <T : Any> selectAll(mapper: (Long, Long, Long, Long?, Long?, Long?) -> T): Query<T> {
        return QueryKt.Query(
            -1166723042,
            arrayOf("Region"),
            this.driver,
            "Region.sq",
            "selectAll",
            "SELECT Region.id, Region.__file_id, Region.start_line, Region.start_column, Region.end_line, Region.end_column\nFROM Region",
            { cursor -> selectAll$lambda$1(mapper, cursor) }
        )
    }

    public fun selectAll(): Query<Region> {
        return this.selectAll(::selectAll$lambda$2)
    }

    public fun insert(__file_id: Long, start_line: Long, start_column: Long?, end_line: Long?, end_column: Long?) {
        DefaultImpls.transaction$default(this as Transacter, false, { transaction ->
            insert$lambda$4(this, __file_id, start_line, start_column, end_line, end_column, transaction)
        }, 1, null)
        this.notifyQueries(1577657408) { emit -> insert$lambda$5(emit) }
    }

    @JvmStatic
    private fun id$lambda$0(cursor: SqlCursor): Long {
        return cursor.getLong(0)!!
    }

    @JvmStatic
    private fun <T> selectAll$lambda$1(mapper: (Long, Long, Long, Long?, Long?, Long?) -> T, cursor: SqlCursor): T {
        return mapper(
            cursor.getLong(0)!!,
            cursor.getLong(1)!!,
            cursor.getLong(2)!!,
            cursor.getLong(3),
            cursor.getLong(4),
            cursor.getLong(5)
        )
    }

    @JvmStatic
    private fun selectAll$lambda$2(
        id: Long,
        __file_id: Long,
        start_line: Long,
        start_column: Long?,
        end_line: Long?,
        end_column: Long?
    ): Region {
        return Region(id, __file_id, start_line, start_column, end_line, end_column)
    }

    @JvmStatic
    private fun insert$lambda$4$lambda$3(
        __file_id: Long,
        start_line: Long,
        start_column: Long?,
        end_line: Long?,
        end_column: Long?,
        this$execute: SqlPreparedStatement
    ) {
        this$execute.bindLong(0, __file_id)
        this$execute.bindLong(1, start_line)
        this$execute.bindLong(2, start_column)
        this$execute.bindLong(3, end_line)
        this$execute.bindLong(4, end_column)
    }

    @JvmStatic
    private fun insert$lambda$4(
        this$0: RegionQueries,
        __file_id: Long,
        start_line: Long,
        start_column: Long?,
        end_line: Long?,
        end_column: Long?,
        this$transaction: TransactionWithoutReturn
    ) {
        this$0.driver.execute(
            5316593,
            "INSERT OR IGNORE INTO Region(__file_id, start_line, start_column, end_line, end_column)\n    VALUES (?, ?, ?, ?, ?)",
            5
        ) { stmt ->
            insert$lambda$4$lambda$3(__file_id, start_line, start_column, end_line, end_column, stmt)
        }
    }

    @JvmStatic
    private fun insert$lambda$5(emit: Function1<String, Unit>) {
        emit.invoke("Region")
    }

    private inner class IdQuery(
        val __file_id: Long,
        val start_line: Long,
        val start_column: Long?,
        val end_line: Long?,
        val end_column: Long?,
        mapper: (SqlCursor) -> Long
    ) : ExecutableQuery<Long>(mapper) {

        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult$default(
                this@RegionQueries as Transacter,
                false,
                { transaction ->
                    execute$lambda$1(this@RegionQueries, this, mapper, transaction)
                },
                1,
                null
            ) as QueryResult<R>
        }

        override fun toString(): String {
            return "Region.sq:id"
        }
    }

    @JvmStatic
    private fun execute$lambda$1$lambda$0(
        this$0: IdQuery,
        this$executeQuery: SqlPreparedStatement
    ) {
        this$executeQuery.bindLong(0, this$0.__file_id)
        this$executeQuery.bindLong(1, this$0.start_line)
        this$executeQuery.bindLong(2, this$0.start_column)
        this$executeQuery.bindLong(3, this$0.end_line)
        this$executeQuery.bindLong(4, this$0.end_column)
    }

    @JvmStatic
    private fun <R> execute$lambda$1(
        this$0: RegionQueries,
        this$1: IdQuery,
        mapper: (SqlCursor) -> QueryResult<R>,
        this$transactionWithResult: TransactionWithReturn
    ): QueryResult<R> {
        return this$0.driver.executeQuery(
            null,
            "SELECT id FROM Region WHERE __file_id = ? AND start_line = ? AND start_column ${if (this$1.start_column == null) "IS" else "="} ? AND end_line ${if (this$1.end_line == null) "IS" else "="} ? AND end_column ${if (this$1.end_column == null) "IS" else "="} ?",
            mapper,
            5
        ) { stmt ->
            execute$lambda$1$lambda$0(this$1, stmt)
        }
    }
}