package cn.sast.framework.plugin

import com.feysh.corax.config.api.SAOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("SootOptionsConfig")
data class SootOptionsConfig(
    override val name: String,
    override val enable: Boolean = true,
    override val options: SAOptions? = null,
) : ConfigSerializable(), IOptional, IFieldOptions {

    override fun compareTo(other: ConfigSerializable): Int = when (other) {
        !is SootOptionsConfig -> super.compareTo(other)
        else -> super.compareTo(other).takeIf { it != 0 } ?: when {
            enable != other.enable -> if (enable) -1 else 1
            else -> 0
        }
    }
}