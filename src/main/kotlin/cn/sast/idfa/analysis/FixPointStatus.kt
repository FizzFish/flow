package cn.sast.idfa.analysis

import kotlin.enums.EnumEntries

enum class FixPointStatus {
   HasChange,
   Fixpoint,
   NeedWideningOperators;
}
