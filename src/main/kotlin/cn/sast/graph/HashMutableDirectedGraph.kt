package cn.sast.graph

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedHashSet
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor
import org.slf4j.Logger
import soot.toolkits.graph.MutableDirectedGraph

@Serializable
@SourceDebugExtension(["SMAP\nHashMutableDirectedGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HashMutableDirectedGraph.kt\ncn/sast/graph/HashMutableDirectedGraph\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,220:1\n381#2,7:221\n381#2,7:228\n381#2,7:235\n381#2,7:242\n*S KotlinDebug\n*F\n+ 1 HashMutableDirectedGraph.kt\ncn/sast/graph/HashMutableDirectedGraph\n*L\n106#1:221,7\n110#1:228,7\n171#1:235,7\n172#1:242,7\n*E\n"])
public class HashMutableDirectedGraph<N> : MutableDirectedGraph<N>, Cloneable {
   private final val nodeToPreds: MutableMap<Any, MutableSet<Any>>
   private final val nodeToSuccs: MutableMap<Any, MutableSet<Any>>
   private final val heads: MutableSet<Any>
   private final val tails: MutableSet<Any>

   public constructor()  {
      this.nodeToPreds = new HashMap<>();
      this.nodeToSuccs = new HashMap<>();
      this.heads = new HashSet<>();
      this.tails = new HashSet<>();
   }

   public constructor(orig: HashMutableDirectedGraph<Any>)  {
      this.nodeToPreds = HashMutableDirectedGraph.Companion.access$deepCopy(Companion, orig.nodeToPreds);
      this.nodeToSuccs = HashMutableDirectedGraph.Companion.access$deepCopy(Companion, orig.nodeToSuccs);
      this.heads = new HashSet<>(orig.heads);
      this.tails = new HashSet<>(orig.tails);
   }

   public override fun clone(): Any {
      return new HashMutableDirectedGraph<>(this);
   }

   public fun clearAll() {
      this.nodeToPreds.clear();
      this.nodeToSuccs.clear();
      this.heads.clear();
      this.tails.clear();
   }

   public open fun getHeads(): List<Any> {
      return HashMutableDirectedGraph.Companion.access$getCopy(Companion, this.heads);
   }

   public open fun getTails(): List<Any> {
      return HashMutableDirectedGraph.Companion.access$getCopy(Companion, this.tails);
   }

   public open fun getPredsOf(s: Any): List<Any> {
      val preds: java.util.Set = this.nodeToPreds.get(s);
      return if (preds != null) HashMutableDirectedGraph.Companion.access$getCopy(Companion, preds) else CollectionsKt.emptyList();
   }

   public fun getPredsOfAsSet(s: Any): Set<Any> {
      val preds: java.util.Set = this.nodeToPreds.get(s);
      if (preds != null) {
         val var10000: java.util.Set = Collections.unmodifiableSet(preds);
         return var10000;
      } else {
         return SetsKt.emptySet();
      }
   }

   public open fun getSuccsOf(s: Any): List<Any> {
      val succs: java.util.Set = this.nodeToSuccs.get(s);
      return if (succs != null) HashMutableDirectedGraph.Companion.access$getCopy(Companion, succs) else CollectionsKt.emptyList();
   }

   public fun getSuccsOfAsSet(s: Any): Set<Any> {
      val succs: java.util.Set = this.nodeToSuccs.get(s);
      if (succs != null) {
         val var10000: java.util.Set = Collections.unmodifiableSet(succs);
         return var10000;
      } else {
         return SetsKt.emptySet();
      }
   }

   public open fun size(): Int {
      return this.nodeToPreds.keySet().size();
   }

   public open operator fun iterator(): MutableIterator<Any> {
      return this.nodeToPreds.keySet().iterator();
   }

   public open fun addEdge(from: Any, to: Any) {
      if (!this.containsEdge(from, to)) {
         val predsList: java.util.Map = this.nodeToSuccs;
         val `$i$f$getOrPut`: Any = this.nodeToSuccs.get(from);
         var var10000: Any;
         if (`$i$f$getOrPut` == null) {
            this.heads.add((N)from);
            val var8: Any = new LinkedHashSet();
            predsList.put(from, var8);
            var10000 = var8;
         } else {
            var10000 = `$i$f$getOrPut`;
         }

         val succsList: java.util.Set = var10000 as java.util.Set;
         val `$this$getOrPut$ivx`: java.util.Map = this.nodeToPreds;
         val `value$ivx`: Any = this.nodeToPreds.get(to);
         if (`value$ivx` == null) {
            this.tails.add((N)to);
            val `answer$iv`: Any = new LinkedHashSet();
            `$this$getOrPut$ivx`.put(to, `answer$iv`);
            var10000 = `answer$iv`;
         } else {
            var10000 = `value$ivx`;
         }

         val var10: java.util.Set = var10000 as java.util.Set;
         this.heads.remove(to);
         this.tails.remove(from);
         succsList.add(to);
         var10.add(from);
      }
   }

