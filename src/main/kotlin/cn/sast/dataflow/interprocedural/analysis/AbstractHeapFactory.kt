package cn.sast.dataflow.interprocedural.analysis

import cn.sast.api.util.SootUtilsKt
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.check.PathFactory
import cn.sast.idfa.check.ICallCB
import com.feysh.corax.config.api.IExpr
import mu.KLogger
import mu.KotlinLogging
import soot.Type
import soot.Unit
import soot.jimple.AnyNewExpr
import soot.jimple.BinopExpr
import soot.jimple.Constant
import soot.jimple.UnopExpr

public abstract class AbstractHeapFactory<V> : IHeapValuesFactory<V> {
   public final lateinit var constantPoolObjectData: Builder<Any>
      internal set

   public open var logger: KLogger
      internal final set

   public abstract val vg: IVGlobal
   public abstract val nullConst: Any

   open fun AbstractHeapFactory() {
      val var10001: KotlinLogging = KotlinLogging.INSTANCE;
      val var10002: java.lang.String = (AbstractHeapFactory::class).getQualifiedName();
      this.logger = var10001.logger(var10002);
   }

   public abstract fun anyNewVal(newExprEnv: AnyNewExprEnv, newExr: AnyNewExpr): Any {
   }

   public abstract fun newSummaryVal(env: HeapValuesEnv, type: Type, special: Any): Any {
   }

   public abstract fun canStore(receivers: IHeapValues<Any>, receiverType: Type): IHeapValues<Any> {
   }

   public abstract fun newConstVal(constant: Constant, type: Type): Any {
   }

   public abstract fun env(node: Unit): HeapValuesEnv {
   }

   public abstract fun env(ctx: AIContext, node: Unit): HookEnv {
   }

   public abstract fun getBooleanValue(v: Any, checkType: Boolean = true): Boolean? {
   }

   public abstract fun getIntValue(v: Any, checkType: Boolean = true): Int? {
   }

   public abstract fun newReNewInterface(orig: MutableSet<Any>): IReNew<Any> {
   }

   public abstract fun push(env: HeapValuesEnv, alloc: Any): JOperatorV<Any> {
   }

   public abstract fun push(env: HeapValuesEnv, value: CompanionV<Any>): JOperatorC<Any> {
   }

   public abstract fun push(env: HeapValuesEnv, value: IHeapValues<Any>): JOperatorHV<Any> {
   }

   public abstract fun getPathFactory(): PathFactory<Any> {
   }

   public fun toConstVal(v: Any): Any {
      val var2: Pair = SootUtilsKt.constOf(v);
      return this.newConstVal(var2.component1() as Constant, var2.component2() as Type);
   }

   public abstract fun resolveOp(env: HeapValuesEnv, vararg ops: IHeapValues<Any>?): IOpCalculator<Any> {
   }

   public abstract fun resolveCast(env: HeapValuesEnv, fact: Builder<Any>, toType: Type, fromValues: IHeapValues<Any>): IOpCalculator<Any>? {
   }

   public abstract fun resolveInstanceOf(env: HeapValuesEnv, fromValues: IHeapValues<Any>, checkType: Type): IOpCalculator<Any> {
   }

   public abstract fun resolveUnop(env: HeapValuesEnv, fact: IIFact<Any>, opValues: IHeapValues<Any>, expr: UnopExpr, resType: Type): IOpCalculator<Any> {
   }

   public abstract fun resolveBinop(
      env: HeapValuesEnv,
      fact: Builder<Any>,
      op1Values: IHeapValues<Any>,
      op2Values: IHeapValues<Any>,
      expr: BinopExpr,
      resType: Type
   ): IOpCalculator<Any> {
   }

   public abstract fun resolve(env: HeapValuesEnv, atCall: ICallCB<IHeapValues<Any>, Builder<IValue>>, iExpr: IExpr): Sequence<Any> {
   }

   override fun emptyBuilder(): IHeapValuesBuilder<V> {
      return IHeapValuesFactory.DefaultImpls.emptyBuilder(this);
   }
}
