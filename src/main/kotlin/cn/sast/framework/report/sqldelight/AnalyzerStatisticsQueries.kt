package cn.sast.framework.report.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.QueryKt
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.FunctionN

public class AnalyzerStatisticsQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun <T : Any> selectAll(
      mapper: (
               String,
               String,
               String,
               String,
               Long,
               Long,
               String,
               String,
               Long,
               Long,
               Long,
               Long?,
               Long?,
               Long?,
               String,
               String,
               String,
               String,
               String,
               String,
               String,
               String,
               String,
               String,
               Long,
               String,
               Long,
               String,
               Long
            ) -> T
   ): Query<T> {
      return QueryKt.Query(
         2087699017,
         new java.lang.String[]{"AnalyzerStatistics"},
         this.getDriver(),
         "AnalyzerStatistics.sq",
         "selectAll",
         "SELECT AnalyzerStatistics.name, AnalyzerStatistics.corax_probe_version, AnalyzerStatistics.analyzer_version, AnalyzerStatistics.analysis_begin_date, AnalyzerStatistics.analysis_begin_timestamp, AnalyzerStatistics.analysis_escape_seconds, AnalyzerStatistics.analysis_escape_time, AnalyzerStatistics.analysis_end_date, AnalyzerStatistics.analysis_end_timestamp, AnalyzerStatistics.file_count, AnalyzerStatistics.line_count, AnalyzerStatistics.code_coverage_covered, AnalyzerStatistics.code_coverage_missed, AnalyzerStatistics.num_of_report_dir, AnalyzerStatistics.source_paths, AnalyzerStatistics.os_name, AnalyzerStatistics.command_json, AnalyzerStatistics.working_directory, AnalyzerStatistics.output_path, AnalyzerStatistics.project_root, AnalyzerStatistics.log_file, AnalyzerStatistics.enable_rules, AnalyzerStatistics.disable_rules, AnalyzerStatistics.failed_sources, AnalyzerStatistics.failed_sources_num, AnalyzerStatistics.successful_sources, AnalyzerStatistics.successful_sources_num, AnalyzerStatistics.skipped_sources, AnalyzerStatistics.skipped_sources_num\nFROM AnalyzerStatistics",
         AnalyzerStatisticsQueries::selectAll$lambda$0
      );
   }

   public fun selectAll(): Query<AnalyzerStatistics> {
      return this.selectAll(<unrepresentable>.INSTANCE);
   }

   public fun insert(AnalyzerStatistics: AnalyzerStatistics) {
      this.getDriver()
         .execute(
            402637301,
            "INSERT OR IGNORE INTO AnalyzerStatistics (name, corax_probe_version, analyzer_version, analysis_begin_date, analysis_begin_timestamp, analysis_escape_seconds, analysis_escape_time, analysis_end_date, analysis_end_timestamp, file_count, line_count, code_coverage_covered, code_coverage_missed, num_of_report_dir, source_paths, os_name, command_json, working_directory, output_path, project_root, log_file, enable_rules, disable_rules, failed_sources, failed_sources_num, successful_sources, successful_sources_num, skipped_sources, skipped_sources_num)\nVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            29,
            AnalyzerStatisticsQueries::insert$lambda$1
         );
      this.notifyQueries(402637301, AnalyzerStatisticsQueries::insert$lambda$2);
   }

   @JvmStatic
   fun `selectAll$lambda$0`(`$mapper`: FunctionN, cursor: SqlCursor): Any {
      val var2: Array<Any> = new Object[29];
      var var10003: java.lang.String = cursor.getString(0);
      var2[0] = var10003;
      var10003 = cursor.getString(1);
      var2[1] = var10003;
      var10003 = cursor.getString(2);
      var2[2] = var10003;
      var10003 = cursor.getString(3);
      var2[3] = var10003;
      val var6: java.lang.Long = cursor.getLong(4);
      var2[4] = var6;
      val var7: java.lang.Long = cursor.getLong(5);
      var2[5] = var7;
      var10003 = cursor.getString(6);
      var2[6] = var10003;
      var10003 = cursor.getString(7);
      var2[7] = var10003;
      val var10: java.lang.Long = cursor.getLong(8);
      var2[8] = var10;
      val var11: java.lang.Long = cursor.getLong(9);
      var2[9] = var11;
      val var12: java.lang.Long = cursor.getLong(10);
      var2[10] = var12;
      var2[11] = cursor.getLong(11);
      var2[12] = cursor.getLong(12);
      var2[13] = cursor.getLong(13);
      var10003 = cursor.getString(14);
      var2[14] = var10003;
      var10003 = cursor.getString(15);
      var2[15] = var10003;
      var10003 = cursor.getString(16);
      var2[16] = var10003;
      var10003 = cursor.getString(17);
      var2[17] = var10003;
      var10003 = cursor.getString(18);
      var2[18] = var10003;
      var10003 = cursor.getString(19);
      var2[19] = var10003;
      var10003 = cursor.getString(20);
      var2[20] = var10003;
      var10003 = cursor.getString(21);
      var2[21] = var10003;
      var10003 = cursor.getString(22);
      var2[22] = var10003;
      var10003 = cursor.getString(23);
      var2[23] = var10003;
      val var23: java.lang.Long = cursor.getLong(24);
      var2[24] = var23;
      var10003 = cursor.getString(25);
      var2[25] = var10003;
      val var25: java.lang.Long = cursor.getLong(26);
      var2[26] = var25;
      var10003 = cursor.getString(27);
      var2[27] = var10003;
      val var27: java.lang.Long = cursor.getLong(28);
      var2[28] = var27;
      return `$mapper`.invoke(var2);
   }

   @JvmStatic
   fun `insert$lambda$1`(`$AnalyzerStatistics`: AnalyzerStatistics, `$this$execute`: SqlPreparedStatement): Unit {
      `$this$execute`.bindString(0, `$AnalyzerStatistics`.getName());
      `$this$execute`.bindString(1, `$AnalyzerStatistics`.getCorax_probe_version());
      `$this$execute`.bindString(2, `$AnalyzerStatistics`.getAnalyzer_version());
      `$this$execute`.bindString(3, `$AnalyzerStatistics`.getAnalysis_begin_date());
      `$this$execute`.bindLong(4, `$AnalyzerStatistics`.getAnalysis_begin_timestamp());
      `$this$execute`.bindLong(5, `$AnalyzerStatistics`.getAnalysis_escape_seconds());
      `$this$execute`.bindString(6, `$AnalyzerStatistics`.getAnalysis_escape_time());
      `$this$execute`.bindString(7, `$AnalyzerStatistics`.getAnalysis_end_date());
      `$this$execute`.bindLong(8, `$AnalyzerStatistics`.getAnalysis_end_timestamp());
      `$this$execute`.bindLong(9, `$AnalyzerStatistics`.getFile_count());
      `$this$execute`.bindLong(10, `$AnalyzerStatistics`.getLine_count());
      `$this$execute`.bindLong(11, `$AnalyzerStatistics`.getCode_coverage_covered());
      `$this$execute`.bindLong(12, `$AnalyzerStatistics`.getCode_coverage_missed());
      `$this$execute`.bindLong(13, `$AnalyzerStatistics`.getNum_of_report_dir());
      `$this$execute`.bindString(14, `$AnalyzerStatistics`.getSource_paths());
      `$this$execute`.bindString(15, `$AnalyzerStatistics`.getOs_name());
      `$this$execute`.bindString(16, `$AnalyzerStatistics`.getCommand_json());
      `$this$execute`.bindString(17, `$AnalyzerStatistics`.getWorking_directory());
      `$this$execute`.bindString(18, `$AnalyzerStatistics`.getOutput_path());
      `$this$execute`.bindString(19, `$AnalyzerStatistics`.getProject_root());
      `$this$execute`.bindString(20, `$AnalyzerStatistics`.getLog_file());
      `$this$execute`.bindString(21, `$AnalyzerStatistics`.getEnable_rules());
      `$this$execute`.bindString(22, `$AnalyzerStatistics`.getDisable_rules());
      `$this$execute`.bindString(23, `$AnalyzerStatistics`.getFailed_sources());
      `$this$execute`.bindLong(24, `$AnalyzerStatistics`.getFailed_sources_num());
      `$this$execute`.bindString(25, `$AnalyzerStatistics`.getSuccessful_sources());
      `$this$execute`.bindLong(26, `$AnalyzerStatistics`.getSuccessful_sources_num());
      `$this$execute`.bindString(27, `$AnalyzerStatistics`.getSkipped_sources());
      `$this$execute`.bindLong(28, `$AnalyzerStatistics`.getSkipped_sources_num());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$2`(emit: Function1): Unit {
      emit.invoke("AnalyzerStatistics");
      return Unit.INSTANCE;
   }
}
