package cn.sast.api.config

import kotlin.enums.EnumEntries

public enum class StaticFieldTrackingMode {
   ContextFlowSensitive,
   ContextFlowInsensitive,
   None
   @JvmStatic
   fun getEntries(): EnumEntries<StaticFieldTrackingMode> {
      return $ENTRIES;
   }
}
