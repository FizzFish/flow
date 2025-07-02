package cn.sast.dataflow.interprocedural.check

import kotlin.enums.EnumEntries

public enum class OverrideModel {
   HashMap,
   ArrayList
   @JvmStatic
   fun getEntries(): EnumEntries<OverrideModel> {
      return $ENTRIES;
   }
}
