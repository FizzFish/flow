package cn.sast.dataflow.interprocedural.check

import cn.sast.api.report.BugPathEvent
import cn.sast.dataflow.interprocedural.check.printer.PathDiagnosticsGenerator
import cn.sast.graph.HashMutableDirectedGraph
import cn.sast.idfa.analysis.InterproceduralCFG
import com.feysh.corax.cache.analysis.SootInfoCache
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentList
import mu.KLogger
import soot.toolkits.graph.DirectedGraph

public class PathGeneratorImpl {
    public companion object {
        public val dataFlowResultPathOnlyStmt: Boolean = TODO("FIXME — initial value not provided")

        public fun getPathGenerator(): PathGenerator<IPath> {
            return object : PathGenerator<IPath>() {
                override fun getShouldExplore(`$this$shouldExplore`: IPath): Boolean {
                    return `$this$shouldExplore` !is UnknownPath
                }

                override fun getPreds(`$this$preds`: IPath): Collection<IPath> {
                    return when (`$this$preds`) {
                        is ModelBind -> listOf(`$this$preds`.prev)
                        is MergePath -> `$this$preds`.all
                        is AssignLocalPath -> listOf(`$this$preds`.rhsValePath)
                        is EntryPath -> emptyList()
                        is UnknownPath -> emptyList()
                        is LiteralPath -> emptyList()
                        is GetEdgePath -> listOf(`$this$preds`.valuePath)
                        is SetEdgePath -> listOf(`$this$preds`.valuePath)
                        is InvokeEdgePath -> `$this$preds`.interproceduralPathMap.keys
                        else -> throw NoWhenBranchMatchedException()
                    }
                }
            }
        }

        public fun generateEvents(info: SootInfoCache?, icfg: InterproceduralCFG, events: List<IPath>): Sequence<List<BugPathEvent>> {
            return SequencesKt.map(EventGenerator(info, icfg).gen(0, events), Companion::generateEvents$lambda$0)
        }

        @JvmStatic
        private fun generateEvents$lambda$0(it: List<Any>): List<Any> {
            return PathGeneratorKt.getRemoveAdjacentDuplicates(it)
        }

        @SourceDebugExtension(["SMAP\nPathGenerator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathGenerator.kt\ncn/sast/dataflow/interprocedural/check/PathGeneratorImpl$Companion$EventGenerator\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,213:1\n381#2,3:214\n384#2,4:219\n1317#3,2:217\n*S KotlinDebug\n*F\n+ 1 PathGenerator.kt\ncn/sast/dataflow/interprocedural/check/PathGeneratorImpl$Companion$EventGenerator\n*L\n172#1:214,3\n172#1:219,4\n174#1:217,2\n*E\n"])
        public class EventGenerator(info: SootInfoCache?, icfg: InterproceduralCFG) {
            public val info: SootInfoCache? = info
            public val icfg: InterproceduralCFG = icfg
            public var invokeCount: Int = 0
                internal set
            public val cache: MutableMap<CacheKey, Set<List<BugPathEvent>>> = LinkedHashMap()

            public fun gen(deep: Int, events: List<IPath>): Sequence<List<BugPathEvent>> {
                return SequencesKt.sequence { sequenceScope ->
                    val normalizedEvents = PathGeneratorKt.getRemoveAdjacentDuplicates(
                        events.filter { it !is EntryPath }
                    )
                    if (normalizedEvents.isEmpty()) return@sequence

                    val worklist = ArrayDeque<Pair<PersistentList<IPath>, Int>>()
                    worklist.add(ExtensionsKt.persistentListOf<IPath>() to 0)

                    while (true) {
                        val (pathEvents, index) = worklist.removeLastOrNull() ?: break
                        if (index >= normalizedEvents.size) {
                            val generatedEvents = PathGeneratorKt.getRemoveAdjacentDuplicates(
                                PathDiagnosticsGenerator(info, icfg, deep).inlineEvents(pathEvents)
                            if (generatedEvents.isNotEmpty()) {
                                yield(generatedEvents)
                            }
                        } else {
                            val nextIndex = index + 1
                            val currentPath = normalizedEvents[index]
                            val newPathEvents = pathEvents.add(currentPath)
                            
                            if (currentPath is InvokeEdgePath) {
                                val exitInvoke = ExitInvoke(currentPath)
                                val calleeEvents = diagnosticsCached(currentPath, deep + 1)
                                if (calleeEvents.isEmpty()) {
                                    worklist.add(newPathEvents to nextIndex)
                                } else {
                                    for (calleeEvent in calleeEvents) {
                                        if (calleeEvent.isEmpty()) {
                                            throw IllegalStateException("Check failed.")
                                        }
                                        worklist.add(
                                            if (nextIndex == normalizedEvents.size) {
                                                ExtensionsKt.plus(newPathEvents, calleeEvent) to nextIndex
                                            } else {
                                                ExtensionsKt.plus(newPathEvents, calleeEvent).add(exitInvoke) to nextIndex
                                            }
                                        )
                                    }
                                }
                            } else {
                                worklist.add(newPathEvents to nextIndex)
                            }
                        }
                    }
                }
            }

            private fun InvokeEdgePath.diagnosticsCached(deep: Int): Set<List<BugPathEvent>> {
                val key = CacheKey(this, deep)
                return cache.getOrPut(key) {
                    val set = LinkedHashSet<List<BugPathEvent>>()
                    diagnostics(this, deep).forEach { events ->
                        if (events.isNotEmpty()) {
                            set.add(events)
                        }
                    }
                    set
                }
            }

            private fun diagnostics(`$this$diagnostics`: InvokeEdgePath, deep: Int): Sequence<List<BugPathEvent>> {
                return SequencesKt.sequence { sequenceScope ->
                    if (invokeCount > 500) {
                        logger.error { "too much call edge of report path. limited!" }
                        return@sequence
                    }

                    val generator = getPathGenerator()
                    val directGraph = HashMutableDirectedGraph<IPath>()
                    val paths = generator.flush(directGraph, generator.getHeads(`$this$diagnostics`.path, directGraph))
                        .values
                        .toSet()

                    for (events in paths) {
                        if (events.firstOrNull() !is LiteralPath) {
                            yieldAll(gen(deep, events))
                        }
                    }
                }
            }

            public data class CacheKey(
                val callee: InvokeEdgePath,
                val deep: Int
            ) {
                override fun toString(): String = "CacheKey(callee=$callee, deep=$deep)"
            }

            public companion object {
                public val logger: KLogger = TODO("FIXME — logger initialization not provided")
            }
        }
    }
}