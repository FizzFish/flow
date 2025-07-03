package cn.sast.framework.plugin

import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.IChecker
import com.feysh.corax.config.api.utils.UtilsKt
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CheckerTypeConfig")
@SourceDebugExtension(["SMAP\nSAConfiguration.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/CheckersConfig\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,557:1\n1#2:558\n*E\n"])
data class CheckersConfig(
    val name: String,
    val desc: String? = null,
    val enable: Boolean,
    var checkTypes: LinkedHashSet<CheckersConfig.CheckTypeConfig> = LinkedHashSet()
) : ConfigSerializable(), IOptional {

    constructor(checker: IChecker) : this(
        UtilsKt.getSootTypeName(checker.getClass()),
        checker.getDesc(),
        true
    )

    fun sort(): CheckersConfig {
        this.checkTypes = CollectionsKt.sorted(this.checkTypes).toCollection(LinkedHashSet())
        return this
    }

    override operator fun compareTo(other: ConfigSerializable): Int {
        if (other !is CheckersConfig) {
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

    operator fun component1(): String = name
    operator fun component2(): String? = desc
    operator fun component3(): Boolean = enable
    operator fun component4(): LinkedHashSet<CheckersConfig.CheckTypeConfig> = checkTypes

    fun copy(
        name: String = this.name,
        desc: String? = this.desc,
        enable: Boolean = this.enable,
        checkTypes: LinkedHashSet<CheckersConfig.CheckTypeConfig> = this.checkTypes
    ): CheckersConfig = CheckersConfig(name, desc, enable, checkTypes)

    override fun toString(): String =
        "CheckersConfig(name=$name, desc=$desc, enable=$enable, checkTypes=$checkTypes)"

    override fun hashCode(): Int =
        ((name.hashCode() * 31 + (desc?.hashCode() ?: 0)) * 31 + enable.hashCode()) * 31 + checkTypes.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CheckersConfig) return false
        return name == other.name &&
            desc == other.desc &&
            enable == other.enable &&
            checkTypes == other.checkTypes
    }

    @Serializable
    @SerialName("CheckTypeConfig")
    @SourceDebugExtension(["SMAP\nSAConfiguration.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/CheckersConfig$CheckTypeConfig\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,557:1\n1#2:558\n*E\n"])
    data class CheckTypeConfig(
        val checkType: String,
        val enable: Boolean
    ) : IOptional, IConfig, Comparable<CheckersConfig.CheckTypeConfig> {

        constructor(type: CheckType) : this(SAConfigurationKt.get1to1SpecialIdentifier(type), true)

        override operator fun compareTo(other: CheckersConfig.CheckTypeConfig): Int {
            val var3 = checkType.compareTo(other.checkType)
            val var2 = if (var3 != 0) var3 else null
            if (var2 != null) {
                return var2
            } else if (enable != other.enable) {
                return if (enable) -1 else 1
            } else {
                return 0
            }
        }

        operator fun component1(): String = checkType
        operator fun component2(): Boolean = enable

        fun copy(
            checkType: String = this.checkType,
            enable: Boolean = this.enable
        ): CheckersConfig.CheckTypeConfig = CheckersConfig.CheckTypeConfig(checkType, enable)

        override fun toString(): String =
            "CheckTypeConfig(checkType=$checkType, enable=$enable)"

        override fun hashCode(): Int =
            checkType.hashCode() * 31 + enable.hashCode()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is CheckTypeConfig) return false
            return checkType == other.checkType && enable == other.enable
        }

        companion object {
            fun serializer(): KSerializer<CheckersConfig.CheckTypeConfig> =
                CheckersConfig.CheckTypeConfig.$serializer.INSTANCE as KSerializer<CheckersConfig.CheckTypeConfig>
        }
    }

    companion object {
        fun serializer(): KSerializer<CheckersConfig> =
            CheckersConfig.$serializer.INSTANCE as KSerializer<CheckersConfig>
    }
}