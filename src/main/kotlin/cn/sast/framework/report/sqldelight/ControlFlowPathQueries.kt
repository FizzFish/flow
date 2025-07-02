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

public class ControlFlowPathQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun <T : Any> verify_control_flow(mapper: (Long, Long, Long) -> T): ExecutableQuery<T> {
      return new ControlFlowPathQueries.Verify_control_flowQuery(this, ControlFlowPathQueries::verify_control_flow$lambda$0);
   }

   public fun verify_control_flow(): ExecutableQuery<ControlFlowPath> {
      return this.verify_control_flow(ControlFlowPathQueries::verify_control_flow$lambda$1);
   }

   public fun <T : Any> selectAll(mapper: (Long, Long, Long) -> T): Query<T> {
      return QueryKt.Query(
         -180228340,
         new java.lang.String[]{"ControlFlowPath"},
         this.getDriver(),
         "ControlFlowPath.sq",
         "selectAll",
         "SELECT ControlFlowPath.__control_flow_array_hash_id, ControlFlowPath.control_flow_sequence, ControlFlowPath.__control_flow_id\nFROM ControlFlowPath",
         ControlFlowPathQueries::selectAll$lambda$2
      );
   }

   public fun selectAll(): Query<ControlFlowPath> {
      return this.selectAll(ControlFlowPathQueries::selectAll$lambda$3);
   }

   public fun insert(ControlFlowPath: ControlFlowPath) {
      this.getDriver()
         .execute(
            -947157998,
            "INSERT OR IGNORE INTO ControlFlowPath (__control_flow_array_hash_id, control_flow_sequence, __control_flow_id)\nVALUES (?, ?, ?)",
            3,
            ControlFlowPathQueries::insert$lambda$4
         );
      this.notifyQueries(-947157998, ControlFlowPathQueries::insert$lambda$5);
   }

   @JvmStatic
   fun `verify_control_flow$lambda$0`(`$mapper`: Function3, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      val var10003: java.lang.Long = cursor.getLong(2);
      return `$mapper`.invoke(var10001, var10002, var10003);
   }

   @JvmStatic
   fun `verify_control_flow$lambda$1`(__control_flow_array_hash_id: Long, control_flow_sequence: Long, __control_flow_id: Long): ControlFlowPath {
      return new ControlFlowPath(__control_flow_array_hash_id, control_flow_sequence, __control_flow_id);
   }

   @JvmStatic
   fun `selectAll$lambda$2`(`$mapper`: Function3, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      val var10003: java.lang.Long = cursor.getLong(2);
      return `$mapper`.invoke(var10001, var10002, var10003);
   }

   @JvmStatic
   fun `selectAll$lambda$3`(__control_flow_array_hash_id: Long, control_flow_sequence: Long, __control_flow_id: Long): ControlFlowPath {
      return new ControlFlowPath(__control_flow_array_hash_id, control_flow_sequence, __control_flow_id);
   }

   @JvmStatic
   fun `insert$lambda$4`(`$ControlFlowPath`: ControlFlowPath, `$this$execute`: SqlPreparedStatement): Unit {
      `$this$execute`.bindLong(0, `$ControlFlowPath`.get__control_flow_array_hash_id());
      `$this$execute`.bindLong(1, `$ControlFlowPath`.getControl_flow_sequence());
      `$this$execute`.bindLong(2, `$ControlFlowPath`.get__control_flow_id());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$5`(emit: Function1): Unit {
      emit.invoke("ControlFlowPath");
      return Unit.INSTANCE;
   }

   private inner class Verify_control_flowQuery<T>(mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      init {
         this.this$0 = `this$0`;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(
            this.this$0 as Transacter, false, ControlFlowPathQueries.Verify_control_flowQuery::execute$lambda$0, 1, null
         ) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "ControlFlowPath.sq:verify_control_flow";
      }

      @JvmStatic
      fun `execute$lambda$0`(`this$0`: ControlFlowPathQueries, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
            ControlFlowPathQueries.access$getDriver(`this$0`),
            1682752654,
            "SELECT ControlFlowPath.__control_flow_array_hash_id, ControlFlowPath.control_flow_sequence, ControlFlowPath.__control_flow_id FROM ControlFlowPath WHERE __control_flow_id NOT IN (SELECT id FROM ControlFlow)",
            `$mapper`,
            0,
            null,
            16,
            null
         );
      }
   }
}
