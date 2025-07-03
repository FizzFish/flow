package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.*
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import cn.sast.dataflow.interprocedural.check.heapimpl.IArrayHeapKV
import soot.ArrayType
import soot.ByteType
import soot.G
import soot.SootField
import soot.Type
import soot.jimple.StringConstant

/**
 * String 相关辅助扩展。
 */

fun <B : IFact.Builder<IValue>> ICallCBImpl<IHeapValues<IValue>, B>.getByteArray(
   strValue: IValue
): ByteArray? =
   (out.getValueData(strValue, BuiltInModelT.Array) as? IArrayHeapKV)
      ?.getByteArray(hf)

fun <B : IFact.Builder<IValue>> ICallCBImpl<IHeapValues<IValue>, B>.getStringFromObject(
   obj: IHeapValues<IValue>
): IOpCalculator<IValue> {
   val byteType: ByteType = G.v().soot_ByteType()
   val arrayType: ArrayType = ArrayType.v(byteType, 1)
   val coderField: SootField =
      cn.sast.dataflow.util.SootUtilsKt.getOrMakeField("java.lang.String", "coder", byteType)

   val valueField: SootField =
      cn.sast.dataflow.util.SootUtilsKt.getOrMakeField("java.lang.String", "value", arrayType)

   val calc = hf.resolveOp(
      env,
      HeapFactoryKt.getValueField(this, obj, valueField),
      HeapFactoryKt.getValueField(this, obj, coderField)
   )

   calc.resolve { _, res, (valueCp, coderCp) ->
      val coder = FactValuesKt.getByteValue(coderCp.value as IValue, true) ?: return@resolve false
      val bytes = getByteArray(valueCp.value as IValue) ?: return@resolve false
      val str = if (coder == WString.LATIN1_BYTE) String(bytes, Charsets.UTF_8)
      else String(bytes, Charsets.UTF_16)
      res.add(
         hf.push(env, hf.newConstVal(StringConstant.v(str), hf.vg.STRING_TYPE))
            .dataSequenceToSeq(valueCp)
            .popHV()
      )
      true
   }

   return calc.also {
      // 兜底：把 obj / const 也并进结果
      val ori = hf.resolveOp(env, obj)
      ori.resolve { _, res, (o) -> res.add(o); true }
      it.res.add(ori.res.build())
   }
}
