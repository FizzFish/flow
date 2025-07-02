package cn.sast.dataflow.interprocedural.analysis

import cn.sast.api.report.Report
import cn.sast.dataflow.interprocedural.check.EntryPath
import cn.sast.dataflow.interprocedural.check.IPath
import cn.sast.dataflow.interprocedural.check.InvokeEdgePath
import cn.sast.dataflow.interprocedural.check.LiteralPath
import cn.sast.dataflow.interprocedural.check.MergePath
import cn.sast.dataflow.interprocedural.check.PathGenerator
import cn.sast.dataflow.interprocedural.check.PathGeneratorImpl
import cn.sast.dataflow.interprocedural.check.PostCallStmtInfo
import cn.sast.dataflow.interprocedural.check.PointsToGraphBuilder.PathTransfer
import cn.sast.dataflow.interprocedural.check.checker.IIPAnalysisResultCollector
import cn.sast.dataflow.interprocedural.check.checker.ProgramStateContext
import cn.sast.dataflow.interprocedural.check.checker.CheckerModeling.Checker
import cn.sast.dataflow.interprocedural.check.printer.PathDiagnosticsGenerator
import cn.sast.dataflow.interprocedural.check.printer.SimpleUnitPrinter
import cn.sast.graph.HashMutableDirectedGraph
import cn.sast.idfa.analysis.Context
import cn.sast.idfa.analysis.InterproceduralCFG
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.IExpr
import com.feysh.corax.config.api.IIexConst
import com.feysh.corax.config.api.IIstStoreLocal
import com.feysh.corax.config.api.IModelStmtVisitor
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.MGlobal
import com.feysh.corax.config.api.MLocal
import com.feysh.corax.config.api.baseimpl.IexConst
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import soot.SootMethod
import soot.Unit
import soot.jimple.Stmt
import soot.toolkits.graph.DirectedGraph

