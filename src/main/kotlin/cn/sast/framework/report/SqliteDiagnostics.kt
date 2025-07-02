package cn.sast.framework.report

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.TransactionWithoutReturn
import cn.sast.api.config.CheckerInfoGenResult
import cn.sast.api.config.ExtSettings
import cn.sast.api.config.MainConfig
import cn.sast.api.config.SaConfig
import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.CheckType2StringKind
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.api.report.Report
import cn.sast.api.util.PhaseIntervalTimerKt
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.Resource
import cn.sast.common.ResourceImplKt
import cn.sast.common.ResourceKt
import cn.sast.framework.metrics.MetricsMonitor
import cn.sast.framework.metrics.MetricsMonitorKt
import cn.sast.framework.report.FileX.ID
import cn.sast.framework.report.ReportConsumer.MetaData
import cn.sast.framework.report.metadata.AnalysisMetadata
import cn.sast.framework.report.metadata.Analyzer
import cn.sast.framework.report.metadata.AnalyzerStatistics
import cn.sast.framework.report.metadata.Tool
import cn.sast.framework.report.sqldelight.AnalyzerResultFile
import cn.sast.framework.report.sqldelight.ControlFlow
import cn.sast.framework.report.sqldelight.ControlFlowPath
import cn.sast.framework.report.sqldelight.Database
import cn.sast.framework.report.sqldelight.Diagnostic
import cn.sast.framework.report.sqldelight.File
import cn.sast.framework.report.sqldelight.Note
import cn.sast.framework.report.sqldelight.NotePath
import cn.sast.framework.report.sqldelight.Rule
import cn.sast.framework.result.OutputType
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.cache.analysis.SootLineToMethodMapFactory
import com.feysh.corax.cache.analysis.SootMethodAndRange
import com.feysh.corax.commons.NullableLateinit
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.github.javaparser.ast.body.BodyDeclaration
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Locale
import java.util.Optional
import java.util.Map.Entry
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.optionals.OptionalsKt
import kotlin.time.Duration
import kotlin.time.DurationKt
import kotlin.time.DurationUnit
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.ExecutorsKt
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import mu.KLogger
import org.utbot.common.StringUtilKt

