package cn.sast.cli.command

import cn.sast.common.IResDirectory
import com.github.ajalt.clikt.parameters.options.OptionTransformContext
import java.nio.file.Path
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nValidate.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt$validate$1\n+ 2 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt\n+ 3 FySastCli.kt\ncn/sast/cli/command/FySastCli\n+ 4 OptionWithValues.kt\ncom/github/ajalt/clikt/parameters/options/OptionTransformContext\n*L\n1#1,71:1\n69#2:72\n133#3,2:73\n132#3:76\n55#4:75\n56#4:77\n*S KotlinDebug\n*F\n+ 1 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt\n*L\n69#1:75\n69#1:77\n*E\n"])
internal class `FySastCli$special$$inlined$check$1` : Lambda(), Function2<OptionTransformContext, IResDirectory, Unit> {
    constructor() : super(2)

    internal fun <AllT, EachT, ValueT> OptionTransformContext.`<anonymous>`(it: AllT) {
        if (it != null) {
            val var10000: Path = (it as IResDirectory).getPath().toAbsolutePath()
            if (var10000.normalize().getNameCount() < 2) {
                this.fail("output is not allow here: ${it as IResDirectory}")
                throw kotlin.KotlinNothingValueException()
            }
        }
    }
}