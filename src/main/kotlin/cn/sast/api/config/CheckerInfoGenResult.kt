package cn.sast.api.config

import com.feysh.corax.config.api.CheckType
import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension

data class CheckerInfoGenResult(
    val checkerInfoList: LinkedHashSet<CheckerInfo>,
    val existsCheckTypes: LinkedHashSet<CheckType>,
    val existsCheckerIds: LinkedHashSet<String>,
    val checkerIdInCsv: LinkedHashSet<String>
) {
    val chapters: List<ChapterFlat>
        get() {
            val destination = ArrayList<ChapterFlat>()
            for (element in checkerInfoList) {
                val chapter = (element as CheckerInfo).chapterFlat
                if (chapter != null) {
                    destination.add(chapter)
                }
            }
            return destination
        }

}