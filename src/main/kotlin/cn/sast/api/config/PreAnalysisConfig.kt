package cn.sast.api.config

import cn.sast.common.ResourceImplKt
import cn.sast.common.ResourceKt
import java.util.ArrayList
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("PreAnalysisConfig")
@SourceDebugExtension(["SMAP\nPreAnalysisConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysisConfig.kt\ncn/sast/api/config/PreAnalysisConfig\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,58:1\n126#2:59\n153#2,2:60\n155#2:66\n153#2,3:68\n1557#3:62\n1628#3,3:63\n1#4:67\n*S KotlinDebug\n*F\n+ 1 PreAnalysisConfig.kt\ncn/sast/api/config/PreAnalysisConfig\n*L\n50#1:59\n50#1:60,2\n50#1:66\n50#1:68,3\n50#1:62\n50#1:63,3\n*E\n"])
data class PreAnalysisConfig(
    @SerialName("cancel_analysis_in_error_count")
    val cancelAnalysisInErrorCount: Int = 10,
    @SerialName("large_file_size")
    val largeFileSize: Int = 512000,
    @SerialName("large_file_semaphore_permits")
    val largeFileSemaphorePermits: Int = 3,
    @SerialName("file_extension_to_size_threshold")
    private val _fileExtensionToSizeThreshold: Map<String, Int> = mapOf(
        ResourceKt.getJavaExtensions().joinToString(",") to 1048576,
        ResourceImplKt.getZipExtensions().joinToString(",") to -1,
        listOf("html", "htm", "adoc", "gradle", "properties", "config", "cnf", "txt", "json", "xml", "yml", "yaml", "toml", "ini").joinToString(",") to 5242880,
        "*" to 5242880
    ),
    @SerialName("maximum_file_size_threshold_warnings")
    val maximumFileSizeThresholdWarnings: Int = 20
) {
    @Transient
    private val fileExtensionToSizeThreshold: Map<String, Int>

    init {
        val destination = ArrayList<List<Pair<String, Int>>>(_fileExtensionToSizeThreshold.size)

        for ((k, v) in _fileExtensionToSizeThreshold) {
            val extensions = k.split(",")
            val pairs = extensions.map { it to v }
            destination.add(pairs)
        }

        this.fileExtensionToSizeThreshold = destination.flatten().toMap()
    }

    fun getSizeThreshold(extension: String): Int? {
        val size = fileExtensionToSizeThreshold[extension] ?: fileExtensionToSizeThreshold["*"]
        return if (size != null && size > 0) size else null
    }

    fun fileSizeThresholdExceeded(extension: String, fileSize: Long): Boolean {
        val threshold = getSizeThreshold(extension)
        return threshold != null && fileSize > threshold
    }

    companion object {
        fun serializer(): KSerializer<PreAnalysisConfig> {
            return PreAnalysisConfig.serializer()
        }
    }
}