package cn.sast.framework.report.sqldelight

public data class AnalyzerStatistics(
    val name: String,
    val corax_probe_version: String,
    val analyzer_version: String,
    val analysis_begin_date: String,
    val analysis_begin_timestamp: Long,
    val analysis_escape_seconds: Long,
    val analysis_escape_time: String,
    val analysis_end_date: String,
    val analysis_end_timestamp: Long,
    val file_count: Long,
    val line_count: Long,
    val code_coverage_covered: Long?,
    val code_coverage_missed: Long?,
    val num_of_report_dir: Long?,
    val source_paths: String,
    val os_name: String,
    val command_json: String,
    val working_directory: String,
    val output_path: String,
    val project_root: String,
    val log_file: String,
    val enable_rules: String,
    val disable_rules: String,
    val failed_sources: String,
    val failed_sources_num: Long,
    val successful_sources: String,
    val successful_sources_num: Long,
    val skipped_sources: String,
    val skipped_sources_num: Long
) {
    public operator fun component1(): String = name
    public operator fun component2(): String = corax_probe_version
    public operator fun component3(): String = analyzer_version
    public operator fun component4(): String = analysis_begin_date
    public operator fun component5(): Long = analysis_begin_timestamp
    public operator fun component6(): Long = analysis_escape_seconds
    public operator fun component7(): String = analysis_escape_time
    public operator fun component8(): String = analysis_end_date
    public operator fun component9(): Long = analysis_end_timestamp
    public operator fun component10(): Long = file_count
    public operator fun component11(): Long = line_count
    public operator fun component12(): Long? = code_coverage_covered
    public operator fun component13(): Long? = code_coverage_missed
    public operator fun component14(): Long? = num_of_report_dir
    public operator fun component15(): String = source_paths
    public operator fun component16(): String = os_name
    public operator fun component17(): String = command_json
    public operator fun component18(): String = working_directory
    public operator fun component19(): String = output_path
    public operator fun component20(): String = project_root
    public operator fun component21(): String = log_file
    public operator fun component22(): String = enable_rules
    public operator fun component23(): String = disable_rules
    public operator fun component24(): String = failed_sources
    public operator fun component25(): Long = failed_sources_num
    public operator fun component26(): String = successful_sources
    public operator fun component27(): Long = successful_sources_num
    public operator fun component28(): String = skipped_sources
    public operator fun component29(): Long = skipped_sources_num

    public fun copy(
        name: String = this.name,
        corax_probe_version: String = this.corax_probe_version,
        analyzer_version: String = this.analyzer_version,
        analysis_begin_date: String = this.analysis_begin_date,
        analysis_begin_timestamp: Long = this.analysis_begin_timestamp,
        analysis_escape_seconds: Long = this.analysis_escape_seconds,
        analysis_escape_time: String = this.analysis_escape_time,
        analysis_end_date: String = this.analysis_end_date,
        analysis_end_timestamp: Long = this.analysis_end_timestamp,
        file_count: Long = this.file_count,
        line_count: Long = this.line_count,
        code_coverage_covered: Long? = this.code_coverage_covered,
        code_coverage_missed: Long? = this.code_coverage_missed,
        num_of_report_dir: Long? = this.num_of_report_dir,
        source_paths: String = this.source_paths,
        os_name: String = this.os_name,
        command_json: String = this.command_json,
        working_directory: String = this.working_directory,
        output_path: String = this.output_path,
        project_root: String = this.project_root,
        log_file: String = this.log_file,
        enable_rules: String = this.enable_rules,
        disable_rules: String = this.disable_rules,
        failed_sources: String = this.failed_sources,
        failed_sources_num: Long = this.failed_sources_num,
        successful_sources: String = this.successful_sources,
        successful_sources_num: Long = this.successful_sources_num,
        skipped_sources: String = this.skipped_sources,
        skipped_sources_num: Long = this.skipped_sources_num
    ): AnalyzerStatistics = AnalyzerStatistics(
        name,
        corax_probe_version,
        analyzer_version,
        analysis_begin_date,
        analysis_begin_timestamp,
        analysis_escape_seconds,
        analysis_escape_time,
        analysis_end_date,
        analysis_end_timestamp,
        file_count,
        line_count,
        code_coverage_covered,
        code_coverage_missed,
        num_of_report_dir,
        source_paths,
        os_name,
        command_json,
        working_directory,
        output_path,
        project_root,
        log_file,
        enable_rules,
        disable_rules,
        failed_sources,
        failed_sources_num,
        successful_sources,
        successful_sources_num,
        skipped_sources,
        skipped_sources_num
    )

    override fun toString(): String =
        "AnalyzerStatistics(name=$name, corax_probe_version=$corax_probe_version, analyzer_version=$analyzer_version, analysis_begin_date=$analysis_begin_date, analysis_begin_timestamp=$analysis_begin_timestamp, analysis_escape_seconds=$analysis_escape_seconds, analysis_escape_time=$analysis_escape_time, analysis_end_date=$analysis_end_date, analysis_end_timestamp=$analysis_end_timestamp, file_count=$file_count, line_count=$line_count, code_coverage_covered=$code_coverage_covered, code_coverage_missed=$code_coverage_missed, num_of_report_dir=$num_of_report_dir, source_paths=$source_paths, os_name=$os_name, command_json=$command_json, working_directory=$working_directory, output_path=$output_path, project_root=$project_root, log_file=$log_file, enable_rules=$enable_rules, disable_rules=$disable_rules, failed_sources=$failed_sources, failed_sources_num=$failed_sources_num, successful_sources=$successful_sources, successful_sources_num=$successful_sources_num, skipped_sources=$skipped_sources, skipped_sources_num=$skipped_sources_num)"

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + corax_probe_version.hashCode()
        result = 31 * result + analyzer_version.hashCode()
        result = 31 * result + analysis_begin_date.hashCode()
        result = 31 * result + analysis_begin_timestamp.hashCode()
        result = 31 * result + analysis_escape_seconds.hashCode()
        result = 31 * result + analysis_escape_time.hashCode()
        result = 31 * result + analysis_end_date.hashCode()
        result = 31 * result + analysis_end_timestamp.hashCode()
        result = 31 * result + file_count.hashCode()
        result = 31 * result + line_count.hashCode()
        result = 31 * result + (code_coverage_covered?.hashCode() ?: 0)
        result = 31 * result + (code_coverage_missed?.hashCode() ?: 0)
        result = 31 * result + (num_of_report_dir?.hashCode() ?: 0)
        result = 31 * result + source_paths.hashCode()
        result = 31 * result + os_name.hashCode()
        result = 31 * result + command_json.hashCode()
        result = 31 * result + working_directory.hashCode()
        result = 31 * result + output_path.hashCode()
        result = 31 * result + project_root.hashCode()
        result = 31 * result + log_file.hashCode()
        result = 31 * result + enable_rules.hashCode()
        result = 31 * result + disable_rules.hashCode()
        result = 31 * result + failed_sources.hashCode()
        result = 31 * result + failed_sources_num.hashCode()
        result = 31 * result + successful_sources.hashCode()
        result = 31 * result + successful_sources_num.hashCode()
        result = 31 * result + skipped_sources.hashCode()
        result = 31 * result + skipped_sources_num.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnalyzerStatistics) return false

        if (name != other.name) return false
        if (corax_probe_version != other.corax_probe_version) return false
        if (analyzer_version != other.analyzer_version) return false
        if (analysis_begin_date != other.analysis_begin_date) return false
        if (analysis_begin_timestamp != other.analysis_begin_timestamp) return false
        if (analysis_escape_seconds != other.analysis_escape_seconds) return false
        if (analysis_escape_time != other.analysis_escape_time) return false
        if (analysis_end_date != other.analysis_end_date) return false
        if (analysis_end_timestamp != other.analysis_end_timestamp) return false
        if (file_count != other.file_count) return false
        if (line_count != other.line_count) return false
        if (code_coverage_covered != other.code_coverage_covered) return false
        if (code_coverage_missed != other.code_coverage_missed) return false
        if (num_of_report_dir != other.num_of_report_dir) return false
        if (source_paths != other.source_paths) return false
        if (os_name != other.os_name) return false
        if (command_json != other.command_json) return false
        if (working_directory != other.working_directory) return false
        if (output_path != other.output_path) return false
        if (project_root != other.project_root) return false
        if (log_file != other.log_file) return false
        if (enable_rules != other.enable_rules) return false
        if (disable_rules != other.disable_rules) return false
        if (failed_sources != other.failed_sources) return false
        if (failed_sources_num != other.failed_sources_num) return false
        if (successful_sources != other.successful_sources) return false
        if (successful_sources_num != other.successful_sources_num) return false
        if (skipped_sources != other.skipped_sources) return false
        if (skipped_sources_num != other.skipped_sources_num) return false

        return true
    }
}