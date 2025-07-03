package cn.sast.cli.command

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

internal class `FySastCli$runCodeMetrics$4$invokeSuspend$$inlined$bracket$default$6` : Function0<Any> {
    private val $startTime: LocalDateTime
    private val $msg: String

    constructor(startTime: LocalDateTime, msg: String) {
        this.$startTime = startTime
        this.$msg = msg
    }

    override fun invoke(): Any {
        val var1: LocalDateTime = this.$startTime
        return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>"
    }
}