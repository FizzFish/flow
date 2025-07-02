package cn.sast.cli.command

import cn.sast.api.AnalyzerEnv
import cn.sast.api.config.BuiltinAnalysisConfig
import cn.sast.api.config.CheckerInfoGenResult
import cn.sast.api.config.ExtSettings
import cn.sast.api.config.MainConfig
import cn.sast.api.config.PreAnalysisCoroutineScope
import cn.sast.api.config.ProjectConfig
import cn.sast.api.config.SaConfig
import cn.sast.api.config.ScanFilter
import cn.sast.api.config.SrcPrecedence
import cn.sast.api.config.StaticFieldTrackingMode
import cn.sast.api.report.CheckType2StringKind
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.IResultCollector
import cn.sast.api.report.Report
import cn.sast.api.util.PhaseIntervalTimer
import cn.sast.cli.ApplicationKt
import cn.sast.cli.command.tools.CheckerInfoCompareOptions
import cn.sast.cli.command.tools.CheckerInfoGenerator
import cn.sast.cli.command.tools.CheckerInfoGeneratorOptions
import cn.sast.cli.command.tools.SubToolsOptions
import cn.sast.common.CustomRepeatingTimer
import cn.sast.common.FileSystemLocator
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.OS
import cn.sast.common.PathExtensionsKt
import cn.sast.common.Resource
import cn.sast.common.ResourceImplKt
import cn.sast.common.ResourceKt
import cn.sast.common.TimeUtilsKt
import cn.sast.common.FileSystemLocator.TraverseMode
import cn.sast.framework.AnalyzeTaskRunner
import cn.sast.framework.SootCtx
import cn.sast.framework.engine.BuiltinAnalysis
import cn.sast.framework.engine.FlowDroidEngine
import cn.sast.framework.engine.IPAnalysisEngine
import cn.sast.framework.engine.PreAnalysisImpl
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.incremental.IncrementalAnalyzeImplByChangeFiles
import cn.sast.framework.metrics.MetricsMonitor
import cn.sast.framework.plugin.CheckerFilterByName
import cn.sast.framework.plugin.ConfigPluginLoader
import cn.sast.framework.report.AbstractFileIndexer
import cn.sast.framework.report.FileSystemCacheLocator
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.report.NullWrapperFileGenerator
import cn.sast.framework.report.ProjectFileLocator
import cn.sast.framework.report.SqliteDiagnostics
import cn.sast.framework.report.AbstractFileIndexer.CompareMode
import cn.sast.framework.report.coverage.JacocoCompoundCoverage
import cn.sast.framework.result.IBuiltInAnalysisCollector
import cn.sast.framework.result.IMissingSummaryReporter
import cn.sast.framework.result.IPreAnalysisResultCollector
import cn.sast.framework.result.MissingSummaryReporter
import cn.sast.framework.result.OutputType
import cn.sast.framework.result.ResultCollector
import cn.sast.framework.result.ResultCounter
import cn.sast.framework.rewrite.IdentityStmt2MethodParamRegion
import cn.sast.framework.util.SootUtils
import cn.sast.framework.validator.AccuracyValidator
import cn.sast.idfa.analysis.ProcessInfoView
import cn.sast.idfa.analysis.UsefulMetrics
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.cache.AnalysisDataFactory
import com.feysh.corax.cache.analysis.CompilationUnitOfSCFactory
import com.feysh.corax.cache.analysis.SootHostExtInfoFactory
import com.feysh.corax.cache.analysis.SootHostExtend
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.utils.UtilsKt
import com.feysh.corax.config.builtin.checkers.DefineUnusedChecker
import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.ContextKt
import com.github.ajalt.clikt.core.GroupableOption
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.groups.ParameterGroup
import com.github.ajalt.clikt.parameters.options.Option
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.rendering.Theme
import com.github.javaparser.ast.body.BodyDeclaration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Closeable
import java.io.File
import java.io.OutputStreamWriter
import java.lang.management.ManagementFactory
import java.lang.reflect.Field
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.util.ArrayDeque
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Objects
import java.util.Map.Entry
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.enums.EnumEntries
import kotlin.io.path.PathsKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.SpreadBuilder
import kotlin.jvm.internal.Ref.ObjectRef
import kotlin.math.MathKt
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentSet.Builder
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mu.KLogger
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.slf4j.Log4jLogger
import org.eclipse.microprofile.metrics.Gauge
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import soot.PackManager
import soot.Scene
import soot.SootClass
import soot.Transform
import soot.Transformer
import soot.jimple.infoflow.InfoflowConfiguration
import soot.jimple.infoflow.cfg.BiDirICFGFactory
import soot.jimple.toolkits.callgraph.CallGraph
import soot.options.Options
import soot.tagkit.AbstractHost
import soot.tagkit.Host

@SourceDebugExtension(["SMAP\nFySastCli.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FySastCli.kt\ncn/sast/cli/command/FySastCli\n+ 2 EagerOption.kt\ncom/github/ajalt/clikt/parameters/options/EagerOptionKt\n+ 3 enum.kt\ncom/github/ajalt/clikt/parameters/types/EnumKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 5 enum.kt\ncom/github/ajalt/clikt/parameters/types/EnumKt$enum$3\n+ 6 Convert.kt\ncom/github/ajalt/clikt/parameters/options/OptionWithValuesKt__ConvertKt\n+ 7 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt\n+ 8 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 9 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 10 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 11 Timer.kt\ncn/sast/api/util/TimerKt\n+ 12 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,1247:1\n65#2,10:1248\n45#3,5:1258\n45#3,5:1293\n45#3,5:1304\n45#3,5:1333\n45#3,5:1355\n45#3,5:1377\n45#3,5:1388\n45#3,5:1399\n8541#4,2:1263\n8801#4,2:1265\n8804#4:1268\n8541#4,2:1298\n8801#4,2:1300\n8804#4:1303\n8541#4,2:1309\n8801#4,2:1311\n8804#4:1314\n8541#4,2:1338\n8801#4,2:1340\n8804#4:1343\n8541#4,2:1360\n8801#4,2:1362\n8804#4:1365\n8541#4,2:1382\n8801#4,2:1384\n8804#4:1387\n8541#4,2:1393\n8801#4,2:1395\n8804#4:1398\n8541#4,2:1404\n8801#4,2:1406\n8804#4:1409\n47#5:1267\n47#5:1302\n47#5:1313\n47#5:1342\n47#5:1364\n47#5:1386\n47#5:1397\n47#5:1408\n35#6,6:1269\n70#6:1275\n82#6,4:1276\n35#6,6:1280\n70#6:1286\n82#6,4:1287\n35#6,6:1344\n70#6:1350\n82#6,4:1351\n35#6,6:1366\n70#6:1372\n82#6,4:1373\n65#6,6:1422\n82#6,4:1428\n69#7:1291\n25#7:1292\n65#7,5:1315\n25#7:1320\n65#7,5:1321\n25#7:1326\n65#7,5:1327\n25#7:1332\n65#7,5:1410\n25#7:1415\n65#7,5:1416\n25#7:1421\n1#8:1432\n1485#9:1433\n1510#9,3:1434\n1513#9,3:1444\n1557#9:1447\n1628#9,3:1448\n1368#9:1451\n1454#9,2:1452\n774#9:1454\n865#9,2:1455\n1456#9,3:1457\n1797#9,3:1460\n1454#9,5:1463\n1368#9:1476\n1454#9,5:1477\n1454#9,5:1482\n1368#9:1487\n1454#9,5:1488\n1863#9,2:1493\n1755#9,3:1495\n1454#9,5:1498\n1863#9,2:1503\n1469#9,5:1505\n1557#9:1510\n1628#9,3:1511\n1557#9:1518\n1628#9,3:1519\n381#10,7:1437\n16#11,8:1468\n37#12,2:1514\n37#12,2:1516\n*S KotlinDebug\n*F\n+ 1 FySastCli.kt\ncn/sast/cli/command/FySastCli\n*L\n92#1:1248,10\n101#1:1258,5\n149#1:1293,5\n152#1:1304,5\n215#1:1333,5\n251#1:1355,5\n290#1:1377,5\n301#1:1388,5\n355#1:1399,5\n101#1:1263,2\n101#1:1265,2\n101#1:1268\n149#1:1298,2\n149#1:1300,2\n149#1:1303\n152#1:1309,2\n152#1:1311,2\n152#1:1314\n215#1:1338,2\n215#1:1340,2\n215#1:1343\n251#1:1360,2\n251#1:1362,2\n251#1:1365\n290#1:1382,2\n290#1:1384,2\n290#1:1387\n301#1:1393,2\n301#1:1395,2\n301#1:1398\n355#1:1404,2\n355#1:1406,2\n355#1:1409\n101#1:1267\n149#1:1302\n152#1:1313\n215#1:1342\n251#1:1364\n290#1:1386\n301#1:1397\n355#1:1408\n117#1:1269,6\n117#1:1275\n117#1:1276,4\n132#1:1280,6\n132#1:1286\n132#1:1287,4\n237#1:1344,6\n237#1:1350\n237#1:1351,4\n277#1:1366,6\n277#1:1372\n277#1:1373,4\n385#1:1422,6\n385#1:1428,4\n132#1:1291\n132#1:1292\n195#1:1315,5\n195#1:1320\n204#1:1321,5\n204#1:1326\n211#1:1327,5\n211#1:1332\n369#1:1410,5\n369#1:1415\n381#1:1416,5\n381#1:1421\n437#1:1433\n437#1:1434,3\n437#1:1444,3\n517#1:1447\n517#1:1448,3\n524#1:1451\n524#1:1452,2\n524#1:1454\n524#1:1455,2\n524#1:1457,3\n660#1:1460,3\n685#1:1463,5\n714#1:1476\n714#1:1477,5\n720#1:1482,5\n726#1:1487\n726#1:1488,5\n728#1:1493,2\n734#1:1495,3\n735#1:1498,5\n836#1:1503,2\n855#1:1505,5\n993#1:1510\n993#1:1511,3\n531#1:1518\n531#1:1519,3\n437#1:1437,7\n699#1:1468,8\n1212#1:1514,2\n1234#1:1516,2\n*E\n"])
public class FySastCli : CliktCommand(null, null, "CoraxJava", false, false, null, null, false, false, false, 1019) {
   private final val verbosity: Level
      private final get() {
         return this.verbosity$delegate.getValue(this, $$delegatedProperties[0]) as Level;
      }


   private final val config: String?
      private final get() {
         return this.config$delegate.getValue(this, $$delegatedProperties[1]) as java.lang.String;
      }


   private final val rules: List<String>?
      private final get() {
         return this.rules$delegate.getValue(this, $$delegatedProperties[2]) as MutableList<java.lang.String>;
      }


   private final val defaultOutput: IResDirectory
      private final get() {
         return this.defaultOutput$delegate.getValue() as IResDirectory;
      }


   private final val output: IResDirectory
      private final get() {
         return this.output$delegate.getValue(this, $$delegatedProperties[3]) as IResDirectory;
      }


   private final val dumpSootScene: Boolean
      private final get() {
         return this.dumpSootScene$delegate.getValue(this, $$delegatedProperties[4]) as java.lang.Boolean;
      }


   private final val resultType: Set<cn.sast.cli.command.FySastCli.ResultType>
      private final get() {
         return this.resultType$delegate.getValue(this, $$delegatedProperties[5]) as MutableSet<FySastCli.ResultType>;
      }


   private final val preferredLanguages: List<Language>
      private final get() {
         return this.preferredLanguages$delegate.getValue(this, $$delegatedProperties[6]) as MutableList<Language>;
      }


   private final val enableDecompile: Boolean

   private final val enableCodeMetrics: Boolean
      private final get() {
         return this.enableCodeMetrics$delegate.getValue(this, $$delegatedProperties[7]) as java.lang.Boolean;
      }


   private final val dumpSrcFileList: String?
      private final get() {
         return this.dumpSrcFileList$delegate.getValue(this, $$delegatedProperties[8]) as java.lang.String;
      }


   private final val target: TargetOptions?
      private final get() {
         return this.target$delegate.getValue(this, $$delegatedProperties[9]) as TargetOptions;
      }


   private final val process: List<String>
      private final get() {
         return this.process$delegate.getValue(this, $$delegatedProperties[10]) as MutableList<java.lang.String>;
      }


   private final val classPath: List<String>
      private final get() {
         return this.classPath$delegate.getValue(this, $$delegatedProperties[11]) as MutableList<java.lang.String>;
      }


   private final val autoAppClasses: List<String>
      private final get() {
         return this.autoAppClasses$delegate.getValue(this, $$delegatedProperties[12]) as MutableList<java.lang.String>;
      }


   private final val autoAppTraverseMode: TraverseMode
      private final get() {
         return this.autoAppTraverseMode$delegate.getValue(this, $$delegatedProperties[13]) as FileSystemLocator.TraverseMode;
      }


   private final val autoAppSrcOnlyFileScheme: Boolean
      private final get() {
         return this.autoAppSrcOnlyFileScheme$delegate.getValue(this, $$delegatedProperties[14]) as java.lang.Boolean;
      }


   private final val disableDefaultJavaClassPath: Boolean
      private final get() {
         return this.disableDefaultJavaClassPath$delegate.getValue(this, $$delegatedProperties[15]) as java.lang.Boolean;
      }


   private final val sourcePath: Set<IResDirectory>
      private final get() {
         return this.sourcePath$delegate.getValue(this, $$delegatedProperties[16]) as MutableSet<IResDirectory>;
      }


   private final val projectRoot: List<String>
      private final get() {
         return this.projectRoot$delegate.getValue(this, $$delegatedProperties[17]) as MutableList<java.lang.String>;
      }


   private final val srcPrecedence: SrcPrecedence
      private final get() {
         return this.srcPrecedence$delegate.getValue(this, $$delegatedProperties[18]) as SrcPrecedence;
      }


