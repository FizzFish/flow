package cn.sast.framework.entries.custom

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mu.KLogger
import mu.KotlinLogging
import soot.SootClass
import soot.SootMethod

open class CustomEntryProvider(
    private val entries: Set<SootMethod>
) : IEntryPointProvider {

    private val logger: KLogger = Companion.logger

    val methods: Set<SootMethod>
        get() {
            if (entries.isEmpty()) {
                logger.warn { "No entry points" }
            }
            logger.info { "custom entry methods num: ${entries.size}" }
            return entries
        }

    override val iterator: Flow<IEntryPointProvider.AnalyzeTask> = flow {
        emit(object : IEntryPointProvider.AnalyzeTask {

            override val entries: Set<SootMethod> = methods
            override val name: String = "CustomEntryProvider(entries=${entries.size})"

            override fun needConstructCallGraph(sootCtx: SootCtx) = sootCtx.constructCallGraph()
            override val  methodsMustAnalyze: Set<SootMethod> = entries
        })
    }

    override fun startAnalyse() = Unit
    override fun endAnalyse() = Unit

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
    }
}