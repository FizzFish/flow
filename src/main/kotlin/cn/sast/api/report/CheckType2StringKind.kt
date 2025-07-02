package cn.sast.api.report

import com.feysh.corax.config.api.CheckType
import java.util.Locale
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/CheckType2StringKind\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
public enum class CheckType2StringKind(convert: (CheckType) -> String) {
   RuleDotTYName(CheckType2StringKind::_init_$lambda$0),
   RuleDotTYName2(CheckType2StringKind::_init_$lambda$1),
   RuleName(CheckType2StringKind::_init_$lambda$2)
   public final val convert: (CheckType) -> String
   @JvmStatic
   public CheckType2StringKind.Companion Companion = new CheckType2StringKind.Companion(null);
   @JvmStatic
   private java.lang.String ruleNameKindEnv = "REPORT_RULE_KIND";
   @JvmStatic
   private CheckType2StringKind checkType2StringKind;

   init {
      this.convert = convert;
   }

   @JvmStatic
   fun getEntries(): EnumEntries<CheckType2StringKind> {
      return $ENTRIES;
   }

   @JvmStatic
   fun `_init_$lambda$0`(t: CheckType): java.lang.String {
      return "${t.getReport().getRealName()}.${t.getClass().getSimpleName()}";
   }

   @JvmStatic
   fun `_init_$lambda$1`(t: CheckType): java.lang.String {
      val var10000: java.lang.String = t.getReport().getRealName();
      var var10001: java.lang.String = t.getClass().getSimpleName();
      val var2: Locale = Locale.getDefault();
      var10001 = var10001.toLowerCase(var2);
      return "$var10000.$var10001";
   }

   @JvmStatic
   fun `_init_$lambda$2`(t: CheckType): java.lang.String {
      return t.getReport().getRealName();
   }

   @JvmStatic
   fun {
      var var10000: java.lang.String = System.getenv("REPORT_RULE_KIND");
      if (var10000 == null) {
         var10000 = System.getProperty("REPORT_RULE_KIND");
      }

      label14: {
         if (var10000 != null) {
            var2 = valueOf(var10000);
            if (var2 != null) {
               break label14;
            }
         }

         var2 = RuleDotTYName;
      }

      checkType2StringKind = var2;
   }

   public companion object {
      private const val ruleNameKindEnv: String
      public final val checkType2StringKind: CheckType2StringKind
   }
}
