package cn.sast.framework.engine

import kotlin.jvm.functions.Function0

internal class `BuiltinAnalysis$allMethodsAnalyzeInScene$$inlined$bracket$default$1` : Function0<Any> {
    private val `$msg`: String

    constructor(`$msg`: String) {
        this.`$msg` = `$msg`
    }

    override fun invoke(): Any {
        return "Started: ${this.`$msg`}"
    }
}