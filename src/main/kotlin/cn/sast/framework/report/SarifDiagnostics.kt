package cn.sast.framework.report

import cn.sast.api.config.MainConfig
import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.IBugResInfo
import cn.sast.api.report.Report
import cn.sast.api.report.ReportKt
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.OS
import cn.sast.framework.report.ReportConsumer.MetaData
import cn.sast.framework.report.sarif.ArtifactLocation
import cn.sast.framework.report.sarif.CodeFlow
import cn.sast.framework.report.sarif.FlowLocation
import cn.sast.framework.report.sarif.FlowLocationWrapper
import cn.sast.framework.report.sarif.Location
import cn.sast.framework.report.sarif.Message
import cn.sast.framework.report.sarif.MessageStrings
import cn.sast.framework.report.sarif.PhysicalLocation
import cn.sast.framework.report.sarif.Result
import cn.sast.framework.report.sarif.Rule
import cn.sast.framework.report.sarif.Run
import cn.sast.framework.report.sarif.SarifLog
import cn.sast.framework.report.sarif.ThreadFlow
import cn.sast.framework.report.sarif.Tool
import cn.sast.framework.report.sarif.ToolComponent
import cn.sast.framework.report.sarif.TranslationToolComponent
import cn.sast.framework.result.OutputType
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.Locale
import java.util.Map.Entry
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger

public open class SarifDiagnostics(outputDir: IResDirectory, type: OutputType = OutputType.SARIF) : ReportConsumer(type, outputDir), IFileReportConsumer {
   public open val metadata: MetaData
      public open get() {
         return new ReportConsumer.MetaData("corax", "1.0", "CoraxJava");
      }


   public open fun getSarifDiagnosticsImpl(metadata: MetaData, locator: IProjectFileLocator): cn.sast.framework.report.SarifDiagnostics.SarifDiagnosticsImpl {
      return new SarifDiagnostics.SarifDiagnosticsImpl(this, metadata, locator);
   }

   public override suspend fun flush(reports: List<Report>, filename: String, locator: IProjectFileLocator) {
      return flush$suspendImpl(this, reports, filename, locator, `$completion`);
   }

   private fun getReportFileName(fileName: String): String {
      val var2: java.lang.String = this.getMetadata().getAnalyzerName();
      val var10001: Locale = Locale.getDefault();
      val var3: java.lang.String = var2.toLowerCase(var10001);
      return "$fileName_$var3.sarif";
   }

   public override fun close() {
   }

   @JvmStatic
   fun `flush$lambda$0`(`$fullPath`: IResource): Any {
      return "Create/modify plist file: '$`$fullPath`'";
   }

