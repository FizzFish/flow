package cn.sast.framework.entries.custom

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.component.ComponentEntryProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import soot.SootMethod

/**
 * ⚠️  Re​‑created stub because the original class could not be decompiled.
 *     Replace the `UnReachableEntryProvider` reference once you can supply its source.
 */
class HybridUnReachThenComponent(
    private val ctx: SootCtx,
    private val unreachableEntries: Set<SootMethod>
) : IEntryPointProvider {

    // TODO: Provide the real implementation of UnReachableEntryProvider
    private val unreachableProvider /* = UnReachableEntryProvider(ctx, unreachableEntries) */: IEntryPointProvider =
        EmptyEntryProvider

    private val componentProvider =
        ComponentEntryProvider(ctx, unreachableEntries.map { it.signature })

    override val iterator: Flow<IEntryPointProvider.AnalyzeTask> = flow {
        emitAll(unreachableProvider.iterator)
        emitAll(componentProvider.iterator)
    }

    override fun startAnalyse() = Unit
    override fun endAnalyse() = Unit
}
