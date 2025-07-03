package cn.sast.framework.entries.javaee

import kotlin.jvm.functions.Function0

internal class `JavaEeEntryProvider$iterator$1$invokeSuspend$$inlined$bracket$default$1` : Function0<Any> {
    private val `$msg`: String

    constructor(`$msg`: String) {
        this.`$msg` = `$msg`
    }

    override fun invoke(): Any {
        return "Started: ${this.`$msg`}"
    }
}