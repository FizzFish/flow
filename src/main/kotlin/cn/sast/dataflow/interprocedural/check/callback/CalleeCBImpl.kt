package cn.sast.dataflow.interprocedural.check.callback

import cn.sast.api.config.StaticFieldTrackingMode
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.HookEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.idfa.check.ICalleeCB
import soot.RefType
import soot.Scene
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.jimple.Stmt

public class CalleeCBImpl(hf: AbstractHeapFactory<IValue>, callee: SootMethod, stmt: Stmt, out: Builder<IValue>, env: HookEnv) : ICalleeSiteCBImpl {
   public open val hf: AbstractHeapFactory<IValue>
   public open val callee: SootMethod
   public open val stmt: Stmt

   public open var out: Builder<IValue>
      internal final set

   public open val env: HookEnv

   public open val newEnv: AnyNewExprEnv
      public open get() {
         return new AnyNewExprEnv(this.getCallee(), this.getStmt() as Unit);
      }


   public final val globalStaticObject: Pair<String, RefType>
      public final get() {
         return TuplesKt.to(this.getHf().getVg().getGLOBAL_LOCAL(), Scene.v().getObjectType());
      }


   public open val global: IHeapValues<IValue>
      public open get() {
         return if (this.getHf().getVg().getStaticFieldTrackingMode() != StaticFieldTrackingMode.None)
            this.getHf().push(this.getEnv(), this.getHf().getVg().getGLOBAL_SITE()).popHV()
            else
            this.getHf().empty();
      }


   public open val `this`: IHeapValues<IValue>
      public open get() {
         return this.arg(-1);
      }


   public open var `return`: IHeapValues<IValue>
      public open get() {
         val var10000: IHeapValues = this.getOut().getTargetsUnsafe(this.getHf().getVg().getRETURN_LOCAL());
         if (var10000 != null) {
            return var10000;
         } else {
            val returnType: Type = this.getCallee().getReturnType();
            val var5: AbstractHeapFactory = this.getHf();
            val var10001: HeapValuesEnv = this.getEnv();
            val var10002: AbstractHeapFactory = this.getHf();
            val var10003: HeapValuesEnv = this.getEnv();
            val var2: IHeapValues = var5.push(var10001, var10002.newSummaryVal(var10003, returnType, this.getHf().getVg().getRETURN_LOCAL()))
               .markSummaryReturnValueInCalleeSite()
               .popHV();
            IFact.Builder.DefaultImpls.assignNewExpr$default(this.getOut(), this.getEnv(), this.getHf().getVg().getRETURN_LOCAL(), var2, false, 8, null);
            return var2;
         }
      }

      public open set(value) {
         IFact.Builder.DefaultImpls.assignNewExpr$default(this.getOut(), this.getEnv(), this.getHf().getVg().getRETURN_LOCAL(), value, false, 8, null);
      }


   init {
      this.hf = hf;
      this.callee = callee;
      this.stmt = stmt;
      this.out = out;
      this.env = env;
   }

   public open fun arg(argIndex: Int): IHeapValues<IValue> {
      var var10000: IHeapValues = this.getOut().getTargetsUnsafe(argIndex);
      if (var10000 == null) {
         var10000 = this.getHf().empty();
      }

      return var10000;
   }

   public override fun argToValue(argIndex: Int): Any {
      return argIndex;
   }

   public class EvalCall(delegate: CalleeCBImpl) : ICalleeCB.IEvalCall<IHeapValues<IValue>, IFact.Builder<IValue>>, ICalleeSiteCBImpl {
      private final val delegate: CalleeCBImpl

      public open var isEvalAble: Boolean
         internal final set

      public open val callee: SootMethod
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

      public open fun arg(argIndex: Int): IHeapValues<IValue> {
         return this.delegate.arg(argIndex);
      }

      public override fun argToValue(argIndex: Int): Any {
         return this.delegate.argToValue(argIndex);
      }
   }

   public class PostCall(delegate: CalleeCBImpl) : ICalleeCB.IPostCall<IHeapValues<IValue>, IFact.Builder<IValue>>, ICalleeSiteCBImpl {
      private final val delegate: CalleeCBImpl
      public open val callee: SootMethod
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

      public open fun arg(argIndex: Int): IHeapValues<IValue> {
         return this.delegate.arg(argIndex);
      }

      public override fun argToValue(argIndex: Int): Any {
         return this.delegate.argToValue(argIndex);
      }
   }

   public class PrevCall(delegate: CalleeCBImpl) : ICalleeCB.IPrevCall<IHeapValues<IValue>, IFact.Builder<IValue>>, ICalleeSiteCBImpl {
      private final val delegate: CalleeCBImpl

      public open var `return`: IHeapValues<IValue>
         public open get() {
            throw new IllegalStateException("prev call has no return".toString());
         }

         public open set(value) {
            throw new IllegalStateException("prev call has no return".toString());
         }


      public open val callee: SootMethod
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

      public open fun arg(argIndex: Int): IHeapValues<IValue> {
         return this.delegate.arg(argIndex);
      }

      public override fun argToValue(argIndex: Int): Any {
         return this.delegate.argToValue(argIndex);
      }
   }
}
