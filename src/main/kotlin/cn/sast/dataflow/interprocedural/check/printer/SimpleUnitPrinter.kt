package cn.sast.dataflow.interprocedural.check.printer

import kotlin.jvm.internal.SourceDebugExtension
import soot.AbstractUnitPrinter
import soot.SootClass
import soot.SootFieldRef
import soot.SootMethod
import soot.SootMethodInterface
import soot.SootMethodRef
import soot.Type
import soot.Unit
import soot.UnitPrinter
import soot.Value
import soot.jimple.CaughtExceptionRef
import soot.jimple.Constant
import soot.jimple.IdentityRef
import soot.jimple.InstanceInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.ParameterRef
import soot.jimple.StaticInvokeExpr
import soot.jimple.StringConstant
import soot.jimple.ThisRef

@SourceDebugExtension(["SMAP\nSimpleUnitPrinter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinter\n+ 2 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinterKt\n*L\n1#1,144:1\n17#2:145\n*S KotlinDebug\n*F\n+ 1 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinter\n*L\n57#1:145\n*E\n"])
class SimpleUnitPrinter : AbstractUnitPrinter() {
    private fun getQuotedStringOf(fromString: String): String {
        val fromStringLen = fromString.length
        val toStringBuffer = StringBuilder(fromStringLen + 20)
        toStringBuffer.append("\"")

        for (i in 0 until fromStringLen) {
            val ch = fromString[i]
            when (ch) {
                '\t' -> toStringBuffer.append("\\t")
                '\n' -> toStringBuffer.append("\\n")
                '\r' -> toStringBuffer.append("\\r")
                '"' -> toStringBuffer.append("\\\"")
                '\\' -> toStringBuffer.append("\\\\")
                else -> toStringBuffer.append(ch)
            }
        }

        toStringBuffer.append("\"")
        return toStringBuffer.toString()
    }

    override fun literal(s: String) {
        output.append(s)
    }

    override fun type(t: Type) {
        output.append(EventPrinterKt.getPname(t))
    }

    override fun constant(c: Constant?) {
        if (c is StringConstant) {
            handleIndent()
            output.append(getQuotedStringOf(c.value))
        } else {
            super.constant(c)
        }
    }

    override fun methodRef(m: SootMethodRef) {
        output.append(SimpleUnitPrinterKt.getPrettyMethodName((m as SootMethodInterface).declaringClass, (m as SootMethodInterface).name))
    }

    override fun fieldRef(f: SootFieldRef) {
        output.append(f.name())
    }

    override fun unitRef(u: Unit?, branchTarget: Boolean) {
    }

    override fun identityRef(r: IdentityRef) {
        when (r) {
            is ThisRef -> {
                literal("@this: ")
                type(r.type)
            }
            is ParameterRef -> {
                literal("@parameter${r.index}: ")
                type(r.type)
            }
            is CaughtExceptionRef -> {
                literal("@caughtexception")
            }
            else -> throw RuntimeException()
        }
    }

    override fun toString(): String {
        return output.toString()
    }

    protected fun handleIndent() {
        startOfLine = false
    }

    @SourceDebugExtension(["SMAP\nSimpleUnitPrinter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinter$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinterKt\n*L\n1#1,144:1\n1#2:145\n17#3:146\n17#3:147\n*S KotlinDebug\n*F\n+ 1 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinter$Companion\n*L\n115#1:146\n137#1:147\n*E\n"])
    companion object {
        fun getStringOf(unit: Unit): String {
            val it = SimpleUnitPrinter()
            unit.toString(it as UnitPrinter)
            return it.toString()
        }

        fun getStringOf(m: SootMethod?, lhs: Value?, invokeExpr: InvokeExpr?, short: Boolean = true): String {
            val up = SimpleUnitPrinter()
            if (invokeExpr != null) {
                if (lhs != null) {
                    lhs.toString(up as UnitPrinter)
                    up.literal(" = ")
                }

                when (invokeExpr) {
                    is InstanceInvokeExpr -> {
                        invokeExpr.baseBox.toString(up as UnitPrinter)
                        up.literal(".")
                    }
                    is StaticInvokeExpr -> {
                        val declaringClass = m?.declaringClass ?: invokeExpr.methodRef.declaringClass
                        up.literal(if (short) declaringClass.shortName else declaringClass.name)
                        up.literal(".")
                    }
                }

                val methodRef = invokeExpr.methodRef
                val methodInterface = methodRef as SootMethodInterface
                up.literal(SimpleUnitPrinterKt.getPrettyMethodName(methodInterface.declaringClass, methodInterface.name))
                up.literal("(")
                val args = invokeExpr.args
                for ((i, arg) in args.withIndex()) {
                    if (i != 0) {
                        up.literal(", ")
                    }
                    (arg as Value).toString(up as UnitPrinter)
                }
                up.literal(")")
            } else if (m != null) {
                if (lhs != null) {
                    lhs.toString(up as UnitPrinter)
                    up.literal(" = ")
                }

                if (m.isStatic) {
                    up.literal(m.declaringClass.shortName)
                } else {
                    up.literal("?")
                }

                up.literal(".")
                up.literal(SimpleUnitPrinterKt.getPrettyMethodName((m as SootMethodInterface).declaringClass, (m as SootMethodInterface).name))
                up.literal("(...)")
            }

            return up.toString()
        }
    }
}