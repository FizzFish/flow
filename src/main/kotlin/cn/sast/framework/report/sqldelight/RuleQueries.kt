package cn.sast.framework.report.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.QueryKt
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.FunctionN

public class RuleQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun <T : Any> selectAll(
      mapper: (
               String,
               String,
               String,
               String?,
               String?,
               String,
               String?,
               String?,
               String?,
               String?,
               String?,
               String?,
               String?,
               Long?,
               Long?,
               String?,
               String?,
               String,
               String,
               Long?,
               String?,
               String?,
               String?,
               String?,
               String,
               String?,
               String,
               String
            ) -> T
   ): Query<T> {
      return QueryKt.Query(
         177633238,
         new java.lang.String[]{"Rule"},
         this.getDriver(),
         "Rule.sq",
         "selectAll",
         "SELECT Rule.name, Rule.short_description_en, Rule.short_description_zh, Rule.severity, Rule.priority, Rule.language, Rule.precision, Rule.recall, Rule.likelihood, Rule.impact, Rule.technique, Rule.analysis_scope, Rule.performance, Rule.configurable, Rule.implemented, Rule.static_analyzability, Rule.c_allocated_target, Rule.category_en, Rule.category_zh, Rule.rule_sort_number, Rule.chapter_name_1, Rule.chapter_name_2, Rule.chapter_name_3, Rule.chapter_name_4, Rule.description_en, Rule.description_zh, Rule.document_en, Rule.document_zh\nFROM Rule",
         RuleQueries::selectAll$lambda$0
      );
   }

   public fun selectAll(): Query<Rule> {
      return this.selectAll(<unrepresentable>.INSTANCE);
   }

   public fun insert(Rule: Rule) {
      this.getDriver()
         .execute(
            -1201750136,
            "INSERT OR IGNORE INTO Rule (name, short_description_en, short_description_zh, severity, priority, language, precision, recall, likelihood, impact, technique, analysis_scope, performance, configurable, implemented, static_analyzability, c_allocated_target, category_en, category_zh, rule_sort_number, chapter_name_1, chapter_name_2, chapter_name_3, chapter_name_4, description_en, description_zh, document_en, document_zh) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            28,
            RuleQueries::insert$lambda$1
         );
      this.notifyQueries(-1201750136, RuleQueries::insert$lambda$2);
   }

   @JvmStatic
   fun `selectAll$lambda$0`(`$mapper`: FunctionN, cursor: SqlCursor): Any {
      val var2: Array<Any> = new Object[28];
      var var10003: java.lang.String = cursor.getString(0);
      var2[0] = var10003;
      var10003 = cursor.getString(1);
      var2[1] = var10003;
      var10003 = cursor.getString(2);
      var2[2] = var10003;
      var2[3] = cursor.getString(3);
      var2[4] = cursor.getString(4);
      var10003 = cursor.getString(5);
      var2[5] = var10003;
      var2[6] = cursor.getString(6);
      var2[7] = cursor.getString(7);
      var2[8] = cursor.getString(8);
      var2[9] = cursor.getString(9);
      var2[10] = cursor.getString(10);
      var2[11] = cursor.getString(11);
      var2[12] = cursor.getString(12);
      var2[13] = cursor.getLong(13);
      var2[14] = cursor.getLong(14);
      var2[15] = cursor.getString(15);
      var2[16] = cursor.getString(16);
      var10003 = cursor.getString(17);
      var2[17] = var10003;
      var10003 = cursor.getString(18);
      var2[18] = var10003;
      var2[19] = cursor.getLong(19);
      var2[20] = cursor.getString(20);
      var2[21] = cursor.getString(21);
      var2[22] = cursor.getString(22);
      var2[23] = cursor.getString(23);
      var10003 = cursor.getString(24);
      var2[24] = var10003;
      var2[25] = cursor.getString(25);
      var10003 = cursor.getString(26);
      var2[26] = var10003;
      var10003 = cursor.getString(27);
      var2[27] = var10003;
      return `$mapper`.invoke(var2);
   }

   @JvmStatic
   fun `insert$lambda$1`(`$Rule`: Rule, `$this$execute`: SqlPreparedStatement): Unit {
      `$this$execute`.bindString(0, `$Rule`.getName());
      `$this$execute`.bindString(1, `$Rule`.getShort_description_en());
      `$this$execute`.bindString(2, `$Rule`.getShort_description_zh());
      `$this$execute`.bindString(3, `$Rule`.getSeverity());
      `$this$execute`.bindString(4, `$Rule`.getPriority());
      `$this$execute`.bindString(5, `$Rule`.getLanguage());
      `$this$execute`.bindString(6, `$Rule`.getPrecision());
      `$this$execute`.bindString(7, `$Rule`.getRecall());
      `$this$execute`.bindString(8, `$Rule`.getLikelihood());
      `$this$execute`.bindString(9, `$Rule`.getImpact());
      `$this$execute`.bindString(10, `$Rule`.getTechnique());
      `$this$execute`.bindString(11, `$Rule`.getAnalysis_scope());
      `$this$execute`.bindString(12, `$Rule`.getPerformance());
      `$this$execute`.bindLong(13, `$Rule`.getConfigurable());
      `$this$execute`.bindLong(14, `$Rule`.getImplemented());
      `$this$execute`.bindString(15, `$Rule`.getStatic_analyzability());
      `$this$execute`.bindString(16, `$Rule`.getC_allocated_target());
      `$this$execute`.bindString(17, `$Rule`.getCategory_en());
      `$this$execute`.bindString(18, `$Rule`.getCategory_zh());
      `$this$execute`.bindLong(19, `$Rule`.getRule_sort_number());
      `$this$execute`.bindString(20, `$Rule`.getChapter_name_1());
      `$this$execute`.bindString(21, `$Rule`.getChapter_name_2());
      `$this$execute`.bindString(22, `$Rule`.getChapter_name_3());
      `$this$execute`.bindString(23, `$Rule`.getChapter_name_4());
      `$this$execute`.bindString(24, `$Rule`.getDescription_en());
      `$this$execute`.bindString(25, `$Rule`.getDescription_zh());
      `$this$execute`.bindString(26, `$Rule`.getDocument_en());
      `$this$execute`.bindString(27, `$Rule`.getDocument_zh());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$2`(emit: Function1): Unit {
      emit.invoke("Rule");
      return Unit.INSTANCE;
   }
}
