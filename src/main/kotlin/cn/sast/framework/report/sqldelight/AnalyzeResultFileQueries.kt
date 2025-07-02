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
import kotlin.jvm.functions.Function3

public class AnalyzeResultFileQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun <T : Any> verify_file(mapper: (String, String?, Long) -> T): ExecutableQuery<T> {
      return new AnalyzeResultFileQueries.Verify_fileQuery(this, AnalyzeResultFileQueries::verify_file$lambda$0);
   }

   public fun verify_file(): ExecutableQuery<AnalyzerResultFile> {
      return this.verify_file(AnalyzeResultFileQueries::verify_file$lambda$1);
   }

   public fun <T : Any> selectAll(mapper: (String, String?, Long) -> T): Query<T> {
      return QueryKt.Query(
         1331798327,
         new java.lang.String[]{"AnalyzerResultFile"},
         this.getDriver(),
         "AnalyzeResultFile.sq",
         "selectAll",
         "SELECT AnalyzerResultFile.file_name, AnalyzerResultFile.file_path, AnalyzerResultFile.__file_id\nFROM AnalyzerResultFile",
         AnalyzeResultFileQueries::selectAll$lambda$2
      );
   }

   public fun selectAll(): Query<AnalyzerResultFile> {
      return this.selectAll(AnalyzeResultFileQueries::selectAll$lambda$3);
   }

   public fun insert(AnalyzerResultFile: AnalyzerResultFile) {
      this.getDriver()
         .execute(
            397277639,
            "INSERT OR IGNORE INTO AnalyzerResultFile (file_name, file_path, __file_id)\nVALUES (?, ?, ?)",
            3,
            AnalyzeResultFileQueries::insert$lambda$4
         );
      this.notifyQueries(397277639, AnalyzeResultFileQueries::insert$lambda$5);
   }

   @JvmStatic
   fun `verify_file$lambda$0`(`$mapper`: Function3, cursor: SqlCursor): Any {
      val var10001: java.lang.String = cursor.getString(0);
      val var10002: java.lang.String = cursor.getString(1);
      val var10003: java.lang.Long = cursor.getLong(2);
      return `$mapper`.invoke(var10001, var10002, var10003);
   }

   @JvmStatic
   fun `verify_file$lambda$1`(file_name: java.lang.String, file_path: java.lang.String, __file_id: Long): AnalyzerResultFile {
      return new AnalyzerResultFile(file_name, file_path, __file_id);
   }

   @JvmStatic
   fun `selectAll$lambda$2`(`$mapper`: Function3, cursor: SqlCursor): Any {
      val var10001: java.lang.String = cursor.getString(0);
      val var10002: java.lang.String = cursor.getString(1);
      val var10003: java.lang.Long = cursor.getLong(2);
      return `$mapper`.invoke(var10001, var10002, var10003);
   }

   @JvmStatic
   fun `selectAll$lambda$3`(file_name: java.lang.String, file_path: java.lang.String, __file_id: Long): AnalyzerResultFile {
      return new AnalyzerResultFile(file_name, file_path, __file_id);
   }

   @JvmStatic
   fun `insert$lambda$4`(`$AnalyzerResultFile`: AnalyzerResultFile, `$this$execute`: SqlPreparedStatement): Unit {
      `$this$execute`.bindString(0, `$AnalyzerResultFile`.getFile_name());
      `$this$execute`.bindString(1, `$AnalyzerResultFile`.getFile_path());
      `$this$execute`.bindLong(2, `$AnalyzerResultFile`.get__file_id());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$5`(emit: Function1): Unit {
      emit.invoke("AnalyzerResultFile");
      return Unit.INSTANCE;
   }

   private inner class Verify_fileQuery<T>(mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      init {
         this.this$0 = `this$0`;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(
            this.this$0 as Transacter, false, AnalyzeResultFileQueries.Verify_fileQuery::execute$lambda$0, 1, null
         ) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "AnalyzeResultFile.sq:verify_file";
      }

      @JvmStatic
      fun `execute$lambda$0`(`this$0`: AnalyzeResultFileQueries, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
            AnalyzeResultFileQueries.access$getDriver(`this$0`),
            -1649108507,
            "SELECT AnalyzerResultFile.file_name, AnalyzerResultFile.file_path, AnalyzerResultFile.__file_id FROM AnalyzerResultFile WHERE __file_id NOT IN (SELECT id FROM File)",
            `$mapper`,
            0,
            null,
            16,
            null
         );
      }
   }
}
