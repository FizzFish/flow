package cn.sast.framework.report

import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.enums.EnumEntries
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nJavaSourceLocator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JavaSourceLocator.kt\ncn/sast/framework/report/AbstractFileIndexer\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,490:1\n1381#2:491\n1469#2,2:492\n1471#2,3:495\n1#3:494\n*S KotlinDebug\n*F\n+ 1 JavaSourceLocator.kt\ncn/sast/framework/report/AbstractFileIndexer\n*L\n197#1:491\n197#1:492,2\n197#1:495,3\n*E\n"])
public abstract class AbstractFileIndexer<PathType> {
    private val errorMsgShow: Boolean

    public abstract fun getNames(path: Any, mode: CompareMode): List<String>

    public abstract fun getPathsByName(name: String): Collection<Any>

    private fun List<String>.hasDot(): Boolean {
        val sz = size
        if (sz <= 1) {
            return false
        } else {
            var i = 0
            for (e in this) {
                if (++i != sz && e.contains(".")) {
                    return true
                }
            }
            return false
        }
    }

    private fun List<String>.normalizePathParts(mode: CompareMode = CompareMode.Path): List<String> {
        if (mode.isClass && hasDot()) {
            val lastIndex = lastIndex
            val ret = ArrayList<String>(size + 2)
            var i = 0
            for (e in this) {
                if (i++ != lastIndex && e.contains(".")) {
                    ret.addAll(e.split("."))
                } else {
                    ret.add(e)
                }
            }
            return ret
        } else {
            return this
        }
    }

    public fun findFromFileIndexMap(
        toFindNames: List<String>,
        mode: CompareMode = CompareMode.Path
    ): Sequence<PathType> {
        return sequence {
            if (toFindNames.isEmpty()) {
                return@sequence
            }
            val find = normalizePathParts(toFindNames, mode)
            val paths = getPathsByName(find.last())
            for (element in paths) {
                val checkNames = normalizePathParts(getNames(element, mode), mode)
                if (mode == CompareMode.ClassNoPackageLayout) {
                    yield(element as PathType)
                } else {
                    val r = JavaSourceLocatorKt.listEndsWith(checkNames, find)
                    if (r == ListSuffixRelation.Equals || r == ListSuffixRelation.BIsSuffixOfA) {
                        yield(element as PathType)
                    }
                }
            }
        }
    }

    public fun findFromFileIndexMap(find: Any, mode: CompareMode = CompareMode.Path): Sequence<PathType> {
        return findFromFileIndexMap(getNames(find as PathType, mode), mode)
    }

    public fun findFiles(fileNames: Collection<String>, mode: CompareMode): List<PathType> {
        val destination = ArrayList<PathType>()
        for (element in fileNames) {
            if (element.indexOf('\\') != -1) {
                throw IllegalArgumentException("invalid source file name: $element")
            }
            destination.addAll(findFromFileIndexMap(element.split("/"), mode).toList())
        }
        return destination
    }

    public fun findAnyFile(fileNames: Collection<String>, mode: CompareMode): PathType? {
        for (s in fileNames) {
            if (s.indexOf('\\') != -1) {
                throw IllegalArgumentException("invalid source file name: $s in $fileNames")
            }
            val result = findFromFileIndexMap(s.split("/"), mode).firstOrNull()
            if (result != null) {
                return result
            }
        }
        return null
    }

    public companion object {
        public lateinit var defaultClassCompareMode: CompareMode
    }

    public enum class CompareMode(val isClass: Boolean) {
        Path(false),
        Class(true),
        ClassNoPackageLayout(true);

        companion object {
            @JvmStatic
            fun getEntries(): EnumEntries<CompareMode> {
                return enumEntries<CompareMode>()
            }
        }
    }
}