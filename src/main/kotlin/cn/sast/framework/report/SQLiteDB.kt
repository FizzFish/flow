package cn.sast.framework.report

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.ConnectionManager
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import cn.sast.framework.report.sqldelight.Database
import cn.sast.framework.report.sqlite.JdbcSqliteDriverDelegate
import com.feysh.corax.commons.Kotlin_extKt
import java.io.Closeable
import java.util.Properties
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.properties.ReadWriteProperty

@SourceDebugExtension(["SMAP\nSqliteDiagnostics.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SqliteDiagnostics.kt\ncn/sast/framework/report/SQLiteDB\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,813:1\n1#2:814\n*E\n"])
public class SQLiteDB private constructor(
    private val dbPath: String,
    private val driver: JdbcDriver,
    public val database: Database,
    private val journalMode: String
) : Closeable {

    public override fun close() {
        label21@ {
            this.driver.close()
            if (this.journalMode != "OFF") {
                val var1: Closeable = SqliteDiagnostics.Companion.openDataBase(this.dbPath, "OFF")
                var var2: Throwable? = null

                try {
                    try {
                        val it: SQLiteDB = var1 as SQLiteDB
                    } catch (var5: Throwable) {
                        var2 = var5
                        throw var5
                    }
                } catch (var6: Throwable) {
                    CloseableKt.closeFinally(var1, var2)
                }

                CloseableKt.closeFinally(var1, null)
            }
        }
    }

    public fun createSchema() {
        Database.Companion.getSchema().create(this.driver as SqlDriver)
    }

    public companion object {
        public fun openDataBase(dbPath: String, journalMode: String): SQLiteDB {
            val url: String = "jdbc:sqlite:$dbPath"
            val delegateProperties = Properties().apply {
                put("SQLITE_THREADSAFE", "2")
                put("SQLITE_OPEN_FULLMUTEX", "true")
                put("journal_mode", journalMode)
            }
            val driver = JdbcSqliteDriverDelegate(
                openDataBase$lambda$1(
                    Kotlin_extKt.delegateField$default(JdbcSqliteDriver(url, delegateProperties), null, 1, null)
                        .provideDelegate(null, $$delegatedProperties[0]) as ReadWriteProperty<Any?, ConnectionManager>
                )
            )
            return SQLiteDB(dbPath, driver, Database.Companion.invoke(driver as SqlDriver), journalMode)
        }

        @JvmStatic
        fun `openDataBase$lambda$1`(delegate: ReadWriteProperty<Any?, ConnectionManager>): ConnectionManager {
            return delegate.getValue(null, $$delegatedProperties[0]) as ConnectionManager
        }
    }
}