package cn.sast.framework

import cn.sast.api.config.MainConfig
import cn.sast.common.FileSystemLocator
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.Resource
import cn.sast.framework.report.AbstractFileIndexer
import cn.sast.framework.report.ProjectFileLocator
import com.feysh.corax.cache.XOptional
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import soot.FoundFile
import soot.IFoundFile
import soot.SourceLocator

@SourceDebugExtension(["SMAP\nSourceLocatorPlus.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SourceLocatorPlus.kt\ncn/sast/framework/SourceLocatorPlus\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,116:1\n1#2:117\n183#3,2:118\n1628#4,3:120\n*S KotlinDebug\n*F\n+ 1 SourceLocatorPlus.kt\ncn/sast/framework/SourceLocatorPlus\n*L\n69#1:118,2\n77#1:120,3\n*E\n"])
class SourceLocatorPlus(mainConfig: MainConfig) : SourceLocator(null) {
    val mainConfig: MainConfig
    private val cacheClassNameMap: LoadingCache<Path, String?>
    private val cacheClassLookMap: LoadingCache<String, XOptional<FoundFile?>>
    private val locator$delegate by lazy { locator_delegate$lambda$10(this) }

    val locator: ProjectFileLocator
        get() = locator$delegate

    init {
        this.mainConfig = mainConfig
        this.cacheClassNameMap = Caffeine.newBuilder()
            .softValues()
            .initialCapacity(5000)
            .build(CacheLoader { path -> cacheClassNameMap$lambda$2(path) })
        
        this.cacheClassLookMap = Caffeine.newBuilder()
            .softValues()
            .initialCapacity(5000)
            .build(CacheLoader { fileName -> cacheClassLookMap$lambda$7(this, fileName) })
    }

    fun update() {
    }

    fun getClassNameOf(cls: IResFile): String? {
        return cacheClassNameMap.get(cls.getPath())
    }

    fun isInvalidClassFile(fileName: String, cls: IResFile): Boolean {
        val className = getClassNameOf(cls)
        return className != null && className == fileName
    }

    override fun lookupInClassPath(fileName: String): IFoundFile? {
        if ("LinearLayout.class" == fileName) {
            return null
        }
        
        val optional = cacheClassLookMap.get(fileName)
        return if (optional != null) {
            optional.getValue()
        } else {
            super.lookupInClassPath(fileName) ?: null
        }
    }

    protected fun lookupInArchive(archivePath: String, fileName: String): IFoundFile? {
        val optional = cacheClassLookMap.get(fileName)
        return optional?.getValue()
    }

    companion object {
        @JvmStatic
        fun cacheClassNameMap$lambda$2(cls: Path): String? {
            try {
                Files.newInputStream(cls).use { input ->
                    val className = SourceLocator.getNameOfClassUnsafe(input)
                    if (className != null) {
                        return "${className.replace('.', '/')}.class"
                    }
                }
            } catch (e: IOException) {
                return null
            }
            return null
        }

        @JvmStatic
        fun cacheClassLookMap$lambda$7(this$0: SourceLocatorPlus, fileName: String): XOptional<FoundFile?> {
            val file = this$0.locator
                .findFromFileIndexMap(
                    fileName.split("/"),
                    AbstractFileIndexer.Companion.getDefaultClassCompareMode()
                )
                .firstOrNull { !this$0.isInvalidClassFile(fileName, it as IResFile) }
            
            return if (file != null) {
                XOptional.of(FoundFile((file as IResFile).getPath()))
            } else {
                XOptional.empty()
            }
        }

        @JvmStatic
        fun locator_delegate$lambda$10(this$0: SourceLocatorPlus): ProjectFileLocator {
            val resources = this$0.mainConfig.getSoot_process_dir()
                .mapTo(LinkedHashSet()) { Resource.INSTANCE.of(it as String) }
            
            return ProjectFileLocator(
                this$0.mainConfig.getMonitor(),
                SourceLocatorPlusKt.sootClassPathsCvt(resources as MutableSet<IResource>),
                null,
                FileSystemLocator.TraverseMode.IndexArchive,
                false
            ).also { it.update() }
        }
    }
}