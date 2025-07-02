package cn.sast.api.report

import com.feysh.corax.cache.analysis.SootInfoCache

public data class BugPathEventEnvironment(sootInfoCache: SootInfoCache?) {
   public final val sootInfoCache: SootInfoCache?

   init {
      this.sootInfoCache = sootInfoCache;
   }

   public operator fun component1(): SootInfoCache? {
      return this.sootInfoCache;
   }

   public fun copy(sootInfoCache: SootInfoCache? = this.sootInfoCache): BugPathEventEnvironment {
      return new BugPathEventEnvironment(sootInfoCache);
   }

   public override fun toString(): String {
      return "BugPathEventEnvironment(sootInfoCache=${this.sootInfoCache})";
   }

   public override fun hashCode(): Int {
      return if (this.sootInfoCache == null) 0 else this.sootInfoCache.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is BugPathEventEnvironment) {
         return false;
      } else {
         return this.sootInfoCache == (other as BugPathEventEnvironment).sootInfoCache;
      }
   }
}
