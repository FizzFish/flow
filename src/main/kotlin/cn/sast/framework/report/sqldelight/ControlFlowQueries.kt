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
import cn.sast.framework.report.sqldelight.controlFlow.Verify_file
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function7

public class ControlFlowQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun id(__file_id: Long, _file_abs_path: String, message_en: String?, message_zh: String?, __edge_from_region_id: Long, __edge_to_region_id: Long): ExecutableQuery<
         Long
      > {
      return new ControlFlowQueries.IdQuery<>(
         (long)this, __file_id, _file_abs_path, message_en, (long)message_zh, __edge_from_region_id, __edge_to_region_id, ControlFlowQueries::id$lambda$0
      );
   }

   public fun <T : Any> verify_file(mapper: (Long, Long) -> T): ExecutableQuery<T> {
      return new ControlFlowQueries.Verify_fileQuery(this, ControlFlowQueries::verify_file$lambda$1);
   }

   public fun verify_file(): ExecutableQuery<Verify_file> {
      return this.verify_file(ControlFlowQueries::verify_file$lambda$2);
   }

   public fun <T : Any> selectAll(mapper: (Long, Long, String, String?, String?, Long, Long) -> T): Query<T> {
      return QueryKt.Query(
         590154801,
         new java.lang.String[]{"ControlFlow"},
         this.getDriver(),
         "ControlFlow.sq",
         "selectAll",
         "SELECT ControlFlow.id, ControlFlow.__file_id, ControlFlow._file_abs_path, ControlFlow.message_en, ControlFlow.message_zh, ControlFlow.__edge_from_region_id, ControlFlow.__edge_to_region_id\nFROM ControlFlow",
         ControlFlowQueries::selectAll$lambda$3
      );
   }

   public fun selectAll(): Query<ControlFlow> {
      return this.selectAll(ControlFlowQueries::selectAll$lambda$4);
   }

   public fun insert(__file_id: Long, _file_abs_path: String, message_en: String?, message_zh: String?, __edge_from_region_id: Long, __edge_to_region_id: Long) {
      DefaultImpls.transaction$default(this as Transacter, false, ControlFlowQueries::insert$lambda$6, 1, null);
      this.notifyQueries(1615921421, ControlFlowQueries::insert$lambda$7);
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
   fun `selectAll$lambda$3`(`$mapper`: Function7, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      val var10003: java.lang.String = cursor.getString(2);
      val var10004: java.lang.String = cursor.getString(3);
      val var10005: java.lang.String = cursor.getString(4);
      val var10006: java.lang.Long = cursor.getLong(5);
      val var10007: java.lang.Long = cursor.getLong(6);
      return `$mapper`.invoke(var10001, var10002, var10003, var10004, var10005, var10006, var10007);
   }

   @JvmStatic
   fun `selectAll$lambda$4`(
      id: Long,
      __file_id: Long,
      _file_abs_path: java.lang.String,
      message_en: java.lang.String,
      message_zh: java.lang.String,
      __edge_from_region_id: Long,
      __edge_to_region_id: Long
   ): ControlFlow {
      return new ControlFlow(id, __file_id, _file_abs_path, message_en, message_zh, __edge_from_region_id, __edge_to_region_id);
   }

   @JvmStatic
   fun `insert$lambda$6$lambda$5`(
      `$__file_id`: Long,
      `$_file_abs_path`: java.lang.String,
      `$message_en`: java.lang.String,
      `$message_zh`: java.lang.String,
      `$__edge_from_region_id`: Long,
      `$__edge_to_region_id`: Long,
      `$this$execute`: SqlPreparedStatement
   ): Unit {
      `$this$execute`.bindLong(0, `$__file_id`);
      `$this$execute`.bindString(1, `$_file_abs_path`);
      `$this$execute`.bindString(2, `$message_en`);
      `$this$execute`.bindString(3, `$message_zh`);
      `$this$execute`.bindLong(4, `$__edge_from_region_id`);
      `$this$execute`.bindLong(5, `$__edge_to_region_id`);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$6`(
      `this$0`: ControlFlowQueries,
      `$__file_id`: Long,
      `$_file_abs_path`: java.lang.String,
      `$message_en`: java.lang.String,
      `$message_zh`: java.lang.String,
      `$__edge_from_region_id`: Long,
      `$__edge_to_region_id`: Long,
      `$this$transaction`: TransactionWithoutReturn
   ): Unit {
      `this$0`.getDriver()
         .execute(
            -1877672578,
            "INSERT OR IGNORE INTO ControlFlow(__file_id, _file_abs_path, message_en, message_zh, __edge_from_region_id, __edge_to_region_id)\n    VALUES (?, ?, ?, ?, ?, ?)",
            6,
            ControlFlowQueries::insert$lambda$6$lambda$5
         );
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$7`(emit: Function1): Unit {
      emit.invoke("ControlFlow");
      return Unit.INSTANCE;
   }

   private inner class IdQuery<T>(__file_id: Long,
      _file_abs_path: String,
      message_en: String?,
      message_zh: String?,
      __edge_from_region_id: Long,
      __edge_to_region_id: Long,
      mapper: (SqlCursor) -> Any
   ) : ExecutableQuery(mapper) {
      public final val __file_id: Long
      public final val _file_abs_path: String
      public final val message_en: String?
      public final val message_zh: String?
      public final val __edge_from_region_id: Long
      public final val __edge_to_region_id: Long

      init {
         this.this$0 = `this$0`;
         this.__file_id = __file_id;
         this._file_abs_path = _file_abs_path;
         this.message_en = message_en;
         this.message_zh = message_zh;
         this.__edge_from_region_id = __edge_from_region_id;
         this.__edge_to_region_id = __edge_to_region_id;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(this.this$0 as Transacter, false, ControlFlowQueries.IdQuery::execute$lambda$1, 1, null) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "ControlFlow.sq:id";
      }

      @JvmStatic
      fun `execute$lambda$1$lambda$0`(`this$0`: ControlFlowQueries.IdQuery, `$this$executeQuery`: SqlPreparedStatement): Unit {
         `$this$executeQuery`.bindLong(0, `this$0`.__file_id);
         `$this$executeQuery`.bindString(1, `this$0`._file_abs_path);
         `$this$executeQuery`.bindString(2, `this$0`.message_en);
         `$this$executeQuery`.bindString(3, `this$0`.message_zh);
         `$this$executeQuery`.bindLong(4, `this$0`.__edge_from_region_id);
         `$this$executeQuery`.bindLong(5, `this$0`.__edge_to_region_id);
         return Unit.INSTANCE;
      }

      @JvmStatic
      fun `execute$lambda$1`(
         `this$0`: ControlFlowQueries, `this$1`: ControlFlowQueries.IdQuery, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn
      ): QueryResult {
         return ControlFlowQueries.access$getDriver(`this$0`)
            .executeQuery(
               null,
               "SELECT id FROM ControlFlow WHERE __file_id = ? AND _file_abs_path = ? AND message_en ${if (`this$1`.message_en == null) "IS" else "="} ? AND message_zh ${if (`this$1`.message_zh
                     == null)
                  "IS"
                  else
                  "="} ? AND __edge_from_region_id = ? AND __edge_to_region_id = ?",
               `$mapper`,
               6,
               ControlFlowQueries.IdQuery::execute$lambda$1$lambda$0
            );
      }
   }

   private inner class Verify_fileQuery<T>(mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      init {
         this.this$0 = `this$0`;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(this.this$0 as Transacter, false, ControlFlowQueries.Verify_fileQuery::execute$lambda$0, 1, null) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "ControlFlow.sq:verify_file";
      }

      @JvmStatic
      fun `execute$lambda$0`(`this$0`: ControlFlowQueries, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
            ControlFlowQueries.access$getDriver(`this$0`),
            2004744159,
            "SELECT id, __file_id FROM ControlFlow WHERE __file_id NOT IN (SELECT id FROM File)",
            `$mapper`,
            0,
            null,
            16,
            null
         );
      }
   }
}
