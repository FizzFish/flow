package cn.sast.framework.report.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.QueryKt
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function8

public class RuleSetInfoQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun <T : Any> selectAll(mapper: (String, String, String?, String?, String?, Long?, String, String) -> T): Query<T> {
      return QueryKt.Query(
         1731641704,
         new java.lang.String[]{"RuleSetInfo"},
         this.getDriver(),
         "RuleSetInfo.sq",
         "selectAll",
         "SELECT RuleSetInfo.name, RuleSetInfo.language, RuleSetInfo.description, RuleSetInfo.prefix, RuleSetInfo.id_pattern, RuleSetInfo.section_level, RuleSetInfo.version, RuleSetInfo.revision\nFROM RuleSetInfo",
         RuleSetInfoQueries::selectAll$lambda$0
      );
   }

   public fun selectAll(): Query<RuleSetInfo> {
      return this.selectAll(RuleSetInfoQueries::selectAll$lambda$1);
   }

   public fun insert(RuleSetInfo: RuleSetInfo) {
      this.getDriver()
         .execute(
            1006841654,
            "INSERT OR IGNORE INTO RuleSetInfo (name, language, description, prefix, id_pattern, section_level, version, revision)\nVALUES (?, ?, ?, ?, ?, ?, ?, ?)",
            8,
            RuleSetInfoQueries::insert$lambda$2
         );
      this.notifyQueries(1006841654, RuleSetInfoQueries::insert$lambda$3);
   }

   @JvmStatic
   fun `selectAll$lambda$0`(`$mapper`: Function8, cursor: SqlCursor): Any {
      val var10001: java.lang.String = cursor.getString(0);
      val var10002: java.lang.String = cursor.getString(1);
      val var10003: java.lang.String = cursor.getString(2);
      val var10004: java.lang.String = cursor.getString(3);
      val var10005: java.lang.String = cursor.getString(4);
      val var10006: java.lang.Long = cursor.getLong(5);
      val var10007: java.lang.String = cursor.getString(6);
      val var10008: java.lang.String = cursor.getString(7);
      return `$mapper`.invoke(var10001, var10002, var10003, var10004, var10005, var10006, var10007, var10008);
   }

   @JvmStatic
   fun `selectAll$lambda$1`(
      name: java.lang.String,
      language: java.lang.String,
      description: java.lang.String,
      prefix: java.lang.String,
      id_pattern: java.lang.String,
      section_level: java.lang.Long,
      version: java.lang.String,
      revision: java.lang.String
   ): RuleSetInfo {
      return new RuleSetInfo(name, language, description, prefix, id_pattern, section_level, version, revision);
   }

   @JvmStatic
   fun `insert$lambda$2`(`$RuleSetInfo`: RuleSetInfo, `$this$execute`: SqlPreparedStatement): Unit {
      `$this$execute`.bindString(0, `$RuleSetInfo`.getName());
      `$this$execute`.bindString(1, `$RuleSetInfo`.getLanguage());
      `$this$execute`.bindString(2, `$RuleSetInfo`.getDescription());
      `$this$execute`.bindString(3, `$RuleSetInfo`.getPrefix());
      `$this$execute`.bindString(4, `$RuleSetInfo`.getId_pattern());
      `$this$execute`.bindLong(5, `$RuleSetInfo`.getSection_level());
      `$this$execute`.bindString(6, `$RuleSetInfo`.getVersion());
      `$this$execute`.bindString(7, `$RuleSetInfo`.getRevision());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$3`(emit: Function1): Unit {
      emit.invoke("RuleSetInfo");
      return Unit.INSTANCE;
   }
}
