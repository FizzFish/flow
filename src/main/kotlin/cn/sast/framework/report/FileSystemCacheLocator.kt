package cn.sast.framework.report

import cn.sast.common.FileSystemLocator
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.FileSystemLocator.TraverseMode
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import java.nio.file.Path
import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nJavaSourceLocator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JavaSourceLocator.kt\ncn/sast/framework/report/FileSystemCacheLocator\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,490:1\n1557#2:491\n1628#2,3:492\n1863#2,2:495\n*S KotlinDebug\n*F\n+ 1 JavaSourceLocator.kt\ncn/sast/framework/report/FileSystemCacheLocator\n*L\n312#1:491\n312#1:492,3\n312#1:495,2\n*E\n"])
public object FileSystemCacheLocator {
    public val cache: LoadingCache<Pair<IResource, TraverseMode>, Deferred<FileIndexer>> = run {
        Caffeine.newBuilder().build(object : CacheLoader<Pair<IResource, TraverseMode>, Deferred<FileIndexer>> {
            override fun load(key: Pair<IResource, TraverseMode>): Deferred<FileIndexer> {
                return cache$lambda$0(key)
            }
        })
    }

    private fun getFileIndexer(res: IResource, traverseMode: TraverseMode): Deferred<FileIndexer> {
        return cache.get(res to traverseMode)
    }

    public suspend fun getIndexer(res: Set<IResource>, traverseMode: TraverseMode): FileIndexerBuilder {
        return suspendCoroutine { continuation ->
            val union = FileIndexerBuilder()
            val deferredList = res.map { getFileIndexer(it, traverseMode) }
            
            GlobalScope.launch {
                try {
                    deferredList.forEach { deferred ->
                        val indexer = deferred.await()
                        union.union(indexer)
                    }
                    continuation.resume(union)
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
        }
    }

    public fun clear() {
        cache.cleanUp()
    }

    @JvmStatic
    private fun cache$lambda$0(var0: Pair<IResource, TraverseMode>): Deferred<FileIndexer> {
        val root = var0.first
        val traverseMode = var0.second
        return BuildersKt.async(
            GlobalScope,
            context = null,
            start = null,
            block = object : Function2<CoroutineScope, Continuation<in FileIndexer>, Any?> {
                private var L$0: Any? = null
                private var label = 0
                private val $root = root
                private val $traverseMode = traverseMode

                override fun invoke(p1: CoroutineScope, p2: Continuation<in FileIndexer>): Any? {
                    return invokeSuspend(p2)
                }

                override fun invokeSuspend(result: Any): Any? {
                    val suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED()
                    val indexer: FileIndexerBuilder
                    when (label) {
                        0 -> {
                            ResultKt.throwOnFailure(result)
                            indexer = FileIndexerBuilder()
                            val locator = object : FileSystemLocator(indexer) {
                                override fun visitFile(file: IResFile) {
                                    indexer.addIndexMap(file)
                                }
                            }
                            label = 1
                            L$0 = indexer
                            if (locator.process($root.getPath(), $traverseMode, this) == suspended) {
                                return suspended
                            }
                        }
                        1 -> {
                            indexer = L$0 as FileIndexerBuilder
                            ResultKt.throwOnFailure(result)
                        }
                        else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                    }
                    return indexer.build()
                }
            }
        )
    }
}