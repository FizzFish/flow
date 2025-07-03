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

class AnalyzeResultFileQueries(driver: SqlDriver) : TransacterImpl(driver) {
    fun <T : Any> verify_file(mapper: (String, String?, Long) -> T): ExecutableQuery<T> {
        return Verify_fileQuery(this) { cursor ->
            mapper(
                cursor.getString(0)!!,
                cursor.getString(1),
                cursor.getLong(2)!!
            )
        }
    }

    fun verify_file(): ExecutableQuery<AnalyzerResultFile> {
        return verify_file { file_name, file_path, __file_id ->
            AnalyzerResultFile(file_name, file_path, __file_id)
        }
    }

    fun <T : Any> selectAll(mapper: (String, String?, Long) -> T): Query<T> {
        return QueryKt.Query(
            1331798327,
            arrayOf("AnalyzerResultFile"),
            driver,
            "AnalyzeResultFile.sq",
            "selectAll",
            "SELECT AnalyzerResultFile.file_name, AnalyzerResultFile.file_path, AnalyzerResultFile.__file_id\nFROM AnalyzerResultFile",
            { cursor ->
                mapper(
                    cursor.getString(0)!!,
                    cursor.getString(1),
                    cursor.getLong(2)!!
                )
            }
        )
    }

    fun selectAll(): Query<AnalyzerResultFile> {
        return selectAll { file_name, file_path, __file_id ->
            AnalyzerResultFile(file_name, file_path, __file_id)
        }
    }

    fun insert(AnalyzerResultFile: AnalyzerResultFile) {
        driver.execute(
            397277639,
            "INSERT OR IGNORE INTO AnalyzerResultFile (file_name, file_path, __file_id)\nVALUES (?, ?, ?)",
            3
        ) { stmt ->
            stmt.bindString(0, AnalyzerResultFile.file_name)
            stmt.bindString(1, AnalyzerResultFile.file_path)
            stmt.bindLong(2, AnalyzerResultFile.__file_id)
        }
        notifyQueries(397277639) { emit ->
            emit("AnalyzerResultFile")
        }
    }

    private inner class Verify_fileQuery<T>(
        private val this$0: AnalyzeResultFileQueries,
        mapper: (SqlCursor) -> Any
    ) : ExecutableQuery<T>(mapper) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
            return DefaultImpls.transactionWithResult$default(
                this$0,
                false,
                { transaction ->
                    driver.executeQuery(
                        -1649108507,
                        "SELECT AnalyzerResultFile.file_name, AnalyzerResultFile.file_path, AnalyzerResultFile.__file_id FROM AnalyzerResultFile WHERE __file_id NOT IN (SELECT id FROM File)",
                        mapper
                    )
                },
                1,
                null
            ) as QueryResult<R>
        }

        override fun toString(): String {
            return "AnalyzeResultFile.sq:verify_file"
        }
    }
}