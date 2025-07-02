package cn.sast.cli.command.tools

import com.feysh.corax.config.api.CheckType
import java.util.Comparator
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2\n+ 2 CheckerInfoGenerator.kt\ncn/sast/cli/command/tools/CheckerInfoGenerator\n*L\n1#1,102:1\n389#2:103\n*E\n"])
internal class `CheckerInfoGenerator$getCheckerInfo$lambda$56$$inlined$sortedBy$1`<T> : Comparator {
   // QF: local property
internal fun <T> `<anonymous>`(a: T, b: T): Int {
      return ComparisonsKt.compareValues(CheckerInfoGeneratorKt.getId(a as CheckType), CheckerInfoGeneratorKt.getId(b as CheckType));
   }
}
