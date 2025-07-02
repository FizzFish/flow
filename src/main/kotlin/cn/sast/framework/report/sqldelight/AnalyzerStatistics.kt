package cn.sast.framework.report.sqldelight

public data class AnalyzerStatistics(name: String,
   corax_probe_version: String,
   analyzer_version: String,
   analysis_begin_date: String,
   analysis_begin_timestamp: Long,
   analysis_escape_seconds: Long,
   analysis_escape_time: String,
   analysis_end_date: String,
   analysis_end_timestamp: Long,
   file_count: Long,
   line_count: Long,
   code_coverage_covered: Long?,
   code_coverage_missed: Long?,
   num_of_report_dir: Long?,
   source_paths: String,
   os_name: String,
   command_json: String,
   working_directory: String,
   output_path: String,
   project_root: String,
   log_file: String,
   enable_rules: String,
   disable_rules: String,
   failed_sources: String,
   failed_sources_num: Long,
   successful_sources: String,
   successful_sources_num: Long,
   skipped_sources: String,
   skipped_sources_num: Long
) {
   public final val name: String
   public final val corax_probe_version: String
   public final val analyzer_version: String
   public final val analysis_begin_date: String
   public final val analysis_begin_timestamp: Long
   public final val analysis_escape_seconds: Long
   public final val analysis_escape_time: String
   public final val analysis_end_date: String
   public final val analysis_end_timestamp: Long
   public final val file_count: Long
   public final val line_count: Long
   public final val code_coverage_covered: Long?
   public final val code_coverage_missed: Long?
   public final val num_of_report_dir: Long?
   public final val source_paths: String
   public final val os_name: String
   public final val command_json: String
   public final val working_directory: String
   public final val output_path: String
   public final val project_root: String
   public final val log_file: String
   public final val enable_rules: String
   public final val disable_rules: String
   public final val failed_sources: String
   public final val failed_sources_num: Long
   public final val successful_sources: String
   public final val successful_sources_num: Long
   public final val skipped_sources: String
   public final val skipped_sources_num: Long

   init {
      this.name = name;
      this.corax_probe_version = corax_probe_version;
      this.analyzer_version = analyzer_version;
      this.analysis_begin_date = analysis_begin_date;
      this.analysis_begin_timestamp = analysis_begin_timestamp;
      this.analysis_escape_seconds = analysis_escape_seconds;
      this.analysis_escape_time = analysis_escape_time;
      this.analysis_end_date = analysis_end_date;
      this.analysis_end_timestamp = analysis_end_timestamp;
      this.file_count = file_count;
      this.line_count = line_count;
      this.code_coverage_covered = code_coverage_covered;
      this.code_coverage_missed = code_coverage_missed;
      this.num_of_report_dir = num_of_report_dir;
      this.source_paths = source_paths;
      this.os_name = os_name;
      this.command_json = command_json;
      this.working_directory = working_directory;
      this.output_path = output_path;
      this.project_root = project_root;
      this.log_file = log_file;
      this.enable_rules = enable_rules;
      this.disable_rules = disable_rules;
      this.failed_sources = failed_sources;
      this.failed_sources_num = failed_sources_num;
      this.successful_sources = successful_sources;
      this.successful_sources_num = successful_sources_num;
      this.skipped_sources = skipped_sources;
      this.skipped_sources_num = skipped_sources_num;
   }

   public operator fun component1(): String {
      return this.name;
   }

   public operator fun component2(): String {
      return this.corax_probe_version;
   }

   public operator fun component3(): String {
      return this.analyzer_version;
   }

   public operator fun component4(): String {
      return this.analysis_begin_date;
   }

   public operator fun component5(): Long {
      return this.analysis_begin_timestamp;
   }

   public operator fun component6(): Long {
      return this.analysis_escape_seconds;
   }

   public operator fun component7(): String {
      return this.analysis_escape_time;
   }

   public operator fun component8(): String {
      return this.analysis_end_date;
   }

   public operator fun component9(): Long {
      return this.analysis_end_timestamp;
   }

   public operator fun component10(): Long {
      return this.file_count;
   }

   public operator fun component11(): Long {
      return this.line_count;
   }

   public operator fun component12(): Long? {
      return this.code_coverage_covered;
   }

   public operator fun component13(): Long? {
      return this.code_coverage_missed;
   }

   public operator fun component14(): Long? {
      return this.num_of_report_dir;
   }

   public operator fun component15(): String {
      return this.source_paths;
   }

   public operator fun component16(): String {
      return this.os_name;
   }

   public operator fun component17(): String {
      return this.command_json;
   }

   public operator fun component18(): String {
      return this.working_directory;
   }

   public operator fun component19(): String {
      return this.output_path;
   }

   public operator fun component20(): String {
      return this.project_root;
   }

   public operator fun component21(): String {
      return this.log_file;
   }

   public operator fun component22(): String {
      return this.enable_rules;
   }

   public operator fun component23(): String {
      return this.disable_rules;
   }

   public operator fun component24(): String {
      return this.failed_sources;
   }

   public operator fun component25(): Long {
      return this.failed_sources_num;
   }

   public operator fun component26(): String {
      return this.successful_sources;
   }

   public operator fun component27(): Long {
      return this.successful_sources_num;
   }

   public operator fun component28(): String {
      return this.skipped_sources;
   }

   public operator fun component29(): Long {
      return this.skipped_sources_num;
   }

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
   ): AnalyzerStatistics {
      return new AnalyzerStatistics(
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
      );
   }

   public override fun toString(): String {
      return "AnalyzerStatistics(name=${this.name}, corax_probe_version=${this.corax_probe_version}, analyzer_version=${this.analyzer_version}, analysis_begin_date=${this.analysis_begin_date}, analysis_begin_timestamp=${this.analysis_begin_timestamp}, analysis_escape_seconds=${this.analysis_escape_seconds}, analysis_escape_time=${this.analysis_escape_time}, analysis_end_date=${this.analysis_end_date}, analysis_end_timestamp=${this.analysis_end_timestamp}, file_count=${this.file_count}, line_count=${this.line_count}, code_coverage_covered=${this.code_coverage_covered}, code_coverage_missed=${this.code_coverage_missed}, num_of_report_dir=${this.num_of_report_dir}, source_paths=${this.source_paths}, os_name=${this.os_name}, command_json=${this.command_json}, working_directory=${this.working_directory}, output_path=${this.output_path}, project_root=${this.project_root}, log_file=${this.log_file}, enable_rules=${this.enable_rules}, disable_rules=${this.disable_rules}, failed_sources=${this.failed_sources}, failed_sources_num=${this.failed_sources_num}, successful_sources=${this.successful_sources}, successful_sources_num=${this.successful_sources_num}, skipped_sources=${this.skipped_sources}, skipped_sources_num=${this.skipped_sources_num})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   (
                                                            (
                                                                     (
                                                                              (
                                                                                       (
                                                                                                (
                                                                                                         (
                                                                                                                  (
                                                                                                                           (
                                                                                                                                    (
                                                                                                                                             (
                                                                                                                                                      (
                                                                                                                                                               (
                                                                                                                                                                        (
                                                                                                                                                                                 (
                                                                                                                                                                                          (
                                                                                                                                                                                                   (
                                                                                                                                                                                                            (
                                                                                                                                                                                                                     (
                                                                                                                                                                                                                              (
                                                                                                                                                                                                                                       (
                                                                                                                                                                                                                                                (
                                                                                                                                                                                                                                                         this.name
                                                                                                                                                                                                                                                                  .hashCode()
                                                                                                                                                                                                                                                               * 31
                                                                                                                                                                                                                                                            + this.corax_probe_version
                                                                                                                                                                                                                                                               .hashCode()
                                                                                                                                                                                                                                                      )
                                                                                                                                                                                                                                                      * 31
                                                                                                                                                                                                                                                   + this.analyzer_version
                                                                                                                                                                                                                                                      .hashCode()
                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                             * 31
                                                                                                                                                                                                                                          + this.analysis_begin_date
                                                                                                                                                                                                                                             .hashCode()
                                                                                                                                                                                                                                    )
                                                                                                                                                                                                                                    * 31
                                                                                                                                                                                                                                 + java.lang.Long.hashCode(
                                                                                                                                                                                                                                    this.analysis_begin_timestamp
                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                           )
                                                                                                                                                                                                                           * 31
                                                                                                                                                                                                                        + java.lang.Long.hashCode(
                                                                                                                                                                                                                           this.analysis_escape_seconds
                                                                                                                                                                                                                        )
                                                                                                                                                                                                                  )
                                                                                                                                                                                                                  * 31
                                                                                                                                                                                                               + this.analysis_escape_time
                                                                                                                                                                                                                  .hashCode()
                                                                                                                                                                                                         )
                                                                                                                                                                                                         * 31
                                                                                                                                                                                                      + this.analysis_end_date
                                                                                                                                                                                                         .hashCode()
                                                                                                                                                                                                )
                                                                                                                                                                                                * 31
                                                                                                                                                                                             + java.lang.Long.hashCode(
                                                                                                                                                                                                this.analysis_end_timestamp
                                                                                                                                                                                             )
                                                                                                                                                                                       )
                                                                                                                                                                                       * 31
                                                                                                                                                                                    + java.lang.Long.hashCode(
                                                                                                                                                                                       this.file_count
                                                                                                                                                                                    )
                                                                                                                                                                              )
                                                                                                                                                                              * 31
                                                                                                                                                                           + java.lang.Long.hashCode(
                                                                                                                                                                              this.line_count
                                                                                                                                                                           )
                                                                                                                                                                     )
                                                                                                                                                                     * 31
                                                                                                                                                                  + (
                                                                                                                                                                     if (this.code_coverage_covered
                                                                                                                                                                           == null)
                                                                                                                                                                        0
                                                                                                                                                                        else
                                                                                                                                                                        this.code_coverage_covered
                                                                                                                                                                           .hashCode()
                                                                                                                                                                  )
                                                                                                                                                            )
                                                                                                                                                            * 31
                                                                                                                                                         + (
                                                                                                                                                            if (this.code_coverage_missed
                                                                                                                                                                  == null)
                                                                                                                                                               0
                                                                                                                                                               else
                                                                                                                                                               this.code_coverage_missed
                                                                                                                                                                  .hashCode()
                                                                                                                                                         )
                                                                                                                                                   )
                                                                                                                                                   * 31
                                                                                                                                                + (
                                                                                                                                                   if (this.num_of_report_dir
                                                                                                                                                         == null)
                                                                                                                                                      0
                                                                                                                                                      else
                                                                                                                                                      this.num_of_report_dir
                                                                                                                                                         .hashCode()
                                                                                                                                                )
                                                                                                                                          )
                                                                                                                                          * 31
                                                                                                                                       + this.source_paths
                                                                                                                                          .hashCode()
                                                                                                                                 )
                                                                                                                                 * 31
                                                                                                                              + this.os_name.hashCode()
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + this.command_json.hashCode()
                                                                                                               )
                                                                                                               * 31
                                                                                                            + this.working_directory.hashCode()
                                                                                                      )
                                                                                                      * 31
                                                                                                   + this.output_path.hashCode()
                                                                                             )
                                                                                             * 31
                                                                                          + this.project_root.hashCode()
                                                                                    )
                                                                                    * 31
                                                                                 + this.log_file.hashCode()
                                                                           )
                                                                           * 31
                                                                        + this.enable_rules.hashCode()
                                                                  )
                                                                  * 31
                                                               + this.disable_rules.hashCode()
                                                         )
                                                         * 31
                                                      + this.failed_sources.hashCode()
                                                )
                                                * 31
                                             + java.lang.Long.hashCode(this.failed_sources_num)
                                       )
                                       * 31
                                    + this.successful_sources.hashCode()
                              )
                              * 31
                           + java.lang.Long.hashCode(this.successful_sources_num)
                     )
                     * 31
                  + this.skipped_sources.hashCode()
            )
            * 31
         + java.lang.Long.hashCode(this.skipped_sources_num);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is AnalyzerStatistics) {
         return false;
      } else {
         val var2: AnalyzerStatistics = other as AnalyzerStatistics;
         if (!(this.name == (other as AnalyzerStatistics).name)) {
            return false;
         } else if (!(this.corax_probe_version == var2.corax_probe_version)) {
            return false;
         } else if (!(this.analyzer_version == var2.analyzer_version)) {
            return false;
         } else if (!(this.analysis_begin_date == var2.analysis_begin_date)) {
            return false;
         } else if (this.analysis_begin_timestamp != var2.analysis_begin_timestamp) {
            return false;
         } else if (this.analysis_escape_seconds != var2.analysis_escape_seconds) {
            return false;
         } else if (!(this.analysis_escape_time == var2.analysis_escape_time)) {
            return false;
         } else if (!(this.analysis_end_date == var2.analysis_end_date)) {
            return false;
         } else if (this.analysis_end_timestamp != var2.analysis_end_timestamp) {
            return false;
         } else if (this.file_count != var2.file_count) {
            return false;
         } else if (this.line_count != var2.line_count) {
            return false;
         } else if (!(this.code_coverage_covered == var2.code_coverage_covered)) {
            return false;
         } else if (!(this.code_coverage_missed == var2.code_coverage_missed)) {
            return false;
         } else if (!(this.num_of_report_dir == var2.num_of_report_dir)) {
            return false;
         } else if (!(this.source_paths == var2.source_paths)) {
            return false;
         } else if (!(this.os_name == var2.os_name)) {
            return false;
         } else if (!(this.command_json == var2.command_json)) {
            return false;
         } else if (!(this.working_directory == var2.working_directory)) {
            return false;
         } else if (!(this.output_path == var2.output_path)) {
            return false;
         } else if (!(this.project_root == var2.project_root)) {
            return false;
         } else if (!(this.log_file == var2.log_file)) {
            return false;
         } else if (!(this.enable_rules == var2.enable_rules)) {
            return false;
         } else if (!(this.disable_rules == var2.disable_rules)) {
            return false;
         } else if (!(this.failed_sources == var2.failed_sources)) {
            return false;
         } else if (this.failed_sources_num != var2.failed_sources_num) {
            return false;
         } else if (!(this.successful_sources == var2.successful_sources)) {
            return false;
         } else if (this.successful_sources_num != var2.successful_sources_num) {
            return false;
         } else if (!(this.skipped_sources == var2.skipped_sources)) {
            return false;
         } else {
            return this.skipped_sources_num == var2.skipped_sources_num;
         }
      }
   }
}
