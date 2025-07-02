package cn.sast.framework.report

import java.lang.reflect.Field
import kotlin.jvm.internal.Intrinsics

@JvmSynthetic
public inline fun <reified T> Any.getField(name: String): T? {
   val it: Field = `$this$getField`.getClass().getDeclaredField(name);
   it.setAccessible(true);
   val var10000: Any = it.get(`$this$getField`);
   Intrinsics.reifiedOperationMarker(2, "T");
   return (T)(var10000 as Any);
}
