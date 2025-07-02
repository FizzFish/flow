@file:SourceDebugExtension(["SMAP\nSootUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SootUtils.kt\ncn/sast/dataflow/util/SootUtilsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,59:1\n1#2:60\n*E\n"])

package cn.sast.dataflow.util

import kotlin.jvm.internal.SourceDebugExtension
import soot.Body
import soot.Local
import soot.RefType
import soot.Scene
import soot.SootClass
import soot.SootField
import soot.SootMethodRef
import soot.Type
import soot.Unit
import soot.Value
import soot.jimple.IdentityRef
import soot.jimple.IdentityStmt
import soot.jimple.InstanceInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.ParameterRef
import soot.jimple.ThisRef
import soot.jimple.infoflow.data.SootMethodAndClass
import soot.jimple.infoflow.util.SootMethodRepresentationParser

public final val thisLocalAndType: Pair<Local, RefType>
   public final get() {
      val it: Pair = argToOpAndType(`$this$thisLocalAndType`, -1);
      val var10000: Any = it.getFirst();
      val var10001: Any = it.getSecond();
      return TuplesKt.to(var10000, var10001 as RefType);
   }


public final val thisIdentityRef: Pair<ThisRef, RefType>
   public final get() {
      val it: IdentityRef = argToIdentityRef(`$this$thisIdentityRef`, -1).getFirst() as IdentityRef;
      val var10000: ThisRef = it as ThisRef;
      val var10001: Type = (it as ThisRef).getType();
      return TuplesKt.to(var10000, var10001 as RefType);
   }


public final val thisOpAndType: Pair<Value, Type>
   public final get() {
      return argToOpAndType(`$this$thisOpAndType`, -1);
   }


public fun sootSignatureToRef(signature: String, isStatic: Boolean): SootMethodRef {
   val smac: SootMethodAndClass = SootMethodRepresentationParser.v().parseSootMethodString(signature);
   val var10000: SootClass = Scene.v().getSootClass(smac.getClassName());
   val var5: Scene = Scene.v();
   val var10002: java.lang.String = smac.getMethodName();
   var var10003: java.util.List = smac.getParameters();
   var10003 = cn.sast.api.util.SootUtilsKt.convertParameterTypes(var10003);
   val var10004: Type = Scene.v().getTypeUnsafe(smac.getReturnType(), true);
   val var4: SootMethodRef = var5.makeMethodRef(var10000, var10002, var10003, var10004, isStatic);
   return var4;
}

public fun Body.argToOpAndType(index: Int): Pair<Local, Type> {
   val var10000: Pair;
   if (index == -1) {
      var10000 = TuplesKt.to(`$this$argToOpAndType`.getThisLocal(), `$this$argToOpAndType`.getMethod().getDeclaringClass().getType());
   } else {
      if (0 > index || index >= `$this$argToOpAndType`.getMethod().getParameterCount()) {
         throw new IllegalStateException(
            ("$`$this$argToOpAndType` parameterCount: ${`$this$argToOpAndType`.getMethod().getParameterCount()}, but index: $index").toString()
         );
      }

      var10000 = TuplesKt.to(`$this$argToOpAndType`.getParameterLocal(index), `$this$argToOpAndType`.getMethod().getParameterType(index));
   }

   return var10000;
}

public fun Body.argToIdentityRef(index: Int): Pair<IdentityRef, Type> {
   val var8: Pair;
   if (index == -1) {
      val var10000: Unit = `$this$argToIdentityRef`.getThisUnit();
      val var7: Value = (var10000 as IdentityStmt).getRightOp();
      var8 = TuplesKt.to(var7 as ThisRef, (var7 as ThisRef).getType());
   } else {
      if (0 > index || index >= `$this$argToIdentityRef`.getMethod().getParameterCount()) {
         throw new IllegalStateException(
            ("$`$this$argToIdentityRef` parameterCount: ${`$this$argToIdentityRef`.getMethod().getParameterCount()}, but index: $index").toString()
         );
      }

      val var9: Any = `$this$argToIdentityRef`.getParameterRefs().get(index);
      var8 = TuplesKt.to(var9 as ParameterRef, (var9 as ParameterRef).getType());
   }

   return var8;
}

public fun InvokeExpr.argToOpAndType(index: Int): Pair<Value, Type> {
   val var10000: Pair;
   if (index == -1 && `$this$argToOpAndType` is InstanceInvokeExpr) {
      val var2: Value = (`$this$argToOpAndType` as InstanceInvokeExpr).getBase();
      var10000 = TuplesKt.to(var2 as Local, (`$this$argToOpAndType` as InstanceInvokeExpr).getMethodRef().getDeclaringClass().getType());
   } else {
      if (0 > index || index >= `$this$argToOpAndType`.getArgCount()) {
         throw new IllegalStateException(("$`$this$argToOpAndType` parameterCount: ${`$this$argToOpAndType`.getMethodRef()}, but index is: $index").toString());
      }

      var10000 = TuplesKt.to(`$this$argToOpAndType`.getArg(index), `$this$argToOpAndType`.getMethodRef().getParameterType(index));
   }

   return var10000;
}

public fun getOrMakeField(sootClass: String, fieldName: String, sootFieldType: Type): SootField {
   val it: SootClass = Scene.v().getSootClass(sootClass);
   val var10000: SootField = if (it.declaresFieldByName(fieldName)) it.getFieldByName(fieldName) else it.getOrAddField(new SootField(fieldName, sootFieldType));
   return var10000;
}
