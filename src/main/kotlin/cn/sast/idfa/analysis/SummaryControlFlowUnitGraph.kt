package cn.sast.idfa.analysis

import kotlin.jvm.internal.SourceDebugExtension
import soot.Body
import soot.Local
import soot.LocalGenerator
import soot.RefType
import soot.Scene
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.UnitPatchingChain
import soot.Value
import soot.jimple.AssignStmt
import soot.jimple.InvokeStmt
import soot.jimple.Jimple
import soot.jimple.JimpleBody
import soot.jimple.StringConstant
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.graph.ExceptionalUnitGraphFactory
import soot.toolkits.graph.UnitGraph

@SourceDebugExtension(["SMAP\nSummaryControlFlowUnitGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SummaryControlFlowUnitGraph.kt\ncn/sast/idfa/analysis/SummaryControlFlowUnitGraph\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,77:1\n1863#2,2:78\n*S KotlinDebug\n*F\n+ 1 SummaryControlFlowUnitGraph.kt\ncn/sast/idfa/analysis/SummaryControlFlowUnitGraph\n*L\n45#1:78,2\n*E\n"])
public open class SummaryControlFlowUnitGraph(method: SootMethod, icfg: InterproceduralCFG) : DirectedGraph<Unit> {
   public final val method: SootMethod
   public final val icfg: InterproceduralCFG
   public final val jimp: Jimple
   public final val body: JimpleBody

   public final var graph: UnitGraph
      internal set

   init {
      this.method = method;
      this.icfg = icfg;
      val var10001: Jimple = Jimple.v();
      this.jimp = var10001;
      this.body = this.jimp.newBody(this.method);
      if (this.method.hasActiveBody()) {
         throw new IllegalStateException(("${this.method} hasActiveBody").toString());
      } else {
         val units: UnitPatchingChain = this.body.getUnits();
         val lg: LocalGenerator = Scene.v().createLocalGenerator(this.body as Body);
         val runtimeExceptionType: RefType = RefType.v("java.lang.Error");
         val exceptionLocal: Local = lg.generateLocal(runtimeExceptionType as Type);
         val var10000: AssignStmt = this.jimp.newAssignStmt(exceptionLocal as Value, this.jimp.newNewExpr(runtimeExceptionType) as Value);
         units.add(var10000 as Unit);
         val var18: InvokeStmt = this.jimp
            .newInvokeStmt(
               this.jimp
                  .newSpecialInvokeExpr(
                     exceptionLocal,
                     runtimeExceptionType.getSootClass().getMethod("<init>", CollectionsKt.listOf(RefType.v("java.lang.String"))).makeRef(),
                     StringConstant.v("phantom method body") as Value
                  ) as Value
            );
         units.insertAfter(var18 as Unit, var10000 as Unit);
         units.insertAfter(this.jimp.newThrowStmt(exceptionLocal as Value) as Unit, var18 as Unit);

         val `$this$forEach$iv`: java.lang.Iterable;
         for (Object element$iv : $this$forEach$iv) {
            val it: Unit = `element$iv` as Unit;
            val var20: InterproceduralCFG = this.icfg;
            var20.setOwnerStatement(it, this.method);
         }

         this.graph = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(this.body as Body) as UnitGraph;
      }
   }

   public open operator fun iterator(): MutableIterator<Unit> {
      val var10000: java.util.Iterator = this.graph.iterator();
      return var10000;
   }

   public open fun getHeads(): List<Unit> {
      val var10000: java.util.List = this.graph.getHeads();
      return var10000;
   }

   public open fun getTails(): List<Unit> {
      val var10000: java.util.List = this.graph.getTails();
      return var10000;
   }

   public open fun getPredsOf(s: Unit): List<Unit> {
      val var10000: java.util.List = this.graph.getPredsOf(s);
      return var10000;
   }

   public open fun getSuccsOf(s: Unit): List<Unit> {
      val var10000: java.util.List = this.graph.getSuccsOf(s);
      return var10000;
   }

   public open fun size(): Int {
      return this.graph.size();
   }
}
