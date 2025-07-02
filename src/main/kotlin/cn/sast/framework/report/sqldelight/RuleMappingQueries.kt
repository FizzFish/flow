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

public class RuleMappingQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun <T : Any> verify_rule_name(mapper: (String, String?, String?) -> T): ExecutableQuery<T> {
      return new RuleMappingQueries.Verify_rule_nameQuery(this, RuleMappingQueries::verify_rule_name$lambda$0);
   }

   public fun verify_rule_name(): ExecutableQuery<RuleMapping> {
      return this.verify_rule_name(RuleMappingQueries::verify_rule_name$lambda$1);
   }

   public fun <T : Any> selectAll(mapper: (String, String?, String?) -> T): Query<T> {
      return QueryKt.Query(
         1383017866,
         new java.lang.String[]{"RuleMapping"},
         this.getDriver(),
         "RuleMapping.sq",
         "selectAll",
         "SELECT RuleMapping.rule_name, RuleMapping.standard_name, RuleMapping.standard_rule\nFROM RuleMapping",
         RuleMappingQueries::selectAll$lambda$2
      );
   }

   public fun selectAll(): Query<RuleMapping> {
      return this.selectAll(RuleMappingQueries::selectAll$lambda$3);
   }

   public fun insert(RuleMapping: RuleMapping) {
      this.getDriver()
         .execute(
            -1057683884,
            "INSERT OR IGNORE INTO RuleMapping (rule_name, standard_name, standard_rule)\nVALUES (?, ?, ?)",
            3,
            RuleMappingQueries::insert$lambda$4
         );
      this.notifyQueries(-1057683884, RuleMappingQueries::insert$lambda$5);
   }

   @JvmStatic
   fun `verify_rule_name$lambda$0`(`$mapper`: Function3, cursor: SqlCursor): Any {
      val var10001: java.lang.String = cursor.getString(0);
      return `$mapper`.invoke(var10001, cursor.getString(1), cursor.getString(2));
   }

   @JvmStatic
   fun `verify_rule_name$lambda$1`(rule_name: java.lang.String, standard_name: java.lang.String, standard_rule: java.lang.String): RuleMapping {
      return new RuleMapping(rule_name, standard_name, standard_rule);
   }

   @JvmStatic
   fun `selectAll$lambda$2`(`$mapper`: Function3, cursor: SqlCursor): Any {
      val var10001: java.lang.String = cursor.getString(0);
      return `$mapper`.invoke(var10001, cursor.getString(1), cursor.getString(2));
   }

   @JvmStatic
   fun `selectAll$lambda$3`(rule_name: java.lang.String, standard_name: java.lang.String, standard_rule: java.lang.String): RuleMapping {
      return new RuleMapping(rule_name, standard_name, standard_rule);
   }

   @JvmStatic
   fun `insert$lambda$4`(`$RuleMapping`: RuleMapping, `$this$execute`: SqlPreparedStatement): Unit {
      `$this$execute`.bindString(0, `$RuleMapping`.getRule_name());
      `$this$execute`.bindString(1, `$RuleMapping`.getStandard_name());
      `$this$execute`.bindString(2, `$RuleMapping`.getStandard_rule());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$5`(emit: Function1): Unit {
      emit.invoke("RuleMapping");
      return Unit.INSTANCE;
   }

   private inner class Verify_rule_nameQuery<T>(mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      init {
         this.this$0 = `this$0`;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(
            this.this$0 as Transacter, false, RuleMappingQueries.Verify_rule_nameQuery::execute$lambda$0, 1, null
         ) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "RuleMapping.sq:verify_rule_name";
      }

      @JvmStatic
      fun `execute$lambda$0`(`this$0`: RuleMappingQueries, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
            RuleMappingQueries.access$getDriver(`this$0`),
            -976712076,
            "SELECT RuleMapping.rule_name, RuleMapping.standard_name, RuleMapping.standard_rule FROM RuleMapping WHERE rule_name NOT IN (SELECT name FROM Rule)",
            `$mapper`,
            0,
            null,
            16,
            null
         );
      }
   }
}
