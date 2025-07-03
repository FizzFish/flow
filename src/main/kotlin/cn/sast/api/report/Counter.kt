package cn.sast.api.report

import cn.sast.common.IResFile
import java.io.Closeable
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.Map.Entry
import java.util.Comparator
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nCounter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Counter.kt\ncn/sast/api/report/Counter\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,53:1\n462#2:54\n412#2:55\n1246#3,4:56\n1062#3:60\n1216#3,2:61\n1246#3,4:63\n1053#3:67\n*S KotlinDebug\n*F\n+ 1 Counter.kt\ncn/sast/api/report/Counter\n*L\n26#1:54\n26#1:55\n26#1:56,4\n26#1:60\n27#1:61,2\n27#1:63,4\n36#1:67\n*E\n"])
class Counter<T> {
    private val statistics: ConcurrentHashMap<Any, AtomicInteger> = ConcurrentHashMap()

    private object SortMapDescendingComparator : Comparator<Entry<Any, Int>> {
        override fun compare(a: Entry<Any, Int>, b: Entry<Any, Int>): Int {
            return b.value.compareTo(a.value)
        }
    }

    private object KeyStringComparator : Comparator<Any> {
        override fun compare(a: Any, b: Any): Int {
            return a.toString().compareTo(b.toString())
        }
    }

    fun count(item: Any, map: MutableMap<Any, AtomicInteger>) {
        var counter = AtomicInteger()
        val old = map.putIfAbsent(item, counter)
        if (old != null) {
            counter = old
        }
        counter.incrementAndGet()
    }

    fun count(item: Any) {
        this.count(item as T, this.statistics)
    }

    fun get(item: Any): Int {
        return statistics[item]?.get() ?: 0
    }

    private fun sortMap(input: Map<Any, AtomicInteger>): Map<Any, Int> {
        val result = LinkedHashMap<Any, Int>(input.size)
        for ((key, value) in input) {
            result[key] = value.get()
        }
        
        val sortedEntries = result.entries.sortedWith(SortMapDescendingComparator)
        val finalMap = LinkedHashMap<Any, Int>(sortedEntries.size.coerceAtLeast(16))
        
        for ((key, value) in sortedEntries) {
            finalMap[key] = value
        }
        
        return finalMap
    }

    fun writeResults(file: IResFile) {
        val statistics = this.sortMap(this.statistics)
        if (statistics.isNotEmpty()) {
            file.mkdirs()
            val path = file.getPath()
            val writerOptions = emptyArray<OpenOption>()
            val charset = Charsets.UTF_8
            
            OutputStreamWriter(Files.newOutputStream(path, Arrays.copyOf(writerOptions, writerOptions.size)), charset).use { writer ->
                writer.write("--------sort--------\n")
                
                for (item in statistics.keys.sortedWith(KeyStringComparator)) {
                    writer.write("$item\n")
                }
                
                writer.write("\n--------frequency--------\n")
                
                for ((key, value) in statistics) {
                    writer.write("$value $key\n")
                }
                
                writer.flush()
            }
        }
    }

    fun isNotEmpty(): Boolean {
        return statistics.isNotEmpty()
    }

    fun clear() {
        statistics.clear()
    }

    fun size(): Int {
        return statistics.size
    }
}