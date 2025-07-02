package cn.sast.framework.validator

import cn.sast.api.config.MainConfig
import cn.sast.api.config.ScanFilter
import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.ExpectBugAnnotationData
import cn.sast.api.report.Report
import cn.sast.api.report.ReportKt
import cn.sast.common.FileSystemLocator
import cn.sast.common.GLB
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.Resource
import cn.sast.common.ResourceKt
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.report.NullWrapperFileGenerator
import cn.sast.framework.report.ProjectFileLocator
import cn.sast.framework.result.ResultCollector
import com.feysh.corax.config.api.AIAnalysisApiKt
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.IChecker
import com.feysh.corax.config.api.IRule
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.FileMatch
import com.github.doyaaaaaken.kotlincsv.client.CsvFileWriter
import com.github.doyaaaaaken.kotlincsv.client.CsvWriter
import com.github.doyaaaaaken.kotlincsv.dsl.CsvWriterDslKt
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.io.Writer
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef
import kotlinx.coroutines.AwaitKt
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.serialization.SerializersKt
import mu.KLogger
import mu.KotlinLogging
import java.util.Comparator
import kotlin.comparisons.compareValues

@SourceDebugExtension(["SMAP\nPrecisionMeasurement.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PrecisionMeasurement.kt\ncn/sast/framework/validator/AccuracyValidator\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 ProcessRule.kt\ncom/feysh/corax/config/api/rules/ProcessRule\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,423:1\n1557#2:424\n1628#2,3:425\n1053#2:429\n24#3:428\n1#4:430\n*S KotlinDebug\n*F\n+ 1 PrecisionMeasurement.kt\ncn/sast/framework/validator/AccuracyValidator\n*L\n86#1:424\n86#1:425,3\n81#1:429\n86#1:428\n*E\n"])
public class AccuracyValidator(mainConfig: MainConfig) {
   public final val mainConfig: MainConfig
   private final val logger: KLogger
   private final val extensions: List<String>

   private object AnnotationLineComparator : Comparator<ExpectBugAnnotationData> {
      override fun compare(a: ExpectBugAnnotationData, b: ExpectBugAnnotationData): Int {
         return compareValues(a.line, b.line)
      }
   }

   private object RowTypeComparator : Comparator<RowType> {
      override fun compare(a: RowType, b: RowType): Int {
         return compareValues(a.toString(), b.toString())
      }
   }

