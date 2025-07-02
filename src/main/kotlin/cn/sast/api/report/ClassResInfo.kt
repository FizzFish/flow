package cn.sast.api.report

import cn.sast.api.util.SootUtilsKt
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import soot.ClassMember
import soot.SootClass
import soot.SootMethod
import soot.SourceLocator
import soot.tagkit.Host

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/ClassResInfo\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
public data class ClassResInfo(sc: SootClass) : IBugResInfo() {
   public final val sc: SootClass

   public open val path: String
      public open get() {
         return (CollectionsKt.first(this.getSourceFile()) as java.lang.String).toString();
      }


   public final val maxLine: Int
      public final get() {
         return (this.maxLine$delegate.getValue() as java.lang.Number).intValue();
      }


   public final val sourcePath: String?
      public final get() {
         return this.sourcePath$delegate.getValue() as java.lang.String;
      }


   public final val sourceFile: LinkedHashSet<String>
      public final get() {
         return this.sourceFile$delegate.getValue() as LinkedHashSet<java.lang.String>;
      }


   public open val reportFileName: String
      public open get() {
         var var10000: java.lang.String = this.getSourcePath();
         if (var10000 != null) {
            val var3: java.util.List = StringsKt.split$default(var10000, new java.lang.String[]{"/"}, false, 0, 6, null);
            if (var3 != null) {
               var10000 = CollectionsKt.lastOrNull(var3) as java.lang.String;
               if (var10000 != null) {
                  return var10000;
               }
            }
         }

         return "${SourceLocator.v().getSourceForClass(this.sc.getShortJavaStyleName())}.java";
      }


   init {
      this.sc = sc;
      this.maxLine$delegate = LazyKt.lazy(ClassResInfo::maxLine_delegate$lambda$0);
      this.sourcePath$delegate = LazyKt.lazy(ClassResInfo::sourcePath_delegate$lambda$1);
      this.sourceFile$delegate = LazyKt.lazy(ClassResInfo::sourceFile_delegate$lambda$2);
   }

   public open operator fun compareTo(other: IBugResInfo): Int {
      label21:
      if (other !is ClassResInfo) {
         val var8: java.lang.String = this.getClass().getSimpleName();
         val var9: java.lang.String = other.getClass().getSimpleName();
         return var8.compareTo(var9);
      } else {
         val var10000: java.lang.String = this.sc.getName();
         val var10001: java.lang.String = (other as ClassResInfo).sc.getName();
         val var3: Int = var10000.compareTo(var10001);
         val var2: Int = if (var3.intValue() != 0) var3 else null;
         return if (var2 != null) var2.intValue() else 0;
      }
   }

   public override fun reportHash(c: IReportHashCalculator): String {
      return c.from(this.sc);
   }

   public operator fun component1(): SootClass {
      return this.sc;
   }

   public fun copy(sc: SootClass = this.sc): ClassResInfo {
      return new ClassResInfo(sc);
   }

   public override fun toString(): String {
      return "ClassResInfo(sc=${this.sc})";
   }

   public override fun hashCode(): Int {
      return this.sc.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ClassResInfo) {
         return false;
      } else {
         return this.sc == (other as ClassResInfo).sc;
      }
   }

   @JvmStatic
   fun `maxLine_delegate$lambda$0`(`this$0`: ClassResInfo): Int {
      return SootUtilsKt.getNumCode(`this$0`.sc);
   }

   @JvmStatic
   fun `sourcePath_delegate$lambda$1`(`this$0`: ClassResInfo): java.lang.String {
      return SootUtilsKt.getSourcePath(`this$0`.sc);
   }

   @JvmStatic
   fun `sourceFile_delegate$lambda$2`(`this$0`: ClassResInfo): LinkedHashSet {
      return SootUtilsKt.getPossibleSourceFiles(`this$0`.sc);
   }

   public companion object {
      public fun of(sc: SootClass): ClassResInfo {
         return new ClassResInfo(sc);
      }

      public fun of(sm: SootMethod): ClassResInfo {
         val var10001: SootClass = sm.getDeclaringClass();
         return this.of(var10001);
      }

      public fun of(sootDecl: Host): ClassResInfo {
         val var10000: SootClass;
         if (sootDecl is SootClass) {
            var10000 = sootDecl as SootClass;
         } else {
            if (sootDecl !is ClassMember) {
               throw new IllegalStateException(("Unsupported sootDecl type: $sootDecl ${sootDecl.getClass()}").toString());
            }

            var10000 = (sootDecl as ClassMember).getDeclaringClass();
         }

         return new ClassResInfo(var10000);
      }
   }
}
