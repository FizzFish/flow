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
   public final val cache: LoadingCache<Pair<IResource, TraverseMode>, Deferred<FileIndexer>>

   private fun getFileIndexer(res: IResource, traverseMode: TraverseMode): Deferred<FileIndexer> {
      val var10000: Any = cache.get(TuplesKt.to(res, traverseMode));
      return var10000 as Deferred<FileIndexer>;
   }

   public suspend fun getIndexer(res: Set<IResource>, traverseMode: TraverseMode): FileIndexerBuilder {
      var `$continuation`: Continuation;
      label42: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label42;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            Object L$1;
            Object L$2;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.getIndexer(null, null, this as Continuation<? super FileIndexerBuilder>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var17: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var union: FileIndexerBuilder;
      var `$this$mapTo$iv$iv`: java.util.Iterator;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            union = new FileIndexerBuilder();
            val `$this$forEach$iv`: java.lang.Iterable = res;
            val `element$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(res, 10));

            for (Object item$iv$iv : $this$map$iv) {
               `element$iv`.add(INSTANCE.getFileIndexer(var25 as IResource, traverseMode));
            }

            `$this$mapTo$iv$iv` = (`element$iv` as java.util.List).iterator();
            break;
         case 1:
            val `item$iv$iv`: FileIndexerBuilder = `$continuation`.L$2 as FileIndexerBuilder;
            `$this$mapTo$iv$iv` = `$continuation`.L$1 as java.util.Iterator;
            union = `$continuation`.L$0 as FileIndexerBuilder;
            ResultKt.throwOnFailure(`$result`);
            `item$iv$iv`.union(`$result` as FileIndexer);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      while ($this$mapTo$iv$iv.hasNext()) {
         val var22: Deferred = `$this$mapTo$iv$iv`.next() as Deferred;
         `$continuation`.L$0 = union;
         `$continuation`.L$1 = `$this$mapTo$iv$iv`;
         `$continuation`.L$2 = union;
         `$continuation`.label = 1;
         val var10000: Any = var22.await(`$continuation`);
         if (var10000 === var17) {
            return var17;
         }

         union.union(var10000 as FileIndexer);
      }

      return union;
   }

   public fun clear() {
      cache.cleanUp();
   }

   @JvmStatic
   fun `cache$lambda$0`(var0: Pair): Deferred {
      val root: IResource = var0.component1() as IResource;
      val traverseMode: FileSystemLocator.TraverseMode = var0.component2() as FileSystemLocator.TraverseMode;
      return BuildersKt.async$default(
         GlobalScope.INSTANCE as CoroutineScope,
         null,
         null,
         (new Function2<CoroutineScope, Continuation<? super FileIndexer>, Object>(root, traverseMode, null) {
            Object L$0;
            int label;

            {
               super(2, `$completionx`);
               this.$root = `$root`;
               this.$traverseMode = `$traverseMode`;
            }

            public final Object invokeSuspend(Object $result) {
               val var4: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               var indexer: FileIndexerBuilder;
               switch (this.label) {
                  case 0:
                     ResultKt.throwOnFailure(`$result`);
                     indexer = new FileIndexerBuilder();
                     val locator: <unrepresentable> = new FileSystemLocator(indexer) {
                        {
                           this.$indexer = `$indexer`;
                        }

                        @Override
                        public void visitFile(IResFile file) {
                           this.$indexer.addIndexMap(file);
                        }
                     };
                     val var10001: Path = this.$root.getPath();
                     val var10002: FileSystemLocator.TraverseMode = this.$traverseMode;
                     val var10003: Continuation = this as Continuation;
                     this.L$0 = indexer;
                     this.label = 1;
                     if (locator.process(var10001, var10002, var10003) === var4) {
                        return var4;
                     }
                     break;
                  case 1:
                     indexer = this.L$0 as FileIndexerBuilder;
                     ResultKt.throwOnFailure(`$result`);
                     break;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }

               return indexer.build();
            }

            public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
               return (new <anonymous constructor>(this.$root, this.$traverseMode, `$completion`)) as Continuation<Unit>;
            }

            public final Object invoke(CoroutineScope p1, Continuation<? super FileIndexer> p2) {
               return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
            }
         }) as Function2,
         3,
         null
      );
   }

   @JvmStatic
   fun {
      val var1: LoadingCache = Caffeine.newBuilder().build(new CacheLoader(FileSystemCacheLocator::cache$lambda$0) {
         {
            this.function = function;
         }
      });
      cache = var1;
   }
}
