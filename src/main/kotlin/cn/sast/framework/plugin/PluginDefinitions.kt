package cn.sast.framework.plugin

import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.CheckerUnit
import com.feysh.corax.config.api.ISootInitializeHandler
import java.util.ServiceLoader
import kotlin.reflect.KClass

/**
 * Generic wrapper that binds a *type* (e.g. `CheckerUnit`) to its default [config] and a
 * lazily‑loaded singleton *instance* discovered via Java’s `ServiceLoader` (or provided at init).
 */
sealed class PluginDefinitions<T : Any, C : IConfig>(
    val type: KClass<T>,
    val defaultConfig: C,
    private val singletonProvider: (() -> T)? = null,
) {
    private val _singleton: Lazy<T> = lazy {
        singletonProvider?.invoke()
            ?: ServiceLoader.load(type.java).firstOrNull()
            ?: error("No implementation found for ${type.qualifiedName}")
    }

    /** Returns the **single** service implementation for [type]. */
    fun singleton(): T = _singleton.value

    override fun toString(): String = "PluginDefinition(type=${type.simpleName})"

    // -------------------------------------------------------------------------
    //  Known concrete plugin definitions – extend as needed
    // -------------------------------------------------------------------------
    class CheckerUnitDefinition<T : CheckerUnit>(
        type: KClass<T>,
        defaultConfig: CheckersConfig,
        provider: () -> T
    ) : PluginDefinitions<T, CheckersConfig>(type, defaultConfig, provider)

    class CheckTypeDefinition<T : CheckType>(
        type: KClass<T>,
        defaultConfig: CheckersConfig.CheckTypeConfig,
        provider: () -> T
    ) : PluginDefinitions<T, CheckersConfig.CheckTypeConfig>(type, defaultConfig, provider)

    class ISootInitializeHandlerDefinition<T : ISootInitializeHandler>(
        type: KClass<T>,
        defaultConfig: SootOptionsConfig,
        provider: () -> T
    ) : PluginDefinitions<T, SootOptionsConfig>(type, defaultConfig, provider)
}