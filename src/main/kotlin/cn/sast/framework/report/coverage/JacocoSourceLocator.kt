package cn.sast.framework.report.coverage

import cn.sast.api.report.ClassResInfo
import cn.sast.common.IResFile
import cn.sast.common.ResourceKt
import cn.sast.framework.report.AbstractFileIndexer
import cn.sast.framework.report.IProjectFileLocator
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import kotlin.jvm.internal.SourceDebugExtension
import org.apache.commons.io.FilenameUtils
import org.jacoco.report.InputStreamSourceFileLocator
import soot.Scene
import soot.SootClass

@SourceDebugExtension(["SMAP\nJacocoSourceLoator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JacocoSourceLoator.kt\ncn/sast/framework/report/coverage/JacocoSourceLocator\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,38:1\n1#2:39\n*E\n"])
public class JacocoSourceLocator(sourceLocator: IProjectFileLocator, encoding: String = "utf-8", tabWidth: Int = 4) : InputStreamSourceFileLocator(
      encoding, tabWidth
   ) {
   private final val sourceLocator: IProjectFileLocator

   init {
      this.sourceLocator = sourceLocator;
   }

   protected open fun getSourceStream(path: String): InputStream? {
      val ext: IResFile = SequencesKt.firstOrNull(
         this.sourceLocator
            .findFromFileIndexMap(
               StringsKt.split$default(path, new char[]{'/', '\\'}, false, 0, 6, null), AbstractFileIndexer.Companion.getDefaultClassCompareMode()
            )
      ) as IResFile;
      if (ext != null) {
         val var10: Path = ext.getPath();
         val var13: Array<OpenOption> = new OpenOption[0];
         val var11: InputStream = Files.newInputStream(var10, Arrays.copyOf(var13, var13.length));
         return var11;
      } else {
         val var6: java.lang.String = FilenameUtils.getExtension(path);
         if (ResourceKt.getJavaExtensions().contains(var6)) {
            val sc: SootClass = Scene.v()
               .getSootClassUnsafe(
                  StringsKt.replace$default(
                     StringsKt.replace$default(StringsKt.removeSuffix(StringsKt.removeSuffix(path, var6), "."), "/", ".", false, 4, null),
                     "\\",
                     ".",
                     false,
                     4,
                     null
                  ),
                  false
               );
            if (sc != null) {
               val src: IResFile = IProjectFileLocator.DefaultImpls.get$default(this.sourceLocator, ClassResInfo.Companion.of(sc), null, 2, null);
               if (src != null) {
                  val var10000: Path = src.getPath();
                  val var12: Array<OpenOption> = new OpenOption[0];
                  val var9: InputStream = Files.newInputStream(var10000, Arrays.copyOf(var12, var12.length));
                  return var9;
               }
            }
         }

         return null;
      }
   }
}
