package cn.sast.api.config

import kotlin.enums.EnumEntries

public enum class SrcPrecedence(sootFlag: Int) {
   prec_class(1),
   prec_only_class(2),
   prec_jimple(3),
   prec_java(6),
   prec_java_soot(4),
   prec_apk(5),
   prec_apk_class_jimple(6),
   prec_dotnet(7)
   public final val sootFlag: Int

   public final val isSootJavaSourcePrec: Boolean
      public final get() {
         return this === prec_java_soot;
      }


   init {
      this.sootFlag = sootFlag;
   }

   @JvmStatic
   fun getEntries(): EnumEntries<SrcPrecedence> {
      return $ENTRIES;
   }
}
