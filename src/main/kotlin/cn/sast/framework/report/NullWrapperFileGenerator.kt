package cn.sast.framework.report

import cn.sast.api.report.IBugResInfo
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile

public object NullWrapperFileGenerator : IWrapperFileGenerator {
   public open val name: String
      public open get() {
         return "null";
      }


   public override fun makeWrapperFile(fileWrapperOutPutDir: IResDirectory, resInfo: IBugResInfo): IResFile? {
      return null;
   }

   override fun getInternalFileName(resInfo: IBugResInfo): java.lang.String {
      return IWrapperFileGenerator.DefaultImpls.getInternalFileName(this, resInfo);
   }
}