   private final val incrementalScanOf: List<String>
      private final get() {
         return this.incrementalScanOf$delegate.getValue(this, $$delegatedProperties[19]) as MutableList<java.lang.String>;
      }


   private final val disableMappingDiffInArchive: Boolean
      private final get() {
         return this.disableMappingDiffInArchive$delegate.getValue(this, $$delegatedProperties[20]) as java.lang.Boolean;
      }


   private final val sunBootClassPath: String
      private final get() {
         return this.sunBootClassPath$delegate.getValue(this, $$delegatedProperties[21]) as java.lang.String;
      }


   private final val javaExtDirs: String
      private final get() {
         return this.javaExtDirs$delegate.getValue(this, $$delegatedProperties[22]) as java.lang.String;
      }


   private final val ecjOptions: List<String>?
      private final get() {
         return this.ecjOptions$delegate.getValue(this, $$delegatedProperties[23]) as MutableList<java.lang.String>;
      }


   private final val serializeCG: Boolean
      private final get() {
         return this.serializeCG$delegate.getValue(this, $$delegatedProperties[24]) as java.lang.Boolean;
      }


   private final val c2sMode: CompareMode
      private final get() {
         return this.c2sMode$delegate.getValue(this, $$delegatedProperties[25]) as AbstractFileIndexer.CompareMode;
      }


   private final val hideNoSource: Boolean
      private final get() {
         return this.hideNoSource$delegate.getValue(this, $$delegatedProperties[26]) as java.lang.Boolean;
      }


   private final val traverseMode: TraverseMode
      private final get() {
         return this.traverseMode$delegate.getValue(this, $$delegatedProperties[27]) as FileSystemLocator.TraverseMode;
      }


   private final val projectScanConfig: File?
      private final get() {
         return this.projectScanConfig$delegate.getValue(this, $$delegatedProperties[28]) as File;
      }


   private final val disableWrapper: Boolean
      private final get() {
         return this.disableWrapper$delegate.getValue(this, $$delegatedProperties[29]) as java.lang.Boolean;
      }


   private final val apponly: Boolean
      private final get() {
         return this.apponly$delegate.getValue(this, $$delegatedProperties[30]) as java.lang.Boolean;
      }


   private final val disablePreAnalysis: Boolean
      private final get() {
         return this.disablePreAnalysis$delegate.getValue(this, $$delegatedProperties[31]) as java.lang.Boolean;
      }


   private final val disableBuiltInAnalysis: Boolean
      private final get() {
         return this.disableBuiltInAnalysis$delegate.getValue(this, $$delegatedProperties[32]) as java.lang.Boolean;
      }


   private final val dataFlowOptions: DataFlowOptions?
      private final get() {
         return this.dataFlowOptions$delegate.getValue(this, $$delegatedProperties[33]) as DataFlowOptions;
      }


   private final val checkerInfoGeneratorOptions: CheckerInfoGeneratorOptions?
      private final get() {
         return this.checkerInfoGeneratorOptions$delegate.getValue(this, $$delegatedProperties[34]) as CheckerInfoGeneratorOptions;
      }


   private final val checkerInfoCompareOptions: CheckerInfoCompareOptions?
      private final get() {
         return this.checkerInfoCompareOptions$delegate.getValue(this, $$delegatedProperties[35]) as CheckerInfoCompareOptions;
      }


   private final val subtoolsOptions: SubToolsOptions?
      private final get() {
         return this.subtoolsOptions$delegate.getValue(this, $$delegatedProperties[36]) as SubToolsOptions;
      }


   private final val flowDroidOptions: FlowDroidOptions?
      private final get() {
         return this.flowDroidOptions$delegate.getValue(this, $$delegatedProperties[37]) as FlowDroidOptions;
      }


   private final val utAnalyzeOptions: UtAnalyzeOptions?
      private final get() {
         return this.utAnalyzeOptions$delegate.getValue(this, $$delegatedProperties[38]) as UtAnalyzeOptions;
      }


   private final val enableStructureAnalysis: Boolean
      private final get() {
         return this.enableStructureAnalysis$delegate.getValue(this, $$delegatedProperties[39]) as java.lang.Boolean;
      }


   private final val enableOriginalNames: Boolean
      private final get() {
         return this.enableOriginalNames$delegate.getValue(this, $$delegatedProperties[40]) as java.lang.Boolean;
      }


   private final val staticFieldTrackingMode: StaticFieldTrackingMode
      private final get() {
         return this.staticFieldTrackingMode$delegate.getValue(this, $$delegatedProperties[41]) as StaticFieldTrackingMode;
      }


   private final val callGraphAlgorithm: String
      private final get() {
         return this.callGraphAlgorithm$delegate.getValue(this, $$delegatedProperties[42]) as java.lang.String;
      }


   private final val callGraphAlgorithmBuiltIn: String
      private final get() {
         return this.callGraphAlgorithmBuiltIn$delegate.getValue(this, $$delegatedProperties[43]) as java.lang.String;
      }


   private final val disableReflection: Boolean
      private final get() {
         return this.disableReflection$delegate.getValue(this, $$delegatedProperties[44]) as java.lang.Boolean;
      }


   private final val maxThreadNum: Int
      private final get() {
         return (this.maxThreadNum$delegate.getValue(this, $$delegatedProperties[45]) as java.lang.Number).intValue();
      }


   private final val memoryThreshold: Double
      private final get() {
         return (this.memoryThreshold$delegate.getValue(this, $$delegatedProperties[46]) as java.lang.Number).doubleValue();
      }


   private final val disableTop: Boolean
      private final get() {
         return this.disableTop$delegate.getValue(this, $$delegatedProperties[47]) as java.lang.Boolean;
      }


   private final val strict: Boolean
      private final get() {
         return this.strict$delegate.getValue(this, $$delegatedProperties[48]) as java.lang.Boolean;
      }


   private final val zipFSEnv: Map<String, String>
      private final get() {
         return this.zipFSEnv$delegate.getValue(this, $$delegatedProperties[49]) as MutableMap<java.lang.String, java.lang.String>;
      }


   private final val zipFSEncodings: List<String>?
      private final get() {
         return this.zipFSEncodings$delegate.getValue(this, $$delegatedProperties[50]) as MutableList<java.lang.String>;
      }


   private final val hashVersion: Int?
      private final get() {
         return this.hashVersion$delegate.getValue(this, $$delegatedProperties[51]) as Int;
      }


   public final val sourceEncoding: Charset
      public final get() {
         return this.sourceEncoding$delegate.getValue(this, $$delegatedProperties[52]) as Charset;
      }


   private final val makeScorecard: Boolean
      private final get() {
         return this.makeScorecard$delegate.getValue(this, $$delegatedProperties[53]) as java.lang.Boolean;
      }


   private final val timeout: Int
      private final get() {
         return (this.timeout$delegate.getValue(this, $$delegatedProperties[54]) as java.lang.Number).intValue();
      }


   public final var collectors: List<IResultCollector>
      internal set

   private final var anchorPointFile: IResFile?
   private final lateinit var lastResult: ResultCollector
   private final val sqliteFileIndexes: MutableSet<IResFile>

   private fun postCheck() {
      val var10000: Int = this.getHashVersion();
      if (var10000 != null) {
         val rules: Int = var10000.intValue();
         ExtSettings.INSTANCE.setHashVersion(rules);
         Report.Companion.setHashVersion(rules);
      }

      if (this.getCheckerInfoGeneratorOptions() != null || this.getSubtoolsOptions() != null) {
         var pl: ConfigPluginLoader;
         label69: {
            pl = this.loadSAConfig(null).component1() as ConfigPluginLoader;
            val var10: java.util.List = this.getRules();
            if (var10 != null) {
               val var11: java.util.Set = CollectionsKt.toSet(var10);
               if (var11 != null) {
                  var12 = this.compatibleOldCheckerNames(var11).getEnables();
                  break label69;
               }
            }

            var12 = null;
         }

         if (this.getCheckerInfoGeneratorOptions() != null) {
            val var13: CheckerInfoGeneratorOptions = this.getCheckerInfoGeneratorOptions();
            var13.run(pl, var12);
         }

         if (this.getSubtoolsOptions() != null) {
            val var14: SubToolsOptions = this.getSubtoolsOptions();
            var14.run(pl, var12);
         }
      }

      if (this.getCheckerInfoCompareOptions() != null) {
         val var15: CheckerInfoCompareOptions = this.getCheckerInfoCompareOptions();
         var15.run();
      }

      if (this.getTarget() == null) {
         throw new BadParameterValue("\"--target\" option is required");
      } else if (this.getFlowDroidOptions() == null
         && this.getDataFlowOptions() == null
         && this.getUtAnalyzeOptions() == null
         && this.getDisableBuiltInAnalysis()) {
         throw new BadParameterValue("No analyze engine is enabled");
      } else if (this.getDisableDefaultJavaClassPath() && this.getClassPath().isEmpty()) {
         throw new BadParameterValue("\"--class-path\" is required when \"--disable-default-java-class-path\" is given");
      } else {
         if (this.getSrcPrecedence().isSootJavaSourcePrec()) {
            if (this.getSunBootClassPath().length() == 0) {
               val var9: Array<Any> = new Object[]{"sun.boot.class.path", "--sun-boot-class-path"};
               val var16: java.lang.String = java.lang.String.format(
                  "System property \"%s\" or option \"%s\" should be provided when \"--src-precedence=prec_java_soot\"", Arrays.copyOf(var9, var9.length)
               );
               throw new BadParameterValue(var16);
            }

            if (this.getJavaExtDirs().length() == 0) {
               val var8: Array<Any> = new Object[]{"java.ext.dirs", "--java-ext-dirs"};
               val var10002: java.lang.String = java.lang.String.format(
                  "System property \"%s\" or option \"%s\" should be provided when \"--src-precedence=prec_java_soot\"", Arrays.copyOf(var8, var8.length)
               );
               throw new BadParameterValue(var10002);
            }
         }
      }
   }

   private fun printOptions() {
      val theme: Theme = ContextKt.getTheme(this.getCurrentContext());
      val text: java.lang.Iterable = this.registeredOptions();
      val groups: java.util.Map = new LinkedHashMap();

      for (Object element$iv$iv : $this$groupBy$iv) {
         val optText: Option = prefix as Option;
         val it: Any = if ((prefix as Option as? GroupableOption) != null) (prefix as Option as? GroupableOption).getParameterGroup() else null;
         val `value$iv$iv$iv`: Any = groups.get(it);
         val var10000: Any;
         if (`value$iv$iv$iv` == null) {
            val var41: Any = new ArrayList();
            groups.put(it, var41);
            var10000 = var41;
         } else {
            var10000 = `value$iv$iv$iv`;
         }

         (var10000 as java.util.List).add(prefix);
      }

      val var17: StringBuilder = new StringBuilder();
      val info: java.util.List = new ArrayList();
      val var42: DataFlowOptions = this.getDataFlowOptions();
      if (var42 != null) {
         val var43: java.lang.String = var42.getGroupName();
         if (var43 != null) {
            info.add(var43);
         }
      }

      val var44: FlowDroidOptions = this.getFlowDroidOptions();
      if (var44 != null) {
         val var45: java.lang.String = var44.getGroupName();
         if (var45 != null) {
            info.add(var45);
         }
      }

      val var46: UtAnalyzeOptions = this.getUtAnalyzeOptions();
      if (var46 != null) {
         val var47: java.lang.String = var46.getGroupName();
         if (var47 != null) {
            info.add(var47);
         }
      }

      val var48: CheckerInfoGeneratorOptions = this.getCheckerInfoGeneratorOptions();
      if (var48 != null) {
         val var49: java.lang.String = var48.getGroupName();
         if (var49 != null) {
            info.add(var49);
         }
      }

      val var50: CheckerInfoCompareOptions = this.getCheckerInfoCompareOptions();
      if (var50 != null) {
         val var51: java.lang.String = var50.getGroupName();
         if (var51 != null) {
            info.add(var51);
         }
      }

      val var52: SubToolsOptions = this.getSubtoolsOptions();
      if (var52 != null) {
         val var53: java.lang.String = var52.getGroupName();
         if (var53 != null) {
            info.add(var53);
         }
      }

      val var18: java.util.List = info;

      for (Entry var21 : destination$iv$iv.entrySet()) {
         var var23: ParameterGroup;
         var var30: java.util.List;
         label76: {
            var23 = var21.getKey() as ParameterGroup;
            var30 = var21.getValue() as java.util.List;
            if (var23 != null) {
               var54 = var23.getGroupName();
               if (var54 == null) {
                  var54 = "${var23.getClass().getSimpleName()}@${System.identityHashCode(var23)}";
               }

               if (var54 != null) {
                  break label76;
               }
            }

            var54 = this.getCommandName();
         }

         if ((if (var23 != null) var23.getGroupName() else null) != null && !CollectionsKt.contains(var18, var23.getGroupName())) {
            var17.append("${theme.getSuccess().invoke(var54)} [off]");
         } else {
            var17.append(
               CollectionsKt.joinToString$default(
                  var30, "\n\t", "${theme.getSuccess().invoke(var54)}\n\t", null, 0, null, FySastCli::printOptions$lambda$33, 28, null
               )
            );
         }

         var17.append("\n");
      }

      val var55: java.lang.String = var17.toString();
      logger.info(FySastCli::printOptions$lambda$34);
      logger.info(FySastCli::printOptions$lambda$35);
      logger.info(FySastCli::printOptions$lambda$36);
      logger.info(FySastCli::printOptions$lambda$37);
      this.getOutput().mkdirs();
      ResourceKt.writeText$default(this.getOutput().resolve("command.txt").toFile(), var55, null, 2, null);
      logger.info(FySastCli::printOptions$lambda$38);
   }

