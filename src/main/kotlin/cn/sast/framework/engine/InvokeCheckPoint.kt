package cn.sast.framework.engine

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.IInvokeCheckPoint
import com.feysh.corax.config.api.report.Region
import kotlin.jvm.internal.SourceDebugExtension
import soot.SootClass
import soot.SootMethod
import soot.SootMethodRef
import soot.Type
import soot.Unit
import soot.jimple.InvokeExpr
import soot.tagkit.AbstractHost

@SourceDebugExtension(["SMAP\nPreAnalysisImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/InvokeCheckPoint\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,760:1\n1#2:761\n*E\n"])
public class InvokeCheckPoint(info: SootInfoCache,
      container: SootMethod,
      callSite: Unit?,
      declaredReceiverType: Type?,
      invokeMethodRef: SootMethodRef?,
      callee: SootMethod,
      invokeExpr: InvokeExpr?
   )
   : CheckPoint,
   IInvokeCheckPoint {
   public final val info: SootInfoCache
   public open val container: SootMethod
   public open val callSite: Unit?
   public open val declaredReceiverType: Type?
   public open val invokeMethodRef: SootMethodRef?
   public open val callee: SootMethod
   public open val invokeExpr: InvokeExpr?

   public open val region: Region
      public open get() {
         val var10000: Unit = this.getCallSite();
         if (var10000 != null) {
            val var3: Region = Region.Companion.invoke(var10000);
            if (var3 != null) {
               return var3;
            }
         }

         var var4: Region = Region.Companion.invoke(this.info, this.getContainer() as AbstractHost);
         if (var4 == null) {
            var4 = Region.Companion.getERROR();
         }

         return var4;
      }


   public open val file: IBugResInfo
      public open get() {
         return this.file$delegate.getValue() as IBugResInfo;
      }


   internal open val env: DefaultEnv
      internal open get() {
         return this.env$delegate.getValue() as DefaultEnv;
      }


   init {
      this.info = info;
      this.container = container;
      this.callSite = callSite;
      this.declaredReceiverType = declaredReceiverType;
      this.invokeMethodRef = invokeMethodRef;
      this.callee = callee;
      this.invokeExpr = invokeExpr;
      this.file$delegate = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, InvokeCheckPoint::file_delegate$lambda$1);
      this.env$delegate = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, InvokeCheckPoint::env_delegate$lambda$2);
   }

   public override fun toString(): String {
      val var10001: Unit = this.getCallSite();
      return "${this.getContainer()}: at ${if (var10001 != null) var10001.getJavaSourceStartLineNumber() else null} : ${this.getCallSite()} -> ${this.getInvokeMethodRef()} -> ${this.getCallee()}";
   }

   @JvmStatic
   fun `file_delegate$lambda$1`(`this$0`: InvokeCheckPoint): ClassResInfo {
      val var10002: SootClass = `this$0`.getContainer().getDeclaringClass();
      return new ClassResInfo(var10002);
   }

   @JvmStatic
   fun `env_delegate$lambda$2`(`this$0`: InvokeCheckPoint): DefaultEnv {
      return new DefaultEnv(
         `this$0`.getRegion().getMutable(),
         null,
         `this$0`.getCallSite(),
         `this$0`.getCallee(),
         `this$0`.getContainer(),
         `this$0`.getInvokeExpr(),
         null,
         null,
         null,
         450,
         null
      );
   }
}
