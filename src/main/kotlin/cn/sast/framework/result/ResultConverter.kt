package cn.sast.framework.result

import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.Report
import cn.sast.dataflow.infoflow.provider.BugTypeProvider
import cn.sast.framework.engine.PreAnalysisReportEnv
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import soot.Scene
import soot.SootMethod
import soot.Unit
import soot.jimple.Stmt
import soot.jimple.infoflow.data.AccessPath
import soot.jimple.infoflow.results.DataFlowResult
import soot.jimple.infoflow.results.ResultSinkInfo
import soot.jimple.infoflow.results.ResultSourceInfo
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinition
import soot.jimple.infoflow.sourcesSinks.definitions.MethodSourceSinkDefinition
import soot.tagkit.AbstractHost

@SourceDebugExtension(["SMAP\nResultConverter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResultConverter.kt\ncn/sast/framework/result/ResultConverter\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,110:1\n1557#2:111\n1628#2,2:112\n1630#2:115\n1557#2:116\n1628#2,3:117\n1#3:114\n*S KotlinDebug\n*F\n+ 1 ResultConverter.kt\ncn/sast/framework/result/ResultConverter\n*L\n78#1:111\n78#1:112,2\n78#1:115\n106#1:116\n106#1:117,3\n*E\n"])
public class ResultConverter(info: SootInfoCache?) {
   public final val info: SootInfoCache?

   init {
      this.info = info;
   }

   private fun writeAccessPath(accessPath: AccessPath, simple: Boolean = false): String {
      if (!simple) {
         val var7: java.lang.String = accessPath.toString();
         return var7;
      } else {
         val b: StringBuilder = new StringBuilder();
         if (accessPath.getPlainValue() != null) {
            b.append(accessPath.getPlainValue().toString());
         }

         if (accessPath.getBaseType() != null) {
            b.append(accessPath.getBaseType().toString());
         }

         if (accessPath.getFragmentCount() > 0) {
            var i: Int = 0;

            for (int var5 = accessPath.getFragmentCount(); i < var5; i++) {
               b.append(".").append(accessPath.getFragments()[i].getField().toString());
            }
         }

         b.append(if (accessPath.getTaintSubFields()) "*" else "");
         val var10000: java.lang.String = b.toString();
         return var10000;
      }
   }

   public fun getReport(checkType: CheckType, env: PreAnalysisReportEnv): Report {
      return Report.Companion.of$default(
         Report.Companion, this.info, env.getFile(), env.getEnv().getRegion().getImmutable(), checkType, env.getEnv(), null, 32, null
      );
   }

