package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class CodeFlow(threadFlows: List<ThreadFlow>) {
   public final val threadFlows: List<ThreadFlow>

   init {
      this.threadFlows = threadFlows;
   }

   public operator fun component1(): List<ThreadFlow> {
      return this.threadFlows;
   }

   public fun copy(threadFlows: List<ThreadFlow> = this.threadFlows): CodeFlow {
      return new CodeFlow(threadFlows);
   }

   public override fun toString(): String {
      return "CodeFlow(threadFlows=${this.threadFlows})";
   }

   public override fun hashCode(): Int {
      return this.threadFlows.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is CodeFlow) {
         return false;
      } else {
         return this.threadFlows == (other as CodeFlow).threadFlows;
      }
   }

   public companion object {
      public fun serializer(): KSerializer<CodeFlow> {
         return CodeFlow.$serializer.INSTANCE as KSerializer<CodeFlow>;
      }
   }
}
