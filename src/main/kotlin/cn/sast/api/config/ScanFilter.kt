package cn.sast.api.config

import cn.sast.common.IResDirectory
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.IMatchItem
import com.feysh.corax.config.api.rules.ProcessRule.IMatchTarget
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import com.feysh.corax.config.api.rules.ProcessRule.FileMatch.MatchTarget
import java.io.Closeable
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import java.util.Collections
import java.util.TreeMap
import java.util.TreeSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.JvmStreamsKt
import soot.SootClass
import soot.SootField
import soot.SootMethod

@SourceDebugExtension(["SMAP\nScanFilter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ScanFilter.kt\ncn/sast/api/config/ScanFilter\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n*L\n1#1,106:1\n1#2:107\n1#2:110\n72#3,2:108\n*S KotlinDebug\n*F\n+ 1 ScanFilter.kt\ncn/sast/api/config/ScanFilter\n*L\n96#1:110\n96#1:108,2\n*E\n"])
class ScanFilter(
    val mainConfig: MainConfig,
    private val matchContentProvider: MatchContentProvider = MatchContentProviderImpl(mainConfig)
) : MatchContentProvider {

    lateinit var processRegex: ProcessRegex
        internal set

    private val show: String
        get() {
            if (this@ScanFilter.show != null) {
                val value = this@ScanFilter.show.toString()
                if (value != null) {
                    return value
                }
            }
            return "{no matched rule}"
        }

    private val filterDiagnostics: ConcurrentHashMap<String, MutableSet<String>> = ConcurrentHashMap()

    fun update(value: ProjectConfig) {
        setProcessRegex(value.getProcessRegex())
    }

    fun getActionOf(
        rule: List<IMatchItem>,
        origAction: String?,
        matchTarget: IMatchTarget,
        prefix: String? = null
    ): ScanAction {
        val (rulex, finalAction) = ProcessRule.INSTANCE.matches(rule, matchTarget)
        if (origAction != null) {
            val message = "$origAction -> $finalAction. rule= ${getShow(rulex)}"
            val op = rulex?.op ?: "Keep"
            val pref = prefix ?: ""
            add(message, "$op: $pref $matchTarget")
        }
        return finalAction
    }

    fun getActionOfClassPath(origAction: String?, classpath: Path, prefix: String? = null): ScanAction {
        return getActionOf(processRegex.classpathRules, origAction, getClassPath(classpath), prefix)
    }

    fun getActionOf(origAction: String?, file: Path, prefix: String? = null): ScanAction {
        return getActionOf(processRegex.fileRules, origAction, get(file), prefix)
    }

    fun getActionOf(origAction: String?, sc: SootClass, prefix: String? = null): ScanAction {
        return getActionOf(processRegex.clazzRules, origAction, get(sc), prefix)
    }

    fun getActionOf(origAction: String?, sm: SootMethod, prefix: String? = null): ScanAction {
        return getActionOf(processRegex.clazzRules, origAction, get(sm), prefix)
    }

    fun getActionOf(origAction: String?, sf: SootField, prefix: String? = null): ScanAction {
        return getActionOf(processRegex.clazzRules, origAction, get(sf), prefix)
    }

    fun add(key: String, item: String) {
        val set = filterDiagnostics.getOrPut(key) { Collections.synchronizedSet(TreeSet()) }
        set.add(item)
    }

    fun dump(dir: IResDirectory) {
        dir.mkdirs()
        val path = dir.resolve("scan-classifier-info.json").path
        val outputStream = Files.newOutputStream(path, emptyArray<OpenOption>())
        outputStream.use {
            JvmStreamsKt.encodeToStream(
                jsonFormat,
                ClassFilerRecord.serializer(),
                ClassFilerRecord(TreeMap(filterDiagnostics)),
                it
            )
        }
    }

    override fun get(file: Path): MatchTarget = matchContentProvider.get(file)

    override fun get(sf: SootField): ProcessRule.ClassMemberMatch.MatchTarget = matchContentProvider.get(sf)

    override fun get(sm: SootMethod): ProcessRule.ClassMemberMatch.MatchTarget = matchContentProvider.get(sm)

    override fun get(sc: SootClass): ProcessRule.ClassMemberMatch.MatchTarget = matchContentProvider.get(sc)

    override fun getClassPath(classpath: Path): ProcessRule.ClassPathMatch.MatchTarget = 
        matchContentProvider.getClassPath(classpath)

    @JvmStatic
    private fun JsonBuilder.jsonFormat$lambda$3() {
        this.useArrayPolymorphism = true
        this.prettyPrint = true
    }

    @Serializable
    private class ClassFilerRecord(val filterDiagnostics: Map<String, MutableSet<String>>) {
        companion object {
            val serializer: KSerializer<ClassFilerRecord> = ClassFilerRecord.serializer()
        }
    }

    companion object {
        private val jsonFormat: Json = Json {
            jsonFormat$lambda$3()
        }
    }
}