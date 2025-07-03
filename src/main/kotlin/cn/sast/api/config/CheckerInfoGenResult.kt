package cn.sast.api.config

import com.feysh.corax.config.api.CheckType
import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nCheckerInfo.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerInfo.kt\ncn/sast/api/config/CheckerInfoGenResult\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,49:1\n1611#2,9:50\n1863#2:59\n1864#2:61\n1620#2:62\n1#3:60\n*S KotlinDebug\n*F\n+ 1 CheckerInfo.kt\ncn/sast/api/config/CheckerInfoGenResult\n*L\n48#1:50,9\n48#1:59\n48#1:61\n48#1:62\n48#1:60\n*E\n"])
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

    operator fun component1(): LinkedHashSet<CheckerInfo> = checkerInfoList

    operator fun component2(): LinkedHashSet<CheckType> = existsCheckTypes

    operator fun component3(): LinkedHashSet<String> = existsCheckerIds

    operator fun component4(): LinkedHashSet<String> = checkerIdInCsv

    fun copy(
        checkerInfoList: LinkedHashSet<CheckerInfo> = this.checkerInfoList,
        existsCheckTypes: LinkedHashSet<CheckType> = this.existsCheckTypes,
        existsCheckerIds: LinkedHashSet<String> = this.existsCheckerIds,
        checkerIdInCsv: LinkedHashSet<String> = this.checkerIdInCsv
    ): CheckerInfoGenResult {
        return CheckerInfoGenResult(checkerInfoList, existsCheckTypes, existsCheckerIds, checkerIdInCsv)
    }

    override fun toString(): String {
        return "CheckerInfoGenResult(checkerInfoList=$checkerInfoList, existsCheckTypes=$existsCheckTypes, existsCheckerIds=$existsCheckerIds, checkerIdInCsv=$checkerIdInCsv)"
    }

    override fun hashCode(): Int {
        return ((checkerInfoList.hashCode() * 31 + existsCheckTypes.hashCode()) * 31 + existsCheckerIds.hashCode()) * 31 +
            checkerIdInCsv.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CheckerInfoGenResult) return false

        if (checkerInfoList != other.checkerInfoList) return false
        if (existsCheckTypes != other.existsCheckTypes) return false
        if (existsCheckerIds != other.existsCheckerIds) return false
        return checkerIdInCsv == other.checkerIdInCsv
    }
}