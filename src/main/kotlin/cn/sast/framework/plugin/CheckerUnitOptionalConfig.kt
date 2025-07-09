package cn.sast.framework.plugin

import com.feysh.corax.config.api.SAOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CheckerUnitOptionalConfig")
data class CheckerUnitOptionalConfig(
    override val name: String,
    override val enable: Boolean = true,
    val options: SAOptions? = null,
) : ConfigSerializable(), IOptional, IFieldOptions {

    /** Also respect the *enable* flag when sorting – enabled units come first. */
    override fun compareTo(other: ConfigSerializable): Int = when (other) {
        !is CheckerUnitOptionalConfig -> super.compareTo(other)
        else -> super.compareTo(other).takeIf { it != 0 } ?: when {
            enable != other.enable -> if (enable) -1 else 1
            else -> 0
        }
    }
}