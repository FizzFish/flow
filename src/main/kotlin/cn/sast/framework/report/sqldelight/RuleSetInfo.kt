package cn.sast.framework.report.sqldelight

public data class RuleSetInfo(
    val name: String,
    val language: String,
    val description: String?,
    val prefix: String?,
    val id_pattern: String?,
    val section_level: Long?,
    val version: String,
    val revision: String
) {
    public operator fun component1(): String {
        return this.name
    }

    public operator fun component2(): String {
        return this.language
    }

    public operator fun component3(): String? {
        return this.description
    }

    public operator fun component4(): String? {
        return this.prefix
    }

    public operator fun component5(): String? {
        return this.id_pattern
    }

    public operator fun component6(): Long? {
        return this.section_level
    }

    public operator fun component7(): String {
        return this.version
    }

    public operator fun component8(): String {
        return this.revision
    }

    public fun copy(
        name: String = this.name,
        language: String = this.language,
        description: String? = this.description,
        prefix: String? = this.prefix,
        id_pattern: String? = this.id_pattern,
        section_level: Long? = this.section_level,
        version: String = this.version,
        revision: String = this.revision
    ): RuleSetInfo {
        return RuleSetInfo(name, language, description, prefix, id_pattern, section_level, version, revision)
    }

    public override fun toString(): String {
        return "RuleSetInfo(name=${this.name}, language=${this.language}, description=${this.description}, prefix=${this.prefix}, id_pattern=${this.id_pattern}, section_level=${this.section_level}, version=${this.version}, revision=${this.revision})"
    }

    public override fun hashCode(): Int {
        return (
            (
                (
                    (
                        (
                            (this.name.hashCode() * 31 + this.language.hashCode()) * 31
                                + (this.description?.hashCode() ?: 0)
                        )
                            * 31
                            + (this.prefix?.hashCode() ?: 0)
                    )
                        * 31
                        + (this.id_pattern?.hashCode() ?: 0)
                )
                    * 31
                    + (this.section_level?.hashCode() ?: 0)
            )
                * 31
                + this.version.hashCode()
        )
            * 31
            + this.revision.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is RuleSetInfo) {
            return false
        }
        
        return name == other.name &&
            language == other.language &&
            description == other.description &&
            prefix == other.prefix &&
            id_pattern == other.id_pattern &&
            section_level == other.section_level &&
            version == other.version &&
            revision == other.revision
    }
}