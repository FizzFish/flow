package cn.sast.framework.incremental

import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.incremental.ModifyInfoFactory
import cn.sast.graph.HashMutableDirectedGraph
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import soot.toolkits.graph.MutableDirectedGraph

@SourceDebugExtension(["SMAP\nIncrementalAnalyzeImplByChangeFiles.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IncrementalAnalyzeImplByChangeFiles.kt\ncn/sast/framework/incremental/DependsGraph\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,395:1\n381#2,7:396\n1317#3,2:403\n*S KotlinDebug\n*F\n+ 1 IncrementalAnalyzeImplByChangeFiles.kt\ncn/sast/framework/incremental/DependsGraph\n*L\n157#1:396,7\n168#1:403,2\n*E\n"])
class DependsGraph(factory: ModifyInfoFactory) : IncrementalAnalyzeByChangeFiles.IDependsGraph {
    override val factory: ModifyInfoFactory = factory
    private val dependenceGraph: MutableDirectedGraph<XDecl> = HashMutableDirectedGraph()
    private val patchRelateAnalysisTargets: MutableMap<String, MutableSet<XDecl>> = LinkedHashMap()
    private val patchRelateObjects: MutableSet<XDecl> = LinkedHashSet()
    private val patchRelateChangedWalk: MutableSet<XDecl> = LinkedHashSet()
    private var isOld: Boolean = false

    override infix fun Collection<XDecl>.dependsOn(deps: Collection<XDecl>) {
        for (n in this) {
            for (d in deps) {
                dependsOn(n, d)
            }
        }
    }

    override fun visitChangedDecl(diffPath: String, changed: Collection<XDecl>) {
        for (e in changed) {
            patchRelateAnalysisTargets.getOrPut(diffPath) { LinkedHashSet() }.add(e)
            patchRelateObjects.add(e)
        }
        patchRelateChangedWalk.clear()
        isOld = true
    }

    private fun walkALl() {
        if (isOld) {
            synchronized(this) {
                if (isOld) {
                    for (element in patchRelateObjects) {
                        patchRelateChangedWalk.add(element)
                    }
                    isOld = false
                }
            }
        }
    }

    override infix fun XDecl.dependsOn(dep: XDecl) {
        dependenceGraph.addEdge(this, dep)
        isOld = true
    }

    private fun walk(node: Collection<XDecl?>, forward: Boolean): Sequence<XDecl> {
        return sequence {
            TODO("FIXME — walk function implementation needs to be determined")
        }
    }

    override fun targetsRelate(targets: Collection<XDecl>): Sequence<XDecl> {
        return walk(targets, true) + walk(targets, false)
    }

    override fun toDecl(target: Any): XDecl {
        return factory.toDecl(target)
    }

    override fun targetRelate(target: XDecl): Sequence<XDecl> {
        return targetsRelate(listOf(target))
    }

    override fun shouldReAnalyzeDecl(target: XDecl): ScanAction {
        walkALl()
        val actionByFactory = factory.getScanAction(target)
        if (actionByFactory != ScanAction.Keep) {
            return actionByFactory
        }
        return if (patchRelateChangedWalk.contains(target)) ScanAction.Process else ScanAction.Skip
    }

    override fun shouldReAnalyzeTarget(target: Any): ScanAction {
        return shouldReAnalyzeDecl(factory.toDecl(target))
    }
}