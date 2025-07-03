package cn.sast.dataflow.interprocedural.check

import java.util.ArrayList
import java.util.Collections
import java.util.IdentityHashMap
import java.util.LinkedList
import kotlin.jvm.internal.SourceDebugExtension
import soot.jimple.infoflow.data.Abstraction
import soot.toolkits.graph.DirectedGraph

@SourceDebugExtension(["SMAP\nAbstractionGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AbstractionGraph.kt\ncn/sast/dataflow/interprocedural/check/AbstractionGraph\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,239:1\n360#2,7:240\n360#2,7:247\n360#2,7:254\n*S KotlinDebug\n*F\n+ 1 AbstractionGraph.kt\ncn/sast/dataflow/interprocedural/check/AbstractionGraph\n*L\n60#1:240,7\n119#1:247,7\n123#1:254,7\n*E\n"])
abstract class AbstractionGraph : DirectedGraph<Abstraction> {
    val sink: Abstraction
    val absChain: ArrayList<Abstraction> = ArrayList()

    lateinit var unitToSuccs: IdentityHashMap<Abstraction, ArrayList<Abstraction>>
        internal set

    lateinit var unitToPreds: IdentityHashMap<Abstraction, ArrayList<Abstraction>>
        internal set

    lateinit var mHeads: ArrayList<Abstraction>
        internal set

    lateinit var mTails: ArrayList<Abstraction>
        internal set

    constructor(sink: Abstraction) {
        this.sink = sink
        val abstractionQueue = LinkedList<Abstraction>()
        abstractionQueue.add(this.sink)
        val set = Collections.newSetFromMap(IdentityHashMap<Abstraction, Boolean>())

        while (abstractionQueue.isNotEmpty()) {
            val abstraction = abstractionQueue.removeAt(0)
            absChain.add(abstraction)
            if (abstraction.sourceContext != null) {
                if (_Assertions.ENABLED && abstraction.predecessor != null) {
                    throw AssertionError("Assertion failed")
                }
            } else if (set.add(abstraction.predecessor)) {
                abstractionQueue.add(abstraction.predecessor)
            }

            abstraction.neighbors?.forEach { nb ->
                if (set.add(nb)) {
                    abstractionQueue.add(nb)
                }
            }
        }
    }

    fun buildHeadsAndTails() {
        mTails = ArrayList()
        mHeads = ArrayList()
        absChain.forEach { s ->
            val preds = unitToSuccs[s]
            if (preds == null || preds.isEmpty()) {
                mTails.add(s)
            }

            val var5 = unitToPreds[s]
            if (var5 == null || var5.isEmpty()) {
                mHeads.add(s)
            }
        }
    }

    private fun addEdge(
        currentAbs: Abstraction,
        target: Abstraction,
        successors: ArrayList<Abstraction>,
        unitToPreds: IdentityHashMap<Abstraction, ArrayList<Abstraction>>
    ) {
        if (target !in successors) {
            successors.add(target)
            val predList = unitToPreds.getOrPut(target) { ArrayList() }
            predList.add(currentAbs)
        }
    }

    protected open fun buildUnexceptionalEdges(
        unitToSuccs: IdentityHashMap<Abstraction, ArrayList<Abstraction>>,
        unitToPreds: IdentityHashMap<Abstraction, ArrayList<Abstraction>>
    ) {
        val unitIt = absChain.iterator()
        var nextAbs = if (unitIt.hasNext()) unitIt.next() else null

        while (nextAbs != null) {
            val currentAbs = nextAbs
            nextAbs = if (unitIt.hasNext()) unitIt.next() else null
            val successors = ArrayList<Abstraction>()
            if (currentAbs.predecessor != null) {
                val predecessor = currentAbs.predecessor
                addEdge(currentAbs, predecessor, successors, unitToPreds)
                currentAbs.predecessor.neighbors?.forEach { targetBox ->
                    addEdge(currentAbs, targetBox, successors, unitToPreds)
                }
            }

            if (successors.isNotEmpty()) {
                successors.trimToSize()
                unitToSuccs[currentAbs] = successors
            }
        }
    }

    open fun getHeads(): List<Abstraction> {
        return mHeads
    }

    open fun getTails(): List<Abstraction> {
        return mTails
    }

    open fun getPredsOf(s: Abstraction): List<Abstraction> {
        return unitToPreds[s] ?: emptyList()
    }

    open fun getSuccsOf(s: Abstraction): List<Abstraction> {
        return unitToSuccs[s] ?: emptyList()
    }

    open fun size(): Int {
        return absChain.size
    }

    override fun iterator(): MutableIterator<Abstraction> {
        return absChain.iterator()
    }

    fun isTail(abs: Abstraction): Boolean {
        return tails.any { it === abs }
    }

    fun isHead(abs: Abstraction): Boolean {
        return heads.any { it === abs }
    }
}