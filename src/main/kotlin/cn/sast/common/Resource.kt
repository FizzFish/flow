package cn.sast.common

import com.github.benmanes.caffeine.cache.Caffeine
import mu.KotlinLogging
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest
import java.time.Duration
import java.util.*
import java.util.zip.ZipException
import kotlin.io.path.*
import java.text.SimpleDateFormat
import java.util.regex.Pattern


/**
 * 文件系统中“某个资源”的抽象：可能是普通文件、目录，也可能是
 * `jar:file:/…!/foo.class` 里的一个条目。
 *
 * ① 维持 **sealed interface** 方便 future exhaustive when
 * ② 内联 `zipLike` 与若干快捷属性
 */
sealed interface IResource : Comparable<IResource> {

   /* ---------- 基本元数据 ---------- */

   val path: Path
   val name: String                                   get() = path.name
   val extension: String                              get() = path.extension
   val exists: Boolean                                get() = Files.exists(path)
   val isFile: Boolean                                get() = Files.isRegularFile(path)
   val isDirectory: Boolean                           get() = Files.isDirectory(path)
   val isRegularFile: Boolean                         get() = isFile
   val absolutePath: String                           get() = path.toAbsolutePath().normalize().toString()
   val absolute: IResource                            get() = Resource.of(path.toAbsolutePath()).normalize
   val normalize: IResource                           get() = Resource.of(path.normalize())
   val isFileScheme: Boolean                          get() = uri.scheme == "file"
   val isJrtScheme: Boolean                           get() = uri.scheme == "jrt"
   val isJarScheme: Boolean                           get() = uri.scheme in setOf("jar", "zip")
   val schemePath: Path                               get() = Resource.uriToPath(uri, getRootFsPath = true)
   val uri: URI                                       get() = path.toUri()
   val url: URL                                       get() = uri.toURL()
   val zipEntry: String?                              get() = Resource.extractEntry(uri)
   val file: File                                     get() = path.toFile()
   val parent: IResource?                             get() = path.parent?.let { Resource.of(it) }

   /* ---------- 派生 ---------- */

   val zipLike: Boolean                               get() = Resource.zipExtensions.any { extension.equals(it, true) }
   val pathString: String                             get() = path.toString()

   /* ---------- 行为 ---------- */

   fun resolve(name: String): IResource               = Resource.of(path.resolve(name))
   fun toFile(): IResFile                             = Resource.of(path).toFile()
   fun toDirectory(): IResDirectory                   = Resource.of(path).toDirectory()
   fun expandRes(outPut: IResDirectory): IResource    = this        // 默认：对普通资源不做展开
   fun mkdirs()                                       = Files.createDirectories(path)

   @OptIn(ExperimentalPathApi::class)
   @Throws(IOException::class)
   fun deleteDirectoryRecursively()                   = path.deleteRecursively()

   @Throws(IOException::class)
   fun deleteDirectoryContents()                      = path.deleteDirectoryContents()

   fun seq(): Sequence<Path>                          = generateSequence(path) { it.parent }

   /* ---------- Comparable ---------- */
   override fun compareTo(other: IResource): Int      = absolutePath.compareTo(other.absolutePath)
}

/**
 * 单个文件资源（含 jar/zip 条目）
 */
interface IResFile : IResource {
   val entries: Set<String>
   val md5: String
   override val absolute:  IResFile
   override val normalize: IResFile
   override fun expandRes(outPut: IResDirectory): IResFile
}

/**
 * 目录资源（本地目录或 jar/zip 内的目录）
 */
interface IResDirectory : IResource {
   override val normalize: IResDirectory
   override val absolute:  IResDirectory
   override fun expandRes(outPut: IResDirectory): IResDirectory
   fun listPathEntries(glob: String = "*"): List<IResource>
}


/**
 * 工具对象：路径 ↔ 资源转换、zip/jar 虚拟 FS 复用、缓存等。
 *
 * 绝大多数逻辑与反编译代码保持一致，但：
 * 1. 使用 Caffeine 替代手写 Map + 软引用
 * 2. 去掉 `$delegate` 相关冗余
 */
object Resource {

   /* ---------- 常量 ---------- */

   const val EXTRACT_DIR_NAME = "archive-extract"
   val zipExtensions = listOf("zip", "jar", "war", "apk", "aar")
   val newFileSystemEnv: Map<String, Any?> = mapOf("create" to true, "zipinfo-time" to false)
   val fileSystemEncodings: List<String> = listOf("UTF-8", "GBK")


