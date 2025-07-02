package cn.sast.dataflow.analysis.deadcode

import cn.sast.dataflow.analysis.IBugReporter
import cn.sast.dataflow.analysis.constant.ConstantValues
import com.feysh.corax.config.api.BugMessage
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.builtin.checkers.DeadCodeChecker
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import soot.Body
import soot.SootClass
import soot.Unit
import soot.UnitPatchingChain
import soot.Value
import soot.jimple.IfStmt
import soot.toolkits.graph.DirectedBodyGraph

@SourceDebugExtension(["SMAP\nDeadCode.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DeadCode.kt\ncn/sast/dataflow/analysis/deadcode/DeadCode\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,40:1\n808#2,11:41\n1863#2,2:52\n*S KotlinDebug\n*F\n+ 1 DeadCode.kt\ncn/sast/dataflow/analysis/deadcode/DeadCode\n*L\n24#1:41,11\n24#1:52,2\n*E\n"])
public class DeadCode(reporter: IBugReporter) {
   private final val reporter: IBugReporter

   init {
      this.reporter = reporter;
   }

   public fun analyze(graph: DirectedBodyGraph<Unit>) {
      this.findDeadCode(graph);
      val constantValues: ConstantValues = new ConstantValues(graph);
      val var10001: Body = graph.getBody();
      this.findUnreachableBranch(var10001, constantValues);
   }

   private fun findDeadCode(graph: DirectedBodyGraph<Unit>) {
   }

   private fun findUnreachableBranch(body: Body, constantValues: ConstantValues) {
      val var10000: UnitPatchingChain = body.getUnits();
      val `$this$forEach$iv`: java.lang.Iterable = var10000 as java.lang.Iterable;
      val `element$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$filterIsInstance$iv) {
         if (guard is IfStmt) {
            `element$iv`.add(guard);
         }
      }

      for (Object element$ivx : $this$filterIsInstance$iv) {
         var var15: IfStmt;
         label53: {
            var15 = `element$ivx` as IfStmt;
            val var10001: Value = (`element$ivx` as IfStmt).getCondition();
            val var17: Int = constantValues.getValueAt(var10001, var15 as Unit);
            if (var17 != null) {
               if (var17 == 1) {
                  var19 = body.getUnits().getSuccOf(var15 as Unit);
                  break label53;
               }
            }

            if (var17 != null) {
               if (var17 == 0) {
                  var19 = var15.getTarget() as Unit;
                  break label53;
               }
            }

            var19 = null;
         }

         if (var19 != null) {
            val var18: java.lang.String = if (var19 == var15.getTarget()) var15.getCondition().toString() else "!(${var15.getCondition()})";
            val var20: IBugReporter = this.reporter;
            val var21: CheckType = DeadCodeChecker.UnreachableBranch.INSTANCE;
            val var10002: SootClass = body.getMethod().getDeclaringClass();
            var20.report(var21, var10002, var19, DeadCode::findUnreachableBranch$lambda$1$lambda$0);
         }
      }
   }

   @JvmStatic
   fun `findUnreachableBranch$lambda$1$lambda$0`(`$guard`: java.lang.String, `$unreachableBranch`: Unit, `$this$report`: BugMessage.Env): kotlin.Unit {
      `$this$report`.getArgs().put("guard", `$guard`);
      `$this$report`.getArgs().put("target", `$unreachableBranch`);
      return kotlin.Unit.INSTANCE;
   }
}
