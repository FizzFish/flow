package cn.sast.dataflow.interprocedural.check.checker

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.HookEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.StmtModelingEnv
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.check.PostCallStmtInfo
import cn.sast.dataflow.interprocedural.check.PrevCallStmtInfo
import cn.sast.idfa.analysis.InterproceduralCFG
import cn.sast.idfa.check.ICallCB
import cn.sast.idfa.check.IPostCB
import cn.sast.idfa.check.IPrevCB
import com.feysh.corax.config.api.IStmt
import soot.SootMethod

class ModelingCallBack(
    val method: SootMethod,
    val stmt: IStmt
) {
    override fun toString(): String {
        return "${this.method}:  ${this.stmt}"
    }

    fun model(
        icfg: InterproceduralCFG,
        hf: AbstractHeapFactory<IValue>,
        env: HookEnv,
        summaryCtxCalleeSite: ICallCB<IHeapValues<IValue>, Builder<IValue>>
    ): IFact<IValue>? {
        this.stmt.accept(
            FactModeling(
                hf,
                if (summaryCtxCalleeSite is IPrevCB)
                    StmtModelingEnv(env.getNode(), PrevCallStmtInfo(this.stmt, this.method))
                else
                    if (summaryCtxCalleeSite is IPostCB) StmtModelingEnv(env.getNode(), PostCallStmtInfo(this.stmt, env.getNode())) else env,
                summaryCtxCalleeSite,
                summaryCtxCalleeSite.getOut() as IFact.Builder<IValue>
            ).getVisitor()
        )
        return null
    }
}