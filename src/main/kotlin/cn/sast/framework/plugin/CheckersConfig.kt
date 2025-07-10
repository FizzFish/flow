package cn.sast.framework.plugin

import cn.sast.api.util.getSootTypeName
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.IChecker
import com.feysh.corax.config.api.SAOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.LinkedHashSet


@Serializable
sealed class ConfigSerializable : IConfig, Comparable<ConfigSerializable> {

    /** Human‑readable unique identifier (enforced by concrete subclasses). */
    abstract val name: String

    @Transient
    private val className: String = javaClass.name

    /** Class‑then‑name ordering so we get deterministic output when serialising. */
    final override fun compareTo(other: ConfigSerializable): Int =
        (className compareTo other.className).takeIf { it != 0 } ?: name.compareTo(other.name)
}

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
        getSootTypeName(checker.javaClass),
        checker.desc,
        enable = true,
    )

    /** Sort [checkTypes] in‑place so YAML output is deterministic. */
    fun sort(): CheckersConfig {
        checkTypes = checkTypes.sorted().toCollection(LinkedHashSet())
        return this
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

        val name: String get() = checkType

        override fun compareTo(other: CheckTypeConfig): Int =
            checkType.compareTo(other.checkType).takeIf { it != 0 } ?: when {
                enable != other.enable -> if (enable) -1 else 1
                else -> 0
            }
    }
}

@Serializable
@SerialName("CheckerUnitOptionalConfig")
data class CheckerUnitOptionalConfig(
    override val name: String,
    override val enable: Boolean = true,
    override val options: SAOptions? = null,
) : ConfigSerializable(), IOptional, IFieldOptions

@Serializable
@SerialName("SootOptionsConfig")
data class SootOptionsConfig(
    override val name: String,
    override val enable: Boolean = true,
    override val options: SAOptions? = null,
) : ConfigSerializable(), IOptional, IFieldOptions