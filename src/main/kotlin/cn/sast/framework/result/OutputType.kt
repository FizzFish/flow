package cn.sast.framework.result

import kotlin.enums.EnumEntries

public enum class OutputType(displayName: String) {
   PLIST("plist"),
   SARIF("sarif"),
   SQLITE("sqlite"),
   SarifPackSrc("sarif-pack"),
   SarifCopySrc("sarif-copy"),
   Coverage("coverage")
   public final val displayName: String

   init {
      this.displayName = displayName;
   }

   @JvmStatic
   fun getEntries(): EnumEntries<OutputType> {
      return $ENTRIES;
   }
}
