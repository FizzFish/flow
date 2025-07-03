@file:SourceDebugExtension(["SMAP\nExtSettings.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ExtSettings.kt\ncn/sast/api/config/ExtSettingsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,118:1\n1#2:119\n*E\n"])

package cn.sast.api.config

import java.io.File
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import mu.KotlinLogging

private val logger: KLogger = KotlinLogging.logger {}
internal val coraxHomePath: String = "${System.getProperty("user.home")}${File.separatorChar}.corax"
private const val SETTING_FILE_NAME: String = "settings.properties"
private val defaultSettingsPath: String = "$coraxHomePath${File.separatorChar}$SETTING_FILE_NAME"
private const val defaultKeyForSettingsPath: String = "corax.settings.path"

private fun logger$lambda$0() {
}

@JvmSynthetic
internal fun access$getLogger$p(): KLogger {
    return logger
}

@JvmSynthetic
internal fun access$getDefaultSettingsPath$p(): String {
    return defaultSettingsPath
}