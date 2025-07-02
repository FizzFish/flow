package cn.sast.dataflow.interprocedural.check.callback

import cn.sast.api.config.StaticFieldTrackingMode
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.HookEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IIFact
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.util.SootUtilsKt
import cn.sast.idfa.check.ICallerSiteCB
import java.io.Serializable
import kotlin.jvm.internal.SourceDebugExtension
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.Value
import soot.jimple.DefinitionStmt
import soot.jimple.InvokeExpr
import soot.jimple.Stmt

@SourceDebugExtension(["SMAP\nCallCallBackImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CallCallBackImpl.kt\ncn/sast/dataflow/interprocedural/check/callback/CallerSiteCBImpl\n+ 2 SootUtils.kt\ncn/sast/api/util/SootUtilsKt\n*L\n1#1,164:1\n310#2:165\n*S KotlinDebug\n*F\n+ 1 CallCallBackImpl.kt\ncn/sast/dataflow/interprocedural/check/callback/CallerSiteCBImpl\n*L\n63#1:165\n*E\n"])
public class CallerSiteCBImpl(hf: AbstractHeapFactory<IValue>, caller: SootMethod, stmt: Stmt, out: Builder<IValue>, returnType: Type, env: HookEnv) :
   ICallerSiteCBImpl {
   public open val hf: AbstractHeapFactory<IValue>
   public open val caller: SootMethod
   public open val stmt: Stmt

   public open var out: Builder<IValue>
      internal final set

   private final val returnType: Type
   public open val env: HookEnv

   public open val newEnv: AnyNewExprEnv
      public open get() {
         return new AnyNewExprEnv(this.getCaller(), this.getStmt() as Unit);
      }


   public open val global: IHeapValues<IValue>
      public open get() {
         return if (this.getHf().getVg().getStaticFieldTrackingMode() != StaticFieldTrackingMode.None)
            this.getHf().push(this.getEnv(), this.getHf().getVg().getGLOBAL_SITE()).popHV()
            else
            this.getHf().empty();
      }


   public open var `return`: IHeapValues<IValue>
      internal final set

   public open val `this`: IHeapValues<IValue>
      public open get() {
         return this.arg(-1);
      }


   init {
      this.hf = hf;
      this.caller = caller;
      this.stmt = stmt;
      this.out = out;
      this.returnType = returnType;
      this.env = env;
      val var10000: AbstractHeapFactory = this.getHf();
      val var10001: HeapValuesEnv = this.getEnv();
      val var10002: AbstractHeapFactory = this.getHf();
      val var10003: HeapValuesEnv = this.getEnv();
      val var10004: Type = this.returnType;
      val `$this$leftOp$iv`: Unit = this.getStmt() as Unit;
      val var10005: Value = if ((`$this$leftOp$iv` as? DefinitionStmt) != null) (`$this$leftOp$iv` as? DefinitionStmt).getLeftOp() else null;
      this.return = var10000.push(var10001, var10002.newSummaryVal(var10003, var10004, if (var10005 != null) var10005 as Serializable else "return"))
         .markSummaryReturnValueInCalleeSite()
         .popHV();
   }

   public override fun argToValue(argIndex: Int): Any {
      val iee: InvokeExpr = this.getStmt().getInvokeExpr();
      val var3: Pair = SootUtilsKt.argToOpAndType(iee, argIndex);
      val sootValue: Value = var3.component1() as Value;
      val type: Type = var3.component2() as Type;
      return sootValue;
   }

   public open fun arg(argIndex: Int): IHeapValues<IValue> {
      if (!this.getStmt().containsInvokeExpr()) {
         throw new IllegalStateException(("env: ${this.getEnv()}\nstmt = ${this.getStmt()}\nargIndex=$argIndex").toString());
      } else {
         val iee: InvokeExpr = this.getStmt().getInvokeExpr();
         val var3: Pair = SootUtilsKt.argToOpAndType(iee, argIndex);
         return this.getOut().getOfSootValue(this.getEnv(), var3.component1() as Value, var3.component2() as Type);
      }
   }

   public class EvalCall(delegate: CallerSiteCBImpl) : ICallerSiteCB.IEvalCall<IHeapValues<IValue>, IFact.Builder<IValue>>, ICallerSiteCBImpl {
      private final val delegate: CallerSiteCBImpl

      public open var isEvalAble: Boolean
         internal final set

      public open val caller: SootMethod
      public open val env: HookEnv
      public open val global: IHeapValues<IValue>
      public open val hf: AbstractHeapFactory<IValue>
      public open val newEnv: AnyNewExprEnv

      public open var out: Builder<IValue>
         internal final set

      public open var `return`: IHeapValues<IValue>
         internal final set

      public open val stmt: Stmt
      public open val `this`: IHeapValues<IValue>

      init {
         this.delegate = delegate;
         this.isEvalAble = true;
      }

      public fun emit(fact: IIFact<IValue>) {
      }

      public open fun arg(argIndex: Int): IHeapValues<IValue> {
         return this.delegate.arg(argIndex);
      }

      public override fun argToValue(argIndex: Int): Any {
         return this.delegate.argToValue(argIndex);
      }
   }

   public class PostCall(delegate: CallerSiteCBImpl) : ICallerSiteCB.IPostCall<IHeapValues<IValue>, IFact.Builder<IValue>>, ICallerSiteCBImpl {
      private final val delegate: CallerSiteCBImpl
      public open val caller: SootMethod
      public open val env: HookEnv
      public open val global: IHeapValues<IValue>
      public open val hf: AbstractHeapFactory<IValue>
      public open val newEnv: AnyNewExprEnv

      public open var out: Builder<IValue>
         internal final set

      public open var `return`: IHeapValues<IValue>
         internal final set

      public open val stmt: Stmt
      public open val `this`: IHeapValues<IValue>

      init {
         this.delegate = delegate;
      }

      public fun emit(fact: IIFact<IValue>) {
      }

      public open fun arg(argIndex: Int): IHeapValues<IValue> {
         return this.delegate.arg(argIndex);
      }

      public override fun argToValue(argIndex: Int): Any {
         return this.delegate.argToValue(argIndex);
      }
   }

   public class PrevCall(delegate: CallerSiteCBImpl) : ICallerSiteCB.IPrevCall<IHeapValues<IValue>, IFact.Builder<IValue>>, ICallerSiteCBImpl {
      private final val delegate: CallerSiteCBImpl

      public open var `return`: IHeapValues<IValue>
         public open get() {
            throw new IllegalStateException("prev call has no return".toString());
         }

         public open set(value) {
            throw new IllegalStateException("prev call has no return".toString());
         }


      public open val caller: SootMethod
      public open val env: HookEnv
      public open val global: IHeapValues<IValue>
      public open val hf: AbstractHeapFactory<IValue>
      public open val newEnv: AnyNewExprEnv

      public open var out: Builder<IValue>
         internal final set

      public open val stmt: Stmt
      public open val `this`: IHeapValues<IValue>

      init {
         this.delegate = delegate;
      }

      public fun emit(fact: IIFact<IValue>) {
      }

      public open fun arg(argIndex: Int): IHeapValues<IValue> {
         return this.delegate.arg(argIndex);
      }

      public override fun argToValue(argIndex: Int): Any {
         return this.delegate.argToValue(argIndex);
      }
   }
}
