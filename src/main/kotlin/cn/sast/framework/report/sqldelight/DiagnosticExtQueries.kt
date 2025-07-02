package cn.sast.framework.report.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.QueryKt
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function3

public class DiagnosticExtQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun <T : Any> selectAll(mapper: (Long, String, String) -> T): Query<T> {
      return QueryKt.Query(
         900870786,
         new java.lang.String[]{"DiagnosticExt"},
         this.getDriver(),
         "DiagnosticExt.sq",
         "selectAll",
         "SELECT DiagnosticExt.__diagnostic_id, DiagnosticExt.attr_name, DiagnosticExt.attr_value\nFROM DiagnosticExt",
         DiagnosticExtQueries::selectAll$lambda$0
      );
   }

   public fun selectAll(): Query<DiagnosticExt> {
      return this.selectAll(DiagnosticExtQueries::selectAll$lambda$1);
   }

   public fun insert(DiagnosticExt: DiagnosticExt) {
      this.getDriver()
         .execute(
            -356457380,
            "INSERT OR IGNORE INTO DiagnosticExt (__diagnostic_id, attr_name, attr_value)\nVALUES (?, ?, ?)",
            3,
            DiagnosticExtQueries::insert$lambda$2
         );
      this.notifyQueries(-356457380, DiagnosticExtQueries::insert$lambda$3);
   }

   @JvmStatic
   fun `selectAll$lambda$0`(`$mapper`: Function3, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.String = cursor.getString(1);
      val var10003: java.lang.String = cursor.getString(2);
      return `$mapper`.invoke(var10001, var10002, var10003);
   }

   @JvmStatic
   fun `selectAll$lambda$1`(__diagnostic_id: Long, attr_name: java.lang.String, attr_value: java.lang.String): DiagnosticExt {
      return new DiagnosticExt(__diagnostic_id, attr_name, attr_value);
   }

   @JvmStatic
   fun `insert$lambda$2`(`$DiagnosticExt`: DiagnosticExt, `$this$execute`: SqlPreparedStatement): Unit {
      `$this$execute`.bindLong(0, `$DiagnosticExt`.get__diagnostic_id());
      `$this$execute`.bindString(1, `$DiagnosticExt`.getAttr_name());
      `$this$execute`.bindString(2, `$DiagnosticExt`.getAttr_value());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$3`(emit: Function1): Unit {
      emit.invoke("DiagnosticExt");
      return Unit.INSTANCE;
   }
}
