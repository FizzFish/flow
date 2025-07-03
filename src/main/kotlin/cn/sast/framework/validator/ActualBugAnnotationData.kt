package cn.sast.framework.validator

import cn.sast.api.report.ReportKt
import cn.sast.common.IResFile
import com.feysh.corax.config.api.CheckType

internal data class ActualBugAnnotationData(
    val file: IResFile,
    val line: Int,
    val checkType: CheckType
) {
    override fun toString(): String {
        this.checkType.getBugMessage()
        return "${this.file}:${this.line} report a [${ReportKt.getPerfectName(this.checkType)}]"
    }

    operator fun component1(): IResFile = file

    operator fun component2(): Int = line

    operator fun component3(): CheckType = checkType

    fun copy(
        file: IResFile = this.file,
        line: Int = this.line,
        checkType: CheckType = this.checkType
    ): ActualBugAnnotationData = ActualBugAnnotationData(file, line, checkType)

    override fun hashCode(): Int =
        (file.hashCode() * 31 + line.hashCode()) * 31 + checkType.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ActualBugAnnotationData) return false

        return file == other.file &&
                line == other.line &&
                checkType == other.checkType
    }
}