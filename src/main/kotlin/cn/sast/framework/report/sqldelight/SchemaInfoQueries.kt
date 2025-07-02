package cn.sast.framework.report.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.QueryKt
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2

public class SchemaInfoQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun <T : Any> selectAll(mapper: (String, String) -> T): Query<T> {
      return QueryKt.Query(
         -1428765821,
         new java.lang.String[]{"SchemaInfo"},
         this.getDriver(),
         "SchemaInfo.sq",
         "selectAll",
         "SELECT SchemaInfo.key, SchemaInfo.value FROM SchemaInfo",
         SchemaInfoQueries::selectAll$lambda$0
      );
   }

   public fun selectAll(): Query<SchemaInfo> {
      return this.selectAll(SchemaInfoQueries::selectAll$lambda$1);
   }

   public fun insert(SchemaInfo: SchemaInfo) {
      this.getDriver().execute(710466299, "INSERT OR IGNORE INTO SchemaInfo (key, value) VALUES (?, ?)", 2, SchemaInfoQueries::insert$lambda$2);
      this.notifyQueries(710466299, SchemaInfoQueries::insert$lambda$3);
   }

   @JvmStatic
   fun `selectAll$lambda$0`(`$mapper`: Function2, cursor: SqlCursor): Any {
      val var10001: java.lang.String = cursor.getString(0);
      val var10002: java.lang.String = cursor.getString(1);
      return `$mapper`.invoke(var10001, var10002);
   }

   @JvmStatic
   fun `selectAll$lambda$1`(key: java.lang.String, value_: java.lang.String): SchemaInfo {
      return new SchemaInfo(key, value_);
   }

   @JvmStatic
   fun `insert$lambda$2`(`$SchemaInfo`: SchemaInfo, `$this$execute`: SqlPreparedStatement): Unit {
      `$this$execute`.bindString(0, `$SchemaInfo`.getKey());
      `$this$execute`.bindString(1, `$SchemaInfo`.getValue_());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$3`(emit: Function1): Unit {
      emit.invoke("SchemaInfo");
      return Unit.INSTANCE;
   }
}
