package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class FlowLocationWrapper(location: FlowLocation) {
   public final val location: FlowLocation

   init {
      this.location = location;
   }

   public operator fun component1(): FlowLocation {
      return this.location;
   }

   public fun copy(location: FlowLocation = this.location): FlowLocationWrapper {
      return new FlowLocationWrapper(location);
   }

   public override fun toString(): String {
      return "FlowLocationWrapper(location=${this.location})";
   }

   public override fun hashCode(): Int {
      return this.location.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is FlowLocationWrapper) {
         return false;
      } else {
         return this.location == (other as FlowLocationWrapper).location;
      }
   }

   public companion object {
      public fun serializer(): KSerializer<FlowLocationWrapper> {
         return FlowLocationWrapper.$serializer.INSTANCE as KSerializer<FlowLocationWrapper>;
      }
   }
}
