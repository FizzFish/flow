package cn.sast.dataflow.infoflow.svfa

import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentSet
import soot.Timers
import soot.Unit
import soot.Value
import soot.jimple.ArrayRef
import soot.jimple.AssignStmt
import soot.jimple.BinopExpr
import soot.jimple.Expr
import soot.jimple.IdentityStmt
import soot.jimple.IfStmt
import soot.jimple.InstanceInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.InvokeStmt
import soot.jimple.ParameterRef
import soot.jimple.ReturnStmt
import soot.jimple.ReturnVoidStmt
import soot.jimple.Stmt
import soot.jimple.ThisRef
import soot.jimple.infoflow.util.BaseSelector
import soot.options.Options
import soot.toolkits.graph.DirectedGraph

@SourceDebugExtension(["SMAP\nSparsePropgrateAnalyze.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SparsePropgrateAnalyze.kt\ncn/sast/dataflow/infoflow/svfa/LocalVFA\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,420:1\n13409#2,2:421\n808#3,11:423\n1279#3,2:434\n1293#3,4:436\n1557#3:440\n1628#3,3:441\n1557#3:444\n1628#3,3:445\n*S KotlinDebug\n*F\n+ 1 SparsePropgrateAnalyze.kt\ncn/sast/dataflow/infoflow/svfa/LocalVFA\n*L\n313#1:421,2\n372#1:423,11\n372#1:434,2\n372#1:436,4\n392#1:440\n392#1:441,3\n398#1:444\n398#1:445,3\n*E\n"])
public class LocalVFA(graph: DirectedGraph<Unit>, trackControlFlowDependencies: Boolean) : ILocalDFA {
   public final val trackControlFlowDependencies: Boolean
   private final val uses: Map<Unit, FlowFact>
   private final val defuses: Map<Unit, FlowFact>

   init {
      this.trackControlFlowDependencies = trackControlFlowDependencies;
      val time: Boolean = Options.v().time();
      if (time) {
         Timers.v().defsTimer.start();
      }

      val var4: Pair = this.init(graph);
      this.uses = var4.component1() as MutableMap<Unit, FlowFact>;
      this.defuses = var4.component2() as MutableMap<Unit, FlowFact>;
      if (time) {
         Timers.v().defsTimer.end();
      }
   }

   private fun <R> Expr.traverse() {
   }

   private fun <R> collectStmtInfo(stmt: Stmt, addValueToInfoMap: (Value, ValueLocation) -> R) {
      if (stmt is AssignStmt) {
         val i: Value = (stmt as AssignStmt).getLeftOp();
         val rightValues: Array<Value> = BaseSelector.selectBaseList((stmt as AssignStmt).getRightOp(), true);
         if (i is ArrayRef) {
            val var10001: Value = (i as ArrayRef).getBase();
            addValueToInfoMap.invoke(var10001, ValueLocation.Right);
         } else {
            addValueToInfoMap.invoke(i, ValueLocation.Left);
         }
         for (Object element$iv : rightValues) {
            addValueToInfoMap.invoke(`element$iv`, ValueLocation.Right);
         }
      } else if (stmt is IdentityStmt) {
         val var14: Value = (stmt as IdentityStmt).getRightOp();
         if (var14 is ParameterRef) {
            val var18: Value = (stmt as IdentityStmt).getLeftOp();
            addValueToInfoMap.invoke(var18, ValueLocation.ParamAndThis);
         } else if (var14 is ThisRef) {
            val var19: Value = (stmt as IdentityStmt).getLeftOp();
            addValueToInfoMap.invoke(var19, ValueLocation.ParamAndThis);
         }
      } else if (stmt !is InvokeStmt) {
         if (stmt is IfStmt) {
            if (this.trackControlFlowDependencies) {
               val var10000: Value = (stmt as IfStmt).getCondition();
               val var16: BinopExpr = var10000 as BinopExpr;
               var var23: Value = (var10000 as BinopExpr).getOp1();
               addValueToInfoMap.invoke(var23, ValueLocation.Right);
               var23 = var16.getOp2();
               addValueToInfoMap.invoke(var23, ValueLocation.Right);
            }

            return;
         }

         if (stmt is ReturnStmt) {
            val var22: Value = (stmt as ReturnStmt).getOp();
            addValueToInfoMap.invoke(var22, ValueLocation.Right);
            return;
         }

         if (stmt is ReturnVoidStmt) {
            addValueToInfoMap.invoke(returnVoidFake, ValueLocation.Right);
            return;
         }
      }

      val ie: InvokeExpr = if (stmt.containsInvokeExpr()) stmt.getInvokeExpr() else null;
      if (ie != null) {
         if (ie is InstanceInvokeExpr) {
            val var20: Value = (ie as InstanceInvokeExpr).getBase();
            addValueToInfoMap.invoke(var20, ValueLocation.Arg);
         }

         var var15: Int = 0;

         for (int var17 = ie.getArgCount(); i < var17; i++) {
            val var21: Value = ie.getArg(var15);
            addValueToInfoMap.invoke(var21, ValueLocation.Arg);
         }
      }
   }

