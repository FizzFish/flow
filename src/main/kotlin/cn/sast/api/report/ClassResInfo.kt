package cn.sast.api.report

import cn.sast.api.util.numCode
import cn.sast.api.util.sourcePath
import cn.sast.api.util.possibleSourceFiles
import soot.ClassMember
import soot.SootClass
import soot.SootMethod
import soot.SourceLocator
import soot.tagkit.Host
import java.util.LinkedHashSet

/**
 * 用 SootClass 解析源信息，为报告定位提供统一接口。
 */
data class ClassResInfo(val sc: SootClass) : IBugResInfo() {

    val maxLine: Int by lazy { sc.numCode }
    val sourcePath: String? by lazy { sc.sourcePath}
    val sourceFile: LinkedHashSet<String> by lazy { sc.possibleSourceFiles }

    override val path: String get() = sourceFile.firstOrNull() ?: sc.name
    override val reportFileName: String
        get() = sourcePath?.substringAfterLast('/') ?: "${SourceLocator.v().getSourceForClass(sc.shortJavaStyleName)}.java"

    /* -- Comparable & Hash-able -- */

    override fun compareTo(other: IBugResInfo): Int =
        if (other is ClassResInfo) sc.name.compareTo(other.sc.name)
        else this::class.simpleName!!.compareTo(other::class.simpleName!!)

    override fun reportHash(c: IReportHashCalculator): String = c.from(sc)

    /* -- 工厂方法 -- */

    companion object {
        fun of(sc: SootClass): ClassResInfo = ClassResInfo(sc)
        fun of(sm: SootMethod): ClassResInfo = ClassResInfo(sm.declaringClass)

        fun of(host: Host): ClassResInfo = when (host) {
            is SootClass -> of(host)
            is ClassMember -> of(host.declaringClass)
            else -> error("Unsupported Host: $host")
        }
    }
}
