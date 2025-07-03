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
            arrayOf("AnalyzerStatistics"),
            this.getDriver(),
            "AnalyzerStatistics.sq",
            "selectAll",
            "SELECT AnalyzerStatistics.name, AnalyzerStatistics.corax_probe_version, AnalyzerStatistics.analyzer_version, AnalyzerStatistics.analysis_begin_date, AnalyzerStatistics.analysis_begin_timestamp, AnalyzerStatistics.analysis_escape_seconds, AnalyzerStatistics.analysis_escape_time, AnalyzerStatistics.analysis_end_date, AnalyzerStatistics.analysis_end_timestamp, AnalyzerStatistics.file_count, AnalyzerStatistics.line_count, AnalyzerStatistics.code_coverage_covered, AnalyzerStatistics.code_coverage_missed, AnalyzerStatistics.num_of_report_dir, AnalyzerStatistics.source_paths, AnalyzerStatistics.os_name, AnalyzerStatistics.command_json, AnalyzerStatistics.working_directory, AnalyzerStatistics.output_path, AnalyzerStatistics.project_root, AnalyzerStatistics.log_file, AnalyzerStatistics.enable_rules, AnalyzerStatistics.disable_rules, AnalyzerStatistics.failed_sources, AnalyzerStatistics.failed_sources_num, AnalyzerStatistics.successful_sources, AnalyzerStatistics.successful_sources_num, AnalyzerStatistics.skipped_sources, AnalyzerStatistics.skipped_sources_num\nFROM AnalyzerStatistics",
            AnalyzerStatisticsQueries::selectAll$lambda$0
        )
    }

    public fun selectAll(): Query<AnalyzerStatistics> {
        TODO("FIXME — unrepresentable instance")
    }

    public fun insert(AnalyzerStatistics: AnalyzerStatistics) {
        this.getDriver()
            .execute(
                402637301,
                "INSERT OR IGNORE INTO AnalyzerStatistics (name, corax_probe_version, analyzer_version, analysis_begin_date, analysis_begin_timestamp, analysis_escape_seconds, analysis_escape_time, analysis_end_date, analysis_end_timestamp, file_count, line_count, code_coverage_covered, code_coverage_missed, num_of_report_dir, source_paths, os_name, command_json, working_directory, output_path, project_root, log_file, enable_rules, disable_rules, failed_sources, failed_sources_num, successful_sources, successful_sources_num, skipped_sources, skipped_sources_num)\nVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                29,
                AnalyzerStatisticsQueries::insert$lambda$1
            )
        this.notifyQueries(402637301, AnalyzerStatisticsQueries::insert$lambda$2)
    }

    @JvmStatic
    private fun selectAll$lambda$0(mapper: FunctionN, cursor: SqlCursor): Any {
        val args = arrayOfNulls<Any?>(29)
        args[0] = cursor.getString(0)
        args[1] = cursor.getString(1)
        args[2] = cursor.getString(2)
        args[3] = cursor.getString(3)
        args[4] = cursor.getLong(4)
        args[5] = cursor.getLong(5)
        args[6] = cursor.getString(6)
        args[7] = cursor.getString(7)
        args[8] = cursor.getLong(8)
        args[9] = cursor.getLong(9)
        args[10] = cursor.getLong(10)
        args[11] = cursor.getLong(11)
        args[12] = cursor.getLong(12)
        args[13] = cursor.getLong(13)
        args[14] = cursor.getString(14)
        args[15] = cursor.getString(15)
        args[16] = cursor.getString(16)
        args[17] = cursor.getString(17)
        args[18] = cursor.getString(18)
        args[19] = cursor.getString(19)
        args[20] = cursor.getString(20)
        args[21] = cursor.getString(21)
        args[22] = cursor.getString(22)
        args[23] = cursor.getString(23)
        args[24] = cursor.getLong(24)
        args[25] = cursor.getString(25)
        args[26] = cursor.getLong(26)
        args[27] = cursor.getString(27)
        args[28] = cursor.getLong(28)
        return mapper.invoke(*args)
    }

    @JvmStatic
    private fun insert$lambda$1(AnalyzerStatistics: AnalyzerStatistics, statement: SqlPreparedStatement) {
        statement.bindString(0, AnalyzerStatistics.getName())
        statement.bindString(1, AnalyzerStatistics.getCorax_probe_version())
        statement.bindString(2, AnalyzerStatistics.getAnalyzer_version())
        statement.bindString(3, AnalyzerStatistics.getAnalysis_begin_date())
        statement.bindLong(4, AnalyzerStatistics.getAnalysis_begin_timestamp())
        statement.bindLong(5, AnalyzerStatistics.getAnalysis_escape_seconds())
        statement.bindString(6, AnalyzerStatistics.getAnalysis_escape_time())
        statement.bindString(7, AnalyzerStatistics.getAnalysis_end_date())
        statement.bindLong(8, AnalyzerStatistics.getAnalysis_end_timestamp())
        statement.bindLong(9, AnalyzerStatistics.getFile_count())
        statement.bindLong(10, AnalyzerStatistics.getLine_count())
        statement.bindLong(11, AnalyzerStatistics.getCode_coverage_covered())
        statement.bindLong(12, AnalyzerStatistics.getCode_coverage_missed())
        statement.bindLong(13, AnalyzerStatistics.getNum_of_report_dir())
        statement.bindString(14, AnalyzerStatistics.getSource_paths())
        statement.bindString(15, AnalyzerStatistics.getOs_name())
        statement.bindString(16, AnalyzerStatistics.getCommand_json())
        statement.bindString(17, AnalyzerStatistics.getWorking_directory())
        statement.bindString(18, AnalyzerStatistics.getOutput_path())
        statement.bindString(19, AnalyzerStatistics.getProject_root())
        statement.bindString(20, AnalyzerStatistics.getLog_file())
        statement.bindString(21, AnalyzerStatistics.getEnable_rules())
        statement.bindString(22, AnalyzerStatistics.getDisable_rules())
        statement.bindString(23, AnalyzerStatistics.getFailed_sources())
        statement.bindLong(24, AnalyzerStatistics.getFailed_sources_num())
        statement.bindString(25, AnalyzerStatistics.getSuccessful_sources())
        statement.bindLong(26, AnalyzerStatistics.getSuccessful_sources_num())
        statement.bindString(27, AnalyzerStatistics.getSkipped_sources())
        statement.bindLong(28, AnalyzerStatistics.getSkipped_sources_num())
    }

    @JvmStatic
    private fun insert$lambda$2(emit: Function1<String, Unit>) {
        emit.invoke("AnalyzerStatistics")
    }
}