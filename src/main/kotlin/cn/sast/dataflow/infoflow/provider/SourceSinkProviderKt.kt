package cn.sast.dataflow.infoflow.provider

import com.feysh.corax.config.api.MGlobal
import com.feysh.corax.config.api.MLocal
import com.feysh.corax.config.api.MParameter
import com.feysh.corax.config.api.MReturn
import soot.Scene
import soot.SootMethod
import soot.Type

private final val nullArg: MutableList<String>?

public fun SootMethod.baseType(loc: MLocal): Type? {
   val var10000: Type;
   if (loc is MParameter) {
      var10000 = if ((loc as MParameter).getIndex() == -1)
         `$this$baseType`.getDeclaringClass().getType() as Type
         else
         (
            if ((loc as MParameter).getIndex() >= `$this$baseType`.getParameterCount())
               null
               else
               `$this$baseType`.getParameterType((loc as MParameter).getIndex())
         );
   } else if (loc is MReturn) {
      var10000 = `$this$baseType`.getReturnType();
   } else {
      if (!(loc == MGlobal.INSTANCE)) {
         throw new NoWhenBranchMatchedException();
      }

      var10000 = Scene.v().getObjectType() as Type;
   }

   return var10000;
}
