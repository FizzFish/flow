package cn.sast.framework.incremental

import cn.sast.api.config.MainConfig
import cn.sast.api.config.MainConfigKt
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.incremental.ModifyInfoFactory
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.InterProceduralAnalysisDependsGraph
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph
import cn.sast.api.util.SootUtilsKt
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.framework.report.AbstractFileIndexer
import cn.sast.framework.report.ProjectFileLocator
import cn.sast.framework.report.AbstractFileIndexer.CompareMode
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Locale
import java.util.function.Consumer
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.patch.FileHeader
import soot.Scene
import soot.SootClass

@SourceDebugExtension(["SMAP\nIncrementalAnalyzeImplByChangeFiles.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IncrementalAnalyzeImplByChangeFiles.kt\ncn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles\n+ 2 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,395:1\n1317#2,2:396\n1#3:398\n1557#4:399\n1628#4,3:400\n381#5,7:403\n*S KotlinDebug\n*F\n+ 1 IncrementalAnalyzeImplByChangeFiles.kt\ncn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles\n*L\n264#1:396,2\n299#1:399\n299#1:400,3\n337#1:403,7\n*E\n"])
class IncrementalAnalyzeImplByChangeFiles(
    private val mainConfig: MainConfig,
    private val mappingDiffInArchive: Boolean = true,
    private val factory: ModifyInfoFactory = ModifyInfoFactoryImpl(),
    public open val simpleDeclAnalysisDependsGraph: SimpleDeclAnalysisDependsGraph = factory.createSimpleDeclAnalysisDependsGraph(),
    public open val interProceduralAnalysisDependsGraph: InterProceduralAnalysisDependsGraph = factory.createInterProceduralAnalysisDependsGraph()
) : IncrementalAnalyzeByChangeFiles {
    private val modifyFiles = LinkedHashSet<String>()
    private val oldPath2Header = LinkedHashMap<String, DiffEntry>()
    private val newPath2Header = LinkedHashMap<String, FileHeader>()
    private val pathsInPatch = LinkedHashSet<String>()
    private val name2Path = HashMap<String, MutableSet<String>>()
    private var ignoreCase: Boolean = false

    private fun visitChangedDecl(target: Any, diffPath: String, diff: DiffEntry) {
        if (!pathsInPatch.contains(diffPath)) {
            throw IllegalStateException("Check failed.")
        } else {
            val changed = factory.getPatchedDeclsByDiff(target, diff)
            simpleDeclAnalysisDependsGraph.visitChangedDecl(diffPath, changed)
            interProceduralAnalysisDependsGraph.visitChangedDecl(diffPath, changed)
        }
    }

    fun update(scene: Scene, locator: ProjectFileLocator?) {
        scene.classes.snapshotIterator().forEach { p ->
            val (oldDiff, newDiff) = getChangeTypeOfClass(p)
            if (oldDiff != null) {
                visitChangedDecl(p, oldDiff.oldPath, oldDiff)
            }
            if (newDiff != null) {
                visitChangedDecl(p, newDiff.newPath, newDiff)
            }
        }

        locator?.let {
            pathsInPatch.forEach { path ->
                val files: Sequence<IResFile> = TODO("FIXME - Original sequence logic not fully decompiled")
                files.forEach { file ->
                    if (mappingDiffInArchive || !MainConfigKt.skipResourceInArchive(mainConfig, file)) {
                        val oldx = oldPath2Header[path]
                        val newx = newPath2Header[path]
                        if (oldx != null) {
                            require(oldx.oldPath == path) { "Check failed." }
                            visitChangedDecl(file, path, oldx)
                        }
                        if (newx != null) {
                            require(newx.newPath == path) { "Check failed." }
                            visitChangedDecl(file, path, newx)
                        }
                    }
                }
            }
        }
    }

    private fun getChangeType(possibleSourceFiles: Collection<String>, mode: CompareMode): Pair<DiffEntry?, DiffEntry?> {
        val indexer = object : AbstractFileIndexer<String>(this) {
            override fun getNames(path: String, mode: CompareMode): List<String> {
                return path.removeSuffix("/").split("/")
            }

            override fun getPathsByName(name: String): Collection<String> {
                return name2Path[name] ?: emptyList()
            }
        }

        val normalizedFiles = if (ignoreCase) {
            possibleSourceFiles.map { it.lowercase(Locale.getDefault()) }
        } else {
            possibleSourceFiles
        }

        return indexer.findFiles(normalizedFiles, mode).firstNotNullOfOrNull { path ->
            oldPath2Header[path] to newPath2Header[path]
        } ?: (null to null)
    }

    override fun getChangeTypeOfClass(cls: SootClass): Pair<DiffEntry?, DiffEntry?> {
        return getChangeType(SootUtilsKt.getPossibleSourceFiles(cls), AbstractFileIndexer.Companion.defaultClassCompareMode)
    }

    override fun getChangeTypeOfFile(file: String): Pair<DiffEntry?, DiffEntry?> {
        return getChangeType(listOf(file), AbstractFileIndexer.CompareMode.Path)
    }

    override fun parseIncrementBaseFile(base: IResource) {
        require(base.exists) { "The incremental base file: `$base` not exists" }
        require(base.isFile) { "The incremental base file: `$base` is not a file" }

        val file = base.toFile()
        when (file.extension) {
            "diff", "patch" -> parseGitDiff(file)
            "txt" -> parseChangeFiles(file)
            else -> throw IllegalStateException(
                "The incremental base file: `$base` with a unsupported extension. Now only support .diff/.patch/.txt"
            )
        }
    }

    private fun addOnePath(p: String) {
        require(!p.contains("\\")) { "Check failed." }
        pathsInPatch.add(p)
        val name = p.removeSuffix("/").substringAfterLast("/")
        name2Path.getOrPut(name) { LinkedHashSet() }.add(p)
    }

    private fun normalizePath(p: String): String {
        val normalized = p.replace("\\", "/")
            .replace("//", "/")
            .removePrefix("/")
            .removeSuffix("\r")
        return if (ignoreCase) normalized.lowercase(Locale.getDefault()) else normalized
    }

    @Throws(java.io.IOException::class)
    private fun parseChangeFiles(diffFilePath: IResFile) {
        Files.newInputStream(diffFilePath.path).use { input ->
            BufferedReader(InputStreamReader(input, Charsets.UTF_8)).useLines { lines ->
                lines.forEach { line ->
                    val file = normalizePath(line.removeSuffix("\n"))
                    if (file.isNotEmpty()) {
                        modifyFiles.add(file)
                        addOnePath(file)
                        oldPath2Header[file] = object : DiffEntry(file) {
                            init {
                                oldPath = file
                            }
                        }
                    }
                }
            }
        }
    }

    @Throws(java.io.IOException::class)
    private fun parseGitDiff(diffFilePath: IResFile) {
        TODO("FIXME - Original parseGitDiff implementation could not be decompiled")
    }

    companion object {
        private val logger: KLogger = TODO("FIXME - Original logger initialization not decompiled")
    }
}