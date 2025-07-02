package cn.sast.framework

import kotlin.enums.EnumEntries

public enum class CgAlgorithmProvider {
   Soot,
   QiLin
   @JvmStatic
   fun getEntries(): EnumEntries<CgAlgorithmProvider> {
      return $ENTRIES;
   }
}
