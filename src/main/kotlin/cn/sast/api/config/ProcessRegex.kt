package cn.sast.api.config

import com.feysh.corax.config.api.rules.ProcessRule
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * YAML 中 “process-regex” 节点的模型：
 *
 * ```yaml
 * process-regex:
 *   class:     []   # → clazzRules
 *   classpath: []   # → classpathRules
 *   file:      []   # → fileRules
 * ```
 *
 * * 三个字段都是列表，元素类型分别对应
 *   `ProcessRule.ClassMemberMatch / ClassPathMatch / FileMatch`
 * * 定义在 `@Serializable` data class 中，自动生成
 *   `copy / equals / hashCode / toString / componentN` 等
 */
@Serializable
data class ProcessRegex(

    @SerialName("class")
    val clazzRules: List<ProcessRule.ClassMemberMatch> =
        emptyList(),                                            // 默认：无规则

    @SerialName("classpath")
    val classpathRules: List<ProcessRule.ClassPathMatch> =
    // 默认：把 MainConfig.excludeFiles 转成 (-)path=xxx 规则
        MainConfig.excludeFiles.map {
            ProcessRule.InlineRuleStringSerialize.deserializeMatchFromLineString(
                serializer(), "(-)path=$it"
            )
        },

    @SerialName("file")
    val fileRules: List<ProcessRule.FileMatch> =
        emptyList()                                             // 默认：无规则
) {

    init {
        // 反编译里调用 ProjectConfigKt.validate(...)，这里沿用
        clazzRules.validate()
        classpathRules.validate()
        fileRules.validate()
    }
}