   public final val str: String
      public final get() {
         return "\"${CollectionsKt.joinToString$default(
            CollectionsKt.sortedWith(`$this$str`, AnnotationLineComparator),
            "\n",
            null,
            null,
            0,
            null,
            AccuracyValidator::_get_str_$lambda$3,
            30,
            null
         )}\"";
      }


   private final val rules: List<FileMatch>
   private final val pattern: Pattern

   init {
      this.mainConfig = mainConfig;
      this.logger = KotlinLogging.INSTANCE.logger(AccuracyValidator::logger$lambda$0);
      this.extensions = CollectionsKt.plus(
         ResourceKt.getJavaExtensions(),
         CollectionsKt.listOf(new java.lang.String[]{"yml", "txt", "gradle", "kts", "cnf", "conf", "config", "xml", "properties"})
      );
      val var17: ProcessRule = ProcessRule.INSTANCE;
      val var18: java.lang.Iterable = CollectionsKt.listOf(new java.lang.String[]{"build", "out", "target", ".idea", ".git"});
      val `$i$f$map`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var18, 10));

      for (Object item$iv$iv : var18) {
         `$i$f$map`.add("(-)path=/${`$i$f$mapTo` as java.lang.String}/");
      }

      val `$this$map$iv$iv`: java.lang.Iterable = `$i$f$map` as java.util.List;
      val var22: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$i$f$map` as java.util.List, 10));

      for (Object item$iv$iv$iv : $this$map$iv$iv) {
         var22.add(
            ProcessRule.InlineRuleStringSerialize.INSTANCE
               .deserializeMatchFromLineString(SerializersKt.serializer(ProcessRule.FileMatch::class), var25 as java.lang.String)
         );
      }

      this.rules = var22 as MutableList<ProcessRule.FileMatch>;
      val var26: Pattern = Pattern.compile("(?<escape>(!?))\\$ *(?<name>((`([^(`\\r\\n)])+`)|([a-zA-Z$_]+[a-zA-Z0-9$_.-]*)))", 8);
      this.pattern = var26;
   }

   public suspend fun makeScore(result: ResultCollector, locator: IProjectFileLocator): cn.sast.framework.validator.AccuracyValidator.Result {
      return BuildersKt.withContext(
         Dispatchers.getIO() as CoroutineContext,
         (
            new Function2<CoroutineScope, Continuation<? super AccuracyValidator.Result>, Object>(this, result, locator, null) {
               Object L$0;
               Object L$1;
               Object L$2;
               Object L$3;
               Object L$4;
               int label;

               {
                  super(2, `$completionx`);
                  this.this$0 = `$receiver`;
                  this.$result = `$result`;
                  this.$locator = `$locator`;
               }

               // $VF: Irreducible bytecode was duplicated to produce valid code
               public final Object invokeSuspend(Object $result) {
                  var expectedResults: java.util.Set;
                  label444: {
                     val var55: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                     var projectFileLocator: ProjectFileLocator;
                     var FP: AccuracyValidator;
                     var TN: java.util.Collection;
                     var typeNoAnnotated: java.util.Iterator;
                     switch (this.label) {
                        case 0:
                           ResultKt.throwOnFailure(`$result`);
                           projectFileLocator = new ProjectFileLocator(
                              this.this$0.getMainConfig().getMonitor(),
                              SetsKt.plus(
                                 SetsKt.plus(
                                    this.this$0.getMainConfig().getSourcePath() as java.util.Set,
                                    this.this$0.getMainConfig().getProcessDir() as java.lang.Iterable
                                 ),
                                 this.this$0.getMainConfig().getAutoAppClasses() as java.lang.Iterable
                              ),
                              null,
                              FileSystemLocator.TraverseMode.Default,
                              false
                           );
                           projectFileLocator.update();
                           expectedResults = new LinkedHashSet();
                           val outputDir: java.lang.Iterable = AccuracyValidator.access$getExtensions$p(this.this$0);
                           FP = this.this$0;
                           TN = new ArrayList();
                           typeNoAnnotated = outputDir.iterator();
                           break;
                        case 1:
                           typeNoAnnotated = this.L$4 as java.util.Iterator;
                           TN = this.L$3 as java.util.Collection;
                           FP = this.L$2 as AccuracyValidator;
                           expectedResults = this.L$1 as java.util.Set;
                           projectFileLocator = this.L$0 as ProjectFileLocator;
                           ResultKt.throwOnFailure(`$result`);
                           CollectionsKt.addAll(TN, SequencesKt.map(`$result` as Sequence, <unrepresentable>::invokeSuspend$lambda$1$lambda$0));
                           break;
                        case 2:
                           expectedResults = this.L$0 as java.util.Set;
                           ResultKt.throwOnFailure(`$result`);
                           break label444;
                        default:
                           throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                     }

                     while (typeNoAnnotated.hasNext()) {
                        val typeNoAnnotatedStream: java.lang.String = typeNoAnnotated.next() as java.lang.String;
                        this.L$0 = projectFileLocator;
                        this.L$1 = expectedResults;
                        this.L$2 = FP;
                        this.L$3 = TN;
                        this.L$4 = typeNoAnnotated;
                        this.label = 1;
                        val var10000: Any = projectFileLocator.getByFileExtension(typeNoAnnotatedStream, this);
                        if (var10000 === var55) {
                           return var55;
                        }

                        CollectionsKt.addAll(TN, SequencesKt.map(var10000 as Sequence, <unrepresentable>::invokeSuspend$lambda$1$lambda$0));
                     }

                     val var276: java.util.Collection = TN as java.util.List;
                     val var10001: Continuation = this as Continuation;
                     this.L$0 = expectedResults;
                     this.L$1 = null;
                     this.L$2 = null;
                     this.L$3 = null;
                     this.L$4 = null;
                     this.label = 2;
                     if (AwaitKt.joinAll(var276, var10001) === var55) {
                        return var55;
                     }
                  }

                  val var56: IResource = this.this$0.getMainConfig().getOutput_dir().resolve("report-accuracy-forms");
                  var56.deleteDirectoryRecursively();
                  var56.mkdirs();
                  val var57: java.util.Map = new LinkedHashMap();
                  val var59: java.util.Map = new LinkedHashMap();
                  val FN: java.util.Map = new LinkedHashMap();
                  val var60: java.util.Map = new LinkedHashMap();
                  val var64: OutputStreamWriter = new OutputStreamWriter(
                     new FileOutputStream(Resource.INSTANCE.fileOf("$var56/source-not-found.txt").getFile())
                  );
                  val var65: OutputStreamWriter = new OutputStreamWriter(
                     new FileOutputStream(Resource.INSTANCE.fileOf("$var56/check-type-no-annotated.txt").getFile())
                  );
                  val var69: java.lang.Iterable = this.$result.getReports();
                  val var72: java.util.Collection = new LinkedHashSet();
                  val foundReport: IProjectFileLocator = this.$locator;

                  for (Object element$iv : var69) {
                     val out3: Report = out2 as Report;
                     val p2: java.util.Set = new LinkedHashSet();
                     val var277: BugPathEvent = CollectionsKt.firstOrNull(out3.getPathEvents()) as BugPathEvent;
                     if (var277 != null) {
                        val var279: IResFile = foundReport.get(var277.getClassname(), NullWrapperFileGenerator.INSTANCE);
                        if (var279 != null) {
                           val var280: IResFile = var279.getAbsolute();
                           if (var280 != null) {
                              Boxing.boxBoolean(p2.add(new ActualBugAnnotationData(var280, var277.getRegion().startLine, out3.getCheckType())));
                           }
                        }
                     }

                     val var282: IResFile = foundReport.get(out3.getBugResFile(), NullWrapperFileGenerator.INSTANCE);
                     val var135: IResFile = if (var282 != null) var282.getAbsolute() else null;
                     if (var135 == null) {
                        var64.write("${out3.getBugResFile()}\n");
                     } else {
                        Boxing.boxBoolean(p2.add(new ActualBugAnnotationData(var135, out3.getRegion().startLine, out3.getCheckType())));
                     }

                     CollectionsKt.addAll(var72, p2);
                  }

                  val var67: java.util.Set = var72 as java.util.Set;
                  var64.close();
                  val var70: java.util.Set = GLB.INSTANCE.get();
                  val var74: java.util.Map = new LinkedHashMap();
                  val var76: java.util.Map = var74;

                  val var88: java.lang.Iterable;
                  for (Object element$iv : var88) {
                     val var127: CheckType = var116 as CheckType;
                     val var144: java.util.Set = new LinkedHashSet();
                     val all_tp: java.lang.Iterable = AIAnalysisApiKt.getCweRules(var127);
                     val all_fp: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(all_tp, 10));

                     for (Object item$iv$iv : $this$map$iv) {
                        all_fp.add((all_FPR as IRule).getRealName());
                     }

                     val fn: java.util.List = all_fp as java.util.List;
                     val `$this$map$ivx`: java.lang.Iterable = AIAnalysisApiKt.getFeyshRules(var127);
                     val `destination$iv$ivx`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$ivx`, 10));

                     for (Object item$iv$iv : $this$map$ivx) {
                        `destination$iv$ivx`.add((var210 as IRule).getRealName());
                     }

                     val var151: java.util.List = `destination$iv$ivx` as java.util.List;
                     val `$this$map$ivxx`: java.lang.Iterable = AIAnalysisApiKt.getCertRules(var127);
                     val `destination$iv$ivxx`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$ivxx`, 10));

                     for (Object item$iv$iv : $this$map$ivxx) {
                        `destination$iv$ivxx`.add((var217 as IRule).getRealName());
                     }

                     val var158: java.util.List = `destination$iv$ivxx` as java.util.List;
                     CollectionsKt.addAll(var144, fn);
                     CollectionsKt.addAll(var144, var151);
                     CollectionsKt.addAll(var144, var158);
                     CollectionsKt.addAll(
                        var144,
                        SetsKt.setOf(
                           new java.lang.String[]{
                              var127.toString(),
                              var127.getClass().getSimpleName(),
                              ReportKt.getPerfectName(var127),
                              var127.getChecker().getClass().getSimpleName(),
                              var127.getChecker().toString(),
                              var127.getReport().getRealName(),
                              var127.getChecker().getSimpleName()
                           }
                        )
                     );
                     CollectionsKt.addAll(var144, var127.getAliasNames());

                     for (Object element$ivx : $this$map$ivxx) {
                        val var206: java.lang.String = `element$ivx` as java.lang.String;
                        var var284: Locale = Locale.getDefault();
                        val var285: java.lang.String = var206.toLowerCase(var284);
                        val total: Any = var76.get(var285);
                        if (total == null) {
                           val var252: Any = new LinkedHashSet();
                           var76.put(var285, var252);
                           var284 = (Locale)var252;
                        } else {
                           var284 = (Locale)total;
                        }

                        (var284 as java.util.Set).add(var127);
                     }
                  }

                  val var73: java.util.Map = var74;
                  val var75: java.util.Set = new LinkedHashSet();

                  for (ExpectBugAnnotationData expect : expectedResults) {
                     var var89: Boolean = false;
                     val var106: java.util.Set = var73.get(var81.getBug()) as java.util.Set;
                     val var98: java.util.Collection = if (var106 != null) var106 else CollectionsKt.emptyList();
                     if (var98.size() <= 1 && var98.isEmpty()) {
                        AccuracyValidator.access$getLogger$p(this.this$0).debug(<unrepresentable>::invokeSuspend$lambda$15);
                     }

                     for (CheckType possibleType : possibleTypes) {
                        val var128: ActualBugAnnotationData = new ActualBugAnnotationData(var81.getFile(), var81.getLine(), var117);
                        if (var67.contains(var128)) {
                           switch (expect.getKind()) {
                              case Expect:
                                 val var160: Any = new RowCheckType(var117);
                                 val var182: Any = var59.get(var160);
                                 val var288: Any;
                                 if (var182 == null) {
                                    val var196: Any = new LinkedHashSet();
                                    var59.put(var160, var196);
                                    var288 = var196;
                                 } else {
                                    var288 = var182;
                                 }

                                 Boxing.boxBoolean((var288 as java.util.Set).add(var81));
                                 break;
                              case Escape:
                                 val var159: Any = new RowCheckType(var117);
                                 val var181: Any = var57.get(var159);
                                 val var287: Any;
                                 if (var181 == null) {
                                    val var194: Any = new LinkedHashSet();
                                    var57.put(var159, var194);
                                    var287 = var194;
                                 } else {
                                    var287 = var181;
                                 }

                                 (var287 as java.util.Set).add(var81);
                                 AccuracyValidator.access$getLogger$p(this.this$0).debug(<unrepresentable>::invokeSuspend$lambda$18);
                                 break;
                              default:
                                 throw new NoWhenBranchMatchedException();
                           }

                           var89 = true;
                           var75.add(var128);
                           break;
                        }
                     }

                     if (!var89) {
                        val var108: java.util.Iterator = var98.iterator();
                        if (var108.hasNext()) {
                           val possibleTypex: CheckType = var108.next() as CheckType;
                           switch (expect.getKind()) {
                              case Expect:
                                 val var153: Any = new RowCheckType(possibleTypex);
                                 val var175: Any = FN.get(var153);
                                 val var290: Any;
                                 if (var175 == null) {
                                    val var186: Any = new LinkedHashSet();
                                    FN.put(var153, var186);
                                    var290 = var186;
                                 } else {
                                    var290 = var175;
                                 }

                                 (var290 as java.util.Set).add(var81);
                                 AccuracyValidator.access$getLogger$p(this.this$0).debug(<unrepresentable>::invokeSuspend$lambda$20);
                                 break;
                              case Escape:
                                 val var152: Any = new RowCheckType(possibleTypex);
                                 val var174: Any = var60.get(var152);
                                 val var289: Any;
                                 if (var174 == null) {
                                    val var184: Any = new LinkedHashSet();
                                    var60.put(var152, var184);
                                    var289 = var184;
                                 } else {
                                    var289 = var174;
                                 }

                                 Boxing.boxBoolean((var289 as java.util.Set).add(var81));
                                 break;
                              default:
                                 throw new NoWhenBranchMatchedException();
                           }

                           var89 = true;
                        }

                        if (!var89) {
                           switch (expect.getKind()) {
                              case Expect:
                                 val var140: Any = new RowUnknownType(var81.getBug() as java.lang.String);
                                 val var155: Any = FN.get(var140);
                                 val var292: Any;
                                 if (var155 == null) {
                                    val var166: Any = new LinkedHashSet();
                                    FN.put(var140, var166);
                                    var292 = var166;
                                 } else {
                                    var292 = var155;
                                 }

                                 (var292 as java.util.Set).add(var81);
                                 break;
                              case Escape:
                                 val var139: Any = new RowUnknownType(var81.getBug() as java.lang.String);
                                 val var154: Any = var60.get(var139);
                                 val var291: Any;
                                 if (var154 == null) {
                                    val var164: Any = new LinkedHashSet();
                                    var60.put(var139, var164);
                                    var291 = var164;
                                 } else {
                                    var291 = var154;
                                 }

                                 (var291 as java.util.Set).add(var81);
                                 break;
                              default:
                                 throw new NoWhenBranchMatchedException();
                           }
                        }
                     }
                  }

                  for (Object element$iv : var78) {
                     var65.write("${var99 as ActualBugAnnotationData}\n");
                  }

                  var65.close();
                  val var79: java.util.Set = new LinkedHashSet();

                  val var83: java.lang.Iterable;
                  for (Object item$iv : var83) {
                     var79.add(var111 as RowType);
                  }

                  for (Object item$iv : var83) {
                     var79.add(var112 as RowType);
                  }

                  for (Object item$iv : var83) {
                     var79.add(var113 as RowType);
                  }

                  for (Object item$iv : var83) {
                     var79.add(var114 as RowType);
                  }

                  val var87: IResFile = Resource.INSTANCE.fileOf("$var56/Scorecard.csv");
                  val var95: IResFile = Resource.INSTANCE.fileOf("$var56/FalsePN.csv");
                  val var104: IResFile = Resource.INSTANCE.fileOf("$var56/Scorecard.txt");
                  val var115: CsvFileWriter = CsvWriter.openAndGetRawWriter$default(
                     CsvWriterDslKt.csvWriter$default(null, 1, null), var87.getFile(), false, 2, null
                  );
                  val var125: CsvFileWriter = CsvWriter.openAndGetRawWriter$default(
                     CsvWriterDslKt.csvWriter$default(null, 1, null), var95.getFile(), false, 2, null
                  );
                  val var141: File = var104.getFile();
                  val var148: Charset = Charsets.UTF_8;
                  val var176: Writer = new OutputStreamWriter(new FileOutputStream(var141), var148);
                  val var134: PrettyTable = new PrettyTable(
                     new PrintWriter(if (var176 is BufferedWriter) var176 as BufferedWriter else new BufferedWriter(var176, 8192)),
                     CollectionsKt.listOf(new java.lang.String[]{"rule", "checkType", "TP", "FN", "TN", "FP", "TPR", "FPR", "score"})
                  );
                  val var143: java.util.List = CollectionsKt.listOf(
                     new java.lang.String[]{"checker", "CheckType", "rule", "CWE", "Positive(TP", "FN)", "Negative(TN", "FP)", "Total", "TPR", "FPR", "Score"}
                  );
                  var115.writeRow(var143);
                  var125.writeRow(var143);
                  var var150: Int = 0;
                  val var156: IntRef = new IntRef();
                  var var168: Int = 0;
                  var var177: Int = 0;
                  var var187: Int = 0;

                  for (RowType checkType : CollectionsKt.sortedWith(rowTypes, RowTypeComparator)) {
                     var var213: java.lang.String = null;
                     var endRow: java.lang.String = null;
                     if (var208 is RowCheckType) {
                        var var226: IRule = CollectionsKt.firstOrNull(AIAnalysisApiKt.getCweRules((var208 as RowCheckType).getType())) as IRule;
                        var213 = if (var226 != null) var226.getRealName() else null;
                        var226 = CollectionsKt.firstOrNull(AIAnalysisApiKt.getFeyshRules((var208 as RowCheckType).getType())) as IRule;
                        endRow = if (var226 != null) var226.getRealName() else null;
                     } else if (var208 !is RowUnknownType) {
                        throw new NoWhenBranchMatchedException();
                     }

                     if (var213 == null) {
                        var213 = "unknown";
                     }

                     if (endRow == null) {
                        endRow = "unknown";
                     }

                     var FPR: Float;
                     var Score: Float;
                     var var251: Int;
                     var var254: Float;
                     var var297: Int;
                     label310: {
                        val var228: java.util.Set = var59.get(var208) as java.util.Set;
                        var297 = if (var228 != null) var228.size() else 0;
                        val var235: java.util.Set = FN.get(var208) as java.util.Set;
                        var297 = if (var235 != null) var235.size() else 0;
                        val var243: java.util.Set = var60.get(var208) as java.util.Set;
                        var297 = if (var243 != null) var243.size() else 0;
                        val var250: java.util.Set = var57.get(var208) as java.util.Set;
                        var297 = if (var250 != null) var250.size() else 0;
                        var251 = var297 + var297 + var297 + var297;
                        var254 = if (var297 + var297 == 0) 0.0F else (float)var297 / (var297 + var297);
                        FPR = if (var297 + var297 == 0) 0.0F else (float)var297 / (var297 + var297);
                        Score = var254 - (if (var297 + var297 == 0) 0.0F else (float)var297 / (var297 + var297));
                        var150 += var251;
                        var156.element += var297;
                        var168 += var297;
                        var177 += var297;
                        var187 += var297;
                        val checkTypeName: RowCheckType = var208 as? RowCheckType;
                        if ((var208 as? RowCheckType) != null) {
                           val var45: CheckType = checkTypeName.getType();
                           if (var45 != null) {
                              val var46: IChecker = var45.getChecker();
                              if (var46 != null) {
                                 val var47: Class = var46.getClass();
                                 if (var47 != null) {
                                    var301 = var47.getSimpleName();
                                    break label310;
                                 }
                              }
                           }
                        }

                        var301 = null;
                     }

                     label303:
                     if (var208 is RowCheckType) {
                        val var259: RowCheckType = var208 as? RowCheckType;
                        if ((var208 as? RowCheckType) != null) {
                           val var264: CheckType = var259.getType();
                           if (var264 != null) {
                              val var48: Class = var264.getClass();
                              if (var48 != null) {
                                 var302 = var48.getSimpleName();
                                 break label303;
                              }
                           }
                        }

                        var302 = null;
                     } else {
                        if (var208 !is RowUnknownType) {
                           throw new NoWhenBranchMatchedException();
                        }

                        var302 = (var208 as RowUnknownType).getType();
                     }

                     var115.writeRow(
                        new Object[]{
                           var301,
                           var302,
                           endRow,
                           var213,
                           Boxing.boxInt(var297),
                           Boxing.boxInt(var297),
                           Boxing.boxInt(var297),
                           Boxing.boxInt(var297),
                           Boxing.boxInt(var251),
                           PrecisionMeasurementKt.getFm(var254),
                           PrecisionMeasurementKt.getFm(FPR),
                           PrecisionMeasurementKt.getFm(Score)
                        }
                     );
                     val var257: Array<Any> = new Object[12];
                     var257[0] = var301;
                     var257[1] = var302;
                     var257[2] = endRow;
                     var257[3] = var213;
                     var var260: java.util.Set = var59.get(var208) as java.util.Set;
                     var257[4] = if (var260 != null) this.this$0.getStr(var260) else null;
                     var260 = FN.get(var208) as java.util.Set;
                     var257[5] = if (var260 != null) this.this$0.getStr(var260) else null;
                     var260 = var60.get(var208) as java.util.Set;
                     var257[6] = if (var260 != null) this.this$0.getStr(var260) else null;
                     var260 = var57.get(var208) as java.util.Set;
                     var257[7] = if (var260 != null) this.this$0.getStr(var260) else null;
                     var257[8] = Boxing.boxInt(var251);
                     var257[9] = PrecisionMeasurementKt.getFm(var254);
                     var257[10] = PrecisionMeasurementKt.getFm(FPR);
                     var257[11] = PrecisionMeasurementKt.getFm(Score);
                     var125.writeRow(var257);
                     var134.addLine(
                        CollectionsKt.listOf(
                           new Object[]{
                              endRow,
                              var302,
                              Boxing.boxInt(var297),
                              Boxing.boxInt(var297),
                              Boxing.boxInt(var297),
                              Boxing.boxInt(var297),
                              PrecisionMeasurementKt.getFm(var254),
                              PrecisionMeasurementKt.getFm(FPR),
                              PrecisionMeasurementKt.getFm(Score)
                           }
                        )
                     );
                  }

                  val var203: Float = if (var156.element + var168 == 0) 0.0F else (float)var156.element / (var156.element + var168);
                  val var209: Float = if (var187 + var177 == 0) 0.0F else (float)var187 / (var187 + var177);
                  val Scorex: Float = var203 - (if (var187 + var177 == 0) 0.0F else (float)var187 / (var187 + var177));
                  val var215: java.util.List = CollectionsKt.listOf(
                     new Object[]{
                        "Total",
                        "",
                        "",
                        "",
                        Boxing.boxInt(var156.element),
                        Boxing.boxInt(var168),
                        Boxing.boxInt(var177),
                        Boxing.boxInt(var187),
                        Boxing.boxInt(var150),
                        PrecisionMeasurementKt.getFm(var203),
                        PrecisionMeasurementKt.getFm(if (var187 + var177 == 0) 0.0F else (float)var187 / (float)(var187 + var177)),
                        PrecisionMeasurementKt.getFm(var203 - (if (var187 + var177 == 0) 0.0F else (float)var187 / (float)(var187 + var177)))
                     }
                  );
                  var115.writeRow(var215);
                  var125.writeRow(var215);
                  var134.addLine(
                     CollectionsKt.listOf(
                        new Object[]{
                           "Total",
                           "",
                           Boxing.boxInt(var156.element),
                           Boxing.boxInt(var168),
                           Boxing.boxInt(var177),
                           Boxing.boxInt(var187),
                           PrecisionMeasurementKt.getFm(var203),
                           PrecisionMeasurementKt.getFm(var209),
                           PrecisionMeasurementKt.getFm(Scorex)
                        }
                     )
                  );
                  var115.flush();
                  var115.close();
                  var125.flush();
                  var125.close();
                  var134.dump();
                  var134.flush();
                  var134.close();
                  System.out.println(FilesKt.readText$default(var104.getFile(), null, 1, null));
                  var var303: Int = var156.element;
                  var var307: Int = var156.element;
                  var var10003: Int = var156.element;
                  var var10004: Int = var156.element + var187;
                  var var230: Int = Boxing.boxInt(var156.element + var187);
                  var var54: java.lang.Boolean = Boxing.boxBoolean(var230.intValue() > 0);
                  var303 = var303;
                  var307 = var307;
                  var var311: Int = var187;
                  var10003 = var10003;
                  var10004 = var10004;
                  var var222: Int = if (var54) var230 else null;
                  var var10005: java.lang.Float;
                  if (var222 != null) {
                     val var273: java.lang.Float = Boxing.boxFloat((float)var156.element / (float)var222.intValue());
                     var303 = var303;
                     var307 = var307;
                     var311 = var187;
                     var10003 = var10003;
                     var10004 = var10004;
                     var10005 = var273;
                  } else {
                     var10005 = null;
                  }

                  System.out.println("Precision = TP/(TP+FP) = $var303/($var307+$var311) = $var10003/($var10004) = $var10005");
                  var303 = var156.element;
                  var307 = var156.element;
                  var10003 = var156.element;
                  var10004 = var156.element + var168;
                  var230 = Boxing.boxInt(var156.element + var168);
                  var54 = Boxing.boxBoolean(var230.intValue() > 0);
                  var303 = var303;
                  var307 = var307;
                  var311 = var168;
                  var10003 = var10003;
                  var10004 = var10004;
                  var222 = if (var54) var230 else null;
                  if (var222 != null) {
                     val var275: java.lang.Float = Boxing.boxFloat((float)var156.element / (float)var222.intValue());
                     var303 = var303;
                     var307 = var307;
                     var311 = var168;
                     var10003 = var10003;
                     var10004 = var10004;
                     var10005 = var275;
                  } else {
                     var10005 = null;
                  }

                  System.out.println("Recall    = TP/(TP+FN) = $var303/($var307+$var311) = $var10003/($var10004) = $var10005");
                  return new AccuracyValidator.Result(var59, FN, var60, var57, var156.element, var168, var177, var187, var150, var203, var209, Scorex);
               }

               public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                  return (new <anonymous constructor>(this.this$0, this.$result, this.$locator, `$completion`)) as Continuation<Unit>;
               }

               public final Object invoke(CoroutineScope p1, Continuation<? super AccuracyValidator.Result> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }

               private static final Job invokeSuspend$lambda$1$lambda$0(AccuracyValidator this$0, java.util.Set $expectedResults, IResFile file) {
                  return BuildersKt.launch$default(
                     CoroutineScopeKt.CoroutineScope(Dispatchers.getIO() as CoroutineContext),
                     null,
                     null,
                     (
                        new Function2<CoroutineScope, Continuation<? super Unit>, Object>(`this$0`, file, `$expectedResults`, null) {
                           int label;

                           {
                              super(2, `$completionx`);
                              this.this$0 = `$receiver`;
                              this.$file = `$file`;
                              this.$expectedResults = `$expectedResultsx`;
                           }

                           // $VF: Extended synchronized range to monitorexit
                           public final Object invokeSuspend(Object $result) {
                              IntrinsicsKt.getCOROUTINE_SUSPENDED();
                              switch (this.label) {
                                 case 0:
                                    ResultKt.throwOnFailure(`$result`);
                                    val var2: java.util.Set = AccuracyValidator.access$parseFile(this.this$0, this.$file);
                                    val var3: java.util.Set = this.$expectedResults;
                                    val var4: AccuracyValidator = this.this$0;
                                    val it: java.util.Set = var2;
                                    synchronized (this.$expectedResults) {
                                       val `$this$filter$iv`: java.lang.Iterable = it;
                                       val `destination$iv$iv`: java.util.Collection = new ArrayList();

                                       for (Object element$iv$iv : $this$filter$iv) {
                                          if (ScanFilter.getActionOf$default(
                                                var4.getMainConfig().getScanFilter(),
                                                AccuracyValidator.access$getRules$p(var4),
                                                null,
                                                var4.getMainConfig().getScanFilter().get((`element$iv$iv` as ExpectBugAnnotationData).getFile().getPath()),
                                                null,
                                                8,
                                                null
                                             )
                                             != ProcessRule.ScanAction.Skip) {
                                             `destination$iv$iv`.add(`element$iv$iv`);
                                          }
                                       }

                                       var3.addAll(`destination$iv$iv` as java.util.List);
                                    }

                                    return Unit.INSTANCE;
                                 default:
                                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                              }
                           }

                           public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                              return (new <anonymous constructor>(this.this$0, this.$file, this.$expectedResults, `$completion`)) as Continuation<Unit>;
                           }

                           public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                              return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                           }
                        }
                     ) as Function2,
                     3,
                     null
                  );
               }

               private static final Object invokeSuspend$lambda$15(ExpectBugAnnotationData $expect) {
                  return "not exists this type: $`$expect`";
               }

               private static final Object invokeSuspend$lambda$18(ExpectBugAnnotationData $expect) {
                  return "FP: $`$expect`";
               }

               private static final Object invokeSuspend$lambda$20(ExpectBugAnnotationData $expect) {
                  return "FN: $`$expect`";
               }
            }
         ) as Function2,
         `$completion`
      );
   }

   private fun parseFile(file: IResFile): Set<ExpectBugAnnotationData<String>> {
      var res: java.lang.String;
      try {
         res = new java.lang.String(ResourceKt.readAllBytes(file), Charsets.UTF_8);
      } catch (var17: IOException) {
         this.logger.error("read config file $file failed");
         res = null;
      }

      if (res == null) {
         return SetsKt.emptySet();
      } else {
         val text: java.lang.String = res;
         val var10000: Matcher = this.pattern.matcher(res);
         val matcher: Matcher = var10000;
         val var18: java.util.Set = new LinkedHashSet();

         while (matcher.find()) {
            val var20: java.lang.String = matcher.group("escape");
            val escape: Boolean = var20.length() > 0;
            val var21: java.lang.String = matcher.group("name");
            val name: java.lang.String = StringsKt.removeSuffix(StringsKt.removeSurrounding(var21, "`"), "--");
            val startIndex: Int = matcher.start();
            val var8: Triple = this.getLineAndColumn(text, startIndex);
            val start: Int = (var8.component1() as java.lang.Number).intValue();
            val line: Int = (var8.component2() as java.lang.Number).intValue();
            val col: Int = (var8.component3() as java.lang.Number).intValue();
            val var22: java.lang.String = text.substring(start);
            val var23: java.lang.String = StringsKt.substringBefore$default(var22, "\n", null, 2, null).substring(0, startIndex - start);
            if (StringsKt.contains$default(var23, "//", false, 2, null)
               || StringsKt.contains$default(var23, "<!--", false, 2, null)
               || file.getExtension() == "properties") {
               val var10003: IResFile = file.getAbsolute();
               val var10006: Locale = Locale.getDefault();
               val var24: java.lang.String = name.toLowerCase(var10006);
               val var13: ExpectBugAnnotationData = new ExpectBugAnnotationData<>(
                  var10003, line, col, var24, if (escape) ExpectBugAnnotationData.Kind.Escape else ExpectBugAnnotationData.Kind.Expect
               );
               this.logger.trace(AccuracyValidator::parseFile$lambda$7$lambda$6);
               var18.add(var13);
            }
         }

         return var18;
      }
   }

   private fun getLineAndColumn(text: String, index: Int): Triple<Int, Int, Int> {
      var line: Int = 1;
      var col: Int = 0;
      var start: Int = 0;

      for (int i = 0; i < index; i++) {
         if (text.charAt(i) == '\n') {
            line++;
            start = i + 1;
            col = 0;
         } else {
            col++;
         }
      }

      return new Triple(start, line, col);
   }

   @JvmStatic
   fun `logger$lambda$0`(): Unit {
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `_get_str_$lambda$3`(`this$0`: AccuracyValidator, bugAnnotationData: ExpectBugAnnotationData): java.lang.CharSequence {
      return "file: ${`this$0`.mainConfig.tryGetRelativePath(bugAnnotationData.getFile()).getRelativePath()}:${bugAnnotationData.getLine()}:${bugAnnotationData.getColumn()} kind: ${bugAnnotationData.getKind()} a bug: ${bugAnnotationData.getBug()}";
   }

   @JvmStatic
   fun `parseFile$lambda$7$lambda$6`(`$it`: ExpectBugAnnotationData): Any {
      return `$it`;
   }

   public data class Result(TP: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>>,
      FN: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>>,
      TN: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>>,
      FP: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>>,
      allTp: Int,
      allFn: Int,
      allTn: Int,
      allFp: Int,
      allTotal: Int,
      allTpr: Float,
      allFpr: Float,
      score: Float
   ) {
      public final val TP: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>>
      public final val FN: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>>
      public final val TN: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>>
      public final val FP: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>>
      public final val allTp: Int
      public final val allFn: Int
      public final val allTn: Int
      public final val allFp: Int
      public final val allTotal: Int
      public final val allTpr: Float
      public final val allFpr: Float
      public final val score: Float

      init {
         this.TP = TP;
         this.FN = FN;
         this.TN = TN;
         this.FP = FP;
         this.allTp = allTp;
         this.allFn = allFn;
         this.allTn = allTn;
         this.allFp = allFp;
         this.allTotal = allTotal;
         this.allTpr = allTpr;
         this.allFpr = allFpr;
         this.score = score;
      }

      public override fun toString(): String {
         return "TP: ${this.allTp}, FN: ${this.allFn}, TN: ${this.allTn}, FP: ${this.allFp}, allTotal: ${this.allTotal}, TPR:${PrecisionMeasurementKt.getFm(
            this.allTpr
         )}, FPR:${PrecisionMeasurementKt.getFm(this.allFpr)}, Score: ${PrecisionMeasurementKt.getFm(this.score)}";
      }

      public operator fun component1(): Map<RowType, MutableSet<ExpectBugAnnotationData<String>>> {
         return this.TP;
      }

      public operator fun component2(): Map<RowType, MutableSet<ExpectBugAnnotationData<String>>> {
         return this.FN;
      }

      public operator fun component3(): Map<RowType, MutableSet<ExpectBugAnnotationData<String>>> {
         return this.TN;
      }

      public operator fun component4(): Map<RowType, MutableSet<ExpectBugAnnotationData<String>>> {
         return this.FP;
      }

      public operator fun component5(): Int {
         return this.allTp;
      }

      public operator fun component6(): Int {
         return this.allFn;
      }

      public operator fun component7(): Int {
         return this.allTn;
      }

      public operator fun component8(): Int {
         return this.allFp;
      }

      public operator fun component9(): Int {
         return this.allTotal;
      }

      public operator fun component10(): Float {
         return this.allTpr;
      }

      public operator fun component11(): Float {
         return this.allFpr;
      }

      public operator fun component12(): Float {
         return this.score;
      }

      public fun copy(
         TP: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>> = this.TP,
         FN: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>> = this.FN,
         TN: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>> = this.TN,
         FP: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>> = this.FP,
         allTp: Int = this.allTp,
         allFn: Int = this.allFn,
         allTn: Int = this.allTn,
         allFp: Int = this.allFp,
         allTotal: Int = this.allTotal,
         allTpr: Float = this.allTpr,
         allFpr: Float = this.allFpr,
         score: Float = this.score
      ): cn.sast.framework.validator.AccuracyValidator.Result {
         return new AccuracyValidator.Result(TP, FN, TN, FP, allTp, allFn, allTn, allFp, allTotal, allTpr, allFpr, score);
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    (
                                             (
                                                      (
                                                               (
                                                                        (
                                                                                 ((this.TP.hashCode() * 31 + this.FN.hashCode()) * 31 + this.TN.hashCode())
                                                                                       * 31
                                                                                    + this.FP.hashCode()
                                                                              )
                                                                              * 31
                                                                           + Integer.hashCode(this.allTp)
                                                                     )
                                                                     * 31
                                                                  + Integer.hashCode(this.allFn)
                                                            )
                                                            * 31
                                                         + Integer.hashCode(this.allTn)
                                                   )
                                                   * 31
                                                + Integer.hashCode(this.allFp)
                                          )
                                          * 31
                                       + Integer.hashCode(this.allTotal)
                                 )
                                 * 31
                              + java.lang.Float.hashCode(this.allTpr)
                        )
                        * 31
                     + java.lang.Float.hashCode(this.allFpr)
               )
               * 31
            + java.lang.Float.hashCode(this.score);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is AccuracyValidator.Result) {
            return false;
         } else {
            val var2: AccuracyValidator.Result = other as AccuracyValidator.Result;
            if (!(this.TP == (other as AccuracyValidator.Result).TP)) {
               return false;
            } else if (!(this.FN == var2.FN)) {
               return false;
            } else if (!(this.TN == var2.TN)) {
               return false;
            } else if (!(this.FP == var2.FP)) {
               return false;
            } else if (this.allTp != var2.allTp) {
               return false;
            } else if (this.allFn != var2.allFn) {
               return false;
            } else if (this.allTn != var2.allTn) {
               return false;
            } else if (this.allFp != var2.allFp) {
               return false;
            } else if (this.allTotal != var2.allTotal) {
               return false;
            } else if (java.lang.Float.compare(this.allTpr, var2.allTpr) != 0) {
               return false;
            } else if (java.lang.Float.compare(this.allFpr, var2.allFpr) != 0) {
               return false;
            } else {
               return java.lang.Float.compare(this.score, var2.score) == 0;
            }
         }
      }
   }
}