public class AIContext(info: SootInfoCache?,
   icfg: InterproceduralCFG,
   result: IIPAnalysisResultCollector,
   method: SootMethod,
   cfg: DirectedGraph<Unit>,
   reverse: Boolean,
   isAnalyzable: Boolean
) : Context(method, cfg, reverse, isAnalyzable) {
   public final val info: SootInfoCache?
   public final val icfg: InterproceduralCFG
   public final val result: IIPAnalysisResultCollector

   public final lateinit var entries: Set<EntryPath>
      internal set

   public final val reports: MutableSet<cn.sast.dataflow.interprocedural.analysis.AIContext.NotCompleteReport>

   init {
      this.info = info;
      this.icfg = icfg;
      this.result = result;
      this.reports = new LinkedHashSet<>();
   }

   public fun report(paths: IPath, ctx: ProgramStateContext, definition: Checker): Boolean {
      val generator: PathGenerator = PathGeneratorImpl.Companion.getPathGenerator();
      val directGraph: HashMutableDirectedGraph = new HashMutableDirectedGraph();
      val heads: java.util.Set = generator.getHeads(paths, directGraph);
      val notEntryPaths: java.util.Set = new LinkedHashSet();
      val notCompleteReport: java.util.Set = new LinkedHashSet();

      for (IPath head : heads) {
         if (head is EntryPath) {
            notCompleteReport.add(head);
         } else {
            notEntryPaths.add(head);
         }
      }

      if (!notCompleteReport.isEmpty()) {
         this.reports.add(new AIContext.NotCompleteReport(notCompleteReport, paths, ctx, definition));
      }

      if (!notEntryPaths.isEmpty()) {
         Companion.reportByEntry(
            notCompleteReport, generator, directGraph as DirectedGraph<IPath>, notEntryPaths, ctx, definition, this.info, this.icfg, this.result
         );
         return true;
      } else {
         if (notCompleteReport.isEmpty()) {
            this.flushInvalidPathReports(paths, ctx, definition);
         }

         return !notCompleteReport.isEmpty();
      }
   }

   private fun flushInvalidPathReports(sink: IPath, ctx: ProgramStateContext, definition: Checker) {
      val generator: PathGenerator = PathGeneratorImpl.Companion.getPathGenerator();
      val directGraph: HashMutableDirectedGraph = new HashMutableDirectedGraph();
      Companion.reportByEntry(
         SetsKt.emptySet(),
         generator,
         directGraph as DirectedGraph<IPath>,
         generator.getHeads(sink, directGraph),
         ctx,
         definition,
         this.info,
         this.icfg,
         this.result
      );
   }

   public fun activeReport(callee: AIContext, pathTransfer: PathTransfer) {
      for (AIContext.NotCompleteReport report : callee.reports) {
         val var10000: InvokeEdgePath = pathTransfer.transform(report.getSink(), report.getEntries());
         this.report(var10000, report.getCtx(), report.getDefine());
      }
   }

   @SourceDebugExtension(["SMAP\nAIContext.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AIContext.kt\ncn/sast/dataflow/interprocedural/analysis/AIContext$Companion\n+ 2 SootUtils.kt\ncn/sast/api/util/SootUtilsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,192:1\n307#2:193\n1#3:194\n1#3:209\n774#4:195\n865#4,2:196\n669#4,11:198\n1557#4:210\n1628#4,3:211\n*S KotlinDebug\n*F\n+ 1 AIContext.kt\ncn/sast/dataflow/interprocedural/analysis/AIContext$Companion\n*L\n75#1:193\n75#1:194\n86#1:195\n86#1:196,2\n86#1:198,11\n110#1:210\n110#1:211,3\n*E\n"])
   public companion object {
      public fun findSinkFromExpr(expr: IExpr, sinkStmt: Stmt): List<Any> {
         return new PostCallStmtInfo(new IIstStoreLocal(expr) {
            {
               this.$expr = `$expr`;
            }

            @Override
            public MLocal getLocal() {
               return MGlobal.INSTANCE;
            }

            @Override
            public IExpr getValue() {
               return this.$expr;
            }

            @Override
            public <TResult> TResult accept(IModelStmtVisitor<TResult> visitor) {
               return (TResult)visitor.visit(this);
            }
         }, sinkStmt as Unit).getParameterNamesInUnitDefUse(sinkStmt as Unit);
      }

      public fun reportByEntry(
         notCompletePath: Set<EntryPath>,
         generator: PathGenerator<IPath>,
         directGraph: DirectedGraph<IPath>,
         entry: Set<IPath>,
         ctx: ProgramStateContext,
         definition: Checker,
         info: SootInfoCache?,
         icfg: InterproceduralCFG,
         result: IIPAnalysisResultCollector
      ) {
         val pathEventsMap: java.util.Map = generator.flush(directGraph, entry);
         val bugPathEvents: AIContext.Companion = this;
         val reports: PathDiagnosticsGenerator = new PathDiagnosticsGenerator(info, icfg, 0);
         val `$this$map$iv`: Stmt = ctx.getCallSiteStmt();
         val `found$iv`: java.lang.String = SimpleUnitPrinter.Companion
            .getStringOf(ctx.getCallee1(), null, if (`$this$map$iv`.containsInvokeExpr()) `$this$map$iv`.getInvokeExpr() else null, false);
         val `$i$f$map`: java.lang.String = CollectionsKt.joinToString$default(
            bugPathEvents.findSinkFromExpr(ctx.getGuard(), `$this$map$iv`), ", ", null, null, 0, null, null, 62, null
         );
         val sink: java.util.List = reports.emit(
            `$this$map$iv` as Unit,
            MapsKt.mapOf(
               new Pair[]{
                  TuplesKt.to(Language.ZH, "污点汇聚 Sink 点: `$`found$iv``, 关键参数: `$`$i$f$map`` 位于方法 ${ctx.getCallee1()}"),
                  TuplesKt.to(Language.EN, "Taint sink: `$`found$iv``, key argument: `$`$i$f$map`` of ${ctx.getCallee1()}")
               }
            ),
            null
         );
         val bugPathEventsMap: java.util.Map = new LinkedHashMap();

         for (Entry var31 : pathEventsMap.entrySet()) {
            val var34: IPath = var31.getKey() as IPath;
            val var37: java.util.List = var31.getValue() as java.util.List;
            var var44: java.lang.Iterable = var37;
            val var53: java.util.Collection = new ArrayList();

            for (Object element$iv$iv : $this$filter$iv) {
               if ((var24 as IPath) !is MergePath) {
                  var53.add(var24);
               }
            }

            var44 = var53 as java.util.List;
            var var51: Any = null;
            var var54: Boolean = false;
            val var56: java.util.Iterator = var44.iterator();

            var var63: Any;
            while (true) {
               if (!var56.hasNext()) {
                  var63 = if (!var54) null else var51;
                  break;
               }

               val var58: Any = var56.next();
               if (var58 as IPath is LiteralPath) {
                  if (var54) {
                     var63 = null;
                     break;
                  }

                  var51 = var58;
                  var54 = true;
               }
            }

            if ((var63 as IPath) !is LiteralPath
               || ((var63 as IPath) as LiteralPath).getConst() !is IexConst
               || (((var63 as IPath) as LiteralPath).getConst() as IexConst).getType() != IIexConst.Type.TaintSet
               || !(((var63 as IPath) as LiteralPath).getNode() == ctx.getCallSiteStmt())) {
               val var48: Pair = TuplesKt.to(var34, SequencesKt.toSet(PathGeneratorImpl.Companion.generateEvents(info, icfg, var37)));
               bugPathEventsMap.put(var48.getFirst(), var48.getSecond());
            }
         }

         if (!bugPathEventsMap.isEmpty()) {
            if (!notCompletePath.isEmpty() && bugPathEventsMap.size() == 1) {
               val var29: Pair = CollectionsKt.first(MapsKt.toList(bugPathEventsMap)) as Pair;
               val var32: IPath = var29.component1() as IPath;
               val var35: java.util.Set = var29.component2() as java.util.Set;
               if (var32 is LiteralPath && var35.size() == 1 && (CollectionsKt.first(var35) as java.util.List).size() == 1) {
                  return;
               }
            }

            val var46: java.util.Collection = CollectionsKt.toSet(CollectionsKt.flatten(bugPathEventsMap.values()));
            val var33: java.util.Collection = if (var46.isEmpty()) CollectionsKt.listOf(CollectionsKt.emptyList()) else var46;
            if (var33.isEmpty()) {
               throw new IllegalStateException("Check failed.".toString());
            } else {
               val var38: java.lang.Iterable = var33;
               val var50: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var33, 10));

               for (Object item$iv$iv : $this$map$iv) {
                  var50.add(
                     Report.Companion
                        .of(
                           info,
                           ctx.getResInfo(),
                           ctx.getRegion().getImmutable(),
                           definition.getReport(),
                           ctx,
                           CollectionsKt.plus(var57 as java.util.List, sink)
                        )
                  );
               }

               result.reportDataFlowBug(CollectionsKt.toList(var50 as java.util.List));
            }
         }
      }
   }

   public data class NotCompleteReport(entries: Set<EntryPath>, sink: IPath, ctx: ProgramStateContext, define: Checker) {
      public final val entries: Set<EntryPath>
      public final val sink: IPath
      public final val ctx: ProgramStateContext
      public final val define: Checker

      init {
         this.entries = entries;
         this.sink = sink;
         this.ctx = ctx;
         this.define = define;
      }

      public operator fun component1(): Set<EntryPath> {
         return this.entries;
      }

      public operator fun component2(): IPath {
         return this.sink;
      }

      public operator fun component3(): ProgramStateContext {
         return this.ctx;
      }

      public operator fun component4(): Checker {
         return this.define;
      }

      public fun copy(entries: Set<EntryPath> = this.entries, sink: IPath = this.sink, ctx: ProgramStateContext = this.ctx, define: Checker = this.define): cn.sast.dataflow.interprocedural.analysis.AIContext.NotCompleteReport {
         return new AIContext.NotCompleteReport(entries, sink, ctx, define);
      }

      public override fun toString(): String {
         return "NotCompleteReport(entries=${this.entries}, sink=${this.sink}, ctx=${this.ctx}, define=${this.define})";
      }

      public override fun hashCode(): Int {
         return ((this.entries.hashCode() * 31 + this.sink.hashCode()) * 31 + this.ctx.hashCode()) * 31 + this.define.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is AIContext.NotCompleteReport) {
            return false;
         } else {
            val var2: AIContext.NotCompleteReport = other as AIContext.NotCompleteReport;
            if (!(this.entries == (other as AIContext.NotCompleteReport).entries)) {
               return false;
            } else if (!(this.sink == var2.sink)) {
               return false;
            } else if (!(this.ctx == var2.ctx)) {
               return false;
            } else {
               return this.define == var2.define;
            }
         }
      }
   }
}
