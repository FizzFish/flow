
package cn.sast.framework.engine

import cn.sast.api.config.MainConfig
import cn.sast.framework.SootCtx
import cn.sast.framework.metrics.MetricsMonitor
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.result.IPreAnalysisResultCollector
import com.feysh.corax.cache.analysis.SootInfoCache
import mu.KLogger
import mu.KotlinLogging

/**
 * Lightweight façade that delegates to [PreAnalysisImpl] while
 * keeping the public surface minimal.
 */
class PreAnalysis(private val mainConfig: MainConfig) {

    suspend fun analyzeInScene(
        soot: SootCtx,
        locator: IProjectFileLocator,
        info: SootInfoCache,
        resultCollector: IPreAnalysisResultCollector,
        monitor: MetricsMonitor,
    ) {
        PreAnalysisImpl(
            mainConfig,
            locator,
            soot.callGraph,
            info,
            resultCollector,
            soot.scene,
        ).also { impl ->
            impl.scope = soot.coroutineScope
        }.processPreAnalysisUnits()
    }

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
    }
}
