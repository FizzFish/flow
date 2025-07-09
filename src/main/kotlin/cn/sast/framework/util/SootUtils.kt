package cn.sast.framework.util

import cn.sast.common.IResFile
import cn.sast.common.JarMerger
import cn.sast.common.Resource
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.googlecode.d2j.dex.Dex2jar
import mu.KotlinLogging
import soot.*
import soot.SourceLocator
import soot.asm.AsmClassSource
import soot.dava.toolkits.base.misc.PackageNamer
import soot.jimple.infoflow.cfg.LibraryClassPatcher
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.createTempFile
import kotlin.io.path.name

/**
 * Misc utilities wrapping *dex2jar* conversion and *class‑file* lookup from Soot artefacts.
 */
object SootUtils {

    private val logger = KotlinLogging.logger {}

    // ---------------------------------------------------------------------
    // 1)  dex → jar conversion helpers (cached under `$TMP/dex2jar/…`)
    // ---------------------------------------------------------------------
    private val dex2jarCache: LoadingCache<Path, Optional<Path>> = Caffeine.newBuilder()
        .maximumSize(128)
        .build { dex -> convertDex(dex) }

    @Throws(Exception::class)
    fun dex2jar(dex: Path, output: Path): Path {
        if (Files.isRegularFile(output)) return output
        Dex2jar.from(dex.toFile()).to(output)
        return output
    }

    private fun convertDex(dex: Path): Optional<Path> = try {
        val outDir = Resource.getZipExtractOutputDir().resolve("dex2jar")
        val out = outDir.resolve("${dex.nameWithoutExtension()}-${dex.hashCode().toUInt()}.jar")
        dex2jar(dex, out); Optional.of(out)
    } catch (e: Exception) {
        logger.warn(e) { "Failed to convert $dex" }; Optional.empty()
    }

    // ---------------------------------------------------------------------
    // 2)  Lookup class‑file or jar‑entry for a Soot‑class (for IDE hyperlinks)
    // ---------------------------------------------------------------------
    fun getClassFileOf(sc: SootClass): IResFile? = when (val src = SourceLocator.v().getClassSource(sc.name)) {
        is AsmClassSource -> src.foundFile()?.let { Resource.fileOf(it) }
        is DexClassSource -> dex2jarCache.get(src.path!!) // Trigger dex → jar; we ignore class‑entry here
            .flatMap { jar -> lookupInArchive(jar, sc.name.replace('.', '/') + ".class") }
            .orElse(null)
        else -> null
    }

    private fun lookupInArchive(jar: Path, entry: String): IResFile? =
        if (Resource.fileOf(jar).entries.contains(entry))
            Resource.fileOf(Resource.archivePath(jar, entry)) else null

    // ---------------------------------------------------------------------
    // 3)  Cleanup helpers
    // ---------------------------------------------------------------------
    fun cleanUp() { dex2jarCache.cleanUp() }

    // small reflection helpers ------------------------------------------------
    private fun AsmClassSource.foundFile(): Path? =
        runCatching {
            val f = this::class.java.getDeclaredField("foundFile").apply { isAccessible = true }
            (f.get(this) as? FoundFile)?.path
        }.getOrNull()

    private fun Path.nameWithoutExtension() = name.substringBeforeLast('.')
}
