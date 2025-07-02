@file:SourceDebugExtension(["SMAP\nMainConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MainConfig.kt\ncn/sast/api/config/MainConfigKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 SerializersModuleBuilders.kt\nkotlinx/serialization/modules/SerializersModuleBuildersKt\n*L\n1#1,463:1\n1#2:464\n31#3,2:465\n254#3,9:467\n33#3:476\n*S KotlinDebug\n*F\n+ 1 MainConfig.kt\ncn/sast/api/config/MainConfigKt\n*L\n51#1:465,2\n52#1:467,9\n51#1:476\n*E\n"])

package cn.sast.api.config

import cn.sast.api.incremental.IncrementalAnalyze
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.common.IResDirectory
import cn.sast.common.IResource
import cn.sast.common.Resource
import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.feysh.corax.config.api.IAnalysisDepends
import com.feysh.corax.config.api.PhantomAnalysisDepends
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.Arrays
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.modules.SerializersModule

public final val serializersModule: SerializersModule
public final val yamlConfiguration: YamlConfiguration =
   new YamlConfiguration(true, false, null, PolymorphismStyle.Tag, null, 0, 200, SequenceStyle.Block, null, null, null, 0, false, null, 16182, null)
   public final val yamlFormat: Yaml

public fun MainConfig.simpleIAnalysisDepends(): IAnalysisDepends {
   val var2: IncrementalAnalyze = `$this$simpleIAnalysisDepends`.getIncrementAnalyze();
   var analysisDepends: IAnalysisDepends = if ((var2 as? IncrementalAnalyzeByChangeFiles) != null)
      (var2 as? IncrementalAnalyzeByChangeFiles).getSimpleDeclAnalysisDependsGraph()
      else
      null;
   if (analysisDepends == null) {
      analysisDepends = PhantomAnalysisDepends.INSTANCE;
   }

   return analysisDepends;
}

public fun MainConfig.interProceduralAnalysisDepends(): IAnalysisDepends {
   val var2: IncrementalAnalyze = `$this$interProceduralAnalysisDepends`.getIncrementAnalyze();
   var analysisDepends: IAnalysisDepends = if ((var2 as? IncrementalAnalyzeByChangeFiles) != null)
      (var2 as? IncrementalAnalyzeByChangeFiles).getInterProceduralAnalysisDependsGraph()
      else
      null;
   if (analysisDepends == null) {
      analysisDepends = PhantomAnalysisDepends.INSTANCE;
   }

   return analysisDepends;
}

public fun MainConfig.skipResourceInArchive(res: IResource): Boolean {
   if (!res.isJarScheme()) {
      return false;
   } else {
      try {
         return !`$this$skipResourceInArchive`.getSourcePathZFS().contains(Resource.INSTANCE.getZipFileSystem(res.getSchemePath()));
      } catch (var3: Exception) {
         return true;
      }
   }
}

public fun checkerInfoDir(configDirs: List<IResource>, stopOnError: Boolean = true): IResDirectory? {
   if (configDirs.size() != 1) {
      if (stopOnError) {
         throw new IllegalStateException(("Only one plugin folder could be exists: $configDirs").toString());
      } else {
         return null;
      }
   } else {
      val analysisConfigDir: Path = (CollectionsKt.single(configDirs) as IResource).getPath();
      val checkerInfoDir: Path = analysisConfigDir.normalize();
      var var10001: Array<LinkOption> = new LinkOption[0];
      if (!Files.exists(checkerInfoDir, Arrays.copyOf(var10001, var10001.length))) {
         if (stopOnError) {
            throw new IllegalStateException(("$checkerInfoDir is not exists").toString());
         } else {
            return null;
         }
      } else {
         var var21: Boolean;
         label71: {
            var10001 = new LinkOption[0];
            if (Files.exists(checkerInfoDir, Arrays.copyOf(var10001, var10001.length))) {
               val var10000: Path = checkerInfoDir.resolve("checker_info.csv");
               val var9: Array<LinkOption> = new LinkOption[0];
               if (Files.exists(var10000, Arrays.copyOf(var9, var9.length))) {
                  var21 = true;
                  break label71;
               }
            }

            var21 = false;
         }

         var checkerInfoDirInConfigProjectDevPath: Path = if (var21) checkerInfoDir else null;
         if ((if (var21) checkerInfoDir else null) != null) {
            return Resource.INSTANCE.dirOf(checkerInfoDirInConfigProjectDevPath);
         } else {
            label65: {
               checkerInfoDirInConfigProjectDevPath = analysisConfigDir.resolve("../../Canary/analysis-config").normalize();
               var10001 = new LinkOption[0];
               if (Files.exists(checkerInfoDirInConfigProjectDevPath, Arrays.copyOf(var10001, var10001.length))) {
                  val var22: Path = checkerInfoDirInConfigProjectDevPath.resolve("checker_info.csv");
                  val var10: Array<LinkOption> = new LinkOption[0];
                  if (Files.exists(var22, Arrays.copyOf(var10, var10.length))) {
                     var21 = true;
                     break label65;
                  }
               }

               var21 = false;
            }

            val var5: Path = if (var21) checkerInfoDirInConfigProjectDevPath else null;
            if ((if (var21) checkerInfoDirInConfigProjectDevPath else null) != null) {
               return Resource.INSTANCE.dirOf(var5);
            } else if (stopOnError) {
               throw new IllegalStateException(("checker_info.csv not exists in $checkerInfoDir").toString());
            } else {
               return null;
            }
         }
      }
   }
}

@JvmSynthetic
fun `checkerInfoDir$default`(var0: java.util.List, var1: Boolean, var2: Int, var3: Any): IResDirectory {
   if ((var2 and 2) != 0) {
      var1 = true;
   }

   return checkerInfoDir(var0, var1);
}
