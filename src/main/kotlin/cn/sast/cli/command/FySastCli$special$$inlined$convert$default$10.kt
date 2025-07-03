package cn.sast.cli.command

import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.options.OptionCallTransformContext
import com.github.ajalt.clikt.parameters.options.OptionKt
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import java.nio.charset.Charset
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nConvert.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Convert.kt\ncom/github/ajalt/clikt/parameters/options/OptionWithValuesKt__ConvertKt$convert$valueTransform$1\n+ 2 FySastCli.kt\ncn/sast/cli/command/FySastCli\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,164:1\n385#2:165\n1#3:166\n*E\n"])
internal class `FySastCli$special$$inlined$convert$default$10`(
    private val `$this_convert`: OptionWithValues<*, *>
) : Lambda(), Function2<OptionCallTransformContext, String, Charset> {
    init {
        super(2)
    }

    override fun invoke(context: OptionCallTransformContext, it: String): Charset {
        try {
            return Charset.forName(`$this_convert`.transformValue.invoke(context, it) as String)
        } catch (var13: UsageError) {
            var var10000: UsageError = var13
            var var15: String? = var13.paramName
            if (var15 == null) {
                val var8: String = context.name
                val var12: Boolean = var8.isEmpty()
                var10000 = var13
                var15 = if (!var12) var8 else null
                if ((if (!var12) var8 else null) == null) {
                    var15 = OptionKt.longestName(`$this_convert`.option)
                }
            }

            var10000.paramName = var15
            throw var13
        } catch (var14: Exception) {
            var var10001: String = var14.message ?: ""
            context.fail(var10001)
            throw kotlin.KotlinNothingValueException()
        }
    }
}