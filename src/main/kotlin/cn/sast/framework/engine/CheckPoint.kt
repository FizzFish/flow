package cn.sast.framework.engine

import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.config.api.ICheckPoint
import com.feysh.corax.config.api.INodeWithRange
import com.feysh.corax.config.api.report.Region
import soot.Local
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.Type
import soot.jimple.Constant
import soot.jimple.StringConstant

public abstract class CheckPoint : ICheckPoint {
   public abstract val file: IBugResInfo
   internal abstract val env: DefaultEnv

   public open val possibleTypes: Set<Type>
      public open get() {
         if (`$this$possibleTypes` is Constant) {
            return SetsKt.setOf((`$this$possibleTypes` as Constant).getType());
         } else if (`$this$possibleTypes` !is Local) {
            return SetsKt.emptySet();
         } else if (!Scene.v().hasPointsToAnalysis()) {
            throw new IllegalArgumentException("Failed requirement.".toString());
         } else {
            var var10000: java.util.Set = Scene.v().getPointsToAnalysis().reachingObjects(`$this$possibleTypes` as Local).possibleTypes();
            if (var10000 == null) {
               var10000 = SetsKt.emptySet();
            }

            return var10000;
         }
      }


   public open val possibleConstantValues: Set<String>
      public open get() {
         if (`$this$possibleConstantValues` is StringConstant) {
            return SetsKt.setOf((`$this$possibleConstantValues` as StringConstant).value);
         } else if (`$this$possibleConstantValues` !is Local) {
            return SetsKt.emptySet();
         } else if (!Scene.v().hasPointsToAnalysis()) {
            throw new IllegalArgumentException("Failed requirement.".toString());
         } else {
            var var10000: java.util.Set = Scene.v().getPointsToAnalysis().reachingObjects(`$this$possibleConstantValues` as Local).possibleStringConstants();
            if (var10000 == null) {
               var10000 = SetsKt.emptySet();
            }

            return var10000;
         }
      }


   public override fun SootMethod.hasSideEffect(): Boolean {
      return true;
   }

   public override fun SootClass.isInstanceOf(parent: String): Boolean? {
      val var10000: SootClass = Scene.v().getSootClassUnsafe(parent, false);
      return if (var10000 == null) null else this.isInstanceOf(`$this$isInstanceOf`, var10000);
   }

   public override fun SootClass.isInstanceOf(parent: SootClass): Boolean {
      return Scene.v().getOrMakeFastHierarchy().canStoreType(`$this$isInstanceOf`.getType() as Type, parent.getType() as Type);
   }

   public override fun toString(): String {
      val var10000: java.lang.String = this.getClass().getSimpleName();
      val var10001: IBugResInfo = this.getFile();
      val var10002: INodeWithRange = this as? INodeWithRange;
      if ((this as? INodeWithRange) != null) {
         val var1: Region = var10002.getRegion();
         if (var1 != null) {
            val var2: java.lang.String = var1.toString();
            if (var2 != null) {
               return "$var10000 at $var10001:$var2";
            }
         }
      }

      return "$var10000 at $var10001:";
   }
}
