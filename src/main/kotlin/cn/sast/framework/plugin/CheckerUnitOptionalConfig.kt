package cn.sast.framework.plugin

import com.feysh.corax.config.api.SAOptions
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CheckerUnitOptionalConfig")
@SourceDebugExtension(["SMAP\nSAConfiguration.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/CheckerUnitOptionalConfig\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,557:1\n1#2:558\n*E\n"])
public data class CheckerUnitOptionalConfig(name: String, enable: Boolean, options: SAOptions?) : ConfigSerializable(), IOptional, IFieldOptions {
   public open val name: String
   public open val enable: Boolean
   public open val options: SAOptions?

   init {
      this.name = name;
      this.enable = enable;
      this.options = options;
   }

   public override operator fun compareTo(other: ConfigSerializable): Int {
      if (other !is CheckerUnitOptionalConfig) {
         return super.compareTo(other);
      } else {
         val var3: Int = super.compareTo(other);
         val var2: Int = if (var3.intValue() != 0) var3 else null;
         if (var2 != null) {
            return var2.intValue();
         } else if (this.getEnable() != (other as CheckerUnitOptionalConfig).getEnable()) {
            return if (this.getEnable()) -1 else 1;
         } else {
            return 0;
         }
      }
   }

   public operator fun component1(): String {
      return this.name;
   }

   public operator fun component2(): Boolean {
      return this.enable;
   }

   public operator fun component3(): SAOptions? {
      return this.options;
   }

   public fun copy(name: String = this.name, enable: Boolean = this.enable, options: SAOptions? = this.options): CheckerUnitOptionalConfig {
      return new CheckerUnitOptionalConfig(name, enable, options);
   }

   public override fun toString(): String {
      return "CheckerUnitOptionalConfig(name=${this.name}, enable=${this.enable}, options=${this.options})";
   }

   public override fun hashCode(): Int {
      return (this.name.hashCode() * 31 + java.lang.Boolean.hashCode(this.enable)) * 31 + (if (this.options == null) 0 else this.options.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is CheckerUnitOptionalConfig) {
         return false;
      } else {
         val var2: CheckerUnitOptionalConfig = other as CheckerUnitOptionalConfig;
         if (!(this.name == (other as CheckerUnitOptionalConfig).name)) {
            return false;
         } else if (this.enable != var2.enable) {
            return false;
         } else {
            return this.options == var2.options;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<CheckerUnitOptionalConfig> {
         return CheckerUnitOptionalConfig.$serializer.INSTANCE as KSerializer<CheckerUnitOptionalConfig>;
      }
   }
}
