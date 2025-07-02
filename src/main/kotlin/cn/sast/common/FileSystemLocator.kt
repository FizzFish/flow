package cn.sast.common

import cn.sast.api.config.MainConfig
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import kotlinx.coroutines.*
import mu.KLogger
import mu.KotlinLogging
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.time.Duration
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Kotlin‑friendly rewrite of the de‑compiled `FileSystemLocator`.
 *
 * Only **structure & naming** are fixed – business logic that relied on
 * project‑specific classes (`IResFile`, `Resource`, etc.) has been
 * replaced with TODOs so the file compiles and can be refined incrementally.
 */
@Suppress("UNUSED_PARAMETER", "RedundantSuspendModifier")
abstract class FileSystemLocator {

   /* ------------ configuration & runtime state ------------ */

   /** deduplication set to avoid traversing the same archive twice */
   private val visitedArchives: MutableSet<Path> =
      Collections.newSetFromMap(ConcurrentHashMap())

   /* ------------ public coroutine entry point ------------ */

   /**
    * Walk the given [path] and invoke [visitFile] / [visitDirectory]
    * / [visitArchive] callbacks according to [traverseMode].
    */
   suspend fun process(path: Path, traverseMode: TraverseMode = TraverseMode.Default) {
      val result = getWalkTreeResultSafe(path).await()
      visit(result, traverseMode)
   }

   /* ------------ template methods to be specialised by subclasses ------------ */

   /** handle a regular file */
   protected open fun visitFile(file: IResFile) {}

   /** handle a directory */
   protected open fun visitDirectory(dir: IResDirectory) {}

   /**
    * Return *true* if the traversal should recurse into this archive.
    * By default we obey several filter rules derived from the original code.
    */
   protected open fun visitArchive(file: IResFile): Boolean = try {
      when {
         file.extension == "apk" -> false
         MainConfig.excludeFiles.contains(file.name) -> false
         file.resolve("java.base/module-info.java").exists -> false
         file.resolve("jdk.zipfs/module-info.java").exists -> false
         file.isZipLike && file.resolve("AndroidManifest.xml").exists && file.extension != "aar" -> false
         else -> true
      }
   } catch (e: Exception) {
      // ignore & skip on error
      false
   }

   /* ------------ core traversal helpers ------------ */

   private suspend fun visit(result: WalkFileTreeResult, traverseMode: TraverseMode) {
      val archivesToProcess = mutableListOf<IResFile>()

      // first pass – files & dirs
      for (filePath in result.files) {
         val res = Resource.fileOf(filePath) // TODO adapt to your API
         visitFile(res)
         if (traverseMode.processArchive && res.isZipLike) {
            archivesToProcess += res
         }
      }
      for (dirPath in result.dirs) {
         visitDirectory(Resource.dirOf(dirPath)) // TODO adapt
      }

      // second pass – recurse into archives (possibly in parallel)
      if (archivesToProcess.isEmpty()) return

      coroutineScope {
         for (archive in archivesToProcess) {
            if (!visitedArchives.add(archive.path) || !visitArchive(archive)) continue

            launch(Dispatchers.IO) {
               val innerTraverseMode = if (traverseMode == TraverseMode.IndexArchive)
                  TraverseMode.Default else traverseMode

               val innerResult = runCatching { traverseArchive(archive) }
                  .getOrElse { e ->
                     logger.error(e) { "invalid archive file: $archive" }
                     WalkFileTreeResult(archive.path, emptyList(), emptyList())
                  }

               visit(innerResult, innerTraverseMode)
            }
         }
      }
   }

   /** traverse entries inside an archive into paths (placeholder) */
   protected open fun traverseArchive(archive: IResFile): WalkFileTreeResult {
      // TODO: replace with real implementation using your archive API
      return WalkFileTreeResult(archive.path, listOf(archive.path), emptyList())
   }

   /* ------------ companion with cache & util ------------ */

   companion object {
      private val logger: KLogger = KotlinLogging.logger {}

      private val walkTreeCache: LoadingCache<Path, Deferred<WalkFileTreeResult>> =
         Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofSeconds(15))
            .build { path: Path ->
               CoroutineScope(Dispatchers.IO).async {
                  try {
                     if (Files.exists(path)) visit(path) else WalkFileTreeResult(path, emptyList(), emptyList())
                  } catch (e: Exception) {
                     logger.error(e) { "failed to traverse path: $path" }
                     WalkFileTreeResult(path, emptyList(), emptyList())
                  }
               }
            }

      /** obtain (and possibly cache) a [WalkFileTreeResult] for [path] */
      fun getWalkTreeResultSafe(path: Path): Deferred<WalkFileTreeResult> =
         walkTreeCache[path.toAbsolutePath().normalize()]

      /** clear internal caches – useful for unit‑tests */
      fun clear() = walkTreeCache.cleanUp()

      /* ---- local file‑system traversal without archives ---- */
      private fun visit(path: Path): WalkFileTreeResult {
         val files = mutableListOf<Path>()
         val dirs = mutableListOf<Path>()
         val containsRoot = AtomicBoolean(false)

         Files.walkFileTree(path, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
               if (file == path) containsRoot.set(true)
               files += file
               return FileVisitResult.CONTINUE
            }

            override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
               val name = dir.fileName?.toString() ?: return FileVisitResult.SKIP_SUBTREE
               if (name.startsWith("java.") || name.startsWith("jdk.")) {
                  val parent = dir.parent ?: return FileVisitResult.SKIP_SUBTREE
                  if (Files.exists(parent.resolve("java.base")) || Files.exists(parent.resolve("jdk.zipfs"))) {
                     return FileVisitResult.SKIP_SUBTREE
                  }
               }
               return FileVisitResult.CONTINUE
            }

            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
               if (dir == path) containsRoot.set(true)
               dirs += dir
               return FileVisitResult.CONTINUE
            }
         })
         if (!containsRoot.get()) dirs += path
         return WalkFileTreeResult(path, files, dirs)
      }
   }

   /* ------------ data & enums ------------ */

   data class WalkFileTreeResult(val root: Path, val files: List<Path>, val dirs: List<Path>)

   enum class TraverseMode(val processArchive: Boolean) {
      /** only ordinary dirs/files */
      Default(false),

      /** also index archives, but do **not** recurse deeper */
      IndexArchive(true),

      /** recursively index archives inside archives */
      RecursivelyIndexArchive(true)
   }
}

/* ---------- placeholder Project‑specific types ---------- */

// TODO delete these stubs when real project classes are on the classpath
interface IResFile {
   val path: Path
   val name: String
   val extension: String
   val isZipLike: Boolean
   fun resolve(child: String): IResFile
   val entries: List<String>
}
interface IResDirectory { val path: Path }
object Resource {
   fun fileOf(path: Path): IResFile = TODO()
   fun dirOf(path: Path): IResDirectory = TODO()
}
