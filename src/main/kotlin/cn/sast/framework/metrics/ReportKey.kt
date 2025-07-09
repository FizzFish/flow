package cn.sast.framework.metrics

import kotlinx.serialization.Serializable

/**
 * A (category, type) pair that is used as the key when grouping `Report`s.
 *
 * `size` is set once the final number of grouped reports is known.
 */
@Serializable
internal data class ReportKey(
    val category: String? = null,
    val type: String,
    var size: Int = -1,
)