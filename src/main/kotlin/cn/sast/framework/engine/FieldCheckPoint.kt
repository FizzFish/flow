package cn.sast.framework.engine

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.cache.AnalysisDataFactory.Key
import com.feysh.corax.cache.analysis.SootHostExtend
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.IFieldCheckPoint
import com.feysh.corax.config.api.report.Region
import com.github.javaparser.ast.body.BodyDeclaration
import soot.SootClass
import soot.SootField
import soot.tagkit.AbstractHost
import soot.tagkit.VisibilityAnnotationTag

public class FieldCheckPoint(sootField: SootField, info: SootInfoCache) : CheckPoint, IFieldCheckPoint, SootInfoCache {
   public open val sootField: SootField
   public final val info: SootInfoCache

   public open val visibilityAnnotationTag: VisibilityAnnotationTag?
      public open get() {
         return this.getSootField().getTag("VisibilityAnnotationTag") as VisibilityAnnotationTag;
      }


   public open val region: Region
      public open get() {
         var var10000: Region = Region.Companion.invoke(this, this.getSootField() as AbstractHost);
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
      this.sootField = sootField;
      this.info = info;
      val var10003: SootClass = this.getSootField().getDeclaringClass();
      this.file = new ClassResInfo(var10003);
      this.env$delegate = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, FieldCheckPoint::env_delegate$lambda$0);
   }

   public override fun SootClass.getMemberAtLine(ln: Int): BodyDeclaration<*>? {
      return this.info.getMemberAtLine(`$this$getMemberAtLine`, ln);
   }

   @JvmStatic
   fun `env_delegate$lambda$0`(`this$0`: FieldCheckPoint): DefaultEnv {
      return new DefaultEnv(
         `this$0`.getRegion().getMutable(), null, null, null, null, null, `this$0`.getSootField().getDeclaringClass(), `this$0`.getSootField(), null, 318, null
      );
   }
}
