package cn.sast.framework.entries.apk

import kotlin.enums.EnumEntries

public enum class CallbackAnalyzerType {
   Default,
   Fast
   @JvmStatic
   fun getEntries(): EnumEntries<CallbackAnalyzerType> {
      return $ENTRIES;
   }
}
