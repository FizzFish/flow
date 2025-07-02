package cn.sast.dataflow.infoflow.svfa

import java.util.LinkedHashMap
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentMap
import soot.Unit
import soot.Value
import soot.jimple.ReturnStmt
import soot.jimple.ReturnVoidStmt
import soot.jimple.Stmt
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.scalar.BackwardFlowAnalysis
import soot.toolkits.scalar.FlowAnalysis.Flow

@SourceDebugExtension(["SMAP\nSparsePropgrateAnalyze.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SparsePropgrateAnalyze.kt\ncn/sast/dataflow/infoflow/svfa/FlowAssignment\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 extensions.kt\nkotlinx/collections/immutable/ExtensionsKt\n*L\n1#1,420:1\n1863#2:421\n1864#2:424\n1279#2,2:425\n1293#2,4:427\n327#3:422\n327#3:423\n362#3:431\n*S KotlinDebug\n*F\n+ 1 SparsePropgrateAnalyze.kt\ncn/sast/dataflow/infoflow/svfa/FlowAssignment\n*L\n173#1:421\n173#1:424\n187#1:425,2\n187#1:427,4\n178#1:422\n181#1:423\n187#1:431\n*E\n"])
private class FlowAssignment(graph: DirectedGraph<Unit>, paramAndThis: MutableSet<Value>, unit2locals: Map<Stmt, MutableSet<Pair<AP, ValueLocation>>>) : BackwardFlowAnalysis(
      graph
   ) {
   public final val paramAndThis: MutableSet<Value>
   public final val unit2locals: Map<Stmt, MutableSet<Pair<AP, ValueLocation>>>

   init {
      this.paramAndThis = paramAndThis;
      this.unit2locals = unit2locals;
      this.doAnalysis();
   }

   protected open fun omissible(u: Unit): Boolean {
      return false;
   }

   protected open fun flowThrough(infact: FlowFact, unit: Unit, out: FlowFact) {
      this.copy(infact, out);
      if (unit is Stmt) {
         if (this.unit2locals.get(unit) as java.util.Set != null) {
            var var21: PersistentMap = out.getData();

            val `map$iv`: java.lang.Iterable;
            for (Object element$iv : map$iv) {
               val `$i$f$associateWithTo`: Pair = `$this$associateWithTo$iv$iv` as Pair;
               val `element$iv$iv`: AP = (`$this$associateWithTo$iv$iv` as Pair).component1() as AP;
               if (SparsePropgrateAnalyzeKt.isLeft(`$i$f$associateWithTo`.component2() as ValueLocation)) {
                  if (`element$iv$iv`.getField() == null) {
                     var21 = ExtensionsKt.minus(var21, `element$iv$iv`.getValue());
                  } else {
                     val var32: Pair = TuplesKt.to(
                        `element$iv$iv`.getValue(), ExtensionsKt.persistentHashSetOf(new VFNode[]{new VFNode(`element$iv$iv`.getValue(), unit)})
                     );
                     var21 = var21.put(var32.getFirst(), var32.getSecond());
                  }
               } else {
                  val var34: Pair = TuplesKt.to(
                     `element$iv$iv`.getValue(), ExtensionsKt.persistentHashSetOf(new VFNode[]{new VFNode(`element$iv$iv`.getValue(), unit)})
                  );
                  var21 = var21.put(var34.getFirst(), var34.getSecond());
               }
            }

            out.setData(var21);
         }

         if (unit is ReturnVoidStmt || unit is ReturnStmt) {
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

   protected open fun copy(source: FlowFact, dest: FlowFact) {
      if (dest != source) {
         dest.setData(source.getData());
      }
   }

   protected open fun newInitialFlow(): FlowFact {
      return new FlowFact();
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
      throw new UnsupportedOperationException("FlowAssignment.merge should never be called");
   }

   public fun getBefore(): Map<Unit, FlowFact> {
      val var10000: java.util.Map = this.unitToBeforeFlow;
      return var10000;
   }
}
