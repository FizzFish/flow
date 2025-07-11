package cn.sast.framework.report.coverage

import com.feysh.corax.cache.*
import com.github.benmanes.caffeine.cache.LoadingCache

/**
 * AnalysisCache 工厂：<br/>
 *   `ClassSourceOfSCKey` ➜ `ClassSourceInfo?`
 */
object ClassSourceOfSCFactory :
    AnalysisDataFactory<ClassSourceInfo?, ClassSourceOfSCKey> {

    // 缓存：className → ClassSourceInfo(或 null)
    override val cache: LoadingCache<ClassSourceOfSCKey, XOptional<ClassSourceInfo?>> =
        AnalysisCache<ClassSourceOfSCKey, XOptional<ClassSourceInfo?>>()
            .buildX { key -> XOptional.empty() }     // 默认找不到
    private val cache: LoadingCache<ClassSourceOfSCKey, XOptional<ClassSourceInfo>> =
        AnalysisCacheKt.buildX(
            AnalysisDataFactory.Companion.getDefaultBuilder(),
            CacheLoader { key ->
                val classSource: IResFile? =
                    SootUtils.INSTANCE.getClassSourceFromSoot(key.className)

                if (classSource == null || !classSource.exists || !classSource.isFile) {
                    null
                } else {
                    ClassSourceInfo(
                        key.className,
                        ResourceKt.readAllBytes(classSource),
                        classSource
                    )
                }
            }
        )

    // 注册到全局 AnalysisCache
    override val key: AnalysisDataFactory.Key<ClassSourceInfo?> =
        object : AnalysisDataFactory.Key<ClassSourceInfo?>() {}

    init {
        AnalysisCache.G.registerFactory(this)
    }
}
