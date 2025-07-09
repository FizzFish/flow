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

            override fun getEntries(): Set<SootMethod> = methods
            override fun getComponents(): Set<SootClass> = emptySet()
            override fun getName(): String = "CustomEntryProvider(entries=${entries.size})"

            override fun needConstructCallGraph(sootCtx: SootCtx) = sootCtx.constructCallGraph()
            override fun getMethodsMustAnalyze(): Set<SootMethod> = entries
            override fun getAdditionalEntries(): Set<SootMethod> = emptySet()
        })
    }

    override fun startAnalyse() = Unit
    override fun endAnalyse() = Unit

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
    }
}