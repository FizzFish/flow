package cn.sast.api.config

import cn.sast.common.IResDirectory
import cn.sast.common.Resource
import com.feysh.corax.config.api.rules.ProcessRule.IMatchItem
import com.feysh.corax.config.api.rules.ProcessRule.IMatchTarget
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import com.feysh.corax.config.api.rules.ProcessRule.matches
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import mu.KotlinLogging
import soot.SootClass
import soot.SootField
import soot.SootMethod
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * 运行时扫描过滤器：
 * 根据 [ProjectConfig.processRegex] 决定对 classpath/file/class/member 的
 * [ScanAction]（Process / Skip / Keep 等）。
 *
 * 调用顺序：
 *  1. [update] – 注入最新 `ProjectConfig`
 *  2. 对各资源调用 `getActionOf*`
 *  3. 分析结束后可调用 [dump] 把决策记录输出为 JSON
 */
class ScanFilter(
    private val mainConfig: MainConfig,
    private val matchContentProvider: MatchContentProvider = MatchContentProviderImpl(mainConfig)
) : MatchContentProvider by matchContentProvider {

    /** 当前生效的 regex 规则 */
    var processRegex: ProcessRegex = ProcessRegex()
        internal set

    /** key = "origAction -> finalAction. rule=xxx"；value = item list */
    private val filterDiagnostics: ConcurrentMap<String, MutableSet<String>> = ConcurrentHashMap()

    /* ─────────── 对外 API ─────────── */

    fun update(cfg: ProjectConfig) {
        processRegex = cfg.processRegex
    }

    /* ---------- 各类型资源 ---------- */

    fun getActionOfClassPath(orig: String?, classpath: Path, prefix: String? = null): ScanAction =
        decide(processRegex.classpathRules, orig, getClassPath(classpath), prefix)

    fun getActionOf(orig: String?, file: Path, prefix: String? = null): ScanAction =
        decide(processRegex.fileRules, orig, get(file), prefix)

    fun getActionOf(orig: String?, sc: SootClass, prefix: String? = null): ScanAction =
        decide(processRegex.clazzRules, orig, get(sc), prefix)

    fun getActionOf(orig: String?, sm: SootMethod, prefix: String? = null): ScanAction =
        decide(processRegex.clazzRules, orig, get(sm), prefix)

    fun getActionOf(orig: String?, sf: SootField, prefix: String? = null): ScanAction =
        decide(processRegex.clazzRules, orig, get(sf), prefix)

    /* ---------- 决策核心 ---------- */

    private fun decide(
        rule: List<IMatchItem>,
        origAction: String?,
        target: IMatchTarget,
        prefix: String? = null
    ): ScanAction {
        val (matchedRule, finalAction) = rule.matches(target)

        if (origAction != null) {
            val op = matchedRule?.op ?: "Keep"
            val key = "$origAction -> $finalAction. rule= ${matchedRule ?: "{no rule}"}"
            val msg = "$op: ${prefix.orEmpty()} $target"
            filterDiagnostics.computeIfAbsent(key) { Collections.synchronizedSet(TreeSet()) }.add(msg)
        }
        return finalAction
    }

    /* ---------- 诊断信息输出 ---------- */

    @OptIn(ExperimentalSerializationApi::class)
    fun dump(outDir: IResDirectory) {
        outDir.mkdirs()
        val record = ClassFilterRecord(TreeMap(filterDiagnostics))
        Files.newOutputStream(outDir.resolve("scan-classifier-info.json").path).use {
//            jsonFormat.encodeToStream(ClassFilterRecord.serializer(), record, it)
        }
    }

    /* ---------- JSON Helper ---------- */

    @Serializable
    private data class ClassFilterRecord(
        val diagnostics: Map<String, Set<String>>
    )

    companion object {
        private val jsonFormat = Json {
            useArrayPolymorphism = true
            prettyPrint = true
        }
        private val logger = KotlinLogging.logger {}
    }
}
