package cn.sast.api.util

import cn.sast.api.report.ClassResInfo
import soot.ModuleUtil
import soot.SootClass
import soot.ModuleUtil.ModuleClassNameWrapper

public fun classSplit(cp: ClassResInfo): Pair<String, String> {
   return SootUtilsKt.classSplit(cp.getSc());
}

public fun getSourcePathModule(c: SootClass): String? {
   val var10000: java.lang.String = SootUtilsKt.getSourcePathFromAnnotation(c);
   if (var10000 == null) {
      return null;
   } else {
      var sourcePath: java.lang.String = var10000;
      val wrapper: ModuleClassNameWrapper = ModuleUtil.v().makeWrapper(c.getName());
      if (wrapper.getModuleName() != null) {
         sourcePath = "${wrapper.getModuleName()}/$var10000";
      }

      return sourcePath;
   }
}
