package cn.sast.cli.command.tools

import cn.sast.common.OS
import mu.KLogger
import mu.KotlinLogging

public fun main(args: Array<String>) {
   val logger: KLogger = KotlinLogging.INSTANCE.logger(UtilsCliKt::main$lambda$0);
   OS.INSTANCE.setArgs(args);

   try {
      new UtilsCli().main(args);
      System.exit(0);
      throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
   } catch (var3: java.lang.Throwable) {
      logger.error(var3, UtilsCliKt::main$lambda$1);
      System.exit(1);
      throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
   }
}

fun `main$lambda$0`(): Unit {
   return Unit.INSTANCE;
}

fun `main$lambda$1`(`$t`: java.lang.Throwable): Any {
   return "An error occurred: $`$t`";
}
