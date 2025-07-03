package cn.sast.api.util

import java.io.File
import java.util.StringTokenizer
import java.util.regex.Pattern
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nClassPathUtil.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ClassPathUtil.kt\ncn/sast/api/util/ClassPathUtil\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,81:1\n37#2,2:82\n*S KotlinDebug\n*F\n+ 1 ClassPathUtil.kt\ncn/sast/api/util/ClassPathUtil\n*L\n67#1:82,2\n*E\n"])
public object ClassPathUtil {
    public val javaClassPath: Array<String>
        get() {
            val classPath = System.getProperty("java.class.path")
            return classPath.split(":").toTypedArray()
        }

    public fun findCodeBaseInClassPath(codeBaseName: String, classPath: String?): String? {
        if (classPath == null) {
            return null
        } else {
            val tok = StringTokenizer(classPath, File.pathSeparator)

            while (tok.hasMoreTokens()) {
                val t = tok.nextToken()
                if (File(t).name == codeBaseName) {
                    return t
                }
            }

            return null
        }
    }

    public fun findCodeBaseInClassPath(codeBaseNamePattern: Pattern, classPath: String?): String? {
        if (classPath == null) {
            return null
        } else {
            val tok = StringTokenizer(classPath, File.pathSeparator)

            while (tok.hasMoreTokens()) {
                val t = tok.nextToken()
                if (codeBaseNamePattern.matcher(File(t).name).matches()) {
                    return t
                }
            }

            return null
        }
    }
}