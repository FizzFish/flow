@file:SourceDebugExtension(["SMAP\nGrimpInline.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GrimpInline.kt\ncn/sast/dataflow/interprocedural/check/printer/GrimpInlineKt\n+ 2 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinterKt\n*L\n1#1,312:1\n17#2:313\n*S KotlinDebug\n*F\n+ 1 GrimpInline.kt\ncn/sast/dataflow/interprocedural/check/printer/GrimpInlineKt\n*L\n20#1:313\n*E\n"])

package cn.sast.dataflow.interprocedural.check.printer

import kotlin.jvm.internal.SourceDebugExtension
import soot.SootClass
import soot.SootMethodInterface
import soot.SootMethodRef
import soot.UnitPrinter
import soot.Value
import soot.jimple.InstanceInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.StaticInvokeExpr

public fun InvokeExpr.invokeToString(up: UnitPrinter) {
    if (this is InstanceInvokeExpr) {
        baseBox.toString(up)
        up.literal(".")
    }

    if (this is StaticInvokeExpr) {
        up.literal(methodRef.declaringClass.shortName)
        up.literal(".")
    }

    val methodRef = this.methodRef
    val args = methodRef as SootMethodInterface
    val declaringClass = args.declaringClass
    val methodName = args.name
    up.literal(SimpleUnitPrinterKt.getPrettyMethodName(declaringClass, methodName))
    up.literal("(")
    val arguments = this.args
    var i = 0

    for (e in args.size()) {
        if (i != 0) {
            up.literal(", ")
        }

        (arguments[i] as Value).toString(up)
        i++
    }

    up.literal(")")
}