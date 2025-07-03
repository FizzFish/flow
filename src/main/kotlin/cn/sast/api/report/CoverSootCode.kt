package cn.sast.api.report

import soot.SootClass
import soot.SootMethod
import soot.Unit

public sealed class CoverSootCode protected constructor(
    public open val method: SootMethod,
    public open val unit: Unit
) : CoverData() {
    public val clazz: SootClass
        get() = method.declaringClass

    public val className: String
        get() = clazz.name

    public val lineNumber: Int
        get() = unit.javaSourceStartLineNumber
}