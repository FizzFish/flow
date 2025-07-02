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

private final val logger: KLogger = KotlinLogging.INSTANCE.logger(ApplicationKt::logger$lambda$1)
private final var lastResort: ByteArray? = new byte[20971520]

public fun getVersion(): String {
   label19: {
      val prop: Properties = new Properties();
      val var1: Closeable = Thread.currentThread().getContextClassLoader().getResourceAsStream("version.properties");
      var var2: java.lang.Throwable = null;

      try {
         try {
            prop.load(var1 as InputStream);
         } catch (var5: java.lang.Throwable) {
            var2 = var5;
            throw var5;
         }
      } catch (var6: java.lang.Throwable) {
         CloseableKt.closeFinally(var1, var2);
      }

      CloseableKt.closeFinally(var1, null);
   }
}

public fun main(args: Array<String>) {
   OS.INSTANCE.setArgs(args);
   Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, java.lang.Throwable e) {
         if (e is OutOfMemoryError) {
            ApplicationKt.access$setLastResort$p(null);
         }

         try {
            System.err.println("Uncaught exception: ${e.getClass()} in thread ${t.getName()}: e: ${e.getMessage()}");
            if (e is IOException) {
               val var10000: java.lang.String = e.getMessage();
               if (var10000 != null && StringsKt.contains(var10000, "no space left", true)) {
                  e.printStackTrace(System.err);
                  System.exit(2);
                  throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
               }
            } else {
               if (e is OutOfMemoryError) {
                  System.exit(10);
                  throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
               }

               e.printStackTrace(System.err);
            }
         } catch (var4: java.lang.Throwable) {
            System.exit(-1);
            throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
         }

         System.exit(-1);
         throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
      }

      @Override
      public java.lang.String toString() {
         return "Corax UncaughtExceptionHandler";
      }
   });

   try {
      new FySastCli().main(args);
      System.exit(0);
      throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
   } catch (var2: java.lang.Throwable) {
      logger.error(var2, ApplicationKt::main$lambda$2);
      System.exit(1);
      throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
   }
}

fun `logger$lambda$1`(): Unit {
   return Unit.INSTANCE;
}

fun `main$lambda$2`(`$t`: java.lang.Throwable): Any {
   return "An error occurred: $`$t`";
}

@JvmSynthetic
fun `access$setLastResort$p`(var0: ByteArray) {
   lastResort = var0;
}
