package cn.sast.framework.report.metadata

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Tool(analyzers: Map<String, Analyzer>,
   command: List<String>,
   name: String,
   outputPath: String,
   projectRoot: String,
   multipleProjectRoot: List<String>,
   resultSourceFiles: Map<String, String>,
   workingDirectory: String
) {
   public final val analyzers: Map<String, Analyzer>
   public final val command: List<String>
   public final val name: String

   @SerialName("output_path")
   public final val outputPath: String

   @SerialName("project_root")
   public final val projectRoot: String

   @SerialName("multiple_project_root")
   public final val multipleProjectRoot: List<String>

   @SerialName("result_source_files")
   public final val resultSourceFiles: Map<String, String>

   @SerialName("working_directory")
   public final val workingDirectory: String

   init {
      this.analyzers = analyzers;
      this.command = command;
      this.name = name;
      this.outputPath = outputPath;
      this.projectRoot = projectRoot;
      this.multipleProjectRoot = multipleProjectRoot;
      this.resultSourceFiles = resultSourceFiles;
      this.workingDirectory = workingDirectory;
   }

   public operator fun component1(): Map<String, Analyzer> {
      return this.analyzers;
   }

   public operator fun component2(): List<String> {
      return this.command;
   }

   public operator fun component3(): String {
      return this.name;
   }

   public operator fun component4(): String {
      return this.outputPath;
   }

   public operator fun component5(): String {
      return this.projectRoot;
   }

   public operator fun component6(): List<String> {
      return this.multipleProjectRoot;
   }

   public operator fun component7(): Map<String, String> {
      return this.resultSourceFiles;
   }

   public operator fun component8(): String {
      return this.workingDirectory;
   }

   public fun copy(
      analyzers: Map<String, Analyzer> = this.analyzers,
      command: List<String> = this.command,
      name: String = this.name,
      outputPath: String = this.outputPath,
      projectRoot: String = this.projectRoot,
      multipleProjectRoot: List<String> = this.multipleProjectRoot,
      resultSourceFiles: Map<String, String> = this.resultSourceFiles,
      workingDirectory: String = this.workingDirectory
   ): Tool {
      return new Tool(analyzers, command, name, outputPath, projectRoot, multipleProjectRoot, resultSourceFiles, workingDirectory);
   }

   public override fun toString(): String {
      return "Tool(analyzers=${this.analyzers}, command=${this.command}, name=${this.name}, outputPath=${this.outputPath}, projectRoot=${this.projectRoot}, multipleProjectRoot=${this.multipleProjectRoot}, resultSourceFiles=${this.resultSourceFiles}, workingDirectory=${this.workingDirectory})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (((this.analyzers.hashCode() * 31 + this.command.hashCode()) * 31 + this.name.hashCode()) * 31 + this.outputPath.hashCode())
                                       * 31
                                    + this.projectRoot.hashCode()
                              )
                              * 31
                           + this.multipleProjectRoot.hashCode()
                     )
                     * 31
                  + this.resultSourceFiles.hashCode()
            )
            * 31
         + this.workingDirectory.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Tool) {
         return false;
      } else {
         val var2: Tool = other as Tool;
         if (!(this.analyzers == (other as Tool).analyzers)) {
            return false;
         } else if (!(this.command == var2.command)) {
            return false;
         } else if (!(this.name == var2.name)) {
            return false;
         } else if (!(this.outputPath == var2.outputPath)) {
            return false;
         } else if (!(this.projectRoot == var2.projectRoot)) {
            return false;
         } else if (!(this.multipleProjectRoot == var2.multipleProjectRoot)) {
            return false;
         } else if (!(this.resultSourceFiles == var2.resultSourceFiles)) {
            return false;
         } else {
            return this.workingDirectory == var2.workingDirectory;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<Tool> {
         return Tool.$serializer.INSTANCE as KSerializer<Tool>;
      }
   }
}
