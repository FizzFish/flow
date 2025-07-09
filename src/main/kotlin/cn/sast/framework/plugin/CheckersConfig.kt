package cn.sast.framework.plugin

import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.IChecker
import com.feysh.corax.config.api.utils.UtilsKt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.LinkedHashSet

@Serializable
@SerialName("CheckerTypeConfig")
data class CheckersConfig(
    override val name: String,
    val desc: String? = null,
    override val enable: Boolean = true,
    internal var checkTypes: LinkedHashSet<CheckTypeConfig> = LinkedHashSet(),
) : ConfigSerializable(), IOptional {

    /** Convenience constructor – build from a concrete [IChecker] implementation. */
    constructor(checker: IChecker) : this(
        UtilsKt.getSootTypeName(checker.javaClass),
        checker.desc,
        enable = true,
    )

    /** Sort [checkTypes] in‑place so YAML output is deterministic. */
    fun sort(): CheckersConfig {
        checkTypes = checkTypes.sorted().toCollection(LinkedHashSet())
        return this
    }

    override fun compareTo(other: ConfigSerializable): Int = when (other) {
        !is CheckersConfig -> super.compareTo(other)
        else -> super.compareTo(other).takeIf { it != 0 } ?: when {
            enable != other.enable -> if (enable) -1 else 1
            else -> 0
        }
    }

    // ---------------------------------------------------------------------
    //  Nested DTO – single check‑type toggle
    // ---------------------------------------------------------------------
    @Serializable
    @SerialName("CheckTypeConfig")
    data class CheckTypeConfig(
        val checkType: String,
        override val enable: Boolean = true,
    ) : IOptional, IConfig, Comparable<CheckTypeConfig> {

        constructor(type: CheckType) : this(type.toIdentifier(), true)

        override val name: String get() = checkType

        override fun compareTo(other: CheckTypeConfig): Int =
            checkType.compareTo(other.checkType).takeIf { it != 0 } ?: when {
                enable != other.enable -> if (enable) -1 else 1
                else -> 0
            }
    }
}