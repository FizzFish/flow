package cn.sast.dataflow.infoflow.svfa

import java.util.LinkedHashMap
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentMap
import soot.Unit
import soot.Value
import soot.jimple.Stmt
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.scalar.ForwardFlowAnalysis
import soot.toolkits.scalar.FlowAnalysis.Flow

@SourceDebugExtension(["SMAP\nSparsePropgrateAnalyze.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SparsePropgrateAnalyze.kt\ncn/sast/dataflow/infoflow/svfa/BackAssignment\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 extensions.kt\nkotlinx/collections/immutable/ExtensionsKt\n*L\n1#1,420:1\n1863#2:421\n1864#2:423\n1279#2,2:424\n1293#2,4:426\n327#3:422\n362#3:430\n*S KotlinDebug\n*F\n+ 1 SparsePropgrateAnalyze.kt\ncn/sast/dataflow/infoflow/svfa/BackAssignment\n*L\n265#1:421\n265#1:423\n271#1:424,2\n271#1:426,4\n266#1:422\n271#1:430\n*E\n"])
private class BackAssignment(graph: DirectedGraph<Unit>, paramAndThis: MutableSet<Value>, unit2locals: Map<Stmt, MutableSet<Pair<AP, ValueLocation>>>) : ForwardFlowAnalysis(
      graph
   ) {
   public final val paramAndThis: MutableSet<Value>
   public final val unit2locals: Map<Stmt, MutableSet<Pair<AP, ValueLocation>>>

   init {
      this.paramAndThis = paramAndThis;
      this.unit2locals = unit2locals;
      this.doAnalysis();
   }

   protected open fun newInitialFlow(): FlowFact {
      return new FlowFact();
   }

   protected open fun omissible(u: Unit): Boolean {
      return false;
   }

   protected open fun copy(source: FlowFact, dest: FlowFact) {
      if (dest != source) {
         dest.setData(source.getData());
      }
   }

   protected open fun getFlow(from: Unit, to: Unit): Flow {
      val var10000: FlowAnalysisOp = FlowAnalysisOp.INSTANCE;
      val var10001: DirectedGraph = this.graph;
      return var10000.getFlow(var10001, from, to);
   }

   protected open fun mergeInto(succNode: Unit, inout: FlowFact, in1: FlowFact) {
      FlowAnalysisOp.INSTANCE.mergeInto(succNode, inout, in1);
   }

   protected open fun merge(in1: FlowFact, in2: FlowFact, out: FlowFact) {
      throw new UnsupportedOperationException("BackAssignment.merge should never be called");
   }

   protected open fun flowThrough(infact: FlowFact, unit: Unit, out: FlowFact) {
      this.copy(infact, out);
      if (unit is Stmt) {
         if (this.unit2locals.get(unit) as java.util.Set != null) {
            var var21: PersistentMap = out.getData();

            val `map$iv`: java.lang.Iterable;
            for (Object element$iv : map$iv) {
               val `element$iv$iv`: AP = (`$this$associateWithTo$iv$iv` as Pair).component1() as AP;
               val it: ValueLocation = (`$this$associateWithTo$iv$iv` as Pair).component2() as ValueLocation;
               val var32: Pair = TuplesKt.to(
                  `element$iv$iv`.getValue(), ExtensionsKt.persistentHashSetOf(new VFNode[]{new VFNode(`element$iv$iv`.getValue(), unit)})
               );
               var21 = var21.put(var32.getFirst(), var32.getSecond());
            }

            out.setData(var21);
         }

         if (this.graph.getHeads().contains(unit)) {
            val var22: PersistentMap = out.getData();
            val var23: java.lang.Iterable = this.paramAndThis;
            val var27: LinkedHashMap = new LinkedHashMap(
               RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(this.paramAndThis, 10)), 16)
            );

            for (Object element$iv$iv : var23) {
               var27.put(var30, ExtensionsKt.persistentHashSetOf(new VFNode[]{new VFNode(var30 as Value, unit)}));
            }

            out.setData(ExtensionsKt.putAll(var22, ExtensionsKt.toPersistentMap(var27) as java.util.Map));
         }
      }
   }

   public fun getAfter(): Map<Unit, FlowFact> {
      val var10000: java.util.Map = this.unitToAfterFlow;
      return var10000;
   }
}