   /* ---------- logger ---------- */

   private val logger = KotlinLogging.logger {}

   /* ---------- 缓存 ---------- */

   private val archiveSystemCache = Caffeine.newBuilder()
      .expireAfterAccess(Duration.ofMinutes(10))
      .weakValues()
      .maximumSize(512)
      .build<Path, FileSystem> { path -> createZipFS(path) }

   private val archiveEntriesCache = Caffeine.newBuilder()
      .expireAfterAccess(Duration.ofMinutes(10))
      .maximumSize(50_000)
      .build<Path, Set<String>> { archive -> listZipEntries(archive) }

   /* ---------- 公共 API ---------- */

   fun of(uri: URI): IResource = when (uri.scheme) {
      "file", null -> ResourceBasic(Path.of(uri))
      "jar", "zip" -> {
         val delim = uri.rawSchemeSpecificPart.indexOf("!/")
         require(delim != -1) { "URI must be of form jar:file:/…!/path" }
         val outer = uri.rawSchemeSpecificPart.substring(0, delim)
         val inner = uri.rawSchemeSpecificPart.substring(delim + 2)
         val fs    = getZipFileSystem(Paths.get(uriOf(outer)))
         ResourceBasic(fs.getPath("/$inner"))
      }
      else         -> ResourceBasic(Path.of(uri))
   }

   fun of(url: URL): IResource               = of(url.toURI())
   fun of(path: Path): IResource             = ResourceBasic(path)
   fun of(path: String): IResource           = of(Path.of(path))

   fun dirOf(directory: String): IResDirectory = of(directory).toDirectory()
   fun dirOf(directory: Path):   IResDirectory = of(directory).toDirectory()
                              fun fileOf(file: String):     IResFile      = of(file).toFile()
   fun fileOf(file: Path):       IResFile      = of(file).toFile()

   /** 批量解析 classpath / pathSep=“;” 的字符串集合 */
   fun multipleOf(paths: List<String>): Set<IResource> =
      paths.flatMap { it.split(File.pathSeparator).filter(String::isNotBlank) }
         .mapTo(LinkedHashSet()) { of(it) }

   /* ---------- Zip / Jar ---------- */

   /** 取 zip 内部条目的虚拟 FileSystem（缓存复用） */
   fun getZipFileSystem(archive: Path): FileSystem = archiveSystemCache[archive]

   /** 快速列出 zip 条目（带缓存） */
   fun getEntriesUnsafe(path: Path): Set<String> = archiveEntriesCache[path]

   /** 外部直接读 jar/zip 字节：`jar:file:/…!/foo.class` */
   fun readRaw(uri: URI): ByteArray =
      uriToPath(uri).readBytes()

   /* ---------- 私有工具 ---------- */

   private fun createZipFS(archive: Path): FileSystem =
      try { FileSystems.newFileSystem(archive) }
      catch (_: FileSystemAlreadyExistsException) { FileSystems.getFileSystem(archive.toUri()) }

   private fun listZipEntries(archive: Path): Set<String> =
      ZipFile(archive.toFile()).use { zip ->
         zip.entries.asSequence()
            .filter { !it.name.contains("..") }
            .map(ZipArchiveEntry::getName)
            .toSet()
      }

   internal fun extractEntry(uri: URI): String? =
      uri.rawSchemeSpecificPart.substringAfter("!/", "").takeIf { it.isNotEmpty() }

   /* ---------- uri ↔ path ---------- */

   fun uriOf(raw: String): URI = URI(raw)

   /**
    * 把 jar / zip / file URI 转绝对 Path。
    * @param getRootFsPath 若为 `true`，遇到 jar 内路径时返回“外层归档文件 Path”
    */
   fun uriToPath(uri: URI, getRootFsPath: Boolean = false): Path =
      when (uri.scheme) {
         "file", null -> Path.of(uri)
         "jar", "zip" -> {
            val delim = uri.rawSchemeSpecificPart.indexOf("!/")
            val outer = uri.rawSchemeSpecificPart.substring(0, delim)
            val inner = uri.rawSchemeSpecificPart.substring(delim + 2)
            val outerPath = uriOf(outer).let(::uriToPath)
            if (getRootFsPath) outerPath
            else getZipFileSystem(outerPath).getPath("/$inner")
         }
         else -> error("Unsupported URI: $uri")
      }

