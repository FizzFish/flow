package cn.sast.cli

import cn.sast.cli.command.FySastCli
import cn.sast.common.OS
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.lang.Thread.UncaughtExceptionHandler
import java.util.Properties
import mu.KLogger
import mu.KotlinLogging

private val logger: KLogger = KotlinLogging.logger(::logger$lambda$1)
private var lastResort: ByteArray? = ByteArray(20971520)

fun getVersion(): String {
    val prop = Properties()
    val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("version.properties")
    inputStream.use {
        prop.load(it)
    }
    return prop.toString()
}

fun main(args: Array<String>) {
    OS.INSTANCE.setArgs(args)
    Thread.setDefaultUncaughtExceptionHandler(object : UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            if (e is OutOfMemoryError) {
                lastResort = null
            }

            try {
                System.err.println("Uncaught exception: ${e.javaClass} in thread ${t.name}: e: ${e.message}")
                when {
                    e is IOException && e.message?.contains("no space left", ignoreCase = true) == true -> {
                        e.printStackTrace(System.err)
                        System.exit(2)
                        throw RuntimeException("System.exit returned normally, while it was supposed to halt JVM.")
                    }
                    e is OutOfMemoryError -> {
                        System.exit(10)
                        throw RuntimeException("System.exit returned normally, while it was supposed to halt JVM.")
                    }
                    else -> {
                        e.printStackTrace(System.err)
                    }
                }
            } catch (var4: Throwable) {
                System.exit(-1)
                throw RuntimeException("System.exit returned normally, while it was supposed to halt JVM.")
            }

            System.exit(-1)
            throw RuntimeException("System.exit returned normally, while it was supposed to halt JVM.")
        }

        override fun toString(): String {
            return "Corax UncaughtExceptionHandler"
        }
    })

    try {
        FySastCli().main(args)
        System.exit(0)
        throw RuntimeException("System.exit returned normally, while it was supposed to halt JVM.")
    } catch (t: Throwable) {
        logger.error(t) { "An error occurred: $t" }
        System.exit(1)
        throw RuntimeException("System.exit returned normally, while it was supposed to halt JVM.")
    }
}

private fun logger$lambda$1() {
}

private fun main$lambda$2(t: Throwable): Any {
    return "An error occurred: $t"
}

@JvmSynthetic
internal fun access$setLastResort$p(var0: ByteArray?) {
    lastResort = var0
}