package cn.sast.framework.report.metadata

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

@Serializable
public data class AnalysisMetadata(fileCount: Int,
   lineCount: Int,
   codeCoverage: Counter,
   numOfReportDir: Int,
   sourcePaths: List<String>,
   osName: String,
   tools: List<Tool>
) {
   @SerialName("file_count")
   public final val fileCount: Int

   @SerialName("line_count")
   public final val lineCount: Int

   @SerialName("code_coverage")
   public final val codeCoverage: Counter

   @SerialName("num_of_report_dir")
   public final val numOfReportDir: Int

   @SerialName("source_paths")
   public final val sourcePaths: List<String>

   @SerialName("os_name")
   public final val osName: String

   public final val tools: List<Tool>

   init {
      this.fileCount = fileCount;
      this.lineCount = lineCount;
      this.codeCoverage = codeCoverage;
      this.numOfReportDir = numOfReportDir;
      this.sourcePaths = sourcePaths;
      this.osName = osName;
      this.tools = tools;
   }

   public fun toJson(): String {
      return jsonFormat.encodeToString(Companion.serializer() as SerializationStrategy, this);
   }

   public operator fun component1(): Int {
      return this.fileCount;
   }

   public operator fun component2(): Int {
      return this.lineCount;
   }

   public operator fun component3(): Counter {
      return this.codeCoverage;
   }

   public operator fun component4(): Int {
      return this.numOfReportDir;
   }

   public operator fun component5(): List<String> {
      return this.sourcePaths;
   }

   public operator fun component6(): String {
      return this.osName;
   }

   public operator fun component7(): List<Tool> {
      return this.tools;
   }

   public fun copy(
      fileCount: Int = this.fileCount,
      lineCount: Int = this.lineCount,
      codeCoverage: Counter = this.codeCoverage,
      numOfReportDir: Int = this.numOfReportDir,
      sourcePaths: List<String> = this.sourcePaths,
      osName: String = this.osName,
      tools: List<Tool> = this.tools
   ): AnalysisMetadata {
      return new AnalysisMetadata(fileCount, lineCount, codeCoverage, numOfReportDir, sourcePaths, osName, tools);
   }

   public override fun toString(): String {
      return "AnalysisMetadata(fileCount=${this.fileCount}, lineCount=${this.lineCount}, codeCoverage=${this.codeCoverage}, numOfReportDir=${this.numOfReportDir}, sourcePaths=${this.sourcePaths}, osName=${this.osName}, tools=${this.tools})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 ((Integer.hashCode(this.fileCount) * 31 + Integer.hashCode(this.lineCount)) * 31 + this.codeCoverage.hashCode()) * 31
                                    + Integer.hashCode(this.numOfReportDir)
                              )
                              * 31
                           + this.sourcePaths.hashCode()
                     )
                     * 31
                  + this.osName.hashCode()
            )
            * 31
         + this.tools.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is AnalysisMetadata) {
         return false;
      } else {
         val var2: AnalysisMetadata = other as AnalysisMetadata;
         if (this.fileCount != (other as AnalysisMetadata).fileCount) {
            return false;
         } else if (this.lineCount != var2.lineCount) {
            return false;
         } else if (!(this.codeCoverage == var2.codeCoverage)) {
            return false;
         } else if (this.numOfReportDir != var2.numOfReportDir) {
            return false;
         } else if (!(this.sourcePaths == var2.sourcePaths)) {
            return false;
         } else if (!(this.osName == var2.osName)) {
            return false;
         } else {
            return this.tools == var2.tools;
         }
      }
   }

   @JvmStatic
   fun JsonBuilder.`jsonFormat$lambda$0`(): Unit {
      `$this$Json`.setUseArrayPolymorphism(true);
      `$this$Json`.setPrettyPrint(true);
      `$this$Json`.setEncodeDefaults(false);
      return Unit.INSTANCE;
   }

   public companion object {
      private final val jsonFormat: Json

      public fun serializer(): KSerializer<AnalysisMetadata> {
         return AnalysisMetadata.$serializer.INSTANCE as KSerializer<AnalysisMetadata>;
      }
   }
}
