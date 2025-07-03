package cn.sast.framework.validator

import cn.sast.api.report.ReportKt
import com.feysh.corax.config.api.CheckType

internal data class RowCheckType(val type: CheckType) : RowType() {
    override fun toString(): String {
        return ReportKt.getPerfectName(this.type)
    }

    public operator fun component1(): CheckType {
        return this.type
    }

    public fun copy(type: CheckType = this.type): RowCheckType {
        return RowCheckType(type)
    }

    override fun hashCode(): Int {
        return this.type.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is RowCheckType) {
            return false
        } else {
            return this.type == other.type
        }
    }
}