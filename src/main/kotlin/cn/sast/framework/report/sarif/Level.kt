package cn.sast.framework.report.sarif

import kotlin.enums.EnumEntries

public enum class Level(value: String) {
   None("none"),
   Note("note"),
   Warning("warning"),
   Error("error")

   public final val value: String
      public final get() {
         return this.value;
      }


   init {
      this.value = value;
   }

   @JvmStatic
   fun getEntries(): EnumEntries<Level> {
      return $ENTRIES;
   }
}
