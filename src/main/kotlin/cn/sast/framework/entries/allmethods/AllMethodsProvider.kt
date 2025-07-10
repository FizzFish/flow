package cn.sast.framework.entries.allmethods

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import soot.Scene
import soot.SootClass
import soot.SootMethod

/**
 * Provides every application​‑class method in the scene as an entry​‑point.
 *
 * @param classes Classes to take the methods from. Defaults to `Scene.v().applicationClasses`.
 */
class AllMethodsProvider(
    private val classes: Collection<SootClass> = Scene.v().applicationClasses
) : IEntryPointProvider {

    override val iterator: Flow<IEntryPointProvider.AnalyzeTask> = flow {
        val entries: Set<SootMethod> = classes
            .filter { it.isInScene }
            .flatMap { it.methods }
            .toSet()

        emit(object : IEntryPointProvider.AnalyzeTask {

            override val entries: Set<SootMethod> = entries
            override val name: String = "AllMethodsProvider(entries=${entries.size})"

            override fun needConstructCallGraph(sootCtx: SootCtx) = sootCtx.constructCallGraph()
        })
    }

    override fun startAnalyse() = Unit
    override fun endAnalyse() = Unit
}