package cn.sast.api.config

import cn.sast.common.Resource
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.config.api.rules.ProcessRule
import mu.KotlinLogging
import soot.SootClass
import soot.SootField
import soot.SootMethod
import java.nio.file.Path

/**
 * 将 SOOT / Path 等对象映射成 ProcessRule.MatchTarget。
 */
class MatchContentProviderImpl(
    private val mainConfig: MainConfig
) : MatchContentProvider {

    /** Path → 相对路径（相对 projectRoot），失败返回 null */
    fun getRelativePath(path: Path): String? = try {
        val absNorm = path.toAbsolutePath().normalize()
        val absStr  = Resource.of(absNorm).toString()
        val (prefix, rel) = mainConfig.tryGetRelativePath(absStr)
        if (prefix.isNotEmpty()) rel else null
    } catch (e: Exception) {
        logger.warn { "Invalid path: [$path], e: ${e.message}" }
        null
    }

    /* ---------- ClassPath/File ---------- */

    override fun getClassPath(classpath: Path): ProcessRule.ClassPathMatch.MatchTarget =
        ProcessRule.ClassPathMatch.MatchTarget(getRelativePath(classpath))

    override fun get(file: Path): ProcessRule.FileMatch.MatchTarget =
        ProcessRule.FileMatch.MatchTarget(getRelativePath(file))

    /* ---------- SOOT ---------- */

    private fun sourceOf(declaring: SootClass): String? =
        AnalysisCache.G.class2SourceFile(declaring)?.let(::getRelativePath)

    override fun get(sc: SootClass): ProcessRule.ClassMemberMatch.MatchTarget =
        ProcessRule.ClassMemberMatch.MatchTarget(
            sc.name, sourceOf(sc), null, null, null, null
        )

    override fun get(sm: SootMethod): ProcessRule.ClassMemberMatch.MatchTarget =
        ProcessRule.ClassMemberMatch.MatchTarget(
            sm.declaringClass.name,
            sourceOf(sm.declaringClass),
            sm.signature,
            sm.name,
            null,
            null
        )

    override fun get(sf: SootField): ProcessRule.ClassMemberMatch.MatchTarget =
        ProcessRule.ClassMemberMatch.MatchTarget(
            sf.declaringClass.name,
            sourceOf(sf.declaringClass),
            null,
            null,
            sf.signature,
            sf.name
        )

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
