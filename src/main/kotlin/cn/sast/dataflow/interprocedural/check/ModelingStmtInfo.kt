package cn.sast.dataflow.interprocedural.check

import com.feysh.corax.config.api.AttributeName
import com.feysh.corax.config.api.ClassField
import com.feysh.corax.config.api.IBinOpExpr
import com.feysh.corax.config.api.IClassField
import com.feysh.corax.config.api.IExpr
import com.feysh.corax.config.api.IIexConst
import com.feysh.corax.config.api.IIexGetField
import com.feysh.corax.config.api.IIexLoad
import com.feysh.corax.config.api.IIstSetField
import com.feysh.corax.config.api.IIstStoreLocal
import com.feysh.corax.config.api.IModelExpressionVisitor
import com.feysh.corax.config.api.IModelStmtVisitor
import com.feysh.corax.config.api.IQOpExpr
import com.feysh.corax.config.api.IStmt
import com.feysh.corax.config.api.ITriOpExpr
import com.feysh.corax.config.api.IUnOpExpr
import com.feysh.corax.config.api.MLocal
import com.feysh.corax.config.api.SubFields
import com.feysh.corax.config.api.TaintProperty
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import soot.Local
import soot.Unit
import soot.ValueBox
import soot.jimple.Constant

@SourceDebugExtension(["SMAP\nPathCompanion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathCompanion.kt\ncn/sast/dataflow/interprocedural/check/ModelingStmtInfo\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,994:1\n1628#2,3:995\n1053#2:998\n*S KotlinDebug\n*F\n+ 1 PathCompanion.kt\ncn/sast/dataflow/interprocedural/check/ModelingStmtInfo\n*L\n362#1:995,3\n447#1:998\n*E\n"])
public abstract class ModelingStmtInfo {
   public final val stmt: IStmt

   public final var firstParamIndex: Int?
      internal set

   open fun ModelingStmtInfo(stmt: IStmt) {
      this.stmt = stmt;
   }

   public abstract fun getParameterNameByIndex(index: Int): Any? {
   }

   public abstract fun getParameterNameByIndex(index: MLocal, filter: (Any) -> Boolean): Any? {
   }

   public fun getParameterNamesInUnitDefUse(unit: Unit): List<Any> {
      val var10000: java.util.List = unit.getUseAndDefBoxes();
      val `$this$mapTo$iv`: java.lang.Iterable = var10000;
      val `destination$iv`: java.util.Collection = new LinkedHashSet();

      for (Object item$iv : $this$mapTo$iv) {
         `destination$iv`.add((`item$iv` as ValueBox).getValue());
      }

      return this.getParameterNames(ModelingStmtInfo::getParameterNamesInUnitDefUse$lambda$1);
   }

   public fun getParameterNames(filter: (Any) -> Boolean): List<Any> {
      val visitor: <unrepresentable> = new IModelStmtVisitor<kotlin.Unit>(this, filter) {
         private java.util.Set<Object> result;

         {
            this.this$0 = `$receiver`;
            this.$filter = `$filter`;
            this.result = new LinkedHashSet<>();
         }

         public final java.util.Set<Object> getResult() {
            return this.result;
         }

         public final void setResult(java.util.Set<Object> var1) {
            this.result = var1;
         }

         public void default(IStmt stmt) {
         }

         public void visit(IIstSetField stmt) {
            stmt.getValue().accept(this as IModelExpressionVisitor<kotlin.Unit>);
         }

         public void visit(IIstStoreLocal stmt) {
            stmt.getValue().accept(this as IModelExpressionVisitor<kotlin.Unit>);
         }

         public void default(IExpr expr) {
         }

         public void visit(IIexGetField expr) {
            val acp: java.util.List = CollectionsKt.toMutableList(expr.getAccessPath());
            val last: IClassField = CollectionsKt.lastOrNull(acp) as IClassField;
            if (last is AttributeName && (last as AttributeName).getName() == "isConstant") {
               CollectionsKt.removeLastOrNull(acp);
            }

            if (last is TaintProperty) {
               CollectionsKt.removeLastOrNull(acp);
            }

            val base: IExpr = expr.getBase();
            val var10000: Any;
            if (base is IIexLoad) {
               var10000 = this.this$0.getParameterNameByIndex((base as IIexLoad).getOp(), this.$filter);
               if (var10000 == null) {
                  return;
               }
            } else {
               var10000 = base.toString();
            }

            if (acp.isEmpty()) {
               this.result.add(var10000.toString());
            } else {
               this.result.add("$var10000.${CollectionsKt.joinToString$default(acp, ".", null, null, 0, null, <unrepresentable>::visit$lambda$0, 30, null)}");
            }
         }

         public void visit(IIexLoad expr) {
            val var10000: Any = this.this$0.getParameterNameByIndex(expr.getOp(), this.$filter);
            if (var10000 != null) {
               this.result.add(var10000);
            }
         }

         public void visit(IUnOpExpr expr) {
            expr.getOp1().accept(this as IModelExpressionVisitor<kotlin.Unit>);
         }

         public void visit(IBinOpExpr expr) {
            expr.getOp1().accept(this as IModelExpressionVisitor<kotlin.Unit>);
            expr.getOp2().accept(this as IModelExpressionVisitor<kotlin.Unit>);
         }

         public void visit(ITriOpExpr expr) {
            expr.getOp1().accept(this as IModelExpressionVisitor<kotlin.Unit>);
            expr.getOp2().accept(this as IModelExpressionVisitor<kotlin.Unit>);
            expr.getOp3().accept(this as IModelExpressionVisitor<kotlin.Unit>);
         }

         public void visit(IQOpExpr expr) {
            expr.getOp1().accept(this as IModelExpressionVisitor<kotlin.Unit>);
            expr.getOp2().accept(this as IModelExpressionVisitor<kotlin.Unit>);
            expr.getOp3().accept(this as IModelExpressionVisitor<kotlin.Unit>);
            expr.getOp4().accept(this as IModelExpressionVisitor<kotlin.Unit>);
         }

         public void visit(IIexConst expr) {
            IModelExpressionVisitor.DefaultImpls.visit(this, expr);
         }

         private static final java.lang.CharSequence visit$lambda$0(IClassField it) {
            return if (it is SubFields) "*" else (if (it is ClassField) (it as ClassField).getFieldName() else it.toString());
         }
      };
      this.stmt.accept(visitor);
      return CollectionsKt.toList(CollectionsKt.sortedWith(visitor.getResult(), new ModelingStmtInfo$getParameterNames$$inlined$sortedBy$1()));
   }

   @JvmStatic
   fun `getParameterNamesInUnitDefUse$lambda$1`(`$useDef`: java.util.Set, it: Any): Boolean {
      return if (it is Local) `$useDef`.contains(it) else it !is Constant;
   }
}
