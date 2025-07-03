package cn.sast.framework.report

import kotlin.jvm.functions.Function0

internal class `ReportConverter$flush$2$invokeSuspend$lambda$17$$inlined$bracket$default$1` : Function0<Any> {
    private val $msg: String

    constructor(`$msg`: String) {
        this.$msg = `$msg`
    }

    override fun invoke(): Any {
        return "Started: ${this.$msg}"
    }
}