package cn.sast.dataflow.callgraph

import cn.sast.common.OS
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import soot.MethodOrMethodContext
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.Unit
import soot.UnitPatchingChain
import soot.jimple.FieldRef
import soot.jimple.StaticFieldRef
import soot.jimple.Stmt
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Targets
import soot.util.queue.ChunkedQueue
import soot.util.queue.QueueReader

@SourceDebugExtension(["SMAP\nReachableStmtSequence.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ReachableStmtSequence.kt\ncn/sast/dataflow/callgraph/StaticFiledAccessCache\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,113:1\n381#2,7:114\n*S KotlinDebug\n*F\n+ 1 ReachableStmtSequence.kt\ncn/sast/dataflow/callgraph/StaticFiledAccessCache\n*L\n94#1:114,7\n*E\n"])
public class StaticFiledAccessCache(cg: CallGraph) {
   public final val cg: CallGraph

   private final val initialCapacity: Int
      private final get() {
         return Math.max(Scene.v().getClasses().size() * 10, Scene.v().getClasses().size() + 1000);
      }


   private final val cacheBuilder: CacheBuilder<Any, Any>
   private final val cache: LoadingCache<Pair<SootMethod, SootMethod>, Boolean>

   private final val staticFieldRefToSootMethod: MutableMap<StaticFieldRef, MutableSet<SootMethod>>
      private final get() {
         return this.staticFieldRefToSootMethod$delegate.getValue() as MutableMap<StaticFieldRef, MutableSet<SootMethod>>;
      }


   init {
      this.cg = cg;
      this.cacheBuilder = CacheBuilder.newBuilder()
         .concurrencyLevel(OS.INSTANCE.getMaxThreadNum())
         .initialCapacity(this.getInitialCapacity())
         .maximumSize((long)(this.getInitialCapacity() * 2))
         .softValues();
      this.cache = this.cacheBuilder.build(new CacheLoader<Pair<? extends SootMethod, ? extends SootMethod>, java.lang.Boolean>(this) {
         {
            this.this$0 = `$receiver`;
         }

         public java.lang.Boolean load(Pair<? extends SootMethod, ? extends SootMethod> key) throws Exception {
            val callGraph: CallGraph = this.this$0.getCg();
            val src: SootMethod = key.getFirst() as SootMethod;
            val reaches: ChunkedQueue = new ChunkedQueue();
            val reachSet: HashSet = new HashSet();
            reachSet.add(src);
            val reader: QueueReader = reaches.reader();

            val cur: java.lang.Iterable;
            for (Object element$iv : cur) {
               reaches.add(`element$iv` as SootMethod);
            }

            while (reader.hasNext()) {
               val var14: Targets = new Targets(callGraph.edgesOutOf((reader.next() as SootMethod) as MethodOrMethodContext));

               while (edgeIt.hasNext()) {
                  val var10000: MethodOrMethodContext = var14.next();
                  val var15: SootMethod = var10000 as SootMethod;
                  if (reachSet.add(var10000 as SootMethod)) {
                     reaches.add(var15);
                     if (var15 == key.getSecond()) {
                        return true;
                     }
                  }
               }
            }

            return false;
         }
      });
      this.staticFieldRefToSootMethod$delegate = LazyKt.lazy(StaticFiledAccessCache::staticFieldRefToSootMethod_delegate$lambda$1);
   }

   public fun isAccessible(entry: SootMethod, fieldRef: StaticFieldRef): Boolean {
      val var10000: java.util.Set = this.getStaticFieldRefToSootMethod().get(fieldRef);
      if (var10000 == null) {
         return false;
      } else {
         for (SootMethod sm : var10000) {
            if (this.cache.get(TuplesKt.to(entry, sm)) as java.lang.Boolean) {
               return true;
            }
         }

         return false;
      }
   }

   @JvmStatic
   fun `staticFieldRefToSootMethod_delegate$lambda$1`(): java.util.Map {
      val res: java.util.Map = new LinkedHashMap();
      var var10000: java.util.Iterator = Scene.v().getClasses().iterator();
      val var1: java.util.Iterator = var10000;

      while (var1.hasNext()) {
         for (SootMethod sm : ((SootClass)var1.next()).getMethods()) {
            if (sm.hasActiveBody()) {
               val var15: UnitPatchingChain = sm.getActiveBody().getUnits();
               var10000 = var15.iterator();
               val var6: java.util.Iterator = var10000;

               while (var6.hasNext()) {
                  val u: Unit = var6.next() as Unit;
                  val stmt: Stmt = u as Stmt;
                  if ((u as Stmt).containsFieldRef() && (u as Stmt).getFieldRef() is StaticFieldRef) {
                     val var17: FieldRef = stmt.getFieldRef();
                     val sf: StaticFieldRef = var17 as StaticFieldRef;
                     val `value$iv`: Any = res.get(var17 as StaticFieldRef);
                     if (`value$iv` == null) {
                        val var14: Any = new LinkedHashSet();
                        res.put(sf, var14);
                        var10000 = (java.util.Iterator)var14;
                     } else {
                        var10000 = (java.util.Iterator)`value$iv`;
                     }

                     val var19: java.util.Set = var10000 as java.util.Set;
                     var19.add(sm);
                  }
               }
            }
         }
      }

      return res;
   }
}
