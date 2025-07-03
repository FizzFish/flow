package cn.sast.dataflow.interprocedural.check

import cn.sast.common.interner.InternerEquiv
import cn.sast.common.interner.WeakInternerX
import com.github.benmanes.caffeine.cache.Interner
import soot.Unit

public sealed class IPath protected constructor() : InternerEquiv {
    public abstract val node: Unit

    public companion object {
        private val specialWeakInterner: Interner<Any> = Interner.newWeakInterner()
        private val weakInterner: WeakInternerX

        val interner: Any
            get() = weakInterner.intern(this)

        fun <T> specialInterner(v: T): T {
            return specialWeakInterner.intern(v) as T
        }
    }
}