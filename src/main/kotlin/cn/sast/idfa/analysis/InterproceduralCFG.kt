package cn.sast.idfa.analysis

import com.github.benmanes.caffeine.cache.LoadingCache
import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.internal.SourceDebugExtension
import soot.Local
import soot.SootMethod
import soot.Unit
import soot.Value
import soot.ValueBox
import soot.jimple.IdentityStmt
import soot.jimple.LookupSwitchStmt
import soot.jimple.Stmt
import soot.jimple.ThisRef
import soot.jimple.toolkits.ide.icfg.AbstractJimpleBasedICFG
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG
import soot.toolkits.graph.DirectedBodyGraph
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.scalar.LiveLocals
import soot.toolkits.scalar.SimpleLiveLocals

@SourceDebugExtension(["SMAP\nInterproceduralCFG.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InterproceduralCFG.kt\ncn/sast/idfa/analysis/InterproceduralCFG\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,246:1\n1619#2:247\n1863#2:248\n1864#2:251\n1620#2:252\n1#3:249\n1#3:250\n*S KotlinDebug\n*F\n+ 1 InterproceduralCFG.kt\ncn/sast/idfa/analysis/InterproceduralCFG\n*L\n244#1:247\n244#1:248\n244#1:251\n244#1:252\n244#1:250\n*E\n"])
public class InterproceduralCFG : ProgramRepresentation<SootMethod, Unit> {
   private final val unitToOwner: MutableMap<Unit, SootMethod> = (new ConcurrentHashMap()) as java.util.Map
   private final val cfgCacheSummary: LoadingCache<SootMethod, DirectedGraph<Unit>>
   private final val cfgCache: LoadingCache<SootMethod, DirectedGraph<Unit>>
   private final val liveLocalCache: LoadingCache<DirectedBodyGraph<Unit>, LiveLocals>

   public final val delegateICFG: AbstractJimpleBasedICFG by LazyKt.lazy(InterproceduralCFG::delegateICFG_delegate$lambda$0)
      public final get() {
         return this.delegateICFG$delegate.getValue() as AbstractJimpleBasedICFG;
      }


   public open fun getControlFlowGraph(method: SootMethod): DirectedGraph<Unit> {
      val var10000: Any = this.cfgCache.get(method);
      return var10000 as DirectedGraph<Unit>;
   }

   public open fun getSummaryControlFlowGraph(method: SootMethod): DirectedGraph<Unit> {
      val var10000: Any = this.cfgCacheSummary.get(method);
      return var10000 as DirectedGraph<Unit>;
   }

   public open fun isCall(node: Unit): Boolean {
      return (node as Stmt).containsInvokeExpr();
   }

   public open fun getCalleesOfCallAt(callerMethod: SootMethod, callNode: Unit): Set<SootMethod> {
      val var10000: java.util.Collection = this.getDelegateICFG().getCalleesOfCallAt(callNode);
      return CollectionsKt.toSet(var10000);
   }

   public fun getMethodOf(node: Unit): SootMethod {
      var var10000: SootMethod = this.unitToOwner.get(node);
      if (var10000 == null) {
         var10000 = this.getDelegateICFG().getMethodOf(node);
      }

      return var10000;
   }

   public open fun setOwnerStatement(u: Unit, owner: SootMethod) {
      this.unitToOwner.put(u, owner);
   }

   public open fun setOwnerStatement(g: Iterable<Unit>, owner: SootMethod) {
      for (Unit u : g) {
         this.unitToOwner.put(u, owner);
      }
   }

   public open fun isAnalyzable(method: SootMethod): Boolean {
      return method.hasActiveBody();
   }

   public fun isFallThroughSuccessor(unit: Unit, succ: Unit): Boolean {
      return this.getDelegateICFG().isFallThroughSuccessor(unit, succ);
   }

   public fun isCallStmt(unit: Unit): Boolean {
      return this.getDelegateICFG().isCallStmt(unit);
   }

   public fun getCalleesOfCallAt(unit: Unit): Collection<SootMethod> {
      val var10000: java.util.Collection = this.getDelegateICFG().getCalleesOfCallAt(unit);
      return var10000;
   }

   public fun getPredsOf(unit: Unit): List<Unit> {
      val var10000: java.util.List = this.getDelegateICFG().getPredsOf(unit);
      return var10000;
   }

   public fun hasPredAsLookupSwitchStmt(unit: Unit): Boolean {
      for (Unit pred : this.getDelegateICFG().getPredsOf(unit)) {
         if (pred is LookupSwitchStmt) {
            return true;
         }
      }

      return false;
   }

   public fun getPredAsLookupSwitchStmt(unit: Unit): Unit? {
      for (Unit pred : this.getDelegateICFG().getPredsOf(unit)) {
         if (pred is LookupSwitchStmt) {
            return pred;
         }
      }

      return null;
   }

   public fun getIdentityStmt(method: SootMethod): IdentityStmt {
      val var10000: java.util.Iterator = method.getActiveBody().getUnits().iterator();
      val var2: java.util.Iterator = var10000;

      while (var2.hasNext()) {
         val s: Unit = var2.next() as Unit;
         if (s is IdentityStmt && (s as IdentityStmt).getRightOp() is ThisRef) {
            return s as IdentityStmt;
         }
      }

      throw new RuntimeException("couldn't find identityref! in $method");
   }

   public open fun isSkipCall(node: Unit): Boolean {
      if (node is Stmt && (node as Stmt).containsInvokeExpr()) {
      }

      return false;
   }

   public fun getNonLiveLocals(ug: DirectedBodyGraph<Unit>, unit: Unit): List<Local> {
      var var10000: Any = this.liveLocalCache.get(ug);
      val liveLocals: java.util.List = (var10000 as LiveLocals).getLiveLocalsAfter(unit);
      var10000 = unit.getUseBoxes();
      val `$this$mapNotNullTo$iv`: java.lang.Iterable = var10000 as java.lang.Iterable;
      val `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$mapNotNullTo$iv) {
         val var16: Value = (`element$iv$iv` as ValueBox).getValue();
         var10000 = if (!liveLocals.contains(var16 as? Local)) (var16 as? Local) else null;
         if (var10000 != null) {
            `destination$iv`.add(var10000);
         }
      }

      return `destination$iv` as MutableList<Local>;
   }

   @JvmStatic
   fun `delegateICFG_delegate$lambda$0`(): JimpleBasedInterproceduralCFG {
      return new JimpleBasedInterproceduralCFG(true);
   }

   @JvmStatic
   fun `_init_$lambda$1`(`this$0`: InterproceduralCFG, key: SootMethod): DirectedGraph {
      return new SummaryControlFlowUnitGraph(key, `this$0`);
   }

   @JvmStatic
   fun `_init_$lambda$2`(`this$0`: InterproceduralCFG, key: SootMethod): DirectedGraph {
      return `this$0`.getDelegateICFG().getOrCreateUnitGraph(key.getActiveBody());
   }

   @JvmStatic
   fun `_init_$lambda$3`(g: DirectedBodyGraph): LiveLocals {
      return (new SimpleLiveLocals(g)) as LiveLocals;
   }
}
