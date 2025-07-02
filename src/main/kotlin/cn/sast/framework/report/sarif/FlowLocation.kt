package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class FlowLocation(message: Message, physicalLocation: PhysicalLocation) {
   public final val message: Message
   public final val physicalLocation: PhysicalLocation

   init {
      this.message = message;
      this.physicalLocation = physicalLocation;
   }

   public operator fun component1(): Message {
      return this.message;
   }

   public operator fun component2(): PhysicalLocation {
      return this.physicalLocation;
   }

   public fun copy(message: Message = this.message, physicalLocation: PhysicalLocation = this.physicalLocation): FlowLocation {
      return new FlowLocation(message, physicalLocation);
   }

   public override fun toString(): String {
      return "FlowLocation(message=${this.message}, physicalLocation=${this.physicalLocation})";
   }

   public override fun hashCode(): Int {
      return this.message.hashCode() * 31 + this.physicalLocation.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is FlowLocation) {
         return false;
      } else {
         val var2: FlowLocation = other as FlowLocation;
         if (!(this.message == (other as FlowLocation).message)) {
            return false;
         } else {
            return this.physicalLocation == var2.physicalLocation;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<FlowLocation> {
         return FlowLocation.$serializer.INSTANCE as KSerializer<FlowLocation>;
      }
   }
}
