/**
 * 该文件整合了 **CheckerPriorityConfig / ClassSerializer / ExtSettings** 三个
 * 逻辑相关模块，全部转写为 **可直接编译** 的 Kotlin 源码，并保持原有功能。
 *
 * - 移除了反编译遗留的 `$access`/`SourceDebugExtension`/`serializer()` 等样板
 * - 用 Kotlin `data class` / `object` idiom 精简重复代码
 * - `ExtSettings` 的辅助常量与 `logger` 内联到同文件，省去额外 `*_Kt.kt`
 * - 其余业务逻辑（排序、YAML 反序列化、Settings 读写等）保持不变
 */

package cn.sast.api.config

/* ---------------------------------------------------------------------------
 *  1. CheckerPriorityConfig
 * ------------------------------------------------------------------------- */
import cn.sast.api.util.compareToNullable
import cn.sast.common.IResFile
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.nio.file.Files
import java.util.Comparator
import java.util.LinkedHashMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File
import mu.KLogger
import mu.KotlinLogging
import org.utbot.common.AbstractSettings

private val logger: KLogger = KotlinLogging.logger {}

@Serializable
data class CheckerPriorityConfig(
    @SerialName("category") val categoryList: List<String>,
    @SerialName("severity") val severityList: List<String>
) {

    /** 根据配置生成排序器：先比 category → severity → ruleId */
    private fun comparator(): Comparator<ChapterFlat> {
        val categoryMap = categoryList.withIndex()
            .associateTo(LinkedHashMap()) { it.value to it.index }   // "name" -> order
        val severityMap = severityList.withIndex()
            .associateTo(LinkedHashMap()) { it.value to it.index }

        return Comparator { a, b ->
            // 1) category  2) severity  3) ruleId
            categoryMap[a.category].compareToNullable(categoryMap[b.category])
                .takeIf { it != 0 }
                ?: severityMap[a.severity].compareToNullable(severityMap[b.severity])
                    .takeIf { it != 0 }
                ?: a.ruleId.compareToNullable(b.ruleId)      // ruleId 自身就是 Comparable
        }
    }


    /** 按配置优先级排好序 */
    private fun sort(chapters: List<ChapterFlat>): List<ChapterFlat> =
        chapters.toSortedSet(comparator()).toList()

    /** 生成 `category -> severity -> [ruleIds]` 的三级树结构 */
    fun getSortTree(chapters: List<ChapterFlat>): Map<String, Map<String, List<String>>> =
        sort(chapters)
            .groupByTo(LinkedHashMap()) { it.category }
            .mapValues { (_, cs) ->
                cs.groupByTo(LinkedHashMap()) { it.severity }
                    .mapValues { (_, ss) -> ss.map(ChapterFlat::ruleId) }
            }

    /** 返回排好序的 [ChapterFlat]，并带上序号 (`withIndex()`) */
    fun getRuleWithSortNumber(chapters: List<ChapterFlat>): Iterable<IndexedValue<ChapterFlat>> =
        sort(chapters).withIndex()

    /* ---------- static helpers ---------- */

    companion object {
        private val yaml = Yaml.default

        /** 从 YAML 资源文件反序列化 */
        fun deserialize(yamlFile: IResFile): CheckerPriorityConfig =
            Files.newInputStream(yamlFile.path)                 // 无需传空数组
                .use { input ->
                    yaml.decodeFromStream(
                        serializer(),     // 用类名限定
                        input
                    )
                }
        fun serializer(): KSerializer<CheckerPriorityConfig> = serializer()
    }
}

/* ---------------------------------------------------------------------------
 *  2. ClassSerializer ― 把 `java.lang.Class` 序列化成类名
 * ------------------------------------------------------------------------- */


object ClassSerializer : KSerializer<Class<*>> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ClassSerializer", STRING)

    override fun serialize(encoder: Encoder, value: Class<*>) =
        encoder.encodeString(value.name)

    override fun deserialize(decoder: Decoder): Class<*> =
        Class.forName(decoder.decodeString())
}

/* ---------------------------------------------------------------------------
 *  3. ExtSettings ― 运行期可配置参数
 * ------------------------------------------------------------------------- */


