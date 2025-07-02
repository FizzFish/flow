package cn.sast.dataflow.infoflow.manager

import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkCategory

public class SourceSinkCategory(detector: String, pattern: String) : ISourceSinkCategory {
   public final val detector: String
   public final val pattern: String

   init {
      this.detector = detector;
      this.pattern = pattern;
   }

   public open fun getHumanReadableDescription(): String {
      return "";
   }

   public open fun getID(): String {
      return this.pattern;
   }

   public override fun toString(): String {
      return "${this.detector}::${this.pattern}";
   }
}
