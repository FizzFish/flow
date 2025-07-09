package cn.sast.cli.command.tools

import cn.sast.framework.plugin.ConfigPluginLoader
import cn.sast.framework.plugin.PluginDefinitions
import com.feysh.corax.config.api.CheckType
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.boolean
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.system.exitProcess

class SubToolsOptions : OptionGroup("Sub tools Options") {

    private val subtools: Boolean by option("--subtools", help = "Enable subtools mode").boolean().required()
    private val listRules by option("--list-rules", help = "Print all rules").flag()
    private val listCheckTypes by option("--list-check-types", help = "Print all check types").flag()

    fun run(pl: ConfigPluginLoader) {
        if (!subtools) return

        val jsonFormat = Json {
            useArrayPolymorphism = true
            prettyPrint = true
        }

        val pluginDefinitions by lazy { PluginDefinitions.load(pl.pluginManager) }

        val checkTypes by lazy {
            pluginDefinitions.getCheckTypeDefinition(CheckType::class.java)
                .map { it.singleton }
                .sortedBy { it.toString() }
        }

        if (listRules) {
            val ruleIds = checkTypes.map { CheckerInfoGenerator.getId(it) } // CheckerInfoGenerator assumed existing
            println(jsonFormat.encodeToString(ListSerializer(String.serializer()), ruleIds))
        }

        if (listCheckTypes) {
            val checkTypeNames = checkTypes.map { it.toString() }
            println(jsonFormat.encodeToString(ListSerializer(String.serializer()), checkTypeNames))
        }

        exitProcess(0)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}