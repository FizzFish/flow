package cn.sast.api.report

import cn.sast.common.IResFile
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/FileResInfo\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
class FileResInfo(sourcePath: IResFile) : IBugResInfo() {
    val sourcePath: IResFile = sourcePath
    private val abs$delegate by lazy { abs_delegate$lambda$0(this) }

    val abs: IResFile
        get() = abs$delegate

    open val reportFileName: String
        get() = sourcePath.getName()

    open val path: String
        get() = StringsKt.replace$default(abs.toString(), ":", "-", false, 4, null)

    override fun reportHash(c: IReportHashCalculator): String {
        return c.fromAbsPath(abs)
    }

    override fun hashCode(): Int {
        return abs.toString().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is FileResInfo && abs.toString() == other.abs.toString()
    }

    open operator fun compareTo(other: IBugResInfo): Int {
        if (other !is FileResInfo) {
            return this::class.simpleName!!.compareTo(other::class.simpleName!!)
        } else {
            val cmp = sourcePath.compareTo(other.sourcePath)
            return if (cmp != 0) cmp else 0
        }
    }

    override fun toString(): String {
        return "FileResInfo(file=$abs)"
    }

    companion object {
        @JvmStatic
        fun abs_delegate$lambda$0(this$0: FileResInfo): IResFile {
            return this$0.sourcePath.getAbsolute().getNormalize()
        }
    }
}