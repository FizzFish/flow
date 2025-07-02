package cn.sast.dataflow.infoflow.svfa.gcSolver

import cn.sast.common.OS
import cn.sast.dataflow.infoflow.svfa.AP
import cn.sast.dataflow.infoflow.svfa.ILocalDFA
import cn.sast.dataflow.infoflow.svfa.LocalVFA
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import soot.Scene
import soot.Unit
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.graph.UnitGraph

public class CacheFlowGuide(trackControlFlowDependencies: Boolean) {
   public final val trackControlFlowDependencies: Boolean

   private final val initialCapacity: Int
      private final get() {
         return Math.max(Scene.v().getClasses().size() * 10, 500);
      }


   private final val flowCacheBuilder: CacheBuilder<Any, Any>
   private final val cache: LoadingCache<UnitGraph, ILocalDFA>

   init {
      this.trackControlFlowDependencies = trackControlFlowDependencies;
      this.flowCacheBuilder = CacheBuilder.newBuilder()
         .concurrencyLevel(OS.INSTANCE.getMaxThreadNum())
         .initialCapacity(this.getInitialCapacity())
         .maximumSize((long)(this.getInitialCapacity() * 2))
         .softValues();
      this.cache = this.flowCacheBuilder.build(new CacheLoader<UnitGraph, ILocalDFA>(this) {
         {
            this.this$0 = `$receiver`;
         }

         public ILocalDFA load(UnitGraph ug) throws Exception {
            return new LocalVFA(ug as DirectedGraph<Unit>, this.this$0.getTrackControlFlowDependencies());
         }
      });
   }

   public fun getSuccess(isForward: Boolean, ap: AP, unit: Unit, unitGraph: UnitGraph): List<Unit> {
      val lu: ILocalDFA = this.cache.getUnchecked(unitGraph) as ILocalDFA;
      return if (isForward) lu.getUsesOfAt(ap, unit) else lu.getDefUsesOfAt(ap, unit);
   }
}
