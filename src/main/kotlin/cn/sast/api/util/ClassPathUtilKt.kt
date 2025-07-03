package cn.sast.api.util

import cn.sast.api.report.ClassResInfo
import soot.ModuleUtil
import soot.SootClass
import soot.ModuleUtil.ModuleClassNameWrapper

fun classSplit(cp: ClassResInfo): Pair<String, String> {
    return SootUtilsKt.classSplit(cp.sc)
}

fun getSourcePathModule(c: SootClass): String? {
    val var10000 = SootUtilsKt.getSourcePathFromAnnotation(c)
    if (var10000 == null) {
        return null
    } else {
        var sourcePath = var10000
        val wrapper = ModuleUtil.v().makeWrapper(c.name)
        if (wrapper.moduleName != null) {
            sourcePath = "${wrapper.moduleName}/$var10000"
        }

        return sourcePath
    }
}