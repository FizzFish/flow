package cn.sast.framework.plugin

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public sealed class ConfigSerializable protected constructor() : IConfig, Comparable<ConfigSerializable> {
    public abstract val name: String

    public open override operator fun compareTo(other: ConfigSerializable): Int {
        val otherClassName = other::class.java.name
        val thisClassName = this::class.java.name
        return if (otherClassName.compareTo(thisClassName) == 0) {
            this.name.compareTo(other.name)
        } else {
            0
        }
    }

    public companion object {
        public fun serializer(): KSerializer<ConfigSerializable> {
            return ConfigSerializable.serializer()
        }
    }
}