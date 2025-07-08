package cn.sast.api.config

import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.CheckerUnit
import com.feysh.corax.config.api.ISootInitializeHandler

data class SaConfig(
    val builtinAnalysisConfig: BuiltinAnalysisConfig = BuiltinAnalysisConfig( 0, 15,),
    val preAnalysisConfig: PreAnalysisConfig = PreAnalysisConfig(0, 0, 0, null, 0, 31, null),
    val checkers: Set<CheckerUnit>,
    val sootConfig: ISootInitializeHandler,
    val enableCheckTypes: Set<CheckType>?
) {
    public fun isEnable(checkType: CheckType): Boolean {
        return this.enableCheckTypes == null || this.enableCheckTypes.contains(checkType)
    }
}