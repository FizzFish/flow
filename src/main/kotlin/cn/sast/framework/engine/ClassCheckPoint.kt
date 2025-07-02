package cn.sast.framework.engine

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.cache.AnalysisDataFactory.Key
import com.feysh.corax.cache.analysis.SootHostExtend
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.IClassCheckPoint
import com.feysh.corax.config.api.IFieldCheckPoint
import com.feysh.corax.config.api.IMethodCheckPoint
import com.feysh.corax.config.api.report.Region
import com.github.javaparser.ast.body.BodyDeclaration
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.tagkit.AbstractHost

public class ClassCheckPoint(sootClass: SootClass, info: SootInfoCache) : CheckPoint, IClassCheckPoint, SootInfoCache {
   public open val sootClass: SootClass
   public final val info: SootInfoCache

   public open val className: String
      public open get() {
         val var10000: java.lang.String = this.getSootClass().getName();
         return var10000;
      }


   public open val region: Region
      public open get() {
         var var10000: Region = Region.Companion.invoke(this, this.getSootClass() as AbstractHost);
         if (var10000 == null) {
            var10000 = Region.Companion.getERROR();
         }

         return var10000;
      }


   public open val file: IBugResInfo

   internal open val env: DefaultEnv
      internal open get() {
         return this.env$delegate.getValue() as DefaultEnv;
      }


   public open val cache: AnalysisCache

   public open val ext: SootHostExtend?
      public open get() {
         return this.info.getExt(`$this$ext`);
      }


   public open val hostKey: Key<SootHostExtend?>

   public open val javaNameSourceEndColumnNumber: Int
      public open get() {
         return this.info.getJavaNameSourceEndColumnNumber(`$this$javaNameSourceEndColumnNumber`);
      }


   public open val javaNameSourceEndLineNumber: Int
      public open get() {
         return this.info.getJavaNameSourceEndLineNumber(`$this$javaNameSourceEndLineNumber`);
      }


   public open val javaNameSourceStartColumnNumber: Int
      public open get() {
         return this.info.getJavaNameSourceStartColumnNumber(`$this$javaNameSourceStartColumnNumber`);
      }


   public open val javaNameSourceStartLineNumber: Int
      public open get() {
         return this.info.getJavaNameSourceStartLineNumber(`$this$javaNameSourceStartLineNumber`);
      }


   init {
      this.sootClass = sootClass;
      this.info = info;
      this.file = new ClassResInfo(this.getSootClass());
      this.env$delegate = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, ClassCheckPoint::env_delegate$lambda$0);
   }

   public override fun eachMethod(block: (IMethodCheckPoint) -> Unit) {
      for (SootMethod method : this.getSootClass().getMethods()) {
         block.invoke(new MethodCheckPoint(method, this.info));
      }
   }

   public override fun eachField(block: (IFieldCheckPoint) -> Unit) {
      val var10000: java.util.Iterator = this.getSootClass().getFields().iterator();
      val var2: java.util.Iterator = var10000;

      while (var2.hasNext()) {
         val field: SootField = var2.next() as SootField;
         block.invoke(new FieldCheckPoint(field, this.info));
      }
   }

   override fun getSuperClasses(): Sequence<SootClass> {
      return IClassCheckPoint.DefaultImpls.getSuperClasses(this);
   }

   override fun getSuperInterfaces(): Sequence<SootClass> {
      return IClassCheckPoint.DefaultImpls.getSuperInterfaces(this);
   }

   public override fun SootClass.getMemberAtLine(ln: Int): BodyDeclaration<*>? {
      return this.info.getMemberAtLine(`$this$getMemberAtLine`, ln);
   }

   @JvmStatic
   fun `env_delegate$lambda$0`(`this$0`: ClassCheckPoint): DefaultEnv {
      return new DefaultEnv(`this$0`.getRegion().getMutable(), null, null, null, null, null, `this$0`.getSootClass(), null, null, 446, null);
   }
}
