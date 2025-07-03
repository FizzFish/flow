@file:SourceDebugExtension(["SMAP\nProjectConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ProjectConfig.kt\ncn/sast/api/config/ProjectConfigKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,45:1\n1863#2,2:46\n*S KotlinDebug\n*F\n+ 1 ProjectConfig.kt\ncn/sast/api/config/ProjectConfigKt\n*L\n9#1:46,2\n*E\n"])

package cn.sast.api.config

import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.IMatchItem
import kotlin.jvm.internal.SourceDebugExtension

public fun List<IMatchItem>.validate() {
    for (element in this) {
        val e: IMatchItem = element
        val errorCommit = e as? ProcessRule.ErrorCommit
        if (errorCommit != null) {
            val error = errorCommit.error
            if (error != null) {
                throw IllegalStateException("Invalid process-regex: `$e`, error: $error")
            }
        }
    }
}