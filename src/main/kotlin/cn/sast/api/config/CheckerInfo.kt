package cn.sast.api.config

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode
import kotlinx.serialization.Serializable

/**
 * 检查器元信息。
 *
 * * 保留了全部字段、`@EncodeDefault` 行为和运行时逻辑
 * * 依赖 Kotlin `data class` 自动生成 `copy / componentN / toString / hashCode / equals`
 * * `@Serializable` 已自动在 `Companion` 中注入 `serializer()`，无需手写
 */
@Serializable
data class CheckerInfo(
    val type: String,
    val format_version: String,
    val analyzer: String,
    val language: String,
    val checker_id: String,
    val severity: String,
    val category: Map<String, String>,
    val name: Map<String, String>,
    val `abstract`: Map<String, String>,
    val description: MutableMap<String, String>,
    val tags: List<Tag>,

    @EncodeDefault(Mode.ALWAYS) val impact: String? = null,
    @EncodeDefault(Mode.ALWAYS) val likelihood: String? = null,
    @EncodeDefault(Mode.ALWAYS) val precision: String? = null,
    @EncodeDefault(Mode.ALWAYS) val reCall: String? = null,
    @EncodeDefault(Mode.ALWAYS) val impl: String? = null,
    @EncodeDefault(Mode.ALWAYS) val implemented: Boolean? = null
) {

    /** 提取中文分类、严重级别与 CheckerId 以便在侧边栏等处扁平展示。 */
    val chapterFlat: ChapterFlat?
        get() = category["zh-CN"]?.let { ChapterFlat(it, severity, checker_id) }
}
