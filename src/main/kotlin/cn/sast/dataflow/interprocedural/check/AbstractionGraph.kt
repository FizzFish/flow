package cn.sast.dataflow.interprocedural.check

import java.util.ArrayList
import java.util.Collections
import java.util.IdentityHashMap
import java.util.LinkedList
import kotlin.jvm.internal.SourceDebugExtension
import soot.jimple.infoflow.data.Abstraction
import soot.toolkits.graph.DirectedGraph

@SourceDebugExtension(["SMAP\nAbstractionGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AbstractionGraph.kt\ncn/sast/dataflow/interprocedural/check/AbstractionGraph\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,239:1\n360#2,7:240\n360#2,7:247\n360#2,7:254\n*S KotlinDebug\n*F\n+ 1 AbstractionGraph.kt\ncn/sast/dataflow/interprocedural/check/AbstractionGraph\n*L\n60#1:240,7\n119#1:247,7\n123#1:254,7\n*E\n"])
public abstract class AbstractionGraph : DirectedGraph<Abstraction> {
   public final val sink: Abstraction
   public final val absChain: ArrayList<Abstraction>

   public final lateinit var unitToSuccs: IdentityHashMap<Abstraction, ArrayList<Abstraction>>
      internal set

   public final lateinit var unitToPreds: IdentityHashMap<Abstraction, ArrayList<Abstraction>>
      internal set

   public final lateinit var mHeads: ArrayList<Abstraction>
      internal set

   public final lateinit var mTails: ArrayList<Abstraction>
      internal set

   open fun AbstractionGraph(sink: Abstraction) {
      this.sink = sink;
      this.absChain = new ArrayList<>();
      val abstractionQueue: LinkedList = new LinkedList();
      abstractionQueue.add(this.sink);
      val set: java.util.Set = Collections.newSetFromMap(new IdentityHashMap());

      while (!abstractionQueue.isEmpty()) {
         val var10000: Any = abstractionQueue.remove(0);
         val abstraction: Abstraction = var10000 as Abstraction;
         this.absChain.add(var10000 as Abstraction);
         if (abstraction.getSourceContext() != null) {
            if (_Assertions.ENABLED && abstraction.getPredecessor() != null) {
               throw new AssertionError("Assertion failed");
            }
         } else if (set.add(abstraction.getPredecessor())) {
            abstractionQueue.add(abstraction.getPredecessor());
         }

         if (abstraction.getNeighbors() != null) {
            for (Abstraction nb : abstraction.getNeighbors()) {
               if (set.add(var8)) {
                  abstractionQueue.add(var8);
               }
            }
         }
      }
   }

   public fun buildHeadsAndTails() {
      this.setMTails(new ArrayList<>());
      this.setMHeads(new ArrayList<>());
      var var10000: java.util.Iterator = this.absChain.iterator();
      val var1: java.util.Iterator = var10000;

      while (var1.hasNext()) {
         var10000 = (java.util.Iterator)var1.next();
         val s: Abstraction = var10000 as Abstraction;
         val preds: java.util.Collection = this.getUnitToSuccs().get(var10000 as Abstraction);
         if (preds == null || preds.isEmpty()) {
            this.getMTails().add(s);
         }

         val var5: java.util.Collection = this.getUnitToPreds().get(s);
         if (var5 == null || var5.isEmpty()) {
            this.getMHeads().add(s);
         }
      }
   }

   private fun addEdge(
      currentAbs: Abstraction,
      target: Abstraction,
      successors: ArrayList<Abstraction>,
      unitToPreds: IdentityHashMap<Abstraction, ArrayList<Abstraction>>
   ) {
      val preds: java.util.List = successors;
      var `index$iv`: Int = 0;
      val var8: java.util.Iterator = preds.iterator();

      var var10000: Int;
      while (true) {
         if (!var8.hasNext()) {
            var10000 = -1;
            break;
         }

         if (var8.next() as Abstraction === target) {
            var10000 = `index$iv`;
            break;
         }

         `index$iv`++;
      }

      if (var10000 == -1) {
         successors.add(target);
         var var12: ArrayList = unitToPreds.get(target) as ArrayList;
         if (var12 == null) {
            var12 = new ArrayList();
            unitToPreds.put(target, var12);
         }

         var12.add(currentAbs);
      }
   }

   protected open fun buildUnexceptionalEdges(
      unitToSuccs: IdentityHashMap<Abstraction, ArrayList<Abstraction>>,
      unitToPreds: IdentityHashMap<Abstraction, ArrayList<Abstraction>>
   ) {
      val var10000: java.util.Iterator = this.absChain.iterator();
      val unitIt: java.util.Iterator = var10000;
      var nextAbs: Abstraction = if (var10000.hasNext()) var10000.next() as Abstraction else null;

      while (nextAbs != null) {
         val currentAbs: Abstraction = nextAbs;
         nextAbs = if (unitIt.hasNext()) unitIt.next() as Abstraction else null;
         val successors: ArrayList = new ArrayList();
         if (currentAbs.getPredecessor() != null) {
            val var10002: Abstraction = currentAbs.getPredecessor();
            this.addEdge(currentAbs, var10002, successors, unitToPreds);
            if (currentAbs.getPredecessor().getNeighbors() != null) {
               val it: java.util.Set;
               for (Abstraction targetBox : it) {
                  this.addEdge(currentAbs, targetBox, successors, unitToPreds);
               }
            }
         }

         if (!successors.isEmpty()) {
            successors.trimToSize();
            unitToSuccs.put(currentAbs, successors);
         }
      }
   }

   public open fun getHeads(): List<Abstraction> {
      return this.getMHeads();
   }

   public open fun getTails(): List<Abstraction> {
      return this.getMTails();
   }

   public open fun getPredsOf(s: Abstraction): List<Abstraction> {
      val var10000: ArrayList = this.getUnitToPreds().get(s);
      return (java.util.List<Abstraction>)(if (var10000 != null) var10000 else CollectionsKt.emptyList());
   }

   public open fun getSuccsOf(s: Abstraction): List<Abstraction> {
      val var10000: ArrayList = this.getUnitToSuccs().get(s);
      return (java.util.List<Abstraction>)(if (var10000 != null) var10000 else CollectionsKt.emptyList());
   }

   public open fun size(): Int {
      return this.absChain.size();
   }

   public open operator fun iterator(): MutableIterator<Abstraction> {
      val var10000: java.util.Iterator = this.absChain.iterator();
      return var10000;
   }

   public fun isTail(abs: Abstraction): Boolean {
      val `$this$indexOfFirst$iv`: java.util.List = this.getTails();
      var `index$iv`: Int = 0;
      val var5: java.util.Iterator = `$this$indexOfFirst$iv`.iterator();

      var var10000: Int;
      while (true) {
         if (!var5.hasNext()) {
            var10000 = -1;
            break;
         }

         if (var5.next() as Abstraction === abs) {
            var10000 = `index$iv`;
            break;
         }

         `index$iv`++;
      }

      return var10000 != -1;
   }

   public fun isHead(abs: Abstraction): Boolean {
      val `$this$indexOfFirst$iv`: java.util.List = this.getHeads();
      var `index$iv`: Int = 0;
      val var5: java.util.Iterator = `$this$indexOfFirst$iv`.iterator();

      var var10000: Int;
      while (true) {
         if (!var5.hasNext()) {
            var10000 = -1;
            break;
         }

         if (var5.next() as Abstraction === abs) {
            var10000 = `index$iv`;
            break;
         }

         `index$iv`++;
      }

      return var10000 != -1;
   }
}
