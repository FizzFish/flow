package cn.sast.dataflow.interprocedural.check

import org.apache.commons.text.StringEscapeUtils
import soot.jimple.Stmt
import soot.jimple.infoflow.data.Abstraction
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG

public fun Abstraction.getLabel(cfg: IInfoflowCFG): String {
    val sb = StringBuffer("\"")
    var var10000: Stmt? = this.getCorrespondingCallSite()
    if (var10000 == null || sb.append(StringEscapeUtils.escapeHtml4("callSite: ${var10000.getJavaSourceStartLineNumber()} $var10000")) == null) {
        sb.append("correspondingCallSite null")
    }

    sb.append("\n")
    var10000 = this.getCurrentStmt()
    if (var10000 != null
        && sb.append(StringEscapeUtils.escapeHtml4("${this.getAccessPath()}  ${var10000.getJavaSourceStartLineNumber()} $var10000")) != null
    ) {
        return "$sb\""
    } else {
        sb.append(StringEscapeUtils.escapeHtml4("${this.getAccessPath()}  currentStmt: null"))
        return "$sb\""
    }
}