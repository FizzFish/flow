package cn.sast.cli.command.tools

import cn.sast.common.OS
import mu.KLogger
import mu.KotlinLogging

fun main(args: Array<String>) {
    val logger: KLogger = KotlinLogging.logger(::main$lambda$0)
    OS.INSTANCE.setArgs(args)

    try {
        UtilsCli().main(args)
        System.exit(0)
        throw RuntimeException("System.exit returned normally, while it was supposed to halt JVM.")
    } catch (t: Throwable) {
        logger.error(t, ::main$lambda$1)
        System.exit(1)
        throw RuntimeException("System.exit returned normally, while it was supposed to halt JVM.")
    }
}

private fun main$lambda$0() {
    return Unit
}

private fun main$lambda$1(t: Throwable): Any {
    return "An error occurred: $t"
}