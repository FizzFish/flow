package cn.sast.dataflow.interprocedural.check.printer

import soot.SootClass
import soot.SootMethodInterface

public fun getPrettyMethodName(declaringClass: SootClass, name: String): String {
    return when (name) {
        "<init>", "<clinit>" -> declaringClass.getShortName()
        else -> name
    }
}

public inline fun getPrettyMethodName(m: SootMethodInterface): String {
    return getPrettyMethodName(m.getDeclaringClass(), m.getName())
}