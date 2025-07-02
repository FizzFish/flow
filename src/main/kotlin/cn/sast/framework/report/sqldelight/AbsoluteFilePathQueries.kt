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

public class AbsoluteFilePathQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun <T : Any> verify_absolute_file_path(mapper: (String, Long) -> T): ExecutableQuery<T> {
      return new AbsoluteFilePathQueries.Verify_absolute_file_pathQuery(this, AbsoluteFilePathQueries::verify_absolute_file_path$lambda$0);
   }

   public fun verify_absolute_file_path(): ExecutableQuery<AbsoluteFilePath> {
      return this.verify_absolute_file_path(AbsoluteFilePathQueries::verify_absolute_file_path$lambda$1);
   }

   public fun <T : Any> selectAll(mapper: (String, Long) -> T): Query<T> {
      return QueryKt.Query(
         77310330,
         new java.lang.String[]{"AbsoluteFilePath"},
         this.getDriver(),
         "AbsoluteFilePath.sq",
         "selectAll",
         "SELECT AbsoluteFilePath.file_abs_path, AbsoluteFilePath.__file_id\nFROM AbsoluteFilePath",
         AbsoluteFilePathQueries::selectAll$lambda$2
      );
   }

   public fun selectAll(): Query<AbsoluteFilePath> {
      return this.selectAll(AbsoluteFilePathQueries::selectAll$lambda$3);
   }

   public fun insert(AbsoluteFilePath: AbsoluteFilePath) {
      this.getDriver()
         .execute(365662308, "INSERT OR IGNORE INTO AbsoluteFilePath (file_abs_path, __file_id) VALUES (?, ?)", 2, AbsoluteFilePathQueries::insert$lambda$4);
      this.notifyQueries(365662308, AbsoluteFilePathQueries::insert$lambda$5);
   }

   @JvmStatic
   fun `verify_absolute_file_path$lambda$0`(`$mapper`: Function2, cursor: SqlCursor): Any {
      val var10001: java.lang.String = cursor.getString(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      return `$mapper`.invoke(var10001, var10002);
   }

   @JvmStatic
   fun `verify_absolute_file_path$lambda$1`(file_abs_path: java.lang.String, __file_id: Long): AbsoluteFilePath {
      return new AbsoluteFilePath(file_abs_path, __file_id);
   }

   @JvmStatic
   fun `selectAll$lambda$2`(`$mapper`: Function2, cursor: SqlCursor): Any {
      val var10001: java.lang.String = cursor.getString(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      return `$mapper`.invoke(var10001, var10002);
   }

   @JvmStatic
   fun `selectAll$lambda$3`(file_abs_path: java.lang.String, __file_id: Long): AbsoluteFilePath {
      return new AbsoluteFilePath(file_abs_path, __file_id);
   }

   @JvmStatic
   fun `insert$lambda$4`(`$AbsoluteFilePath`: AbsoluteFilePath, `$this$execute`: SqlPreparedStatement): Unit {
      `$this$execute`.bindString(0, `$AbsoluteFilePath`.getFile_abs_path());
      `$this$execute`.bindLong(1, `$AbsoluteFilePath`.get__file_id());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$5`(emit: Function1): Unit {
      emit.invoke("AbsoluteFilePath");
      return Unit.INSTANCE;
   }

   private inner class Verify_absolute_file_pathQuery<T>(mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      init {
         this.this$0 = `this$0`;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(
            this.this$0 as Transacter, false, AbsoluteFilePathQueries.Verify_absolute_file_pathQuery::execute$lambda$0, 1, null
         ) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "AbsoluteFilePath.sq:verify_absolute_file_path";
      }

      @JvmStatic
      fun `execute$lambda$0`(`this$0`: AbsoluteFilePathQueries, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
            AbsoluteFilePathQueries.access$getDriver(`this$0`),
            -1503538388,
            "SELECT AbsoluteFilePath.file_abs_path, AbsoluteFilePath.__file_id FROM AbsoluteFilePath WHERE __file_id NOT IN (SELECT id FROM File)",
            `$mapper`,
            0,
            null,
            16,
            null
         );
      }
   }
}
