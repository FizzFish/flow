package cn.sast.dataflow.analysis

import com.feysh.corax.config.api.BugMessage
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.BugMessage.Env
import com.feysh.corax.config.api.report.Region
import java.nio.file.Path
import soot.SootClass
import soot.SootField
import soot.SootMethod

public interface IBugReporter {
   public abstract fun report(checkType: CheckType, atClass: SootClass, region: Region, env: (Env) -> Unit = ...) {
   }

   public abstract fun report(checkType: CheckType, file: Path, region: Region, env: (Env) -> Unit = ...) {
   }

   public abstract fun report(ct: CheckType, field: SootField, env: (Env) -> Unit = ...) {
   }

   public abstract fun report(ct: CheckType, method: SootMethod, env: (Env) -> Unit = ...) {
   }

   public abstract fun report(ct: CheckType, clazz: SootClass, unit: soot.Unit, env: (Env) -> Unit = ...) {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun `report$lambda$0`(var0: BugMessage.Env): Unit {
         return Unit.INSTANCE;
      }

      @JvmStatic
      fun `report$lambda$1`(var0: BugMessage.Env): Unit {
         return Unit.INSTANCE;
      }

      @JvmStatic
      fun `report$lambda$2`(var0: BugMessage.Env): Unit {
         return Unit.INSTANCE;
      }

      @JvmStatic
      fun `report$lambda$3`(var0: BugMessage.Env): Unit {
         return Unit.INSTANCE;
      }

      @JvmStatic
      fun `report$lambda$4`(var0: BugMessage.Env): Unit {
         return Unit.INSTANCE;
      }
   }
}
