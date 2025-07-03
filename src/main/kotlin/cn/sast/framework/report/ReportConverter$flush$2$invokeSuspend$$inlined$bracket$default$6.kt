package cn.sast.framework.report

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

internal class `ReportConverter$flush$2$invokeSuspend$$inlined$bracket$default$6`(
    private val `$startTime`: LocalDateTime,
    private val `$msg`: String
) : Function0<Any> {
    override fun invoke(): Any {
        return "Finished (in ${LoggingKt.elapsedSecFrom(`$startTime`)}): ${`$msg`} <Nothing>"
    }
}