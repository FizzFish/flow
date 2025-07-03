package cn.sast.framework.plugin

import com.feysh.corax.config.api.SAOptions
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("SootOptionsConfig")
@SourceDebugExtension(["SMAP\nSAConfiguration.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/SootOptionsConfig\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,557:1\n1#2:558\n*E\n"])
public data class SootOptionsConfig(
    public override val name: String,
    public override val enable: Boolean,
    public override val options: SAOptions?
) : ConfigSerializable(), IOptional, IFieldOptions {

    public override operator fun compareTo(other: ConfigSerializable): Int {
        if (other !is SootOptionsConfig) {
            return super.compareTo(other)
        } else {
            val var3 = super.compareTo(other)
            val var2 = if (var3 != 0) var3 else null
            if (var2 != null) {
                return var2
            } else if (this.enable != other.enable) {
                return if (this.enable) -1 else 1
            } else {
                return 0
            }
        }
    }

    public operator fun component1(): String {
        return this.name
    }

    public operator fun component2(): Boolean {
        return this.enable
    }

    public operator fun component3(): SAOptions? {
        return this.options
    }

    public fun copy(name: String = this.name, enable: Boolean = this.enable, options: SAOptions? = this.options): SootOptionsConfig {
        return SootOptionsConfig(name, enable, options)
    }

    public override fun toString(): String {
        return "SootOptionsConfig(name=${this.name}, enable=${this.enable}, options=${this.options})"
    }

    public override fun hashCode(): Int {
        return (this.name.hashCode() * 31 + this.enable.hashCode()) * 31 + (this.options?.hashCode() ?: 0)
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is SootOptionsConfig) {
            return false
        } else {
            if (this.name != other.name) {
                return false
            } else if (this.enable != other.enable) {
                return false
            } else {
                return this.options == other.options
            }
        }
    }

    public companion object {
        public fun serializer(): KSerializer<SootOptionsConfig> {
            return SootOptionsConfig.serializer()
        }
    }
}