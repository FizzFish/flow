package cn.sast.cli.command

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nDataFlowOptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DataFlowOptions.kt\ncn/sast/cli/command/DataFlowOptions\n+ 2 Convert.kt\ncom/github/ajalt/clikt/parameters/options/OptionWithValuesKt__ConvertKt\n*L\n1#1,23:1\n35#2,6:24\n70#2:30\n82#2,4:31\n*S KotlinDebug\n*F\n+ 1 DataFlowOptions.kt\ncn/sast/cli/command/DataFlowOptions\n*L\n10#1:24,6\n10#1:30\n10#1:31,4\n*E\n"])
public class DataFlowOptions : OptionGroup("Data Flow Options", null, 2) {
   public final val enableDataFlow: Boolean
      public final get() {
         return this.enableDataFlow$delegate.getValue(this, $$delegatedProperties[0]) as java.lang.Boolean;
      }


   public final val enableCoverage: Boolean
      public final get() {
         return this.enableCoverage$delegate.getValue(this, $$delegatedProperties[1]) as java.lang.Boolean;
      }


   public final val factor1: Int?
      public final get() {
         return this.factor1$delegate.getValue(this, $$delegatedProperties[2]) as Int;
      }


   public final val dataFlowInterProceduralCalleeTimeOut: Int?
      public final get() {
         return this.dataFlowInterProceduralCalleeTimeOut$delegate.getValue(this, $$delegatedProperties[3]) as Int;
      }

}
