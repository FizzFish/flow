package cn.sast.api.report

import cn.sast.common.OS
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.Collections
import java.util.LinkedHashSet

/**
 * 项目级统计指标；序列化到 JSON/YAML 供仪表盘展示。
 */
@Serializable
data class ProjectMetrics(
    var command: List<String>? = OS.getCommandLine(),
    var paths: MutableList<String> = mutableListOf(),

    var applicationClasses: Int = -1,
    var libraryClasses: Int = -1,
    var phantomClasses: Int = -1,

    var applicationMethods: Int = -1,
    var libraryMethods: Int = -1,

    var applicationMethodsHaveBody: Int = -1,
    private var applicationMethodsHaveBodyRatio: Float = -1f,

    var libraryMethodsHaveBody: Int = -1,
    private var libraryMethodsHaveBodyRatio: Float = -1f,

    var analyzedFiles: Int = -1,
    var appJavaFileCount: Int = -1,
    var appJavaLineCount: Int = -1,

    var totalFileNum: Long = -1,
    var totalAnySourceFileNum: Long = -1,
    var totalSourceFileNum: Long = -1,

    @SerialName("analyzedClasses")
    private var _analyzedClasses: Int = -1,
    @SerialName("analyzedMethodEntries")
    private var _analyzedMethodEntries: Int = -1,
    @SerialName("analyzedApplicationMethods")
    private var _analyzedApplicationMethods: Int = -1,
    private var analyzedApplicationMethodsRatio: Float = -1f,
    @SerialName("analyzedLibraryMethods")
    private var _analyzedLibraryMethods: Int = -1,
    private var analyzedLibraryMethodsRatio: Float = -1f,

    var serializedReports: Int = -1
) {

    /* -------- runtime 收集集合，不参与序列化 -------- */

    @Transient val analyzedClasses            = syncSet<String>()
    @Transient val analyzedMethodEntries      = syncSet<String>()
    @Transient val analyzedApplicationMethods = syncSet<String>()
    @Transient val analyzedLibraryMethods     = syncSet<String>()

    /** 根据集合数量更新 *_ratio 字段；分析结束时调用。 */
    fun process() {
        _analyzedClasses            = analyzedClasses.size
        _analyzedMethodEntries      = analyzedMethodEntries.size
        _analyzedApplicationMethods = analyzedApplicationMethods.size
        _analyzedLibraryMethods     = analyzedLibraryMethods.size

        if (applicationMethods > 0) {
            applicationMethodsHaveBodyRatio =
                applicationMethodsHaveBody.toFloat() / applicationMethods
            analyzedApplicationMethodsRatio =
                _analyzedApplicationMethods.toFloat() / applicationMethods
        }
        if (libraryMethods > 0) {
            libraryMethodsHaveBodyRatio =
                libraryMethodsHaveBody.toFloat() / libraryMethods
            analyzedLibraryMethodsRatio =
                _analyzedLibraryMethods.toFloat() / libraryMethods
        }
    }

    /* ---- helper ---- */
    private fun <E> syncSet(): MutableSet<E> =
        Collections.synchronizedSet(LinkedHashSet())
}
