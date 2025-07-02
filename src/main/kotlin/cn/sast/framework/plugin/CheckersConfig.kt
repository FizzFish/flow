package cn.sast.framework.plugin

import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.IChecker
import com.feysh.corax.config.api.utils.UtilsKt
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CheckerTypeConfig")
@SourceDebugExtension(["SMAP\nSAConfiguration.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/CheckersConfig\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,557:1\n1#2:558\n*E\n"])
public data class CheckersConfig(name: String,
      desc: String? = null,
      enable: Boolean,
      checkTypes: LinkedHashSet<cn.sast.framework.plugin.CheckersConfig.CheckTypeConfig> = new LinkedHashSet()
   ) : ConfigSerializable(),
   IOptional {
   public open val name: String
   public final val desc: String?
   public open val enable: Boolean

   public final var checkTypes: LinkedHashSet<cn.sast.framework.plugin.CheckersConfig.CheckTypeConfig>
      internal set

   init {
      this.name = name;
      this.desc = desc;
      this.enable = enable;
      this.checkTypes = checkTypes;
   }

   public constructor(checker: IChecker) : this(UtilsKt.getSootTypeName(checker.getClass()), checker.getDesc(), true, null, 8, null)
   public fun sort(): CheckersConfig {
      this.checkTypes = CollectionsKt.toCollection(CollectionsKt.sorted(this.checkTypes), new LinkedHashSet()) as LinkedHashSet<CheckersConfig.CheckTypeConfig>;
      return this;
   }

   public override operator fun compareTo(other: ConfigSerializable): Int {
      if (other !is CheckersConfig) {
         return super.compareTo(other);
      } else {
         val var3: Int = super.compareTo(other);
         val var2: Int = if (var3.intValue() != 0) var3 else null;
         if (var2 != null) {
            return var2.intValue();
         } else if (this.getEnable() != (other as CheckersConfig).getEnable()) {
            return if (this.getEnable()) -1 else 1;
         } else {
            return 0;
         }
      }
   }

   public operator fun component1(): String {
      return this.name;
   }

   public operator fun component2(): String? {
      return this.desc;
   }

   public operator fun component3(): Boolean {
      return this.enable;
   }

   public operator fun component4(): LinkedHashSet<cn.sast.framework.plugin.CheckersConfig.CheckTypeConfig> {
      return this.checkTypes;
   }

   public fun copy(
      name: String = this.name,
      desc: String? = this.desc,
      enable: Boolean = this.enable,
      checkTypes: LinkedHashSet<cn.sast.framework.plugin.CheckersConfig.CheckTypeConfig> = this.checkTypes
   ): CheckersConfig {
      return new CheckersConfig(name, desc, enable, checkTypes);
   }

   public override fun toString(): String {
      return "CheckersConfig(name=${this.name}, desc=${this.desc}, enable=${this.enable}, checkTypes=${this.checkTypes})";
   }

   public override fun hashCode(): Int {
      return ((this.name.hashCode() * 31 + (if (this.desc == null) 0 else this.desc.hashCode())) * 31 + java.lang.Boolean.hashCode(this.enable)) * 31
         + this.checkTypes.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is CheckersConfig) {
         return false;
      } else {
         val var2: CheckersConfig = other as CheckersConfig;
         if (!(this.name == (other as CheckersConfig).name)) {
            return false;
         } else if (!(this.desc == var2.desc)) {
            return false;
         } else if (this.enable != var2.enable) {
            return false;
         } else {
            return this.checkTypes == var2.checkTypes;
         }
      }
   }

   @Serializable
   @SerialName("CheckTypeConfig")
   @SourceDebugExtension(["SMAP\nSAConfiguration.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/CheckersConfig$CheckTypeConfig\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,557:1\n1#2:558\n*E\n"])
   public data class CheckTypeConfig(checkType: String, enable: Boolean) : IOptional, IConfig, java.lang.Comparable<CheckersConfig.CheckTypeConfig> {
      public final val checkType: String
      public open val enable: Boolean

      init {
         this.checkType = checkType;
         this.enable = enable;
      }

      public constructor(type: CheckType) : this(SAConfigurationKt.get1to1SpecialIdentifier(type), true)
      public open operator fun compareTo(other: cn.sast.framework.plugin.CheckersConfig.CheckTypeConfig): Int {
         val var3: Int = this.checkType.compareTo(other.checkType);
         val var2: Int = if (var3.intValue() != 0) var3 else null;
         if (var2 != null) {
            return var2.intValue();
         } else if (this.getEnable() != other.getEnable()) {
            return if (this.getEnable()) -1 else 1;
         } else {
            return 0;
         }
      }

      public operator fun component1(): String {
         return this.checkType;
      }

      public operator fun component2(): Boolean {
         return this.enable;
      }

      public fun copy(checkType: String = this.checkType, enable: Boolean = this.enable): cn.sast.framework.plugin.CheckersConfig.CheckTypeConfig {
         return new CheckersConfig.CheckTypeConfig(checkType, enable);
      }

      public override fun toString(): String {
         return "CheckTypeConfig(checkType=${this.checkType}, enable=${this.enable})";
      }

      public override fun hashCode(): Int {
         return this.checkType.hashCode() * 31 + java.lang.Boolean.hashCode(this.enable);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is CheckersConfig.CheckTypeConfig) {
            return false;
         } else {
            val var2: CheckersConfig.CheckTypeConfig = other as CheckersConfig.CheckTypeConfig;
            if (!(this.checkType == (other as CheckersConfig.CheckTypeConfig).checkType)) {
               return false;
            } else {
               return this.enable == var2.enable;
            }
         }
      }

      public companion object {
         public fun serializer(): KSerializer<cn.sast.framework.plugin.CheckersConfig.CheckTypeConfig> {
            return CheckersConfig.CheckTypeConfig.$serializer.INSTANCE as KSerializer<CheckersConfig.CheckTypeConfig>;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<CheckersConfig> {
         return CheckersConfig.$serializer.INSTANCE as KSerializer<CheckersConfig>;
      }
   }
}
