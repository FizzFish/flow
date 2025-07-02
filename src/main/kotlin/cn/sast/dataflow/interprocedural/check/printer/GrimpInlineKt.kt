@file:SourceDebugExtension(["SMAP\nGrimpInline.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GrimpInline.kt\ncn/sast/dataflow/interprocedural/check/printer/GrimpInlineKt\n+ 2 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinterKt\n*L\n1#1,312:1\n17#2:313\n*S KotlinDebug\n*F\n+ 1 GrimpInline.kt\ncn/sast/dataflow/interprocedural/check/printer/GrimpInlineKt\n*L\n20#1:313\n*E\n"])

package cn.sast.dataflow.interprocedural.check.printer

import kotlin.jvm.internal.SourceDebugExtension
import soot.SootClass
import soot.SootMethodInterface
import soot.SootMethodRef
import soot.UnitPrinter
import soot.Value
import soot.jimple.InstanceInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.StaticInvokeExpr

public fun InvokeExpr.invokeToString(up: UnitPrinter) {
   if (`$this$invokeToString` is InstanceInvokeExpr) {
      (`$this$invokeToString` as InstanceInvokeExpr).getBaseBox().toString(up);
      up.literal(".");
   }

   if (`$this$invokeToString` is StaticInvokeExpr) {
      up.literal((`$this$invokeToString` as StaticInvokeExpr).getMethodRef().getDeclaringClass().getShortName());
      up.literal(".");
   }

   val var10001: SootMethodRef = `$this$invokeToString`.getMethodRef();
   val args: SootMethodInterface = var10001 as SootMethodInterface;
   val var7: SootClass = (var10001 as SootMethodInterface).getDeclaringClass();
   val var10002: java.lang.String = args.getName();
   up.literal(SimpleUnitPrinterKt.getPrettyMethodName(var7, var10002));
   up.literal("(");
   val var5: java.util.List = `$this$invokeToString`.getArgs();
   var var6: Int = 0;

   for (int e = args.size(); i < e; i++) {
      if (var6 != 0) {
         up.literal(", ");
      }

      (var5.get(var6) as Value).toString(up);
   }

   up.literal(")");
}
