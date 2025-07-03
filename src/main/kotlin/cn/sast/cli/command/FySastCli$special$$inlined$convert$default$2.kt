package cn.sast.cli.command

import cn.sast.common.IResFile
import cn.sast.common.Resource
import cn.sast.common.ResourceKt
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.options.OptionCallTransformContext
import com.github.ajalt.clikt.parameters.options.OptionKt
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.ArrayList
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nConvert.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Convert.kt\ncom/github/ajalt/clikt/parameters/options/OptionWithValuesKt__ConvertKt$convert$valueTransform$1\n+ 2 FySastCli.kt\ncn/sast/cli/command/FySastCli\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,164:1\n118#2,2:165\n120#2,5:168\n1#3:167\n1557#4:173\n1628#4,3:174\n774#4:177\n865#4,2:178\n*S KotlinDebug\n*F\n+ 1 FySastCli.kt\ncn/sast/cli/command/FySastCli\n*L\n124#1:173\n124#1:174,3\n124#1:177\n124#1:178,2\n*E\n"])
internal class `FySastCli$special$$inlined$convert$default$2`(
    private val `$this_convert`: OptionWithValues<*, *, *>
) : Lambda(), Function2<OptionCallTransformContext, String, List<String>> {
    override fun invoke(context: OptionCallTransformContext, it: String): List<String> {
        try {
            val option: String = `$this_convert`.getTransformValue().invoke(context, it) as String

            val file: IResFile? = try {
                Resource.INSTANCE.fileOf(option).takeIf { f -> f.getExists() && f.isFile() }
            } catch (e: Exception) {
                null
            }

            if (file != null) {
                val list = Gson().fromJson<List<String>>(
                    ResourceKt.readText(file, null, 1, null),
                    object : TypeToken<List<String>>() {}.type
                )
                if (list != null) {
                    return list
                }
            }

            val split = option.removeSurrounding("[", "]").split(",").map { s -> s.trim() }
            return split.filter { s -> s.isNotEmpty() }.toMutableList()
        } catch (e: UsageError) {
            val paramName = e.paramName ?: context.name.takeIf { n -> n.isNotEmpty() } ?: OptionKt.longestName(context.option)
            e.paramName = paramName
            throw e
        } catch (e: Exception) {
            context.fail(e.message ?: "")
            throw KotlinNothingValueException()
        }
    }
}