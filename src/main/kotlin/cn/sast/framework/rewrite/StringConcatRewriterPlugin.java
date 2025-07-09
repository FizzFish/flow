package cn.sast.framework.rewrite;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import soot.Body;
import soot.PatchingChain;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.DynamicInvokeExpr;
import soot.tagkit.LineNumberTag;

public class StringConcatRewriterPlugin {
   private final StringConcatRewriter rewriter = new StringConcatRewriter();
   private static final String ARG_KEY_ENABLE_PLUGIN = "enableJava9StringConcat";
   private Boolean enabled;

   private static boolean isMakeConcatBootstrapMethod(SootMethodRef methodRef) {
      return methodRef.getDeclaringClass().getName().equals("java.lang.invoke.StringConcatFactory") && methodRef.getName().equals("makeConcat");
   }

   private static boolean isMakeConcatWithConstantsBootstrapMethod(SootMethodRef methodRef) {
      return methodRef.getDeclaringClass().getName().equals("java.lang.invoke.StringConcatFactory") && methodRef.getName().equals("makeConcatWithConstants");
   }

   public void transform(SootClass sootClass) throws IOException {
      for (SootMethod method : sootClass.getMethods()) {
         this.transformStringConcats(method);
      }
   }

   public void transformStringConcats(SootMethod method) {
      if (method.isConcrete()) {
         Body body = method.retrieveActiveBody();
         if (body != null) {
            this.transformStringConcats(body);
         }
      }
   }

   public void transformStringConcats(Body body) {
      PatchingChain<Unit> units = body.getUnits();
      if (!units.isEmpty()) {
         for (Unit unit = units.getFirst(); unit != null; unit = body.getUnits().getSuccOf(unit)) {
            if (unit instanceof DefinitionStmt && ((DefinitionStmt)unit).getRightOp() instanceof DynamicInvokeExpr) {
               DynamicInvokeExpr expr = (DynamicInvokeExpr)((DefinitionStmt)unit).getRightOp();
               Value outValue = ((DefinitionStmt)unit).getLeftOp();
               SootMethodRef bootstrapMethodRef = expr.getBootstrapMethodRef();
               List<Value> args = expr.getArgs();
               List<Value> bootstrapArgs = expr.getBootstrapArgs();
               LinkedList<Unit> newUnits = null;
               if (isMakeConcatBootstrapMethod(bootstrapMethodRef)) {
                  newUnits = this.rewriter.rewriteMakeConcat(body, outValue, args);
               } else if (isMakeConcatWithConstantsBootstrapMethod(bootstrapMethodRef)) {
                  newUnits = this.rewriter.rewriteMakeConcatWithConstants(body, outValue, args, bootstrapArgs);
               }

               if (newUnits != null) {
                  int ln = unit.getJavaSourceStartLineNumber();
                  if (ln != -1) {
                     for (Unit u : newUnits) {
                        u.addTag(new LineNumberTag(ln));
                     }
                  }

                  units.insertAfter(newUnits, unit);
                  units.remove(unit);
                  unit = newUnits.getLast();
               }
            }
         }
      }
   }
}
