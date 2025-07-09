package cn.sast.framework.plugin

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * All configuration DTOs that can appear in the SA‑Configuration YAML extend this sealed base
 * class.  It provides a **natural ordering** that first sorts by *concrete class name* and then
 * by [name] (case‑sensitive) within the same class.
 */
@Serializable
sealed class ConfigSerializable : IConfig, Comparable<ConfigSerializable> {

    /** Human‑readable unique identifier (enforced by concrete subclasses). */
    abstract val name: String

    @Transient private val className: String = javaClass.name

    /** Class‑then‑name ordering so we get deterministic output when serialising. */
    final override fun compareTo(other: ConfigSerializable): Int =
        (className compareTo other.className).takeIf { it != 0 } ?: name.compareTo(other.name)
}