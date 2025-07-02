package cn.sast.framework.plugin

import cn.sast.api.report.CheckType2StringKind
import com.feysh.corax.config.api.CheckType
import java.util.LinkedHashSet

private final val sort: LinkedHashSet<ConfigSerializable>
   private final get() {
      return CollectionsKt.toCollection(CollectionsKt.sorted(`$this$sort`), new LinkedHashSet()) as LinkedHashSet<ConfigSerializable>;
   }


public fun get1to1SpecialIdentifier(checkType: CheckType): String {
   return CheckType2StringKind.Companion.getCheckType2StringKind().getConvert().invoke(checkType) as java.lang.String;
}

@JvmSynthetic
fun `access$getSort`(`$receiver`: LinkedHashSet): LinkedHashSet {
   return getSort(`$receiver`);
}
