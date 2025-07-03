package cn.sast.cli.command.tools

import cn.sast.framework.plugin.ConfigPluginLoader
import cn.sast.framework.plugin.PluginDefinitions
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging

/**
 * `--subtools` 命令行分组，用于列举规则 / CheckType
 */
class SubToolsOptions : OptionGroup("Sub tools Options") {

    private val json = Json { prettyPrint = true }
    private val logger = KotlinLogging.logger {}

    private val subtools by option("--subtools").flag(default = false)
    private val listRules by option("--list-rules").flag(default = false)
    private val listCheckTypes by option("--list-check-types").flag(default = false)

    fun run(loader: ConfigPluginLoader) {
        if (!subtools) return
        val defs = PluginDefinitions.load(loader.pluginManager)

        val checkTypes = defs.getCheckTypeDefinition(com.feysh.corax.config.api.CheckType::class.java)
            .map { it.singleton }
            .sortedBy { CheckerInfoGeneratorKt.getId(it) }

        if (listRules) {
            val ruleIds = checkTypes.map { CheckerInfoGeneratorKt.getId(it) }
            println(json.encodeToString(ruleIds))
        }

        if (listCheckTypes) {
            println(json.encodeToString(checkTypes.map { it.toString() }))
        }

        // 兼容旧逻辑：立即退出
        kotlin.system.exitProcess(0)
    }
}
