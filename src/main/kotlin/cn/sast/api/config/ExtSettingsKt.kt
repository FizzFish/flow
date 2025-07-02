@file:SourceDebugExtension(["SMAP\nExtSettings.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ExtSettings.kt\ncn/sast/api/config/ExtSettingsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,118:1\n1#2:119\n*E\n"])

package cn.sast.api.config

import java.io.File
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import mu.KotlinLogging

private final val logger: KLogger = KotlinLogging.INSTANCE.logger(ExtSettingsKt::logger$lambda$0)
internal final val coraxHomePath: String = "${System.getProperty("user.home")}${File.separatorChar}.corax"
private const val SETTING_FILE_NAME: String = "settings.properties"
private final val defaultSettingsPath: String
private const val defaultKeyForSettingsPath: String = "corax.settings.path"

fun `logger$lambda$0`(): Unit {
   return Unit.INSTANCE;
}

@JvmSynthetic
fun `access$getLogger$p`(): KLogger {
   return logger;
}

@JvmSynthetic
fun `access$getDefaultSettingsPath$p`(): java.lang.String {
   return defaultSettingsPath;
}
