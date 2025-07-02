@file:SourceDebugExtension(["SMAP\nSparseInfoflowSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SparseInfoflowSolver.kt\ncn/sast/dataflow/infoflow/svfa/SparseInfoflowSolverKt\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,232:1\n381#2,7:233\n774#3:240\n865#3,2:241\n1557#3:243\n1628#3,3:244\n*S KotlinDebug\n*F\n+ 1 SparseInfoflowSolver.kt\ncn/sast/dataflow/infoflow/svfa/SparseInfoflowSolverKt\n*L\n77#1:233,7\n86#1:240\n86#1:241,2\n86#1:243\n86#1:244,3\n*E\n"])

package cn.sast.dataflow.infoflow.svfa

import java.lang.reflect.Field
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedList
import java.util.Queue
import java.util.function.Consumer
import kotlin.jvm.internal.SourceDebugExtension
import soot.SootMethod
import soot.Unit
import soot.jimple.infoflow.collect.MyConcurrentHashMap
import soot.jimple.infoflow.problems.AbstractInfoflowProblem
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG
import soot.util.queue.ChunkedQueue
import soot.util.queue.QueueReader

public final val activationUnitsToCallSites: MyConcurrentHashMap<Unit, Set<Unit>>
   public final get() {
      val field: Field = AbstractInfoflowProblem.class.getDeclaredField("activationUnitsToCallSites");
      field.setAccessible(true);
      val var10000: Any = field.get(`$this$activationUnitsToCallSites`);
      return var10000 as MyConcurrentHashMap<Unit, java.utilSet<Unit>>;
   }


public fun <M, N> BiDiInterproceduralCFG<N, M>.getGoThrough(from: N, to: N, skipNodes: Set<N> = SetsKt.emptySet()): MutableSet<N> {
   if (from == to) {
      return SetsKt.mutableSetOf(new Object[]{from});
   } else {
      val workList: Queue = new LinkedList();
      val startNode: RefCntUnit = new RefCntUnit<>(from, 1);
      workList.add(startNode);
      val set: HashMap = new HashMap();
      set.put(startNode, startNode);

      while (!workList.isEmpty()) {
         val `$this$map$iv`: RefCntUnit = workList.poll() as RefCntUnit;
         val `$i$f$map`: Any = `$this$map$iv`.getU();
         if (!skipNodes.contains(`$i$f$map`)) {
            for (Object succ : $this$getGoThrough.getSuccsOf(curNode)) {
               val `$i$f$mapTo`: RefCntUnit = new RefCntUnit<>(`destination$iv$iv`, 1);
               val `item$iv$iv`: java.util.Map = set;
               val var15: Any = set.get(`$i$f$mapTo`);
               val var10000: Any;
               if (var15 == null) {
                  `item$iv$iv`.put(`$i$f$mapTo`, `$i$f$mapTo`);
                  var10000 = `$i$f$mapTo`;
               } else {
                  var10000 = var15;
               }

               val next: RefCntUnit = var10000 as RefCntUnit;
               if (var10000 as RefCntUnit === `$i$f$mapTo` && !(`destination$iv$iv` == to)) {
                  workList.offer(next);
               }

               next.add(`$this$map$iv`);
            }
         }

         `$this$map$iv`.dec();
      }

      val var35: java.util.Collection = set.values();
      var var19: java.lang.Iterable = var35;
      var var23: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$filter$iv) {
         if ((var29 as RefCntUnit).getCnt() != 0) {
            var23.add(var29);
         }
      }

      var19 = var23 as java.util.List;
      var23 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var23 as java.util.List, 10));

      for (Object item$iv$ivx : $this$filter$iv) {
         var23.add((`item$iv$ivx` as RefCntUnit).getU());
      }

      return CollectionsKt.toMutableSet(var23 as java.util.List);
   }
}

@JvmSynthetic
fun `getGoThrough$default`(var0: BiDiInterproceduralCFG, var1: Any, var2: Any, var3: java.util.Set, var4: Int, var5: Any): java.util.Set {
   if ((var4 and 4) != 0) {
      var3 = SetsKt.emptySet();
   }

   return getGoThrough(var0, var1, var2, var3);
}

public fun getReachSet(icfg: BiDiInterproceduralCFG<Unit, SootMethod>, target: Unit): Set<Unit> {
   val reaches: ChunkedQueue = new ChunkedQueue();
   val reachSet: HashSet = new HashSet();
   reachSet.add(target);
   val reader: QueueReader = reaches.reader();
   reachSet.forEach(new Consumer(reaches) {
      {
         this.$reaches = `$reaches`;
      }

      public final void accept(Unit o) {
         this.$reaches.add(o);
      }
   });

   while (reader.hasNext()) {
      for (Unit s : icfg.getSuccsOf(reader.next())) {
         if (reachSet.add(s)) {
            reaches.add(s);
         }
      }
   }

   return reachSet;
}
