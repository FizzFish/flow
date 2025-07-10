package cn.sast.api.util

import cn.sast.api.report.ClassResInfo
import soot.ModuleUtil
import soot.SootClass
import java.io.File
import java.util.StringTokenizer
import java.util.regex.Pattern

/**
 * Utility helpers around the current Java class‑path.
 */
object ClassPathUtil {

    /**
     * JVM class‑path split into individual path elements.
     */
    val javaClassPath: Array<String>
        get() = System.getProperty("java.class.path").split(File.pathSeparator).toTypedArray()

    /**
     * Locate a concrete file/jar in an *explicit* class‑path string.
     * @param codeBaseName file name (no path) we are looking for
     * @param classPath    optional “classpath”.  When `null` → *not found*.
     * @return absolute path to the match or `null`.
     */
    fun findCodeBaseInClassPath(codeBaseName: String, classPath: String? = System.getProperty("java.class.path")): String? {
        classPath ?: return null
        val tok = StringTokenizer(classPath, File.pathSeparator)
        while (tok.hasMoreTokens()) {
            val t = tok.nextToken()
            if (File(t).name == codeBaseName) return t
        }
        return null
    }

    /**
     * Regex variant of [findCodeBaseInClassPath].
     */
    fun findCodeBaseInClassPath(codeBaseNamePattern: Pattern, classPath: String? = System.getProperty("java.class.path")): String? {
        classPath ?: return null
        val tok = StringTokenizer(classPath, File.pathSeparator)
        while (tok.hasMoreTokens()) {
            val t = tok.nextToken()
            if (codeBaseNamePattern.matcher(File(t).name).matches()) return t
        }
        return null
    }
}

/**
 * Split fully‑qualified “package.ClassName” → (package, ClassName)
 */
fun classSplit(cp: ClassResInfo): Pair<String, String> = classSplit(cp.sc)

/**
 * Resolve a *source* path for the given [SootClass] considering JPMS module name.
 */
fun getSourcePathModule(c: SootClass): String? {
    val path = c.getSourcePathFromAnnotation() ?: return null
    val wrapper: ModuleUtil.ModuleClassNameWrapper = ModuleUtil.v().makeWrapper(c.name)
    return if (wrapper.moduleName != null) "${wrapper.moduleName}/$path" else path
}