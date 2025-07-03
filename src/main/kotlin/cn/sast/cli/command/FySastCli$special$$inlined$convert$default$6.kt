package cn.sast.cli.command

import cn.sast.common.IResDirectory
import cn.sast.common.Resource
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.options.OptionCallTransformContext
import com.github.ajalt.clikt.parameters.options.OptionKt
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nConvert.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Convert.kt\ncom/github/ajalt/clikt/parameters/options/OptionWithValuesKt__ConvertKt$convert$valueTransform$1\n+ 2 FySastCli.kt\ncn/sast/cli/command/FySastCli\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,164:1\n237#2:165\n1#3:166\n*E\n"])
internal class `FySastCli$special$$inlined$convert$default$6`(
    private val `$this_convert`: OptionWithValues<*, *, *>
) : Lambda(), Function2<OptionCallTransformContext, String, IResDirectory> {
    init {
        super(2)
    }

    override fun invoke(context: OptionCallTransformContext, it: String): IResDirectory {
        try {
            val itx: String = `$this_convert`.transformValue.invoke(context, it) as String
            if (itx.isEmpty()) {
                throw IllegalStateException("Check failed.")
            } else {
                return Resource.INSTANCE.dirOf(itx)
            }
        } catch (e: UsageError) {
            var paramName = e.paramName
            if (paramName == null) {
                val name = context.name
                val isEmpty = name.isEmpty()
                paramName = if (!isEmpty) name else null
                if (paramName == null) {
                    paramName = OptionKt.longestName(`$this_convert`.option)
                }
            }

            e.paramName = paramName
            throw e
        } catch (e: Exception) {
            val message = e.message ?: ""
            context.fail(message)
            throw KotlinNothingValueException()
        }
    }
}