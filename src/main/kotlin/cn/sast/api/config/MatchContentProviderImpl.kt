package cn.sast.api.config

import cn.sast.common.Resource
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.ClassPathMatch.MatchTarget
import java.nio.file.Path
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import soot.SootClass
import soot.SootField
import soot.SootMethod

@SourceDebugExtension(["SMAP\nMatchContentProviderImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MatchContentProviderImpl.kt\ncn/sast/api/config/MatchContentProviderImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,80:1\n1#2:81\n*E\n"])
class MatchContentProviderImpl(val mainConfig: MainConfig) : MatchContentProvider {

    fun getRelativePath(path: Path): String? {
        val relativePath: String
        try {
            relativePath = Resource.INSTANCE.of(path.toAbsolutePath().normalize()).toString()
        } catch (e: Exception) {
            logger.warn { getRelativePath$lambda$0(path, e) }
            return null
        }

        val pathConfig = mainConfig.tryGetRelativePathFromAbsolutePath(relativePath)
        return if (pathConfig.prefix.isNotEmpty()) pathConfig.relativePath else null
    }

    private fun getSourceOfClassMember(declaringClass: SootClass): String? {
        val path = AnalysisCache.G.INSTANCE.class2SourceFile(declaringClass)
        return path?.let { this.getRelativePath(it) }
    }

    override fun getClassPath(classpath: Path): MatchTarget {
        return ProcessRule.ClassPathMatch.MatchTarget(this.getRelativePath(classpath))
    }

    override fun get(file: Path): ProcessRule.FileMatch.MatchTarget {
        return ProcessRule.FileMatch.MatchTarget(this.getRelativePath(file))
    }

    override fun get(sc: SootClass): ProcessRule.ClassMemberMatch.MatchTarget {
        return ProcessRule.ClassMemberMatch.MatchTarget(
            sc.name,
            this.getSourceOfClassMember(sc),
            null,
            null,
            null,
            null
        )
    }

    override fun get(sm: SootMethod): ProcessRule.ClassMemberMatch.MatchTarget {
        return ProcessRule.ClassMemberMatch.MatchTarget(
            sm.declaringClass.name,
            this.getSourceOfClassMember(sm.declaringClass),
            sm.signature,
            sm.name,
            null,
            null
        )
    }

    override fun get(sf: SootField): ProcessRule.ClassMemberMatch.MatchTarget {
        return ProcessRule.ClassMemberMatch.MatchTarget(
            sf.declaringClass.name,
            this.getSourceOfClassMember(sf.declaringClass),
            null,
            null,
            sf.signature,
            sf.name
        )
    }

    @JvmStatic
    fun getRelativePath$lambda$0(path: Path, e: Exception): Any {
        return "Invalid path: [$path], e: ${e.message}"
    }

    @JvmStatic
    fun logger$lambda$3() {
    }

    companion object {
        private val logger: KLogger
    }
}