@SourceDebugExtension(["SMAP\nSqliteDiagnostics.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SqliteDiagnostics.kt\ncn/sast/framework/report/SqliteDiagnostics\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Region.kt\ncom/feysh/corax/config/api/report/Region\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,813:1\n381#2,7:814\n381#2,7:821\n1#3:828\n1#3:830\n1#3:834\n59#4:829\n57#4:831\n60#4:832\n59#4:833\n57#4,2:835\n1279#5,2:837\n1293#5,4:839\n1863#5,2:843\n1279#5,2:845\n1293#5,4:847\n1863#5,2:851\n1863#5:853\n1863#5,2:854\n1864#5:856\n1863#5,2:857\n1863#5,2:859\n1628#5,3:861\n1863#5,2:864\n*S KotlinDebug\n*F\n+ 1 SqliteDiagnostics.kt\ncn/sast/framework/report/SqliteDiagnostics\n*L\n365#1:814,7\n379#1:821,7\n465#1:830\n474#1:834\n465#1:829\n465#1:831\n474#1:832\n474#1:833\n474#1:835,2\n573#1:837,2\n573#1:839,4\n593#1:843,2\n600#1:845,2\n600#1:847,4\n624#1:851,2\n708#1:853\n709#1:854,2\n708#1:856\n755#1:857,2\n762#1:859,2\n770#1:861,3\n738#1:864,2\n*E\n"])
public open class SqliteDiagnostics(mainConfig: MainConfig,
      info: SootInfoCache?,
      outputDir: IResDirectory,
      monitor: MetricsMonitor?,
      type: OutputType = OutputType.SQLITE
   ) : ReportConsumer(type, outputDir),
   IFileReportConsumer,
   IMetadataVisitor {
   public final val mainConfig: MainConfig
   public final val info: SootInfoCache?
   public final val monitor: MetricsMonitor?
   private final var _sdb: NullableLateinit<SQLiteDB>

   private final var sqLiteDB: SQLiteDB
      private final get() {
         return this.sqLiteDB$delegate.getValue(this, $$delegatedProperties[0]) as SQLiteDB;
      }

      private final set(<set-?>) {
         this.sqLiteDB$delegate.setValue(this, $$delegatedProperties[0], var1);
      }


   private final val database: Database
      private final get() {
         return this.getSqLiteDB().getDatabase();
      }


   private final var sqliteReportDb: IResFile

   public final val metadata: MetaData
      public final get() {
         return new ReportConsumer.MetaData("CoraxJava sqlite report", "1.0", "CoraxJava");
      }


   private final val writeDispatcher: ExecutorCoroutineDispatcher
   private final var ruleAndRuleMapping: RuleAndRuleMapping?
   private final val fileIdMap: MutableMap<IResFile, FileID>
   private final val fileCache: LoadingCache<IResFile, Optional<FileX>>

   public open val sourceEncoding: Charset
      public open get() {
         return Charsets.UTF_8;
      }


   private final val id: String
      private final get() {
         return CheckType2StringKind.Companion.getCheckType2StringKind().getConvert().invoke(`$this$id`) as java.lang.String;
      }


   private final val associateChecker: Rule?
      private final get() {
         if (this.ruleAndRuleMapping != null) {
            val var10000: java.util.Map = this.ruleAndRuleMapping.getId2checkerMap();
            if (var10000 != null) {
               return var10000.get(`$this$associateChecker`.getCheck_name()) as Rule;
            }
         }

         return null;
      }


   private final val noteHashIdAutoIncrement: MutableMap<String, Long>
   private final val ctrlFlowHashIdAutoIncrement: MutableMap<String, Long>

   init {
      this.mainConfig = mainConfig;
      this.info = info;
      this.monitor = monitor;
      this._sdb = new NullableLateinit<>("SQLiteDB is not initialized yet");
      this.sqLiteDB$delegate = this._sdb;
      this.sqliteReportDb = this.mainConfig.getSqlite_report_db();
      val var10001: ExecutorService = Executors.newSingleThreadExecutor();
      this.writeDispatcher = ExecutorsKt.from(var10001);
      val var6: java.util.Map = Collections.synchronizedMap(new LinkedHashMap());
      this.fileIdMap = var6;
      this.fileCache = Caffeine.newBuilder().initialCapacity(1000).softValues().build(new CacheLoader(this) {
         {
            this.this$0 = `$receiver`;
         }

         public final Optional<FileX> load(IResFile absFile) {
            var var2: Optional;
            try {
               val var10000: SqliteDiagnostics = this.this$0;
               var2 = Optional.ofNullable(SqliteDiagnostics.access$createFile(var10000, absFile));
            } catch (var4: Exception) {
               SqliteDiagnostics.access$getLogger$cp().warn(<unrepresentable>::load$lambda$0);
               SqliteDiagnostics.access$getLogger$cp().debug(var4, <unrepresentable>::load$lambda$1);
               var2 = Optional.empty();
            }

            return var2;
         }

         private static final Object load$lambda$0(IResFile $absFile, Exception $e) {
            return "Failed to read file: $`$absFile`, e: ${`$e`.getMessage()}";
         }

         private static final Object load$lambda$1(IResFile $absFile, Exception $e) {
            return "Failed to read file: $`$absFile`, e: ${`$e`.getMessage()}";
         }
      });
      this.noteHashIdAutoIncrement = new HashMap<>(1000);
      this.ctrlFlowHashIdAutoIncrement = new HashMap<>(1000);
   }

   public override suspend fun init() {
      return init$suspendImpl(this, `$completion`);
   }

   public fun open(journalMode: String = ExtSettings.INSTANCE.getSqliteJournalMode()) {
      this.sqliteReportDb.mkdirs();
      this.setSqLiteDB(Companion.openDataBase(this.sqliteReportDb.getPathString(), journalMode));
      this.getSqLiteDB().createSchema();
   }

   private fun createRuleAndRuleMapping() {
      var ruleSortYaml: IResFile;
      var var5: CheckerInfoGenResult;
      label23: {
         ruleSortYaml = this.mainConfig.getRule_sort_yaml();
         val var10000: Lazy = this.mainConfig.getCheckerInfo();
         if (var10000 != null) {
            var5 = var10000.getValue() as CheckerInfoGenResult;
            if (var5 != null) {
               break label23;
            }
         }

         val `$this$createRuleAndRuleMapping_u24lambda_u241`: SqliteDiagnostics = this;
         logger.warn(SqliteDiagnostics::createRuleAndRuleMapping$lambda$1$lambda$0);
         var5 = null;
      }

      if (ruleSortYaml != null && var5 != null) {
         this.ruleAndRuleMapping = new RuleAndRuleMapping(var5, ruleSortYaml.getPath());
         if (this.ruleAndRuleMapping != null) {
            this.ruleAndRuleMapping.insert(this.getDatabase());
         }
      } else {
         logger.warn(SqliteDiagnostics::createRuleAndRuleMapping$lambda$2);
      }
   }

   private fun ExecutableQuery<*>.verify(name: String) {
      val it: java.util.List = `$this$verify`.executeAsList();
      if (!it.isEmpty()) {
         logger.error(SqliteDiagnostics::verify$lambda$4$lambda$3);
      }
   }

   public fun verify() {
      this.verify(this.getDatabase().getRuleMappingQueries().verify_rule_name(), "RuleMapping.rule_name");
      this.verify(this.getDatabase().getDiagnosticQueries().verify_rule_name(), "diagnostic.rule_name");
      this.verify(this.getDatabase().getDiagnosticQueries().verify_file(), "diagnostic.__file_id");
      this.verify(this.getDatabase().getDiagnosticQueries().verify_note_path(), "diagnostic.__note_array_hash_id");
      this.verify(this.getDatabase().getDiagnosticQueries().verify_control_flow_path(), "diagnostic.__control_flow_array_hash_id");
      this.verify(this.getDatabase().getDiagnosticQueries().verify_macro(), "diagnostic.__macro_note_set_hash_id");
      this.verify(this.getDatabase().getNotePathQueries().verify_note(), "NotePath.__note_id");
      this.verify(this.getDatabase().getControlFlowPathQueries().verify_control_flow(), "ControlFlowPath.__control_flow_id");
      this.verify(this.getDatabase().getMacroExpansionQueries().verify_note(), "MacroExpansion.__macro_note_id");
      this.verify(this.getDatabase().getControlFlowQueries().verify_file(), "ControlFlow.__file_id");
      this.verify(this.getDatabase().getNoteQueries().verify_file(), "Note.__file_id");
      this.verify(this.getDatabase().getAnalyzeResultFileQueries().verify_file(), "AnalyzeResultFile.__file_id");
      this.verify(this.getDatabase().getAbsoluteFilePathQueries().verify_absolute_file_path(), "AbsoluteFilePath.__file_id");
   }

   public override fun close() {
      this.verify();
      this.getSqLiteDB().close();
      this._sdb.uninitialized();
      this.noteHashIdAutoIncrement.clear();
      this.ctrlFlowHashIdAutoIncrement.clear();
      this.fileCache.cleanUp();
      this.fileIdMap.clear();
   }

   public override suspend fun flush(reports: List<Report>, filename: String, locator: IProjectFileLocator) {
      return flush$suspendImpl(this, reports, filename, locator, `$completion`);
   }

   public override fun visit(analysisMetadata: AnalysisMetadata) {
      if (this.monitor != null) {
         app.cash.sqldelight.Transacter.DefaultImpls.transaction$default(this.getDatabase(), false, SqliteDiagnostics::visit$lambda$7, 1, null);
      }
   }

   private suspend fun serializeReportsToDb(reports: List<Report>, locator: IProjectFileLocator, filename: String) {
      val var10000: Any = BuildersKt.withContext(
         this.writeDispatcher as CoroutineContext,
         (
            new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, reports, locator, null) {
               int label;

               {
                  super(2, `$completionx`);
                  this.this$0 = `$receiver`;
                  this.$reports = `$reports`;
                  this.$locator = `$locator`;
               }

               public final Object invokeSuspend(Object $result) {
                  IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        app.cash.sqldelight.Transacter.DefaultImpls.transaction$default(
                           SqliteDiagnostics.access$getDatabase(this.this$0), false, <unrepresentable>::invokeSuspend$lambda$0, 1, null
                        );
                        return Unit.INSTANCE;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }
               }

               public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                  return (new <anonymous constructor>(this.this$0, this.$reports, this.$locator, `$completion`)) as Continuation<Unit>;
               }

               public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }

               private static final Unit invokeSuspend$lambda$0(
                  java.util.List $reports, SqliteDiagnostics this$0, IProjectFileLocator $locator, TransactionWithoutReturn $this$transaction
               ) {
                  for (Report report : $reports) {
                     val var6: ValueWithId = SqliteDiagnostics.access$createDiagnostic(`this$0`, `$locator`, report);
                  }

                  return Unit.INSTANCE;
               }
            }
         ) as Function2,
         `$completion`
      );
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   private fun createFileCached(locator: IProjectFileLocator, file: IBugResInfo): FileID? {
      var var10000: IResFile = locator.get(file, EmptyWrapperFileGenerator.INSTANCE);
      if (var10000 != null) {
         var10000 = var10000.getAbsolute();
         if (var10000 != null) {
            var10000 = var10000.getNormalize();
            if (var10000 != null) {
               val `$this$getOrPut$iv`: java.util.Map = this.fileIdMap;
               val `value$iv`: Any = this.fileIdMap.get(var10000);
               var var14: Any;
               if (`value$iv` == null) {
                  var14 = this.fileCache.get(var10000);
                  var14 = OptionalsKt.getOrNull(var14 as Optional) as FileX;
                  if (var14 == null) {
                     return null;
                  }

                  val `answer$iv`: Any = new FileID(var14.insert(this.getDatabase()).getId(), var14.getAssociateAbsFile());
                  `$this$getOrPut$iv`.put(var10000, `answer$iv`);
                  var14 = (FileX)`answer$iv`;
               } else {
                  var14 = (FileX)`value$iv`;
               }

               return var14 as FileID;
            }
         }
      }

      return null;
   }

   private fun createFileXCached(locator: IProjectFileLocator, file: IBugResInfo): ID? {
      var var10000: IResFile = locator.get(file, EmptyWrapperFileGenerator.INSTANCE);
      if (var10000 != null) {
         var10000 = var10000.getAbsolute();
         if (var10000 != null) {
            var10000 = var10000.getNormalize();
            if (var10000 != null) {
               return this.createFileXCachedFromAbsFile(var10000);
            }
         }
      }

      return null;
   }

   private fun createFileXCachedFromAbsFile(absFile: IResFile): ID? {
      var var10000: FileX = (FileX)this.fileCache.get(absFile);
      var10000 = OptionalsKt.getOrNull(var10000 as Optional) as FileX;
      if (var10000 == null) {
         return null;
      } else {
         val `$this$getOrPut$iv`: java.util.Map = this.fileIdMap;
         val `value$iv`: Any = this.fileIdMap.get(absFile);
         if (`value$iv` == null) {
            val `answer$iv`: Any = new FileID(var10000.insert(this.getDatabase()).getId(), var10000.getAssociateAbsFile());
            `$this$getOrPut$iv`.put(absFile, `answer$iv`);
            var10000 = (FileX)`answer$iv`;
         } else {
            var10000 = (FileX)`value$iv`;
         }

         return var10000.withId((var10000 as FileID).getId());
      }
   }

   public fun createFileXCachedFromFile(file: IResFile): ID? {
      return this.createFileXCachedFromAbsFile(file.getAbsolute().getNormalize());
   }

   private fun createFile(absFile: IResFile): FileX {
      val bytesContent: ByteArray = ResourceKt.readAllBytes(absFile);
      val hash: java.lang.String = ResourceImplKt.calculate(bytesContent, "sha256");
      val encoding: Charset = this.getSourceEncoding(absFile);
      val lines: java.util.List = StringsKt.lines(new java.lang.String(bytesContent, encoding));
      val relativePath: MainConfig.RelativePath = this.mainConfig.tryGetRelativePathFromAbsolutePath(Resource.INSTANCE.getOriginFileFromExpandAbsPath(absFile));
      val var10004: java.lang.String = StringsKt.removePrefix(relativePath.getRelativePath(), "/");
      val var10005: Long = lines.size();
      var var10006: java.lang.String = encoding.name();
      val var11: Locale = Locale.getDefault();
      var10006 = var10006.toLowerCase(var11);
      return new FileX(new File(-1L, hash, var10004, var10005, var10006, bytesContent.length, bytesContent), relativePath, absFile, lines);
   }

   private fun FileX.lineContent(line: Int): String? {
      var var10000: java.lang.String = CollectionsKt.getOrNull(`$this$lineContent`.getLines(), line - 1) as java.lang.String;
      if (var10000 != null) {
         if (var10000.length() > 384) {
            var10000 = var10000.substring(0, 384);
            return var10000;
         }

         var10000 = var10000;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private fun createRegion(locator: IProjectFileLocator, res: IBugResInfo, region: Region): ValueWithId<cn.sast.framework.report.sqldelight.Region>? {
      val var10000: FileID = this.createFileCached(locator, res);
      if (var10000 == null) {
         return null;
      } else {
         val it: cn.sast.framework.report.sqldelight.Region = new cn.sast.framework.report.sqldelight.Region(
            0L, var10000.getId(), region.startLine, (long)region.startColumn, (long)region.getEndLine(), (long)region.getEndColumn()
         );
         this.getDatabase().getRegionQueries().insert(it.get__file_id(), it.getStart_line(), it.getStart_column(), it.getEnd_line(), it.getEnd_column());
         return new ValueWithId<>(
            (CollectionsKt.first(
                  this.getDatabase()
                     .getRegionQueries()
                     .id(it.get__file_id(), it.getStart_line(), it.getStart_column(), it.getEnd_line(), it.getEnd_column())
                     .executeAsList()
               ) as java.lang.Number)
               .longValue(),
            it
         );
      }
   }

   private fun getFuncRange(classInfo: IBugResInfo, line: Int): Region? {
      if (classInfo is ClassResInfo) {
         val var10000: BodyDeclaration = if (this.info != null) this.info.getMemberAtLine((classInfo as ClassResInfo).getSc(), line) else null;
         if (var10000 != null) {
            val var17: Region.Companion = Region.Companion;
            val var10001: Optional = var10000.getRange();
            val range: Region = var17.invoke_Optional_Range(var10001);
            if (range != null) {
               return range;
            }
         }

         val var15: SootMethodAndRange = SootLineToMethodMapFactory.getSootMethodAtLine$default(
            SootLineToMethodMapFactory.INSTANCE, (classInfo as ClassResInfo).getSc(), line, false, 4, null
         );
         if (var15 != null) {
            val var16: Pair = var15.getRange();
            val `this_$iv`: Region = new Region(
               (var16.getFirst() as java.lang.Number).intValue() - 1, 0, (var16.getSecond() as java.lang.Number).intValue() + 1, 0
            );
            return if (`this_$iv`.startLine >= 0) `this_$iv` else null;
         }
      }

      return null;
   }

   private fun createNote(locator: IProjectFileLocator, event: BugPathEvent): ValueWithId<Note>? {
      val var10000: FileID = this.createFileCached(locator, event.getClassname());
      if (var10000 == null) {
         return null;
      } else {
         val `this_$iv`: Region = event.getRegion();
         val funcRange: Region = if (`this_$iv`.startColumn >= 0 && `this_$iv`.getEndColumn() >= 0)
            (if (`this_$iv`.startLine >= 0) `this_$iv` else null)
            else
            null;
         val noticesRegion: ValueWithId = if (funcRange != null) this.createRegion(locator, event.getClassname(), funcRange) else null;
         val var24: Region = this.getFuncRange(event.getClassname(), event.getRegion().startLine);
         val var25: ValueWithId = if (var24 != null) this.createRegion(locator, event.getClassname(), var24) else null;
         val var26: Note = new Note;
         val var10005: Long = var10000.getId();
         val var10006: java.lang.String = var10000.getFileAbsPath();
         val var10007: Long = event.getRegion().startLine;
         val var10008: java.lang.Long = (long)event.getRegion().startColumn;
         var var10009: java.lang.String = event.getMessage().get(Language.EN);
         if (var10009 == null) {
            var10009 = "";
         }

         var var10010: java.lang.String = event.getMessage().get(Language.ZH);
         if (var10010 == null) {
            var10010 = "";
         }

         var26./* $VF: Unable to resugar constructor */<init>(
            0L,
            "event",
            "Below",
            var10005,
            var10006,
            var10007,
            var10008,
            var10009,
            var10010,
            if (noticesRegion != null) noticesRegion.getId() else null,
            if (var25 != null) var25.getId() else null
         );
         this.getDatabase()
            .getNoteQueries()
            .insert(
               var26.getKind(),
               var26.getDisplay_hint(),
               var26.get__file_id(),
               var26.get_file_abs_path(),
               var26.getLine(),
               var26.getColumn(),
               var26.getMessage_en(),
               var26.getMessage_zh(),
               var26.get__notices_region_id(),
               var26.get__func_region_id()
            );
         return new ValueWithId<>(
            (CollectionsKt.first(
                  this.getDatabase()
                     .getNoteQueries()
                     .id(
                        var26.getKind(),
                        var26.getDisplay_hint(),
                        var26.get__file_id(),
                        var26.get_file_abs_path(),
                        var26.getLine(),
                        var26.getColumn(),
                        var26.getMessage_en(),
                        var26.getMessage_zh(),
                        var26.get__notices_region_id(),
                        var26.get__func_region_id()
                     )
                     .executeAsList()
               ) as java.lang.Number)
               .longValue(),
            var26
         );
      }
   }

   private fun createControlFlow(locator: IProjectFileLocator, event: BugPathEvent): ValueWithId<ControlFlow>? {
      return null;
   }

   private fun createControlFlowPath(locator: IProjectFileLocator, path: MutableList<BugPathEvent>): Long? {
      val arrayHashIdG: java.lang.Iterable = path;
      val `result$iv`: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(path, 10)), 16));

      for (Object element$iv$iv : $this$associateWith$iv) {
         `result$iv`.put(`$this$forEach$iv`, this.createControlFlow(locator, `$this$forEach$iv` as BugPathEvent));
      }

      val path2controlFlow: java.util.Map = `result$iv`;
      val var19: SqliteDiagnostics.ArrayHashIdGenerator = new SqliteDiagnostics.ArrayHashIdGenerator(this.ctrlFlowHashIdAutoIncrement);
      var var20: Long = 0L;
      val array: java.util.List = new ArrayList();
      val var21: java.util.Iterator = path2controlFlow.entrySet().iterator();

      while (var21.hasNext()) {
         val var23: ValueWithId = (var21.next() as Entry).getValue() as ValueWithId;
         if (var23 != null) {
            val var25: ControlFlowPath = new ControlFlowPath(-1L, var20++, var23.getId());
            array.add(var25);
            val var28: java.util.List = var19.getArray();
            var28.add(java.lang.String.valueOf(var25.get__control_flow_id()));
            var28.add(java.lang.String.valueOf(var25.getControl_flow_sequence()));
         }
      }

      val var31: java.lang.Long = var19.getArrayId();
      if (var31 == null) {
         return null;
      } else {
         val var22: Long = var31;

         val var24: java.lang.Iterable;
         for (Object element$iv : var24) {
            this.getDatabase().getControlFlowPathQueries().insert(ControlFlowPath.copy$default(`element$iv` as ControlFlowPath, var22, 0L, 0L, 6, null));
         }

         return var22;
      }
   }

   private fun createNotePath(locator: IProjectFileLocator, pathEvents: MutableList<BugPathEvent>): Long? {
      val arrayHashIdG: java.lang.Iterable = pathEvents;
      val `result$iv`: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(pathEvents, 10)), 16));

      for (Object element$iv$iv : $this$associateWith$iv) {
         `result$iv`.put(`$this$forEach$iv`, this.createNote(locator, `$this$forEach$iv` as BugPathEvent));
      }

      val event2note: java.util.Map = `result$iv`;
      val var19: SqliteDiagnostics.ArrayHashIdGenerator = new SqliteDiagnostics.ArrayHashIdGenerator(this.noteHashIdAutoIncrement);
      var var20: Long = 0L;
      val array: java.util.List = new ArrayList();

      for (Entry var23 : event2note.entrySet()) {
         val var24: BugPathEvent = var23.getKey() as BugPathEvent;
         val var26: ValueWithId = var23.getValue() as ValueWithId;
         if (var26 != null) {
            val `element$iv`: Long = var20++;
            val var10004: Int = var24.getStackDepth();
            val var28: NotePath = new NotePath(-1L, `element$iv`, if (var10004 != null) (long)var10004.intValue() else null, 0L, var26.getId());
            array.add(var28);
            val var30: java.util.List = var19.getArray();
            var30.add(java.lang.String.valueOf(var28.getNote_sequence()));
            var30.add(java.lang.String.valueOf(var28.getNote_stack_depth()));
            var30.add(java.lang.String.valueOf(var28.getNote_is_key_event()));
            var30.add(java.lang.String.valueOf(var28.get__note_id()));
         }
      }

      val var33: java.lang.Long = var19.getArrayId();
      if (var33 == null) {
         return null;
      } else {
         val var22: Long = var33;

         val var25: java.lang.Iterable;
         for (Object element$iv : var25) {
            this.getDatabase().getNotePathQueries().insert(NotePath.copy$default(var31 as NotePath, var22, 0L, null, null, 0L, 30, null));
         }

         return var22;
      }
   }

   private fun createDiagnostic(locator: IProjectFileLocator, report: Report): ValueWithId<Diagnostic>? {
      val var10000: FileX.ID = this.createFileXCached(locator, report.getBugResFile());
      if (var10000 == null) {
         return null;
      } else if (report.getPathEvents().isEmpty()) {
         logger.error(SqliteDiagnostics::createDiagnostic$lambda$24);
         return null;
      } else {
         val lastPathEvents: BugPathEvent = CollectionsKt.last(report.getPathEvents()) as BugPathEvent;
         val var15: java.lang.Long = this.createNotePath(locator, report.getPathEvents());
         if (var15 != null) {
            val noteArrayHashId: Long = var15;
            val controlFlowArrayHashId: java.lang.Long = this.createControlFlowPath(locator, report.getNotes());
            val line: Int = lastPathEvents.getRegion().startLine;
            val var16: Diagnostic = new Diagnostic;
            val var10003: java.lang.String = report.getCheck_name();
            val var10004: Rule = this.getAssociateChecker(report);
            val var17: java.lang.String = if (var10004 != null) var10004.getShort_description_zh() else null;
            val var10005: java.lang.Long = var10000.getId();
            val var10006: java.lang.String = var10000.getFile().getFileAbsPath();
            val var10007: Long = line;
            val var10008: Long = lastPathEvents.getRegion().startColumn;
            var var10009: java.lang.String = report.getMessage().get(Language.EN);
            if (var10009 == null) {
               var10009 = "";
            }

            var var10010: java.lang.String = report.getMessage().get(Language.ZH);
            if (var10010 == null) {
               var10010 = "";
            }

            var var10011: java.lang.String = report.getSeverity();
            if (var10011 == null) {
               val var18: Rule = this.getAssociateChecker(report);
               var10011 = if (var18 != null) var18.getSeverity() else null;
               if (var10011 == null) {
                  var10011 = "None";
               }
            }

            var16./* $VF: Unable to resugar constructor */<init>(
               0L,
               var10003,
               var17,
               var10005,
               var10006,
               var10007,
               var10008,
               var10009,
               var10010,
               var10011,
               null,
               null,
               null,
               null,
               null,
               this.lineContent(var10000.getFile(), line),
               noteArrayHashId,
               controlFlowArrayHashId,
               null
            );
            this.getDatabase()
               .getDiagnosticQueries()
               .insert(
                  var16.getRule_name(),
                  var16.get_rule_short_description_zh(),
                  var16.get__file_id(),
                  var16.get_file_abs_path(),
                  var16.get_line(),
                  var16.get_column(),
                  var16.get_message_en(),
                  var16.get_message_zh(),
                  var16.getSeverity(),
                  var16.getPrecision(),
                  var16.getLikelihood(),
                  var16.getImpact(),
                  var16.getTechnique(),
                  var16.getAnalysis_scope(),
                  var16.getLine_content(),
                  var16.get__note_array_hash_id(),
                  var16.get__control_flow_array_hash_id(),
                  var16.get__macro_note_set_hash_id()
               );
            return new ValueWithId<>(
               (CollectionsKt.first(
                     this.getDatabase()
                        .getDiagnosticQueries()
                        .id(
                           var16.getRule_name(),
                           var16.get_rule_short_description_zh(),
                           var16.get__file_id(),
                           var16.get_file_abs_path(),
                           var16.getSeverity(),
                           var16.getPrecision(),
                           var16.getLikelihood(),
                           var16.getImpact(),
                           var16.getTechnique(),
                           var16.getAnalysis_scope(),
                           var16.getLine_content(),
                           var16.get__note_array_hash_id(),
                           var16.get__control_flow_array_hash_id(),
                           var16.get__macro_note_set_hash_id()
                        )
                        .executeAsList()
                  ) as java.lang.Number)
                  .longValue(),
               var16
            );
         } else {
            val `$this$createDiagnostic_u24lambda_u2426`: SqliteDiagnostics = this;
            logger.error(SqliteDiagnostics::createDiagnostic$lambda$26$lambda$25);
            return null;
         }
      }
   }

   private fun getAnalyzerStatisticsSet(tools: List<Tool>): MutableSet<AnalyzerStatistics> {
      val analyzerStatisticsSet: java.util.Set = new LinkedHashSet();

      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         val `$this$forEach$ivx`: java.lang.Iterable;
         for (Object element$ivx : $this$forEach$ivx) {
            analyzerStatisticsSet.add((`element$ivx` as Analyzer).getAnalyzerStatistics());
         }
      }

      return analyzerStatisticsSet;
   }

   private fun createAnalyzerResultFile(file: IResFile, fileName: String? = null): AnalyzerResultFile? {
      val absFile: IResFile = file.getAbsolute().getNormalize();
      val var10000: FileX.ID = this.createFileXCachedFromAbsFile(absFile);
      if (var10000 == null) {
         return null;
      } else {
         val var8: AnalyzerResultFile = new AnalyzerResultFile;
         var var10002: java.lang.String = fileName;
         if (fileName == null) {
            var10002 = StringsKt.removePrefix(this.mainConfig.tryGetRelativePathFromAbsolutePath(absFile).getRelativePath(), "/");
         }

         var8./* $VF: Unable to resugar constructor */<init>(var10002, absFile.getPathString(), var10000.getId());
         this.getDatabase().getAnalyzeResultFileQueries().insert(var8);
         return var8;
      }
   }

   public fun writeAnalyzerResultFiles() {
      app.cash.sqldelight.Transacter.DefaultImpls.transaction$default(this.getDatabase(), false, SqliteDiagnostics::writeAnalyzerResultFiles$lambda$32, 1, null);
   }

   private fun createAnalyzerStatistics(data: AnalysisMetadata, monitor: MetricsMonitor): cn.sast.framework.report.sqldelight.AnalyzerStatistics {
      val tools: java.util.List = data.getTools();
      val analyzerStatisticsSet: java.util.Set = this.getAnalyzerStatisticsSet(tools);
      var failed: Int = 0;
      var successful: Int = 0;
      val failedSources: LinkedHashSet = new LinkedHashSet();
      val successfulSources: LinkedHashSet = new LinkedHashSet();

      val command: java.lang.Iterable;
      for (Object element$iv : command) {
         val projectRoot: AnalyzerStatistics = outputPath as AnalyzerStatistics;
         failed += (outputPath as AnalyzerStatistics).getFailed();
         failedSources.addAll((outputPath as AnalyzerStatistics).getFailedSources());
         successful += projectRoot.getSuccessful();
         successfulSources.addAll(projectRoot.getSuccessfulSources());
      }

      val var31: java.util.List = new ArrayList();

      val var32: java.lang.Iterable;
      for (Object element$iv : var32) {
         var31.addAll((var38 as Tool).getCommand());
      }

      var endTimestamp: Long;
      var elapsed: java.lang.Long;
      var var35: java.lang.String;
      var var37: java.lang.String;
      var var39: java.lang.String;
      var var41: Long;
      var var43: java.util.Set;
      label50: {
         var33 = CollectionsKt.joinToString$default(tools, ",", null, null, 0, null, SqliteDiagnostics::createAnalyzerStatistics$lambda$35, 30, null);
         var35 = jsonFormat.encodeToString((new ArrayListSerializer(StringSerializer.INSTANCE)) as SerializationStrategy, var31);
         var37 = CollectionsKt.joinToString$default(tools, ",", null, null, 0, null, SqliteDiagnostics::createAnalyzerStatistics$lambda$36, 30, null);
         var39 = CollectionsKt.joinToString$default(tools, ",", null, null, 0, null, SqliteDiagnostics::createAnalyzerStatistics$lambda$37, 30, null);
         var41 = monitor.getBeginMillis();
         endTimestamp = System.currentTimeMillis();
         elapsed = MetricsMonitorKt.timeSub(PhaseIntervalTimerKt.currentNanoTime(), monitor.getBeginNanoTime());
         val var10000: SaConfig = this.mainConfig.getSaConfig();
         if (var10000 != null) {
            var43 = var10000.getEnableCheckTypes();
            if (var43 != null) {
               val `$this$mapTo$iv`: java.lang.Iterable = var43;
               val `destination$iv`: java.util.Collection = new LinkedHashSet();

               for (Object item$iv : $this$mapTo$iv) {
                  `destination$iv`.add(this.getId(`item$iv` as CheckType));
               }

               var43 = `destination$iv` as java.util.Set;
               break label50;
            }
         }

         var43 = SetsKt.emptySet();
      }

      val var44: cn.sast.framework.report.sqldelight.AnalyzerStatistics = new cn.sast.framework.report.sqldelight.AnalyzerStatistics;
      var var10004: java.lang.String = this.mainConfig.getVersion();
      if (var10004 == null) {
         var10004 = "None";
      }

      var var10005: java.lang.String;
      var var10007: Long;
      var var10008: java.lang.String;
      label38: {
         var10005 = MetricsMonitorKt.getDateStringFromMillis(var41);
         var10007 = (long)PhaseIntervalTimerKt.nanoTimeInSeconds$default(elapsed, 0, 1, null);
         if (elapsed != null) {
            var10008 = Duration.toString-impl(DurationKt.toDuration(elapsed, DurationUnit.NANOSECONDS));
            if (var10008 != null) {
               break label38;
            }
         }

         var10008 = "";
      }

      var var47: java.util.Collection;
      var var10009: java.lang.String;
      var var10011: Long;
      var var10012: Long;
      var var10013: java.lang.Long;
      var var10014: java.lang.Long;
      var var10015: java.lang.Long;
      var var10016: java.lang.String;
      var var10017: java.lang.String;
      var var10023: java.lang.String;
      label33: {
         var10009 = MetricsMonitorKt.getDateStringFromMillis(endTimestamp);
         var10011 = data.getFileCount();
         var10012 = data.getLineCount();
         var10013 = (long)data.getCodeCoverage().getCovered();
         var10014 = (long)data.getCodeCoverage().getMissed();
         var10015 = (long)data.getNumOfReportDir();
         var10016 = CollectionsKt.joinToString$default(data.getSourcePaths(), ",", null, null, 0, null, null, 62, null);
         var10017 = data.getOsName();
         var10023 = CollectionsKt.joinToString$default(var43, ",", null, null, 0, null, null, 62, null);
         val var10024: Lazy = this.mainConfig.getCheckerInfo();
         if (var10024 != null) {
            val var45: CheckerInfoGenResult = var10024.getValue() as CheckerInfoGenResult;
            if (var45 != null) {
               val var46: LinkedHashSet = var45.getExistsCheckerIds();
               if (var46 != null) {
                  var47 = var46;
                  break label33;
               }
            }
         }

         var47 = CollectionsKt.emptyList();
      }

      var44./* $VF: Unable to resugar constructor */<init>(
         "Corax",
         "",
         var10004,
         var10005,
         var41,
         var10007,
         var10008,
         var10009,
         endTimestamp,
         var10011,
         var10012,
         var10013,
         var10014,
         var10015,
         var10016,
         var10017,
         var35,
         var33,
         var37,
         var39,
         "",
         var10023,
         CollectionsKt.joinToString$default(CollectionsKt.minus(var47, var43), ",", null, null, 0, null, null, 62, null),
         CollectionsKt.joinToString$default(failedSources, ",", null, null, 0, null, null, 62, null),
         (long)failed,
         CollectionsKt.joinToString$default(successfulSources, ",", null, null, 0, null, null, 62, null),
         (long)successful,
         "",
         1L
      );
      this.getDatabase().getAnalyzerStatisticsQueries().insert(var44);
      return var44;
   }

   @JvmStatic
   fun `createRuleAndRuleMapping$lambda$1$lambda$0`(): Any {
      return "The checkerInfo field of mainConfig is null";
   }

   @JvmStatic
   fun `createRuleAndRuleMapping$lambda$2`(): Any {
      return "rule_sort.yaml is not exists";
   }

   @JvmStatic
   fun `verify$lambda$4$lambda$3`(`$name`: java.lang.String, `$it`: java.util.List): Any {
      return "reference of $`$name`: $`$it` is not exists in the parent table";
   }

   @JvmStatic
   fun `flush$lambda$5`(`$e`: Exception): Any {
      return "There are some errors when serialize data to sqlite: ${`$e`.getMessage()}";
   }

   @JvmStatic
   fun `flush$lambda$6`(`$e`: Exception): Any {
      return "There are some errors when serialize data to sqlite: ${`$e`.getMessage()}";
   }

   @JvmStatic
   fun `visit$lambda$7`(`this$0`: SqliteDiagnostics, `$analysisMetadata`: AnalysisMetadata, `$this$transaction`: TransactionWithoutReturn): Unit {
      `this$0`.createAnalyzerStatistics(`$analysisMetadata`, `this$0`.monitor);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `createDiagnostic$lambda$24`(`$report`: Report): Any {
      return "Report.pathEvents is empty! report: $`$report`";
   }

   @JvmStatic
   fun `createDiagnostic$lambda$26$lambda$25`(`$report`: Report): Any {
      return "invalid report: $`$report`";
   }

   @JvmStatic
   fun `writeAnalyzerResultFiles$lambda$32`(`this$0`: SqliteDiagnostics, `$this$transaction`: TransactionWithoutReturn): Unit {
      val out: IResDirectory = `this$0`.mainConfig.getOutput_dir();

      val var12: java.lang.Iterable;
      for (Object element$iv : var12) {
         val path: IResource = (`element$iv` as Pair).component1() as IResource;
         val name: java.lang.String = (`element$iv` as Pair).component2() as java.lang.String;
         val file: IResFile = path.toFile();
         if (file.getExists()) {
            `this$0`.createAnalyzerResultFile(file, name);
         }
      }

      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `createAnalyzerStatistics$lambda$35`(it: Tool): java.lang.CharSequence {
      return it.getWorkingDirectory();
   }

   @JvmStatic
   fun `createAnalyzerStatistics$lambda$36`(it: Tool): java.lang.CharSequence {
      return it.getOutputPath();
   }

   @JvmStatic
   fun `createAnalyzerStatistics$lambda$37`(it: Tool): java.lang.CharSequence {
      return it.getProjectRoot();
   }

   @JvmStatic
   fun `logger$lambda$40`(): Unit {
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun JsonBuilder.`jsonFormat$lambda$41`(): Unit {
      `$this$Json`.setUseArrayPolymorphism(true);
      `$this$Json`.setPrettyPrint(true);
      `$this$Json`.setEncodeDefaults(false);
      return Unit.INSTANCE;
   }

   @SourceDebugExtension(["SMAP\nSqliteDiagnostics.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SqliteDiagnostics.kt\ncn/sast/framework/report/SqliteDiagnostics$ArrayHashIdGenerator\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,813:1\n381#2,7:814\n*S KotlinDebug\n*F\n+ 1 SqliteDiagnostics.kt\ncn/sast/framework/report/SqliteDiagnostics$ArrayHashIdGenerator\n*L\n566#1:814,7\n*E\n"])
   public class ArrayHashIdGenerator(hashIdAutoIncrement: MutableMap<String, Long>) {
      private final val hashIdAutoIncrement: MutableMap<String, Long>
      public final val array: MutableList<String>

      private final val md5: String
         private final get() {
            val var10003: MessageDigest = StringUtilKt.getMd5();
            val var10004: ByteArray = CollectionsKt.joinToString$default(this.array, ",", null, null, 0, null, null, 62, null).getBytes(Charsets.UTF_8);
            return new BigInteger(1, var10003.digest(var10004)).toString(16);
         }


      public final val arrayId: Long?
         public final get() {
            synchronized (this.hashIdAutoIncrement) {
               val var10000: java.lang.Long;
               if (this.array.isEmpty()) {
                  var10000 = null;
               } else {
                  val id: Long = this.hashIdAutoIncrement.size() + 1L;
                  val `$this$getOrPut$iv`: java.util.Map = this.hashIdAutoIncrement;
                  val `key$iv`: Any = this.getMd5();
                  val `value$iv`: Any = `$this$getOrPut$iv`.get(`key$iv`);
                  val var12: Any;
                  if (`value$iv` == null) {
                     val `answer$iv`: Any = id;
                     `$this$getOrPut$iv`.put(`key$iv`, `answer$iv`);
                     var12 = `answer$iv`;
                  } else {
                     var12 = `value$iv`;
                  }

                  var10000 = var12 as java.lang.Long;
               }

               return var10000;
            }
         }


      init {
         this.hashIdAutoIncrement = hashIdAutoIncrement;
         this.array = new ArrayList<>();
      }
   }

   public companion object {
      private final val logger: KLogger
      private final val jsonFormat: Json

      public fun openDataBase(fullPath: String, journalMode: String = ExtSettings.INSTANCE.getSqliteJournalMode()): SQLiteDB {
         return SQLiteDB.Companion.openDataBase(fullPath, journalMode);
      }
   }
}
