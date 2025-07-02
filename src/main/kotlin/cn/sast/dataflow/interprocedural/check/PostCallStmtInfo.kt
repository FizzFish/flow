package cn.sast.dataflow.interprocedural.check

import com.feysh.corax.config.api.IStmt
import com.feysh.corax.config.api.MLocal
import com.feysh.corax.config.api.MParameter
import com.feysh.corax.config.api.MReturn
import java.io.Serializable
import kotlin.jvm.internal.SourceDebugExtension
import soot.Unit
import soot.Value
import soot.jimple.DefinitionStmt
import soot.jimple.InstanceInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.Stmt

@SourceDebugExtension(["SMAP\nPathCompanion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathCompanion.kt\ncn/sast/dataflow/interprocedural/check/PostCallStmtInfo\n+ 2 SootUtils.kt\ncn/sast/api/util/SootUtilsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,994:1\n310#2:995\n303#2:996\n1#3:997\n*S KotlinDebug\n*F\n+ 1 PathCompanion.kt\ncn/sast/dataflow/interprocedural/check/PostCallStmtInfo\n*L\n495#1:995\n503#1:996\n503#1:997\n*E\n"])
public class PostCallStmtInfo(stmt: IStmt, node: Unit) : ModelingStmtInfo(stmt) {
   public final val node: Unit

   init {
      this.node = node;
   }

   public override fun getParameterNameByIndex(index: MLocal, filter: (Any) -> Boolean): Any? {
      val var14: Serializable;
      if (index is MParameter) {
         label51: {
            val var10000: Value = this.getParameterNameByIndex((index as MParameter).getIndex());
            if (var10000 != null) {
               val var12: Value = if (filter.invoke(var10000)) var10000 else null;
               if (var12 != null) {
                  if (this.getFirstParamIndex() == null) {
                     this.setFirstParamIndex((index as MParameter).getIndex());
                  }

                  var13 = var12;
                  break label51;
               }
            }

            var13 = null;
         }

         var14 = var13 as Serializable;
      } else {
         if (index is MReturn) {
            val `$this$leftOp$iv`: Unit = this.node;
            val var15: Value = if ((this.node as? DefinitionStmt) != null) (this.node as? DefinitionStmt).getLeftOp() else null;
            return if (var15 != null) (if (filter.invoke(var15)) var15 else null) else null;
         }

         var14 = java.lang.String.valueOf(if (filter.invoke(index)) index else null);
      }

      return var14;
   }

   public open fun getParameterNameByIndex(index: Int): Value? {
      val `$this$invokeExprOrNull$iv`: Unit = this.node;
      val var10000: InvokeExpr = if ((this.node as? Stmt) != null)
         (if ((this.node as Stmt).containsInvokeExpr()) (`$this$invokeExprOrNull$iv` as Stmt).getInvokeExpr() else null)
         else
         null;
      if (var10000 == null) {
         return null;
      } else {
         val names: java.util.List = var10000.getArgs();
         if (index == -1 && var10000 is InstanceInvokeExpr) {
            return (var10000 as InstanceInvokeExpr).getBase();
         } else {
            return if (index >= 0 && index < names.size()) names.get(index) as Value else null;
         }
      }
   }
}
