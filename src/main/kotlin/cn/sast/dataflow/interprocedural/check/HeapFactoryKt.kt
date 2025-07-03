@file:SourceDebugExtension(["SMAP\nHeapFactory.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HeapFactory.kt\ncn/sast/dataflow/interprocedural/check/HeapFactoryKt\n+ 2 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1430:1\n44#2:1431\n1#3:1432\n*S KotlinDebug\n*F\n+ 1 HeapFactory.kt\ncn/sast/dataflow/interprocedural/check/HeapFactoryKt\n*L\n44#1:1431\n*E\n"])

package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FieldUtil
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JSootFieldType
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.check.callback.ICallCBImpl
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import soot.SootField

public inline fun <T> flowIt(crossinline c: (FlowCollector<T>, Continuation<Unit>) -> Any?): Flow<T> {
    return FlowKt.flow(Function2<FlowCollector<T>, Continuation<Unit>, Any?> { p1, p2 ->
        object : Continuation<Unit> {
            var label = 0
            val $c = c
            var L$0: Any? = null

            override val context: kotlin.coroutines.CoroutineContext
                get() = p2.context

            override fun resumeWith(result: Result<Unit>) {
                invokeSuspend(result.getOrThrow())
            }

            fun invokeSuspend($result: Any?): Any? {
                val var3x: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
                when (label) {
                    0 -> {
                        ResultKt.throwOnFailure($result)
                        val $this$flow: FlowCollector<T> = L$0 as FlowCollector<T>
                        val var10000 = $c
                        label = 1
                        if (var10000.invoke($this$flow, this) === var3x) {
                            return var3x
                        }
                    }
                    1 -> {
                        ResultKt.throwOnFailure($result)
                    }
                    else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                }
                return Unit
            }

            fun create(value: Any?, completion: Continuation<*>): Continuation<Unit> {
                val instance = this
                instance.L$0 = value
                return instance
            }
        }.create(p1, p2).invokeSuspend(Unit)
    })
}

public fun ICallCBImpl<IHeapValues<IValue>, Builder<IValue>>.getValueField(obj: IHeapValues<IValue>, valueField: SootField): IHeapValues<IValue> {
    getOut().assignNewExpr(env, "@obj", obj, false)
    val r: FieldUtil = FieldUtil.INSTANCE
    getOut().getField(env, "@obj.value", "@obj", JSootFieldType(valueField), true)
    val var5: IHeapValues<IValue> = getOut().getTargetsUnsafe("@obj.value")
    getOut().kill("@obj")
    getOut().kill("@obj.value")
    var var6: IHeapValues<IValue> = var5
    if (var5 == null) {
        var6 = hf.empty()
    }
    return var6
}

public fun ICallCBImpl<IHeapValues<IValue>, Builder<IValue>>.getConstantValue(obj: IHeapValues<IValue>, valueField: SootField): IHeapValues<IValue> {
    val res: IHeapValues.Builder<IValue> = hf.emptyBuilder()
    res.add(getValueField(obj, valueField))

    for (o in obj) {
        if (o.getValue() is ConstVal) {
            res.add(o)
        }
    }

    return res.build()
}