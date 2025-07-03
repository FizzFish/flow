package cn.sast.api.report

import cn.sast.common.IResFile
import kotlin.enums.EnumEntries

public data class ExpectBugAnnotationData<BugT>(
    val file: IResFile,
    val line: Int,
    val column: Int,
    val bug: Any,
    val kind: Kind
) {
    init {
        this.bug = bug as BugT
    }

    override fun toString(): String {
        return "file: ${this.file}:${this.line}:${this.column} kind: ${this.kind} a bug: ${this.bug}"
    }

    public operator fun component1(): IResFile {
        return this.file
    }

    public operator fun component2(): Int {
        return this.line
    }

    public operator fun component3(): Int {
        return this.column
    }

    public operator fun component4(): Any {
        return this.bug
    }

    public operator fun component5(): Kind {
        return this.kind
    }

    public fun copy(
        file: IResFile = this.file,
        line: Int = this.line,
        column: Int = this.column,
        bug: Any = this.bug,
        kind: Kind = this.kind
    ): ExpectBugAnnotationData<Any> {
        return ExpectBugAnnotationData(file, line, column, bug as BugT, kind)
    }

    override fun hashCode(): Int {
        return (
            ((this.file.hashCode() * 31 + Integer.hashCode(this.line)) * 31 + Integer.hashCode(this.column)) * 31
            + (this.bug?.hashCode() ?: 0)
        ) * 31 + this.kind.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is ExpectBugAnnotationData<*>) {
            return false
        } else {
            if (this.file != other.file) {
                return false
            } else if (this.line != other.line) {
                return false
            } else if (this.column != other.column) {
                return false
            } else if (this.bug != other.bug) {
                return false
            } else {
                return this.kind == other.kind
            }
        }
    }

    public enum class Kind {
        Expect,
        Escape;

        @JvmStatic
        fun getEntries(): EnumEntries<Kind> {
            return entries
        }
    }
}