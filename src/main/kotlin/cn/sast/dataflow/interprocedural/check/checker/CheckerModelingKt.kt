package cn.sast.dataflow.interprocedural.check.checker

import com.feysh.corax.config.api.TaintProperty
import com.feysh.corax.config.api.ViaProperty

public final val KeyTaintProperty: Any = TaintProperty.INSTANCE
public final val KeyViaProperty: Any = ViaProperty.INSTANCE

public final val KeyAttribute: Any = new Object() {
   @Override
   public java.lang.String toString() {
      return "Attribute";
   }
}
