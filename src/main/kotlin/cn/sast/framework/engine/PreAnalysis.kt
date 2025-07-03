package cn.sast.framework.engine

import cn.sast.api.config.MainConfig
import cn.sast.framework.SootCtx
import cn.sast.framework.metrics.MetricsMonitor
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.result.IPreAnalysisResultCollector
import cn.sast.idfa.analysis.ProcessInfoView
import com.feysh.corax.cache.analysis.SootInfoCache
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import java.time.LocalDateTime
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.Maybe

@SourceDebugExtension(["SMAP\nPreAnalysis.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysis.kt\ncn/sast/framework/engine/PreAnalysis\n+ 2 Logging.kt\norg/utbot/common/LoggingKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,36:1\n49#2,13:37\n62#2,11:54\n1557#3:50\n1628#3,3:51\n*S KotlinDebug\n*F\n+ 1 PreAnalysis.kt\ncn/sast/framework/engine/PreAnalysis\n*L\n28#1:37,13\n28#1:54,11\n31#1:50\n31#1:51,3\n*E\n"])
class PreAnalysis(mainConfig: MainConfig) {
    val mainConfig: MainConfig = mainConfig

    suspend fun analyzeInScene(
        soot: SootCtx,
        locator: IProjectFileLocator,
        info: SootInfoCache,
        resultCollector: IPreAnalysisResultCollector,
        monitor: MetricsMonitor
    ) {
        TODO("FIXME — Vineflower decompilation failed, original bytecode too complex")
    }

    companion object {
        private val logger: KLogger

        @JvmStatic
        fun `analyzeInScene$lambda$0`(): Any {
            return "Before PreAnalysis: Process information: ${ProcessInfoView.Companion.getGlobalProcessInfo().getProcessInfoText()}"
        }

        @JvmStatic
        fun `analyzeInScene$lambda$3`(): Any {
            return "After PreAnalysis: Process information: ${ProcessInfoView.Companion.getGlobalProcessInfo().getProcessInfoText()}"
        }

        @JvmStatic
        fun `logger$lambda$4`() {
        }

        @JvmStatic
        fun `PreAnalysis$analyzeInScene$$inlined$bracket$default$1`(`$msg`: String): Any {
            return "Started: ${`$msg`}"
        }

        @JvmStatic
        fun `PreAnalysis$analyzeInScene$$inlined$bracket$default$2`(
            `$startTime`: LocalDateTime,
            `$msg`: String,
            `$res`: ObjectRef
        ): Any {
            val var1: LocalDateTime = `$startTime`
            return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} "
        }

        @JvmStatic
        fun `PreAnalysis$analyzeInScene$$inlined$bracket$default$3`(
            `$startTime`: LocalDateTime,
            `$msg`: String
        ): Any {
            val var1: LocalDateTime = `$startTime`
            return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} <Nothing>"
        }

        @JvmStatic
        fun `PreAnalysis$analyzeInScene$$inlined$bracket$default$4`(
            `$startTime`: LocalDateTime,
            `$msg`: String,
            `$t`: Throwable
        ): Any {
            val var1: LocalDateTime = `$startTime`
            return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} :: EXCEPTION :: "
        }

        @JvmStatic
        fun `PreAnalysis$analyzeInScene$$inlined$bracket$default$5`(
            `$startTime`: LocalDateTime,
            `$msg`: String,
            `$res`: ObjectRef
        ): Any {
            val var1: LocalDateTime = `$startTime`
            return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} "
        }

        @JvmStatic
        fun `PreAnalysis$analyzeInScene$$inlined$bracket$default$6`(
            `$startTime`: LocalDateTime,
            `$msg`: String
        ): Any {
            val var1: LocalDateTime = `$startTime`
            return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} <Nothing>"
        }
    }
}