package cn.sast.framework.report

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import org.utbot.common.LoggingKt

@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$3\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,64:1\n51#2:65\n*E\n"])
internal class `SarifDiagnosticsCopySrc$close$$inlined$bracket$default$4`(
    private val `$startTime`: LocalDateTime,
    private val `$msg`: String,
    private val `$t`: Throwable
) : Function0<Any> {
    override fun invoke(): Any {
        val var1: LocalDateTime = `$startTime`
        val var10000: String = LoggingKt.elapsedSecFrom(var1)
        val var10001: String = `$msg`
        val it: Any = Result.constructor-impl(ResultKt.createFailure(`$t`))
        return "Finished (in $var10000): $var10001 :: EXCEPTION :: "
    }
}