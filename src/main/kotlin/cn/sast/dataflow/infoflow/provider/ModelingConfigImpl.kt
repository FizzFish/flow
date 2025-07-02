package cn.sast.dataflow.infoflow.provider

import cn.sast.coroutines.caffine.impl.FastCacheImpl
import cn.sast.dataflow.util.ConfigInfoLogger
import com.feysh.corax.cache.coroutines.FastCache
import com.feysh.corax.config.api.*
import com.feysh.corax.config.api.baseimpl.AIAnalysisBaseImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import soot.SootField
import soot.SootMethod
import soot.Type
import soot.jimple.infoflow.methodSummary.data.sourceSink.*
import soot.jimple.infoflow.methodSummary.data.summary.*
import soot.jimple.infoflow.taintWrappers.AccessPathFragment

/**
 * 将 Corax 的“建模 DSL”转换为 FlowDroid 可识别的 MethodSummary。
 *
 * 这里只演示核心结构与辅助函数，复杂的语义建模请在 TODO 处补充。
 */
class ModelingConfigImpl(
   private val provider: MethodSummaryProvider,
   override val preAnalysis: PreAnalysisApi
) : AIAnalysisBaseImpl() {

   /* ---------- 基础设施 ---------- */

   override val fastCache: FastCache
      get() = FastCacheImpl.INSTANCE

   override val scope: CoroutineScope
      get() = GlobalScope                               // 可替换为自定义 scope

   override val error: ConfigInfoLogger = ConfigInfoLogger()

   /* ---------- 辅助：Source / Sink / Clear ---------- */

   private fun createSource(
      matchStrict: Boolean,
      loc: MLocal,
      baseType: String?,
      fields: List<String>,
      fieldTypes: List<String>
   ): FlowSource? {
      val ap = if (fields.isEmpty())
         null
      else
         AccessPathFragment(fields.toMutableList(), fieldTypes.toMutableList())

      return when (loc) {
         is MReturn -> null
         is MParameter -> when (loc.index) {
            -1  -> FlowSource(SourceSinkType.Field, baseType, ap, null, matchStrict)
            else -> FlowSource(SourceSinkType.Parameter, loc.index, baseType, ap, null, matchStrict)
         }
         else -> null
      }
   }

   private fun createSink(
      matchStrict: Boolean,
      taintSubFields: Boolean,
      loc: MLocal,
      baseType: String?,
      fields: List<String>,
      fieldTypes: List<String>
   ): FlowSink? {
      val ap = if (fields.isEmpty())
         null
      else
         AccessPathFragment(fields.toMutableList(), fieldTypes.toMutableList())

      return when (loc) {
         is MReturn -> FlowSink(SourceSinkType.Return, baseType, ap, taintSubFields, null, matchStrict)
         is MParameter -> when (loc.index) {
            -1  -> FlowSink(SourceSinkType.Field,  baseType, ap, taintSubFields, null, matchStrict)
            else -> FlowSink(SourceSinkType.Parameter, loc.index, baseType, ap, taintSubFields, null, matchStrict)
         }
         else -> null
      }
   }

   private fun createClear(
      loc: MLocal,
      baseType: String?,
      fields: List<String>,
      fieldTypes: List<String>
   ): FlowClear? {
      val ap = if (fields.isEmpty())
         null
      else
         AccessPathFragment(fields.toMutableList(), fieldTypes.toMutableList())

      return when (loc) {
         is MParameter -> when (loc.index) {
            -1  -> FlowClear(SourceSinkType.Field, baseType, ap, null)
            else -> FlowClear(SourceSinkType.Parameter, loc.index, baseType, ap, null)
         }
         else -> null            // return / MGlobal 暂不支持 clear
      }
   }

   /* ---------- AIAnalysisBaseImpl 回调 ---------- */

   /** 将 DSL 的一条语句映射到 MethodSummary。示例实现仅作演示，可按需增强。 */
   override fun addStmt(
      decl: IJDecl,
      config: (MethodConfig) -> Unit,
      stmt: IStmt
   ) {
      if (decl !is IMethodDecl) return

      // 找到所有实现/覆写的方法
      val targets: Set<SootMethod> = buildSet {
         decl.match.matched(null).forEach { add(it) }
         addAll(findAllOverrideMethodsOfMethod(decl.match.matched(null).firstOrNull() ?: return))
      }

      // 简化：每条语句直接转成 “从参数 0 → 返回值” 的伪 flow
      // 复杂逻辑请替换为真正的 stmt 解析
      for (impl in targets) {
         val classSummaries = ClassMethodSummaries(impl.declaringClass.name)
         val summary = classSummaries.methodSummaries
         val from = createSource(false, MParameter(0), null, emptyList(), emptyList()) ?: continue
         val to   = createSink(false, false, MReturn, null, emptyList(), emptyList()) ?: continue
         summary.addFlow(MethodFlow(impl.subSignature, from, to, false, false, false, false))
         provider.addMethodSummaries(classSummaries)
      }
   }

   override fun check(
      decl: IJDecl,
      config: (MethodConfig) -> Unit,
      expr: IBoolExpr,
      checkType: CheckType,
      env: (BugMessage.Env) -> Unit
   ) {
      /* 建模阶段无需额外处理 check */
   }

   override fun eval(
      decl: IJDecl,
      config: (MethodConfig) -> Unit,
      expr: IExpr,
      accept: (Any) -> Unit
   ) {
      /* 建模阶段无需 eval */
   }

   override fun validate() {
      /* 校验逻辑（可选） */
   }

   /* ---------- 静态工具 ---------- */

   companion object {

      /**
       * 把 field 链（可能包含 `SubFields` 等特殊节点）转换为
       *  (字段签名列表, 字段类型列表, 是否包含子字段) 并交给回调。
       */
      fun transFields(
         baseType: String?,
         fields: List<IClassField>,
         res: (fieldSigs: List<String>, fieldTypes: List<String>, subFields: Boolean) -> Unit
      ) {
         if (fields.isEmpty()) {
            res(emptyList(), emptyList(), true)
            return
         }

         // 若最后一个是 MapKeys/MapValues 之类特殊节点，去掉再匹配 sootField
         val effectiveFields =
            if (fields.last() is ClassField || fields.last() is SubFields) fields
            else fields.dropLast(1)

         val accessPaths = FieldFinder(baseType, effectiveFields).sootFields()

         for (ap in accessPaths) {
            val sigs  = ap.sootField.map { it.signature }
            val types = ap.sootField.mapNotNull { (it.type as? Type)?.toString() }
            res(sigs, types, ap.subFields)
         }
      }

      /**
       * 解析 `expr` 中出现的 **最终本地变量** 及其字段链，
       * 调用 `cb(MLocal, baseType, fields)`。
       */
      fun getAccessPath(
         method: SootMethod,
         fields: List<IClassField> = emptyList(),
         expr: IExpr,
         cb: (loc: MLocal, baseType: String?, fields: List<IClassField>) -> Unit
      ) {
         fun add(e: IExpr, cur: List<IClassField>) {
            when (e) {
               is IIexLoad -> {
                  val loc = e.op
                  val baseType = method.baseType(loc)?.toString()
                  cb(loc, baseType, cur)
               }
               is IIexGetField -> add(e.base, e.accessPath + cur)
               is IUnOpExpr -> add(e.op1, cur)
               is BinOpExpr -> {
                  add(e.op1, cur)
                  add(e.op2, cur)
               }
               else -> { /* 常量等忽略 */ }
            }
         }
         add(expr, fields)
      }
   }
}
