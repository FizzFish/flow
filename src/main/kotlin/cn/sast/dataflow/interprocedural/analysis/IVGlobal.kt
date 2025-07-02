package cn.sast.dataflow.interprocedural.analysis

import cn.sast.api.config.StaticFieldTrackingMode
import cn.sast.dataflow.util.SootUtilsKt
import soot.*
import soot.jimple.*
import soot.jimple.internal.JimpleLocal

/**
 * 存放一些**全局共用**的常量 / 单例对象。
 */
class IVGlobal(
   val GLOBAL_SITE: IValue = GlobalStaticObject()
) {

   val GLOBAL_LOCAL = "@global"
   val RETURN_LOCAL = "@return"
   val SUMMARY_RETURN_LOCAL: Local =
      JimpleLocal("@SummaryReturn", Scene.v().objectType)

   val STRING_TYPE: RefType = RefType.v("java.lang.String")
   val OBJECT_TYPE: RefType = Scene.v().objectType
   val BYTE_ARRAY_TYPE: ArrayType = ArrayType.v(G.v().soot_ByteType, 1)
   val OBJ_ARRAY_TYPE: ArrayType = ArrayType.v(OBJECT_TYPE, 1)
   val CLASS_TYPE: RefType = RefType.v("java.lang.Class")

   val NOP: Unit = Jimple.v().newNopStmt()

   /** 用于 new Expr 环境的占位 */
   val NEW_Env: AnyNewExprEnv = run {
      val ref = SootUtilsKt.sootSignatureToRef(
         "<java.lang.String: void <clinit>()>",
         isStatic = true
      )
      AnyNewExprEnv(ref.resolve(), NOP)
   }

   /* ---------- 常量 ---------- */

   val NULL_CONST: NullConstant = NullConstant.v()
   val INT_ZERO_CONST: IntConstant = IntConstant.v(0)
   val DOUBLE_ZERO_CONST: DoubleConstant = DoubleConstant.v(0.0)
   val FLOAT_ZERO_CONST: FloatConstant = FloatConstant.v(0.0F)
   val LONG_ZERO_CONST: LongConstant = LongConstant.v(0L)

   /* ---------- 配置 ---------- */
   var staticFieldTrackingMode: StaticFieldTrackingMode =
      StaticFieldTrackingMode.ContextFlowSensitive

   /* ---------- 工具 ---------- */

   fun zeroValue(array: ArrayType): Pair<Constant, Type> =
      defaultValue(array.elementType)

   fun defaultValue(type: Type): Pair<Constant, Type> = when (type) {
      is RefLikeType -> NULL_CONST to NullType.v()
      is IntegerType -> INT_ZERO_CONST to type
      is DoubleType  -> DOUBLE_ZERO_CONST to type
      is FloatType   -> FLOAT_ZERO_CONST to type
      is LongType    -> LONG_ZERO_CONST to type
      else -> error("unsupported type: $type")
   }
}
