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
import kotlin.jvm.functions.Function2

public class MacroExpansionQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun <T : Any> verify_note(mapper: (Long, Long) -> T): ExecutableQuery<T> {
      return new MacroExpansionQueries.Verify_noteQuery(this, MacroExpansionQueries::verify_note$lambda$0);
   }

   public fun verify_note(): ExecutableQuery<MacroExpansion> {
      return this.verify_note(MacroExpansionQueries::verify_note$lambda$1);
   }

   public fun <T : Any> selectAll(mapper: (Long, Long) -> T): Query<T> {
      return QueryKt.Query(
         1160581151,
         new java.lang.String[]{"MacroExpansion"},
         this.getDriver(),
         "MacroExpansion.sq",
         "selectAll",
         "SELECT MacroExpansion.__macro_note_set_hash_id, MacroExpansion.__macro_note_id\nFROM MacroExpansion",
         MacroExpansionQueries::selectAll$lambda$2
      );
   }

   public fun selectAll(): Query<MacroExpansion> {
      return this.selectAll(MacroExpansionQueries::selectAll$lambda$3);
   }

   public fun insert(MacroExpansion: MacroExpansion) {
      this.getDriver()
         .execute(
            -1356123169,
            "INSERT OR IGNORE INTO MacroExpansion (__macro_note_set_hash_id, __macro_note_id)\nVALUES (?, ?)",
            2,
            MacroExpansionQueries::insert$lambda$4
         );
      this.notifyQueries(-1356123169, MacroExpansionQueries::insert$lambda$5);
   }

   @JvmStatic
   fun `verify_note$lambda$0`(`$mapper`: Function2, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      return `$mapper`.invoke(var10001, var10002);
   }

   @JvmStatic
   fun `verify_note$lambda$1`(__macro_note_set_hash_id: Long, __macro_note_id: Long): MacroExpansion {
      return new MacroExpansion(__macro_note_set_hash_id, __macro_note_id);
   }

   @JvmStatic
   fun `selectAll$lambda$2`(`$mapper`: Function2, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      return `$mapper`.invoke(var10001, var10002);
   }

   @JvmStatic
   fun `selectAll$lambda$3`(__macro_note_set_hash_id: Long, __macro_note_id: Long): MacroExpansion {
      return new MacroExpansion(__macro_note_set_hash_id, __macro_note_id);
   }

   @JvmStatic
   fun `insert$lambda$4`(`$MacroExpansion`: MacroExpansion, `$this$execute`: SqlPreparedStatement): Unit {
      `$this$execute`.bindLong(0, `$MacroExpansion`.get__macro_note_set_hash_id());
      `$this$execute`.bindLong(1, `$MacroExpansion`.get__macro_note_id());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$5`(emit: Function1): Unit {
      emit.invoke("MacroExpansion");
      return Unit.INSTANCE;
   }

   private inner class Verify_noteQuery<T>(mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      init {
         this.this$0 = `this$0`;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(this.this$0 as Transacter, false, MacroExpansionQueries.Verify_noteQuery::execute$lambda$0, 1, null) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "MacroExpansion.sq:verify_note";
      }

      @JvmStatic
      fun `execute$lambda$0`(`this$0`: MacroExpansionQueries, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
            MacroExpansionQueries.access$getDriver(`this$0`),
            -555923005,
            "SELECT MacroExpansion.__macro_note_set_hash_id, MacroExpansion.__macro_note_id FROM MacroExpansion WHERE __macro_note_id NOT IN (SELECT id FROM Note)",
            `$mapper`,
            0,
            null,
            16,
            null
         );
      }
   }
}
