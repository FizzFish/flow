package cn.sast.api.util

import sun.misc.Unsafe

public object UnsafeProvider {
    public val unsafe: Unsafe = unsafeInternal

    private val unsafeInternal: Unsafe
        get() {
            try {
                return Unsafe.getUnsafe().also {
                    kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(it, "getUnsafe(...)")
                }
            } catch (e: SecurityException) {
                for (field in Unsafe::class.java.declaredFields) {
                    if (field.type == Unsafe::class.java) {
                        field.isAccessible = true
                        return (field.get(null) as Unsafe).also {
                            kotlin.jvm.internal.Intrinsics.checkNotNull(it, "null cannot be cast to non-null type sun.misc.Unsafe")
                        }
                    }
                }
                throw IllegalStateException(
                    "Failed to find Unsafe member on Unsafe class, have: ${
                        Unsafe::class.java.declaredFields.contentDeepToString()
                    }"
                )
            } catch (e: Exception) {
                throw IllegalStateException("Failed to access Unsafe member on Unsafe class", e)
            }
        }
}