/** ~/.corax 目录 */
private val coraxHomePath: String =
    "${System.getProperty("user.home")}${File.separatorChar}.corax"

/** 缺省配置文件路径 */
private const val SETTINGS_FILE = "settings.properties"
private val defaultSettingsPath: String =
    "$coraxHomePath${File.separatorChar}$SETTINGS_FILE"

/** JVM 参数 key，可通过 `-Dcorax.settings.path=xxx` 覆盖 */
private const val SETTINGS_KEY = "corax.settings.path"

/**
 * 可热更新的全局配置。依赖 `AbstractSettings` 提供的 `get*Property` 委托。
 */
object ExtSettings :
    AbstractSettings(logger, SETTINGS_KEY, defaultSettingsPath) {

    /* —— 数据流分析相关 —— */
    var dataFlowIteratorCountForAppClasses        by getIntProperty(12, 1, Int.MAX_VALUE)
    var dataFlowIteratorCountForLibClasses        by getIntProperty(8, 1, Int.MAX_VALUE)
    var dataFlowIteratorIsFixPointSizeLimit       by getIntProperty(4, 1, Int.MAX_VALUE)
    var dataFlowMethodUnitsSizeLimit              by getIntProperty(1000, -1, Int.MAX_VALUE)
    var dataFlowCacheExpireAfterAccess            by getLongProperty(30_000L, 1L, Long.MAX_VALUE)
    var dataFlowCacheMaximumWeight                by getLongProperty(10_000L, 1L, Long.MAX_VALUE)
    var dataFlowCacheMaximumSizeFactor: Double by getProperty(
        /* defaultValue = */ 5.0,
        /* range = */ Triple(
            1.0E-4,
            Double.MAX_VALUE,
            Comparator { a: Double, b: Double -> a.compareTo(b) }   // 自然顺序比较器
        ),
        /* converter = */ String::toDouble
    )
    var calleeDepChainMaxNumForLibClassesInInterProceduraldataFlow
            by getIntProperty(5, -1, Int.MAX_VALUE)
    var dataFlowInterProceduralCalleeDepChainMaxNum
            by getLongProperty(30L, -1L, Long.MAX_VALUE)
    var dataFlowInterProceduralCalleeTimeOut      by getIntProperty(30_000, -1, Int.MAX_VALUE)
    var dataFlowResolveTargetsMaxNum              by getLongProperty(8L, -1L, Long.MAX_VALUE)
    var dataFlowResultPathOnlyStmt                by getBooleanProperty(true)

    /* —— UI / 输出控制 —— */
    var enableProcessBar                          by getBooleanProperty(true)
    var showMetadata                              by getBooleanProperty(true)
    var tabSize                                   by getIntProperty(4)
    var dumpCompleteDotCg                         by getBooleanProperty(false)
    var prettyPrintJsonReport                     by getBooleanProperty(true)
    var prettyPrintPlistReport                    by getBooleanProperty(false)

    /* —— 运行时 / 兼容性 —— */
    var sqliteJournalMode                         by getStringProperty("WAL")
    var jdCoreDecompileTimeOut                    by getIntProperty(20_000, -1, Int.MAX_VALUE)
    var skip_large_class_by_maximum_methods       by getIntProperty(2000, -1, Int.MAX_VALUE)
    var skip_large_class_by_maximum_fields        by getIntProperty(2000, -1, Int.MAX_VALUE)
    var castNeverFailsOfPhantomClass              by getBooleanProperty(false)
    var printAliasInfo                            by getBooleanProperty(false)
    var useRoaringPointsToSet                     by getBooleanProperty(false)
    var hashVersion                               by getIntProperty(2)

    /* ---------- 辅助 ---------- */

    /** 返回默认配置文件路径（供外部调用） */
    fun defaultSettingsPath(): String = defaultSettingsPath

    /** 读取当前生效的配置文件路径（JVM 参数优先生效） */
    @JvmStatic
    fun getPath(): String =
        System.getProperty(SETTINGS_KEY) ?: defaultSettingsPath

    /** 打印一次启动日志，在主程序早期调用 */
    @JvmStatic
    fun init() {
        logger.info { "ExtSettingsPath: ${getPath()}" }
    }
}
