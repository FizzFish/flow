package cn.sast.dataflow.interprocedural.check.checker

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CallStackContext
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.HookEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.check.PathCompanionV
import cn.sast.dataflow.interprocedural.check.UnknownPath
import cn.sast.dataflow.interprocedural.check.checker.CheckerModeling.Checker
import cn.sast.idfa.analysis.InterproceduralCFG
import cn.sast.idfa.check.ICallCB
import com.feysh.corax.config.api.report.Region
import java.util.LinkedHashMap
import kotlin.coroutines.jvm.internal.Boxing
import mu.KLogger
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.jimple.InvokeExpr
import soot.jimple.Stmt

public class CheckCallBack(atMethod: SootMethod, define: Checker) {
   public final val atMethod: SootMethod
   private final val define: Checker

   init {
      this.atMethod = atMethod;
      this.define = define;
   }

   public override fun toString(): String {
      return "check ${this.atMethod}:  ${this.define.getGuard()}";
   }

   public suspend fun check(
      hf: AbstractHeapFactory<IValue>,
      env: HookEnv,
      summaryCtxCalleeSite: ICallCB<IHeapValues<IValue>, Builder<IValue>>,
      icfg: InterproceduralCFG
   ): IFact<IValue>? {
      val callStack: CallStackContext = (summaryCtxCalleeSite.getOut() as IFact.Builder).getCallStack();
      val fact: IFact.Builder = summaryCtxCalleeSite.getOut() as IFact.Builder;

      for (Object isBug : SequencesKt.toList(hf.resolve(env, summaryCtxCalleeSite, this.define.getGuard().getExpr()))) {
         val var10000: CompanionV = isBug as? CompanionV;
         if ((isBug as? CompanionV) != null) {
            label35: {
               val var21: PathCompanionV = var10000 as? PathCompanionV;
               if ((var10000 as? PathCompanionV) != null) {
                  var22 = var21.getPath();
                  if (var22 != null) {
                     break label35;
                  }
               }

               var22 = UnknownPath.Companion.v(env);
            }

            val bool: Any = var10000.getValue();
            if (bool is ConstVal && FactValuesKt.getBooleanValue$default(bool as IValue, false, 1, null) == Boxing.boxBoolean(true)) {
               logger.debug(CheckCallBack::check$lambda$0);
               val container: SootMethod = icfg.getMethodOf(env.getNode());
               var var23: Region = Region.Companion.invoke(env.getNode());
               if (var23 == null) {
                  var23 = Region.Companion.getERROR();
               }

               val ctx: <unrepresentable> = new ProgramStateContext(
                  this, container, var23.getMutable(), ClassResInfo.Companion.of(container), env.getNode(), this.atMethod, this.define.getGuard().getExpr()
               ) {
                  private final java.util.Map<Object, Object> args;
                  private SootMethod callee;
                  private java.lang.String fileName;
                  private InvokeExpr invokeExpr;
                  private SootMethod method;
                  private SootClass clazz;
                  private SootField field;

                  {
                     val var10002: IBugResInfo = `$super_call_param$2`;
                     super(`$super_call_param$1`, var10002, `$super_call_param$3` as Stmt, `$container`, `$super_call_param$4`, `$super_call_param$5`);
                     this.args = new LinkedHashMap<>();
                     this.callee = `$receiver`.getAtMethod();
                  }

                  @Override
                  public java.util.Map<Object, Object> getArgs() {
                     return this.args;
                  }

                  @Override
                  public SootMethod getCallee() {
                     return this.callee;
                  }

                  @Override
                  public void setCallee(SootMethod var1) {
                     this.callee = var1;
                  }

                  @Override
                  public java.lang.String getFileName() {
                     return this.fileName;
                  }

                  @Override
                  public void setFileName(java.lang.String var1) {
                     this.fileName = var1;
                  }

                  @Override
                  public InvokeExpr getInvokeExpr() {
                     return this.invokeExpr;
                  }

                  @Override
                  public void setInvokeExpr(InvokeExpr var1) {
                     this.invokeExpr = var1;
                  }

                  @Override
                  public SootMethod getMethod() {
                     return this.method;
                  }

                  @Override
                  public void setMethod(SootMethod var1) {
                     this.method = var1;
                  }

                  @Override
                  public SootClass getClazz() {
                     return this.clazz;
                  }

                  @Override
                  public void setClazz(SootClass var1) {
                     this.clazz = var1;
                  }

                  @Override
                  public SootField getField() {
                     return this.field;
                  }

                  @Override
                  public void setField(SootField var1) {
                     this.field = var1;
                  }
               };
               this.define.getEnv().invoke(ctx);
               env.getCtx().report(var22, ctx, this.define);
            }
         }
      }

      return null;
   }

   @JvmStatic
   fun `check$lambda$0`(`this$0`: CheckCallBack): Any {
      return "found a bug at: method: ${`this$0`.atMethod}. define = ${`this$0`.define}";
   }

   @JvmStatic
   fun `logger$lambda$1`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
