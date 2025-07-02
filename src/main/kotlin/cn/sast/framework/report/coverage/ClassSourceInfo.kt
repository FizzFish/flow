package cn.sast.framework.report.coverage

import cn.sast.common.IResFile
import org.jacoco.core.internal.data.CRC64
import org.jacoco.core.internal.instr.InstrSupport

public class ClassSourceInfo(className: String, byteArray: ByteArray, classSource: IResFile?) {
   public final val className: String
   public final val byteArray: ByteArray
   public final val classSource: IResFile?

   public final val jacocoClassId: Long
      public final get() {
         return (this.jacocoClassId$delegate.getValue() as java.lang.Number).longValue();
      }


   init {
      this.className = className;
      this.byteArray = byteArray;
      this.classSource = classSource;
      this.jacocoClassId$delegate = LazyKt.lazy(ClassSourceInfo::jacocoClassId_delegate$lambda$0);
   }

   @JvmStatic
   fun `jacocoClassId_delegate$lambda$0`(`this$0`: ClassSourceInfo): Long {
      return CRC64.classId(`this$0`.byteArray);
   }

   public companion object {
      public operator fun invoke(byteArray: ByteArray): ClassSourceInfo {
         val var10002: java.lang.String = InstrSupport.classReaderFor(byteArray).getClassName();
         return new ClassSourceInfo(StringsKt.replace$default(var10002, "/", ".", false, 4, null), byteArray, null);
      }
   }
}
