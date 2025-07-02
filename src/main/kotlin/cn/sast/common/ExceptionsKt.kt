package cn.sast.common

import java.io.IOException

/**
 * Checks whether this throwable is considered critical. If the exception
 * indicates that the disk space is exhausted or an out-of-memory situation
 * occurs, the JVM will be terminated.
 */
fun Throwable.checkCritical() {
    when (this) {
        is IOException -> {
            message?.let {
                if (it.contains("no space left", ignoreCase = true)) {
                    printStackTrace(System.err)
                    System.exit(2)
                    throw RuntimeException(
                        "System.exit returned normally, while it was supposed to halt JVM."
                    )
                }
            }
        }
        is OutOfMemoryError -> {
            printStackTrace(System.err)
            System.exit(10)
            throw RuntimeException(
                "System.exit returned normally, while it was supposed to halt JVM."
            )
        }
    }
}