   @JvmStatic
   fun `logger$lambda$1`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }

   public data class MultiLangRule(id: String, name: String, messageStrings: Map<Language, MessageStrings>) {
      public final val id: String
      public final val name: String
      public final val messageStrings: Map<Language, MessageStrings>

      init {
         this.id = id;
         this.name = name;
         this.messageStrings = messageStrings;
      }

      public operator fun component1(): String {
         return this.id;
      }

      public operator fun component2(): String {
         return this.name;
      }

      public operator fun component3(): Map<Language, MessageStrings> {
         return this.messageStrings;
      }

      public fun copy(id: String = this.id, name: String = this.name, messageStrings: Map<Language, MessageStrings> = this.messageStrings): cn.sast.framework.report.SarifDiagnostics.MultiLangRule {
         return new SarifDiagnostics.MultiLangRule(id, name, messageStrings);
      }

      public override fun toString(): String {
         return "MultiLangRule(id=${this.id}, name=${this.name}, messageStrings=${this.messageStrings})";
      }

      public override fun hashCode(): Int {
         return (this.id.hashCode() * 31 + this.name.hashCode()) * 31 + this.messageStrings.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is SarifDiagnostics.MultiLangRule) {
            return false;
         } else {
            val var2: SarifDiagnostics.MultiLangRule = other as SarifDiagnostics.MultiLangRule;
            if (!(this.id == (other as SarifDiagnostics.MultiLangRule).id)) {
               return false;
            } else if (!(this.name == var2.name)) {
               return false;
            } else {
               return this.messageStrings == var2.messageStrings;
            }
         }
      }
   }

   @SourceDebugExtension(["SMAP\nSarifDiagnostics.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SarifDiagnostics.kt\ncn/sast/framework/report/SarifDiagnostics$SarifDiagnosticsImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,146:1\n1557#2:147\n1628#2,2:148\n1630#2:151\n1797#2,3:152\n1246#2,4:157\n1557#2:168\n1628#2,3:169\n1053#2:172\n1557#2:173\n1628#2,3:174\n1#3:150\n462#4:155\n412#4:156\n381#4,7:161\n*S KotlinDebug\n*F\n+ 1 SarifDiagnostics.kt\ncn/sast/framework/report/SarifDiagnostics$SarifDiagnosticsImpl\n*L\n59#1:147\n59#1:148,2\n59#1:151\n92#1:152,3\n103#1:157,4\n114#1:168\n114#1:169,3\n118#1:172\n118#1:173\n118#1:174,3\n103#1:155\n103#1:156\n104#1:161,7\n*E\n"])
   public open inner class SarifDiagnosticsImpl(metadata: MetaData, locator: IProjectFileLocator) {
      private final val metadata: MetaData
      private final val locator: IProjectFileLocator
      private final var ruleToIndex: MutableMap<cn.sast.framework.report.SarifDiagnostics.MultiLangRule, Int>
      private final lateinit var sortedRuleToIndex: List<cn.sast.framework.report.SarifDiagnostics.MultiLangRule>

      public open val file2uri: String
         public open get() {
            val var10000: java.lang.String = `$this$file2uri`.expandRes(this.this$0.getOutputDir()).getAbsolute().getNormalize().getPath().toUri().toString();
            return var10000;
         }


      public open val absPathMapToFolder: String
         public open get() {
            val it: java.lang.String = StringsKt.removePrefix(
               StringsKt.removePrefix(`$this$absPathMapToFolder`.getAbsolute().getNormalize().toString(), "/"), "\\"
            );
            return StringsKt.replace$default(
               StringsKt.replace$default(
                  StringsKt.replace$default(
                     if (OS.INSTANCE.isWindows()) StringsKt.replace$default(it, ":", "", false, 4, null) else it, "\\", "/", false, 4, null
                  ),
                  "//",
                  "/",
                  false,
                  4,
                  null
               ),
               "!",
               "",
               false,
               4,
               null
            );
         }


      private final val preferredMessage: String
         private final get() {
            return ReportKt.preferredMessage(`$this$preferredMessage`, SarifDiagnostics.SarifDiagnosticsImpl::_get_preferredMessage_$lambda$1);
         }


      init {
         this.this$0 = `this$0`;
         this.metadata = metadata;
         this.locator = locator;
         this.ruleToIndex = new LinkedHashMap<>();
      }

      private fun getTool(): Tool {
         val var10000: java.lang.String = this.metadata.getAnalyzerName();
         val var10001: java.lang.String = this.metadata.getToolName();
         val var10002: java.lang.String = this.metadata.getToolVersion();
         var var10003: java.util.List = this.sortedRuleToIndex;
         if (this.sortedRuleToIndex == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sortedRuleToIndex");
            var10003 = null;
         }

         val `$this$map$iv`: java.lang.Iterable = var10003;
         val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var10003, 10));

         for (Object item$iv$iv : $this$map$iv) {
            `destination$iv$iv`.add(
               new Rule(
                  (`item$iv$iv` as SarifDiagnostics.MultiLangRule).getId(),
                  (`item$iv$iv` as SarifDiagnostics.MultiLangRule).getName(),
                  ReportKt.preferredMessage(
                     (`item$iv$iv` as SarifDiagnostics.MultiLangRule).getMessageStrings(),
                     SarifDiagnostics.SarifDiagnosticsImpl::getTool$lambda$4$lambda$3$lambda$2
                  )
               )
            );
         }

         return new Tool(new ToolComponent(var10000, var10001, var10002, `destination$iv$iv` as MutableList<Rule>));
      }

      private fun getTranslations(): List<TranslationToolComponent> {
         return CollectionsKt.emptyList();
      }

      public open fun getArtifactLocation(file: IResFile): ArtifactLocation {
         return new ArtifactLocation(this.getFile2uri(file), null, 2, null);
      }

      public open fun getPhysicalLocation(classInfo: IBugResInfo, region: Region): PhysicalLocation? {
         val var10000: IResFile = this.locator.get(classInfo, EmptyWrapperFileGenerator.INSTANCE);
         return if (var10000 != null) new PhysicalLocation(this.getArtifactLocation(var10000), new cn.sast.framework.report.sarif.Region(region)) else null;
      }

      private fun getPhysicalLocation(report: Report): PhysicalLocation? {
         return this.getPhysicalLocation(report.getBugResFile(), report.getRegion());
      }

      private fun getPhysicalLocation(event: BugPathEvent): PhysicalLocation? {
         return this.getPhysicalLocation(event.getClassname(), event.getRegion());
      }

      private fun getThreadFlow(events: List<BugPathEvent>): ThreadFlow {
         val `$this$fold$iv`: java.lang.Iterable = events;
         var `accumulator$iv`: Any = CollectionsKt.emptyList();

         for (Object element$iv : $this$fold$iv) {
            var var14: Any;
            label16: {
               val event: BugPathEvent = `element$iv` as BugPathEvent;
               var14 = this.getPhysicalLocation(`element$iv` as BugPathEvent);
               if (var14 != null) {
                  var14 = CollectionsKt.plus(
                     `accumulator$iv` as java.util.Collection,
                     new FlowLocationWrapper(
                        new FlowLocation(new Message(this.getPreferredMessage(event.getMessage()), null, 2, null), (PhysicalLocation)var14)
                     )
                  );
                  if (var14 != null) {
                     break label16;
                  }
               }

               var14 = `accumulator$iv`;
            }

            `accumulator$iv` = var14;
         }

         return new ThreadFlow((java.util.List<FlowLocationWrapper>)`accumulator$iv`);
      }

      private fun getResult(report: Report): Result? {
         var var10000: java.lang.String = report.getCheck_name();
         var `$this$getOrPut$iv`: java.util.Map = new LinkedHashMap(MapsKt.mapCapacity(report.getMessage().size()));

         val `value$iv`: Any;
         for (Object element$iv$iv$iv : value$iv) {
            `$this$getOrPut$iv`.put(
               (`element$iv$iv$iv` as Entry).getKey(),
               new MessageStrings(new Message((`element$iv$iv$iv` as Entry).getValue() as java.lang.String, null, 2, null))
            );
         }

         val var25: SarifDiagnostics.MultiLangRule = new SarifDiagnostics.MultiLangRule(var10000, "", `$this$getOrPut$iv`);
         `$this$getOrPut$iv` = this.ruleToIndex;
         `value$iv` = this.ruleToIndex.get(var25);
         if (`value$iv` == null) {
            val var30: Any = this.ruleToIndex.size();
            `$this$getOrPut$iv`.put(var25, var30);
            var10000 = (java.lang.String)var30;
         } else {
            var10000 = (java.lang.String)`value$iv`;
         }

         val ruleIndex: Int = (var10000 as java.lang.Number).intValue();
         val var32: Result = new Result;
         val var10002: java.lang.String = report.getCheck_name();
         val var10005: Location = new Location;
         val var10007: PhysicalLocation = this.getPhysicalLocation(report);
         if (var10007 == null) {
            return null;
         } else {
            var10005./* $VF: Unable to resugar constructor */<init>(var10007);
            var32./* $VF: Unable to resugar constructor */<init>(
               var10002,
               ruleIndex,
               null,
               CollectionsKt.listOf(var10005),
               CollectionsKt.listOf(new CodeFlow(CollectionsKt.listOf(this.getThreadFlow(report.getPathEvents())))),
               4,
               null
            );
            return var32;
         }
      }

      private fun getResults(reports: List<Report>): List<Result?> {
         val `$this$map$iv`: java.lang.Iterable = reports;
         val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(reports, 10));

         for (Object item$iv$iv : $this$map$iv) {
            `destination$iv$iv`.add(this.getResult(`item$iv$iv` as Report));
         }

         return `destination$iv$iv` as MutableList<Result>;
      }

      public open fun getRun(reports: List<Report>): Run {
         val results: java.util.List = CollectionsKt.filterNotNull(this.getResults(reports));
         val var14: java.lang.Iterable = CollectionsKt.sortedWith(
            MapsKt.toList(this.ruleToIndex), new SarifDiagnostics$SarifDiagnosticsImpl$getRun$$inlined$sortedBy$1()
         );
         val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var14, 10));

         for (Object item$iv$iv : var14) {
            `destination$iv$iv`.add((`item$iv$iv` as Pair).getFirst() as SarifDiagnostics.MultiLangRule);
         }

         this.sortedRuleToIndex = `destination$iv$iv` as MutableList<SarifDiagnostics.MultiLangRule>;
         return new Run(this.getTool(), null, results, this.getTranslations(), 2, null);
      }

      public fun getSarifLog(reports: List<Report>): SarifLog {
         return new SarifLog(
            "https://raw.githubusercontent.com/oasis-tcs/sarif-spec/master/Schemata/sarif-schema-2.1.0.json",
            "2.1.0",
            CollectionsKt.listOf(this.getRun(reports))
         );
      }

      @JvmStatic
      fun `_get_preferredMessage_$lambda$1`(): java.lang.String {
         return "Engine error: no such message of preferred languages ${MainConfig.Companion.getPreferredLanguages()}";
      }

      @JvmStatic
      fun `getTool$lambda$4$lambda$3$lambda$2`(): MessageStrings {
         return new MessageStrings(
            new Message("Engine error: no such message of preferred languages ${MainConfig.Companion.getPreferredLanguages()}", null, 2, null)
         );
      }
   }

   private object SarifMetadata {
      public const val schema: String = "https://raw.githubusercontent.com/oasis-tcs/sarif-spec/master/Schemata/sarif-schema-2.1.0.json"
      public const val version: String = "2.1.0"
   }
}
