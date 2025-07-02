package cn.sast.common

import com.feysh.corax.config.api.CheckType

/**
 * Global registry of enabled [CheckType]s.
 */
object GLB {
    private val allTypes: MutableSet<CheckType> = LinkedHashSet()

    operator fun plusAssign(t: CheckType) {
        synchronized(allTypes) {
            allTypes.add(t)
        }
    }

    operator fun plusAssign(t: Collection<CheckType>) {
        synchronized(allTypes) {
            allTypes.addAll(t)
        }
    }

    fun get(): Set<CheckType> = allTypes
}
