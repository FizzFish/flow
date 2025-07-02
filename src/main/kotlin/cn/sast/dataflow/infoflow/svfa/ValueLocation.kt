package cn.sast.dataflow.infoflow.svfa

import kotlin.enums.EnumEntries

internal enum class ValueLocation {
   ParamAndThis,
   Left,
   Right,
   Arg
   @JvmStatic
   fun getEntries(): EnumEntries<ValueLocation> {
      return $ENTRIES;
   }
}
