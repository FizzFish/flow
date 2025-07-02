package cn.sast.api.report

import soot.SootClass
import soot.SootMethod
import soot.Unit

public sealed class CoverSootCode protected constructor(method: SootMethod, unit: Unit) : CoverData() {
   public open val method: SootMethod
   public open val unit: Unit

   public final val clazz: SootClass
      public final get() {
         val var10000: SootClass = this.getMethod().getDeclaringClass();
         return var10000;
      }


   public final val className: String
      public final get() {
         val var10000: java.lang.String = this.getClazz().getName();
         return var10000;
      }


   public final val lineNumber: Int
      public final get() {
         return this.getUnit().getJavaSourceStartLineNumber();
      }


   init {
      this.method = method;
      this.unit = unit;
   }
}
