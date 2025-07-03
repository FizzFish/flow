package cn.sast.framework.report

import java.lang.reflect.Field
import kotlin.jvm.internal.Intrinsics

@JvmSynthetic
public inline fun <reified T> Any.getField(name: String): T? {
    val it: Field = this.javaClass.getDeclaredField(name)
    it.isAccessible = true
    val var10000: Any = it.get(this)
    Intrinsics.reifiedOperationMarker(2, "T")
    return var10000 as T
}