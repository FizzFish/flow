package cn.sast.framework.entries.apk

import soot.jimple.infoflow.android.InfoflowAndroidConfiguration.CallbackAnalyzer

public val CallbackAnalyzerType.convert: CallbackAnalyzerType
    get() {
        return when (this) {
            CallbackAnalyzerType.Default -> CallbackAnalyzerType.Default
            CallbackAnalyzerType.Fast -> CallbackAnalyzerType.Fast
            else -> throw NoWhenBranchMatchedException()
        }
    }

public val CallbackAnalyzer.convert: CallbackAnalyzer
    get() {
        return when (this) {
            CallbackAnalyzer.Default -> CallbackAnalyzer.Default
            CallbackAnalyzer.Fast -> CallbackAnalyzer.Fast
            else -> throw NoWhenBranchMatchedException()
        }
    }

@JvmSynthetic
internal object WhenMappings {
    @JvmStatic
    val $EnumSwitchMapping$0: IntArray = run {
        val array = IntArray(CallbackAnalyzer.values().size)
        try {
            array[CallbackAnalyzer.Default.ordinal] = 1
        } catch (_: NoSuchFieldError) { }
        
        try {
            array[CallbackAnalyzer.Fast.ordinal] = 2
        } catch (_: NoSuchFieldError) { }
        array
    }

    @JvmStatic
    val $EnumSwitchMapping$1: IntArray = run {
        val array = IntArray(CallbackAnalyzerType.values().size)
        try {
            array[CallbackAnalyzerType.Default.ordinal] = 1
        } catch (_: NoSuchFieldError) { }
        
        try {
            array[CallbackAnalyzerType.Fast.ordinal] = 2
        } catch (_: NoSuchFieldError) { }
        array
    }
}