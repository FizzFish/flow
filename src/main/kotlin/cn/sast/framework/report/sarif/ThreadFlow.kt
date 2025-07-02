package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class ThreadFlow(locations: List<FlowLocationWrapper>) {
   public final val locations: List<FlowLocationWrapper>

   init {
      this.locations = locations;
   }

   public operator fun component1(): List<FlowLocationWrapper> {
      return this.locations;
   }

   public fun copy(locations: List<FlowLocationWrapper> = this.locations): ThreadFlow {
      return new ThreadFlow(locations);
   }

   public override fun toString(): String {
      return "ThreadFlow(locations=${this.locations})";
   }

   public override fun hashCode(): Int {
      return this.locations.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ThreadFlow) {
         return false;
      } else {
         return this.locations == (other as ThreadFlow).locations;
      }
   }

   public companion object {
      public fun serializer(): KSerializer<ThreadFlow> {
         return ThreadFlow.$serializer.INSTANCE as KSerializer<ThreadFlow>;
      }
   }
}
