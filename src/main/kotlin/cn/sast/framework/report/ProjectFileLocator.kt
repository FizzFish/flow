package cn.sast.framework.report

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.FileResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.api.util.IMonitor
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.ResourceImplKt
import cn.sast.common.ResourceKt
import cn.sast.common.FileSystemLocator.TraverseMode
import cn.sast.framework.report.AbstractFileIndexer.CompareMode
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import java.io.IOException
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.Map.Entry
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.*
import mu.KLogger
import mu.KotlinLogging

public open class ProjectFileLocator(
    monitor: IMonitor?,
    sourceDir: Set<IResource>,
    fileWrapperOutPutDir: IResDirectory?,
    traverseMode: TraverseMode,
    enableInfo: Boolean = true
) : IProjectFileLocator {
    private val monitor: IMonitor?
    public open var sourceDir: Set<IResource>
        internal set
    private val fileWrapperOutPutDir: IResDirectory?
    private var traverseMode: TraverseMode
    private val enableInfo: Boolean
    private var updateJob: Deferred<FileIndexer>? = null
    private val loader: CacheLoader<Pair<IBugResInfo, IWrapperFileGenerator>, IResFile?>
    private val cache: LoadingCache<Pair<IBugResInfo, IWrapperFileGenerator>, IResFile?>

    init {
        this.monitor = monitor
        this.sourceDir = sourceDir
        this.fileWrapperOutPutDir = fileWrapperOutPutDir
        this.traverseMode = traverseMode
        this.enableInfo = enableInfo
        this.loader = object : CacheLoader<Pair<IBugResInfo, IWrapperFileGenerator>, IResFile?> {
            override fun load(var1: Pair<IBugResInfo, IWrapperFileGenerator>): IResFile? {
                val resInfo = var1.component1()
                val fileWrapperIfNotEExists = var1.component2()
                return when (resInfo) {
                    is ClassResInfo -> this@ProjectFileLocator.get(resInfo, fileWrapperIfNotEExists)
                    is FileResInfo -> this@ProjectFileLocator.get(resInfo, fileWrapperIfNotEExists)
                    else -> throw NoWhenBranchMatchedException()
                }
            }
        }
        this.cache = Caffeine.newBuilder()
            .softValues()
            .maximumSize(8000L)
            .build(this.loader)
    }

    private suspend fun indexer(): FileIndexer {
        if (this.updateJob == null) {
            throw IllegalStateException("update at first!")
        } else {
            return this.updateJob!!.await()
        }
    }

    private fun indexerBlock(): FileIndexer {
        if (this.updateJob == null) {
            throw IllegalStateException("update at first!")
        } else {
            val job = this.updateJob!!
            return if (job.isCompleted) {
                job.getCompleted() as FileIndexer
            } else {
                runBlocking {
                    indexer()
                }
            }
        }
    }

    public override fun update() {
        if (this.updateJob != null) {
            throw IllegalStateException("Check failed.")
        } else {
            this.updateJob = GlobalScope.async {
                TODO("FIXME — Original decompilation failed")
            }
            this.updateJob?.start()
        }
    }

    public override fun findFromFileIndexMap(parentSubPath: List<String>, mode: CompareMode): Sequence<IResFile> {
        return this.indexerBlock().findFromFileIndexMap(parentSubPath, mode)
    }

    public fun totalFiles(): Long {
        var c = 0L
        for (x in this.indexerBlock().getFileNameToPathMap$corax_framework().entries) {
            c += (x.value as Set<*>).size
        }
        return c
    }

    public fun totalJavaSrcFiles(): Long {
        val extensionToPathMap = this.indexerBlock().getExtensionToPathMap$corax_framework()
        var count = 0L
        for (ext in ResourceKt.getJavaExtensions()) {
            val set = extensionToPathMap[ext] as? Set<*>
            count += set?.size ?: 0
        }
        return count
    }

    private fun makeWrapperFile(resInfo: IBugResInfo, fileWrapperIfNotEExists: IWrapperFileGenerator): IResFile? {
        return fileWrapperOutPutDir?.let { fileWrapperIfNotEExists.makeWrapperFile(it, resInfo) }
    }

    private fun get(resInfo: ClassResInfo, fileWrapperIfNotEExists: IWrapperFileGenerator): IResFile? {
        val found = this.indexerBlock().findAnyFile(
            resInfo.getSourceFile(),
            AbstractFileIndexer.Companion.getDefaultClassCompareMode()
        )
        return found ?: makeWrapperFile(resInfo, fileWrapperIfNotEExists)
    }

    private fun get(resInfo: FileResInfo, fileWrapperIfNotEExists: IWrapperFileGenerator): IResFile? {
        return if (resInfo.getSourcePath().getExists()) resInfo.getSourcePath() else makeWrapperFile(resInfo, fileWrapperIfNotEExists)
    }

    public override fun get(resInfo: IBugResInfo, fileWrapperIfNotEExists: IWrapperFileGenerator): IResFile? {
        return this.cache.get(resInfo to fileWrapperIfNotEExists)
    }

    public override suspend fun getByFileExtension(extension: String): Sequence<IResFile> {
        return suspendCoroutineUninterceptedOrReturn { cont ->
            TODO("FIXME — Implement getByFileExtension$suspendImpl")
        }
    }

    public override suspend fun getByFileName(filename: String): Sequence<IResFile> {
        return suspendCoroutineUninterceptedOrReturn { cont ->
            TODO("FIXME — Implement getByFileName$suspendImpl")
        }
    }

    public override suspend fun getAllFiles(): Sequence<IResFile> {
        return suspendCoroutineUninterceptedOrReturn { cont ->
            TODO("FIXME — Implement getAllFiles$suspendImpl")
        }
    }

    public override fun toString(): String {
        return "Source-Locator@${System.identityHashCode(this)}"
    }

    @JvmStatic
    fun `getAllFiles$lambda$1`(it: Entry<*, *>): Iterable<*> {
        return it.value as Iterable<*>
    }

    @JvmStatic
    fun `logger$lambda$2`() {
    }

    public companion object {
        private val logger = KotlinLogging.logger {}

        public fun findJdkSources(home: IResFile): List<IResFile> {
            val result = ArrayList<IResFile>()
            var srcZip = home.resolve("lib").resolve("src.zip")
            if (!Files.isReadable(srcZip.getPath())) {
                srcZip = home.getParent()?.resolve("src.zip") ?: return result
            }

            if (Files.isReadable(srcZip.getPath())) {
                var zipFO: FileSystem? = null
                try {
                    try {
                        val uri = URI.create("jar:${srcZip.getPath().toUri()}")
                        zipFO = ResourceImplKt.createFileSystem(uri)
                        val root = zipFO.rootDirectories.iterator().next()
                        val separator = zipFO.separator
                        if (Files.exists(root.resolve("java/lang/Object.java".replace("/", separator))) {
                            result.add(srcZip.toFile())
                        } else if (Files.exists(root.resolve("java.base/java/lang/Object.java".replace("/", separator)))) {
                            result.add(srcZip.toFile())
                        }
                    } catch (e: IOException) {
                        logger.debug { "$e, findSources()" }
                    }
                } finally {
                    try {
                        zipFO?.close()
                    } catch (e: IOException) {
                        logger.debug { "$e, findSources()" }
                    }
                }
            }
            return result
        }

        @JvmStatic
        fun `findJdkSources$lambda$0`(`$ex`: IOException): Any {
            return "$`$ex`, findSources()"
        }

        @JvmStatic
        fun `findJdkSources$lambda$1`(`$ex`: IOException): Any {
            return "$`$ex`, findSources()"
        }
    }
}