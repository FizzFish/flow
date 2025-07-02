package cn.sast.framework.report

import kotlin.enums.EnumEntries

public enum class ListSuffixRelation(neitherSuffix: Boolean) {
   Equals(false),
   AIsSuffixOfB(false),
   BIsSuffixOfA(false),
   NeitherSuffix(true)
   public final val neitherSuffix: Boolean

   init {
      this.neitherSuffix = neitherSuffix;
   }

   @JvmStatic
   fun getEntries(): EnumEntries<ListSuffixRelation> {
      return $ENTRIES;
   }
}
