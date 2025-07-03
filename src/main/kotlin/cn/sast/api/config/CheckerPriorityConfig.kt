package cn.sast.api.config

import cn.sast.api.util.ComparatorUtilsKt
import cn.sast.common.IResFile
import com.charleskorn.kaml.Yaml
import java.io.Closeable
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.Comparator
import java.util.LinkedHashMap
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SourceDebugExtension(["SMAP\nCheckerPriorityConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerPriorityConfig.kt\ncn/sast/api/config/CheckerPriorityConfig\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,52:1\n1187#2,2:53\n1261#2,4:55\n1187#2,2:59\n1261#2,4:61\n1485#2:65\n1510#2,3:66\n1513#2,3:76\n1246#2,2:81\n1485#2:83\n1510#2,3:84\n1513#2,3:94\n1246#2,2:99\n1557#2:101\n1628#2,3:102\n1249#2:105\n1249#2:106\n381#3,7:69\n462#3:79\n412#3:80\n381#3,7:87\n462#3:97\n412#3:98\n*S KotlinDebug\n*F\n+ 1 CheckerPriorityConfig.kt\ncn/sast/api/config/CheckerPriorityConfig\n*L\n16#1:53,2\n16#1:55,4\n17#1:59,2\n17#1:61,4\n31#1:65\n31#1:66,3\n31#1:76,3\n31#1:81,2\n32#1:83\n32#1:84,3\n32#1:94,3\n32#1:99,2\n33#1:101\n33#1:102,3\n32#1:105\n31#1:106\n31#1:69,7\n31#1:79\n31#1:80\n32#1:87,7\n32#1:97\n32#1:98\n*E\n"])
public data class CheckerPriorityConfig(
    @SerialName("category")
    public val categoryList: List<String>,
    @SerialName("severity")
    public val severityList: List<String>
) {
    private fun getComparator(): Comparator<ChapterFlat> {
        val categoryMap = this.categoryList.withIndex().associateTo(LinkedHashMap()) { 
            it.value to it.index 
        }
        val severityMap = this.severityList.withIndex().associateTo(LinkedHashMap()) { 
            it.value to it.index 
        }

        return object : Comparator<ChapterFlat> {
            override fun compare(a: ChapterFlat, b: ChapterFlat): Int {
                val categoryCompare = ComparatorUtilsKt.compareToNullable(
                    categoryMap[a.category], 
                    categoryMap[b.category]
                )
                if (categoryCompare != 0) return categoryCompare

                val severityCompare = ComparatorUtilsKt.compareToNullable(
                    severityMap[a.severity], 
                    severityMap[b.severity]
                )
                if (severityCompare != 0) return severityCompare

                return ComparatorUtilsKt.compareToNullable(a.ruleId, b.ruleId) ?: 0
            }
        }
    }

    private fun sort(chapters: List<ChapterFlat>): List<ChapterFlat> {
        return chapters.toSortedSet(getComparator()).toList()
    }

    public fun getSortTree(chapters: List<ChapterFlat>): Map<String, Map<String, List<String>>> {
        val sortedChapters = sort(chapters)
        val categoryGroups = sortedChapters.groupByTo(LinkedHashMap()) { it.category }

        return categoryGroups.mapValues { (_, categoryChapters) ->
            val severityGroups = categoryChapters.groupByTo(LinkedHashMap()) { it.severity }
            severityGroups.mapValues { (_, severityChapters) ->
                severityChapters.map { it.ruleId }
            }
        }
    }

    public fun getRuleWithSortNumber(chapters: List<ChapterFlat>): Iterable<IndexedValue<ChapterFlat>> {
        return sort(chapters).withIndex()
    }

    public companion object {
        private val yamlFormat: Yaml = Yaml.default

        public fun deserialize(checkerPriorityYamlFile: IResFile): CheckerPriorityConfig {
            val inputStream = Files.newInputStream(checkerPriorityYamlFile.path, emptyArray<OpenOption>())
            inputStream.use {
                return yamlFormat.decodeFromStream(serializer(), it)
            }
        }

        public fun serializer(): KSerializer<CheckerPriorityConfig> {
            return CheckerPriorityConfig.serializer()
        }
    }
}