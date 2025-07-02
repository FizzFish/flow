package cn.sast.api.report

import cn.sast.api.util.ComparatorUtilsKt
import cn.sast.common.GLB
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.IChecker
import com.feysh.corax.config.api.IRule
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.BugMessage.Env
import com.feysh.corax.config.api.report.Region
import java.util.ArrayList
import kotlin.enums.EnumEntries
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.json.JsonBuilder

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/Report\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1628#2,3:452\n1628#2,3:455\n1628#2,3:458\n1628#2,3:461\n1#3:464\n*S KotlinDebug\n*F\n+ 1 Report.kt\ncn/sast/api/report/Report\n*L\n330#1:452,3\n331#1:455,3\n332#1:458,3\n333#1:461,3\n*E\n"])
public data class Report internal constructor(bugResFile: IBugResInfo,
      region: Region,
      message: Map<Language, String>,
      check_name: String,
      detector_name: String,
      type: String,
      standard: Set<IRule>,
      severity: String? = null,
      analyzer_name: String? = null,
      category: String? = null,
      analyzer_result_file_path: String? = null,
      checkType: CheckType,
      pathEvents: MutableList<BugPathEvent> = (new ArrayList()) as java.util.List,
      bug_path_positions: MutableList<BugPathPosition> = (new ArrayList()) as java.util.List,
      notes: MutableList<BugPathEvent> = (new ArrayList()) as java.util.List,
      macro_expansions: MutableList<MacroExpansion> = (new ArrayList()) as java.util.List
   ) :
   java.lang.Comparable<Report>,
   IReportHashAble {
   public final val bugResFile: IBugResInfo
   public final val region: Region
   public final val message: Map<Language, String>
   public final val check_name: String
   public final val detector_name: String
   public final val type: String
   public final val standard: Set<IRule>
   public final val severity: String?
   public final val analyzer_name: String?
   public final val category: String?
   public final val analyzer_result_file_path: String?
   public final val checkType: CheckType

   public final var pathEvents: MutableList<BugPathEvent>
      internal set

   public final var bug_path_positions: MutableList<BugPathPosition>
      internal set

   public final var notes: MutableList<BugPathEvent>
      internal set

   public final var macro_expansions: MutableList<MacroExpansion>
      internal set

   public final val classes: MutableSet<IBugResInfo>
      public final get() {
         val ret: java.util.Set = SetsKt.mutableSetOf(new IBugResInfo[]{this.bugResFile});

         val var9: java.lang.Iterable;
         for (Object item$iv : var9) {
            ret.add((`item$iv` as BugPathEvent).getClassname());
         }

         for (Object item$iv : var9) {
            ret.add((var19 as BugPathPosition).getClassname());
         }

         for (Object item$iv : var9) {
            ret.add((var20 as BugPathEvent).getClassname());
         }

         for (Object item$iv : var9) {
            ret.add((var21 as MacroExpansion).getClassname());
         }

         return ret;
      }


   init {
      this.bugResFile = bugResFile;
      this.region = region;
      this.message = message;
      this.check_name = check_name;
      this.detector_name = detector_name;
      this.type = type;
      this.standard = standard;
      this.severity = severity;
      this.analyzer_name = analyzer_name;
      this.category = category;
      this.analyzer_result_file_path = analyzer_result_file_path;
      this.checkType = checkType;
      this.pathEvents = pathEvents;
      this.bug_path_positions = bug_path_positions;
      this.notes = notes;
      this.macro_expansions = macro_expansions;
      GLB.INSTANCE.plusAssign(this.checkType);
      this.pathEvents.add(new BugPathEvent(this.message, this.bugResFile, this.region, null, 8, null));
   }

   public override fun reportHash(c: IReportHashCalculator): String {
      return "${this.bugResFile.reportHash(c)}:${this.region} ${this.check_name},${this.detector_name},${this.type},${this.category},${this.severity},${this.analyzer_name}} ";
   }

   private fun getReportHashContextFree(c: IReportHashCalculator, ret: MutableList<String>) {
      ret.add(this.reportHash(c));
   }

   private fun getReportHashPathSensitive(c: IReportHashCalculator, ret: MutableList<String>) {
      val var10000: BugPathEvent = CollectionsKt.lastOrNull(this.pathEvents) as BugPathEvent;
      if (var10000 != null) {
         ret.add(var10000.reportHash(c));
      }
   }

   private fun getReportHashDiagnosticMessage(c: IReportHashCalculator, ret: MutableList<String>, onlyHeadTail: Boolean = false) {
      if (onlyHeadTail) {
         for (BugPathEvent be : this.pathEvents) {
            ret.add(be.reportHashWithMessage(c));
         }
      } else {
         var var10000: BugPathEvent = CollectionsKt.firstOrNull(this.pathEvents) as BugPathEvent;
         if (var10000 != null) {
            ret.add(var10000.reportHashWithMessage(c));
         }

         var10000 = CollectionsKt.lastOrNull(this.pathEvents) as BugPathEvent;
         if (var10000 != null) {
            var10000 = if (this.pathEvents.size() >= 2) var10000 else null;
            if (var10000 != null) {
               ret.add(var10000.reportHashWithMessage(c));
            }
         }
      }
   }

   public fun reportHash(hashCalculator: IReportHashCalculator, hashType: cn.sast.api.report.Report.HashType): String {
      val ret: java.util.List = new ArrayList();
      switch (Report.WhenMappings.$EnumSwitchMapping$0[hashType.ordinal()]) {
         case 1:
            this.getReportHashContextFree(hashCalculator, ret);
            break;
         case 2:
            this.getReportHashContextFree(hashCalculator, ret);
            this.getReportHashPathSensitive(hashCalculator, ret);
            break;
         case 3:
            this.getReportHashContextFree(hashCalculator, ret);
            getReportHashDiagnosticMessage$default(this, hashCalculator, ret, false, 4, null);
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return ReportKt.toHex(ReportKt.md5(CollectionsKt.joinToString$default(ret, "|", null, null, 0, null, null, 62, null)));
   }

   public open operator fun compareTo(other: Report): Int {
      var var3: Int = ComparatorUtilsKt.compareToNullable(this.analyzer_name, other.analyzer_name);
      var var2: Int = if (var3.intValue() != 0) var3 else null;
      if (var2 != null) {
         return var2.intValue();
      } else {
         var3 = this.bugResFile.compareTo(other.bugResFile);
         var2 = if (var3.intValue() != 0) var3 else null;
         if (var2 != null) {
            return var2.intValue();
         } else {
            var3 = Intrinsics.compare(this.region.startLine, other.region.startLine);
            var2 = if (var3.intValue() != 0) var3 else null;
            if (var2 != null) {
               return var2.intValue();
            } else {
               var3 = ComparatorUtilsKt.compareToMap(MapsKt.toSortedMap(this.message), MapsKt.toSortedMap(other.message));
               var2 = if (var3.intValue() != 0) var3 else null;
               if (var2 != null) {
                  return var2.intValue();
               } else {
                  var3 = this.check_name.compareTo(other.check_name);
                  var2 = if (var3.intValue() != 0) var3 else null;
                  if (var2 != null) {
                     return var2.intValue();
                  } else {
                     var3 = this.detector_name.compareTo(other.detector_name);
                     var2 = if (var3.intValue() != 0) var3 else null;
                     if (var2 != null) {
                        return var2.intValue();
                     } else {
                        var3 = this.type.compareTo(other.type);
                        var2 = if (var3.intValue() != 0) var3 else null;
                        if (var2 != null) {
                           return var2.intValue();
                        } else {
                           var3 = (new Report$compareTo$$inlined$compareBy$1<CheckType>()).compare(this.checkType, other.checkType);
                           var2 = if (var3.intValue() != 0) var3 else null;
                           if (var2 != null) {
                              return var2.intValue();
                           } else {
                              var3 = ComparatorUtilsKt.compareTo(new Report$compareTo$$inlined$compareBy$2<>(), this.standard, other.standard);
                              var2 = if (var3.intValue() != 0) var3 else null;
                              if (var2 != null) {
                                 return var2.intValue();
                              } else {
                                 var3 = Intrinsics.compare(this.region.startColumn, other.region.startColumn);
                                 var2 = if (var3.intValue() != 0) var3 else null;
                                 if (var2 != null) {
                                    return var2.intValue();
                                 } else {
                                    var3 = ComparatorUtilsKt.compareToNullable(this.severity, other.severity);
                                    var2 = if (var3.intValue() != 0) var3 else null;
                                    if (var2 != null) {
                                       return var2.intValue();
                                    } else {
                                       var3 = ComparatorUtilsKt.compareToNullable(this.category, other.category);
                                       var2 = if (var3.intValue() != 0) var3 else null;
                                       if (var2 != null) {
                                          return var2.intValue();
                                       } else {
                                          var3 = ComparatorUtilsKt.compareToNullable(this.analyzer_result_file_path, other.analyzer_result_file_path);
                                          var2 = if (var3.intValue() != 0) var3 else null;
                                          if (var2 != null) {
                                             return var2.intValue();
                                          } else {
                                             var3 = ComparatorUtilsKt.compareToCollection(this.pathEvents, other.pathEvents);
                                             var2 = if (var3.intValue() != 0) var3 else null;
                                             if (var2 != null) {
                                                return var2.intValue();
                                             } else {
                                                var3 = ComparatorUtilsKt.compareToCollection(this.bug_path_positions, other.bug_path_positions);
                                                var2 = if (var3.intValue() != 0) var3 else null;
                                                if (var2 != null) {
                                                   return var2.intValue();
                                                } else {
                                                   var3 = ComparatorUtilsKt.compareToCollection(this.notes, other.notes);
                                                   var2 = if (var3.intValue() != 0) var3 else null;
                                                   label147:
                                                   if (var2 != null) {
                                                      return var2.intValue();
                                                   } else {
                                                      var3 = ComparatorUtilsKt.compareToCollection(this.macro_expansions, other.macro_expansions);
                                                      var2 = if (var3.intValue() != 0) var3 else null;
                                                      return if (var2 != null) var2.intValue() else 0;
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public operator fun component1(): IBugResInfo {
      return this.bugResFile;
   }

   public operator fun component2(): Region {
      return this.region;
   }

   public operator fun component3(): Map<Language, String> {
      return this.message;
   }

   public operator fun component4(): String {
      return this.check_name;
   }

   public operator fun component5(): String {
      return this.detector_name;
   }

   public operator fun component6(): String {
      return this.type;
   }

   public operator fun component7(): Set<IRule> {
      return this.standard;
   }

   public operator fun component8(): String? {
      return this.severity;
   }

   public operator fun component9(): String? {
      return this.analyzer_name;
   }

   public operator fun component10(): String? {
      return this.category;
   }

   public operator fun component11(): String? {
      return this.analyzer_result_file_path;
   }

   public operator fun component12(): CheckType {
      return this.checkType;
   }

   public operator fun component13(): MutableList<BugPathEvent> {
      return this.pathEvents;
   }

   public operator fun component14(): MutableList<BugPathPosition> {
      return this.bug_path_positions;
   }

   public operator fun component15(): MutableList<BugPathEvent> {
      return this.notes;
   }

   public operator fun component16(): MutableList<MacroExpansion> {
      return this.macro_expansions;
   }

   public fun copy(
      bugResFile: IBugResInfo = this.bugResFile,
      region: Region = this.region,
      message: Map<Language, String> = this.message,
      check_name: String = this.check_name,
      detector_name: String = this.detector_name,
      type: String = this.type,
      standard: Set<IRule> = this.standard,
      severity: String? = this.severity,
      analyzer_name: String? = this.analyzer_name,
      category: String? = this.category,
      analyzer_result_file_path: String? = this.analyzer_result_file_path,
      checkType: CheckType = this.checkType,
      pathEvents: MutableList<BugPathEvent> = this.pathEvents,
      bug_path_positions: MutableList<BugPathPosition> = this.bug_path_positions,
      notes: MutableList<BugPathEvent> = this.notes,
      macro_expansions: MutableList<MacroExpansion> = this.macro_expansions
   ): Report {
      return new Report(
         bugResFile,
         region,
         message,
         check_name,
         detector_name,
         type,
         standard,
         severity,
         analyzer_name,
         category,
         analyzer_result_file_path,
         checkType,
         pathEvents,
         bug_path_positions,
         notes,
         macro_expansions
      );
   }

   public override fun toString(): String {
      return "Report(bugResFile=${this.bugResFile}, region=${this.region}, message=${this.message}, check_name=${this.check_name}, detector_name=${this.detector_name}, type=${this.type}, standard=${this.standard}, severity=${this.severity}, analyzer_name=${this.analyzer_name}, category=${this.category}, analyzer_result_file_path=${this.analyzer_result_file_path}, checkType=${this.checkType}, pathEvents=${this.pathEvents}, bug_path_positions=${this.bug_path_positions}, notes=${this.notes}, macro_expansions=${this.macro_expansions})";
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
                                                                              (
                                                                                       (
                                                                                                (
                                                                                                         (
                                                                                                                  (
                                                                                                                           (
                                                                                                                                    this.bugResFile.hashCode()
                                                                                                                                          * 31
                                                                                                                                       + this.region.hashCode()
                                                                                                                                 )
                                                                                                                                 * 31
                                                                                                                              + this.message.hashCode()
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + this.check_name.hashCode()
                                                                                                               )
                                                                                                               * 31
                                                                                                            + this.detector_name.hashCode()
                                                                                                      )
                                                                                                      * 31
                                                                                                   + this.type.hashCode()
                                                                                             )
                                                                                             * 31
                                                                                          + this.standard.hashCode()
                                                                                    )
                                                                                    * 31
                                                                                 + (if (this.severity == null) 0 else this.severity.hashCode())
                                                                           )
                                                                           * 31
                                                                        + (if (this.analyzer_name == null) 0 else this.analyzer_name.hashCode())
                                                                  )
                                                                  * 31
                                                               + (if (this.category == null) 0 else this.category.hashCode())
                                                         )
                                                         * 31
                                                      + (if (this.analyzer_result_file_path == null) 0 else this.analyzer_result_file_path.hashCode())
                                                )
                                                * 31
                                             + this.checkType.hashCode()
                                       )
                                       * 31
                                    + this.pathEvents.hashCode()
                              )
                              * 31
                           + this.bug_path_positions.hashCode()
                     )
                     * 31
                  + this.notes.hashCode()
            )
            * 31
         + this.macro_expansions.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Report) {
         return false;
      } else {
         val var2: Report = other as Report;
         if (!(this.bugResFile == (other as Report).bugResFile)) {
            return false;
         } else if (!(this.region == var2.region)) {
            return false;
         } else if (!(this.message == var2.message)) {
            return false;
         } else if (!(this.check_name == var2.check_name)) {
            return false;
         } else if (!(this.detector_name == var2.detector_name)) {
            return false;
         } else if (!(this.type == var2.type)) {
            return false;
         } else if (!(this.standard == var2.standard)) {
            return false;
         } else if (!(this.severity == var2.severity)) {
            return false;
         } else if (!(this.analyzer_name == var2.analyzer_name)) {
            return false;
         } else if (!(this.category == var2.category)) {
            return false;
         } else if (!(this.analyzer_result_file_path == var2.analyzer_result_file_path)) {
            return false;
         } else if (!(this.checkType == var2.checkType)) {
            return false;
         } else if (!(this.pathEvents == var2.pathEvents)) {
            return false;
         } else if (!(this.bug_path_positions == var2.bug_path_positions)) {
            return false;
         } else if (!(this.notes == var2.notes)) {
            return false;
         } else {
            return this.macro_expansions == var2.macro_expansions;
         }
      }
   }

   @JvmStatic
   fun JsonBuilder.`deprecatedRuleCategoryMap$lambda$46$lambda$44`(): Unit {
      `$this$Json`.setUseArrayPolymorphism(true);
      `$this$Json`.setPrettyPrint(true);
      return Unit.INSTANCE;
   }

   @SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/Report$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1611#2,9:452\n1863#2:461\n1864#2:463\n1620#2:464\n1#3:462\n1#3:465\n*S KotlinDebug\n*F\n+ 1 Report.kt\ncn/sast/api/report/Report$Companion\n*L\n278#1:452,9\n278#1:461\n278#1:463\n278#1:464\n278#1:462\n*E\n"])
   public companion object {
      private final val deprecatedRuleCategoryMap: Map<String, String>

      public final var hashVersion: Int
         internal set

      private fun getFinalReportCheckerName(checker: IChecker): String {
         val var10000: java.lang.String = checker.getClass().getName();
         return var10000;
      }

      public fun of(
         info: SootInfoCache?,
         file: IBugResInfo,
         region: Region,
         checkType: CheckType,
         env: Env,
         pathEvents: List<BugPathEvent> = CollectionsKt.emptyList()
      ): Report {
         var pathEventsMutable: java.util.List = null;
         val f: BugPathEventEnvironment = new BugPathEventEnvironment(info);
         if (env is AbstractBugEnv && !(env as AbstractBugEnv).getAppendEvents().isEmpty()) {
            val `$this$mapNotNull$iv`: java.lang.Iterable = (env as AbstractBugEnv).getAppendEvents();
            val `destination$iv$iv`: java.util.Collection = new ArrayList();

            for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
               val var10000: BugPathEvent = (`element$iv$iv$iv` as Function1).invoke(f) as BugPathEvent;
               if (var10000 != null) {
                  `destination$iv$iv`.add(var10000);
               }
            }

            pathEventsMutable = `destination$iv$iv` as java.util.List;
         }

         var `$this$mapNotNullTo$iv$iv`: java.lang.String;
         var `$this$forEach$iv$iv$iv`: java.util.Set;
         var var24: java.util.Map;
         var var25: java.lang.String;
         var var26: java.lang.String;
         var var27: java.lang.String;
         var var30: java.util.List;
         label26: {
            var24 = ReportKt.bugMessage(checkType, env);
            var25 = CheckType2StringKind.Companion.getCheckType2StringKind().getConvert().invoke(checkType) as java.lang.String;
            `$this$mapNotNullTo$iv$iv` = this.getFinalReportCheckerName(checkType.getChecker());
            var26 = checkType.toString();
            var27 = this.categoryStr(checkType);
            `$this$forEach$iv$iv$iv` = checkType.getStandards();
            if (pathEventsMutable != null) {
               var30 = CollectionsKt.plus(pathEvents, pathEventsMutable);
               if (var30 != null) {
                  break label26;
               }
            }

            var30 = pathEvents;
         }

         return new Report(
            file,
            region,
            var24,
            var25,
            `$this$mapNotNullTo$iv$iv`,
            var26,
            `$this$forEach$iv$iv$iv`,
            null,
            null,
            var27,
            null,
            checkType,
            CollectionsKt.toMutableList(var30),
            null,
            null,
            null,
            58752,
            null
         );
      }

      private fun CheckType.categoryStr(): String {
         var var10000: java.lang.String;
         switch (this.getHashVersion()) {
            case 1:
               var10000 = Report.access$getDeprecatedRuleCategoryMap$cp()
                  .get(CheckType2StringKind.RuleDotTYName.getConvert().invoke(`$this$categoryStr`) as java.lang.String) as java.lang.String;
               if (var10000 == null) {
                  var10000 = "unknown";
               }
               break;
            case 2:
               var10000 = CheckType2StringKind.Companion.getCheckType2StringKind().getConvert().invoke(`$this$categoryStr`) as java.lang.String;
               break;
            default:
               throw new IllegalStateException(("Bad hash version: ${this.getHashVersion()}").toString());
         }

         return var10000;
      }
   }

   public enum class HashType {
      CONTEXT_FREE,
      PATH_SENSITIVE,
      DIAGNOSTIC_MESSAGE
      @JvmStatic
      fun getEntries(): EnumEntries<Report.HashType> {
         return $ENTRIES;
      }
   }
}
