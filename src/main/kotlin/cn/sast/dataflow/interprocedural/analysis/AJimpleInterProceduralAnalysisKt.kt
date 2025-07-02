package cn.sast.dataflow.interprocedural.analysis

import soot.Body
import soot.Unit
import soot.Value
import soot.jimple.IdentityStmt
import soot.jimple.ParameterRef
import soot.jimple.ThisRef

public fun Body.getParameterUnit(i: Int): Unit? {
   val var10000: java.util.Iterator = `$this$getParameterUnit`.getUnits().iterator();
   val var2: java.util.Iterator = var10000;

   while (var2.hasNext()) {
      val s: Unit = var2.next() as Unit;
      if (s is IdentityStmt) {
         val rightOp: Value = (s as IdentityStmt).getRightOp();
         if (rightOp is ParameterRef) {
            if ((rightOp as ParameterRef).getIndex() == i) {
               return s;
            }
         } else if (rightOp is ThisRef && -1 == i) {
            return s;
         }
      }
   }

   return null;
}
