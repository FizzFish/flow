package cn.sast.framework

import java.lang.reflect.Field
import java.util.LinkedList
import soot.Scene

private val Scene.excludedPackages: LinkedList<String>
    get() {
        val field: Field = this::class.java.getDeclaredField("excludedPackages")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as LinkedList<String>
    }

val Scene.sootCtx: SootCtx
    get() = SootCtx(this)

@JvmSynthetic
internal fun access$getExcludedPackages(receiver: Scene): LinkedList<String> {
    return receiver.excludedPackages
}