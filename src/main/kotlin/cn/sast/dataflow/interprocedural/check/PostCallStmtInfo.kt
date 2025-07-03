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
class PostCallStmtInfo(stmt: IStmt, val node: Unit) : ModelingStmtInfo(stmt) {

    override fun getParameterNameByIndex(index: MLocal, filter: (Any) -> Boolean): Any? {
        val result: Serializable? = if (index is MParameter) {
            val value = getParameterNameByIndex(index.getIndex())
            if (value != null && filter(value)) {
                if (getFirstParamIndex() == null) {
                    setFirstParamIndex(index.getIndex())
                }
                value
            } else {
                null
            }
        } else {
            if (index is MReturn) {
                val leftOp = (node as? DefinitionStmt)?.leftOp
                if (leftOp != null && filter(leftOp)) leftOp else null
            } else {
                if (filter(index)) index else null
            }?.let { java.lang.String.valueOf(it) }
        }
        return result
    }

    open fun getParameterNameByIndex(index: Int): Value? {
        val invokeExpr = (node as? Stmt)?.takeIf { it.containsInvokeExpr() }?.invokeExpr
        return when {
            invokeExpr == null -> null
            index == -1 && invokeExpr is InstanceInvokeExpr -> invokeExpr.base
            index >= 0 && index < invokeExpr.args.size -> invokeExpr.args[index] as Value
            else -> null
        }
    }
}