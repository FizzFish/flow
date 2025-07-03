package cn.sast.api.report

import cn.sast.api.util.ComparatorUtilsKt
import com.feysh.corax.config.api.report.Region
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/BugPathPosition\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
data class BugPathPosition(val classname: IBugResInfo, val region: Region?) : Comparable<BugPathPosition> {
    override fun compareTo(other: BugPathPosition): Int {
        val classCompare = classname.compareTo(other.classname)
        if (classCompare != 0) {
            return classCompare
        }
        
        val regionCompare = ComparatorUtilsKt.compareToNullable(region, other.region)
        return if (regionCompare != 0) regionCompare else 0
    }

    override fun toString(): String {
        return "$classname $region"
    }

    operator fun component1(): IBugResInfo {
        return classname
    }

    operator fun component2(): Region? {
        return region
    }

    fun copy(classname: IBugResInfo = this.classname, region: Region? = this.region): BugPathPosition {
        return BugPathPosition(classname, region)
    }

    override fun hashCode(): Int {
        return classname.hashCode() * 31 + (region?.hashCode() ?: 0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is BugPathPosition) {
            return false
        }
        return classname == other.classname && region == other.region
    }
}