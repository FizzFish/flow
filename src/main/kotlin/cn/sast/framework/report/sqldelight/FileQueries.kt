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
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function7

public class FileQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun id(file_raw_content_hash: String, relative_path: String): ExecutableQuery<Long> {
      return new FileQueries.IdQuery<>(this, file_raw_content_hash, relative_path, FileQueries::id$lambda$0);
   }

   public fun <T : Any> selectAll(mapper: (Long, String, String, Long, String?, Long, ByteArray) -> T): Query<T> {
      return QueryKt.Query(
         -1345738410,
         new java.lang.String[]{"File"},
         this.getDriver(),
         "File.sq",
         "selectAll",
         "SELECT File.id, File.file_raw_content_hash, File.relative_path, File.lines, File.encoding, File.file_raw_content_size, File.file_raw_content\nFROM File",
         FileQueries::selectAll$lambda$1
      );
   }

   public fun selectAll(): Query<File> {
      return this.selectAll(FileQueries::selectAll$lambda$2);
   }

   public fun insert(file_raw_content_hash: String, relative_path: String, lines: Long, file_raw_content_size: Long, file_raw_content: ByteArray) {
      DefaultImpls.transaction$default(this as Transacter, false, FileQueries::insert$lambda$4, 1, null);
      this.notifyQueries(-204145144, FileQueries::insert$lambda$5);
   }

   @JvmStatic
   fun `id$lambda$0`(cursor: SqlCursor): Long {
      val var10000: java.lang.Long = cursor.getLong(0);
      return var10000;
   }

   @JvmStatic
   fun `selectAll$lambda$1`(`$mapper`: Function7, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.String = cursor.getString(1);
      val var10003: java.lang.String = cursor.getString(2);
      val var10004: java.lang.Long = cursor.getLong(3);
      val var10005: java.lang.String = cursor.getString(4);
      val var10006: java.lang.Long = cursor.getLong(5);
      val var10007: ByteArray = cursor.getBytes(6);
      return `$mapper`.invoke(var10001, var10002, var10003, var10004, var10005, var10006, var10007);
   }

   @JvmStatic
   fun `selectAll$lambda$2`(
      id: Long,
      file_raw_content_hash: java.lang.String,
      relative_path: java.lang.String,
      lines: Long,
      encoding: java.lang.String,
      file_raw_content_size: Long,
      file_raw_content: ByteArray
   ): File {
      return new File(id, file_raw_content_hash, relative_path, lines, encoding, file_raw_content_size, file_raw_content);
   }

   @JvmStatic
   fun `insert$lambda$4$lambda$3`(
      `$file_raw_content_hash`: java.lang.String,
      `$relative_path`: java.lang.String,
      `$lines`: Long,
      `$file_raw_content_size`: Long,
      `$file_raw_content`: ByteArray,
      `$this$execute`: SqlPreparedStatement
   ): Unit {
      `$this$execute`.bindString(0, `$file_raw_content_hash`);
      `$this$execute`.bindString(1, `$relative_path`);
      `$this$execute`.bindLong(2, `$lines`);
      `$this$execute`.bindLong(3, `$file_raw_content_size`);
      `$this$execute`.bindBytes(4, `$file_raw_content`);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$4`(
      `this$0`: FileQueries,
      `$file_raw_content_hash`: java.lang.String,
      `$relative_path`: java.lang.String,
      `$lines`: Long,
      `$file_raw_content_size`: Long,
      `$file_raw_content`: ByteArray,
      `$this$transaction`: TransactionWithoutReturn
   ): Unit {
      `this$0`.getDriver()
         .execute(
            1385015225,
            "INSERT OR IGNORE INTO File(file_raw_content_hash, relative_path, lines, file_raw_content_size, file_raw_content)\n    VALUES (?, ?, ?, ?, ?)",
            5,
            FileQueries::insert$lambda$4$lambda$3
         );
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$5`(emit: Function1): Unit {
      emit.invoke("File");
      return Unit.INSTANCE;
   }

   private inner class IdQuery<T>(file_raw_content_hash: String, relative_path: String, mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      public final val file_raw_content_hash: String
      public final val relative_path: String

      init {
         this.this$0 = `this$0`;
         this.file_raw_content_hash = file_raw_content_hash;
         this.relative_path = relative_path;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(this.this$0 as Transacter, false, FileQueries.IdQuery::execute$lambda$1, 1, null) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "File.sq:id";
      }

      @JvmStatic
      fun `execute$lambda$1$lambda$0`(`this$0`: FileQueries.IdQuery, `$this$executeQuery`: SqlPreparedStatement): Unit {
         `$this$executeQuery`.bindString(0, `this$0`.file_raw_content_hash);
         `$this$executeQuery`.bindString(1, `this$0`.relative_path);
         return Unit.INSTANCE;
      }

      @JvmStatic
      fun `execute$lambda$1`(`this$0`: FileQueries, `$mapper`: Function1, `this$1`: FileQueries.IdQuery, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return FileQueries.access$getDriver(`this$0`)
            .executeQuery(
               2042233115,
               "SELECT id FROM File WHERE file_raw_content_hash = ? AND relative_path = ?",
               `$mapper`,
               2,
               FileQueries.IdQuery::execute$lambda$1$lambda$0
            );
      }
   }
}
