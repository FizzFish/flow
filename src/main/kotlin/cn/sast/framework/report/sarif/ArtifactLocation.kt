package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class ArtifactLocation(uri: String, uriBaseId: String = "") {
   public final val uri: String
   public final val uriBaseId: String

   init {
      this.uri = uri;
      this.uriBaseId = uriBaseId;
   }

   public operator fun component1(): String {
      return this.uri;
   }

   public operator fun component2(): String {
      return this.uriBaseId;
   }

   public fun copy(uri: String = this.uri, uriBaseId: String = this.uriBaseId): ArtifactLocation {
      return new ArtifactLocation(uri, uriBaseId);
   }

   public override fun toString(): String {
      return "ArtifactLocation(uri=${this.uri}, uriBaseId=${this.uriBaseId})";
   }

   public override fun hashCode(): Int {
      return this.uri.hashCode() * 31 + this.uriBaseId.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ArtifactLocation) {
         return false;
      } else {
         val var2: ArtifactLocation = other as ArtifactLocation;
         if (!(this.uri == (other as ArtifactLocation).uri)) {
            return false;
         } else {
            return this.uriBaseId == var2.uriBaseId;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<ArtifactLocation> {
         return ArtifactLocation.$serializer.INSTANCE as KSerializer<ArtifactLocation>;
      }
   }
}
