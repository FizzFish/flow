package cn.sast.framework.report.sarif

import cn.sast.api.config.ExtSettings
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

@Serializable
public data class SarifLog(schema: String, version: String, runs: List<Run>) {
   @SerialName("$schema")
   public final val schema: String

   public final val version: String
   public final val runs: List<Run>

   init {
      this.schema = schema;
      this.version = version;
      this.runs = runs;
   }

   public fun toJson(): String {
      return jsonFormat.encodeToString(Companion.serializer() as SerializationStrategy, this);
   }

   public operator fun component1(): String {
      return this.schema;
   }

   public operator fun component2(): String {
      return this.version;
   }

   public operator fun component3(): List<Run> {
      return this.runs;
   }

   public fun copy(schema: String = this.schema, version: String = this.version, runs: List<Run> = this.runs): SarifLog {
      return new SarifLog(schema, version, runs);
   }

   public override fun toString(): String {
      return "SarifLog(schema=${this.schema}, version=${this.version}, runs=${this.runs})";
   }

   public override fun hashCode(): Int {
      return (this.schema.hashCode() * 31 + this.version.hashCode()) * 31 + this.runs.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is SarifLog) {
         return false;
      } else {
         val var2: SarifLog = other as SarifLog;
         if (!(this.schema == (other as SarifLog).schema)) {
            return false;
         } else if (!(this.version == var2.version)) {
            return false;
         } else {
            return this.runs == var2.runs;
         }
      }
   }

   @JvmStatic
   fun JsonBuilder.`jsonFormat$lambda$0`(): Unit {
      `$this$Json`.setUseArrayPolymorphism(true);
      `$this$Json`.setPrettyPrint(ExtSettings.INSTANCE.getPrettyPrintJsonReport());
      `$this$Json`.setEncodeDefaults(false);
      return Unit.INSTANCE;
   }

   public companion object {
      private final val jsonFormat: Json

      public fun serializer(): KSerializer<SarifLog> {
         return SarifLog.$serializer.INSTANCE as KSerializer<SarifLog>;
      }
   }
}
