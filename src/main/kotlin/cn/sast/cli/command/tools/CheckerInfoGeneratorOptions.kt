package cn.sast.cli.command.tools

import cn.sast.framework.plugin.ConfigPluginLoader
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlin.io.path.Path

class CheckerInfoGeneratorOptions : CliktCommand(name = "gen-checker-info", help = "Generate checker_info.json + aux artefacts") {
    private val root  by option("--root", help = "<Corax>/resources/checker_info dir").required()
    private val out   by option("--out",  help = "Output directory (will be created)").required()
    private val langs by option("--lang", help = "Comma‑separated languages (default: zh-CN,en-US)")
    private val dumpOnlyJson by option("--json-only").flag(default = false)

    override fun run() {
        val loader = ConfigPluginLoader() // assumes default plugin discovery
        val gen = CheckerInfoGenerator(
            languages = langs?.split(',') ?: listOf("zh-CN","en-US"),
            outputDir = Path(out),
            resRoot   = Path(root),
            plugin    = loader.pluginDefinitions
        )
        val res = gen.generate(abortOnError = true)
        if (!dumpOnlyJson) {
            // Future: gen.dumpRuleChaptersYaml(res); gen.dumpRuleAndRuleMappingDB(res, null)
        }
        echo("checker_info.json generated successfully → ${Path(root).resolve("checker_info.json")}")
    }
}