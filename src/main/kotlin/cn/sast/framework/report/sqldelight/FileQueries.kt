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
import kotlin.jvm.functions.Function7

public class FileQueries(driver: SqlDriver) : TransacterImpl(driver) {
    public fun id(file_raw_content_hash: String, relative_path: String): ExecutableQuery<Long> {
        return IdQuery(this, file_raw_content_hash, relative_path, FileQueries::id$lambda$0)
    }

    public fun <T : Any> selectAll(mapper: (Long, String, String, Long, String?, Long, ByteArray) -> T): Query<T> {
        return QueryKt.Query(
            -1345738410,
            arrayOf("File"),
            this.driver,
            "File.sq",
            "selectAll",
            "SELECT File.id, File.file_raw_content_hash, File.relative_path, File.lines, File.encoding, File.file_raw_content_size, File.file_raw_content\nFROM File",
            FileQueries::selectAll$lambda$1
        )
    }

    public fun selectAll(): Query<File> {
        return this.selectAll(FileQueries::selectAll$lambda$2)
    }

    public fun insert(file_raw_content_hash: String, relative_path: String, lines: Long, file_raw_content_size: Long, file_raw_content: ByteArray) {
        DefaultImpls.transaction$default(this as Transacter, false, { transaction ->
            insert$lambda$4(this, file_raw_content_hash, relative_path, lines, file_raw_content_size, file_raw_content, transaction)
        }, 1, null)
        this.notifyQueries(-204145144) { emit ->
            insert$lambda$5(emit)
        }
    }

    @JvmStatic
    private fun id$lambda$0(cursor: SqlCursor): Long {
        return cursor.getLong(0)
    }

    @JvmStatic
    private fun selectAll$lambda$1(mapper: Function7<*, *, *, *, *, *, *, *>, cursor: SqlCursor): Any {
        return mapper.invoke(
            cursor.getLong(0),
            cursor.getString(1),
            cursor.getString(2),
            cursor.getLong(3),
            cursor.getString(4),
            cursor.getLong(5),
            cursor.getBytes(6)
        )
    }

    @JvmStatic
    private fun selectAll$lambda$2(
        id: Long,
        file_raw_content_hash: String,
        relative_path: String,
        lines: Long,
        encoding: String?,
        file_raw_content_size: Long,
        file_raw_content: ByteArray
    ): File {
        return File(id, file_raw_content_hash, relative_path, lines, encoding, file_raw_content_size, file_raw_content)
    }

    @JvmStatic
    private fun insert$lambda$4$lambda$3(
        file_raw_content_hash: String,
        relative_path: String,
        lines: Long,
        file_raw_content_size: Long,
        file_raw_content: ByteArray,
        this$execute: SqlPreparedStatement
    ) {
        this$execute.bindString(0, file_raw_content_hash)
        this$execute.bindString(1, relative_path)
        this$execute.bindLong(2, lines)
        this$execute.bindLong(3, file_raw_content_size)
        this$execute.bindBytes(4, file_raw_content)
    }

    @JvmStatic
    private fun insert$lambda$4(
        this$0: FileQueries,
        file_raw_content_hash: String,
        relative_path: String,
        lines: Long,
        file_raw_content_size: Long,
        file_raw_content: ByteArray,
        this$transaction: TransactionWithoutReturn
    ) {
        this$0.driver.execute(
            1385015225,
            "INSERT OR IGNORE INTO File(file_raw_content_hash, relative_path, lines, file_raw_content_size, file_raw_content)\n    VALUES (?, ?, ?, ?, ?)",
            5
        ) { stmt ->
            insert$lambda$4$lambda$3(file_raw_content_hash, relative_path, lines, file_raw_content_size, file_raw_content, stmt)
        }
    }

    @JvmStatic
    private fun insert$lambda$5(emit: Function1<String, Unit>) {
        emit.invoke("File")
    }

    private inner class IdQuery<T>(
        private val this$0: FileQueries,
        val file_raw_content_hash: String,
        val relative_path: String,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult$default(
                this$0 as Transacter,
                false,
                { transaction ->
                    execute$lambda$1(this$0, mapper, this, transaction)
                },
                1,
                null
            ) as QueryResult<R>
        }

        override fun toString(): String {
            return "File.sq:id"
        }

        @JvmStatic
        private fun execute$lambda$1$lambda$0(
            this$0: IdQuery<*>,
            this$executeQuery: SqlPreparedStatement
        ) {
            this$executeQuery.bindString(0, this$0.file_raw_content_hash)
            this$executeQuery.bindString(1, this$0.relative_path)
        }

        @JvmStatic
        private fun execute$lambda$1(
            this$0: FileQueries,
            mapper: Function1<SqlCursor, QueryResult<*>>,
            this$1: IdQuery<*>,
            this$transactionWithResult: TransactionWithReturn
        ): QueryResult<*> {
            return this$0.driver.executeQuery(
                2042233115,
                "SELECT id FROM File WHERE file_raw_content_hash = ? AND relative_path = ?",
                mapper,
                2
            ) { stmt ->
                execute$lambda$1$lambda$0(this$1, stmt)
            }
        }
    }
}