   /* ====================================================================== */
   /* ======================== 内部实现类 =================================== */
   /* ====================================================================== */

   /** 统一的基础实现，派生出 File / Directory 两类 */
   open class ResourceBasic(final override val path: Path) : IResource {

      override val uri by lazy { path.toUri() }
      override val url by lazy { uri.toURL() }

      private val isFileCache by lazy { Files.isRegularFile(path) }
      override val isFile get() = isFileCache

      override fun toDirectory(): IResDirectory = ResDirectoryImpl(path)
      override fun toFile(): IResFile         = ResFileImpl(path)

      /** zip 内条目展开到外部目录（见旧代码 ExpandResKey） */
      override fun expandRes(outPut: IResDirectory): IResource {
         val zipEntry = zipEntry ?: return this
         val target   = outPut.resolve(zipEntry).path
         if (!Files.exists(target)) {
            // 把 zip 内部文件提取出来（简化实现）
            val outerZip = schemePath
            ZipFile(outerZip.toFile()).use { zip ->
               zip.getInputStream(zip.getEntry(zipEntry)).use { inStream ->
                  Files.createDirectories(target.parent)
                  Files.copy(inStream, target, StandardCopyOption.REPLACE_EXISTING)
               }
            }
         }
         return of(target)
      }

      override fun mkdirs() = Files.createDirectories(path)

      override fun compareTo(other: IResource): Int = path.compareTo(other.path)

      override fun toString(): String = path.toString()
   }

   /** 目录实现 */
   class ResDirectoryImpl(path: Path) : ResourceBasic(path), IResDirectory {
      override val absolute:  IResDirectory get() = super.absolute as IResDirectory
      override val normalize: IResDirectory get() = super.normalize as IResDirectory
      override fun expandRes(outPut: IResDirectory): IResDirectory =
         super.expandRes(outPut) as IResDirectory

      override fun listPathEntries(glob: String): List<IResource> =
         if (exists && isDirectory)
            path.listDirectoryEntries(glob).map(Resource::of)
         else emptyList()
   }

   /** 文件实现 */
   class ResFileImpl(path: Path) : ResourceBasic(path), IResFile {
      override val absolute:  IResFile get() = super.absolute as IResFile
      override val normalize: IResFile get() = super.normalize as IResFile

      override val entries: Set<String>
         get() = runCatching { getEntriesUnsafe(path) }
            .onFailure { logger.warn(it) { "Failed to list entries of $path" } }
            .getOrDefault(emptySet())

      override val md5: String by lazy { path.inputStream().use { it.readAllBytes().calculate("MD5") } }

      override fun expandRes(outPut: IResDirectory): IResFile =
         super.expandRes(outPut) as IResFile
   }

   /* ---------- 扩展工具 ---------- */

   fun String.calculate(alg: String): String = byteInputStream().calculate(alg)
   fun ByteArray.calculate(alg: String): String = MessageDigest.getInstance(alg)
      .digest(this).joinToString("") { "%02x".format(it) }

   fun InputStream.calculate(alg: String): String =
      MessageDigest.getInstance(alg).let { md ->
         val buf = ByteArray(8 * 1024)
         var n: Int
         while (read(buf).also { n = it } != -1) md.update(buf, 0, n)
         md.digest().joinToString("") { "%02x".format(it) }
      }
}

/**
 * 临时目录路径（可按需生成）
 */
val tempDirectoryPath: String = System.getProperty("java.io.tmpdir")

/**
 * 生成可清理的 SAST 临时目录（带时间戳）
 */
val sAstTempDirectory: Path
   get() {
      val dir = Paths.get(tempDirectoryPath, "0a-sast-corax-can-delete",
         SimpleDateFormat("YYYY.MM.DD.HH.mm").format(System.currentTimeMillis()))
      dir.toFile().mkdirs()
      return dir
   }

/** 常见压缩扩展名 */
val zipExtensions: List<String> = listOf("zip", "jar", "war", "apk", "aar")
/** Recognised source-file extensions. */
val javaExtensions: List<String> = listOf("java", "kt", "kts", "scala", "groovy", "jsp")

/** URL‑escape map for URI helper. */
private val escapes: Map<String, String> = mapOf(
   "[" to "%5B", "]" to "%5D", "{" to "%7B", "}" to "%7D",
   "<" to "%3C", ">" to "%3E", "#" to "%23", "?" to "%3F",
   "@" to "%40", " " to "%20"
)

