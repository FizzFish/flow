package cn.sast.dataflow.interprocedural.check.checker

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FieldUtil
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JFieldNameType
import cn.sast.dataflow.interprocedural.analysis.PointsToGraphBuilderAbstract
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementHashMap
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementHashMapBuilder
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSet
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSetBuilder
import cn.sast.dataflow.interprocedural.check.heapimpl.ObjectValues
import cn.sast.dataflow.interprocedural.check.heapimpl.ObjectValuesBuilder
import cn.sast.idfa.check.ICallCB
import com.feysh.corax.config.api.AttributeName
import com.feysh.corax.config.api.BuiltInField
import com.feysh.corax.config.api.ClassField
import com.feysh.corax.config.api.Elements
import com.feysh.corax.config.api.IClassField
import com.feysh.corax.config.api.IExpr
import com.feysh.corax.config.api.IIexGetField
import com.feysh.corax.config.api.IIexLoad
import com.feysh.corax.config.api.IIstSetField
import com.feysh.corax.config.api.IIstStoreLocal
import com.feysh.corax.config.api.IModelExpressionVisitor
import com.feysh.corax.config.api.IModelStmtVisitor
import com.feysh.corax.config.api.IStmt
import com.feysh.corax.config.api.MGlobal
import com.feysh.corax.config.api.MLocal
import com.feysh.corax.config.api.MParameter
import com.feysh.corax.config.api.MReturn
import com.feysh.corax.config.api.MapKeys
import com.feysh.corax.config.api.MapValues
import com.feysh.corax.config.api.SubFields
import com.feysh.corax.config.api.TaintProperty
import com.feysh.corax.config.api.ViaProperty
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import soot.ArrayType
import soot.Scene
import soot.Type
import kotlin.collections.CollectionsKt
import kotlin.collections.SequencesKt

