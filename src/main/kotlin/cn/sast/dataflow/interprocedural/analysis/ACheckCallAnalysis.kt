package cn.sast.dataflow.interprocedural.analysis

import cn.sast.api.config.ExtSettings
import cn.sast.api.report.Counter
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl
import cn.sast.dataflow.util.SootUtilsKt
import cn.sast.idfa.analysis.InterproceduralCFG
import cn.sast.idfa.analysis.ForwardInterProceduralAnalysis.InvokeResult
import cn.sast.idfa.check.CallBackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import mu.KLogger
import mu.KotlinLogging
import soot.*
import soot.jimple.*
import soot.toolkits.graph.BriefUnitGraph
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.graph.UnitGraph
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2

/** 在 Jimple 上实现 “Call-Modeling + 结果回调” 的基类 */
abstract class ACheckCallAnalysis(
   hf: AbstractHeapFactory<IValue>,
   icfg: InterproceduralCFG
) : AJimpleInterProceduralAnalysis<IValue, AIContext>(hf, icfg) {

   /* ---------------- 公共可扩展成员 ---------------- */

   /** 回调管理器：允许对方法、调用点注册 eval/post 回调 */
   val callBackManager = CallBackManager()

   /** 由 `registerWrapper*` 生成的 summary 句柄 */
   val summaries: MutableList<SummaryHandlePackage<IValue>> = mutableListOf()

   /** 用于 “跳过分析” 的计数器 */
   private val excludeMethods = Counter<SootMethod>()

   /* ---------------- Wrapper 注册 ---------------- */

   fun registerWrapper(signature: String, isStatic: Boolean = false) {
      registerWrapper(SootUtilsKt.sootSignatureToRef(signature, isStatic))
   }

   fun registerWrapper(ref: SootMethodRef) {
      val sm = ref.resolve()
      val ug = runCatching { BriefUnitGraph(sm.retrieveActiveBody()) }.getOrNull() ?: return
      registerJimpleWrapper(sm.signature, ug)
   }

   fun registerClassAllWrapper(className: String) =
      Scene.v().getSootClassUnsafe(className, true)?.let { registerClassAllWrapper(it) }

   fun registerClassAllWrapper(sc: SootClass) {
      sc.methods.forEach { sm ->
         if (sm.source == null && !sm.hasActiveBody()) {
            logger.warn { "method source of $sm is null" }
            return@forEach
         }
         Scene.v().forceResolve(sm.declaringClass.name, SootClass.BODIES)
         sm.retrieveActiveBody()?.let { body ->
            registerJimpleWrapper(sm.signature, BriefUnitGraph(body))
         }
      }
   }

   fun registerJimpleWrapper(methodSignature: String, jimple: UnitGraph) {
      Scene.v().grabMethod(methodSignature)?.let { m ->
         callBackManager.putUnitGraphOverride(m, jimple)
      }
   }

   /* ---------------- 回调 API（对外开放） ---------------- */

   fun evalCallAtCaller(signature: String, cb: (CallerSiteCBImpl.EvalCall) -> Unit) {
      Scene.v().grabMethod(signature)?.let { m ->
         callBackManager.put(
            CallerSiteCBImpl.EvalCall::class.java,
            m,
            suspendConversion(cb)
         )
      }
   }

   fun postCallAtCaller(signature: String, cb: (CallerSiteCBImpl.PostCall) -> Unit) {
      Scene.v().grabMethod(signature)?.let { m ->
         callBackManager.put(
            CallerSiteCBImpl.PostCall::class.java,
            m,
            suspendConversion(cb)
         )
      }
   }

   fun evalCall(signature: String, cb: (CalleeCBImpl.EvalCall) -> Unit) {
      Scene.v().grabMethod(signature)?.let { m ->
         callBackManager.put(
            CalleeCBImpl.EvalCall::class.java,
            m,
            suspendConversion(cb)
         )
      }
   }

   /* ---------------- ForwardInterProceduralAnalysis 钩子 ---------------- */

   override fun getCfg(method: SootMethod, isAnalyzable: Boolean): DirectedGraph<Unit> =
      callBackManager.getUnitGraphOverride(method) ?: super.getCfg(method, isAnalyzable)

   override fun doAnalysis(
      entries: Collection<SootMethod>,
      methodsMustAnalyze: Collection<SootMethod>
   ) {
      summaries.forEach { it.register(this) }
      super.doAnalysis(entries, methodsMustAnalyze)
      excludeMethods.clear()
   }

   override fun isAnalyzable(callee: SootMethod, in1: IFact<IValue>): Boolean {
      // 1) 有 Wrapper 则一定可分析
      if (callBackManager.getUnitGraphOverride(callee) != null) return true

      // 2) 基础规则
      if (!super.isAnalyzable(callee, in1)) return false
      if (excludeSubSignature.contains(callee.subSignature)) return false

      // 3) 递增跳过过多调用
      val hit = excludeMethods[callee]
      if (hit > 0 && (!callee.declaringClass.isApplicationClass || hit > 2)) return false

      // 4) 按方法大小限幅
      val g = getCfg(callee, true)
      val limit = ExtSettings.dataFlowMethodUnitsSizeLimit
      return limit <= 0 || g.size() <= limit
   }

   /* ---------------- 辅助 ---------------- */

   /** 把普通 `(T)->Unit` 封装成 suspend-aware `Function2`（与 CallbackManager 签名一致） */
   private fun <T> suspendConversion(
      f: (T) -> Unit
   ): Function2<T, Continuation<Unit>, Any?> = { t, cont ->
      runBlocking { f(t) }
      Unit
   }

   /* ---------------- 日志 ---------------- */

   companion object {
      val logger: KLogger = KotlinLogging.logger {}
   }
}
