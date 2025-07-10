package cn.sast.framework.entries.custom

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.component.ComponentEntryProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import soot.SootMethod

/**
 * First analyses the *custom* methods supplied by the user and afterwards the components
 * obtained from those methods.
 */
class HybridCustomThenComponent(
    private val ctx: SootCtx,
    private val customEntries: Set<SootMethod>
) : CustomEntryProvider(customEntries) {

    override val iterator: Flow<IEntryPointProvider.AnalyzeTask> = flow {

        // Phase 1 – user​‑supplied custom entries
        emitAll(super.iterator)

        // Phase 2 – components inferred from custom entries
        val componentProvider = ComponentEntryProvider(
            ctx,
            customEntries.map { it.signature }
        )

        emitAll(
            componentProvider.iterator.map { baseTask ->
                object : IEntryPointProvider.AnalyzeTask by baseTask {

                    fun getMethodsMustAnalyze(): Set<SootMethod> = customEntries
                    fun getName(): String =
                        "HybridCustomThenComponent(componentEntries=${customEntries.size})"
                }
            }
        )
    }
}