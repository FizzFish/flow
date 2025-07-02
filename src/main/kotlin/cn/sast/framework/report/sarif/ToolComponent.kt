package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class ToolComponent(name: String, organization: String, version: String, rules: List<Rule>) {
   public final val name: String
   public final val organization: String
   public final val version: String
   public final val rules: List<Rule>

   init {
      this.name = name;
      this.organization = organization;
      this.version = version;
      this.rules = rules;
   }

   public operator fun component1(): String {
      return this.name;
   }

   public operator fun component2(): String {
      return this.organization;
   }

   public operator fun component3(): String {
      return this.version;
   }

   public operator fun component4(): List<Rule> {
      return this.rules;
   }

   public fun copy(name: String = this.name, organization: String = this.organization, version: String = this.version, rules: List<Rule> = this.rules): ToolComponent {
      return new ToolComponent(name, organization, version, rules);
   }

   public override fun toString(): String {
      return "ToolComponent(name=${this.name}, organization=${this.organization}, version=${this.version}, rules=${this.rules})";
   }

   public override fun hashCode(): Int {
      return ((this.name.hashCode() * 31 + this.organization.hashCode()) * 31 + this.version.hashCode()) * 31 + this.rules.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ToolComponent) {
         return false;
      } else {
         val var2: ToolComponent = other as ToolComponent;
         if (!(this.name == (other as ToolComponent).name)) {
            return false;
         } else if (!(this.organization == var2.organization)) {
            return false;
         } else if (!(this.version == var2.version)) {
            return false;
         } else {
            return this.rules == var2.rules;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<ToolComponent> {
         return ToolComponent.$serializer.INSTANCE as KSerializer<ToolComponent>;
      }
   }
}
