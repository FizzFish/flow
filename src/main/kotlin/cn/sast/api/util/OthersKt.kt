@file:SourceDebugExtension(["SMAP\nOthers.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Others.kt\ncn/sast/api/util/OthersKt\n+ 2 Timing.kt\nkotlin/system/TimingKt\n*L\n1#1,48:1\n17#2,6:49\n*S KotlinDebug\n*F\n+ 1 Others.kt\ncn/sast/api/util/OthersKt\n*L\n36#1:49,6\n*E\n"])

package cn.sast.api.util

import com.feysh.corax.config.api.IMethodMatch
import com.feysh.corax.config.api.baseimpl.MatchUtilsKt
import java.io.InputStream
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KClass
import soot.SootClass

public val SootClass.isDummy: Boolean
    get() {
        if (name != "dummyMainMethod") {
            val declaringClass = declaringClass
            if (!declaringClass.isDummy) {
                return false
            }
        }
        return true
    }

public val SootClass.isDummy: Boolean
    get() = name.contains("dummy", ignoreCase = true)

public val SootClass.isSyntheticComponent: Boolean
    get() = name.contains("synthetic", ignoreCase = true) || isDummy

public val SootClass.isSyntheticComponent: Boolean
    get() = name.contains("synthetic", ignoreCase = true) || isDummy

public val SootClass.skipPathSensitive: Boolean
    get() = isDummy || isSyntheticComponent

public fun KClass<*>.asInputStream(): InputStream {
    return java.getResourceAsStream(
        "/${java.name.replace('.', '/')}.class"
    )
}

public fun printMilliseconds(message: String, body: () -> Unit) {
    val start = System.currentTimeMillis()
    body()
    println("$message: ${System.currentTimeMillis() - start} ms")
}

public fun methodSignatureToMatcher(signature: String): IMethodMatch? {
    return when {
        signature.startsWith("<") && signature.endsWith(">") -> MatchUtilsKt.matchSoot(signature)
        signature.contains(":") -> MatchUtilsKt.matchSimpleSig(signature)
        else -> null
    }
}