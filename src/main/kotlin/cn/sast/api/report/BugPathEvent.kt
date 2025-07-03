package cn.sast.api.report

import cn.sast.api.util.ComparatorUtilsKt
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.collections.CollectionsKt

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/BugPathEvent\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
data class BugPathEvent(
    val message: Map<Language, String>,
    val classname: IBugResInfo,
    val region: Region,
    val stackDepth: Int? = null
) : Comparable<BugPathEvent>, IReportHashAble {

    override fun compareTo(other: BugPathEvent): Int {
        var result = ComparatorUtilsKt.compareToMap(this.message, other.message)
        if (result != 0) return result
        
        result = this.classname.compareTo(other.classname)
        if (result != 0) return result
        
        result = this.region.compareTo(other.region)
        if (result != 0) return result
        
        return 0
    }

    override fun reportHash(c: IReportHashCalculator): String {
        return "${this.classname.reportHash(c)}:${this.region}"
    }

    fun reportHashWithMessage(c: IReportHashCalculator): String {
        return "${this.reportHash(c)} ${CollectionsKt.toSortedSet(this.message.values)}"
    }

    override fun toString(): String {
        return "${this.classname} at ${this.region} ${this.message}"
    }

    operator fun component1(): Map<Language, String> = message
    operator fun component2(): IBugResInfo = classname
    operator fun component3(): Region = region
    operator fun component4(): Int? = stackDepth

    fun copy(
        message: Map<Language, String> = this.message,
        classname: IBugResInfo = this.classname,
        region: Region = this.region,
        stackDepth: Int? = this.stackDepth
    ): BugPathEvent {
        return BugPathEvent(message, classname, region, stackDepth)
    }

    override fun hashCode(): Int {
        return ((message.hashCode() * 31 + classname.hashCode()) * 31 + region.hashCode()) * 31 +
            (stackDepth?.hashCode() ?: 0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BugPathEvent) return false
        
        return message == other.message &&
            classname == other.classname &&
            region == other.region &&
            stackDepth == other.stackDepth
    }
}