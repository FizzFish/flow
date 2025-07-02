package cn.sast.dataflow.interprocedural.analysis

import cn.sast.api.report.Report
import cn.sast.dataflow.interprocedural.check.*
import cn.sast.dataflow.interprocedural.check.PathGeneratorImpl.Companion.getPathGenerator
import cn.sast.dataflow.interprocedural.check.checker.*
import cn.sast.graph.HashMutableDirectedGraph
import cn.sast.idfa.analysis.Context
import cn.sast.idfa.analysis.InterproceduralCFG
import com.feysh.corax.cache.analysis.SootInfoCache
import kotlinx.collections.immutable.toImmutableSet
import soot.SootMethod
import soot.Unit
import soot.jimple.Stmt
import soot.toolkits.graph.DirectedGraph

/**
 * 数据流分析时 _**单个方法上下文**_ 运行期信息。
 */
class AIContext(
   val info: SootInfoCache?,
   val icfg: InterproceduralCFG,
   val result: IIPAnalysisResultCollector,
   method: SootMethod,
   cfg: DirectedGraph<Unit>,
   reverse: Boolean,
   isAnalyzable: Boolean
) : Context<SootMethod, Unit, IFact<IValue>>(method, cfg, reverse, isAnalyzable) {

   /** 入口节点集（在入口未完整时用于延迟填充） */
   lateinit var entries: Set<EntryPath>

   /** 记录 “未闭合路径” 的报告 */
   val reports: MutableSet<NotCompleteReport> = linkedSetOf()

   /* ---------------- 报告生成 ---------------- */

   fun report(paths: IPath, ctx: ProgramStateContext, checker: Checker): Boolean {
      val generator = getPathGenerator()
      val graph = HashMutableDirectedGraph<IPath>()
      val heads = generator.getHeads(paths, graph)
      val entryPaths = mutableSetOf<EntryPath>()
      val nonEntry = mutableSetOf<IPath>()

      heads.forEach {
         if (it is EntryPath) entryPaths += it else nonEntry += it
      }

      if (entryPaths.isNotEmpty()) {
         reports += NotCompleteReport(entryPaths, paths, ctx, checker)
      }
      if (nonEntry.isNotEmpty()) {
         Companion.reportByEntry(
            entryPaths,
            generator,
            graph,
            nonEntry,
            ctx,
            checker,
            info,
            icfg,
            result
         )
         return true
      }
      if (entryPaths.isEmpty()) flushInvalidPathReports(paths, ctx, checker)
      return entryPaths.isNotEmpty()
   }

   private fun flushInvalidPathReports(sink: IPath, ctx: ProgramStateContext, checker: Checker) {
      val generator = getPathGenerator()
      val graph = HashMutableDirectedGraph<IPath>()
      Companion.reportByEntry(
         emptySet(),
         generator,
         graph,
         generator.getHeads(sink, graph),
         ctx,
         checker,
         info,
         icfg,
         result
      )
   }

   /** 将 callee 中未闭合报告合并到当前 ctx */
   fun activeReport(callee: AIContext, transfer: PointsToGraphBuilder.PathTransfer) {
      for (r in callee.reports) {
         val transformed = transfer.transform(r.sink, r.entries)
         report(transformed, r.ctx, r.define)
      }
   }

   /* ---------------- 嵌套类 / 静态工具 ---------------- */

   data class NotCompleteReport(
      val entries: Set<EntryPath>,
      val sink: IPath,
      val ctx: ProgramStateContext,
      val define: Checker
   )

   companion object {

      /** 从 IExpr + sink 语句中提取 “关键参数名” */
      fun findSinkFromExpr(expr: IExpr, sinkStmt: Stmt): List<Any> =
         PostCallStmtInfo(
            object : IIstStoreLocal(expr) {
               override fun getLocal() = MGlobal
               override fun getValue() = expr
               override fun <TResult> accept(visitor: IModelStmtVisitor<TResult>) =
                  visitor.visit(this)
            },
            sinkStmt
         ).parameterNamesInUnitDefUse

      /**
       * 把完整路径以 **入口为界** 打包成报告并存入 [result]。
       */
      fun reportByEntry(
         notCompletePath: Set<EntryPath>,
         generator: PathGenerator<IPath>,
         graph: DirectedGraph<IPath>,
         heads: Set<IPath>,
         ctx: ProgramStateContext,
         checker: Checker,
         info: SootInfoCache?,
         icfg: InterproceduralCFG,
         result: IIPAnalysisResultCollector
      ) {
         val pathEventsMap = generator.flush(graph, heads)
         val printer = PathDiagnosticsGenerator(info, icfg, 0)

         val sinkInfo = run {
            val callSite = ctx.callSiteStmt
            val callee = ctx.callee1
            val keyArgs = findSinkFromExpr(ctx.guard, callSite).joinToString()
            mapOf(
               Language.ZH to "污点汇聚 Sink 点: `$callee`, 参数: `$keyArgs`".trimIndent(),
               Language.EN to "Taint sink: `$callee`, key args: `$keyArgs`".trimIndent()
            )
         }

         val sinkEvents = printer.emit(ctx.callSiteStmt, sinkInfo, null)

         // 过滤掉纯 merge / dummy 的路径；仅保留真正 bug 路径
         val bugEvents = pathEventsMap
            .filterNot { (k, v) ->
               k is LiteralPath &&
                       (v.size == 1 && v.first().size == 1) &&
                       (k as? LiteralPath)?.const is IexConst &&
                       (k.const as IexConst).type == IIexConst.Type.TaintSet
            }
            .values
            .flatten()
            .toImmutableSet()

         if (bugEvents.isEmpty()) return
         bugEvents.forEach { evList ->
            val report = Report.of(
               info,
               ctx.resInfo,
               ctx.region.immutable,
               checker.report,
               ctx,
               evList + sinkEvents
            )
            result.reportDataFlowBug(report)
         }
      }
   }
}
