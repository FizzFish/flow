package cn.sast.framework.engine

import cn.sast.api.report.IBugResInfo
import com.feysh.corax.config.api.BugMessage.Env

public data class PreAnalysisReportEnv(
    public val file: IBugResInfo,
    public val env: Env
) {
    public operator fun component1(): IBugResInfo {
        return this.file
    }

    public operator fun component2(): Env {
        return this.env
    }

    public fun copy(file: IBugResInfo = this.file, env: Env = this.env): PreAnalysisReportEnv {
        return PreAnalysisReportEnv(file, env)
    }

    public override fun toString(): String {
        return "PreAnalysisReportEnv(file=${this.file}, env=${this.env})"
    }

    public override fun hashCode(): Int {
        return this.file.hashCode() * 31 + this.env.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is PreAnalysisReportEnv) {
            return false
        } else {
            val var2: PreAnalysisReportEnv = other
            if (!(this.file == other.file)) {
                return false
            } else {
                return this.env == var2.env
            }
        }
    }
}