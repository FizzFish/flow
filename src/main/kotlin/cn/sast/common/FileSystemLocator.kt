package cn.sast.common

import cn.sast.api.config.MainConfig
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.time.Duration
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.HashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.enums.EnumEntries
import kotlin.io.path.PathsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Ref.BooleanRef
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

public abstract class FileSystemLocator {
   private final val visitedArchive: MutableSet<Path>

   open fun FileSystemLocator() {
      val var10001: java.util.Set = Collections.synchronizedSet(new HashSet(1000));
      this.visitedArchive = var10001;
   }

   public suspend fun process(path: Path, traverseMode: cn.sast.common.FileSystemLocator.TraverseMode) {
      var `$continuation`: Continuation;
      label27: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label27;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            Object L$1;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.process(null, null, this as Continuation<? super Unit>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var7: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var var4: FileSystemLocator;
      var var10000: Any;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            var4 = this;
            var10000 = Companion.getWalkTreeResultSafe(path);
            `$continuation`.L$0 = traverseMode;
            `$continuation`.L$1 = this;
            `$continuation`.label = 1;
            var10000 = (Deferred)var10000.await(`$continuation`);
            if (var10000 === var7) {
               return var7;
            }
            break;
         case 1:
            var4 = `$continuation`.L$1 as FileSystemLocator;
            traverseMode = `$continuation`.L$0 as FileSystemLocator.TraverseMode;
            ResultKt.throwOnFailure(`$result`);
            var10000 = (Deferred)`$result`;
            break;
         case 2:
            ResultKt.throwOnFailure(`$result`);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      val var10001: WalkFileTreeResult = var10000 as WalkFileTreeResult;
      `$continuation`.L$0 = null;
      `$continuation`.L$1 = null;
      `$continuation`.label = 2;
      return if (var4.visit(var10001, traverseMode, `$continuation`) === var7) var7 else Unit.INSTANCE;
   }

   private suspend fun visit(res: WalkFileTreeResult, traverseMode: cn.sast.common.FileSystemLocator.TraverseMode) {
      val archives: java.util.List = new ArrayList();

      for (Path it : res.getFiles()) {
         val file: IResFile = Resource.INSTANCE.fileOf(dir);
         this.visitFile(file);
         if (traverseMode.getProcessArchive() && file.getZipLike()) {
            archives.add(file);
         }
      }

      for (Path dir : res.getDirs()) {
         this.visitDirectory(Resource.INSTANCE.dirOf(var9));
      }

      if (archives.isEmpty()) {
         return Unit.INSTANCE;
      } else {
         val var10000: Any = CoroutineScopeKt.coroutineScope(
            (
               new Function2<CoroutineScope, Continuation<? super Unit>, Object>(archives, this, traverseMode, null) {
                  int label;

                  {
                     super(2, `$completionx`);
                     this.$archives = `$archives`;
                     this.this$0 = `$receiver`;
                     this.$traverseMode = `$traverseMode`;
                  }

                  public final Object invokeSuspend(Object $result) {
                     IntrinsicsKt.getCOROUTINE_SUSPENDED();
                     switch (this.label) {
                        case 0:
                           ResultKt.throwOnFailure(`$result`);
                           val `$this$coroutineScope`: CoroutineScope = this.L$0 as CoroutineScope;
                           val `$this$forEach$iv`: java.lang.Iterable = this.$archives;
                           val var4: FileSystemLocator = this.this$0;
                           val var5: FileSystemLocator.TraverseMode = this.$traverseMode;

                           for (Object element$iv : $this$forEach$iv) {
                              val archiveLike: IResFile = `element$iv` as IResFile;
                              if (FileSystemLocator.access$getVisitedArchive$p(var4).add((`element$iv` as IResFile).getPath())
                                 && var4.visitArchive(`element$iv` as IResFile)) {
                                 BuildersKt.launch$default(
                                    `$this$coroutineScope`,
                                    null,
                                    null,
                                    (
                                       new Function2<CoroutineScope, Continuation<? super Unit>, Object>(var5, var4, archiveLike, null) {
                                          int label;

                                          {
                                             super(2, `$completionx`);
                                             this.$traverseMode = `$traverseMode`;
                                             this.this$0 = `$receiver`;
                                             this.$archiveLike = `$archiveLike`;
                                          }

                                          public final Object invokeSuspend(Object $result) {
                                             val var6: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                             switch (this.label) {
                                                case 0:
                                                   ResultKt.throwOnFailure(`$result`);
                                                   val traverseModeIn: FileSystemLocator.TraverseMode = if (this.$traverseMode === FileSystemLocator.TraverseMode.IndexArchive)
                                                      FileSystemLocator.TraverseMode.Default
                                                      else
                                                      this.$traverseMode;

                                                   var var4: WalkFileTreeResult;
                                                   try {
                                                      var4 = FileSystemLocator.access$traverseArchive(this.this$0, this.$archiveLike);
                                                   } catch (var7: Exception) {
                                                      FileSystemLocator.access$getLogger$cp().error(<unrepresentable>::invokeSuspend$lambda$0);
                                                      FileSystemLocator.access$getLogger$cp().debug(var7, <unrepresentable>::invokeSuspend$lambda$1);
                                                      var4 = new WalkFileTreeResult(
                                                         this.$archiveLike.getPath(), CollectionsKt.emptyList(), CollectionsKt.emptyList()
                                                      );
                                                   }

                                                   val var10000: FileSystemLocator = this.this$0;
                                                   val var10003: Continuation = this as Continuation;
                                                   this.label = 1;
                                                   if (FileSystemLocator.access$visit(var10000, var4, traverseModeIn, var10003) === var6) {
                                                      return var6;
                                                   }
                                                   break;
                                                case 1:
                                                   ResultKt.throwOnFailure(`$result`);
                                                   break;
                                                default:
                                                   throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                             }

                                             return Unit.INSTANCE;
                                          }

                                          public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                             return (new <anonymous constructor>(this.$traverseMode, this.this$0, this.$archiveLike, `$completion`)) as Continuation<Unit>;
                                          }

                                          public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                             return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                                          }

                                          private static final Object invokeSuspend$lambda$0(IResFile $archiveLike, Exception $e) {
                                             return "invalid archive file: `$`$archiveLike``. e: ${`$e`.getMessage()}";
                                          }

                                          private static final Object invokeSuspend$lambda$1(IResFile $archiveLike) {
                                             return "invalid archive file: `$`$archiveLike``.";
                                          }
                                       }
                                    ) as Function2,
                                    3,
                                    null
                                 );
                              }
                           }

                           return Unit.INSTANCE;
                        default:
                           throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                     }
                  }

                  public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                     val var3: Function2 = new <anonymous constructor>(this.$archives, this.this$0, this.$traverseMode, `$completion`);
                     var3.L$0 = value;
                     return var3 as Continuation<Unit>;
                  }

                  public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                     return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                  }
               }
            ) as Function2,
            `$completion`
         );
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
      }
   }

   private fun traverseArchive(archiveLike: IResFile): WalkFileTreeResult {
      val files: java.util.List = new ArrayList();
      val dirs: java.util.List = new ArrayList();
      files.add(archiveLike.getPath());
      val errorCnt: Int = 0;

      for (java.lang.String entry : archiveLike.getEntries()) {
         try {
            val innerPath: Path = Resource.INSTANCE.archivePath(archiveLike.getPath(), entry);
            if (PathExtensionsKt.isDirectory(innerPath)) {
               dirs.add(innerPath);
            } else {
               files.add(innerPath);
            }
         } catch (var10: Exception) {
            logger.error(FileSystemLocator::traverseArchive$lambda$0);
            logger.debug(var10, FileSystemLocator::traverseArchive$lambda$1);
            if (errorCnt++ >= 10) {
               logger.error(FileSystemLocator::traverseArchive$lambda$2);
               break;
            }
         }
      }

      return new WalkFileTreeResult(archiveLike.getPath(), files, dirs);
   }

   public open fun visitFile(file: IResFile) {
   }

   public open fun visitArchive(file: IResFile): Boolean {
      try {
         if (file.getExtension() == "apk") {
            return false;
         }

         if (MainConfig.Companion.getExcludeFiles().contains(file.getName())) {
            return false;
         }

         if (file.resolve("java.base/module-info.java").getExists()) {
            return false;
         }

         if (file.resolve("jdk.zipfs/module-info.java").getExists()) {
            return false;
         }

         if (file.getZipLike() && file.resolve("AndroidManifest.xml").getExists() && !(file.getExtension() == "aar")) {
            return false;
         }
      } catch (var3: Exception) {
      }

      return true;
   }

   public open fun visitDirectory(dir: IResDirectory) {
   }

   @JvmStatic
   fun `traverseArchive$lambda$0`(`$archiveLike`: IResFile, `$entry`: java.lang.String, `$e`: Exception): Any {
      return "invalid inner zip file: `$`$archiveLike`!/$`$entry`` ${`$e`.getClass()} ${`$e`.getMessage()}";
   }

   @JvmStatic
   fun `traverseArchive$lambda$1`(`$archiveLike`: IResFile, `$entry`: java.lang.String): Any {
      return "invalid inner zip file: `$`$archiveLike`!/$`$entry``";
   }

   @JvmStatic
   fun `traverseArchive$lambda$2`(`$archiveLike`: IResFile): Any {
      return "Skip invalid zip file: `$`$archiveLike``";
   }

   @JvmStatic
   fun `logger$lambda$3`(): Unit {
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun {
      val var10000: LoadingCache = Caffeine.newBuilder()
         .expireAfterAccess(Duration.ofSeconds(15L))
         .build(
            new CacheLoader<Path, Deferred<? extends WalkFileTreeResult>>() {
               public Deferred<WalkFileTreeResult> load(Path path) {
                  return BuildersKt.async$default(
                     CoroutineScopeKt.CoroutineScope(Dispatchers.getIO() as CoroutineContext),
                     null,
                     null,
                     (new Function2<CoroutineScope, Continuation<? super WalkFileTreeResult>, Object>(path, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$path = `$path`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val `$this$async`: CoroutineScope = this.L$0 as CoroutineScope;
                                 val var4: Path = this.$path;
                                 val var10001: Array<LinkOption> = new LinkOption[0];
                                 if (Files.exists(var4, Arrays.copyOf(var10001, var10001.length))) {
                                    try {
                                       return FileSystemLocator.Companion.access$visit(FileSystemLocator.Companion, var4);
                                    } catch (var7: Exception) {
                                       FileSystemLocator.access$getLogger$cp().error(<unrepresentable>::invokeSuspend$lambda$1$lambda$0);
                                    }
                                 }

                                 return new WalkFileTreeResult(this.$path, CollectionsKt.emptyList(), CollectionsKt.emptyList());
                              default:
                                 throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                           }
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                           val var3: Function2 = new <anonymous constructor>(this.$path, `$completion`);
                           var3.L$0 = value;
                           return var3 as Continuation<Unit>;
                        }

                        public final Object invoke(CoroutineScope p1, Continuation<? super WalkFileTreeResult> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }

                        private static final Object invokeSuspend$lambda$1$lambda$0(Path $path, Exception $e) {
                           return "failed to traverse path: `$`$path`` ${`$e`.getMessage()}";
                        }
                     }) as Function2,
                     3,
                     null
                  );
               }
            }
         );
      walkTreeCache = var10000;
   }

   public companion object {
      private final val logger: KLogger
      private final val walkTreeCache: LoadingCache<Path, Deferred<WalkFileTreeResult>>

      public fun getWalkTreeResultSafe(path: Path): Deferred<WalkFileTreeResult> {
         var var10000: Path = path.toAbsolutePath();
         var10000 = (Path)FileSystemLocator.access$getWalkTreeCache$cp().get(var10000.normalize());
         return var10000 as Deferred<WalkFileTreeResult>;
      }

      private fun visit(path: Path): WalkFileTreeResult {
         val files: java.util.List = new ArrayList();
         val dirs: java.util.List = new ArrayList();
         val containsSelf: BooleanRef = new BooleanRef();
         Files.walkFileTree(path, new SimpleFileVisitor<Path>(path, containsSelf, files, dirs) {
            {
               this.$path = `$path`;
               this.$containsSelf = `$containsSelf`;
               this.$files = `$files`;
               this.$dirs = `$dirs`;
            }

            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
               if (file == this.$path) {
                  this.$containsSelf.element = true;
               }

               this.$files.add(file);
               return FileVisitResult.CONTINUE;
            }

            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
               val name: java.lang.String = PathsKt.getName(dir);
               if (StringsKt.startsWith$default(name, "java.", false, 2, null) || StringsKt.startsWith$default(name, "jdk.", false, 2, null)) {
                  val parent: Path = dir.getParent();
                  var var10000: Path = parent.resolve("java.base");
                  var var6: Array<LinkOption> = new LinkOption[0];
                  if (Files.exists(var10000, Arrays.copyOf(var6, var6.length))) {
                     return FileVisitResult.SKIP_SUBTREE;
                  }

                  var10000 = parent.resolve("jdk.zipfs");
                  var6 = new LinkOption[0];
                  if (Files.exists(var10000, Arrays.copyOf(var6, var6.length))) {
                     return FileVisitResult.SKIP_SUBTREE;
                  }
               }

               val var10: FileVisitResult = super.preVisitDirectory(dir, attrs);
               return var10;
            }

            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
               if (dir == this.$path) {
                  this.$containsSelf.element = true;
               }

               this.$dirs.add(dir);
               return FileVisitResult.CONTINUE;
            }
         });
         if (!containsSelf.element) {
            dirs.add(path);
         }

         return new WalkFileTreeResult(path, files, dirs);
      }

      public fun clear() {
         FileSystemLocator.access$getWalkTreeCache$cp().cleanUp();
      }
   }

   public enum class TraverseMode(processArchive: Boolean) {
      Default(false),
      IndexArchive(true),
      RecursivelyIndexArchive(true)
      public final val processArchive: Boolean

      init {
         this.processArchive = processArchive;
      }

      @JvmStatic
      fun getEntries(): EnumEntries<FileSystemLocator.TraverseMode> {
         return $ENTRIES;
      }
   }
}
