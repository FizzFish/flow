package cn.sast.api.util

import java.io.File
import java.util.StringTokenizer
import java.util.regex.Pattern
import soot.ModuleUtil
import soot.SootClass
import cn.sast.api.report.ClassResInfo

/**
 * 提供对 `java.class.path` 的快捷访问与搜索。
 *
 * 语义与反编译版保持一致，只是改为更惯用的 Kotlin 写法：
 *
 * * `javaClassPath` 改成 `List<String>`，更方便调用者使用
 * * 统一使用 `File.pathSeparator`（Windows `;`、Linux `:`）
 */
object ClassPathUtil {

    /** 当前进程 `java.class.path` 拆分后的条目列表（顺序保持不变）。 */
    val javaClassPath: List<String>
        get() = System.getProperty("java.class.path")
            .split(File.pathSeparatorChar)

    /**
     * 在给定 classPath 字符串里查找 *文件名精确匹配* 的条目。
     *
     * @param codeBaseName 目标文件名（不含路径）
     * @param classPath    若为 `null` 则直接返回 `null`
     * @return 命中的完整路径；未命中返回 `null`
     */
    fun findCodeBaseInClassPath(
        codeBaseName: String,
        classPath: String? = System.getProperty("java.class.path")
    ): String? =
        classPath?.let { cp ->
            StringTokenizer(cp, File.pathSeparator).asSequence()
                .firstOrNull { File(it).name == codeBaseName }
        }

    /**
     * 在 class-path 中查找 *文件名满足正则* 的条目。
     */
    fun findCodeBaseInClassPath(
        codeBaseNamePattern: Pattern,
        classPath: String? = System.getProperty("java.class.path")
    ): String? =
        classPath?.let { cp ->
            StringTokenizer(cp, File.pathSeparator).asSequence()
                .firstOrNull { codeBaseNamePattern.matcher(File(it).name).matches() }
        }

    /* 把 Java 的 Enumeration/StringTokenizer 转成 Kotlin Sequence */
    private fun StringTokenizer.asSequence(): Sequence<String> = sequence {
        while (hasMoreTokens()) yield(nextToken())
    }
}

/**
 * 拆分 `cn.foo.Bar$Inner` → `cn.foo.Bar` to `Inner`
 * 这里仍然委托给现有 `SootUtilsKt.classSplit`，方便与项目其他代码保持一致。
 */
fun classSplit(cp: ClassResInfo): Pair<String, String> =
    SootUtils.classSplit(cp.sc)

/**
 * 读取注解里的源码路径，并在存在 JPMS 模块时加上 `<module>/` 前缀，
 * 用于生成 SARIF / 报告中的文件路径。
 */
fun getSourcePathModule(c: SootClass): String? =
    SootUtils.getSourcePathFromAnnotation(c)?.let { srcPath ->
        val wrapper: ModuleClassNameWrapper = ModuleUtil.v().makeWrapper(c.name)
        wrapper.moduleName?.let { "${it}/$srcPath" } ?: srcPath
    }