package cn.sast.framework.report

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.FileResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.api.util.IMonitor
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.ResourceImplKt
import cn.sast.common.ResourceKt
import cn.sast.common.FileSystemLocator.TraverseMode
import cn.sast.framework.report.AbstractFileIndexer.CompareMode
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import java.io.IOException
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.Map.Entry
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import mu.KLogger

public open class ProjectFileLocator(monitor: IMonitor?,
      sourceDir: Set<IResource>,
      fileWrapperOutPutDir: IResDirectory?,
      traverseMode: TraverseMode,
      enableInfo: Boolean = true
   ) :
   IProjectFileLocator {
   private final val monitor: IMonitor?

   public open var sourceDir: Set<IResource>
      internal final set

   private final val fileWrapperOutPutDir: IResDirectory?
   private final var traverseMode: TraverseMode
   private final val enableInfo: Boolean
   private final var updateJob: Deferred<FileIndexer>?
   private final val loader: CacheLoader<Pair<IBugResInfo, IWrapperFileGenerator>, IResFile?>
   private final val cache: LoadingCache<Pair<IBugResInfo, IWrapperFileGenerator>, IResFile?>

   init {
      this.monitor = monitor;
      this.sourceDir = sourceDir;
      this.fileWrapperOutPutDir = fileWrapperOutPutDir;
      this.traverseMode = traverseMode;
      this.enableInfo = enableInfo;
      this.loader = new CacheLoader(this) {
         {
            this.this$0 = `$receiver`;
         }

         public final IResFile load(Pair<? extends IBugResInfo, ? extends IWrapperFileGenerator> var1) {
            val resInfo: IBugResInfo = var1.component1() as IBugResInfo;
            val fileWrapperIfNotEExists: IWrapperFileGenerator = var1.component2() as IWrapperFileGenerator;
            val var10000: IResFile;
            if (resInfo is ClassResInfo) {
               var10000 = ProjectFileLocator.access$get(this.this$0, resInfo as ClassResInfo, fileWrapperIfNotEExists);
            } else {
               if (resInfo !is FileResInfo) {
                  throw new NoWhenBranchMatchedException();
               }

               var10000 = ProjectFileLocator.access$get(this.this$0, resInfo as FileResInfo, fileWrapperIfNotEExists);
            }

            return var10000;
         }
      };
      val var6: Caffeine = Caffeine.newBuilder().softValues();
      var6.maximumSize(8000L);
      val var10001: LoadingCache = var6.build(this.loader);
      this.cache = var10001;
   }

   private suspend fun indexer(): FileIndexer {
      if (this.updateJob == null) {
         throw new IllegalStateException("update at first!".toString());
      } else {
         return this.updateJob.await(`$completion`);
      }
   }

   private fun indexerBlock(): FileIndexer {
      if (this.updateJob == null) {
         throw new IllegalStateException("update at first!".toString());
      } else {
         val job: Deferred = this.updateJob;
         return if (this.updateJob.isCompleted())
            job.getCompleted() as FileIndexer
            else
            BuildersKt.runBlocking$default(null, (new Function2<CoroutineScope, Continuation<? super FileIndexer>, Object>(this, null) {
               int label;

               {
                  super(2, `$completionx`);
                  this.this$0 = `$receiver`;
               }

               public final Object invokeSuspend(Object $result) {
                  val var2: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  var var10000: Any;
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        var10000 = this.this$0;
                        val var10001: Continuation = this as Continuation;
                        this.label = 1;
                        var10000 = ProjectFileLocator.access$indexer((ProjectFileLocator)var10000, var10001);
                        if (var10000 === var2) {
                           return var2;
                        }
                        break;
                     case 1:
                        ResultKt.throwOnFailure(`$result`);
                        var10000 = `$result`;
                        break;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }

                  return var10000;
               }

               public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                  return (new <anonymous constructor>(this.this$0, `$completion`)) as Continuation<Unit>;
               }

               public final Object invoke(CoroutineScope p1, Continuation<? super FileIndexer> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }
            }) as Function2, 1, null) as FileIndexer;
      }
   }

   public override fun update() {
      if (this.updateJob != null) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         this.updateJob = BuildersKt.async$default(
            GlobalScope.INSTANCE as CoroutineScope,
            null,
            null,
            (
               new Function2<CoroutineScope, Continuation<? super FileIndexer>, Object>(this, null)// $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getVarDefinitions()" because "stat" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1468)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingExprent(VarDefinitionHelper.java:1679)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1496)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1545)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.remapClashingNames(VarDefinitionHelper.java:1458)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.rerunClashing(VarProcessor.java:99)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:118)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:352)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.toJava(NewExprent.java:407)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:761)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:727)
      
            ) as Function2,
            3,
            null
         );
         if (this.updateJob != null) {
            this.updateJob.start();
         }
      }
   }

   public override fun findFromFileIndexMap(parentSubPath: List<String>, mode: CompareMode): Sequence<IResFile> {
      return this.indexerBlock().findFromFileIndexMap(parentSubPath, mode);
   }

   public fun totalFiles(): Long {
      var c: Long = 0L;

      for (Entry x : this.indexerBlock().getFileNameToPathMap$corax_framework().entrySet()) {
         c += (x.getValue() as java.util.Set).size();
      }

      return c;
   }

   public fun totalJavaSrcFiles(): Long {
      val extensionToPathMap: java.util.Map = this.indexerBlock().getExtensionToPathMap$corax_framework();
      var count: Long = 0L;

      for (java.lang.String ext : ResourceKt.getJavaExtensions()) {
         val var10000: java.util.Set = extensionToPathMap.get(ext) as java.util.Set;
         count += if (var10000 != null) var10000.size() else 0;
      }

      return count;
   }

   private fun makeWrapperFile(resInfo: IBugResInfo, fileWrapperIfNotEExists: IWrapperFileGenerator): IResFile? {
      return if (this.fileWrapperOutPutDir == null) null else fileWrapperIfNotEExists.makeWrapperFile(this.fileWrapperOutPutDir, resInfo);
   }

   private fun get(resInfo: ClassResInfo, fileWrapperIfNotEExists: IWrapperFileGenerator): IResFile? {
      val found: IResFile = this.indexerBlock().findAnyFile(resInfo.getSourceFile(), AbstractFileIndexer.Companion.getDefaultClassCompareMode());
      var var10000: IResFile = found;
      if (found == null) {
         var10000 = this.makeWrapperFile(resInfo, fileWrapperIfNotEExists);
      }

      return var10000;
   }

   private fun get(resInfo: FileResInfo, fileWrapperIfNotEExists: IWrapperFileGenerator): IResFile? {
      return if (resInfo.getSourcePath().getExists()) resInfo.getSourcePath() else this.makeWrapperFile(resInfo, fileWrapperIfNotEExists);
   }

   public override fun get(resInfo: IBugResInfo, fileWrapperIfNotEExists: IWrapperFileGenerator): IResFile? {
      return this.cache.get(TuplesKt.to(resInfo, fileWrapperIfNotEExists)) as IResFile;
   }

   public override suspend fun getByFileExtension(extension: String): Sequence<IResFile> {
      return getByFileExtension$suspendImpl(this, extension, `$completion`);
   }

   public override suspend fun getByFileName(filename: String): Sequence<IResFile> {
      return getByFileName$suspendImpl(this, filename, `$completion`);
   }

   public override suspend fun getAllFiles(): Sequence<IResFile> {
      return getAllFiles$suspendImpl(this, `$completion`);
   }

   public override fun toString(): String {
      return "Source-Locator@${System.identityHashCode(this)}";
   }

   @JvmStatic
   fun `getAllFiles$lambda$1`(it: Entry): java.lang.Iterable {
      return it.getValue() as java.lang.Iterable;
   }

   @JvmStatic
   fun `logger$lambda$2`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger

      public fun findJdkSources(home: IResFile): List<IResFile> {
         val result: java.util.List = new ArrayList();
         var srcZip: IResource = home.resolve("lib").resolve("src.zip");
         if (!Files.isReadable(srcZip.getPath())) {
            val var10000: IResource = home.getParent();
            srcZip = var10000.resolve("src.zip");
         }

         label59:
         if (Files.isReadable(srcZip.getPath())) {
            var zipFO: FileSystem = null;

            try {
               try {
                  var var18: URI = URI.create("jar:${srcZip.getPath().toUri()}");
                  zipFO = ResourceImplKt.createFileSystem(var18);
                  var18 = zipFO.getRootDirectories().iterator().next();
                  val ex: Path = var18 as Path;
                  var var10003: java.lang.String = zipFO.getSeparator();
                  if (Files.exists(ex.resolve(StringsKt.replace$default("java/lang/Object.java", "/", var10003, false, 4, null)))) {
                     result.add(srcZip.toFile());
                  } else {
                     var10003 = zipFO.getSeparator();
                     if (Files.exists(ex.resolve(StringsKt.replace$default("java.base/java/lang/Object.java", "/", var10003, false, 4, null)))) {
                        result.add(srcZip.toFile());
                     }
                  }
               } catch (var11: IOException) {
                  ProjectFileLocator.access$getLogger$cp().debug(ProjectFileLocator.Companion::findJdkSources$lambda$0);
               }
            } catch (var12: java.lang.Throwable) {
               if (zipFO != null) {
                  try {
                     zipFO.close();
                  } catch (var8: IOException) {
                     ProjectFileLocator.access$getLogger$cp().debug(ProjectFileLocator.Companion::findJdkSources$lambda$1);
                  }
               }
            }

            if (zipFO != null) {
               try {
                  zipFO.close();
               } catch (var9: IOException) {
                  ProjectFileLocator.access$getLogger$cp().debug(ProjectFileLocator.Companion::findJdkSources$lambda$1);
               }
            }

            if (zipFO != null) {
               try {
                  zipFO.close();
               } catch (var10: IOException) {
                  ProjectFileLocator.access$getLogger$cp().debug(ProjectFileLocator.Companion::findJdkSources$lambda$1);
               }
            }
         }
      }

      @JvmStatic
      fun `findJdkSources$lambda$0`(`$ex`: IOException): Any {
         return "$`$ex`, findSources()";
      }

      @JvmStatic
      fun `findJdkSources$lambda$1`(`$ex`: IOException): Any {
         return "$`$ex`, findSources()";
      }
   }
}
