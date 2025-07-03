package cn.sast.api.config

import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.EncodeDefault.Mode

@Serializable
@SourceDebugExtension(["SMAP\nCheckerInfo.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerInfo.kt\ncn/sast/api/config/CheckerInfo\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,49:1\n1#2:50\n*E\n"])
public data class CheckerInfo(
    public val type: String,
    public val format_version: String,
    public val analyzer: String,
    public val language: String,
    public val checker_id: String,
    public val severity: String,
    public val category: Map<String, String>,
    public val name: Map<String, String>,
    public val `abstract`: Map<String, String>,
    public val description: MutableMap<String, String>,
    public val tags: List<Tag>,
    @EncodeDefault(mode = Mode.ALWAYS)
    public val impact: String? = null,
    @EncodeDefault(mode = Mode.ALWAYS)
    public val likelihood: String? = null,
    @EncodeDefault(mode = Mode.ALWAYS)
    public val precision: String? = null,
    @EncodeDefault(mode = Mode.ALWAYS)
    public val reCall: String? = null,
    @EncodeDefault(mode = Mode.ALWAYS)
    public val impl: String? = null,
    @EncodeDefault(mode = Mode.ALWAYS)
    public val implemented: Boolean? = null
) {
    public val chapterFlat: ChapterFlat?
        get() {
            val zhCN = this.category["zh-CN"]
            return if (zhCN != null) ChapterFlat(zhCN, this.severity, this.checker_id) else null
        }

    public fun copy(
        type: String = this.type,
        format_version: String = this.format_version,
        analyzer: String = this.analyzer,
        language: String = this.language,
        checker_id: String = this.checker_id,
        severity: String = this.severity,
        category: Map<String, String> = this.category,
        name: Map<String, String> = this.name,
        `abstract`: Map<String, String> = this.`abstract`,
        description: MutableMap<String, String> = this.description,
        tags: List<Tag> = this.tags,
        impact: String? = this.impact,
        likelihood: String? = this.likelihood,
        precision: String? = this.precision,
        reCall: String? = this.reCall,
        impl: String? = this.impl,
        implemented: Boolean? = this.implemented
    ): CheckerInfo {
        return CheckerInfo(
            type,
            format_version,
            analyzer,
            language,
            checker_id,
            severity,
            category,
            name,
            `abstract`,
            description,
            tags,
            impact,
            likelihood,
            precision,
            reCall,
            impl,
            implemented
        )
    }

    public companion object {
        public fun serializer(): KSerializer<CheckerInfo> {
            return CheckerInfo.serializer()
        }
    }
}