package cn.sast.framework.report

import app.cash.sqldelight.TransactionWithoutReturn
import app.cash.sqldelight.Transacter.DefaultImpls
import cn.sast.api.config.ChapterFlat
import cn.sast.api.config.CheckerInfo
import cn.sast.api.config.CheckerInfoGenResult
import cn.sast.api.config.CheckerPriorityConfig
import cn.sast.api.config.Tag
import cn.sast.common.Resource
import cn.sast.framework.report.sqldelight.Database
import cn.sast.framework.report.sqldelight.Rule
import cn.sast.framework.report.sqldelight.RuleMapping
import cn.sast.framework.report.sqldelight.RuleMappingQueries
import cn.sast.framework.report.sqldelight.RuleQueries
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger

@SourceDebugExtension(["SMAP\nSqliteDiagnostics.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SqliteDiagnostics.kt\ncn/sast/framework/report/RuleAndRuleMapping\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,813:1\n1202#2,2:814\n1230#2,4:816\n1202#2,2:820\n1230#2,4:822\n1628#2,2:826\n1630#2:829\n1628#2,3:830\n774#2:833\n865#2,2:834\n1863#2,2:836\n774#2:838\n865#2,2:839\n1863#2,2:841\n1#3:828\n*S KotlinDebug\n*F\n+ 1 SqliteDiagnostics.kt\ncn/sast/framework/report/RuleAndRuleMapping\n*L\n137#1:814,2\n137#1:816,4\n147#1:820,2\n147#1:822,4\n151#1:826,2\n151#1:829\n194#1:830,3\n208#1:833\n208#1:834,2\n208#1:836,2\n209#1:838\n209#1:839,2\n209#1:841,2\n*E\n"])
public class RuleAndRuleMapping(checkerInfo: CheckerInfoGenResult, ruleSortYaml: Path) {
   private final val checkerInfo: CheckerInfoGenResult
   private final val ruleSortYaml: Path
   private final val rules: Set<Rule>
   private final val ruleMapping: Set<RuleMapping>

   public final var id2checkerMap: Map<String, Rule>
      internal set

