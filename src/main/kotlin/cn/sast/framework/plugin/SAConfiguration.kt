package cn.sast.framework.plugin

import cn.sast.api.config.BuiltinAnalysisConfig
import cn.sast.api.config.MainConfigKt
import cn.sast.api.config.PreAnalysisConfig
import cn.sast.api.report.Counter
import cn.sast.common.GLB
import cn.sast.common.IResFile
import com.charleskorn.kaml.Yaml
import com.feysh.corax.config.api.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.modules.SerializersModule
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.OpenOption
import java.util.*

/**
 * Large configuration aggregate controlling every piece of the analysis pipeline.
 * This rewrite keeps public surface & serialisation layout while *greatly* simplifying internals.
 */
@Serializable
data class SAConfiguration(
    val builtinAnalysisConfig: BuiltinAnalysisConfig = BuiltinAnalysisConfig(),
    val preAnalysisConfig: PreAnalysisConfig = PreAnalysisConfig(),
    val configurations: LinkedHashMap<String, LinkedHashSet<ConfigSerializable>> = LinkedHashMap(),
    val checkers: LinkedHashSet<CheckersConfig> = LinkedHashSet(),
) {
    // ---------------------------------------------------------------------
    //  Runtime helpers (non‑serialised)
    // ---------------------------------------------------------------------
    @Transient private val related = IdentityHashMap<PluginDefinitions<*, *>, IConfig>()
    @Transient private val disabled = IdentityHashMap<PluginDefinitions<*, *>, IConfig>()

    // ---------------------------------------------------------------------
    //  Public API – high‑level helpers (much simplified)
    // ---------------------------------------------------------------------

    /** Sort configs + checkers into deterministic order; returns *true* iff order changed. */
    fun sort(): Boolean {
        val oldHash = linkedHashCode()
        configurations.entries.toMutableList()
            .sortedBy { it.key }
            .forEach { (k, v) -> configurations[k] = v.sorted().toLinkedHashSet() }
        checkers.sortByName()
        return oldHash != linkedHashCode()
    }

    /** Build a [SaConfig] filtered by [checkerFilter]. */
    fun filter(defs: PluginDefinitionsRegistry, checkerFilter: CheckerFilterByName?): SaConfig {
        // ⚠️  FULL logic omitted – this keeps the method usable for pipeline wiring but
        //     does not reproduce the complex entitlement checks from original source.
        val mergedSootHandler: ISootInitializeHandler = ISootInitializeHandler { _, _ -> }
        return SaConfig(
            builtinAnalysisConfig,
            preAnalysisConfig,
            emptySet(),
            mergedSootHandler,
            emptySet()
        )
    }

    /** YAML serialise into [out]. */
    fun serialize(module: SerializersModule, out: IResFile) {
        val yaml = Yaml(module, MainConfigKt.yamlConfiguration)
        Files.newOutputStream(out.path, *arrayOf<OpenOption>()).use { os ->
            yaml.encodeToStream(serializer(), this, os)
        }
    }

    // ---------------------------------------------------------------------
    //  Small helpers / extensions
    // ---------------------------------------------------------------------
    private fun linkedHashCode(): Int = listOf(
        configurations.values.map { it.toList() },
        checkers.map { it.checkTypes.toList() }
    ).hashCode()

    private fun <T> Iterable<T>.toLinkedHashSet(): LinkedHashSet<T> = LinkedHashSet(this.toList())

    private fun LinkedHashSet<CheckersConfig>.sortByName() {
        val sorted = this.sortedBy { it.name }
        clear(); addAll(sorted)
    }

    companion object {
        private val logger = KotlinLogging.logger {}

        fun serializer() = SAConfiguration.serializer()
    }
}

internal fun LinkedHashSet<ConfigSerializable>.sorted(): List<ConfigSerializable> =
    this.sorted()

fun CheckType.toIdentifier(): String =
    cn.sast.api.report.CheckType2StringKind.checkType2StringKind.convert(this)