private val urlEscapePattern =
   Pattern.compile("""([\[\]{}<>#?@ ])""")

/**
 * 创建归档文件系统（支持多编码 fallback）
 */
fun createFileSystem(uri: URI): FileSystem {
   val env = Resource.newFileSystemEnv.toMutableMap()
   val encodings = Resource.fileSystemEncodings
   var lastException: ZipException? = null

   for (encoding in encodings) {
      env["encoding"] = encoding
      try {
         return FileSystems.newFileSystem(uri, env)
      } catch (ex: ZipException) {
         lastException = ex
      }
   }

   throw lastException ?: ZipException("Unknown zip exception")
}

fun createFileSystem(path: Path): FileSystem =
   createFileSystem(path.toUri())

/**
 * 通用输入流校验和
 */
fun calculate(input: InputStream, algorithm: String): String {
   val buffer = ByteArray(8192)
   val md = MessageDigest.getInstance(algorithm)
   var bytesRead: Int

   while (true) {
      bytesRead = input.read(buffer)
      if (bytesRead == -1) break
      md.update(buffer, 0, bytesRead)
   }

   return md.digest().joinToString("") { "%02x".format(it) }
}

/** ByteArray 校验和 */
fun ByteArray.calculate(algorithm: String): String =
   ByteArrayInputStream(this).use { calculate(it, algorithm) }

/** Path 校验和 */
fun Path.calculate(algorithm: String): String =
   inputStream().use { calculate(it, algorithm) }

/** IResFile 校验和 */
fun IResFile.calculate(algorithm: String): String =
   path.calculate(algorithm)

/**
 * 拆解 glob 路径，返回 `(归档文件或目录, glob 部分)` pair。
 */
fun splitGlobPath(p: String): Pair<IResource, String?> {
   var candidate = p.replace("\\", "/").replace("//", "/")
   val globParts = mutableListOf<String>()

   while (true) {
      try {
         val res = Resource.of(candidate)
         if (res.exists) {
            return if (globParts.isEmpty()) res to null
            else res to globParts.joinToString("/")
         }
      } catch (_: Exception) { /* ignore */ }

      if (candidate == "./") {
         error("\"$p\" is an invalid path")
      }

      val lastSlash = candidate.lastIndexOf('/')
      if (lastSlash == -1) {
         globParts.add(0, candidate)
         candidate = "./"
      } else {
         globParts.add(0, candidate.substring(lastSlash + 1))
         candidate = candidate.substring(0, lastSlash)
      }
   }
}

/**
 * 在给定路径下用 `glob` 模式匹配子项
 */
fun globPaths(path: IResource, pattern: String): List<IResource> {
   val matcher = path.path.fileSystem.getPathMatcher("glob:$pattern")
   val results = mutableListOf<IResource>()
   val base = path.path.normalize()

   Files.walkFileTree(base, object : SimpleFileVisitor<Path>() {
      override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
         val rel = base.relativize(file)
         if (matcher.matches(rel)) {
            results += Resource.of(file)
         }
         return FileVisitResult.CONTINUE
      }

      override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
         return visitFile(dir, attrs)
      }
   })

   return results
}

/**
 * 多路径字符串（以 pathSeparator 分割），支持归档内 glob。
 * 例如 `lib.jar!/**/*.class`
 */
fun globPaths(pathString: String): List<IResource>? {
   val parts = pathString.split(File.pathSeparator)
   val all = parts.mapNotNull { globPath(it) }
   val flat = all.flatten()
   return flat.takeIf { it.isNotEmpty() }
}

fun globPath(p: String): List<IResource>? {
   val file = File(p)
   if (file.exists()) {
      return listOf(Resource.of(p))
   }

   // jar!glob 支持
   val parts = p.split("!")
   var current: List<Pair<IResource, String?>> = listOf(splitGlobPath(parts[0]))

   for (i in 1 until parts.size) {
      val glob = parts[i]
      val expanded = mutableListOf<IResource>()
      for ((res, g) in current) {
         val toGlob = g ?: glob
         val matches = globPaths(res, toGlob)
         expanded += matches
      }
      current = expanded.map { it to null }
   }

   return current.map { it.first }.takeIf { it.isNotEmpty() }
}

