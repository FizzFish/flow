package cn.sast.framework.incremental

import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.incremental.ModifyInfoFactory
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import java.util.ArrayDeque
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.sequences.Sequence
import kotlin.sequences.plus
import kotlin.sequences.sequence
import soot.toolkits.graph.MutableDirectedGraph
import cn.sast.graph.HashMutableDirectedGraph

/**
 * A *bidirectional* dependency graph used by the incremental‑analysis pipeline.
 *
 * Every edge `A → B` means *A depends on B* (i.e. if **B** is changed then **A** must be re‑analysed).
 * The class also keeps track of *changed* declarations coming from the current patch so that we
 * can quickly answer *shouldReAnalyse?* queries.
 */
class DependsGraph(
    override val factory: ModifyInfoFactory,
) : IncrementalAnalyzeByChangeFiles.IDependsGraph {

    // -------------------------------------------------------------------------
    //  Internal state
    // -------------------------------------------------------------------------
    private val graph: MutableDirectedGraph<XDecl> = HashMutableDirectedGraph()
    private val patchRelateAnalysisTargets: MutableMap<String, MutableSet<XDecl>> = LinkedHashMap()
    private val patchRelateObjects: MutableSet<XDecl> = LinkedHashSet()
    private val patchRelateChangedWalk: MutableSet<XDecl> = LinkedHashSet()

    /** Whenever a new edge/change is added we mark the internal *walk cache* dirty. */
    @Volatile private var dirty = false

    // -------------------------------------------------------------------------
    //  IncrementalAnalyzeByChangeFiles.IDependsGraph – graph construction helpers
    // -------------------------------------------------------------------------
    override fun Collection<XDecl>.dependsOn(deps: Collection<XDecl>) {
        for (n in this) for (d in deps) n.dependsOn(d)
    }

    override infix fun XDecl.dependsOn(dep: XDecl) {
        graph.addEdge(this, dep)
        dirty = true
    }

    override fun visitChangedDecl(diffPath: String, changed: Collection<XDecl>) {
        val bucket = patchRelateAnalysisTargets.getOrPut(diffPath) { LinkedHashSet() }
        bucket += changed
        patchRelateObjects += changed
        patchRelateChangedWalk.clear()
        dirty = true
    }

    // -------------------------------------------------------------------------
    //  Internal helpers – BFS walk & cache
    // -------------------------------------------------------------------------
    private fun ensureWalkCache() {
        if (!dirty) return
        synchronized(this) {
            if (!dirty) return
            patchRelateChangedWalk.clear()
            patchRelateChangedWalk += patchRelateObjects
            val q: ArrayDeque<XDecl> = ArrayDeque(patchRelateObjects)
            while (q.isNotEmpty()) {
                val cur = q.removeFirst()
                // forward deps (cur → *)
                graph.getSuccsOf(cur)?.forEach {
                    if (patchRelateChangedWalk.add(it)) q.add(it)
                }
                // backward deps (* → cur)
                graph.getPredsOf(cur)?.forEach {
                    if (patchRelateChangedWalk.add(it)) q.add(it)
                }
            }
            dirty = false
        }
    }

    private fun walk(nodes: Collection<XDecl>, forward: Boolean): Sequence<XDecl> = sequence {
        val seen = HashSet<XDecl>()
        val q: ArrayDeque<XDecl> = ArrayDeque(nodes)
        while (q.isNotEmpty()) {
            val cur = q.removeFirst()
            if (!seen.add(cur)) continue
            yield(cur)
            val next = if (forward) graph.getSuccsOf(cur) else graph.getPredsOf(cur)
            next?.forEach { q.add(it) }
        }
    }

    // -------------------------------------------------------------------------
    //  IncrementalAnalyzeByChangeFiles.IDependsGraph – queries
    // -------------------------------------------------------------------------
    override fun targetsRelate(targets: Collection<XDecl>): Sequence<XDecl> =
        walk(targets, true) + walk(targets, false)

    override fun targetRelate(target: XDecl): Sequence<XDecl> = targetsRelate(listOf(target))

    override fun shouldReAnalyzeDecl(target: XDecl): ScanAction {
        ensureWalkCache()
        // 1️⃣ Let factory decide first (e.g. dummy methods)
        val factoryAction = factory.getScanAction(target)
        if (factoryAction != ScanAction.Keep) return factoryAction
        // 2️⃣ If the node is reachable from *any* patched decl => re‑analyse
        return if (patchRelateChangedWalk.contains(target)) ScanAction.Process else ScanAction.Skip
    }

    override fun shouldReAnalyzeTarget(target: Any): ScanAction =
        shouldReAnalyzeDecl(factory.toDecl(target))

    // -------------------------------------------------------------------------
    //  Simple delegates
    // -------------------------------------------------------------------------
    override fun toDecl(target: Any): XDecl = factory.toDecl(target)
    fun getFactory(): ModifyInfoFactory = factory
}