package cn.sast.cli.command

import com.github.ajalt.clikt.parameters.options.OptionTransformContext
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nValidate.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt$validate$1\n+ 2 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt\n+ 3 FySastCli.kt\ncn/sast/cli/command/FySastCli\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 OptionWithValues.kt\ncom/github/ajalt/clikt/parameters/options/OptionTransformContext\n+ 6 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt$check$2\n*L\n1#1,71:1\n69#2:72\n195#3:73\n1734#4,3:74\n55#5:77\n56#5:79\n66#6:78\n*S KotlinDebug\n*F\n+ 1 FySastCli.kt\ncn/sast/cli/command/FySastCli\n+ 2 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt\n*L\n195#1:74,3\n69#2:77\n69#2:79\n*E\n"])
internal class `FySastCli$special$$inlined$check$default$1` : Lambda(), Function2<OptionTransformContext, List<String>, Unit> {
    constructor() : super(2)

    internal fun <AllT, EachT, ValueT> OptionTransformContext.`<anonymous>`(it: AllT) {
        if (it != null) {
            val `$this$all$iv`: Iterable<*> = it as List<*>
            var var10000: Boolean
            if (it is Collection<*> && (it as Collection<*>).isEmpty()) {
                var10000 = true
            } else {
                label42@ {
                    for (element$iv in `$this$all$iv`) {
                        if ((element$iv as String).isEmpty()) {
                            var10000 = false
                            break@label42
                        }
                    }
                    var10000 = true
                }
            }

            if (!var10000) {
                `$this$copy`.fail(it.toString())
                throw KotlinNothingValueException()
            }
        }
    }
}