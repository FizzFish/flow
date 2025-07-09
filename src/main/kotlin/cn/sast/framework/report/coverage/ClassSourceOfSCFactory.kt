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
        AnalysisCache.defaultBuilder()
            .buildX { key -> XOptional.empty() }     // 默认找不到

    // 注册到全局 AnalysisCache
    override val key: AnalysisDataFactory.Key<ClassSourceInfo?> =
        object : AnalysisDataFactory.Key<ClassSourceInfo?>() {}

    init {
        AnalysisCache.registerFactory(this)
    }
}
