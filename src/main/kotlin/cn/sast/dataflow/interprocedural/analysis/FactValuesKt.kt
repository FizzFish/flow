package cn.sast.dataflow.interprocedural.analysis

import soot.G
import soot.RefType
import soot.Type
import soot.jimple.*

/** 是否启用 “最小表达式” 优化 */
val leastExpr: Boolean = true

/* ---------- Boolean ---------- */

fun IValue.getBooleanValue(checkType: Boolean = true): Boolean? {
   if (checkType && typeIsConcrete() && type != G.v().soot_BooleanType()) return null
   val c = (this as? ConstVal)?.v ?: return null
   return (c as? IntConstant)?.value == 1
}

/* ---------- Int ---------- */

fun IValue.getIntValue(checkType: Boolean = true): Int? {
   if (checkType && typeIsConcrete() && type != G.v().soot_IntType()) return null
   val c = (this as? ConstVal)?.v ?: return null
   return (c as? IntConstant)?.value
}

/* ---------- Long ---------- */

fun IValue.getLongValue(checkType: Boolean = true): Long? {
   if (checkType && typeIsConcrete() && type != G.v().soot_LongType()) return null
   val c = (this as? ConstVal)?.v ?: return null
   return (c as? LongConstant)?.value
}

/* ---------- Byte ---------- */

fun IValue.getByteValue(checkType: Boolean = true): Byte? {
   if (checkType && typeIsConcrete() && type != G.v().soot_ByteType()) return null
   val c = (this as? ConstVal)?.v ?: return null
   return (c as? IntConstant)?.value?.toByte()
}

/* ---------- Float ---------- */

fun IValue.getFloatValue(checkType: Boolean = true): Float? {
   if (checkType && typeIsConcrete() && type != G.v().soot_FloatType()) return null
   val c = (this as? ConstVal)?.v ?: return null
   return (c as? FloatConstant)?.value
}

/* ---------- Double ---------- */

fun IValue.getDoubleValue(checkType: Boolean = true): Double? {
   if (checkType && typeIsConcrete() && type != G.v().soot_DoubleType()) return null
   val c = (this as? ConstVal)?.v ?: return null
   return (c as? DoubleConstant)?.value
}

/* ---------- String ---------- */

fun IValue.getStringValue(checkType: Boolean = true): String? {
   if (checkType && typeIsConcrete() && type != RefType.v("java.lang.String")) return null
   val c = (this as? ConstVal)?.v ?: return null
   return (c as? StringConstant)?.value
}

/* ---------- Class<Type> ---------- */

fun IValue.getClassValue(checkType: Boolean = true): Type? {
   if (checkType && typeIsConcrete() && type != RefType.v("java.lang.Class")) return null
   val c = (this as? ConstVal)?.v ?: return null
   return (c as? ClassConstant)?.toSootType()
}

/* ---------- 任意常量 ---------- */

inline fun IValue.getAnyValue(res: (Any?) -> Unit) {
   val c = (this as? ConstVal)?.v ?: return
   when (c) {
      is IntConstant                   -> res(c.value)
      is StringConstant                -> res(c.value)
      is LongConstant                  -> res(c.value)
      is NullConstant                  -> res(null)
      is DoubleConstant                -> res(c.value)
      is FloatConstant                 -> res(c.value)
      is ClassConstant                 -> res(c.value)
      is UntypedIntOrFloatConstant     -> res(c.value)
      is UntypedLongOrDoubleConstant   -> res(c.value)
   }
}

/* ---------- null 判断 ---------- */

fun IValue.isNull(): Boolean? =
   (this as? ConstVal)?.v is NullConstant
