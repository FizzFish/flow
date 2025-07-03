package cn.sast.framework.report

import cn.sast.api.config.MainConfig
import cn.sast.api.config.ScanFilter
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.ProjectMetrics
import cn.sast.api.report.Report
import cn.sast.api.util.IMonitor
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.ResourceKt
import cn.sast.framework.report.coverage.JacocoCompoundCoverage
import cn.sast.idfa.progressbar.ProgressBarExt
import com.feysh.corax.config.api.rules.ProcessRule
import com.github.ajalt.mordant.rendering.Theme
import java.io.Closeable
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import soot.Scene
import soot.SootClass
import soot.util.Chain

@SourceDebugExtension(["SMAP\nReportConverter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ReportConverter.kt\ncn/sast/framework/report/ReportConverter\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,391:1\n774#2:392\n865#2,2:393\n774#2:395\n865#2,2:396\n1279#2,2:398\n1293#2,4:400\n1053#2:404\n*S KotlinDebug\n*F\n+ 1 ReportConverter.kt\ncn/sast/framework/report/ReportConverter\n*L\n53#1:392\n53#1:393,2\n83#1:395\n83#1:396,2\n84#1:398,2\n84#1:400,4\n96#1:404\n*E\n"])
class ReportConverter(mainConfig: MainConfig, progressBarExt: ProgressBarExt = ProgressBarExt(0, 0, 3, null)) {
    val mainConfig: MainConfig
    private val progressBarExt: ProgressBarExt

    init {
        this.mainConfig = mainConfig
        this.progressBarExt = progressBarExt
    }

    private fun filterSourceFiles(sources: Collection<IResFile?>): Set<IResFile> {
        val destination = ArrayList<IResFile>()

        for (element in sources) {
            val it = element as IResFile
            if (element != null &&
                (!(it.extension == "kts") || !it.name.contains("gradle", true)) &&
                !it.name.contains("package-info", true) &&
                mainConfig.scanFilter.getActionOf(path = it.path) != ProcessRule.ScanAction.Skip
            ) {
                destination.add(it)
            }
        }

        return destination.filterNotNullTo(LinkedHashSet())
    }

    private suspend fun findAllSourceFiles(locator: IProjectFileLocator): Set<IResFile> {
        val allSourceFiles = LinkedHashSet<IResFile>()
        
        for (javaExtension in ResourceKt.getJavaExtensions()) {
            val files = locator.getByFileExtension(javaExtension)
            allSourceFiles.addAll(files.filter { 
                mainConfig.autoAppSrcInZipScheme || it.isFileScheme() 
            })
        }
        
        return allSourceFiles
    }

    private fun reportSourceFileWhichClassNotFound(
        allSourceFiles: Set<IResFile>,
        outputDir: IResDirectory,
        locator: IProjectFileLocator
    ): Pair<MutableSet<IResFile>, Set<IResFile>> {
        mainConfig.monitor?.projectMetrics?.totalSourceFileNum = allSourceFiles.size.toLong()

        val foundClasses = Scene.v().applicationClasses + Scene.v().libraryClasses
        val nonPhantomClasses = foundClasses.filter { !(it as SootClass).isPhantom }
        
        val classToFileMap = LinkedHashMap<SootClass, IResFile>(
            kotlin.math.max(kotlin.collections.mapCapacity(nonPhantomClasses.size), 16)
        
        for (sootClass in nonPhantomClasses) {
            classToFileMap[sootClass] = locator.get(ClassResInfo(sootClass), NullWrapperFileGenerator.INSTANCE)
        }

        val foundSourceFiles = classToFileMap.values.filterNotNullTo(LinkedHashSet())
        val classNotFoundSourceFiles = allSourceFiles - foundSourceFiles

        val outputFile = outputDir.resolve("source_files_which_class_not_found.txt").toFile()
        if (classNotFoundSourceFiles.isNotEmpty()) {
            logger.warn { 
                "Incomplete analysis! The num of ${classNotFoundSourceFiles.size} source files not found any class!!! check: ${outputFile.absolutePath.normalize()}" 
            }
            
            outputFile.parentFile.mkdirs()
            OutputStreamWriter(Files.newOutputStream(outputFile.toPath()), Charsets.UTF_8).use { writer ->
                classNotFoundSourceFiles.sortedBy { it.path }.forEach { file ->
                    writer.write("${file.path}\n")
                }
                writer.flush()
            }
        } else {
            Files.deleteIfExists(outputFile.toPath())
        }

        return foundSourceFiles to classNotFoundSourceFiles
    }

    public suspend fun flush(
        mainConfig: MainConfig,
        locator: IProjectFileLocator,
        coverage: JacocoCompoundCoverage,
        consumers: List<IReportConsumer>,
        reports: Collection<Report>,
        outputDir: IResDirectory
    ) {
        coroutineScope {
            // TODO("FIXME — Original decompilation failed, preserve original behavior")
            throw NotImplementedError("Original decompilation failed for flush implementation")
        }
    }

    companion object {
        val logger: KLogger = TODO("Initialize logger properly")
    }
}