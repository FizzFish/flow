package cn.sast.framework.report

import cn.sast.common.IResDirectory
import cn.sast.framework.result.OutputType
import kotlin.coroutines.Continuation

public abstract class ReportConsumer : IReportConsumer {
   public open val type: OutputType
   public final val outputDir: IResDirectory
   public abstract val metadata: cn.sast.framework.report.ReportConsumer.MetaData

   open fun ReportConsumer(type: OutputType, outputDir: IResDirectory) {
      this.type = type;
      this.outputDir = outputDir;
      this.outputDir.mkdirs();
   }

   public override suspend fun init() {
      return init$suspendImpl(this, `$completion`);
   }

   override fun run(locator: IProjectFileLocator, `$completion`: Continuation<? super Unit>): Any? {
      return IReportConsumer.DefaultImpls.run(this, locator, `$completion`);
   }

   public data class MetaData(toolName: String, toolVersion: String, analyzerName: String) {
      public final val toolName: String
      public final val toolVersion: String
      public final val analyzerName: String

      init {
         this.toolName = toolName;
         this.toolVersion = toolVersion;
         this.analyzerName = analyzerName;
      }

      public operator fun component1(): String {
         return this.toolName;
      }

      public operator fun component2(): String {
         return this.toolVersion;
      }

      public operator fun component3(): String {
         return this.analyzerName;
      }

      public fun copy(toolName: String = this.toolName, toolVersion: String = this.toolVersion, analyzerName: String = this.analyzerName): cn.sast.framework.report.ReportConsumer.MetaData {
         return new ReportConsumer.MetaData(toolName, toolVersion, analyzerName);
      }

      public override fun toString(): String {
         return "MetaData(toolName=${this.toolName}, toolVersion=${this.toolVersion}, analyzerName=${this.analyzerName})";
      }

      public override fun hashCode(): Int {
         return (this.toolName.hashCode() * 31 + this.toolVersion.hashCode()) * 31 + this.analyzerName.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is ReportConsumer.MetaData) {
            return false;
         } else {
            val var2: ReportConsumer.MetaData = other as ReportConsumer.MetaData;
            if (!(this.toolName == (other as ReportConsumer.MetaData).toolName)) {
               return false;
            } else if (!(this.toolVersion == var2.toolVersion)) {
               return false;
            } else {
               return this.analyzerName == var2.analyzerName;
            }
         }
      }
   }
}
