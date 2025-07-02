package cn.sast.api.report

import cn.sast.common.IResFile
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/FileResInfo\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
public class FileResInfo(sourcePath: IResFile) : IBugResInfo() {
   public final val sourcePath: IResFile

   public final val abs: IResFile
      public final get() {
         return this.abs$delegate.getValue() as IResFile;
      }


   public open val reportFileName: String
      public open get() {
         return this.sourcePath.getName();
      }


   public open val path: String
      public open get() {
         return StringsKt.replace$default(this.getAbs().toString(), ":", "-", false, 4, null);
      }


   init {
      this.sourcePath = sourcePath;
      this.abs$delegate = LazyKt.lazy(FileResInfo::abs_delegate$lambda$0);
   }

   public override fun reportHash(c: IReportHashCalculator): String {
      return c.fromAbsPath(this.getAbs());
   }

   public override fun hashCode(): Int {
      return this.getAbs().toString().hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is FileResInfo && this.getAbs().toString() == (other as FileResInfo).getAbs().toString();
   }

   public open operator fun compareTo(other: IBugResInfo): Int {
      label21:
      if (other !is FileResInfo) {
         val var10000: java.lang.String = this.getClass().getSimpleName();
         val var10001: java.lang.String = other.getClass().getSimpleName();
         return var10000.compareTo(var10001);
      } else {
         val var3: Int = this.sourcePath.compareTo((other as FileResInfo).sourcePath);
         val var2: Int = if (var3.intValue() != 0) var3 else null;
         return if (var2 != null) var2.intValue() else 0;
      }
   }

   public override fun toString(): String {
      return "FileResInfo(file=${this.getAbs()})";
   }

   @JvmStatic
   fun `abs_delegate$lambda$0`(`this$0`: FileResInfo): IResFile {
      return `this$0`.sourcePath.getAbsolute().getNormalize();
   }
}
