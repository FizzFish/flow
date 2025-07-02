package cn.sast.dataflow.infoflow.svfa

import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentSet
import soot.IdentityUnit
import soot.Trap
import soot.Unit
import soot.Value
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.graph.ExceptionalGraph
import soot.toolkits.graph.ExceptionalGraph.ExceptionDest
import soot.toolkits.scalar.FlowAnalysis.Flow

@SourceDebugExtension(["SMAP\nSparsePropgrateAnalyze.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SparsePropgrateAnalyze.kt\ncn/sast/dataflow/infoflow/svfa/FlowAnalysisOp\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,420:1\n1863#2,2:421\n1863#2,2:423\n*S KotlinDebug\n*F\n+ 1 SparsePropgrateAnalyze.kt\ncn/sast/dataflow/infoflow/svfa/FlowAnalysisOp\n*L\n130#1:421,2\n133#1:423,2\n*E\n"])
internal object FlowAnalysisOp {
   public fun mergeInto(succNode: Unit, inout: FlowFact, in1: FlowFact) {
      val fx: Function1 = FlowAnalysisOp::mergeInto$lambda$0;

      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         fx.invoke(`element$iv` as Value);
      }

      for (Object element$iv : $this$forEach$iv) {
         fx.invoke(var14 as Value);
      }
   }

   public fun getFlow(graph: DirectedGraph<Unit>, from: Unit, to: Unit): Flow {
      if (to is IdentityUnit && graph is ExceptionalGraph) {
         val g: ExceptionalGraph = graph as ExceptionalGraph;
         val var10000: java.util.List = (graph as ExceptionalGraph).getExceptionalPredsOf(to);
         if (!var10000.isEmpty()) {
            for (ExceptionDest exd : g.getExceptionDests(from)) {
               val trap: Trap = exd.getTrap();
               if (trap != null && trap.getHandlerUnit() === to) {
                  return Flow.IN;
               }
            }
         }
      }

      return Flow.OUT;
   }

   @JvmStatic
   fun `mergeInto$lambda$0`(`$inout`: FlowFact, `$in1`: FlowFact, k: Value): kotlin.Unit {
      var var10000: PersistentSet = `$inout`.getData().get(k) as PersistentSet;
      if (var10000 == null) {
         var10000 = ExtensionsKt.persistentHashSetOf();
      }

      val var10001: PersistentSet = `$in1`.getData().get(k) as PersistentSet;
      `$inout`.setData(
         `$inout`.getData()
            .put(
               k,
               ExtensionsKt.plus(var10000, if (var10001 != null) var10001 as java.lang.Iterable else ExtensionsKt.persistentHashSetOf() as java.lang.Iterable)
            )
      );
      return kotlin.Unit.INSTANCE;
   }
}