   private fun init(graph: DirectedGraph<Unit>): Pair<Map<Unit, FlowFact>, Map<Unit, FlowFact>> {
      val paramAndThis: java.util.Set = new LinkedHashSet();
      var `$this$associateWith$iv`: java.lang.Iterable = graph as java.lang.Iterable;
      val `$this$associateWithTo$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$filterIsInstance$iv) {
         if (`element$iv$iv` is Stmt) {
            `$this$associateWithTo$iv$iv`.add(`element$iv$iv`);
         }
      }

      `$this$associateWith$iv` = `$this$associateWithTo$iv$iv` as java.util.List;
      val `result$iv`: LinkedHashMap = new LinkedHashMap(
         RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$associateWithTo$iv$iv` as java.util.List, 10)), 16)
      );

      for (Object element$iv$ivx : $this$filterIsInstance$iv) {
         val var10000: java.util.Map = `result$iv`;
         val stmt: Stmt = `element$iv$ivx` as Stmt;
         val apAndLoc: java.util.Set = new LinkedHashSet();
         this.collectStmtInfo(stmt, LocalVFA::init$lambda$2$lambda$1);
         var10000.put(`element$iv$ivx`, apAndLoc);
      }

      return TuplesKt.to(new FlowAssignment(graph, paramAndThis, `result$iv`).getBefore(), new BackAssignment(graph, paramAndThis, `result$iv`).getAfter());
   }

   public override fun getDefUsesOfAt(ap: AP, stmt: Unit): List<Unit> {
      val var10000: FlowFact = this.defuses.get(stmt);
      if (var10000 == null) {
         return CollectionsKt.emptyList();
      } else {
         var var15: PersistentSet = var10000.getData().get(ap.getValue()) as PersistentSet;
         if (var15 == null) {
            var15 = ExtensionsKt.persistentHashSetOf();
         }

         val `$this$map$iv`: java.lang.Iterable = var15 as java.lang.Iterable;
         val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var15 as java.lang.Iterable, 10));

         for (Object item$iv$iv : $this$map$iv) {
            `destination$iv$iv`.add((`item$iv$iv` as VFNode).getStmt());
         }

         return `destination$iv$iv` as MutableList<Unit>;
      }
   }

   public override fun getUsesOfAt(ap: AP, stmt: Unit): List<Unit> {
      val var10000: FlowFact = this.uses.get(stmt);
      if (var10000 == null) {
         return CollectionsKt.emptyList();
      } else {
         var var15: PersistentSet = var10000.getData().get(ap.getValue()) as PersistentSet;
         if (var15 == null) {
            var15 = ExtensionsKt.persistentHashSetOf();
         }

         val `$this$map$iv`: java.lang.Iterable = var15 as java.lang.Iterable;
         val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var15 as java.lang.Iterable, 10));

         for (Object item$iv$iv : $this$map$iv) {
            `destination$iv$iv`.add((`item$iv$iv` as VFNode).getStmt());
         }

         return `destination$iv$iv` as MutableList<Unit>;
      }
   }

   @JvmStatic
   fun `init$lambda$2$lambda$1`(`$paramAndThis`: java.util.Set, `$apAndLoc`: java.util.Set, value: Value, valueLocation: ValueLocation): kotlin.Unit {
      val ap: AP = AP.Companion.get(value);
      if (ap != null) {
         if (valueLocation === ValueLocation.ParamAndThis) {
            `$paramAndThis`.add(ap.getValue());
         }

         `$apAndLoc`.add(TuplesKt.to(ap, valueLocation));
      }

      return kotlin.Unit.INSTANCE;
   }

   public companion object {
      public final val returnVoidFake: Value
      public final val entryFake: Value
   }
}
