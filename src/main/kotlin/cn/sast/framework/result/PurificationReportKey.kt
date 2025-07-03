package cn.sast.framework.result

import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.IBugResInfo
import org.utbot.common.Maybe
import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.Ref.ObjectRef

data class PurificationReportKey(
    val bugResFile: IBugResInfo,
    val line: Int,
    val checkName: String,
    val firstEvent: BugPathEvent
) {
    operator fun component1(): IBugResInfo = bugResFile

    operator fun component2(): Int = line

    operator fun component3(): String = checkName

    operator fun component4(): BugPathEvent = firstEvent

    fun copy(
        bugResFile: IBugResInfo = this.bugResFile,
        line: Int = this.line,
        checkName: String = this.checkName,
        firstEvent: BugPathEvent = this.firstEvent
    ): PurificationReportKey = PurificationReportKey(bugResFile, line, checkName, firstEvent)

    override fun toString(): String =
        "PurificationReportKey(bugResFile=$bugResFile, line=$line, checkName=$checkName, firstEvent=$firstEvent)"

    override fun hashCode(): Int =
        ((bugResFile.hashCode() * 31 + Integer.hashCode(line)) * 31 + checkName.hashCode()) * 31 + firstEvent.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PurificationReportKey) return false

        return bugResFile == other.bugResFile &&
                line == other.line &&
                checkName == other.checkName &&
                firstEvent == other.firstEvent
    }
}