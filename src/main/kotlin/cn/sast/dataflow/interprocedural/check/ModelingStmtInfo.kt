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
public abstract class ModelingStmtInfo(public final val stmt: IStmt) {
    public final var firstParamIndex: Int? = null
        internal set

    public abstract fun getParameterNameByIndex(index: Int): Any?

    public abstract fun getParameterNameByIndex(index: MLocal, filter: (Any) -> Boolean): Any?

    public fun getParameterNamesInUnitDefUse(unit: Unit): List<Any> {
        val useDef = unit.getUseAndDefBoxes().map { (it as ValueBox).value }.toSet()
        return getParameterNames { it ->
            if (it is Local) useDef.contains(it) else it !is Constant
        }
    }

    public fun getParameterNames(filter: (Any) -> Boolean): List<Any> {
        val visitor = object : IModelStmtVisitor<Unit>, IModelExpressionVisitor<Unit> {
            private val result = LinkedHashSet<Any>()

            override fun default(stmt: IStmt) {
            }

            override fun visit(stmt: IIstSetField) {
                stmt.value.accept(this)
            }

            override fun visit(stmt: IIstStoreLocal) {
                stmt.value.accept(this)
            }

            override fun default(expr: IExpr) {
            }

            override fun visit(expr: IIexGetField) {
                val acp = expr.accessPath.toMutableList()
                val last = acp.lastOrNull() as? IClassField
                if (last is AttributeName && last.name == "isConstant") {
                    acp.removeLastOrNull()
                }

                if (last is TaintProperty) {
                    acp.removeLastOrNull()
                }

                val base = expr.base
                val baseName = if (base is IIexLoad) {
                    getParameterNameByIndex(base.op, filter) ?: return
                } else {
                    base.toString()
                }

                if (acp.isEmpty()) {
                    result.add(baseName.toString())
                } else {
                    result.add("$baseName.${acp.joinToString(".") { 
                        if (it is SubFields) "*" else if (it is ClassField) it.fieldName else it.toString()
                    }}")
                }
            }

            override fun visit(expr: IIexLoad) {
                getParameterNameByIndex(expr.op, filter)?.let { result.add(it) }
            }

            override fun visit(expr: IUnOpExpr) {
                expr.op1.accept(this)
            }

            override fun visit(expr: IBinOpExpr) {
                expr.op1.accept(this)
                expr.op2.accept(this)
            }

            override fun visit(expr: ITriOpExpr) {
                expr.op1.accept(this)
                expr.op2.accept(this)
                expr.op3.accept(this)
            }

            override fun visit(expr: IQOpExpr) {
                expr.op1.accept(this)
                expr.op2.accept(this)
                expr.op3.accept(this)
                expr.op4.accept(this)
            }

            override fun visit(expr: IIexConst) {
                IModelExpressionVisitor.DefaultImpls.visit(this, expr)
            }
        }

        stmt.accept(visitor)
        return visitor.result.sortedBy { it.toString() }.toList()
    }
}