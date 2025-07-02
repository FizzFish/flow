package cn.sast.framework.report.coverage

import com.feysh.corax.cache.AnalysisKey

public data class ClassSourceOfSCKey(className: String) : AnalysisKey(ClassSourceOfSCFactory.INSTANCE.getKey()) {
   public final val className: String

   init {
      this.className = className;
   }

   public operator fun component1(): String {
      return this.className;
   }

   public fun copy(className: String = this.className): ClassSourceOfSCKey {
      return new ClassSourceOfSCKey(className);
   }

   public override fun toString(): String {
      return "ClassSourceOfSCKey(className=${this.className})";
   }

   public override fun hashCode(): Int {
      return this.className.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ClassSourceOfSCKey) {
         return false;
      } else {
         return this.className == (other as ClassSourceOfSCKey).className;
      }
   }
}
