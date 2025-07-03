package cn.sast.framework.report

import kotlin.jvm.functions.Function0

internal class `ProjectFileLocator$update$1$invokeSuspend$$inlined$bracket$default$1` : Function0<Any> {
    private lateinit var $msg: String

    constructor($msg: String) {
        this.$msg = $msg
    }

    override fun invoke(): Any {
        return "Started: ${this.$msg}"
    }
}