package cn.sast.common

import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermissions
import java.util.Locale
import kotlin.io.path.name
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 运行时环境信息与常用工具。
 *
 * **保持了原有行为，但改成了更 idiomatic 的 Kotlin 写法：**
 *
 * * `binaryUrl` / `jarBinPath` 用 `lazy {}` 计算；
 * * 删除了无关的 `SourceDebugExtension` 与内部生成的 `$delegate$lambda` 命名；
 * * `getCommandLine()` 逻辑与原版一致，返回 *java 可执行 + JVM 参数 + classpath + 业务参数*；
 * * 去掉了对 `PathExtensionsKt.isRegularFile`、`ResourceKt` 等外部工具的依赖，
 *   用 JDK 自带 API 实现同等功能。
 */
object OS {

   /* ---------- 基本环境 ---------- */

   /** 当前可用 CPU 线程数（启动时固定，不随 cgroup 动态调整） */
   var maxThreadNum: Int = Runtime.getRuntime().availableProcessors()
      internal set

   /** 是否运行在 Windows 平台 */
   val isWindows: Boolean =
      System.getProperty("os.name").lowercase(Locale.getDefault()).contains("windows")

   /** 非 Windows 时要赋予可执行脚本的 POSIX 权限集；Windows 返回 `null` */
   val posixFilePermissions: Set<PosixFilePermission>? =
      if (isWindows) null else PosixFilePermissions.fromString("rwxr--r--")

   /** 启动参数，可由宿主程序写入 */
   var args: Array<String> = emptyArray()
      internal set

   /* ---------- 可执行 / classpath ---------- */

   /**
    * 运行时 **JVM 可执行文件**（绝对路径）。
    * 在部分发行版内核/容器中可能获取不到，返回 `null`。
    */
   val javaExecutableFilePath: String?
      get() = ProcessHandle.current().info().command().orElse(null)

   /**
    * 启动 jar / classes 的物理 URL。
    * - 普通可执行 jar: `file:/xxx/myapp.jar`
    * - Spring Boot fat-jar:  `jar:file:/xxx/myapp.jar!/BOOT-INF/classes/`
    *
    * 对于 Boot-jar 场景，自动转换成外层 jar 的 URL。
    */
   val binaryUrl: URL? by lazy(NONE) { computeBinaryUrl() }

   /**
    * 若当前程序是 **从可执行 jar 启动**，则为该 jar 的 `Path`；否则 `null`。
    */
   val jarBinPath: Path? by lazy(NONE) { computeJarBinPath() }

   /* ---------- 组合命令行 ---------- */

   /**
    * 组装一条“重新启动当前 JVM”所需的命令行：
    *
    * ```
    * <javaExe> [JVM-opts] -cp <binaryUrl.path> [args...]
    * ```
    *
    * @param args       要追加的业务参数；若 `null` 则不追加
    * @param jvmOptions 是否包含 `RuntimeMXBean.inputArguments`
    * @return `null` 表示无法确定 `java` 路径
    */
   fun getCommandLine(
      args: Array<String>? = null,
      jvmOptions: Boolean = true,
   ): List<String>? {
      val javaExe = javaExecutableFilePath ?: return null

      val cmd = mutableListOf<String>()
      cmd += javaExe

      if (jvmOptions) {
         cmd += ManagementFactory.getRuntimeMXBean().inputArguments
      }

      binaryUrl?.path?.let { cp ->
         cmd += listOf("-cp", cp)
      }

      if (args != null) cmd += args

      return cmd
   }

   /* ---------- 私有实现 ---------- */

   /** 解析启动 URL，兼容 Fat-jar 与普通目录/类路径。 */
   private fun computeBinaryUrl(): URL? {
      val location = OS::class.java.protectionDomain
         ?.codeSource
         ?.location
         ?: return null

      val locStr = location.toString()

      // Spring Boot fat-jar 会是 “…/my.jar!/BOOT-INF/classes/”
      return if (
         !locStr.endsWith("BOOT-INF/classes!/") &&
         !locStr.endsWith("BOOT-INF/classes")
      ) {
         location                       // 普通情形，直接返回
      } else {
         // 去掉 “!/BOOT-INF/classes/” → 指向外层 jar
         runCatching {
            URI(locStr.removeSuffix("!/")).toURL()
         }.getOrNull()
      }
   }

   /** 若启动介质是可执行 jar，返回其 `Path`；否则 `null`. */
   private fun computeJarBinPath(): Path? {
      val url = binaryUrl ?: return null
      val path = Path.of(url.toURI())
      return if (Files.isRegularFile(path) && path.fileName.toString().endsWith(".jar"))
         path
      else
         null
   }
}
