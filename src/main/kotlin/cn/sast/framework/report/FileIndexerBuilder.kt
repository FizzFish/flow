package cn.sast.framework.report

import cn.sast.common.IResFile
import java.util.Collections
import java.util.LinkedHashMap
import java.util.NavigableSet
import java.util.Map.Entry
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentNavigableMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.jvm.internal.SourceDebugExtension
import soot.util.ArraySet

@SourceDebugExtension(["SMAP\nJavaSourceLocator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JavaSourceLocator.kt\ncn/sast/framework/report/FileIndexerBuilder\n+ 2 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,490:1\n267#1,5:507\n267#1,5:517\n72#2,2:491\n72#2,2:494\n72#2,2:497\n72#2,2:500\n1#3:493\n1#3:496\n1#3:499\n1#3:502\n462#4:503\n412#4:504\n462#4:513\n412#4:514\n1246#5,2:505\n1249#5:512\n1246#5,2:515\n1249#5:522\n*S KotlinDebug\n*F\n+ 1 JavaSourceLocator.kt\ncn/sast/framework/report/FileIndexerBuilder\n*L\n277#1:507,5\n278#1:517,5\n248#1:491,2\n249#1:494,2\n254#1:497,2\n257#1:500,2\n248#1:493\n249#1:496\n254#1:499\n257#1:502\n277#1:503\n277#1:504\n278#1:513\n278#1:514\n277#1:505,2\n277#1:512\n278#1:515,2\n278#1:522\n*E\n"])
open class FileIndexerBuilder {
    private val fileNameToPathMap: ConcurrentNavigableMap<String, NavigableSet<IResFile>> = ConcurrentSkipListMap()
    private val extensionToPathMap: ConcurrentNavigableMap<String, NavigableSet<IResFile>> = ConcurrentSkipListMap()

    private val <E> Set<E>.compressToSet: Set<E>
        get() {
            val size = this.size
            return when {
                size == 0 -> emptySet()
                size == 1 -> Collections.singleton(this.first())
                size in 0..16 -> ArraySet(this) as Set<E>
                else -> this
            }
        }

    fun addIndexMap(resFile: IResFile) {
        if (resFile.isFile()) {
            fileNameToPathMap.getOrPut(resFile.name) { ConcurrentSkipListSet() }.add(resFile)
            extensionToPathMap.getOrPut(resFile.extension) { ConcurrentSkipListSet() }.add(resFile)
        }
    }

    fun union(indexer: FileIndexer) {
        for ((key, value) in indexer.getFileNameToPathMap$corax_framework()) {
            fileNameToPathMap.getOrPut(key) { ConcurrentSkipListSet() }.addAll(value)
        }

        for ((key, value) in indexer.getExtensionToPathMap$corax_framework()) {
            extensionToPathMap.getOrPut(key) { ConcurrentSkipListSet() }.addAll(value)
        }
    }

    fun build(): FileIndexer {
        return FileIndexer(fileNameToPathMap, extensionToPathMap)
    }

    fun sortAndOptimizeMem(): FileIndexer {
        val optimizedFileNameMap = fileNameToPathMap.mapValues { (_, value) ->
            value.compressToSet
        }.toMap(LinkedHashMap(fileNameToPathMap.size))

        val optimizedExtensionMap = extensionToPathMap.mapValues { (_, value) ->
            value.compressToSet
        }.toMap(LinkedHashMap(extensionToPathMap.size))

        return FileIndexer(optimizedFileNameMap, optimizedExtensionMap)
    }

    companion object
}