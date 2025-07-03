package cn.sast.cli.command

import com.github.ajalt.clikt.parameters.options.OptionTransformContext
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nValidate.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt$validate$1\n+ 2 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt\n+ 3 FySastCli.kt\ncn/sast/cli/command/FySastCli\n+ 4 OptionWithValues.kt\ncom/github/ajalt/clikt/parameters/options/OptionTransformContext\n+ 5 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt$check$2\n*L\n1#1,71:1\n69#2:72\n381#3:73\n55#4:74\n56#4:76\n66#5:75\n*S KotlinDebug\n*F\n+ 1 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt\n*L\n69#1:74\n69#1:76\n*E\n"])
internal class `FySastCli$special$$inlined$check$default$5` : Lambda(), Function2<OptionTransformContext, Int, Unit> {
    constructor() : super(2)

    internal fun <AllT, EachT, ValueT> OptionTransformContext.`<anonymous>`(it: AllT) {
        if (it != null) {
            val itx: Int = (it as Number).toInt()
            if (1 > itx || itx >= 3) {
                this.fail(it.toString())
                throw kotlin.KotlinNothingValueException()
            }
        }
    }
}