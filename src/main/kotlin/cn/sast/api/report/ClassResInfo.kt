package cn.sast.api.report

import cn.sast.api.util.SootUtilsKt
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import soot.ClassMember
import soot.SootClass
import soot.SootMethod
import soot.SourceLocator
import soot.tagkit.Host

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/ClassResInfo\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
data class ClassResInfo(val sc: SootClass) : IBugResInfo() {
    private val maxLine$delegate by lazy { maxLine_delegate$lambda$0(this) }
    private val sourcePath$delegate by lazy { sourcePath_delegate$lambda$1(this) }
    private val sourceFile$delegate by lazy { sourceFile_delegate$lambda$2(this) }

    val maxLine: Int
        get() = maxLine$delegate

    val sourcePath: String?
        get() = sourcePath$delegate

    val sourceFile: LinkedHashSet<String>
        get() = sourceFile$delegate

    override val path: String
        get() = sourceFile.first().toString()

    override val reportFileName: String
        get() {
            val sourcePath = this.sourcePath
            if (sourcePath != null) {
                val parts = sourcePath.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                if (parts.isNotEmpty()) {
                    return parts.last()
                }
            }
            return "${SourceLocator.v().getSourceForClass(sc.shortJavaStyleName)}.java"
        }

    override fun compareTo(other: IBugResInfo): Int {
        if (other !is ClassResInfo) {
            return this::class.simpleName!!.compareTo(other::class.simpleName!!)
        }
        return sc.name.compareTo(other.sc.name).takeIf { it != 0 } ?: 0
    }

    override fun reportHash(c: IReportHashCalculator): String {
        return c.from(sc)
    }

    operator fun component1(): SootClass = sc

    fun copy(sc: SootClass = this.sc): ClassResInfo = ClassResInfo(sc)

    override fun toString(): String = "ClassResInfo(sc=$sc)"

    override fun hashCode(): Int = sc.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClassResInfo) return false
        return sc == other.sc
    }

    companion object {
        @JvmStatic
        private fun maxLine_delegate$lambda$0(`this$0`: ClassResInfo): Int =
            SootUtilsKt.getNumCode(`this$0`.sc)

        @JvmStatic
        private fun sourcePath_delegate$lambda$1(`this$0`: ClassResInfo): String? =
            SootUtilsKt.getSourcePath(`this$0`.sc)

        @JvmStatic
        private fun sourceFile_delegate$lambda$2(`this$0`: ClassResInfo): LinkedHashSet<String> =
            SootUtilsKt.getPossibleSourceFiles(`this$0`.sc)

        fun of(sc: SootClass): ClassResInfo = ClassResInfo(sc)

        fun of(sm: SootMethod): ClassResInfo = of(sm.declaringClass)

        fun of(sootDecl: Host): ClassResInfo {
            val sc = when (sootDecl) {
                is SootClass -> sootDecl
                is ClassMember -> sootDecl.declaringClass
                else -> throw IllegalStateException("Unsupported sootDecl type: $sootDecl ${sootDecl::class.java}")
            }
            return ClassResInfo(sc)
        }
    }
}