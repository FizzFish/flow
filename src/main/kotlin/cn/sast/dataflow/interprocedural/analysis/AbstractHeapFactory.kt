package cn.sast.dataflow.interprocedural.analysis

import cn.sast.api.util.SootUtilsKt
import cn.sast.dataflow.interprocedural.check.PathFactory
import cn.sast.idfa.check.ICallCB
import com.feysh.corax.config.api.IExpr
import kotlinx.collections.immutable.persistentHashMapOf
import mu.KLogger
import mu.KotlinLogging
import soot.*
import soot.jimple.*

/**
 * 提供 **堆对象** 与 **常量值** 的统一创建接口；各语言/运行时需实现子类。
 */
abstract class AbstractHeapFactory<V> : IHeapValuesFactory<V> {

   /** “常量池” 对象（全局共用，故 `lateinit`） */
   lateinit var constantPoolObjectData: IFact.Builder<Any>

   /** 日志 */
   open var logger: KLogger = KotlinLogging.logger(this::class.qualifiedName!!)

   /* ---------------------- 必须由子类实现的 API ---------------------- */

   abstract val vg: IVGlobal
   abstract val nullConst: V

   abstract fun anyNewVal(newExprEnv: AnyNewExprEnv, newExpr: AnyNewExpr): V
   abstract fun newSummaryVal(env: HeapValuesEnv, type: Type, special: Any): V
   abstract fun canStore(receivers: IHeapValues<V>, receiverType: Type): IHeapValues<V>
   abstract fun newConstVal(constant: Constant, type: Type): V
   abstract fun env(node: Unit): HeapValuesEnv
   abstract fun env(ctx: AIContext, node: Unit): HookEnv
   abstract fun getBooleanValue(v: V, checkType: Boolean = true): Boolean?
   abstract fun getIntValue(v: V, checkType: Boolean = true): Int?
   abstract fun newReNewInterface(orig: MutableSet<V>): IReNew<V>
   abstract fun push(env: HeapValuesEnv, alloc: V): JOperatorV<V>
   abstract fun push(env: HeapValuesEnv, value: CompanionV<V>): JOperatorC<V>
   abstract fun push(env: HeapValuesEnv, value: IHeapValues<V>): JOperatorHV<V>
   abstract fun getPathFactory(): PathFactory<V>

   /** 将任意对象转为常量包装 */
   fun toConstVal(v: Any): V =
      SootUtilsKt.constOf(v).let { (c, t) -> newConstVal(c, t) }

   /* ------------------------- 计算 / 求值 --------------------------- */

   abstract fun resolveOp(
      env: HeapValuesEnv, vararg ops: IHeapValues<V>?
   ): IOpCalculator<V>

   abstract fun resolveCast(
      env: HeapValuesEnv,
      fact: IFact.Builder<V>,
      toType: Type,
      fromValues: IHeapValues<V>
   ): IOpCalculator<V>?

   abstract fun resolveInstanceOf(
      env: HeapValuesEnv,
      fromValues: IHeapValues<V>,
      checkType: Type
   ): IOpCalculator<V>

   abstract fun resolveUnop(
      env: HeapValuesEnv,
      fact: IIFact<V>,
      opValues: IHeapValues<V>,
      expr: UnopExpr,
      resType: Type
   ): IOpCalculator<V>

   abstract fun resolveBinop(
      env: HeapValuesEnv,
      fact: IFact.Builder<V>,
      op1Values: IHeapValues<V>,
      op2Values: IHeapValues<V>,
      expr: BinopExpr,
      resType: Type
   ): IOpCalculator<V>

   abstract fun resolve(
      env: HeapValuesEnv,
      atCall: ICallCB<IHeapValues<V>, IFact.Builder<IValue>>,
      iExpr: IExpr
   ): Sequence<V>

   /* -------------------------- 默认实现 ----------------------------- */

   override fun emptyBuilder(): IHeapValuesBuilder<V> =
      IHeapValuesFactory.DefaultImpls.emptyBuilder(this)
}
