package cn.sast.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.options.OptionTransformContext
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nEagerOption.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EagerOption.kt\ncom/github/ajalt/clikt/parameters/options/EagerOptionKt$versionOption$2\n+ 2 EagerOption.kt\ncom/github/ajalt/clikt/parameters/options/EagerOptionKt$versionOption$1\n*L\n1#1,75:1\n73#2:76\n*E\n"])
internal class `FySastCli$special$$inlined$versionOption$default$1`(
    private val `$version`: String,
    private val `$this_versionOption$inlined`: CliktCommand
) : Lambda(), Function1<OptionTransformContext, Unit> {
    override fun invoke(p1: OptionTransformContext) {
        val it: String = `$version`
        throw PrintMessage("${`$this_versionOption$inlined`.commandName} version $it", 0, false, 6, null)
    }
}