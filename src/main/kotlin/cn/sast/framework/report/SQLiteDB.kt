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
public class SQLiteDB private constructor(dbPath: String, driver: JdbcDriver, database: Database, journalMode: String) : Closeable {
   private final val dbPath: String
   private final val driver: JdbcDriver
   public final val database: Database
   private final val journalMode: String

   init {
      this.dbPath = dbPath;
      this.driver = driver;
      this.database = database;
      this.journalMode = journalMode;
   }

   public override fun close() {
      label21: {
         this.driver.close();
         if (!(this.journalMode == "OFF")) {
            val var1: Closeable = SqliteDiagnostics.Companion.openDataBase(this.dbPath, "OFF");
            var var2: java.lang.Throwable = null;

            try {
               try {
                  val it: SQLiteDB = var1 as SQLiteDB;
               } catch (var5: java.lang.Throwable) {
                  var2 = var5;
                  throw var5;
               }
            } catch (var6: java.lang.Throwable) {
               CloseableKt.closeFinally(var1, var2);
            }

            CloseableKt.closeFinally(var1, null);
         }
      }
   }

   public fun createSchema() {
      Database.Companion.getSchema().create(this.driver as SqlDriver);
   }

   public companion object {
      public fun openDataBase(dbPath: String, journalMode: String): SQLiteDB {
         val url: java.lang.String = "jdbc:sqlite:$dbPath";
         val `$$delegate_0$delegate`: Properties = new Properties();
         `$$delegate_0$delegate`.put("SQLITE_THREADSAFE", "2");
         `$$delegate_0$delegate`.put("SQLITE_OPEN_FULLMUTEX", "true");
         `$$delegate_0$delegate`.put("journal_mode", journalMode);
         val driver: JdbcSqliteDriverDelegate = new JdbcSqliteDriverDelegate(
            openDataBase$lambda$1(
               Kotlin_extKt.delegateField$default(new JdbcSqliteDriver(url, `$$delegate_0$delegate`), null, 1, null)
                  .provideDelegate(null, $$delegatedProperties[0]) as ReadWriteProperty<Object, ConnectionManager>
            )
         );
         return new SQLiteDB(dbPath, driver, Database.Companion.invoke(driver as SqlDriver), journalMode, null);
      }

      @JvmStatic
      fun `openDataBase$lambda$1`(`$$$delegate_0$delegate`: ReadWriteProperty<Object, ConnectionManager>): ConnectionManager {
         return `$$$delegate_0$delegate`.getValue(null, $$delegatedProperties[0]) as ConnectionManager;
      }
   }
}