@SourceDebugExtension(["SMAP\nCheckerModeling.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerModeling.kt\ncn/sast/dataflow/interprocedural/check/checker/FactModeling\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,565:1\n774#2:566\n865#2,2:567\n774#2:569\n865#2,2:570\n46#3:572\n47#3:574\n1#4:573\n*S KotlinDebug\n*F\n+ 1 CheckerModeling.kt\ncn/sast/dataflow/interprocedural/check/checker/FactModeling\n*L\n197#1:566\n197#1:567,2\n200#1:569\n200#1:570,2\n242#1:572\n242#1:574\n242#1:573\n*E\n"])
class FactModeling(
    val hf: AbstractHeapFactory<IValue>,
    val env: HeapValuesEnv,
    val summaryCtxCalleeSite: ICallCB<IHeapValues<IValue>, Builder<IValue>>,
    val builder: Builder<IValue>
) {
    val isArray: Boolean
        get() = `$this$isArray` is CompanionV<*> &&
                (`$this$isArray` as CompanionV<*>).getValue() is IValue &&
                ((`$this$isArray` as CompanionV<*>).getValue() as IValue).getType() is ArrayType

    fun List<Any>.toHV(): IHeapValues<IValue> {
        val b = hf.emptyBuilder()

        for (e in this) {
            if (e is CompanionV<*>) {
                if ((e as CompanionV<*>).getValue() !is IValue) {
                    throw IllegalStateException("Check failed.")
                }
                b.add(e as CompanionV<IValue>)
            }
        }

        return b.build()
    }

    fun store(values: List<Any>, dest: MLocal, append: Boolean = false) {
        val out = builder as PointsToGraphBuilderAbstract
        val value = toHV(values)
        when {
            dest == MReturn.INSTANCE -> {
                if (append) {
                    summaryCtxCalleeSite.setReturn(summaryCtxCalleeSite.getReturn().plus(value))
                } else {
                    summaryCtxCalleeSite.setReturn(value)
                }
            }
            dest is MParameter -> {
                out.assignNewExpr(env, summaryCtxCalleeSite.argToValue((dest as MParameter).getIndex()), value, append)
            }
            dest == MGlobal.INSTANCE -> {
                throw NoWhenBranchMatchedException()
            }
        }
    }

    fun setField(baseExpr: IExpr?, bases: List<Any>, values: List<Any>, field: IClassField, append: Boolean) {
        when {
            field is BuiltInField -> {
                val mt = field as BuiltInField
                when {
                    field == TaintProperty.INSTANCE || field == ViaProperty.INSTANCE -> {
                        propertyPropagate(baseExpr, bases, values, field, append)
                    }
                    mt == MapKeys.INSTANCE || mt == MapValues.INSTANCE || mt == Elements.INSTANCE -> {
                        var value = bases
                        val appendx = field as BuiltInField
                        val var10000 = when {
                            field !is Elements -> {
                                if (appendx is MapKeys) {
                                    BuiltInModelT.MapKeys
                                } else {
                                    if (appendx !is MapValues) {
                                        throw NotImplementedError("An operation is not implemented: unreachable")
                                    }
                                    BuiltInModelT.MapValues
                                }
                            }
                            else -> {
                                val base = bases
                                val var15 = ArrayList<Any>()

                                for (element$iv$iv in this.filter { isArray(it) }) {
                                    var15.add(element$iv$iv)
                                }

                                val this_$iv = var15 as List<Any>
                                val var15x = ArrayList<Any>()

                                for (element$iv$ivx in this.filter { !isArray(it) }) {
                                    var15x.add(element$iv$ivx)
                                }

                                value = var15x as List<Any>
                                if (!this_$iv.isEmpty()) {
                                    IFact.Builder.DefaultImpls.assignNewExpr$default(builder, env, "@arr", toHV(this_$iv), false, 8, null)
                                    builder.setArrayValueNew(env, "@arr", null, toHV(values))
                                    builder.kill("@arr")
                                }
                                BuiltInModelT.Element
                            }
                        }
                        val var27 = toHV(value)
                        hf.resolveOp(env, var27).resolve(::setField$lambda$2)
                    }
                    else -> throw NoWhenBranchMatchedException()
                }
            }
            field is ClassField -> {
                val var21 = toHV(bases)
                val var23 = toHV(values)
                val var31 = FieldUtil.INSTANCE
                val var36 = field as ClassField
                val var53 = field.getFieldType()
                val var54 = if (var53 != null) Scene.v().getTypeUnsafe(var53, true) else null
                val var55 = JFieldNameType(
                    var36.getFieldName(),
                    var54 ?: Scene.v().getObjectType() as Type
                )
                IFact.Builder.DefaultImpls.assignNewExpr$default(builder, env, "@base", var21, false, 8, null)
                builder.setFieldNew(env, "@base", var55, var23)
                builder.kill("@base")
            }
            field is AttributeName -> {
                val var22 = CheckerModelingKt.getKeyAttribute()
                val var24 = toHV(values)
                val var28 = !toHV(bases).isSingle()

                for (base in toHV(bases)) {
                    val var41 = builder.getValueData((base.getValue() as IValue), var22)
                    var var40 = var41 as? ImmutableElementHashMap
                        ?: ImmutableElementHashMap(null, null, 3, null)

                    val var42 = var40.builder()
                    var42.set(hf, env, (field as AttributeName).getName(), var24, var28)
                    val var45 = var42.build()
                    if (base.getValue() as IValue is ConstVal) {
                        return
                    }
                    builder.setValueData(env, (base.getValue() as IValue), var22, var45)
                }
            }
            field == SubFields.INSTANCE -> {
                // Do nothing
            }
        }
    }

    fun setConstValue(rvalue: IExpr, newBase: CompanionV<IValue>) {
        when (rvalue) {
            is IIexGetField -> {
                if (rvalue.getAccessPath().isEmpty()) {
                    return
                }

                val acp = CollectionsKt.dropLast(rvalue.getAccessPath(), 1)
                setField(
                    null,
                    SequencesKt.toList(hf.resolve(env, summaryCtxCalleeSite, object : IIexGetField {
                        private val $acp = acp
                        private val $rvalue = rvalue

                        override fun getAccessPath(): List<IClassField> = $acp
                        override fun getBase(): IExpr = ($rvalue as IIexGetField).getBase()
                        override fun <TResult> accept(visitor: IModelExpressionVisitor<TResult>): TResult = visitor.visit(this)
                    })),
                    listOf(newBase),
                    CollectionsKt.last(rvalue.getAccessPath()) as IClassField,
                    true
                )
            }
            is IIexLoad -> {
                store(listOf(newBase), rvalue.getOp(), true)
            }
        }
    }

    fun propertyPropagate(baseExpr: IExpr?, bases: List<Any>, values: List<Any>, field: IClassField, append: Boolean) {
        val base = toHV(bases)
        hf.resolveOp(env, base).resolve(::propertyPropagate$lambda$4)
    }

    fun getVisitor(): IModelStmtVisitor<Any> = object : IModelStmtVisitor<Any> {
        override fun default(stmt: IStmt): Any {
            throw IllegalStateException(stmt.toString())
        }

        override fun visit(stmt: IIstStoreLocal): Any {
            store(
                SequencesKt.toList(hf.resolve(env, summaryCtxCalleeSite, stmt.getValue())),
                stmt.getLocal(),
                false
            )
            return Unit
        }

        override fun visit(stmt: IIstSetField) {
            setField(
                stmt.getBase(),
                SequencesKt.toList(hf.resolve(env, summaryCtxCalleeSite, stmt.getBase())),
                SequencesKt.toList(hf.resolve(env, summaryCtxCalleeSite, stmt.getValue())),
                stmt.getField(),
                false
            )
        }
    }

    companion object {
        @JvmStatic
        fun `setField$lambda$2`(
            `$append`: Boolean,
            `this$0`: FactModeling,
            `$mt`: Any,
            `$values`: List<Any>,
            `$this$solve`: IOpCalculator,
            ret: IHeapValues.Builder,
            var6: Array<CompanionV<*>>
        ): Boolean {
            val base = var6[0]
            if (`$append`) {
                val b = `this$0`.builder.getValueData((base.getValue() as IValue), `$mt`)
                val collection = b as? ObjectValues ?: ObjectValues(`this$0`.hf.empty())
                val var11 = collection.builder()
                var11.addAll(`this$0`.toHV(`$values`))
                `this$0`.builder.setValueData(`this$0`.env, (base.getValue() as IValue), `$mt`, var11.build())
            } else {
                `this$0`.builder.setValueData(`this$0`.env, (base.getValue() as IValue), `$mt`, ObjectValues(`this$0`.toHV(`$values`)))
            }
            return true
        }

        @JvmStatic
        fun `propertyPropagate$lambda$4`(
            `$append1`: Boolean,
            `this$0`: FactModeling,
            `$field`: IClassField,
            `$values`: List<Any>,
            `$this$solve`: IOpCalculator,
            var5: IHeapValues.Builder,
            var6: Array<CompanionV<*>>
        ): Boolean {
            val base = var6[0]
            var var10000 = if (`$append1`) {
                val var9 = `this$0`.builder.getValueData((base.getValue() as IValue), `$field`)
                var9 as? ImmutableElementSet
            } else {
                null
            }

            var10000 = when {
                var10000 != null && !var10000.isEmpty() -> null
                `$values`.size == 1 -> (`$values`.first() as? ImmutableElementSet)
                else -> null
            }

            val result = var10000 ?: run {
                val setBuilder = ImmutableElementSet(null, null, 3, null).builder()
                for (typeValues in `$values`) {
                    val set = typeValues as? ImmutableElementSet
                    if (set != null) {
                        for (e in set.getElement()) {
                            setBuilder.set(`this$0`.hf, `this$0`.env, e, set.get(`this$0`.hf, e), `$append1`)
                        }
                    }
                }
                setBuilder.build()
            }

            if (base.getValue() as IValue is ConstVal) {
                return false
            } else {
                `this$0`.builder.setValueData(`this$0`.env, (base.getValue() as IValue), CheckerModelingKt.getKeyTaintProperty(), result)
                return true
            }
        }
    }
}