   public fun getReport(icfg: IInfoflowCFG, result: DataFlowResult, bugTypeProvider: BugTypeProvider, serializeTaintPath: Boolean = true): List<Report> {
      val sink: ResultSinkInfo = result.getSink();
      val stmt: Stmt = sink.getStmt();
      val method: SootMethod = icfg.getMethodOf(sink.getStmt()) as SootMethod;
      val definition: ISourceSinkDefinition = sink.getDefinition();
      if (definition !is MethodSourceSinkDefinition) {
         logger.warn(ResultConverter::getReport$lambda$0);
         return CollectionsKt.emptyList();
      } else {
         val sinkMethod: SootMethod = Scene.v().grabMethod((definition as MethodSourceSinkDefinition).getMethod().getSignature());
         if (sinkMethod == null) {
            logger.warn(ResultConverter::getReport$lambda$1);
            return CollectionsKt.emptyList();
         } else {
            val source: ResultSourceInfo = result.getSource();
            val var10000: java.util.List;
            if (serializeTaintPath && source.getPath() != null) {
               val var43: Array<Stmt> = source.getPath();
               val checkTypes: java.lang.Iterable = ArraysKt.getIndices(var43) as java.lang.Iterable;
               val `$this$map$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(checkTypes, 10));
               val `$this$mapTo$iv$iv`: java.util.Iterator = checkTypes.iterator();

               while ($this$mapTo$iv$iv.hasNext()) {
                  val `destination$iv$iv`: Int = (`$this$mapTo$iv$iv` as IntIterator).nextInt();
                  val var44: Stmt = source.getPath()[`destination$iv$iv`];
                  val type: SootMethod = icfg.getMethodOf(var44) as SootMethod;
                  val var45: AccessPath = source.getPathAccessPaths()[`destination$iv$iv`];
                  val ap: java.lang.String = writeAccessPath$default(this, var45, false, 2, null);
                  var var46: Region = Region.Companion.invoke(var44 as Unit);
                  if (var46 == null) {
                     if (this.info != null) {
                        val it: SootInfoCache = this.info;
                        val var47: Region.Companion = Region.Companion;
                        var46 = var47.invoke(it, type as AbstractHost);
                     } else {
                        var46 = null;
                     }

                     if (var46 == null) {
                        var46 = Region.Companion.getERROR();
                     }
                  }

                  val var10002: java.util.Map = MapsKt.mapOf(TuplesKt.to(Language.EN, "`$ap` is tainted after `$var44`"));
                  val var10003: ClassResInfo.Companion = ClassResInfo.Companion;
                  `$this$map$iv`.add(new BugPathEvent(var10002, var10003.of(type), var46, null, 8, null));
               }

               var10000 = `$this$map$iv` as java.util.List;
            } else {
               var10000 = CollectionsKt.emptyList();
            }

            val events: java.util.List = var10000;
            val var29: java.util.Set = bugTypeProvider.lookUpCheckType(sinkMethod);
            if (var29.isEmpty()) {
               logger.warn(ResultConverter::getReport$lambda$4);
            }

            val var48: Region.Companion = Region.Companion;
            var var49: Region = var48.invoke(stmt as Unit);
            if (var49 == null) {
               if (this.info != null) {
                  val var35: SootInfoCache = this.info;
                  val var50: Region.Companion = Region.Companion;
                  var49 = var50.invoke(var35, method as AbstractHost);
               } else {
                  var49 = null;
               }

               if (var49 == null) {
                  var49 = Region.Companion.getERROR();
               }
            }

            val var30: Region = var49;
            val var31: DefaultEnv = new DefaultEnv(var49.getMutable(), null, null, null, null, null, null, null, null, 510, null);
            var31.setCallSite(stmt as Unit);
            var31.setClazz(method.getDeclaringClass());
            var31.setContainer(method);
            var31.setCallee(sinkMethod);
            var31.setMethod(sinkMethod);
            val var10001: Stmt = if (java.lang.Boolean.valueOf(stmt.containsInvokeExpr())) stmt else null;
            var31.setInvokeExpr(if (var10001 != null) var10001.getInvokeExpr() else null);
            val env: DefaultEnv = var31;
            val var32: java.lang.Iterable = var29;
            val var36: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var29, 10));

            for (Object item$iv$iv : $this$map$iv) {
               val var41: CheckType = var40 as CheckType;
               val var51: Report.Companion = Report.Companion;
               val var52: SootInfoCache = this.info;
               val var53: ClassResInfo.Companion = ClassResInfo.Companion;
               var36.add(var51.of(var52, var53.of(method), var30, var41, env, events));
            }

            return var36 as MutableList<Report>;
         }
      }
   }

   @JvmStatic
   fun `getReport$lambda$0`(`$definition`: ISourceSinkDefinition): Any {
      return "Definition: $`$definition` is not a MethodSourceSinkDefinition.";
   }

   @JvmStatic
   fun `getReport$lambda$1`(`$definition`: ISourceSinkDefinition): Any {
      return "Soot can not find method: $`$definition`";
   }

   @JvmStatic
   fun `getReport$lambda$4`(`$sinkMethod`: SootMethod): Any {
      return "could not find any checkTypes from bugTypeProvider at sink method: $`$sinkMethod`";
   }

   @JvmStatic
   fun `logger$lambda$9`(): kotlin.Unit {
      return kotlin.Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