   private fun compatibleOldCheckerNames(enableCheckers: Set<String>): CheckerFilterByName {
      val all: CheckerFilterByName = new CheckerFilterByName(enableCheckers, MapsKt.emptyMap());

      try {
         var var10000: URL = OS.INSTANCE.getBinaryUrl();
         if (var10000 == null) {
            return all;
         } else {
            val var12: IResource = Resource.INSTANCE.of(var10000).getParent();
            if (var12 != null) {
               val var13: IResource = var12.resolve("checker_name_mapping.json");
               if (var13 != null) {
                  if (var13.getExists() && var13.isFile()) {
                     var10000 = (URL)new Gson()
                        .fromJson(
                           PathsKt.readText(var13.getPath(), Charsets.UTF_8),
                           (new TypeToken<java.util.Map<java.lang.String, ? extends java.lang.String>>() {}).getType()
                        );
                     val renameMap: java.util.Map = var10000 as java.util.Map;
                     val worklist: ArrayDeque = new ArrayDeque(enableCheckers.size());
                     worklist.addAll(enableCheckers);
                     val visited: java.util.Set = new LinkedHashSet();

                     while (!worklist.isEmpty()) {
                        val var15: java.lang.String = worklist.removeLast() as java.lang.String;
                        if (var15 != null) {
                           if (visited.add(var15)) {
                              val var16: java.lang.String = renameMap.get(var15) as java.lang.String;
                              if (var16 != null) {
                                 worklist.add(var16);
                              }
                           }
                        }
                     }

                     return new CheckerFilterByName(visited, renameMap);
                  }

                  return all;
               }
            }

            return all;
         }
      } catch (var11: Exception) {
         logger.error(var11, FySastCli::compatibleOldCheckerNames$lambda$40);
         return all;
      }
   }

