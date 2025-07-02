package cn.sast.dataflow.interprocedural.check.printer

import soot.SootClass
import soot.SootMethodInterface

public fun getPrettyMethodName(declaringClass: SootClass, name: String): String {
   val var10000: java.lang.String;
   if (name == "<init>") {
      var10000 = declaringClass.getShortName();
   } else if (name == "<clinit>") {
      var10000 = declaringClass.getShortName();
   } else {
      var10000 = name;
   }

   return var10000;
}

public inline fun getPrettyMethodName(m: SootMethodInterface): String {
   val var10000: SootClass = m.getDeclaringClass();
   val var10001: java.lang.String = m.getName();
   return getPrettyMethodName(var10000, var10001);
}
