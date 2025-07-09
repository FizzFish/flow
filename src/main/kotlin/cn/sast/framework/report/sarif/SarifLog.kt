package cn.sast.framework.report.sarif

import cn.sast.api.config.ExtSettings
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
/**
 * 顶层 SARIF 日志对象（v2.1.0）。
 *
 * @param schema  `$schema` 字段，指向 SARIF JSON Schema URL
 * @param version SARIF 版本号（通常为 "2.1.0"）
 * @param runs    若干扫描结果批次
 */
@Serializable
data class SarifLog(
    @SerialName("\$schema")
    val schema: String,
    val version: String,
    val runs: List<Run>,
) {

    /** 以项目统一的 JSON 配置序列化自身。 */
    fun toJson(): String = jsonFormat.encodeToString(this)

    companion object {
        /**
         * 项目级 SARIF 输出格式：
         * * **useArrayPolymorphism = true** —— 与官方示例保持一致
         * * **prettyPrint** – 由 [ExtSettings.prettyPrintJsonReport] 控制
         * * **encodeDefaults = false** – 省略默认值字段，缩减体积
         */
        val jsonFormat: Json = Json {
            useArrayPolymorphism = true
            prettyPrint = ExtSettings.prettyPrintJsonReport
            encodeDefaults = false
        }
    }
}
