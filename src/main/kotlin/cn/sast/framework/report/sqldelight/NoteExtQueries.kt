package cn.sast.framework.report.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.QueryKt
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function3

public class NoteExtQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun <T : Any> selectAll(mapper: (Long, String, String) -> T): Query<T> {
      return QueryKt.Query(
         313069709,
         new java.lang.String[]{"NoteExt"},
         this.getDriver(),
         "NoteExt.sq",
         "selectAll",
         "SELECT NoteExt.__note_id, NoteExt.attr_name, NoteExt.attr_value\nFROM NoteExt",
         NoteExtQueries::selectAll$lambda$0
      );
   }

   public fun selectAll(): Query<NoteExt> {
      return this.selectAll(NoteExtQueries::selectAll$lambda$1);
   }

   public fun insert(NoteExt: NoteExt) {
      this.getDriver()
         .execute(-1109476815, "INSERT OR IGNORE INTO NoteExt (__note_id, attr_name, attr_value)\nVALUES (?, ?, ?)", 3, NoteExtQueries::insert$lambda$2);
      this.notifyQueries(-1109476815, NoteExtQueries::insert$lambda$3);
   }

   @JvmStatic
   fun `selectAll$lambda$0`(`$mapper`: Function3, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.String = cursor.getString(1);
      val var10003: java.lang.String = cursor.getString(2);
      return `$mapper`.invoke(var10001, var10002, var10003);
   }

   @JvmStatic
   fun `selectAll$lambda$1`(__note_id: Long, attr_name: java.lang.String, attr_value: java.lang.String): NoteExt {
      return new NoteExt(__note_id, attr_name, attr_value);
   }

   @JvmStatic
   fun `insert$lambda$2`(`$NoteExt`: NoteExt, `$this$execute`: SqlPreparedStatement): Unit {
      `$this$execute`.bindLong(0, `$NoteExt`.get__note_id());
      `$this$execute`.bindString(1, `$NoteExt`.getAttr_name());
      `$this$execute`.bindString(2, `$NoteExt`.getAttr_value());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$3`(emit: Function1): Unit {
      emit.invoke("NoteExt");
      return Unit.INSTANCE;
   }
}
