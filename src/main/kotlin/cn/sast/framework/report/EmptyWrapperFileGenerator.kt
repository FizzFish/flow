package cn.sast.framework.report

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.ResourceKt
import java.io.IOException
import mu.KLogger
import mu.KotlinLogging

public object EmptyWrapperFileGenerator : IWrapperFileGenerator {
   private final val logger: KLogger = KotlinLogging.INSTANCE.logger(EmptyWrapperFileGenerator::logger$lambda$0)

   public open val name: String
      public open get() {
         return "empty";
      }


   private fun makeWrapperFileContent(resInfo: IBugResInfo): String {
      val var10000: java.lang.String;
      if (resInfo is ClassResInfo) {
         var maxLine: Int = (resInfo as ClassResInfo).getMaxLine();
         if (maxLine > 8000) {
            maxLine = 8000;
         }

         var10000 = StringsKt.repeat("\n", maxLine);
      } else {
         var10000 = "\n";
      }

      return var10000;
   }

   public override fun makeWrapperFile(fileWrapperOutPutDir: IResDirectory, resInfo: IBugResInfo): IResFile? {
      val missingSourceFile: IResFile = fileWrapperOutPutDir.resolve(this.getName()).resolve(this.getInternalFileName(resInfo)).toFile();
      if (missingSourceFile.getExists()) {
         if (missingSourceFile.isFile()) {
            return missingSourceFile;
         } else {
            logger.error(EmptyWrapperFileGenerator::makeWrapperFile$lambda$1);
            return null;
         }
      } else {
         val text: java.lang.String = this.makeWrapperFileContent(resInfo);

         try {
            val var10000: IResource = missingSourceFile.getParent();
            if (var10000 != null) {
               var10000.mkdirs();
            }

            ResourceKt.writeText$default(missingSourceFile, text, null, 2, null);
         } catch (var7: IOException) {
            var7.printStackTrace();
            return null;
         }

         return missingSourceFile.toFile();
      }
   }

   override fun getInternalFileName(resInfo: IBugResInfo): java.lang.String {
      return IWrapperFileGenerator.DefaultImpls.getInternalFileName(this, resInfo);
   }

   @JvmStatic
   fun `logger$lambda$0`(): Unit {
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `makeWrapperFile$lambda$1`(`$missingSourceFile`: IResFile): Any {
      return "duplicate folder exists $`$missingSourceFile`";
   }
}
