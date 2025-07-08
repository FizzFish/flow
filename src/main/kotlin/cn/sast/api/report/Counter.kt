package cn.sast.api.report

import cn.sast.common.IResFile
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * 并发安全计数器，支持写入统计结果。
 */
class Counter<T : Any> {

    private val stats = ConcurrentHashMap<T, AtomicInteger>()

    fun count(item: T) {
        stats.computeIfAbsent(item) { AtomicInteger() }.incrementAndGet()
    }

    operator fun get(item: T): Int = stats[item]?.get() ?: 0

    fun isNotEmpty(): Boolean = stats.isNotEmpty()
    fun clear()            = stats.clear()
    fun size(): Int        = stats.size

    /** 按频次降序 + key 字符串升序 写入文件 */
    fun writeResults(out: IResFile) {
        if (stats.isEmpty()) return

        out.mkdirs()
        val sorted = stats.entries
            .sortedWith(compareByDescending<Map.Entry<T, AtomicInteger>> { it.value.get() }
                .thenBy { it.key.toString() })

        Files.newOutputStream(out.path).use { os ->
            OutputStreamWriter(os, StandardCharsets.UTF_8).use { w ->
                w.appendLine("--------sort--------")
                sorted.forEach { w.appendLine(it.key.toString()) }

                w.appendLine("\n--------frequency--------")
                sorted.forEach { w.appendLine("${it.value.get()} ${it.key}") }
            }
        }
    }
}
