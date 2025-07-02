package cn.sast.dataflow.interprocedural.check.printer

import kotlin.jvm.internal.SourceDebugExtension
import soot.AbstractUnitPrinter
import soot.SootClass
import soot.SootFieldRef
import soot.SootMethod
import soot.SootMethodInterface
import soot.SootMethodRef
import soot.Type
import soot.Unit
import soot.UnitPrinter
import soot.Value
import soot.jimple.CaughtExceptionRef
import soot.jimple.Constant
import soot.jimple.IdentityRef
import soot.jimple.InstanceInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.ParameterRef
import soot.jimple.StaticInvokeExpr
import soot.jimple.StringConstant
import soot.jimple.ThisRef

@SourceDebugExtension(["SMAP\nSimpleUnitPrinter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinter\n+ 2 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinterKt\n*L\n1#1,144:1\n17#2:145\n*S KotlinDebug\n*F\n+ 1 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinter\n*L\n57#1:145\n*E\n"])
public class SimpleUnitPrinter : AbstractUnitPrinter {
   private fun getQuotedStringOf(fromString: String): String {
      val fromStringLen: Int = fromString.length();
      val toStringBuffer: StringBuilder = new StringBuilder(fromStringLen + 20);
      toStringBuffer.append("\"");

      for (int i = 0; i < fromStringLen; i++) {
         val ch: Char = fromString.charAt(i);
         switch (ch) {
            case '\t':
               toStringBuffer.append("\\t");
               break;
            case '\n':
               toStringBuffer.append("\\n");
               break;
            case '\r':
               toStringBuffer.append("\\r");
               break;
            case '"':
               toStringBuffer.append("\\\"");
               break;
            case '\\':
               toStringBuffer.append("\\\\");
               break;
            default:
               toStringBuffer.append(ch);
         }
      }

      toStringBuffer.append("\"");
      val var10000: java.lang.String = toStringBuffer.toString();
      return var10000;
   }

   public open fun literal(s: String) {
      this.output.append(s);
   }

   public open fun type(t: Type) {
      this.output.append(EventPrinterKt.getPname(t));
   }

   public open fun constant(c: Constant?) {
      if (c is StringConstant) {
         this.handleIndent();
         val var10000: StringBuffer = this.output;
         val var10002: java.lang.String = (c as StringConstant).value;
         var10000.append(this.getQuotedStringOf(var10002));
      } else {
         super.constant(c);
      }
   }

   public open fun methodRef(m: SootMethodRef) {
      val var10000: StringBuffer = this.output;
      val var10001: SootClass = (m as SootMethodInterface).getDeclaringClass();
      val var10002: java.lang.String = (m as SootMethodInterface).getName();
      var10000.append(SimpleUnitPrinterKt.getPrettyMethodName(var10001, var10002));
   }

   public open fun fieldRef(f: SootFieldRef) {
      this.output.append(f.name());
   }

   public open fun unitRef(u: Unit?, branchTarget: Boolean) {
   }

   public open fun identityRef(r: IdentityRef) {
      if (r is ThisRef) {
         this.literal("@this: ");
         val var10001: Type = (r as ThisRef).getType();
         this.type(var10001);
      } else if (r is ParameterRef) {
         this.literal("@parameter${(r as ParameterRef).getIndex()}: ");
         val var2: Type = (r as ParameterRef).getType();
         this.type(var2);
      } else {
         if (r !is CaughtExceptionRef) {
            throw new RuntimeException();
         }

         this.literal("@caughtexception");
      }
   }

   public open fun toString(): String {
      val var10000: java.lang.String = this.output.toString();
      return var10000;
   }

   protected open fun handleIndent() {
      this.startOfLine = false;
   }

   @SourceDebugExtension(["SMAP\nSimpleUnitPrinter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinter$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinterKt\n*L\n1#1,144:1\n1#2:145\n17#3:146\n17#3:147\n*S KotlinDebug\n*F\n+ 1 SimpleUnitPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/SimpleUnitPrinter$Companion\n*L\n115#1:146\n137#1:147\n*E\n"])
   public companion object {
      public fun getStringOf(unit: Unit): String {
         val it: SimpleUnitPrinter = new SimpleUnitPrinter();
         unit.toString(it as UnitPrinter);
         return it.toString();
      }

      public fun getStringOf(m: SootMethod?, lhs: Value?, invokeExpr: InvokeExpr?, short: Boolean = true): String {
         val up: SimpleUnitPrinter = new SimpleUnitPrinter();
         if (invokeExpr != null) {
            if (lhs != null) {
               lhs.toString(up as UnitPrinter);
               up.literal(" = ");
            }

            if (invokeExpr is InstanceInvokeExpr) {
               (invokeExpr as InstanceInvokeExpr).getBaseBox().toString(up as UnitPrinter);
               up.literal(".");
            } else if (invokeExpr is StaticInvokeExpr) {
               var var10000: SootClass;
               label54: {
                  if (m != null) {
                     var10000 = m.getDeclaringClass();
                     if (var10000 != null) {
                        break label54;
                     }
                  }

                  var10000 = invokeExpr.getMethodRef().getDeclaringClass();
               }

               if (var4) {
                  val var10001: java.lang.String = var10000.getShortName();
                  up.literal(var10001);
               } else {
                  val var18: java.lang.String = var10000.getName();
                  up.literal(var18);
               }

               up.literal(".");
            }

            val var19: SootMethodRef = invokeExpr.getMethodRef();
            val var16: SootMethodInterface = var19 as SootMethodInterface;
            val var20: SootClass = (var19 as SootMethodInterface).getDeclaringClass();
            val var10002: java.lang.String = var16.getName();
            up.literal(SimpleUnitPrinterKt.getPrettyMethodName(var20, var10002));
            up.literal("(");
            val var17: java.util.List = invokeExpr.getArgs();
            var var14: Int = 0;

            for (int e = args.size(); i < e; i++) {
               if (var14 != 0) {
                  up.literal(", ");
               }

               (var17.get(var14) as Value).toString(up as UnitPrinter);
            }

            up.literal(")");
         } else if (m != null) {
            if (lhs != null) {
               lhs.toString(up as UnitPrinter);
               up.literal(" = ");
            }

            if (m.isStatic()) {
               val var21: java.lang.String = m.getDeclaringClass().getShortName();
               up.literal(var21);
            } else {
               up.literal("?");
            }

            up.literal(".");
            val var22: SootClass = (m as SootMethodInterface).getDeclaringClass();
            val var23: java.lang.String = (m as SootMethodInterface).getName();
            up.literal(SimpleUnitPrinterKt.getPrettyMethodName(var22, var23));
            up.literal("(...)");
         }

         return up.toString();
      }
   }
}
