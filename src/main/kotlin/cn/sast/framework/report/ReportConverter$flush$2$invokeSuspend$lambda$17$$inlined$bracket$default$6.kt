package cn.sast.framework.report

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

internal class `ReportConverter$flush$2$invokeSuspend$lambda$17$$inlined$bracket$default$6`(
    private val `$startTime`: LocalDateTime,
    private val `$msg`: String
) : Function0<Any> {
    override fun invoke(): Any {
        val var1: LocalDateTime = `$startTime`
        return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} <Nothing>"
    }
}