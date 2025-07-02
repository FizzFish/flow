package cn.sast.framework.engine

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.cache.AnalysisDataFactory.Key
import com.feysh.corax.cache.analysis.SootHostExtend
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.IMethodCheckPoint
import com.feysh.corax.config.api.IUnitCheckPoint
import com.feysh.corax.config.api.report.Region
import com.github.javaparser.ast.body.BodyDeclaration
import soot.SootClass
import soot.SootMethod
import soot.tagkit.AbstractHost
import soot.tagkit.VisibilityAnnotationTag

public class MethodCheckPoint(sootMethod: SootMethod, info: SootInfoCache) : CheckPoint, IMethodCheckPoint, SootInfoCache {
   public open val sootMethod: SootMethod
   public final val info: SootInfoCache

   public open val visibilityAnnotationTag: VisibilityAnnotationTag?
      public open get() {
         return this.getSootMethod().getTag("VisibilityAnnotationTag") as VisibilityAnnotationTag;
      }


   public open val region: Region
      public open get() {
         var var10000: Region = Region.Companion.invoke(this, this.getSootMethod() as AbstractHost);
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
      this.sootMethod = sootMethod;
      this.info = info;
      val var10003: SootClass = this.getSootMethod().getDeclaringClass();
      this.file = new ClassResInfo(var10003);
      this.env$delegate = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, MethodCheckPoint::env_delegate$lambda$0);
   }

   public override fun eachUnit(block: (IUnitCheckPoint) -> Unit) {
      if (this.getSootMethod().hasActiveBody()) {
         val var10000: java.util.Iterator = this.getSootMethod().getActiveBody().getUnits().iterator();
         val var3: java.util.Iterator = var10000;

         while (var3.hasNext()) {
            val unit: soot.Unit = var3.next() as soot.Unit;
            val var10003: SootInfoCache = this.info;
            block.invoke(new UnitCheckPoint(var10003, unit, this.getSootMethod()));
         }
      }
   }

   public override fun SootClass.getMemberAtLine(ln: Int): BodyDeclaration<*>? {
      return this.info.getMemberAtLine(`$this$getMemberAtLine`, ln);
   }

   @JvmStatic
   fun `env_delegate$lambda$0`(`this$0`: MethodCheckPoint): DefaultEnv {
      return new DefaultEnv(
         `this$0`.getRegion().getMutable(),
         null,
         null,
         null,
         `this$0`.getSootMethod(),
         null,
         `this$0`.getSootMethod().getDeclaringClass(),
         null,
         `this$0`.getSootMethod(),
         174,
         null
      );
   }
}
