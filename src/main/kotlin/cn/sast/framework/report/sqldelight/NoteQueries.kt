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
import cn.sast.framework.report.sqldelight.note.Verify_file
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function11
import kotlin.jvm.functions.Function2

public class NoteQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun id(
      kind: String,
      display_hint: String,
      __file_id: Long,
      _file_abs_path: String,
      line: Long,
      column: Long?,
      message_en: String,
      message_zh: String,
      __notices_region_id: Long?,
      __func_region_id: Long?
   ): ExecutableQuery<Long> {
      return new NoteQueries.IdQuery<>(
         this,
         kind,
         (long)display_hint,
         __file_id,
         (long)_file_abs_path,
         line,
         column,
         message_en,
         message_zh,
         __notices_region_id,
         __func_region_id,
         NoteQueries::id$lambda$0
      );
   }

   public fun <T : Any> verify_file(mapper: (Long, Long) -> T): ExecutableQuery<T> {
      return new NoteQueries.Verify_fileQuery(this, NoteQueries::verify_file$lambda$1);
   }

   public fun verify_file(): ExecutableQuery<Verify_file> {
      return this.verify_file(NoteQueries::verify_file$lambda$2);
   }

   public fun <T : Any> selectAll(mapper: (Long, String, String, Long, String, Long, Long?, String, String, Long?, Long?) -> T): Query<T> {
      return QueryKt.Query(
         1298079392,
         new java.lang.String[]{"Note"},
         this.getDriver(),
         "Note.sq",
         "selectAll",
         "SELECT Note.id, Note.kind, Note.display_hint, Note.__file_id, Note._file_abs_path, Note.line, Note.column, Note.message_en, Note.message_zh, Note.__notices_region_id, Note.__func_region_id\nFROM Note",
         NoteQueries::selectAll$lambda$3
      );
   }

   public fun selectAll(): Query<Note> {
      return this.selectAll(NoteQueries::selectAll$lambda$4);
   }

   public fun insert(
      kind: String,
      display_hint: String,
      __file_id: Long,
      _file_abs_path: String,
      line: Long,
      column: Long?,
      message_en: String,
      message_zh: String,
      __notices_region_id: Long?,
      __func_region_id: Long?
   ) {
      DefaultImpls.transaction$default(this as Transacter, false, NoteQueries::insert$lambda$6, 1, null);
      this.notifyQueries(-681403138, NoteQueries::insert$lambda$7);
   }

   @JvmStatic
   fun `id$lambda$0`(cursor: SqlCursor): Long {
      val var10000: java.lang.Long = cursor.getLong(0);
      return var10000;
   }

   @JvmStatic
   fun `verify_file$lambda$1`(`$mapper`: Function2, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      return `$mapper`.invoke(var10001, var10002);
   }

   @JvmStatic
   fun `verify_file$lambda$2`(id: Long, __file_id: Long): Verify_file {
      return new Verify_file(id, __file_id);
   }

   @JvmStatic
   fun `selectAll$lambda$3`(`$mapper`: Function11, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.String = cursor.getString(1);
      val var10003: java.lang.String = cursor.getString(2);
      val var10004: java.lang.Long = cursor.getLong(3);
      val var10005: java.lang.String = cursor.getString(4);
      val var10006: java.lang.Long = cursor.getLong(5);
      val var10007: java.lang.Long = cursor.getLong(6);
      val var10008: java.lang.String = cursor.getString(7);
      val var10009: java.lang.String = cursor.getString(8);
      return `$mapper`.invoke(var10001, var10002, var10003, var10004, var10005, var10006, var10007, var10008, var10009, cursor.getLong(9), cursor.getLong(10));
   }

   @JvmStatic
   fun `selectAll$lambda$4`(
      id: Long,
      kind: java.lang.String,
      display_hint: java.lang.String,
      __file_id: Long,
      _file_abs_path: java.lang.String,
      line: Long,
      column: java.lang.Long,
      message_en: java.lang.String,
      message_zh: java.lang.String,
      __notices_region_id: java.lang.Long,
      __func_region_id: java.lang.Long
   ): Note {
      return new Note(id, kind, display_hint, __file_id, _file_abs_path, line, column, message_en, message_zh, __notices_region_id, __func_region_id);
   }

   @JvmStatic
   fun `insert$lambda$6$lambda$5`(
      `$kind`: java.lang.String,
      `$display_hint`: java.lang.String,
      `$__file_id`: Long,
      `$_file_abs_path`: java.lang.String,
      `$line`: Long,
      `$column`: java.lang.Long,
      `$message_en`: java.lang.String,
      `$message_zh`: java.lang.String,
      `$__notices_region_id`: java.lang.Long,
      `$__func_region_id`: java.lang.Long,
      `$this$execute`: SqlPreparedStatement
   ): Unit {
      `$this$execute`.bindString(0, `$kind`);
      `$this$execute`.bindString(1, `$display_hint`);
      `$this$execute`.bindLong(2, `$__file_id`);
      `$this$execute`.bindString(3, `$_file_abs_path`);
      `$this$execute`.bindLong(4, `$line`);
      `$this$execute`.bindLong(5, `$column`);
      `$this$execute`.bindString(6, `$message_en`);
      `$this$execute`.bindString(7, `$message_zh`);
      `$this$execute`.bindLong(8, `$__notices_region_id`);
      `$this$execute`.bindLong(9, `$__func_region_id`);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$6`(
      `this$0`: NoteQueries,
      `$kind`: java.lang.String,
      `$display_hint`: java.lang.String,
      `$__file_id`: Long,
      `$_file_abs_path`: java.lang.String,
      `$line`: Long,
      `$column`: java.lang.Long,
      `$message_en`: java.lang.String,
      `$message_zh`: java.lang.String,
      `$__notices_region_id`: java.lang.Long,
      `$__func_region_id`: java.lang.Long,
      `$this$transaction`: TransactionWithoutReturn
   ): Unit {
      `this$0`.getDriver()
         .execute(
            -1993383633,
            "INSERT OR IGNORE INTO Note(kind, display_hint, __file_id, _file_abs_path, line, column, message_en, message_zh, __notices_region_id, __func_region_id)\n    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            10,
            NoteQueries::insert$lambda$6$lambda$5
         );
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$7`(emit: Function1): Unit {
      emit.invoke("Note");
      return Unit.INSTANCE;
   }

   private inner class IdQuery<T>(kind: String,
      display_hint: String,
      __file_id: Long,
      _file_abs_path: String,
      line: Long,
      column: Long?,
      message_en: String,
      message_zh: String,
      __notices_region_id: Long?,
      __func_region_id: Long?,
      mapper: (SqlCursor) -> Any
   ) : ExecutableQuery(mapper) {
      public final val kind: String
      public final val display_hint: String
      public final val __file_id: Long
      public final val _file_abs_path: String
      public final val line: Long
      public final val column: Long?
      public final val message_en: String
      public final val message_zh: String
      public final val __notices_region_id: Long?
      public final val __func_region_id: Long?

      init {
         this.this$0 = `this$0`;
         this.kind = kind;
         this.display_hint = display_hint;
         this.__file_id = __file_id;
         this._file_abs_path = _file_abs_path;
         this.line = line;
         this.column = column;
         this.message_en = message_en;
         this.message_zh = message_zh;
         this.__notices_region_id = __notices_region_id;
         this.__func_region_id = __func_region_id;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(this.this$0 as Transacter, false, NoteQueries.IdQuery::execute$lambda$1, 1, null) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "Note.sq:id";
      }

      @JvmStatic
      fun `execute$lambda$1$lambda$0`(`this$0`: NoteQueries.IdQuery, `$this$executeQuery`: SqlPreparedStatement): Unit {
         `$this$executeQuery`.bindString(0, `this$0`.kind);
         `$this$executeQuery`.bindString(1, `this$0`.display_hint);
         `$this$executeQuery`.bindLong(2, `this$0`.__file_id);
         `$this$executeQuery`.bindString(3, `this$0`._file_abs_path);
         `$this$executeQuery`.bindLong(4, `this$0`.line);
         `$this$executeQuery`.bindLong(5, `this$0`.column);
         `$this$executeQuery`.bindString(6, `this$0`.message_en);
         `$this$executeQuery`.bindString(7, `this$0`.message_zh);
         `$this$executeQuery`.bindLong(8, `this$0`.__notices_region_id);
         `$this$executeQuery`.bindLong(9, `this$0`.__func_region_id);
         return Unit.INSTANCE;
      }

      @JvmStatic
      fun `execute$lambda$1`(`this$0`: NoteQueries, `this$1`: NoteQueries.IdQuery, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return NoteQueries.access$getDriver(`this$0`)
            .executeQuery(
               null,
               StringsKt.trimMargin$default(
                  "\n          |SELECT id FROM Note WHERE kind = ? AND display_hint = ? AND __file_id = ? AND _file_abs_path = ? AND line = ? AND column ${if (`this$1`.column
                        == null)
                     "IS"
                     else
                     "="} ?\n          |    AND message_en = ? AND message_zh = ? AND __notices_region_id ${if (`this$1`.__notices_region_id == null)
                     "IS"
                     else
                     "="} ? AND __func_region_id ${if (`this$1`.__func_region_id == null) "IS" else "="} ?\n          ",
                  null,
                  1,
                  null
               ),
               `$mapper`,
               10,
               NoteQueries.IdQuery::execute$lambda$1$lambda$0
            );
      }
   }

   private inner class Verify_fileQuery<T>(mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      init {
         this.this$0 = `this$0`;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(this.this$0 as Transacter, false, NoteQueries.Verify_fileQuery::execute$lambda$0, 1, null) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "Note.sq:verify_file";
      }

      @JvmStatic
      fun `execute$lambda$0`(`this$0`: NoteQueries, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
            NoteQueries.access$getDriver(`this$0`),
            1014184654,
            "SELECT id, __file_id FROM Note WHERE __file_id NOT IN (SELECT id FROM File)",
            `$mapper`,
            0,
            null,
            16,
            null
         );
      }
   }
}
