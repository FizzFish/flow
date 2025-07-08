package cn.sast.common

import cn.sast.api.config.MainConfig
import kotlinx.coroutines.*
import mu.KLogger
import mu.KotlinLogging
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.time.Duration
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache

/**
 * Traverse file system with custom hooks for files, dirs, archives.
 */
abstract class FileSystemLocator {

   /** 防止重复遍历同一个归档 */
   private val visitedArchives = Collections.newSetFromMap(ConcurrentHashMap<Path, Boolean>())

   suspend fun process(path: Path, traverseMode: TraverseMode = TraverseMode.Default) {
      val result = getWalkTreeResultSafe(path).await()
      visit(result, traverseMode)
   }

   /** 需子类实现：处理普通文件 */
   protected open fun visitFile(file: IResFile) {}

   /** 需子类实现：处理目录 */
   protected open fun visitDirectory(dir: IResDirectory) {}

   /** 是否进入归档内部 */
   protected open fun visitArchive(file: IResFile): Boolean = try {
      when {
         file.extension == "apk" -> false
         MainConfig.excludeFiles.contains(file.name) -> false
         file.resolve("java.base/module-info.java").exists -> false
         file.resolve("jdk.zipfs/module-info.java").exists -> false
         file.isZipLike && file.resolve("AndroidManifest.xml").exists && file.extension != "aar" -> false
         else -> true
      }
   } catch (_: Exception) { false }

   private suspend fun visit(result: WalkFileTreeResult, traverseMode: TraverseMode) {
      val archives = mutableListOf<IResFile>()

      for (file in result.files) {
         val res = Resource.fileOf(file)
         visitFile(res)
         if (traverseMode.processArchive && res.zipLike) archives += res
      }

      for (dir in result.dirs) {
         visitDirectory(Resource.dirOf(dir))
      }

      if (archives.isEmpty()) return

      coroutineScope {
         for (archive in archives) {
            if (!visitedArchives.add(archive.path) || !visitArchive(archive)) continue

            launch(Dispatchers.IO) {
               val innerMode = if (traverseMode == TraverseMode.IndexArchive) TraverseMode.Default else traverseMode
               val inner = runCatching { traverseArchive(archive) }.getOrElse {
                  logger.error(it) { "Invalid archive: $archive" }
                  WalkFileTreeResult(archive.path, emptyList(), emptyList())
               }
               visit(inner, innerMode)
            }
         }
      }
   }

   protected open fun traverseArchive(archive: IResFile): WalkFileTreeResult =
      WalkFileTreeResult(archive.path, listOf(archive.path), emptyList()) // TODO

   companion object {
      private val logger: KLogger = KotlinLogging.logger {}

      private val walkTreeCache: LoadingCache<Path, Deferred<WalkFileTreeResult>> =
         Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofSeconds(15))
            .build { path ->
               CoroutineScope(Dispatchers.IO).async {
                  if (Files.exists(path)) visit(path) else WalkFileTreeResult(path, emptyList(), emptyList())
               }
            }

      fun getWalkTreeResultSafe(path: Path): Deferred<WalkFileTreeResult> =
         walkTreeCache[path.toAbsolutePath().normalize()]

      fun clear() = walkTreeCache.cleanUp()

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

            override fun postVisitDirectory(dir: Path, exc: java.io.IOException?): FileVisitResult {
               if (dir == path) containsRoot.set(true)
               dirs += dir
               return FileVisitResult.CONTINUE
            }
         })

         if (!containsRoot.get()) dirs += path
         return WalkFileTreeResult(path, files, dirs)
      }
   }

   data class WalkFileTreeResult(val root: Path, val files: List<Path>, val dirs: List<Path>)

   enum class TraverseMode(val processArchive: Boolean) {
      Default(false),
      IndexArchive(true),
      RecursivelyIndexArchive(true)
   }
}