   private fun parseConfig(config: String, checkerFilter: CheckerFilterByName?): Pair<ConfigPluginLoader, SaConfig> {
      val var10000: Pair = if (StringsKt.contains$default(config, "@", false, 2, null))
         TuplesKt.to(StringsKt.substringAfter$default(config, "@", null, 2, null), StringsKt.substringBefore$default(config, "@", null, 2, null))
         else
         TuplesKt.to(config, "default-config.yml");
      val var35: java.lang.String = var10000.component1() as java.lang.String;
      val var36: java.lang.String = var10000.component2() as java.lang.String;
      val var6: Pair = if (StringsKt.contains$default(var35, "#", false, 2, null))
         TuplesKt.to(StringsKt.substringBefore$default(var35, "#", null, 2, null), StringsKt.substringAfter$default(var35, "#", null, 2, null))
         else
         TuplesKt.to(var35, null);
      val configPathFixed: java.lang.String = var6.component1() as java.lang.String;
      val pluginId: java.lang.String = var6.component2() as java.lang.String;
      if (configPathFixed.length() == 0) {
         throw new IllegalStateException("pluginsPath is empty!".toString());
      } else {
         val var50: java.util.List = ResourceImplKt.globPaths(configPathFixed);
         if (var50 == null) {
            throw new IllegalStateException(("$configPathFixed not exists").toString());
         } else {
            val mayBeFile: java.lang.Iterable = var50;
            val tempFileDir: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var50, 10));

            for (Object item$iv$iv : mayBeFile) {
               tempFileDir.add((`$i$f$flatMapTo` as IResource).resolve("plugins").toDirectory());
            }

            val pl: ConfigPluginLoader = new ConfigPluginLoader(var50, tempFileDir as MutableList<IResDirectory>);
            if (!StringsKt.endsWith$default(var36, ".yml", false, 2, null)) {
               return TuplesKt.to(pl, pl.loadFromName(pluginId, var36));
            } else {
               val var38: IResFile = Resource.INSTANCE.fileOf(var36);
               val var37: IResFile = if (var38.getExists() && var38.isFile()) var38 else null;
               val var51: java.util.List;
               if (var37 != null) {
                  var51 = CollectionsKt.listOf(var37.getPath());
               } else {
                  val ymlConfig: java.lang.Iterable = var50;
                  val `destination$iv$ivx`: java.util.Collection = new ArrayList();

                  for (Object element$iv$iv : $this$flatMap$iv) {
                     val `$this$filter$iv`: java.lang.Iterable = PathExtensionsKt.getFiles((var48 as IResource).getPath());
                     val `destination$iv$ivxx`: java.util.Collection = new ArrayList();

                     for (Object element$iv$ivx : $this$filter$iv) {
                        if (PathsKt.getName(`element$iv$ivx` as Path) == var36) {
                           `destination$iv$ivxx`.add(`element$iv$ivx`);
                        }
                     }

                     CollectionsKt.addAll(`destination$iv$ivx`, `destination$iv$ivxx` as java.util.List);
                  }

                  var51 = `destination$iv$ivx` as java.util.List;
               }

               val var52: IResFile;
               if (var51.isEmpty()) {
                  val var43: IResource = CollectionsKt.first(var50) as IResource;
                  val var44: IResFile = var43.resolve(var36).getAbsolute().getNormalize().toFile();
                  pl.makeTemplateYml(var44);
                  logger.warn(FySastCli::parseConfig$lambda$46);
                  var52 = var44;
               } else {
                  if (var51.size() != 1) {
                     throw new IllegalStateException(
                        ("multiple files: $var36: $var51 were found in: ${CollectionsKt.joinToString$default(var50, "\n", null, null, 0, null, null, 62, null)}")
                           .toString()
                     );
                  }

                  var52 = Resource.INSTANCE.fileOf(CollectionsKt.first(var51) as Path);
               }

               logger.info(FySastCli::parseConfig$lambda$47);
               return TuplesKt.to(pl, pl.searchCheckerUnits(var52, checkerFilter));
            }
         }
      }
   }

   private fun defaultConfig(checkerFilter: CheckerFilterByName?): Pair<ConfigPluginLoader, SaConfig>? {
      logger.info(FySastCli::defaultConfig$lambda$48);
      val configFromEnv: java.lang.String = FySastCliKt.getDefaultConfigDir();
      if (configFromEnv == null) {
         logger.info(FySastCli::defaultConfig$lambda$49);
         return null;
      } else {
         return this.parseConfig("default-config.yml@$configFromEnv", checkerFilter);
      }
   }

   private fun setTimeOut() {
      if (this.getTimeout() >= 1) {
         val var1: CustomRepeatingTimer = new CustomRepeatingTimer(this.getTimeout() * 1000L, FySastCli::setTimeOut$lambda$51);
         var1.setRepeats(false);
         var1.start();
      }
   }

   public open fun run() {
      this.setTimeOut();
      AbstractFileIndexer.Companion.setDefaultClassCompareMode(this.getC2sMode());
      this.checkOutputDir();
      MainConfig.Companion.setPreferredLanguages(this.getPreferredLanguages());
      OS.INSTANCE.setMaxThreadNum(this.getMaxThreadNum());
      val startTime: MetricsMonitor = new MetricsMonitor();
      startTime.start();
      val monitor: MetricsMonitor = startTime;
      CheckType2StringKind.Companion.getCheckType2StringKind();
      this.postCheck();
      this.createAnchorPointFile();
      this.printOptions();
      Resource.INSTANCE.setZipExtractOutputDir(this.getOutput().getPath());
      if (!this.getZipFSEnv().isEmpty()) {
         Resource.INSTANCE.setNewFileSystemEnv(this.getZipFSEnv());
      }

      val var6: java.util.Collection = this.getZipFSEncodings();
      if (var6 != null && !var6.isEmpty()) {
         val var10000: Resource = Resource.INSTANCE;
         val var10001: java.util.List = this.getZipFSEncodings();
         var10000.setFileSystemEncodings(var10001);
      }

      val var7: LocalDateTime = LocalDateTime.now();

      try {
         BuildersKt.runBlocking(
            new CoroutineName("sast-main${this.getMaxThreadNum()}")
               .plus(CoroutineDispatcher.limitedParallelism$default(Dispatchers.getDefault(), this.getMaxThreadNum(), null, 2, null) as CoroutineContext),
            (
               new Function2<CoroutineScope, Continuation<? super Unit>, Object>(monitor, this, null)// $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getVarDefinitions()" because "stat" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1468)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingExprent(VarDefinitionHelper.java:1679)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1496)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1545)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.remapClashingNames(VarDefinitionHelper.java:1458)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.rerunClashing(VarProcessor.java:99)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:118)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:352)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.toJava(NewExprent.java:407)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:761)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:727)
      
            ) as Function2
         );
         monitor.runAnalyzeFinishHook();
         logger.info(FySastCli::run$lambda$54);
         logger.info(FySastCli::run$lambda$55);
      } catch (var5: java.lang.Throwable) {
         logger.info(FySastCli::run$lambda$56);
         logger.info(FySastCli::run$lambda$57);
         throw var5;
      }
   }

   public fun setVerbosity(verbosity: Level) {
      System.out.println("Log Level changed to [$verbosity]");
      val apacheLog4j: Logger = LogManager.getRootLogger();
      Configurator.setAllLevels(apacheLog4j.getName(), UtilsKt.level(verbosity));
      System.out
         .println(
            "apache log4j2 root logger: isTraceEnabled: ${apacheLog4j.isTraceEnabled()}, isDebugEnabled: ${apacheLog4j.isDebugEnabled()}, isInfoEnabled: ${apacheLog4j.isInfoEnabled()}, isWarnEnabled: ${apacheLog4j.isWarnEnabled()}, isErrorEnabled: ${apacheLog4j.isErrorEnabled()}"
         );
      var var10000: org.slf4j.Logger = LoggerFactory.getLogger("ROOT");
      val slf4jLogger: org.slf4j.Logger = var10000;

      var var6: Logger;
      try {
         val var10: Field = Log4jLogger.class.getDeclaredField("logger");
         var10.setAccessible(true);
         val ignore: Any = var10.get(slf4jLogger);
         var6 = ignore as? Logger;
      } catch (var8: NoSuchFieldException) {
         var6 = null;
      }

      var10000 = var6;
      if (var6 == null) {
         var10000 = var10000;
      }

      if (!(apacheLog4j.getClass() == var10000.getClass())) {
         System.out
            .println(
               "org.slf4j root logger:${var10000.getClass().getSimpleName()} isTraceEnabled: ${var10000.isTraceEnabled()}, isDebugEnabled: ${var10000.isDebugEnabled()}, isInfoEnabled: ${var10000.isInfoEnabled()}, isWarnEnabled: ${var10000.isWarnEnabled()}, isErrorEnabled: ${var10000.isErrorEnabled()}"
            );
         throw new IllegalStateException(("invalid logger: ${apacheLog4j.getClass()} != $var10000, 不使用 apache log4j2 可能会导致分析效率减少一倍").toString());
      }
   }

   private fun getResultCollector(info: SootInfoCache, locator: ProjectFileLocator, mainConfig: MainConfig, monitor: MetricsMonitor): ResultCollector {
      val methodSummariesMissing: java.util.Collection = this.getResultType();
      val resultTypes: java.util.Set = (if (methodSummariesMissing.isEmpty()) SetsKt.setOf(FySastCli.ResultType.SARIF) else methodSummariesMissing) as java.util.Set;
      if (resultTypes.contains(FySastCli.ResultType.COUNTER)) {
         this.collectors = CollectionsKt.plus(this.collectors, new ResultCounter());
      }

      val var21: MissingSummaryReporter = new MissingSummaryReporter(mainConfig.getOutput_dir().resolve("undefined_summary_methods.txt").toFile());
      val var22: ResultCounter = new ResultCounter();
      val var10002: IProjectFileLocator = locator;
      val var10005: DataFlowOptions = this.getDataFlowOptions();
      val coverage: JacocoCompoundCoverage = new JacocoCompoundCoverage(var10002, null, null, var10005 != null && var10005.getEnableCoverage(), 6, null);
      val var9: java.util.List = CollectionsKt.plus(CollectionsKt.plus(this.collectors, var21), var22);
      val var10: IResDirectory = mainConfig.getOutput_dir();
      val `$this$fold$iv`: java.lang.Iterable = resultTypes;
      var `accumulator$iv`: Any = CollectionsKt.emptyList();

      for (Object element$iv : $this$fold$iv) {
         var var24: Any;
         switch (FySastCli.WhenMappings.$EnumSwitchMapping$0[((FySastCli.ResultType)element$iv).ordinal()]) {
            case 1:
               var24 = CollectionsKt.plus(`accumulator$iv` as java.util.Collection, OutputType.PLIST);
               break;
            case 2:
               var24 = CollectionsKt.plus(`accumulator$iv` as java.util.Collection, OutputType.SARIF);
               break;
            case 3:
               var24 = `accumulator$iv`;
               break;
            case 4:
               var24 = CollectionsKt.plus(`accumulator$iv` as java.util.Collection, OutputType.SarifPackSrc);
               break;
            case 5:
               var24 = CollectionsKt.plus(`accumulator$iv` as java.util.Collection, OutputType.SarifCopySrc);
               break;
            case 6:
               var24 = `accumulator$iv`;
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         `accumulator$iv` = var24;
      }

      val var25: DataFlowOptions = this.getDataFlowOptions();
      return new ResultCollector(
         mainConfig,
         info,
         var10,
         locator,
         var9,
         (java.util.List)`accumulator$iv`,
         false,
         null,
         coverage,
         var25 != null && var25.getEnableCoverage(),
         monitor,
         192,
         null
      );
   }

   private fun loadSAConfig(checkerFilter: CheckerFilterByName?): Pair<ConfigPluginLoader, SaConfig> {
      val var10000: java.lang.String = this.getConfig();
      if (var10000 != null) {
         val var4: Pair = this.parseConfig(var10000, checkerFilter);
         if (var4 != null) {
            return var4;
         }
      }

      val var5: Pair = this.defaultConfig(checkerFilter);
      if (var5 == null) {
         throw new IllegalStateException("SA-Config is required.".toString());
      } else {
         return var5;
      }
   }

   private fun configureMainConfig(mainConfig: MainConfig, monitor: MetricsMonitor) {
      label349: {
         mainConfig.setAndroidScene(this.getTarget() is AndroidOptions);
         mainConfig.setCallGraphAlgorithm(this.getCallGraphAlgorithm());
         mainConfig.setCallGraphAlgorithmBuiltIn(this.getCallGraphAlgorithmBuiltIn());
         mainConfig.setEnableOriginalNames(this.getEnableOriginalNames());
         val checkerFilter: java.lang.Iterable = this.getAutoAppClasses();
         val ecjOptionsFile: java.util.Collection = ExtensionsKt.persistentSetOf().builder() as java.util.Collection;

         for (Object element$iv : checkerFilter) {
            val it: java.lang.String = it as java.lang.String;
            val var10000: java.util.List = ResourceImplKt.globPaths(it as java.lang.String);
            if (var10000 == null) {
               throw new IllegalStateException(("autoAppClasses option: \"$it\" is invalid or target not exists").toString());
            }

            CollectionsKt.addAll(ecjOptionsFile, var10000);
         }

         var var93: CheckerFilterByName;
         label198: {
            mainConfig.setAutoAppClasses((ecjOptionsFile as Builder).build());
            mainConfig.setAutoAppTraverseMode(this.getAutoAppTraverseMode());
            mainConfig.setAutoAppSrcInZipScheme(!this.getAutoAppSrcOnlyFileScheme());
            mainConfig.setStaticFieldTrackingMode(this.getStaticFieldTrackingMode());
            mainConfig.setEnableReflection(!this.getDisableReflection());
            mainConfig.setParallelsNum(this.getMaxThreadNum());
            mainConfig.setMemoryThreshold(this.getMemoryThreshold());
            mainConfig.setOutput_dir(this.getOutput());
            mainConfig.setDumpSootScene(this.getDumpSootScene());
            val var91: java.util.List = this.getRules();
            if (var91 != null) {
               val var92: java.util.Set = CollectionsKt.toSet(var91);
               if (var92 != null) {
                  var93 = this.compatibleOldCheckerNames(var92);
                  break label198;
               }
            }

            var93 = null;
         }

         val var19: CheckerFilterByName = var93;
         val var20: PhaseIntervalTimer = monitor.timer("plugins.load");
         if (var20 == null) {
            val var43: Pair = this.loadSAConfig(var93);
            val var52: ConfigPluginLoader = var43.component1() as ConfigPluginLoader;
            val var63: SaConfig = var43.component2() as SaConfig;
            CollectionsKt.addAll(mainConfig.getConfigDirs(), var52.getConfigDirs());
            mainConfig.setCheckerInfo(LazyKt.lazy(FySastCli::configureMainConfig$lambda$67$lambda$66));
            var93 = var63;
         } else {
            val it: PhaseIntervalTimer.Snapshot = var20.start();

            try {
               val var44: Pair = this.loadSAConfig(var19);
               val var53: ConfigPluginLoader = var44.component1() as ConfigPluginLoader;
               val var64: SaConfig = var44.component2() as SaConfig;
               CollectionsKt.addAll(mainConfig.getConfigDirs(), var53.getConfigDirs());
               mainConfig.setCheckerInfo(LazyKt.lazy(FySastCli::configureMainConfig$lambda$67$lambda$66));
            } catch (var17: java.lang.Throwable) {
               var20.stop(it);
            }

            var93 = var20;
            var20.stop(it);
         }

         mainConfig.setSaConfig(var93);
         mainConfig.setVersion(ApplicationKt.getVersion());
         if (this.getSrcPrecedence() != SrcPrecedence.prec_java) {
            val var28: java.lang.Iterable = this.getClassPath();
            val var54: java.util.Collection = new ArrayList();

            for (Object element$iv$iv : $this$flatMap$iv) {
               val var12: java.lang.String = var82 as java.lang.String;
               val var95: java.lang.CharSequence = var82 as java.lang.String;
               val var10001: java.lang.String = File.pathSeparator;
               CollectionsKt.addAll(
                  var54,
                  if (StringsKt.contains$default(var95, var10001, false, 2, null))
                     StringsKt.split$default(var12, new java.lang.String[]{File.pathSeparator}, false, 0, 6, null)
                     else
                     CollectionsKt.listOf(var12)
               );
            }

            mainConfig.setClasspath(ExtensionsKt.toPersistentSet(var54 as java.util.List));
         } else {
            mainConfig.setClasspath(ExtensionsKt.toPersistentSet(this.getClassPath()));
         }

         val var22: java.lang.Iterable = this.getProjectRoot();
         var var29: java.util.Collection = ExtensionsKt.persistentSetOf().builder() as java.util.Collection;

         for (Object element$iv : var22) {
            val var66: java.lang.String = var55 as java.lang.String;
            val var98: java.util.List = ResourceImplKt.globPaths(var55 as java.lang.String);
            if (var98 == null) {
               throw new IllegalStateException(("option: --project-root \"$var66\" is invalid or path not exists").toString());
            }

            CollectionsKt.addAll(var29, var98);
         }

         mainConfig.setProjectRoot((var29 as Builder).build());
         if (!this.getIncrementalScanOf().isEmpty()) {
            val var23: IncrementalAnalyzeImplByChangeFiles = new IncrementalAnalyzeImplByChangeFiles(
               mainConfig, !this.getDisableMappingDiffInArchive(), null, null, null, 28, null
            );
            val var30: IncrementalAnalyzeImplByChangeFiles = var23;
            val var46: java.lang.Iterable = this.getIncrementalScanOf();
            val var79: java.util.Collection = new ArrayList();

            for (Object element$iv$iv : $this$flatMap$iv) {
               val var89: java.lang.String = var88 as java.lang.String;
               val var99: java.util.List = ResourceImplKt.globPaths(var88 as java.lang.String);
               if (var99 == null) {
                  throw new IllegalStateException(("option: --incremental-base \"$var89\" is invalid or target not exists").toString());
               }

               CollectionsKt.addAll(var79, var99);
            }

            for (Object element$iv : $this$flatMap$iv) {
               var30.parseIncrementBaseFile(var80 as IResource);
            }

            mainConfig.setIncrementAnalyze(var23);
         }

         val var24: java.lang.Iterable = this.getProcess();
         var var100: Boolean;
         if (var24 is java.util.Collection && (var24 as java.util.Collection).isEmpty()) {
            var100 = false;
         } else {
            label348: {
               for (Object element$iv : $this$any$iv) {
                  if ((var48 as java.lang.String).length() == 0) {
                     var100 = true;
                     break label348;
                  }
               }

               var100 = false;
            }
         }

         if (var100) {
            throw new IllegalStateException("process has empty string".toString());
         } else {
            val var25: java.lang.Iterable = this.getProcess();
            var29 = ExtensionsKt.persistentSetOf().builder() as java.util.Collection;

            for (Object element$ivx : var25) {
               val var70: java.lang.String = `element$ivx` as java.lang.String;
               val var101: java.util.List = ResourceImplKt.globPaths(`element$ivx` as java.lang.String);
               if (var101 == null) {
                  throw new IllegalStateException(("option: --process \"$var70\" is invalid or target not exists").toString());
               }

               CollectionsKt.addAll(var29, var101);
            }

            mainConfig.setProcessDir((var29 as Builder).build());
            mainConfig.setSourcePath(ExtensionsKt.toPersistentSet(this.getSourcePath()));
            mainConfig.setHideNoSource(this.getHideNoSource());
            mainConfig.setTraverseMode(this.getTraverseMode());
            mainConfig.setSrc_precedence(this.getSrcPrecedence());
            mainConfig.setSunBootClassPath(this.getSunBootClassPath());
            mainConfig.setJavaExtDirs(this.getJavaExtDirs());
            mainConfig.setUseDefaultJavaClassPath(!this.getDisableDefaultJavaClassPath());
            mainConfig.setDeCompileIfNotExists(this.enableDecompile);
            mainConfig.setEnableCodeMetrics(this.getEnableCodeMetrics());
            val var26: java.util.List = this.getEcjOptions();
            if (var26 != null) {
               mainConfig.setEcj_options(var26);
            }

            if (this.getProjectScanConfig() != null) {
               val var102: ProjectConfig.Companion = ProjectConfig.Companion;
               val var105: File = this.getProjectScanConfig();
               mainConfig.setProjectConfig(var102.load(var105));
            }

            mainConfig.setUse_wrapper(!this.getDisableWrapper());
            mainConfig.setApponly(this.getApponly());
            val var103: DataFlowOptions = this.getDataFlowOptions();
            if (var103 != null) {
               val var104: Int = var103.getFactor1();
               if (var104 != null) {
                  ExtSettings.INSTANCE.setCalleeDepChainMaxNumForLibClassesInInterProceduraldataFlow(var104.intValue());
               }
            }

            if (this.getStrict()) {
               ExtSettings.INSTANCE.setDataFlowInterProceduralCalleeTimeOut(-1);
            } else {
               val var106: DataFlowOptions = this.getDataFlowOptions();
               if (var106 != null) {
                  val var107: Int = var106.getDataFlowInterProceduralCalleeTimeOut();
                  if (var107 != null) {
                     ExtSettings.INSTANCE.setDataFlowInterProceduralCalleeTimeOut(var107.intValue());
                  }
               }
            }

            mainConfig.setRootPathsForConvertRelativePath(
               CollectionsKt.toList(
                  SetsKt.plus(
                     SetsKt.plus(
                        SetsKt.plus(
                           SetsKt.plus(mainConfig.getProjectRoot() as java.util.Set, mainConfig.getSourcePath() as java.lang.Iterable),
                           mainConfig.getAutoAppClasses() as java.lang.Iterable
                        ),
                        mainConfig.getProcessDir() as java.lang.Iterable
                     ),
                     mainConfig.getOutput_dir()
                  )
               )
            );
         }
      }
   }

   private fun showMetrics() {
      val theme: Theme = ContextKt.getTheme(this.getCurrentContext());
      val `$this$showMetrics_u24lambda_u2483`: UsefulMetrics = UsefulMetrics.Companion.getMetrics();
      var var10000: Gauge = `$this$showMetrics_u24lambda_u2483`.getJvmMemoryMax();
      if (var10000 != null) {
         val var21: java.lang.Long = var10000.getValue() as java.lang.Long;
      }

      var10000 = `$this$showMetrics_u24lambda_u2483`.getFreePhysicalSize();
      if (var10000 != null) {
         val var23: java.lang.Long = var10000.getValue() as java.lang.Long;
      }

      label66: {
         var10000 = `$this$showMetrics_u24lambda_u2483`.getJvmMemoryMax();
         if (var10000 != null) {
            val var25: java.lang.Long = var10000.getValue() as java.lang.Long;
            if (var25 != null) {
               var26 = var25;
               break label66;
            }
         }

         var26 = -1L;
      }

      label61: {
         var10000 = `$this$showMetrics_u24lambda_u2483`.getFreePhysicalSize();
         if (var10000 != null) {
            val var28: java.lang.Long = var10000.getValue() as java.lang.Long;
            if (var28 != null) {
               val it: Long = var28.longValue();
               val var29: java.lang.Long = if (it >= 0L) var28 else null;
               if ((if (it >= 0L) var28 else null) != null) {
                  var30 = var29;
                  break label61;
               }
            }
         }

         var30 = -1L;
      }

      var xmxExpect: Double = -1.0;
      val roundedNumber: Function1 = FySastCli::showMetrics$lambda$83$lambda$80;
      val var20: java.lang.String;
      if (var26 <= 0L || var30 <= 0L) {
         var20 = theme.getDanger().invoke("Xmx:${`$this$showMetrics_u24lambda_u2483`.getMemFmt(`$this$showMetrics_u24lambda_u2483`.getJvmMemoryMax())} ");
      } else if (var26 > var30 + 419430400) {
         var20 = theme.getWarning()
            .invoke(
               "Xmx:${`$this$showMetrics_u24lambda_u2483`.getMemFmt(`$this$showMetrics_u24lambda_u2483`.getJvmMemoryMax())} (warning: Greater than remaining virtual memory!)"
            );
         xmxExpect = (var30 + 419430400) * 0.9;
      } else if (var26 < var30 * 0.74) {
         var20 = theme.getWarning()
            .invoke(
               "Xmx:${`$this$showMetrics_u24lambda_u2483`.getMemFmt(`$this$showMetrics_u24lambda_u2483`.getJvmMemoryMax())} (warning: The memory restriction is excessively stringent!)"
            );
         xmxExpect = (var30 + 419430400) * 0.9;
      } else {
         var20 = theme.getInfo().invoke("Xmx:${`$this$showMetrics_u24lambda_u2483`.getMemFmt(`$this$showMetrics_u24lambda_u2483`.getJvmMemoryMax())}");
      }

      logger.info(FySastCli::showMetrics$lambda$83$lambda$81);
      if (xmxExpect > 1.0) {
         val var19: DecimalFormat = new DecimalFormat("#.#");
         var19.setDecimalSeparatorAlwaysShown(false);
         logger.warn(FySastCli::showMetrics$lambda$83$lambda$82);
      }
   }

   private fun createAnchorPointFile() {
      if (this.anchorPointFile != null) {
         val var1: IResFile = this.anchorPointFile;
         this.anchorPointFile.mkdirs();
         PathsKt.writeText$default(var1.getPath(), "nothing", null, new OpenOption[0], 2, null);
      }
   }

   private fun checkOutputDir() {
      if (this.getCheckerInfoCompareOptions() == null) {
         val anchorPointFile: IResFile = this.getOutput().resolve(FySastCliKt.getANCHOR_POINT_FILE()).toFile();
         if (!anchorPointFile.getExists()) {
            if (!IResDirectory.DefaultImpls.listPathEntries$default(this.getOutput(), null, 1, null).isEmpty()) {
               throw new IllegalArgumentException(
                  "The output directory (${this.getOutput()}) is not empty. To avoid disrupting your environment with overwritten files, please check this parameter or clear the output folder."
               );
            }

            this.anchorPointFile = anchorPointFile;
         }
      }
   }

   private fun checkEnv(locator: ProjectFileLocator) {
      this.getOutput().mkdirs();
      val `$this$checkEnv_u24lambda_u2487`: FySastCli = this;

      val exists: java.lang.Iterable;
      for (Object element$iv : exists) {
         val it: IResource = `element$iv` as IResource;
         if ((`element$iv` as IResource).isFileScheme()) {
            var var10000: Path = `$this$checkEnv_u24lambda_u2487`.getOutput().getPath().toAbsolutePath();
            var10000 = var10000.normalize();
            val var10001: Path = it.getPath().toAbsolutePath();
            if (var10000.startsWith(var10001.normalize())) {
               throw new IllegalArgumentException(
                  "The output (${`$this$checkEnv_u24lambda_u2487`.getOutput()}) directory cannot be pointed to within the resources ($it) that will be scanned."
               );
            }
         }
      }

      val var10: java.util.List = SequencesKt.toList(
         SequencesKt.map(
            locator.findFromFileIndexMap(
               StringsKt.split$default(FySastCliKt.getANCHOR_POINT_FILE(), new java.lang.String[]{File.separator}, false, 0, 6, null),
               AbstractFileIndexer.CompareMode.Path
            ),
            FySastCli::checkEnv$lambda$87$lambda$86
         )
      );
      if (!var10.isEmpty()) {
         throw new IllegalArgumentException(
            "The corax output directory ($var10) has been detected as being included in the paths ${locator.getSourceDir()} to be scanned. Please move all the output directories to another location or delete them."
         );
      }
   }

   private suspend fun getFilteredJavaSourceFiles(mainConfig: MainConfig, locator: ProjectFileLocator): Set<IResFile> {
      var `$continuation`: Continuation;
      label33: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label33;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            Object L$1;
            Object L$2;
            Object L$3;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return FySastCli.access$getFilteredJavaSourceFiles(this.this$0, null, null, this as Continuation);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var13: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var `destination$iv`: java.util.Collection;
      var var7: java.util.Iterator;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            val `$this$flatMapTo$iv`: java.lang.Iterable = ResourceKt.getJavaExtensions();
            `destination$iv` = new LinkedHashSet();
            var7 = `$this$flatMapTo$iv`.iterator();
            break;
         case 1:
            var7 = `$continuation`.L$3 as java.util.Iterator;
            `destination$iv` = `$continuation`.L$2 as java.util.Collection;
            locator = `$continuation`.L$1 as ProjectFileLocator;
            mainConfig = `$continuation`.L$0 as MainConfig;
            ResultKt.throwOnFailure(`$result`);
            CollectionsKt.addAll(`destination$iv`, SequencesKt.filter(`$result` as Sequence, FySastCli::getFilteredJavaSourceFiles$lambda$89$lambda$88));
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      while (var7.hasNext()) {
         val var15: java.lang.String = var7.next() as java.lang.String;
         `$continuation`.L$0 = mainConfig;
         `$continuation`.L$1 = locator;
         `$continuation`.L$2 = `destination$iv`;
         `$continuation`.L$3 = var7;
         `$continuation`.label = 1;
         val var10000: Any = locator.getByFileExtension(var15, `$continuation`);
         if (var10000 === var13) {
            return var13;
         }

         CollectionsKt.addAll(`destination$iv`, SequencesKt.filter(var10000 as Sequence, FySastCli::getFilteredJavaSourceFiles$lambda$89$lambda$88));
      }

      return `destination$iv`;
   }

   private fun writeSourceFileListForProbe(mainConfig: MainConfig, out: IResFile, srcFiles: Set<IResFile>) {
      label28: {
         out.mkdirs();
         val var4: Path = out.getPath();
         val var5: Charset = Charsets.UTF_8;
         val it: Array<OpenOption> = new OpenOption[0];
         val var15: Closeable = new OutputStreamWriter(Files.newOutputStream(var4, Arrays.copyOf(it, it.length)), var5);
         var var16: java.lang.Throwable = null;

         try {
            try {
               val var17: OutputStreamWriter = var15 as OutputStreamWriter;

               for (IResFile f : srcFiles) {
                  val e: IResFile = f.expandRes(mainConfig.getOutput_dir());
                  var17.write("$e\n");
                  this.sqliteFileIndexes.add(e);
               }
            } catch (var11: java.lang.Throwable) {
               var16 = var11;
               throw var11;
            }
         } catch (var12: java.lang.Throwable) {
            CloseableKt.closeFinally(var15, var16);
         }

         CloseableKt.closeFinally(var15, null);
      }
   }

   private fun addFilesToDataBase(mainConfig: MainConfig, files: Set<IResFile>) {
      label28: {
         val var3: Closeable = ResultCollector.Companion.newSqliteDiagnostics(mainConfig, null, mainConfig.getOutput_dir(), null);
         var var4: java.lang.Throwable = null;

         try {
            try {
               val it: SqliteDiagnostics = var3 as SqliteDiagnostics;
               SqliteDiagnostics.open$default(var3 as SqliteDiagnostics, null, 1, null);

               for (IResFile f : files) {
                  it.createFileXCachedFromFile(f);
               }
            } catch (var9: java.lang.Throwable) {
               var4 = var9;
               throw var9;
            }
         } catch (var10: java.lang.Throwable) {
            CloseableKt.closeFinally(var3, var4);
         }

         CloseableKt.closeFinally(var3, null);
      }
   }

   private suspend fun runCodeMetrics(mainConfig: MainConfig, srcFiles: Set<IResFile>) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.code.cfg.ExceptionRangeCFG.isCircular()" because "range" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.graphToStatement(DomHelper.java:84)
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:203)
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:157)
      //
      // Bytecode:
      // 000: aload 3
      // 001: instanceof cn/sast/cli/command/FySastCli$runCodeMetrics$1
      // 004: ifeq 029
      // 007: aload 3
      // 008: checkcast cn/sast/cli/command/FySastCli$runCodeMetrics$1
      // 00b: astore 13
      // 00d: aload 13
      // 00f: getfield cn/sast/cli/command/FySastCli$runCodeMetrics$1.label I
      // 012: ldc_w -2147483648
      // 015: iand
      // 016: ifeq 029
      // 019: aload 13
      // 01b: dup
      // 01c: getfield cn/sast/cli/command/FySastCli$runCodeMetrics$1.label I
      // 01f: ldc_w -2147483648
      // 022: isub
      // 023: putfield cn/sast/cli/command/FySastCli$runCodeMetrics$1.label I
      // 026: goto 034
      // 029: new cn/sast/cli/command/FySastCli$runCodeMetrics$1
      // 02c: dup
      // 02d: aload 0
      // 02e: aload 3
      // 02f: invokespecial cn/sast/cli/command/FySastCli$runCodeMetrics$1.<init> (Lcn/sast/cli/command/FySastCli;Lkotlin/coroutines/Continuation;)V
      // 032: astore 13
      // 034: aload 13
      // 036: getfield cn/sast/cli/command/FySastCli$runCodeMetrics$1.result Ljava/lang/Object;
      // 039: astore 12
      // 03b: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 03e: astore 14
      // 040: aload 13
      // 042: getfield cn/sast/cli/command/FySastCli$runCodeMetrics$1.label I
      // 045: tableswitch 459 0 1 23 426
      // 05c: aload 12
      // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 061: nop
      // 062: aload 1
      // 063: invokevirtual cn/sast/api/config/MainConfig.getOutput_dir ()Lcn/sast/common/IResDirectory;
      // 066: ldc_w "tmp/code-metrics-file-list.txt"
      // 069: invokeinterface cn/sast/common/IResDirectory.resolve (Ljava/lang/String;)Lcn/sast/common/IResource; 2
      // 06e: invokeinterface cn/sast/common/IResource.toFile ()Lcn/sast/common/IResFile; 1
      // 073: astore 4
      // 075: aload 4
      // 077: invokeinterface cn/sast/common/IResFile.mkdirs ()V 1
      // 07c: aload 4
      // 07e: invokeinterface cn/sast/common/IResFile.getPath ()Ljava/nio/file/Path; 1
      // 083: astore 5
      // 085: getstatic kotlin/text/Charsets.UTF_8 Ljava/nio/charset/Charset;
      // 088: astore 6
      // 08a: bipush 0
      // 08b: anewarray 2587
      // 08e: astore 7
      // 090: new java/io/OutputStreamWriter
      // 093: dup
      // 094: aload 5
      // 096: aload 7
      // 098: aload 7
      // 09a: arraylength
      // 09b: invokestatic java/util/Arrays.copyOf ([Ljava/lang/Object;I)[Ljava/lang/Object;
      // 09e: checkcast [Ljava/nio/file/OpenOption;
      // 0a1: invokestatic java/nio/file/Files.newOutputStream (Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/OutputStream;
      // 0a4: aload 6
      // 0a6: invokespecial java/io/OutputStreamWriter.<init> (Ljava/io/OutputStream;Ljava/nio/charset/Charset;)V
      // 0a9: checkcast java/io/Closeable
      // 0ac: astore 5
      // 0ae: aconst_null
      // 0af: astore 6
      // 0b1: nop
      // 0b2: aload 5
      // 0b4: checkcast java/io/OutputStreamWriter
      // 0b7: astore 7
      // 0b9: bipush 0
      // 0ba: istore 8
      // 0bc: aload 2
      // 0bd: invokeinterface java/util/Set.iterator ()Ljava/util/Iterator; 1
      // 0c2: astore 9
      // 0c4: aload 9
      // 0c6: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 0cb: ifeq 115
      // 0ce: aload 9
      // 0d0: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 0d5: checkcast cn/sast/common/IResFile
      // 0d8: astore 10
      // 0da: aload 10
      // 0dc: invokeinterface cn/sast/common/IResFile.getExtension ()Ljava/lang/String; 1
      // 0e1: ldc_w "java"
      // 0e4: invokestatic kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 0e7: ifeq 0c4
      // 0ea: aload 10
      // 0ec: aload 1
      // 0ed: invokevirtual cn/sast/api/config/MainConfig.getOutput_dir ()Lcn/sast/common/IResDirectory;
      // 0f0: invokeinterface cn/sast/common/IResFile.expandRes (Lcn/sast/common/IResDirectory;)Lcn/sast/common/IResFile; 2
      // 0f5: astore 11
      // 0f7: aload 7
      // 0f9: aload 11
      // 0fb: invokedynamic makeConcatWithConstants (Lcn/sast/common/IResFile;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "\u0001\n" ]
      // 100: invokevirtual java/io/OutputStreamWriter.write (Ljava/lang/String;)V
      // 103: aload 0
      // 104: getfield cn/sast/cli/command/FySastCli.sqliteFileIndexes Ljava/util/Set;
      // 107: checkcast java/util/Collection
      // 10a: aload 11
      // 10c: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 111: pop
      // 112: goto 0c4
      // 115: nop
      // 116: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 119: astore 7
      // 11b: aload 5
      // 11d: aload 6
      // 11f: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 122: goto 13a
      // 125: astore 7
      // 127: aload 7
      // 129: astore 6
      // 12b: aload 7
      // 12d: athrow
      // 12e: astore 7
      // 130: aload 5
      // 132: aload 6
      // 134: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 137: aload 7
      // 139: athrow
      // 13a: aload 0
      // 13b: aload 4
      // 13d: aload 1
      // 13e: invokevirtual cn/sast/api/config/MainConfig.getOutput_dir ()Lcn/sast/common/IResDirectory;
      // 141: ldc_w "metrics/metrics.csv"
      // 144: invokeinterface cn/sast/common/IResDirectory.resolve (Ljava/lang/String;)Lcn/sast/common/IResource; 2
      // 149: invokeinterface cn/sast/common/IResource.toFile ()Lcn/sast/common/IResFile; 1
      // 14e: invokespecial cn/sast/cli/command/FySastCli.constructPmdAnalyzerCmd (Lcn/sast/common/IResFile;Lcn/sast/common/IResFile;)Ljava/util/List;
      // 151: dup
      // 152: ifnonnull 15a
      // 155: pop
      // 156: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 159: areturn
      // 15a: astore 5
      // 15c: new kotlin/jvm/internal/Ref$ObjectRef
      // 15f: dup
      // 160: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
      // 163: astore 6
      // 165: aload 6
      // 167: new java/lang/ProcessBuilder
      // 16a: dup
      // 16b: bipush 0
      // 16c: anewarray 80
      // 16f: invokespecial java/lang/ProcessBuilder.<init> ([Ljava/lang/String;)V
      // 172: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 175: new java/io/File
      // 178: dup
      // 179: ldc_w "."
      // 17c: invokespecial java/io/File.<init> (Ljava/lang/String;)V
      // 17f: invokevirtual java/io/File.getAbsoluteFile ()Ljava/io/File;
      // 182: astore 7
      // 184: aload 6
      // 186: aload 6
      // 188: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 18b: checkcast java/lang/ProcessBuilder
      // 18e: aload 7
      // 190: invokevirtual java/lang/ProcessBuilder.directory (Ljava/io/File;)Ljava/lang/ProcessBuilder;
      // 193: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 196: aload 6
      // 198: aload 6
      // 19a: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 19d: checkcast java/lang/ProcessBuilder
      // 1a0: aload 5
      // 1a2: invokevirtual java/lang/ProcessBuilder.command (Ljava/util/List;)Ljava/lang/ProcessBuilder;
      // 1a5: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 1a8: getstatic cn/sast/cli/command/FySastCli.logger Lmu/KLogger;
      // 1ab: aload 6
      // 1ad: invokedynamic invoke (Lkotlin/jvm/internal/Ref$ObjectRef;)Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/cli/command/FySastCli.runCodeMetrics$lambda$93 (Lkotlin/jvm/internal/Ref$ObjectRef;)Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 1b2: invokeinterface mu/KLogger.debug (Lkotlin/jvm/functions/Function0;)V 2
      // 1b7: aload 6
      // 1b9: aload 6
      // 1bb: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 1be: checkcast java/lang/ProcessBuilder
      // 1c1: bipush 1
      // 1c2: invokevirtual java/lang/ProcessBuilder.redirectErrorStream (Z)Ljava/lang/ProcessBuilder;
      // 1c5: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 1c8: invokestatic kotlinx/coroutines/Dispatchers.getIO ()Lkotlinx/coroutines/CoroutineDispatcher;
      // 1cb: checkcast kotlin/coroutines/CoroutineContext
      // 1ce: new cn/sast/cli/command/FySastCli$runCodeMetrics$4
      // 1d1: dup
      // 1d2: aload 6
      // 1d4: aconst_null
      // 1d5: invokespecial cn/sast/cli/command/FySastCli$runCodeMetrics$4.<init> (Lkotlin/jvm/internal/Ref$ObjectRef;Lkotlin/coroutines/Continuation;)V
      // 1d8: checkcast kotlin/jvm/functions/Function2
      // 1db: aload 13
      // 1dd: aload 13
      // 1df: bipush 1
      // 1e0: putfield cn/sast/cli/command/FySastCli$runCodeMetrics$1.label I
      // 1e3: invokestatic kotlinx/coroutines/BuildersKt.withContext (Lkotlin/coroutines/CoroutineContext;Lkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 1e6: dup
      // 1e7: aload 14
      // 1e9: if_acmpne 1f7
      // 1ec: aload 14
      // 1ee: areturn
      // 1ef: nop
      // 1f0: aload 12
      // 1f2: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 1f5: aload 12
      // 1f7: pop
      // 1f8: goto 20c
      // 1fb: astore 4
      // 1fd: getstatic cn/sast/cli/command/FySastCli.logger Lmu/KLogger;
      // 200: aload 4
      // 202: invokedynamic invoke (Ljava/lang/Exception;)Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/cli/command/FySastCli.runCodeMetrics$lambda$94 (Ljava/lang/Exception;)Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 207: invokeinterface mu/KLogger.error (Lkotlin/jvm/functions/Function0;)V 2
      // 20c: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 20f: areturn
      // 210: new java/lang/IllegalStateException
      // 213: dup
      // 214: ldc_w "call to 'resume' before 'invoke' with coroutine"
      // 217: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 21a: athrow
   }

   private fun top(monitor: MetricsMonitor) {
      val topLogWriter: Path = this.getOutput().resolve("top.log").getPath();
      val `$this$top_u24lambda_u2497`: Array<OpenOption> = new OpenOption[]{StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE};
      val var4: Charset = Charsets.UTF_8;
      val io: OutputStreamWriter = new OutputStreamWriter(
         Files.newOutputStream(topLogWriter, Arrays.copyOf(`$this$top_u24lambda_u2497`, `$this$top_u24lambda_u2497`.length)), var4
      );
      io.write("${LocalDateTime.now()}: --------------------start--------------------");
      io.write("${LocalDateTime.now()}: PID: ${ProcessHandle.current().pid()}");
      val var8: CustomRepeatingTimer = new CustomRepeatingTimer(io, FySastCli::top$lambda$96) {
         {
            super(1000L, `$super_call_param$1`);
            this.$io = `$io`;
         }

         @Override
         public void stop() {
            super.stop();
            this.$io.write("\n${LocalDateTime.now()}: --------------------finish--------------------\n\n\n");
            this.$io.flush();
            this.$io.close();
         }
      };
      var8.setRepeats(true);
      var8.start();
      monitor.addAnalyzeFinishHook(new Thread(new Runnable(var8) {
         {
            this.$topLogWriter = `$topLogWriter`;
         }

         @Override
         public final void run() {
            this.$topLogWriter.stop();
         }
      }));
   }

   private suspend fun runAnalyze(target: TargetOptions, monitor: MetricsMonitor) {
      var `$continuation`: Continuation;
      label154: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label154;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            Object L$1;
            Object L$2;
            Object L$3;
            Object L$4;
            Object L$5;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return FySastCli.access$runAnalyze(this.this$0, null, null, this as Continuation);
            }
         };
      }

      var mainConfig: MainConfig;
      var locator: ProjectFileLocator;
      var result: ResultCollector;
      var var61: java.util.Set;
      label176: {
         var out: java.lang.String;
         label158: {
            var var27: Any;
            label145: {
               label159: {
                  val `$result`: Any = `$continuation`.result;
                  var27 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  switch ($continuation.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        AnalysisCache.G.INSTANCE.clear();
                        Resource.INSTANCE.clean();
                        FileSystemCacheLocator.INSTANCE.clear();
                        SootUtils.INSTANCE.cleanUp();
                        FileSystemLocator.Companion.clear();
                        if (!this.getDisableTop()) {
                           this.top(monitor);
                        }

                        this.setVerbosity(this.getVerbosity());
                        this.showMetrics();
                        System.setProperty("ENABLE_STRUCTURE_ANALYSIS", if (this.getEnableStructureAnalysis()) "true" else "false");
                        val var56: Charset = this.getSourceEncoding();
                        mainConfig = new MainConfig(
                           var56,
                           monitor,
                           null,
                           null,
                           false,
                           null,
                           false,
                           false,
                           null,
                           false,
                           null,
                           null,
                           null,
                           null,
                           null,
                           null,
                           false,
                           false,
                           null,
                           false,
                           false,
                           0,
                           0,
                           false,
                           null,
                           null,
                           null,
                           null,
                           null,
                           false,
                           false,
                           false,
                           false,
                           false,
                           false,
                           false,
                           false,
                           null,
                           null,
                           null,
                           0.0,
                           -4,
                           511,
                           null
                        );
                        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(monitor) {
                           {
                              this.$monitor = `$monitor`;
                           }

                           @Override
                           public final void run() {
                              this.$monitor.runAnalyzeFinishHook();
                              System.out.println("log file: ${AnalyzerEnv.INSTANCE.getLastLogFile()}");
                           }
                        }));
                        monitor.addAnalyzeFinishHook(new Thread(new Runnable(mainConfig, monitor) {
                           {
                              this.$mainConfig = `$mainConfig`;
                              this.$monitor = `$monitor`;
                           }

                           @Override
                           public final void run() {
                              val analyzeSkipDir: IResDirectory = this.$mainConfig.getOutput_dir().toDirectory();
                              this.$monitor.serialize(this.$mainConfig.getOutput_dir());
                              this.$mainConfig.getScanFilter().dump(analyzeSkipDir);
                           }
                        }));
                        this.configureMainConfig(mainConfig, monitor);
                        target.configureMainConfig(mainConfig);
                        val var28: FlowDroidOptions = this.getFlowDroidOptions();
                        val dataFlowOptions: DataFlowOptions = this.getDataFlowOptions();
                        val utAnalyzeOptions: UtAnalyzeOptions = this.getUtAnalyzeOptions();
                        val f0: IResDirectory = if (!mainConfig.getHideNoSource()) mainConfig.getOutput_dir().resolve("source").toDirectory() else null;
                        val f1: java.util.Set = SetsKt.plus(
                           SetsKt.plus(
                              SetsKt.plus(
                                 SetsKt.plus(mainConfig.getProjectRoot() as java.util.Set, mainConfig.getSourcePath() as java.lang.Iterable),
                                 mainConfig.getProcessDir() as java.lang.Iterable
                              ),
                              mainConfig.getAutoAppClasses() as java.lang.Iterable
                           ),
                           mainConfig.get_expand_class_path()
                        );
                        var info: <unrepresentable> = monitor.getProjectMetrics().getPaths() as java.util.Collection;
                        val var34: java.lang.Iterable = f1;
                        val builtinAnalysis: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(f1, 10));

                        for (Object item$iv$iv : $this$map$iv) {
                           builtinAnalysis.add((infoFlowConfig as IResource).toString());
                        }

                        CollectionsKt.addAll(info, builtinAnalysis as java.util.List);
                        val locatorx: ProjectFileLocator = new ProjectFileLocator(monitor, f1, f0, mainConfig.getTraverseMode(), false, 16, null);
                        locatorx.update();
                        monitor.getProjectMetrics().setTotalFileNum(locatorx.totalFiles());
                        val it: Long = locatorx.totalJavaSrcFiles();
                        monitor.getProjectMetrics().setTotalAnySourceFileNum(it);
                        logger.info(FySastCli::runAnalyze$lambda$100$lambda$99);
                        locator = locatorx;
                        this.checkEnv(locatorx);
                        val var29: SootCtx = new SootCtx(mainConfig);
                        val var30: CompilationUnitOfSCFactory = new CompilationUnitOfSCFactory(FySastCli::runAnalyze$lambda$101);
                        val var31: SootHostExtInfoFactory = new SootHostExtInfoFactory(var30);
                        AnalysisCache.G.INSTANCE.setCompilationUnitOfSCFactory(var30);
                        AnalysisCache.G.INSTANCE.setSootHostExtInfoFactory(var31);
                        target.initSoot(var29, locatorx);
                        info = new SootInfoCache(var31) {
                           {
                              this.$f1 = `$f1`;
                           }

                           @Override
                           public AnalysisCache getCache() {
                              return AnalysisCache.G.INSTANCE;
                           }

                           @Override
                           public AnalysisDataFactory.Key<SootHostExtend> getHostKey() {
                              return this.$f1.getKey();
                           }

                           @Override
                           public BodyDeclaration getMemberAtLine(SootClass $this$getMemberAtLine, int ln) {
                              return SootInfoCache.DefaultImpls.getMemberAtLine(this, `$this$getMemberAtLine`, ln);
                           }

                           @Override
                           public SootHostExtend getExt(Host $this$ext) {
                              return SootInfoCache.DefaultImpls.getExt(this, `$this$ext`);
                           }

                           @Override
                           public int getJavaNameSourceStartLineNumber(AbstractHost $this$javaNameSourceStartLineNumber) {
                              return SootInfoCache.DefaultImpls.getJavaNameSourceStartLineNumber(this, `$this$javaNameSourceStartLineNumber`);
                           }

                           @Override
                           public int getJavaNameSourceStartColumnNumber(AbstractHost $this$javaNameSourceStartColumnNumber) {
                              return SootInfoCache.DefaultImpls.getJavaNameSourceStartColumnNumber(this, `$this$javaNameSourceStartColumnNumber`);
                           }

                           @Override
                           public int getJavaNameSourceEndLineNumber(AbstractHost $this$javaNameSourceEndLineNumber) {
                              return SootInfoCache.DefaultImpls.getJavaNameSourceEndLineNumber(this, `$this$javaNameSourceEndLineNumber`);
                           }

                           @Override
                           public int getJavaNameSourceEndColumnNumber(AbstractHost $this$javaNameSourceEndColumnNumber) {
                              return SootInfoCache.DefaultImpls.getJavaNameSourceEndColumnNumber(this, `$this$javaNameSourceEndColumnNumber`);
                           }
                        };
                        PackManager.v()
                           .getPack("jb")
                           .add(new Transform("jb.identityStmt2MethodParamRegion", (new IdentityStmt2MethodParamRegion(info)) as Transformer));
                        val var36: ResultCollector = this.getResultCollector(info, locatorx, mainConfig, monitor);
                        this.lastResult = var36;
                        result = var36;
                        val var37: AnalyzeTaskRunner = new AnalyzeTaskRunner(mainConfig.getParallelsNum(), var29, monitor);
                        val var39: BuiltinAnalysis = if (!this.getDisableBuiltInAnalysis()
                              && (!AnalyzerEnv.INSTANCE.getShouldV3r14y() || AnalyzerEnv.INSTANCE.getBvs1n3ss().get() != 0))
                           new BuiltinAnalysis(mainConfig, info)
                           else
                           null;
                        if (var39 != null) {
                           label121: {
                              val var57: SaConfig = mainConfig.getSaConfig();
                              if (var57 != null) {
                                 var58 = var57.getBuiltinAnalysisConfig();
                                 if (var58 != null) {
                                    break label121;
                                 }
                              }

                              var58 = new BuiltinAnalysisConfig(null, null, 0, 0, 15, null);
                           }

                           if (!mainConfig.getSkipClass()
                              && BuiltinAnalysis.Companion.isEnableAllMethodsAnalyzeInScene(mainConfig)
                              && mainConfig.isEnable(DefineUnusedChecker.UnusedMethod.INSTANCE)) {
                              val var46: BuiltinAnalysis.CHA-AllMethodsProvider = new BuiltinAnalysis.CHA-AllMethodsProvider(null, 1, null);
                              AnalyzeTaskRunner.registerAnalysis$default(
                                 var37,
                                 "BuiltinAnalysis.allMethodsAnalyzeInScene",
                                 var46,
                                 null,
                                 (
                                    new Function2<AnalyzeTaskRunner.Env, Continuation<? super Unit>, Object>(var39, var58, var46, var36, null) {
                                       int label;

                                       {
                                          super(2, `$completionx`);
                                          this.$builtinAnalysis = `$builtinAnalysis`;
                                          this.$builtinAnalysisConfig = `$builtinAnalysisConfig`;
                                          this.$allMethodsProvider = `$allMethodsProvider`;
                                          this.$result = `$result`;
                                       }

                                       public final Object invokeSuspend(Object $result) {
                                          val var3x: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                          switch (this.label) {
                                             case 0:
                                                ResultKt.throwOnFailure(`$result`);
                                                val it: AnalyzeTaskRunner.Env = this.L$0 as AnalyzeTaskRunner.Env;
                                                val var10000: BuiltinAnalysis = this.$builtinAnalysis;
                                                val var10001: SootCtx = it.getSootCtx();
                                                val var10002: BuiltinAnalysisConfig = this.$builtinAnalysisConfig;
                                                val var10003: BuiltinAnalysis.CHA-AllMethodsProvider = this.$allMethodsProvider;
                                                val var10004: IBuiltInAnalysisCollector = this.$result;
                                                val var10005: Continuation = this as Continuation;
                                                this.label = 1;
                                                if (var10000.allMethodsAnalyzeInScene(var10001, var10002, var10003, var10004, var10005) === var3x) {
                                                   return var3x;
                                                }
                                                break;
                                             case 1:
                                                ResultKt.throwOnFailure(`$result`);
                                                break;
                                             default:
                                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                          }

                                          return Unit.INSTANCE;
                                       }

                                       public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                          val var3: Function2 = new <anonymous constructor>(
                                             this.$builtinAnalysis, this.$builtinAnalysisConfig, this.$allMethodsProvider, this.$result, `$completion`
                                          );
                                          var3.L$0 = value;
                                          return var3 as Continuation<Unit>;
                                       }

                                       public final Object invoke(AnalyzeTaskRunner.Env p1, Continuation<? super Unit> p2) {
                                          return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                                       }
                                    }
                                 ) as Function2,
                                 null,
                                 20,
                                 null
                              );
                           }
                        }

                        val var45: IEntryPointProvider = target.getProvider(var29, locatorx);
                        if (var39 != null) {
                           AnalyzeTaskRunner.registerAnalysis$default(
                              var37,
                              "BuiltinAnalysis.analyzeInScene",
                              var45,
                              null,
                              (new Function2<AnalyzeTaskRunner.Env, Continuation<? super Unit>, Object>(var39, var36, null) {
                                 int label;

                                 {
                                    super(2, `$completionx`);
                                    this.$builtinAnalysis = `$builtinAnalysis`;
                                    this.$result = `$result`;
                                 }

                                 public final Object invokeSuspend(Object $result) {
                                    val var3x: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                    switch (this.label) {
                                       case 0:
                                          ResultKt.throwOnFailure(`$result`);
                                          val it: AnalyzeTaskRunner.Env = this.L$0 as AnalyzeTaskRunner.Env;
                                          val var10000: BuiltinAnalysis = this.$builtinAnalysis;
                                          val var10001: SootCtx = it.getSootCtx();
                                          val var10002: IBuiltInAnalysisCollector = this.$result;
                                          val var10003: Continuation = this as Continuation;
                                          this.label = 1;
                                          if (var10000.analyzeInScene(var10001, var10002, var10003) === var3x) {
                                             return var3x;
                                          }
                                          break;
                                       case 1:
                                          ResultKt.throwOnFailure(`$result`);
                                          break;
                                       default:
                                          throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                    }

                                    return Unit.INSTANCE;
                                 }

                                 public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                    val var3: Function2 = new <anonymous constructor>(this.$builtinAnalysis, this.$result, `$completion`);
                                    var3.L$0 = value;
                                    return var3 as Continuation<Unit>;
                                 }

                                 public final Object invoke(AnalyzeTaskRunner.Env p1, Continuation<? super Unit> p2) {
                                    return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                                 }
                              }) as Function2,
                              null,
                              20,
                              null
                           );
                        }

                        if (dataFlowOptions != null && dataFlowOptions.getEnableDataFlow() || target is SrcAnalyzeOptions || !this.getDisablePreAnalysis()) {
                           val var47: IPAnalysisEngine = new IPAnalysisEngine(mainConfig, null, 2, null);
                           AnalyzeTaskRunner.registerAnalysis$default(
                              var37,
                              "dataFlow",
                              var45,
                              null,
                              (
                                 new Function2<AnalyzeTaskRunner.Env, Continuation<? super Unit>, Object>(
                                    this, monitor, dataFlowOptions, target, mainConfig, var29, locatorx, info, var36, var47, null
                                 )// $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getVarDefinitions()" because "stat" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1468)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingExprent(VarDefinitionHelper.java:1679)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1496)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1545)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.remapClashingNames(VarDefinitionHelper.java:1458)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.rerunClashing(VarProcessor.java:99)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:118)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:352)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.toJava(NewExprent.java:407)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:761)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:727)
      
                              ) as Function2,
                              (new Function1<Continuation<? super Unit>, Object>(this, var47, null) {
                                 int label;

                                 {
                                    super(1, `$completionx`);
                                    this.this$0 = `$receiver`;
                                    this.$analyzer = `$analyzer`;
                                 }

                                 public final Object invokeSuspend(Object $result) {
                                    IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                    switch (this.label) {
                                       case 0:
                                          ResultKt.throwOnFailure(`$result`);
                                          if (FySastCli.access$getSerializeCG(this.this$0)) {
                                             this.$analyzer.dump(FySastCli.access$getOutput(this.this$0));
                                          }

                                          return Unit.INSTANCE;
                                       default:
                                          throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                    }
                                 }

                                 public final Continuation<Unit> create(Continuation<?> $completion) {
                                    return (new <anonymous constructor>(this.this$0, this.$analyzer, `$completion`)) as Continuation<Unit>;
                                 }

                                 public final Object invoke(Continuation<? super Unit> p1) {
                                    return (this.create(p1) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                                 }
                              }) as Function1,
                              4,
                              null
                           );
                        }

                        if (var28 != null && var28.getEnableFlowDroid()) {
                           val var48: InfoflowConfiguration = (target as IClassAnalyzeOptionGroup).getInfoFlowConfig();
                           var28.configureInfoFlowConfig(var48, mainConfig);
                           val var50: FlowDroidEngine = new FlowDroidEngine(mainConfig, var48, var28.getExtInfoFlowConfig());
                           val var51: Void = null;
                           AnalyzeTaskRunner.registerAnalysis$default(
                              var37,
                              "flowDroid",
                              var45,
                              (new Function1<Continuation<? super Unit>, Object>(var50, var51, null) {
                                 int label;

                                 {
                                    super(1, `$completionx`);
                                    this.$flowEngine = `$flowEngine`;
                                    this.$cfgFactory = `$cfgFactory`;
                                 }

                                 public final Object invokeSuspend(Object $result) {
                                    IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                    switch (this.label) {
                                       case 0:
                                          ResultKt.throwOnFailure(`$result`);
                                          this.$flowEngine.beforeAnalyze(this.$cfgFactory as BiDirICFGFactory);
                                          return Unit.INSTANCE;
                                       default:
                                          throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                    }
                                 }

                                 public final Continuation<Unit> create(Continuation<?> $completion) {
                                    return (new <anonymous constructor>(this.$flowEngine, this.$cfgFactory, `$completion`)) as Continuation<Unit>;
                                 }

                                 public final Object invoke(Continuation<? super Unit> p1) {
                                    return (this.create(p1) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                                 }
                              }) as Function1,
                              (
                                 new Function2<AnalyzeTaskRunner.Env, Continuation<? super Unit>, Object>(
                                    mainConfig, locatorx, var29, info, var36, var50, var51, null
                                 ) {
                                    int label;

                                    {
                                       super(2, `$completionx`);
                                       this.$mainConfig = `$mainConfig`;
                                       this.$locator = `$locator`;
                                       this.$sootCtx = `$sootCtx`;
                                       this.$info = `$info`;
                                       this.$result = `$result`;
                                       this.$flowEngine = `$flowEngine`;
                                       this.$cfgFactory = `$cfgFactory`;
                                    }

                                    public final Object invokeSuspend(Object $result) {
                                       val var4: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                       switch (this.label) {
                                          case 0:
                                             ResultKt.throwOnFailure(`$result`);
                                             val it: AnalyzeTaskRunner.Env = this.L$0 as AnalyzeTaskRunner.Env;
                                             val var10002: MainConfig = this.$mainConfig;
                                             val var10003: IProjectFileLocator = this.$locator;
                                             val var10004: CallGraph = this.$sootCtx.getSootMethodCallGraph();
                                             val var10005: SootInfoCache = this.$info;
                                             val var10006: IPreAnalysisResultCollector = this.$result;
                                             val var10007: Scene = Scene.v();
                                             val analysisImpl: PreAnalysisImpl = new PreAnalysisImpl(var10002, var10003, var10004, var10005, var10006, var10007);
                                             this.$result.setPreAnalysis(analysisImpl);
                                             val var10000: FlowDroidEngine = this.$flowEngine;
                                             val var10001: IEntryPointProvider.AnalyzeTask = it.getTask();
                                             val var5: IEntryPointProvider = it.getProvider();
                                             val var6: SootCtx = it.getSootCtx();
                                             val var7: PreAnalysisCoroutineScope = analysisImpl;
                                             val var8: BiDirICFGFactory = this.$cfgFactory as BiDirICFGFactory;
                                             val var9: java.util.Set = SetsKt.setOf(this.$result);
                                             val var10: java.util.Set = SetsKt.setOf(this.$result);
                                             val var10008: IMissingSummaryReporter = this.$result;
                                             val var10009: Continuation = this as Continuation;
                                             this.label = 1;
                                             if (var10000.analyzeInScene(var10001, var5, var6, var7, var8, var9, var10, var10008, var10009) === var4) {
                                                return var4;
                                             }
                                             break;
                                          case 1:
                                             ResultKt.throwOnFailure(`$result`);
                                             break;
                                          default:
                                             throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                       }

                                       return Unit.INSTANCE;
                                    }

                                    public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                       val var3: Function2 = new <anonymous constructor>(
                                          this.$mainConfig,
                                          this.$locator,
                                          this.$sootCtx,
                                          this.$info,
                                          this.$result,
                                          this.$flowEngine,
                                          this.$cfgFactory,
                                          `$completion`
                                       );
                                       var3.L$0 = value;
                                       return var3 as Continuation<Unit>;
                                    }

                                    public final Object invoke(AnalyzeTaskRunner.Env p1, Continuation<? super Unit> p2) {
                                       return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                                    }
                                 }
                              ) as Function2,
                              null,
                              16,
                              null
                           );
                        }

                        if (utAnalyzeOptions != null && utAnalyzeOptions.getEnableUtAnalyze()) {
                           throw new NotImplementedError(null, 1, null);
                        }

                        `$continuation`.L$0 = this;
                        `$continuation`.L$1 = monitor;
                        `$continuation`.L$2 = mainConfig;
                        `$continuation`.L$3 = locatorx;
                        `$continuation`.L$4 = var36;
                        `$continuation`.label = 1;
                        if (var37.run(`$continuation`) === var27) {
                           return var27;
                        }
                        break;
                     case 1:
                        result = `$continuation`.L$4 as ResultCollector;
                        locator = `$continuation`.L$3 as ProjectFileLocator;
                        mainConfig = `$continuation`.L$2 as MainConfig;
                        monitor = `$continuation`.L$1 as MetricsMonitor;
                        this = `$continuation`.L$0 as FySastCli;
                        ResultKt.throwOnFailure(`$result`);
                        break;
                     case 2:
                        result = `$continuation`.L$4 as ResultCollector;
                        locator = `$continuation`.L$3 as ProjectFileLocator;
                        mainConfig = `$continuation`.L$2 as MainConfig;
                        monitor = `$continuation`.L$1 as MetricsMonitor;
                        this = `$continuation`.L$0 as FySastCli;
                        ResultKt.throwOnFailure(`$result`);
                        var61 = (java.util.Set)`$result`;
                        break label159;
                     case 3:
                        out = `$continuation`.L$5 as java.lang.String;
                        result = `$continuation`.L$4 as ResultCollector;
                        locator = `$continuation`.L$3 as ProjectFileLocator;
                        mainConfig = `$continuation`.L$2 as MainConfig;
                        monitor = `$continuation`.L$1 as MetricsMonitor;
                        this = `$continuation`.L$0 as FySastCli;
                        ResultKt.throwOnFailure(`$result`);
                        var61 = (java.util.Set)`$result`;
                        break label158;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }

                  if (!this.getMakeScorecard()) {
                     break label145;
                  }

                  val var59: AccuracyValidator = new AccuracyValidator(mainConfig);
                  val var10002: IProjectFileLocator = locator;
                  `$continuation`.L$0 = this;
                  `$continuation`.L$1 = monitor;
                  `$continuation`.L$2 = mainConfig;
                  `$continuation`.L$3 = locator;
                  `$continuation`.L$4 = result;
                  `$continuation`.label = 2;
                  var61 = (java.util.Set)var59.makeScore(result, var10002, `$continuation`);
                  if (var61 === var27) {
                     return var27;
                  }
               }

               logger.warn(FySastCli::runAnalyze$lambda$105);
            }

            val var60: java.lang.String = this.getDumpSrcFileList();
            if (var60 == null) {
               var61 = null;
               break label176;
            }

            out = var60;
            `$continuation`.L$0 = this;
            `$continuation`.L$1 = monitor;
            `$continuation`.L$2 = mainConfig;
            `$continuation`.L$3 = locator;
            `$continuation`.L$4 = result;
            `$continuation`.L$5 = var60;
            `$continuation`.label = 3;
            var61 = (java.util.Set)this.getFilteredJavaSourceFiles(mainConfig, locator, `$continuation`);
            if (var61 === var27) {
               return var27;
            }
         }

         this.writeSourceFileListForProbe(mainConfig, Resource.INSTANCE.fileOf(out), var61);
         var61 = var61;
      }

      val var43: java.util.Set = var61;
      BuildersKt.runBlocking$default(
         null,
         (
            new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, monitor, result, var43, mainConfig, locator, null)// $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getVarDefinitions()" because "stat" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1468)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingExprent(VarDefinitionHelper.java:1679)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1496)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1545)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.remapClashingNames(VarDefinitionHelper.java:1458)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.rerunClashing(VarProcessor.java:99)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:118)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:352)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.toJava(NewExprent.java:407)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:761)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:727)
      
         ) as Function2,
         1,
         null
      );
      if (!Options.v().oaat() && mainConfig.getDumpSootScene()) {
         logger.info(FySastCli::runAnalyze$lambda$108);
         PackManager.v().writeOutput();
      }

      this.addFilesToDataBase(mainConfig, this.sqliteFileIndexes);
      return Unit.INSTANCE;
   }

   private fun constructPmdAnalyzerCmd(fileList: IResFile, csvResultPath: IResFile): List<String>? {
      val javaExe: java.lang.String = OS.INSTANCE.getJavaExecutableFilePath();
      if (javaExe == null) {
         logger.warn(FySastCli::constructPmdAnalyzerCmd$lambda$109);
         return null;
      } else {
         val outputDir: IResource = csvResultPath.getParent();
         if (outputDir != null && !outputDir.getExists()) {
            outputDir.mkdirs();
         }

         var var20: Path;
         label31: {
            var20 = OS.INSTANCE.getJarBinPath();
            if (var20 != null) {
               var20 = var20.getParent();
               if (var20 != null) {
                  var20 = var20.getParent();
                  if (var20 != null) {
                     var20 = var20.resolve("pmd");
                     break label31;
                  }
               }
            }

            var20 = null;
         }

         if (var20 == null) {
            logger.warn(FySastCli::constructPmdAnalyzerCmd$lambda$110);
            return null;
         } else {
            val var10001: Array<LinkOption> = new LinkOption[0];
            if (Files.exists(var20, Arrays.copyOf(var10001, var10001.length)) && PathExtensionsKt.isDirectory(var20)) {
               val vmOptions: Array<java.lang.String> = new java.lang.String[]{
                  "check",
                  "--no-cache",
                  "--threads",
                  "8",
                  "-R",
                  var20.resolve("conf/corax-rule.xml").toString(),
                  "-f",
                  "coraxcsv",
                  "-r",
                  csvResultPath.toString(),
                  "--file-list",
                  fileList.toString()
               };
               val var21: java.util.List = ManagementFactory.getRuntimeMXBean().getInputArguments();
               val var14: Array<java.lang.String> = var21.toArray(new java.lang.String[0]);
               val var22: java.lang.Iterable = CollectionsKt.listOf(
                  new java.lang.String[]{var20.resolve("conf").toString(), "${var20.resolve("lib")}${File.separator}*"}
               );
               val var23: java.lang.String = File.pathSeparator;
               val pmdClasspathArg: java.lang.String = CollectionsKt.joinToString$default(var22, var23, null, null, 0, null, null, 62, null);
               val var13: SpreadBuilder = new SpreadBuilder(6);
               var13.add(javaExe);
               var13.addSpread(var14);
               var13.add("-classpath");
               var13.add(pmdClasspathArg);
               var13.add("net.sourceforge.pmd.cli.PmdCli");
               var13.addSpread(vmOptions);
               val pmdAnalyzerCmd: java.util.List = CollectionsKt.mutableListOf(var13.toArray(new java.lang.String[var13.size()]));
               logger.debug(FySastCli::constructPmdAnalyzerCmd$lambda$112);
               return pmdAnalyzerCmd;
            } else {
               logger.debug(FySastCli::constructPmdAnalyzerCmd$lambda$111);
               return null;
            }
         }
      }
   }

   public fun main2(argv: List<String>) {
      logger.info(FySastCli::main2$lambda$113);
      OS.INSTANCE.setArgs(argv.toArray(new java.lang.String[0]));
      Runtime.getRuntime().addShutdownHook(new Thread(<unrepresentable>.INSTANCE));
      this.main(argv);
   }

   public fun main2(argv: Array<String>) {
      this.main2(ArraysKt.toList(argv));
   }

   @JvmStatic
   fun `lambda$1$lambda$0`(it: Context): MordantHelpFormatter {
      return new MordantHelpFormatter(it, null, true, true, 2, null);
   }

   @JvmStatic
   fun com.github.ajalt.clikt.core.Context.Builder.`_init_$lambda$1`(): Unit {
      `$this$context`.setHelpFormatter(FySastCli::lambda$1$lambda$0);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `defaultOutput_delegate$lambda$7`(): IResDirectory {
      return Resource.INSTANCE.dirOf("${System.getProperty("user.dir")}${File.separator}output");
   }

   @JvmStatic
   fun `printOptions$lambda$33`(`$theme`: Theme, it: Option): java.lang.CharSequence {
      val var6: java.lang.CharSequence;
      if (it is OptionWithValues) {
         var var3: Any;
         try {
            var3 = (it as OptionWithValues).getValue();
         } catch (var5: IllegalStateException) {
            var3 = null;
         }

         val var10000: java.lang.String = `$theme`.getInfo().invoke(it.getNames().toString());
         val var10001: TextStyle = `$theme`.getWarning();
         val var10002: java.lang.String = Objects.toString(var3);
         var6 = "$var10000 \"${var10001.invoke(var10002)}\"";
      } else {
         var6 = it.getNames().toString();
      }

      return var6;
   }

   @JvmStatic
   fun `printOptions$lambda$34`(): Any {
      return Theme.Companion.getDefault().getInfo().invoke("Current work directory: ${FilesKt.normalize(new File(".")).getAbsolutePath()}");
   }

   @JvmStatic
   fun `printOptions$lambda$35`(): Any {
      return Theme.Companion.getDefault().getInfo().invoke("PID: ${ProcessHandle.current().pid()}");
   }

   @JvmStatic
   fun `printOptions$lambda$36`(): Any {
      return "log files: ${AnalyzerEnv.INSTANCE.getLastLogFile().getParent()}";
   }

   @JvmStatic
   fun `printOptions$lambda$37`(`$info`: java.lang.String): Any {
      return "\n$`$info`";
   }

   @JvmStatic
   fun `printOptions$lambda$38`(`$theme`: Theme): Any {
      val var10000: java.lang.String = `$theme`.getSuccess().invoke(ApplicationKt.getVersion());
      val var10001: URL = OS.INSTANCE.getBinaryUrl();
      return "corax $var10000: ${if (var10001 != null) var10001.getPath() else null}}";
   }

   @JvmStatic
   fun `compatibleOldCheckerNames$lambda$40`(`$e`: Exception): Any {
      return `$e`.getMessage();
   }

   @JvmStatic
   fun `parseConfig$lambda$46`(`$name`: java.lang.String, `$configDirs`: java.util.List, `$tempFile`: IResFile, `$tempFileDir`: IResource): Any {
      val `$this$map$iv`: java.lang.Iterable = `$configDirs`;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$configDirs`, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as IResource).getAbsolute().getNormalize());
      }

      return "\n\n\nfile: $`$name` is not exists in plugin directories: [${CollectionsKt.joinToString$default(
         `destination$iv$iv` as java.util.List, "\n\t", null, null, 0, null, null, 62, null
      )}].\nA template SA-configuration file has been generated for you. Please edit it then run it again.\n-> $`$tempFile`\n-> args: --config $`$name`@$`$tempFileDir`\n\n\n";
   }

   @JvmStatic
   fun `parseConfig$lambda$47`(`$ymlConfig`: IResFile): Any {
      return "Use SA-Configuration yml file: $`$ymlConfig`";
   }

   @JvmStatic
   fun `defaultConfig$lambda$48`(): Any {
      return "Try find config from local env: CORAX_CONFIG_DEFAULT_DIR";
   }

   @JvmStatic
   fun `defaultConfig$lambda$49`(): Any {
      return "No config exists in the local env: CORAX_CONFIG_DEFAULT_DIR";
   }

   @JvmStatic
   fun `setTimeOut$lambda$51$lambda$50`(`this$0`: FySastCli): Any {
      return "Custom analysis shutdown timer has been exceeded in ${`this$0`.getTimeout()} seconds";
   }

   @JvmStatic
   fun `setTimeOut$lambda$51`(`this$0`: FySastCli): Unit {
      logger.warn(FySastCli::setTimeOut$lambda$51$lambda$50);
      System.exit(700);
      throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
   }

   @JvmStatic
   fun `run$lambda$54`(`this$0`: FySastCli): Any {
      return "Output: ${`this$0`.getOutput().getAbsolute().getNormalize()}";
   }

   @JvmStatic
   fun `run$lambda$55`(`$startTime`: LocalDateTime): Any {
      return "Analysis was completed in ${TimeUtilsKt.prettyPrintTime$default(`$startTime`, null, 1, null)}";
   }

   @JvmStatic
   fun `run$lambda$56`(`this$0`: FySastCli): Any {
      return "Output: ${`this$0`.getOutput().getAbsolute().getNormalize()}";
   }

   @JvmStatic
   fun `run$lambda$57`(`$startTime`: LocalDateTime): Any {
      return "Analysis was terminated after ${TimeUtilsKt.prettyPrintTime$default(`$startTime`, null, 1, null)}";
   }

   @JvmStatic
   fun `configureMainConfig$lambda$67$lambda$66`(`$gen`: CheckerInfoGenerator): CheckerInfoGenResult {
      return if (`$gen` != null) `$gen`.getCheckerInfo(false) else null;
   }

   @JvmStatic
   fun `showMetrics$lambda$83$lambda$80`(number: Double): Double {
      return MathKt.roundToLong(number / 0.5) * 0.5;
   }

   @JvmStatic
   fun `showMetrics$lambda$83$lambda$81`(`$xmxInfo`: java.lang.String, `$info`: java.lang.String): Any {
      return "Hardware Info: $`$xmxInfo` $`$info`";
   }

   @JvmStatic
   fun `showMetrics$lambda$83$lambda$82`(`$xmxHint`: java.lang.String): Any {
      return "\nYou can add the following command before run analysis to fully utilize the machine's performance:\n\t > ${Theme.Companion
         .getDefault()
         .getWarning()
         .invoke(`$xmxHint`)}\n";
   }

   @JvmStatic
   fun `checkEnv$lambda$87$lambda$86`(it: IResFile): IResource {
      val var10000: IResource = it.getParent();
      return if (var10000 != null) var10000.getParent() else null;
   }

   @JvmStatic
   fun `getFilteredJavaSourceFiles$lambda$89$lambda$88`(`$mainConfig`: MainConfig, it: IResFile): Boolean {
      if (!`$mainConfig`.getAutoAppSrcInZipScheme() && !it.isFileScheme()) {
         return false;
      } else {
         return ScanFilter.getActionOf$default(`$mainConfig`.getScanFilter(), null, it.getPath(), null, 4, null) != ProcessRule.ScanAction.Skip;
      }
   }

   @JvmStatic
   fun `runCodeMetrics$lambda$93`(`$pb`: ObjectRef): Any {
      val var10000: java.util.List = (`$pb`.element as ProcessBuilder).command();
      return "PMD command line: ${CollectionsKt.joinToString$default(var10000, " ", null, null, 0, null, null, 62, null)}";
   }

   @JvmStatic
   fun `runCodeMetrics$lambda$94`(`$e`: Exception): Any {
      return "There are some errors when running code metrics analysis of pmd: ${`$e`.getMessage()}";
   }

   @JvmStatic
   fun `top$lambda$96`(`$io`: OutputStreamWriter): Unit {
      val `$this$top_u24lambda_u2496_u24lambda_u2495`: ProcessInfoView = ProcessInfoView.Companion.getGlobalProcessInfo();
      `$io`.write(
         "${LocalDateTime.now()}: ${`$this$top_u24lambda_u2496_u24lambda_u2495`.getProcessInfoText()} ${`$this$top_u24lambda_u2496_u24lambda_u2495`.getCpuLoadText()}\n"
      );
      `$io`.flush();
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `runAnalyze$lambda$100$lambda$99`(`$totalAnySourceFileNum`: Long): Any {
      return "Total java source files: $`$totalAnySourceFileNum`";
   }

   @JvmStatic
   fun `runAnalyze$lambda$101`(`$locator`: ProjectFileLocator, sc: SootClass): Path {
      val var10000: IResFile = `$locator`.get(new ClassResInfo(sc), NullWrapperFileGenerator.INSTANCE);
      return if (var10000 != null) var10000.getPath() else null;
   }

   @JvmStatic
   fun `runAnalyze$lambda$105`(`$res`: AccuracyValidator.Result): Any {
      return `$res`;
   }

   @JvmStatic
   fun `runAnalyze$lambda$108`(): Any {
      return "dump soot scene ...";
   }

   @JvmStatic
   fun `constructPmdAnalyzerCmd$lambda$109`(): Any {
      return "Java executable path is null";
   }

   @JvmStatic
   fun `constructPmdAnalyzerCmd$lambda$110`(): Any {
      return "Executable main jar path is null";
   }

   @JvmStatic
   fun `constructPmdAnalyzerCmd$lambda$111`(): Any {
      return "PMD dir does not exist or is not a directory";
   }

   @JvmStatic
   fun `constructPmdAnalyzerCmd$lambda$112`(`$pmdAnalyzerCmd`: java.util.List): Any {
      return "The command of PMD is: $`$pmdAnalyzerCmd`";
   }

   @JvmStatic
   fun `main2$lambda$113`(`$argv`: java.util.List): Any {
      return "argv is ${CollectionsKt.joinToString$default(`$argv`, " ", null, null, 0, null, null, 62, null)}";
   }

   @JvmStatic
   fun `logger$lambda$114`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }

   public enum class ResultType {
      PLIST,
      SARIF,
      SQLITE,
      SarifPackSrc,
      SarifCopySrc,
      COUNTER
      @JvmStatic
      fun getEntries(): EnumEntries<FySastCli.ResultType> {
         return $ENTRIES;
      }
   }
}
