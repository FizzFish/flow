package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.*
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl.EvalCall
import cn.sast.dataflow.util.SootUtilsKt
import mu.KotlinLogging
import soot.*
import soot.jimple.*
import kotlin.math.abs
import kotlin.text.*

/**
 * Number 派生类（Integer / Long / Float …）建模。
 *
 * ⚠️ 注：下方仅保留核心注册流程；内部解析逻辑用 TODO 标记，
 * 以免在缺少完整上下文时写出错误实现。你可按 TODO 处补全。
 */
class WNumber : SummaryHandlePackage<IValue> {

   private val logger = KotlinLogging.logger {}

   /* ---------- 注册入口 ---------- */

   override fun ACheckCallAnalysis.register() {
      val numberTypes = listOf(
         "java.lang.Integer" to G.v().soot_IntType(),
         "java.lang.Long"    to G.v().soot_LongType(),
         "java.lang.Short"   to G.v().soot_ShortType(),
         "java.lang.Byte"    to G.v().soot_ByteType(),
         "java.lang.Float"   to G.v().soot_FloatType(),
         "java.lang.Double"  to G.v().soot_DoubleType(),
      )

      numberTypes.forEach { (cls, prim) ->
         registerClassAllWrapper(cls)           // 把整个类做 wrapper
         registerValueOf(cls, prim)             // valueOf / parse 等
         registerEquals(cls)                    // equals 重写
      }
   }

   /* ---------- valueOf / parse ---------- */

   private fun ACheckCallAnalysis.registerValueOf(
      clsName: String,
      prim: PrimType
   ) {
      val valueField = SootUtilsKt.getOrMakeField(clsName, "value", prim)
      val newExpr = Jimple.v().newNewExpr(RefType.v(clsName))

      // valueOf(primitive)
      evalCall("<$clsName: $clsName valueOf($prim)>") { eval ->
         handlePrimitiveValueOf(eval, newExpr, valueField)
      }

      // valueOf(String)
      registerWrapper("<$clsName: $clsName valueOf(java.lang.String)>", isStatic = true)

      // parseXxx(String)
      val cap = prim.toString().replaceFirstChar { it.uppercase() }
      registerWrapper("<$clsName: $prim parse$cap(java.lang.String)>", true)
   }

   private fun handlePrimitiveValueOf(
      eval: EvalCall,
      newExpr: NewExpr,
      valueField: SootField
   ) {
      val hf = eval.hf
      val env = eval.env

      // 创建新对象
      val obj = hf.anyNewVal(eval.newEnv, newExpr)
      hf.push(env, obj).markOfNewExpr(newExpr).popHV().let {
         eval.out.assignNewExpr(env, eval.vg.RETURN_LOCAL, it, false)
      }

      // 写 value 字段
      eval.out.setField(
         env, eval.vg.RETURN_LOCAL,
         JSootFieldType(valueField), eval.arg(0), append = false
      )
   }

   /* ---------- equals ---------- */

   private fun ACheckCallAnalysis.registerEquals(clsName: String) {
      registerWrapper("<$clsName: boolean equals(java.lang.Object)>", false)
   }

   /* ---------- parse(String, int) 及其它 ↓ (留空) ---------- */

   // 可在此补充 parseXxx / toXxx / xxxValue 等 Hook …

   companion object { fun v() = WNumber() }
}
