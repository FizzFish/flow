package cn.sast.framework.report

import cn.sast.api.report.IBugResInfo
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile

public interface IWrapperFileGenerator {
   public val name: String

   public abstract fun makeWrapperFile(fileWrapperOutPutDir: IResDirectory, resInfo: IBugResInfo): IResFile? {
   }

   public open fun getInternalFileName(resInfo: IBugResInfo): String {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun getInternalFileName(`$this`: IWrapperFileGenerator, resInfo: IBugResInfo): java.lang.String {
         return resInfo.getPath();
      }
   }
}
