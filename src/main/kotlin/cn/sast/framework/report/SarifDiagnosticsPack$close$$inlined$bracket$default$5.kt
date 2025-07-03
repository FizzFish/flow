package cn.sast.framework.report

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class `SarifDiagnosticsPack$close$$inlined$bracket$default$5`(
    private val `$startTime`: LocalDateTime,
    private val `$msg`: String,
    private val `$res`: ObjectRef
) : Function0<Any> {
    override fun invoke(): Any {
        val var1: LocalDateTime = `$startTime`
        val var10000: String = LoggingKt.elapsedSecFrom(var1)
        val var10001: String = `$msg`
        val it: Any = Result.constructor-impl((`$res`.element as Maybe).getOrThrow())
        return "Finished (in $var10000): $var10001"
    }
}