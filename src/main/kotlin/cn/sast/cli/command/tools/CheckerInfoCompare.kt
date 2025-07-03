package cn.sast.cli.command.tools

import cn.sast.api.config.CheckerInfo
import cn.sast.common.IResFile
import java.nio.file.Path
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import mu.KLogger
import kotlin.collections.CollectionsKt

@SourceDebugExtension(["SMAP\nCheckerInfoCompare.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerInfoCompare.kt\ncn/sast/cli/command/tools/CheckerInfoCompare\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,59:1\n1#2:60\n774#3:61\n865#3,2:62\n1557#3:64\n1628#3,3:65\n774#3:68\n865#3,2:69\n774#3:71\n865#3,2:72\n1557#3:74\n1628#3,3:75\n1187#3,2:78\n1261#3,4:80\n1628#3,3:84\n774#3:87\n865#3,2:88\n*S KotlinDebug\n*F\n+ 1 CheckerInfoCompare.kt\ncn/sast/cli/command/tools/CheckerInfoCompare\n*L\n28#1:61\n28#1:62,2\n28#1:64\n28#1:65,3\n29#1:68\n29#1:69,2\n31#1:71\n31#1:72,2\n34#1:74\n34#1:75,3\n44#1:78,2\n44#1:80,4\n47#1:84,3\n48#1:87\n48#1:88,2\n*E\n"])
public class CheckerInfoCompare {
    public fun compareWith(output: Path, left: IResFile, right: IResFile) {
        TODO("FIXME — Couldn't be decompiled")
    }

    @JvmStatic
    fun `compareWith$lambda$2`(`$left`: IResFile, `$right`: IResFile, `$out`: Path): Any {
        return "The computed diff between '${`$left`}' and '${`$right`}' yields the following result: ${`$out`}"
    }

    @JvmStatic
    fun `compareWith$lambda$9$lambda$8`(`$deletedIds`: List<*>): Any {
        return "[-] Deleted ${`$deletedIds`.size} checker ids. ${
            CollectionsKt.joinToString$default(
                `$deletedIds`, "\n\t", "[\n\t", "\n]", 0, null, null, 56, null
            )
        }"
    }

    @JvmStatic
    fun `compareWith$lambda$16$lambda$15`(`$new`: List<*>): Any {
        return "[+] New ${`$new`.size} checker ids: ${
            CollectionsKt.joinToString$default(
                `$new`, "\n\t", "[\n\t", "\n]", 0, null, null, 56, null
            )
        }"
    }

    @JvmStatic
    fun `logger$lambda$17`(): Unit {
        return Unit.INSTANCE
    }

    public companion object {
        private val logger: KLogger
            get() = TODO("FIXME — Couldn't be decompiled")
        private val jsonFormat: Json
            get() = TODO("FIXME — Couldn't be decompiled")
        private val infoSerializer: KSerializer<List<CheckerInfo>>
            get() = TODO("FIXME — Couldn't be decompiled")
    }
}