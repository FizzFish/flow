@file:SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/ReportKt\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n12834#2,3:452\n9326#2,2:456\n9476#2,4:458\n1#3:455\n*S KotlinDebug\n*F\n+ 1 Report.kt\ncn/sast/api/report/ReportKt\n*L\n236#1:452,3\n410#1:456,2\n410#1:458,4\n*E\n"])

package cn.sast.api.report

import cn.sast.api.config.MainConfig
import com.feysh.corax.config.api.BugMessage
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.BugMessage.Env
import java.security.MessageDigest
import java.util.Arrays
import java.util.LinkedHashMap
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension

public val perfectName: String
    get() = "${this.getChecker().javaClass.simpleName}.${this.javaClass.simpleName}"

public fun md5(str: String): ByteArray {
    val digest = MessageDigest.getInstance("MD5")
    val bytes = str.toByteArray(Charsets.UTF_8)
    return digest.digest(bytes)
}

public fun ByteArray.toHex(): String {
    return this.joinToString("") { "%02x".format(it) }
}

public fun ByteArray.xor2Int(): Int {
    var accumulator = 123
    for (byte in this) {
        accumulator = accumulator shl 8 xor byte.toInt()
    }
    return accumulator
}

public fun CheckType.bugMessage(lang: Language, env: Env): String {
    val message = this.getBugMessage()[lang]?.msg?.invoke(env) as? String
    return message ?: "$lang not exists of checkType: $this"
}

public fun CheckType.bugMessage(env: Env): Map<Language, String> {
    return Language.values().associateWith { bugMessage(it, env) }
}

public fun <T> Map<Language, T>.preferredMessage(defaultValue: () -> T): T {
    for (lang in MainConfig.Companion.preferredLanguages) {
        this[lang]?.let { return it }
    }
    return defaultValue()
}