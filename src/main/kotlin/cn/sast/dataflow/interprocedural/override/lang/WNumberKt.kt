@file:Suppress("FunctionName")

package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.*
import soot.*

/**
 * 从 **包装数值对象** 中抽取其基本数值。
 *
 * 仅当满足：
 *   - 对象是 ConstVal 且类型为 wrapper
 *   - 或已知字段 `value` 存在
 * 才会返回对应数值的 IOpCalculator。
 */
fun ICallCBImpl<IHeapValues<IValue>, IFact.Builder<IValue>>.getValueFromObject(
   obj: IHeapValues<IValue>,
   type: Type
): IOpCalculator<IValue> {

   val calc = hf.resolveOp(env, obj)

   calc.resolve { _, res, (numObj) ->
      val iv = numObj.value as IValue
      // 直接常量 => 收集
      when (type) {
         G.v().soot_IntType()    -> FactValuesKt.getIntValue(iv, false)
         G.v().soot_LongType()   -> FactValuesKt.getLongValue(iv, false)
         G.v().soot_FloatType()  -> FactValuesKt.getFloatValue(iv, false)
         G.v().soot_DoubleType() -> FactValuesKt.getDoubleValue(iv, false)
         else                    -> null
      }?.let {
         res.add(numObj) ; return@resolve true
      }

      // 尝试读取 obj.value 字段
      val field = (iv.type as? RefType)
         ?.sootClass
         ?.getFieldByNameUnsafe("value")
         ?: return@resolve false

      out.getField(env, "@value", "@num", JSootFieldType(field), false)
      val fieldHv = out.targets("@value")
      out.kill("@value"); out.kill("@num")
      res.add(fieldHv); true
   }

   return calc
}