   init {
      this.checkerInfo = checkerInfo;
      this.ruleSortYaml = ruleSortYaml;
      this.id2checkerMap = new LinkedHashMap<>();
      val rulesAndRuleMappingsPair: Pair = this.parseRuleAndRuleMapping(this.ruleSortYaml);
      this.rules = rulesAndRuleMappingsPair.getFirst() as MutableSet<Rule>;
      this.ruleMapping = rulesAndRuleMappingsPair.getSecond() as MutableSet<RuleMapping>;
      val `$this$associateBy$iv`: java.lang.Iterable = this.rules;
      val `destination$iv$iv`: java.util.Map = new LinkedHashMap(
         RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(this.rules, 10)), 16)
      );

      for (Object element$iv$iv : $this$associateBy$iv) {
         `destination$iv$iv`.put((`element$iv$iv` as Rule).getName(), `element$iv$iv`);
      }

      this.id2checkerMap = `destination$iv$iv`;
   }

   private fun parseRuleAndRuleMapping(ruleSortYaml: Path): Pair<Set<Rule>, Set<RuleMapping>> {
      val var10001: Array<LinkOption> = new LinkOption[0];
      val var10000: java.util.Map;
      if (!Files.exists(ruleSortYaml, Arrays.copyOf(var10001, var10001.length))) {
         logger.warn(RuleAndRuleMapping::parseRuleAndRuleMapping$lambda$1);
         var10000 = null;
      } else {
         val rows: java.lang.Iterable = CheckerPriorityConfig.Companion
            .deserialize(Resource.INSTANCE.fileOf(ruleSortYaml))
            .getRuleWithSortNumber(this.checkerInfo.getChapters());
         val `destination$iv`: java.util.Map = new LinkedHashMap(
            RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(rows, 10)), 16)
         );

         for (Object element$iv$iv : rows) {
            `destination$iv`.put(((`item$iv` as IndexedValue).getValue() as ChapterFlat).getRuleId(), `item$iv`);
         }

         var10000 = `destination$iv`;
      }

      val ruleWithSortNumber: java.util.Map = var10000;
      val var62: LinkedHashSet = this.checkerInfo.getCheckerInfoList();
      val var63: java.util.Set = new LinkedHashSet();
      val `$this$mapTo$iv`: java.lang.Iterable = var62;
      val var65: java.util.Collection = new LinkedHashSet();

      for (Object item$iv : $this$mapTo$iv) {
         val var69: CheckerInfo = var68 as CheckerInfo;
         val name: java.lang.String = (var68 as CheckerInfo).getChecker_id();
         val ruleSortNumber: IndexedValue = if (ruleWithSortNumber != null) ruleWithSortNumber.get(name) as IndexedValue else null;
         if (ruleWithSortNumber != null && ruleSortNumber == null) {
            logger.warn(RuleAndRuleMapping::parseRuleAndRuleMapping$lambda$5$lambda$3);
         }

         val chapterFlat: ChapterFlat = var69.getChapterFlat();
         var63.addAll(this.getCurrentRuleMappingSet(var69, name));
         var var71: java.lang.String = name;
         var var72: java.lang.String = var69.getName().get("en-US");
         if (var72 == null) {
            var72 = "None";
         }

         var var10002: java.lang.String = var69.getName().get("zh-CN");
         if (var10002 == null) {
            var10002 = "None";
         }

         val var10003: java.lang.String = var69.getSeverity();
         var var10004: Any = null;
         var var10005: java.lang.String = "Java";
         val var10006: java.lang.String = var69.getPrecision();
         val var10007: java.lang.String = var69.getReCall();
         val var10008: java.lang.String = var69.getLikelihood();
         val var10009: java.lang.String = var69.getImpact();
         val var10010: java.lang.String = var69.getImpl();
         var var10011: java.lang.String = "";
         var var10012: java.lang.String = "";
         val var10013: java.lang.Long = 1L;
         val var10014: java.lang.Boolean = var69.getImplemented();
         val var73: java.lang.Long;
         if (var10014 != null) {
            val var32: java.lang.Long = if (var10014) 1L else 0L;
            var71 = name;
            var10004 = null;
            var10005 = "Java";
            var10011 = "";
            var10012 = "";
            var73 = var32;
         } else {
            var73 = null;
         }

         var var10017: java.lang.String = var69.getCategory().get("en-US");
         if (var10017 == null) {
            var10017 = "None";
         }

         var var10018: java.lang.String = var69.getCategory().get("zh-CN");
         if (var10018 == null) {
            var10018 = "None";
         }

         val var10019: java.lang.Long = if (ruleSortNumber != null) (long)ruleSortNumber.getIndex() else (long)var62.size() + 1L;
         val var10020: java.lang.String = if (chapterFlat != null) chapterFlat.getCategory() else null;
         val var10021: java.lang.String = if (chapterFlat != null) chapterFlat.getSeverity() else null;
         val var10022: java.lang.String = if (chapterFlat != null) chapterFlat.getRuleId() else null;
         var var10024: java.lang.String = var69.getAbstract().get("en-US");
         if (var10024 == null) {
            var10024 = "None";
         }

         var var10025: java.lang.String = var69.getAbstract().get("zh-CN");
         if (var10025 == null) {
            var10025 = "None";
         }

         var var10026: java.lang.String = var69.getDescription().get("en-US");
         if (var10026 == null) {
            var10026 = "None";
         }

         var var10027: java.lang.String = var69.getDescription().get("zh-CN");
         if (var10027 == null) {
            var10027 = "None";
         }

         var65.add(
            new Rule(
               var71,
               var72,
               var10002,
               var10003,
               (java.lang.String)var10004,
               var10005,
               var10006,
               var10007,
               var10008,
               var10009,
               var10010,
               var10011,
               var10012,
               var10013,
               var73,
               null,
               null,
               var10017,
               var10018,
               var10019,
               var10020,
               var10021,
               var10022,
               null,
               var10024,
               var10025,
               var10026,
               var10027
            )
         );
      }

      return new Pair(var65 as java.util.Set, var63);
   }

   private fun getCurrentRuleMappingSet(row: CheckerInfo, name: String): Set<RuleMapping> {
      val `$this$mapTo$iv`: java.lang.Iterable = row.getTags();
      val `destination$iv`: java.util.Collection = new LinkedHashSet();

      for (Object item$iv : $this$mapTo$iv) {
         `destination$iv`.add(new RuleMapping(name, (`item$iv` as Tag).getStandard(), (`item$iv` as Tag).getRule()));
      }

      return `destination$iv` as MutableSet<RuleMapping>;
   }

   public fun insert(database: Database) {
      this.insert(database, null);
   }

   public fun insert(database: Database, filter: Set<String>?) {
      DefaultImpls.transaction$default(database, false, RuleAndRuleMapping::insert$lambda$11, 1, null);
   }

   @JvmStatic
   fun `parseRuleAndRuleMapping$lambda$1`(`$ruleSortYaml`: Path): Any {
      return "The ruleSortYaml $`$ruleSortYaml` is not exists";
   }

   @JvmStatic
   fun `parseRuleAndRuleMapping$lambda$5$lambda$3`(`$row`: CheckerInfo, `$name`: java.lang.String, `$ruleSortYaml`: Path): Any {
      return "can't find the category '${`$row`.getCategory().get("zh-CN")}' of $`$name` in $`$ruleSortYaml`";
   }

   @JvmStatic
   fun `insert$lambda$11`(`this$0`: RuleAndRuleMapping, `$database`: Database, `$f`: java.util.Set, `$this$transaction`: TransactionWithoutReturn): Unit {
      var `$this$forEach$iv`: java.lang.Iterable = `this$0`.rules;
      var `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$filter$iv) {
         if (`$f` == null || `$f`.contains((var10 as Rule).getName())) {
            `destination$iv$iv`.add(var10);
         }
      }

      `$this$forEach$iv` = `destination$iv$iv` as java.util.List;
      val var16: RuleQueries = `$database`.getRuleQueries();

      for (Object element$iv : $this$filter$iv) {
         var16.insert(var23 as Rule);
      }

      `$this$forEach$iv` = `this$0`.ruleMapping;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$ivx : $this$filter$iv) {
         if (`$f` == null || `$f`.contains((`element$iv$ivx` as RuleMapping).getRule_name())) {
            `destination$iv$iv`.add(`element$iv$ivx`);
         }
      }

      `$this$forEach$iv` = `destination$iv$iv` as java.util.List;
      val var18: RuleMappingQueries = `$database`.getRuleMappingQueries();

      for (Object element$iv : $this$filter$iv) {
         var18.insert(var25 as RuleMapping);
      }

      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `logger$lambda$12`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
