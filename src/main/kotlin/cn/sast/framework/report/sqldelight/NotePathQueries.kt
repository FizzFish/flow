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
import kotlin.jvm.functions.Function5

public class NotePathQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun <T : Any> verify_note(mapper: (Long, Long, Long?, Long?, Long) -> T): ExecutableQuery<T> {
      return new NotePathQueries.Verify_noteQuery(this, NotePathQueries::verify_note$lambda$0);
   }

   public fun verify_note(): ExecutableQuery<NotePath> {
      return this.verify_note(NotePathQueries::verify_note$lambda$1);
   }

   public fun <T : Any> selectAll(mapper: (Long, Long, Long?, Long?, Long) -> T): Query<T> {
      return QueryKt.Query(
         -1170787845,
         new java.lang.String[]{"NotePath"},
         this.getDriver(),
         "NotePath.sq",
         "selectAll",
         "SELECT NotePath.__note_array_hash_id, NotePath.note_sequence, NotePath.note_stack_depth, NotePath.note_is_key_event, NotePath.__note_id\nFROM NotePath",
         NotePathQueries::selectAll$lambda$2
      );
   }

   public fun selectAll(): Query<NotePath> {
      return this.selectAll(NotePathQueries::selectAll$lambda$3);
   }

   public fun insert(NotePath: NotePath) {
      this.getDriver()
         .execute(
            -476332157,
            "INSERT OR IGNORE INTO NotePath (__note_array_hash_id, note_sequence, note_stack_depth, note_is_key_event, __note_id)\nVALUES (?, ?, ?, ?, ?)",
            5,
            NotePathQueries::insert$lambda$4
         );
      this.notifyQueries(-476332157, NotePathQueries::insert$lambda$5);
   }

   @JvmStatic
   fun `verify_note$lambda$0`(`$mapper`: Function5, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      val var10003: java.lang.Long = cursor.getLong(2);
      val var10004: java.lang.Long = cursor.getLong(3);
      val var10005: java.lang.Long = cursor.getLong(4);
      return `$mapper`.invoke(var10001, var10002, var10003, var10004, var10005);
   }

   @JvmStatic
   fun `verify_note$lambda$1`(
      __note_array_hash_id: Long, note_sequence: Long, note_stack_depth: java.lang.Long, note_is_key_event: java.lang.Long, __note_id: Long
   ): NotePath {
      return new NotePath(__note_array_hash_id, note_sequence, note_stack_depth, note_is_key_event, __note_id);
   }

   @JvmStatic
   fun `selectAll$lambda$2`(`$mapper`: Function5, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      val var10003: java.lang.Long = cursor.getLong(2);
      val var10004: java.lang.Long = cursor.getLong(3);
      val var10005: java.lang.Long = cursor.getLong(4);
      return `$mapper`.invoke(var10001, var10002, var10003, var10004, var10005);
   }

   @JvmStatic
   fun `selectAll$lambda$3`(
      __note_array_hash_id: Long, note_sequence: Long, note_stack_depth: java.lang.Long, note_is_key_event: java.lang.Long, __note_id: Long
   ): NotePath {
      return new NotePath(__note_array_hash_id, note_sequence, note_stack_depth, note_is_key_event, __note_id);
   }

   @JvmStatic
   fun `insert$lambda$4`(`$NotePath`: NotePath, `$this$execute`: SqlPreparedStatement): Unit {
      `$this$execute`.bindLong(0, `$NotePath`.get__note_array_hash_id());
      `$this$execute`.bindLong(1, `$NotePath`.getNote_sequence());
      `$this$execute`.bindLong(2, `$NotePath`.getNote_stack_depth());
      `$this$execute`.bindLong(3, `$NotePath`.getNote_is_key_event());
      `$this$execute`.bindLong(4, `$NotePath`.get__note_id());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$5`(emit: Function1): Unit {
      emit.invoke("NotePath");
      return Unit.INSTANCE;
   }

   private inner class Verify_noteQuery<T>(mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      init {
         this.this$0 = `this$0`;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(this.this$0 as Transacter, false, NotePathQueries.Verify_noteQuery::execute$lambda$0, 1, null) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "NotePath.sq:verify_note";
      }

      @JvmStatic
      fun `execute$lambda$0`(`this$0`: NotePathQueries, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
            NotePathQueries.access$getDriver(`this$0`),
            -1676993121,
            "SELECT NotePath.__note_array_hash_id, NotePath.note_sequence, NotePath.note_stack_depth, NotePath.note_is_key_event, NotePath.__note_id FROM NotePath WHERE __note_id NOT IN (SELECT id FROM Note)",
            `$mapper`,
            0,
            null,
            16,
            null
         );
      }
   }
}
