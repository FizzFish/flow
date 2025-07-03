package cn.sast.api.config

import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.CheckerUnit
import com.feysh.corax.config.api.ISootInitializeHandler

public data class SaConfig(
    val builtinAnalysisConfig: BuiltinAnalysisConfig = BuiltinAnalysisConfig(null, null, 0, 0, 15, null),
    val preAnalysisConfig: PreAnalysisConfig = PreAnalysisConfig(0, 0, 0, null, 0, 31, null),
    val checkers: Set<CheckerUnit>,
    val sootConfig: ISootInitializeHandler,
    val enableCheckTypes: Set<CheckType>?
) {
    public fun isEnable(checkType: CheckType): Boolean {
        return this.enableCheckTypes == null || this.enableCheckTypes.contains(checkType)
    }

    public operator fun component1(): BuiltinAnalysisConfig {
        return this.builtinAnalysisConfig
    }

    public operator fun component2(): PreAnalysisConfig {
        return this.preAnalysisConfig
    }

    public operator fun component3(): Set<CheckerUnit> {
        return this.checkers
    }

    public operator fun component4(): ISootInitializeHandler {
        return this.sootConfig
    }

    public operator fun component5(): Set<CheckType>? {
        return this.enableCheckTypes
    }

    public fun copy(
        builtinAnalysisConfig: BuiltinAnalysisConfig = this.builtinAnalysisConfig,
        preAnalysisConfig: PreAnalysisConfig = this.preAnalysisConfig,
        checkers: Set<CheckerUnit> = this.checkers,
        sootConfig: ISootInitializeHandler = this.sootConfig,
        enableCheckTypes: Set<CheckType>? = this.enableCheckTypes
    ): SaConfig {
        return SaConfig(builtinAnalysisConfig, preAnalysisConfig, checkers, sootConfig, enableCheckTypes)
    }

    public override fun toString(): String {
        return "SaConfig(builtinAnalysisConfig=${this.builtinAnalysisConfig}, preAnalysisConfig=${this.preAnalysisConfig}, checkers=${this.checkers}, sootConfig=${this.sootConfig}, enableCheckTypes=${this.enableCheckTypes})"
    }

    public override fun hashCode(): Int {
        return (
                ((this.builtinAnalysisConfig.hashCode() * 31 + this.preAnalysisConfig.hashCode()) * 31 + this.checkers.hashCode()) * 31
                    + this.sootConfig.hashCode()
            )
            * 31
            + (this.enableCheckTypes?.hashCode() ?: 0)
    }

    public override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is SaConfig) {
            return false
        }
        
        return this.builtinAnalysisConfig == other.builtinAnalysisConfig &&
            this.preAnalysisConfig == other.preAnalysisConfig &&
            this.checkers == other.checkers &&
            this.sootConfig == other.sootConfig &&
            this.enableCheckTypes == other.enableCheckTypes
    }
}