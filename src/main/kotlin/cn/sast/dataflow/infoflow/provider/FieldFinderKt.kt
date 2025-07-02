package cn.sast.dataflow.infoflow.provider

import soot.Scene
import soot.SootClass
import soot.SootField
import soot.Type

public final val sootField: SootField?
   public final get() {
      val var10000: java.lang.String = `$this$sootField`.getDeclaringClassType();
      if (var10000 == null) {
         return null;
      } else {
         val var4: SootClass = Scene.v().getSootClassUnsafe(var10000, false);
         if (var4 == null) {
            return null;
         } else {
            val var5: SootField;
            if (`$this$sootField`.getFieldType() == null) {
               var5 = var4.getFieldByNameUnsafe(`$this$sootField`.getFieldName());
            } else {
               val var6: Type = Scene.v().getTypeUnsafe(`$this$sootField`.getFieldType());
               if (var6 == null) {
                  return var4.getFieldByNameUnsafe(`$this$sootField`.getFieldName());
               }

               var5 = var4.getFieldUnsafe(`$this$sootField`.getFieldName(), var6);
            }

            return var5;
         }
      }
   }

