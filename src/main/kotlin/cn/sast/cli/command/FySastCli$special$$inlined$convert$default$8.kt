package cn.sast.cli.command

import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.options.OptionCallTransformContext
import com.github.ajalt.clikt.parameters.options.OptionKt
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nConvert.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Convert.kt\ncom/github/ajalt/clikt/parameters/options/OptionWithValuesKt__ConvertKt$convert$valueTransform$1\n+ 2 FySastCli.kt\ncn/sast/cli/command/FySastCli\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,164:1\n278#2:165\n1#3:166\n*E\n"])
internal class `FySastCli$special$$inlined$convert$default$8`(
    private val `$this_convert`: OptionWithValues<*, *, *>
) : Lambda(),
    Function2<OptionCallTransformContext, String, List<String>> {

    override fun invoke(context: OptionCallTransformContext, it: String): List<String> {
        try {
            return Gson()
                .fromJson(
                    context.`$this_convert`.transformValue.invoke(context, it) as File).readText(),
                    object : TypeToken<List<String>>() {}.type
                ) as List<String>
        } catch (e: UsageError) {
            val paramName = e.paramName ?: run {
                val name = context.getName()
                if (name.isNotEmpty()) name else OptionKt.longestName(context.option)
            }
            e.paramName = paramName
            throw e
        } catch (e: Exception) {
            context.fail(e.message ?: "")
            throw kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
        }
    }
}