package cn.sast.api.config

import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.CheckerUnit
import com.feysh.corax.config.api.ISootInitializeHandler

public data class SaConfig(builtinAnalysisConfig: BuiltinAnalysisConfig = new BuiltinAnalysisConfig(null, null, 0, 0, 15, null),
   preAnalysisConfig: PreAnalysisConfig = new PreAnalysisConfig(0, 0, 0, null, 0, 31, null),
   checkers: Set<CheckerUnit>,
   sootConfig: ISootInitializeHandler,
   enableCheckTypes: Set<CheckType>?
) {
   public final val builtinAnalysisConfig: BuiltinAnalysisConfig
   public final val preAnalysisConfig: PreAnalysisConfig
   public final val checkers: Set<CheckerUnit>
   public final val sootConfig: ISootInitializeHandler
   public final val enableCheckTypes: Set<CheckType>?

   init {
      this.builtinAnalysisConfig = builtinAnalysisConfig;
      this.preAnalysisConfig = preAnalysisConfig;
      this.checkers = checkers;
      this.sootConfig = sootConfig;
      this.enableCheckTypes = enableCheckTypes;
   }

   public fun isEnable(checkType: CheckType): Boolean {
      return this.enableCheckTypes == null || this.enableCheckTypes.contains(checkType);
   }

   public operator fun component1(): BuiltinAnalysisConfig {
      return this.builtinAnalysisConfig;
   }

   public operator fun component2(): PreAnalysisConfig {
      return this.preAnalysisConfig;
   }

   public operator fun component3(): Set<CheckerUnit> {
      return this.checkers;
   }

   public operator fun component4(): ISootInitializeHandler {
      return this.sootConfig;
   }

   public operator fun component5(): Set<CheckType>? {
      return this.enableCheckTypes;
   }

   public fun copy(
      builtinAnalysisConfig: BuiltinAnalysisConfig = this.builtinAnalysisConfig,
      preAnalysisConfig: PreAnalysisConfig = this.preAnalysisConfig,
      checkers: Set<CheckerUnit> = this.checkers,
      sootConfig: ISootInitializeHandler = this.sootConfig,
      enableCheckTypes: Set<CheckType>? = this.enableCheckTypes
   ): SaConfig {
      return new SaConfig(builtinAnalysisConfig, preAnalysisConfig, checkers, sootConfig, enableCheckTypes);
   }

   public override fun toString(): String {
      return "SaConfig(builtinAnalysisConfig=${this.builtinAnalysisConfig}, preAnalysisConfig=${this.preAnalysisConfig}, checkers=${this.checkers}, sootConfig=${this.sootConfig}, enableCheckTypes=${this.enableCheckTypes})";
   }

   public override fun hashCode(): Int {
      return (
               ((this.builtinAnalysisConfig.hashCode() * 31 + this.preAnalysisConfig.hashCode()) * 31 + this.checkers.hashCode()) * 31
                  + this.sootConfig.hashCode()
            )
            * 31
         + (if (this.enableCheckTypes == null) 0 else this.enableCheckTypes.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is SaConfig) {
         return false;
      } else {
         val var2: SaConfig = other as SaConfig;
         if (!(this.builtinAnalysisConfig == (other as SaConfig).builtinAnalysisConfig)) {
            return false;
         } else if (!(this.preAnalysisConfig == var2.preAnalysisConfig)) {
            return false;
         } else if (!(this.checkers == var2.checkers)) {
            return false;
         } else if (!(this.sootConfig == var2.sootConfig)) {
            return false;
         } else {
            return this.enableCheckTypes == var2.enableCheckTypes;
         }
      }
   }
}
