package cn.sast.framework.entries.component

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mu.KLogger
import mu.KotlinLogging
import soot.Scene
import soot.SootClass
import soot.SootMethod

class ComponentEntryProvider(
    private val ctx: SootCtx,
    private val entries: Collection<String>
) : IEntryPointProvider {

    val methods: Set<SootMethod> by lazy {
        val dummyMain = ComponentEntryPointCreator(entries).createDummyMain()
        if (!dummyMain.declaringClass.isInScene) {
            Scene.v().addClass(dummyMain.declaringClass)
        }
        dummyMain.declaringClass.setApplicationClass()
        setOf(dummyMain)
    }

    override val iterator: Flow<IEntryPointProvider.AnalyzeTask> = flow {
        emit(object : IEntryPointProvider.AnalyzeTask {

            override val entries: Set<SootMethod> = methods
            override val name: String = "ComponentEntryProvider(entries=${methods.size})"

            override fun needConstructCallGraph(sootCtx: SootCtx) = sootCtx.constructCallGraph()
        })
    }

    override fun startAnalyse() = Unit
    override fun endAnalyse() = Unit

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
    }
}
