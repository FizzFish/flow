package cn.sast.cli

import cn.sast.cli.command.FySastCli
import cn.sast.common.OS
import mu.KotlinLogging
import java.io.InputStream
import java.util.Properties
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

/** 预留 20 MiB，遇到 OOM 时便于打印日志再退出 */
private var lastResort: ByteArray? = ByteArray(20 * 1_024 * 1_024)

/** 读取 `resources/version.properties` 中的 `version` 字段 */
fun getVersion(): String = runCatching {
    val props = Properties()
    val stream: InputStream =
        Thread.currentThread().contextClassLoader
            .getResourceAsStream("version.properties")
            ?: return@runCatching "UNKNOWN"
    stream.use(props::load)
    props.getProperty("version", "UNKNOWN")
}.getOrElse { "UNKNOWN" }

fun main(args: Array<String>) {
    // 记录启动参数供其他模块（如 OS 工具类）使用
    OS.setArgs(args)

    // ------------------------------------------------------------------ //
    // 全局未捕获异常处理
    // ------------------------------------------------------------------ //
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        if (e is OutOfMemoryError) {
            lastResort = null            // 释放最后救命的 20 MiB
        }

        try {
            System.err.println("Uncaught exception in thread '${t.name}': $e")

            when (e) {
                is java.io.IOException ->
                    if (e.message?.contains("no space left", ignoreCase = true) == true) {
                        e.printStackTrace(System.err)
                        exitProcess(2)
                    }

                is OutOfMemoryError      -> exitProcess(10)

                else                     -> e.printStackTrace(System.err)
            }
        } catch (_: Throwable) {
            exitProcess(-1)
        }

        exitProcess(-1)
    }

    // ------------------------------------------------------------------ //
    // 启动主 CLI
    // ------------------------------------------------------------------ //
    try {
        FySastCli().main(args)
        exitProcess(0)
    } catch (t: Throwable) {
        logger.error(t) { "An error occurred: $t" }
        exitProcess(1)
    }
}
