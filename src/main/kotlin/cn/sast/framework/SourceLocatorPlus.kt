package cn.sast.framework

import cn.sast.api.config.MainConfig
import cn.sast.common.FileSystemLocator
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.Resource
import cn.sast.framework.report.AbstractFileIndexer
import cn.sast.framework.report.ProjectFileLocator
import com.feysh.corax.cache.XOptional
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import soot.FoundFile
import soot.IFoundFile
import soot.SourceLocator

@SourceDebugExtension(["SMAP\nSourceLocatorPlus.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SourceLocatorPlus.kt\ncn/sast/framework/SourceLocatorPlus\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,116:1\n1#2:117\n183#3,2:118\n1628#4,3:120\n*S KotlinDebug\n*F\n+ 1 SourceLocatorPlus.kt\ncn/sast/framework/SourceLocatorPlus\n*L\n69#1:118,2\n77#1:120,3\n*E\n"])
public class SourceLocatorPlus(mainConfig: MainConfig) : SourceLocator(null) {
   public final val mainConfig: MainConfig
   private final val cacheClassNameMap: LoadingCache<Path, String?>
   private final val cacheClassLookMap: LoadingCache<String, XOptional<FoundFile?>>

   public final val locator: ProjectFileLocator
      public final get() {
         return this.locator$delegate.getValue() as ProjectFileLocator;
      }


   init {
      this.mainConfig = mainConfig;
      var var2: Caffeine = Caffeine.newBuilder().softValues();
      var2.initialCapacity(5000);
      var var10: LoadingCache = var2.build(new CacheLoader(SourceLocatorPlus::cacheClassNameMap$lambda$2) {
         {
            this.function = function;
         }
      });
      this.cacheClassNameMap = var10;
      var2 = Caffeine.newBuilder().softValues();
      var2.initialCapacity(5000);
      var10 = var2.build(new CacheLoader(SourceLocatorPlus::cacheClassLookMap$lambda$7) {
         {
            this.function = function;
         }
      });
      this.cacheClassLookMap = var10;
      this.locator$delegate = LazyKt.lazy(SourceLocatorPlus::locator_delegate$lambda$10);
   }

   public fun update() {
   }

   public fun getClassNameOf(cls: IResFile): String? {
      return this.cacheClassNameMap.get(cls.getPath()) as java.lang.String;
   }

   public fun isInvalidClassFile(fileName: String, cls: IResFile): Boolean {
      val var10000: java.lang.String = this.getClassNameOf(cls);
      return var10000 != null && var10000 == fileName;
   }

   public open fun lookupInClassPath(fileName: String): IFoundFile? {
      if ("LinearLayout.class" == fileName) {
         return null;
      } else {
         val var2: XOptional = this.cacheClassLookMap.get(fileName) as XOptional;
         label13:
         if (var2 != null) {
            return var2.getValue() as IFoundFile;
         } else {
            val var5: IFoundFile = super.lookupInClassPath(fileName);
            return var5 ?: null;
         }
      }
   }

   protected open fun lookupInArchive(archivePath: String, fileName: String): IFoundFile? {
      val var3: XOptional = this.cacheClassLookMap.get(fileName) as XOptional;
      return if (var3 != null) var3.getValue() as IFoundFile else null;
   }

   @JvmStatic
   fun `cacheClassNameMap$lambda$2`(cls: Path): java.lang.String {
      try {
         label35: {
            val var10001: Array<OpenOption> = new OpenOption[0];
            val var10000: InputStream = Files.newInputStream(cls, Arrays.copyOf(var10001, var10001.length));
            val var1: Closeable = var10000;
            var var2: java.lang.Throwable = null;

            label30: {
               try {
                  try {
                     val var13: java.lang.String = SourceLocator.getNameOfClassUnsafe(var1 as InputStream);
                     if (var13 == null) {
                        break label30;
                     }

                     val var5: java.lang.String = "${StringsKt.replace$default(var13, '.', '/', false, 4, null)}.class";
                  } catch (var7: java.lang.Throwable) {
                     var2 = var7;
                     throw var7;
                  }
               } catch (var8: java.lang.Throwable) {
                  CloseableKt.closeFinally(var1, var2);
               }

               CloseableKt.closeFinally(var1, null);
            }

            CloseableKt.closeFinally(var1, null);
         }
      } catch (var9: IOException) {
         return null;
      }
   }

   @JvmStatic
   fun `cacheClassLookMap$lambda$7`(`this$0`: SourceLocatorPlus, fileName: java.lang.String): XOptional {
      label22: {
         val it: java.util.Iterator = `this$0`.getLocator()
            .findFromFileIndexMap(
               StringsKt.split$default(fileName, new java.lang.String[]{"/"}, false, 0, 6, null), AbstractFileIndexer.Companion.getDefaultClassCompareMode()
            )
            .iterator();

         var var13: Any;
         while (true) {
            if (it.hasNext()) {
               val var6: Any = it.next();
               if (!`this$0`.isInvalidClassFile(fileName, var6 as IResFile)) {
                  continue;
               }

               var13 = var6;
               break;
            }

            var13 = null;
            break;
         }

         return if (var13 as IResFile != null) XOptional.Companion.of(new FoundFile((var13 as IResFile).getPath())) else null;
      }
   }

   @JvmStatic
   fun `locator_delegate$lambda$10`(`this$0`: SourceLocatorPlus): ProjectFileLocator {
      val `$this$mapTo$iv`: java.lang.Iterable = `this$0`.mainConfig.getSoot_process_dir();
      val it: java.util.Collection = new LinkedHashSet();

      for (Object item$iv : $this$mapTo$iv) {
         it.add(Resource.INSTANCE.of(`item$iv` as java.lang.String));
      }

      val var10: ProjectFileLocator = new ProjectFileLocator(
         `this$0`.mainConfig.getMonitor(),
         SourceLocatorPlusKt.sootClassPathsCvt(it as MutableSet<IResource>),
         null,
         FileSystemLocator.TraverseMode.IndexArchive,
         false
      );
      var10.update();
      return var10;
   }
}
