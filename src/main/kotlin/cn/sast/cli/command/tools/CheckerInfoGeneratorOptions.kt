package cn.sast.cli.command.tools

import cn.sast.api.config.CheckerInfoGenResult
import cn.sast.framework.plugin.ConfigPluginLoader
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import mu.KLogger

public class CheckerInfoGeneratorOptions : OptionGroup("Generate checker_info.json for CoraxServer Options", null, 2) {
    private val genCheckerInfoJson: Boolean by lazy {
        this.genCheckerInfoJson$delegate.getValue(this, $$delegatedProperties[0]) as Boolean
    }

    private val language: List<String> by lazy {
        this.language$delegate.getValue(this, $$delegatedProperties[1]) as MutableList<String>
    }

    public fun run(pl: ConfigPluginLoader, rules: Set<String>?) {
        if (this.genCheckerInfoJson) {
            val generator = CheckerInfoGenerator.Companion.createCheckerInfoGenerator$default(
                CheckerInfoGenerator.Companion, pl, this.language, false, 4, null
            )
            if (generator == null) {
                System.exit(2)
                throw RuntimeException("System.exit returned normally, while it was supposed to halt JVM.")
            }

            val checkerInfo = CheckerInfoGenerator.getCheckerInfo$default(generator, false, 1, null)
            generator.dumpCheckerInfoJson(checkerInfo, true)
            generator.dumpRuleAndRuleMappingDB(checkerInfo, rules)
        }

        System.exit(0)
        throw RuntimeException("System.exit returned normally, while it was supposed to halt JVM.")
    }

    @JvmStatic
    fun `logger$lambda$0`() {
        return Unit
    }

    public companion object {
        private val logger: KLogger
    }
}