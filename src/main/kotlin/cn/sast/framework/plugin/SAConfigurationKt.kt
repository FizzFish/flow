package cn.sast.framework.plugin

import cn.sast.api.report.CheckType2StringKind
import com.feysh.corax.config.api.CheckType
import java.util.LinkedHashSet

private val sort: LinkedHashSet<ConfigSerializable>
    get() = CollectionsKt.toCollection(CollectionsKt.sorted(this.sort), LinkedHashSet()) as LinkedHashSet<ConfigSerializable>

fun get1to1SpecialIdentifier(checkType: CheckType): String {
    return CheckType2StringKind.Companion.getCheckType2StringKind().getConvert().invoke(checkType) as String
}

@JvmSynthetic
internal fun access$getSort(receiver: LinkedHashSet<*>): LinkedHashSet<*> {
    return getSort(receiver)
}