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
import kotlin.jvm.functions.Function6

public class RegionQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun id(__file_id: Long, start_line: Long, start_column: Long?, end_line: Long?, end_column: Long?): ExecutableQuery<Long> {
      return new RegionQueries.IdQuery<>((long)this, __file_id, start_line, start_column, end_line, end_column, RegionQueries::id$lambda$0);
   }

   public fun <T : Any> selectAll(mapper: (Long, Long, Long, Long?, Long?, Long?) -> T): Query<T> {
      return QueryKt.Query(
         -1166723042,
         new java.lang.String[]{"Region"},
         this.getDriver(),
         "Region.sq",
         "selectAll",
         "SELECT Region.id, Region.__file_id, Region.start_line, Region.start_column, Region.end_line, Region.end_column\nFROM Region",
         RegionQueries::selectAll$lambda$1
      );
   }

   public fun selectAll(): Query<Region> {
      return this.selectAll(RegionQueries::selectAll$lambda$2);
   }

   public fun insert(__file_id: Long, start_line: Long, start_column: Long?, end_line: Long?, end_column: Long?) {
      DefaultImpls.transaction$default(this as Transacter, false, RegionQueries::insert$lambda$4, 1, null);
      this.notifyQueries(1577657408, RegionQueries::insert$lambda$5);
   }

   @JvmStatic
   fun `id$lambda$0`(cursor: SqlCursor): Long {
      val var10000: java.lang.Long = cursor.getLong(0);
      return var10000;
   }

   @JvmStatic
   fun `selectAll$lambda$1`(`$mapper`: Function6, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      val var10003: java.lang.Long = cursor.getLong(2);
      return `$mapper`.invoke(var10001, var10002, var10003, cursor.getLong(3), cursor.getLong(4), cursor.getLong(5));
   }

   @JvmStatic
   fun `selectAll$lambda$2`(id: Long, __file_id: Long, start_line: Long, start_column: java.lang.Long, end_line: java.lang.Long, end_column: java.lang.Long): Region {
      return new Region(id, __file_id, start_line, start_column, end_line, end_column);
   }

   @JvmStatic
   fun `insert$lambda$4$lambda$3`(
      `$__file_id`: Long,
      `$start_line`: Long,
      `$start_column`: java.lang.Long,
      `$end_line`: java.lang.Long,
      `$end_column`: java.lang.Long,
      `$this$execute`: SqlPreparedStatement
   ): Unit {
      `$this$execute`.bindLong(0, `$__file_id`);
      `$this$execute`.bindLong(1, `$start_line`);
      `$this$execute`.bindLong(2, `$start_column`);
      `$this$execute`.bindLong(3, `$end_line`);
      `$this$execute`.bindLong(4, `$end_column`);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$4`(
      `this$0`: RegionQueries,
      `$__file_id`: Long,
      `$start_line`: Long,
      `$start_column`: java.lang.Long,
      `$end_line`: java.lang.Long,
      `$end_column`: java.lang.Long,
      `$this$transaction`: TransactionWithoutReturn
   ): Unit {
      `this$0`.getDriver()
         .execute(
            5316593,
            "INSERT OR IGNORE INTO Region(__file_id, start_line, start_column, end_line, end_column)\n    VALUES (?, ?, ?, ?, ?)",
            5,
            RegionQueries::insert$lambda$4$lambda$3
         );
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$5`(emit: Function1): Unit {
      emit.invoke("Region");
      return Unit.INSTANCE;
   }

   private inner class IdQuery<T>(__file_id: Long, start_line: Long, start_column: Long?, end_line: Long?, end_column: Long?, mapper: (SqlCursor) -> Any) : ExecutableQuery(
         mapper
      ) {
      public final val __file_id: Long
      public final val start_line: Long
      public final val start_column: Long?
      public final val end_line: Long?
      public final val end_column: Long?

      init {
         this.this$0 = `this$0`;
         this.__file_id = __file_id;
         this.start_line = start_line;
         this.start_column = start_column;
         this.end_line = end_line;
         this.end_column = end_column;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(this.this$0 as Transacter, false, RegionQueries.IdQuery::execute$lambda$1, 1, null) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "Region.sq:id";
      }

      @JvmStatic
      fun `execute$lambda$1$lambda$0`(`this$0`: RegionQueries.IdQuery, `$this$executeQuery`: SqlPreparedStatement): Unit {
         `$this$executeQuery`.bindLong(0, `this$0`.__file_id);
         `$this$executeQuery`.bindLong(1, `this$0`.start_line);
         `$this$executeQuery`.bindLong(2, `this$0`.start_column);
         `$this$executeQuery`.bindLong(3, `this$0`.end_line);
         `$this$executeQuery`.bindLong(4, `this$0`.end_column);
         return Unit.INSTANCE;
      }

      @JvmStatic
      fun `execute$lambda$1`(
         `this$0`: RegionQueries, `this$1`: RegionQueries.IdQuery, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn
      ): QueryResult {
         return RegionQueries.access$getDriver(`this$0`)
            .executeQuery(
               null,
               "SELECT id FROM Region WHERE __file_id = ? AND start_line = ? AND start_column ${if (`this$1`.start_column == null) "IS" else "="} ? AND end_line ${if (`this$1`.end_line
                     == null)
                  "IS"
                  else
                  "="} ? AND end_column ${if (`this$1`.end_column == null) "IS" else "="} ?",
               `$mapper`,
               5,
               RegionQueries.IdQuery::execute$lambda$1$lambda$0
            );
      }
   }
}
