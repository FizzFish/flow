package cn.sast.framework.engine

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.IUnitCheckPoint
import com.feysh.corax.config.api.report.Region
import kotlin.jvm.internal.SourceDebugExtension
import soot.SootClass
import soot.SootMethod
import soot.Unit
import soot.Value
import soot.ValueBox
import soot.jimple.Expr
import soot.tagkit.Host

@SourceDebugExtension(["SMAP\nPreAnalysisImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/UnitCheckPoint\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,760:1\n1863#2,2:761\n*S KotlinDebug\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/UnitCheckPoint\n*L\n662#1:761,2\n*E\n"])
public class UnitCheckPoint(info: SootInfoCache, unit: Unit, sootMethod: SootMethod) : CheckPoint, IUnitCheckPoint {
   public final val info: SootInfoCache
   public open val unit: Unit
   public final val sootMethod: SootMethod

   public open val region: Region
      public open get() {
         var var10000: Region = Region.Companion.invoke(this.info, this.getUnit() as Host);
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


   init {
      this.info = info;
      this.unit = unit;
      this.sootMethod = sootMethod;
      val var10003: SootClass = this.sootMethod.getDeclaringClass();
      this.file = new ClassResInfo(var10003);
      this.env$delegate = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, UnitCheckPoint::env_delegate$lambda$1);
   }

   public override fun eachExpr(block: (Expr) -> kotlin.Unit) {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         val value: Value = (`element$iv` as ValueBox).getValue();
         if (value is Expr) {
            block.invoke(value);
         }
      }
   }

   @JvmStatic
   fun `env_delegate$lambda$1`(`this$0`: UnitCheckPoint): DefaultEnv {
      return new DefaultEnv(
         `this$0`.getRegion().getMutable(),
         null,
         null,
         null,
         `this$0`.sootMethod,
         null,
         `this$0`.sootMethod.getDeclaringClass(),
         null,
         `this$0`.sootMethod,
         174,
         null
      );
   }
}
