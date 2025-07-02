@file:Suppress("NOTHING_TO_INLINE")

package cn.sast.dataflow.util

import org.apache.commons.lang3.tuple.Pair
import soot.*
import soot.jimple.*
import soot.jimple.infoflow.data.SootMethodAndClass
import soot.jimple.infoflow.util.SootMethodRepresentationParser

/* ------------------------------------------------------ */
/* 1. SootMethodRef 构造                                   */
/* ------------------------------------------------------ */
fun sootSignatureToRef(signature: String, isStatic: Boolean): SootMethodRef {
   val (cls, mtd, ret, params) = with(
      SootMethodRepresentationParser.v().parseSootMethodString(signature)
   ) { arrayOf(className, methodName, returnType, parameters) }

   val sc  = Scene.v().getSootClass(cls)
   val retTy = Scene.v().getTypeUnsafe(ret, /* allowPhantom = */ true)
   val paramTys = cn.sast.api.util.SootUtilsKt.convertParameterTypes(params)

   return Scene.v().makeMethodRef(sc, mtd, paramTys, retTy, isStatic)
}

/* ------------------------------------------------------ */
/* 2. Body / InvokeExpr 参数辅助                          */
/* ------------------------------------------------------ */
fun Body.argToOpAndType(index: Int): Pair<Local, Type> = when (index) {
   -1   -> Pair.of(this.thisLocal, method.declaringClass.type)
   else -> {
      require(index in 0 until method.parameterCount) {
         "index: $index, parameterCount: ${method.parameterCount}"
      }
      Pair.of(getParameterLocal(index), method.getParameterType(index))
   }
}

fun Body.argToIdentityRef(index: Int): Pair<IdentityRef, Type> = when (index) {
   -1   -> {
      val thisUnit = this.thisUnit as IdentityStmt
      val ref = thisUnit.rightOp as ThisRef
      Pair.of(ref, ref.type)
   }
   else -> {
      require(index in 0 until method.parameterCount)
      val ref = parameterRefs[index] as ParameterRef
      Pair.of(ref, ref.type)
   }
}

fun InvokeExpr.argToOpAndType(index: Int): Pair<Value, Type> =
   if (index == -1 && this is InstanceInvokeExpr) {
      Pair.of(base, methodRef.declaringClass.type)
   } else {
      require(index in 0 until argCount)
      Pair.of(getArg(index), methodRef.getParameterType(index))
   }

/* ------------------------------------------------------ */
/* 3. 获取 / 创建字段                                      */
/* ------------------------------------------------------ */
fun getOrMakeField(sootClass: String, fieldName: String, type: Type): SootField {
   val sc = Scene.v().getSootClass(sootClass)
   return if (sc.declaresFieldByName(fieldName)) {
      sc.getFieldByName(fieldName)
   } else {
      sc.getOrAddField(SootField(fieldName, type))
   }
}
