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

public class ModelingCallBack(method: SootMethod, stmt: IStmt) {
   public final val method: SootMethod
   public final val stmt: IStmt

   init {
      this.method = method;
      this.stmt = stmt;
   }

   public override fun toString(): String {
      return "${this.method}:  ${this.stmt}";
   }

   public fun model(
      icfg: InterproceduralCFG,
      hf: AbstractHeapFactory<IValue>,
      env: HookEnv,
      summaryCtxCalleeSite: ICallCB<IHeapValues<IValue>, Builder<IValue>>
   ): IFact<IValue>? {
      this.stmt
         .accept(
            new FactModeling(
                  hf,
                  if (summaryCtxCalleeSite is IPrevCB)
                     new StmtModelingEnv(env.getNode(), new PrevCallStmtInfo(this.stmt, this.method))
                     else
                     (if (summaryCtxCalleeSite is IPostCB) new StmtModelingEnv(env.getNode(), new PostCallStmtInfo(this.stmt, env.getNode())) else env),
                  summaryCtxCalleeSite,
                  summaryCtxCalleeSite.getOut() as IFactBuilder<IValue>
               )
               .getVisitor()
         );
      return null;
   }
}
