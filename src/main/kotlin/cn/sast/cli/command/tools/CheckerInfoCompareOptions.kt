package cn.sast.cli.command.tools

import cn.sast.common.IResFile
import cn.sast.common.Resource
import com.github.ajalt.clikt.core.ParameterHolder
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.OptionWithValuesKt
import com.github.ajalt.clikt.parameters.types.FileKt
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.Arrays
import mu.KLogger
import kotlin.Lazy
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.lazy

public class CheckerInfoCompareOptions : OptionGroup("Compare checker_info.json Options", null, 2) {
    private val compareRight: File by OptionWithValuesKt.required(
        FileKt.file$default(
            OptionWithValuesKt.option$default(
                this as ParameterHolder,
                emptyArray(),
                "Compare and diff an other checker_info.json",
                null,
                false,
                null,
                null,
                null,
                null,
                false,
                508,
                null
            ),
            true,
            false,
            false,
            false,
            false,
            false,
            58,
            null
        )
    )

    private val compareLeft: File by OptionWithValuesKt.required(
        FileKt.file$default(
            OptionWithValuesKt.option$default(
                this as ParameterHolder,
                emptyArray(),
                "Compare and diff an other checker_info.json",
                null,
                false,
                null,
                null,
                null,
                null,
                false,
                508,
                null
            ),
            true,
            false,
            false,
            false,
            false,
            false,
            58,
            null
        )
    )

    public fun run() {
        val output$delegate: Lazy<Path> = lazy(NONE) { run$lambda$3(this) }
        val var10000 = CheckerInfoCompare()
        val var10001 = run$lambda$4(output$delegate)
        val var10002 = Resource.INSTANCE
        val var10003 = compareLeft.path
        val var2: IResFile = var10002.fileOf(var10003)
        val var3 = Resource.INSTANCE
        val var10004 = compareRight.path
        var10000.compareWith(var10001, var2, var3.fileOf(var10004))
        System.exit(0)
        throw RuntimeException("System.exit returned normally, while it was supposed to halt JVM.")
    }

    @JvmStatic
    private fun run$lambda$3$lambda$2$lambda$1(it: Path): Any {
        return "The compare output path is: $it"
    }

    @JvmStatic
    private fun run$lambda$3(this$0: CheckerInfoCompareOptions): Path {
        var var1: Path = this$0.compareLeft.toPath().parent.resolve("compare-result")
        val var10001 = emptyArray<LinkOption>()
        if (!Files.exists(var1, Arrays.copyOf(var10001, var10001.size))) {
            Files.createDirectories(var1)
        }

        var1 = var1.normalize()
        logger.info { run$lambda$3$lambda$2$lambda$1(var1) }
        return var1
    }

    @JvmStatic
    private fun run$lambda$4(output$delegate: Lazy<Path>): Path {
        return output$delegate.value
    }

    @JvmStatic
    private fun logger$lambda$5() {
        return Unit
    }

    public companion object {
        private val logger: KLogger
    }
}