   public open fun removeEdge(from: Any, to: Any) {
      val succs: java.util.Set = this.nodeToSuccs.get(from);
      if (succs != null && succs.contains(to)) {
         val var10000: java.util.Set = this.nodeToPreds.get(to);
         if (var10000 == null) {
            throw new RuntimeException("$to not in graph!");
         } else {
            succs.remove(to);
            var10000.remove(from);
            if (succs.isEmpty()) {
               this.tails.add((N)from);
               this.nodeToSuccs.remove(from);
            }

            if (var10000.isEmpty()) {
               this.heads.add((N)to);
               this.nodeToPreds.remove(to);
            }

            this.removeSingle((N)from);
            this.removeSingle((N)to);
         }
      }
   }

   private fun removeSingle(n: Any) {
      val succs: java.util.Set = this.nodeToSuccs.get(n);
      val preds: java.util.Set = this.nodeToPreds.get(n);
      if ((succs == null || succs.isEmpty()) && this.heads.contains(n)) {
         this.heads.remove(n);
      }

      if ((preds == null || preds.isEmpty()) && this.tails.contains(n)) {
         this.tails.remove(n);
      }
   }

   public open fun containsEdge(from: Any, to: Any): Boolean {
      val succs: java.util.Set = this.nodeToSuccs.get(from);
      return succs != null && succs.contains(to);
   }

   public open fun containsNode(node: Any): Boolean {
      return this.nodeToPreds.keySet().contains(node);
   }

   public open fun getNodes(): List<Any> {
      return HashMutableDirectedGraph.Companion.access$getCopy(Companion, this.nodeToPreds.keySet());
   }

   public open fun addNode(node: Any) {
      if (!this.containsNode(node)) {
         var `$this$getOrPut$iv`: java.util.Map = this.nodeToSuccs;
         if (this.nodeToSuccs.get(node) == null) {
            `$this$getOrPut$iv`.put(node, new LinkedHashSet());
         }

         `$this$getOrPut$iv` = this.nodeToPreds;
         if (this.nodeToPreds.get(node) == null) {
            `$this$getOrPut$iv`.put(node, new LinkedHashSet());
         }

         this.heads.add((N)node);
         this.tails.add((N)node);
      }
   }

   public open fun removeNode(node: Any) {
      var var10000: java.util.Iterator = new ArrayList<>(this.nodeToSuccs.get(node)).iterator();
      var var2: java.util.Iterator = var10000;

      while (var2.hasNext()) {
         this.removeEdge((N)node, (N)var2.next());
      }

      this.nodeToSuccs.remove(node);
      var10000 = new ArrayList<>(this.nodeToPreds.get(node)).iterator();
      var2 = var10000;

      while (var2.hasNext()) {
         this.removeEdge((N)var2.next(), (N)node);
      }

      this.nodeToPreds.remove(node);
      this.heads.remove(node);
      this.tails.remove(node);
   }

   public fun printGraph() {
      for (Object node : this) {
         logger.debug("Node = $node");
         logger.debug("Preds:");

         for (Object p : this.getPredsOf((N)node)) {
            logger.debug("     ");
            logger.debug("$s");
         }

         logger.debug("Succs:");

         for (Object s : this.getSuccsOf((N)node)) {
            logger.debug("     ");
            logger.debug("$var6");
         }
      }
   }

   @JvmStatic
   fun {
      val var0: PluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("cn.sast.graph.HashMutableDirectedGraph", null, 4);
      var0.addElement("nodeToPreds", false);
      var0.addElement("nodeToSuccs", false);
      var0.addElement("heads", false);
      var0.addElement("tails", false);
      $cachedDescriptor = var0 as SerialDescriptor;
   }

   public companion object {
      private final val logger: Logger

      private fun <T> getCopy(c: Collection<T>): List<T> {
         val var10000: java.util.List = Collections.unmodifiableList(new ArrayList(c));
         return var10000;
      }

      private fun <A, B> deepCopy(`in`: Map<A, MutableSet<B>>): MutableMap<A, MutableSet<B>> {
         val retVal: HashMap = new HashMap(`in`);

         for (Object var10000 : retVal.entrySet()) {
            (var10000 as Entry).setValue(new LinkedHashSet((var10000 as Entry).getValue() as java.util.Collection));
         }

         return retVal;
      }

      public fun <N> serializer(typeSerial0: KSerializer<N>): KSerializer<HashMutableDirectedGraph<N>> {
         return (typeSerial0.new $serializer<HashMutableDirectedGraph<N>>(typeSerial0)) as KSerializer<HashMutableDirectedGraph<N>>;
      }
   }
}
