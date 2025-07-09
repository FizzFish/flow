package cn.sast.idfa.analysis

import kotlin.enums.EnumEntries

enum class FixPointStatus {
   HasChange,
   Fixpoint,
   NeedWideningOperators;

   companion object {
      fun of(hasChange: Boolean): FixPointStatus {
         return if (hasChange) HasChange else Fixpoint
      }

      fun getEntries(): EnumEntries<FixPointStatus> = entries
   }
}
