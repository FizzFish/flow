package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.AIContext
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.AnyNewValue
import cn.sast.dataflow.interprocedural.analysis.CallStackContext
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.FieldUtil
import cn.sast.dataflow.interprocedural.analysis.HeapDataBuilder
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IDiff
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IFieldManager
import cn.sast.dataflow.interprocedural.analysis.IHeapKVData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IHeapValuesFactory
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IVGlobal
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JFieldType
import cn.sast.dataflow.interprocedural.analysis.JOperatorV
import cn.sast.dataflow.interprocedural.analysis.JSootFieldType
import cn.sast.dataflow.interprocedural.analysis.PointsToGraphAbstract
import cn.sast.dataflow.interprocedural.analysis.PointsToGraphBuilderAbstract
import cn.sast.dataflow.interprocedural.analysis.heapimpl.ArrayHeapKV
import cn.sast.dataflow.interprocedural.check.heapimpl.FieldHeapKV
import cn.sast.dataflow.interprocedural.override.lang.WString
import cn.sast.idfa.analysis.Context
import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentMap.Builder
import soot.ArrayType
import soot.ByteType
import soot.G
import soot.IntType
import soot.RefType
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.jimple.AnyNewExpr
import soot.jimple.Constant
import soot.jimple.IntConstant
import soot.jimple.NewArrayExpr
import soot.jimple.StringConstant

@SourceDebugExtension(["SMAP\nPointsToGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/PointsToGraphBuilder\n+ 2 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 extensions.kt\nkotlinx/collections/immutable/ExtensionsKt\n*L\n1#1,612:1\n49#2:613\n44#2:614\n44#2:615\n44#2:616\n1#3:617\n362#4:618\n362#4:619\n362#4:620\n*S KotlinDebug\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/PointsToGraphBuilder\n*L\n308#1:613\n371#1:614\n372#1:615\n373#1:616\n556#1:618\n593#1:619\n596#1:620\n*E\n"])
open class PointsToGraphBuilder(
    orig: PointsToGraphAbstract<IValue>,
    hf: AbstractHeapFactory<IValue>,
    vg: IVGlobal,
    callStack: CallStackContext,
    slots: Builder<Any, IHeapValues<IValue>>,
    heap: Builder<IValue, PersistentMap<Any, IData<IValue>>>,
    calledMethods: kotlinx.collections.immutable.PersistentSet.Builder<SootMethod>
) : PointsToGraphBuilderAbstract(orig, hf, vg, callStack, slots, heap, calledMethods) {

    fun mayChange(): Boolean {
        return getSlots().build() != getOrig().getSlots() ||
            getHeap().build() != getOrig().getHeap() ||
            getCalledMethods().build() != getOrig().getCalledMethods()
    }

    override fun build(): IFact<IValue> {
        return if (!mayChange()) {
            getOrig()
        } else {
            PointsToGraph(
                getHf(),
                getVg(),
                getCallStack(),
                getSlots().build(),
                getHeap().build(),
                getCalledMethods().build()
            )
        }
    }

    override fun newSummary(env: HeapValuesEnv, src: CompanionV<IValue>, mt: Any, key: Any?): IHeapValues<IValue>? {
        if (mt != BuiltInModelT.Field) {
            if (mt === BuiltInModelT.Array) {
                val var11: Type = getType(src)
                val var9: ArrayType? = var11 as? ArrayType
                if (var9 != null) {
                    val var15: AbstractHeapFactory<IValue> = getHf()
                    val var16: AbstractHeapFactory<IValue> = getHf()
                    val var10004: Type = var9.elementType
                    return var15.push(env, var16.newSummaryVal(env, var10004, "${src.getValue().hashCode()}.$key"))
                        .markSummaryReturnValueFailedGetKeyFromKey(src, mt, key)
                        .popHV()
                }
            }
            return null
        } else {
            val var13: IHeapValues<IValue>?
            if (src.getValue() is IFieldManager) {
                val var10000: AbstractHeapFactory<IValue> = getHf()
                val var10002: IFieldManager = src.getValue() as IFieldManager
                var13 = var10000.push(env, var10002.getPhantomField(key as JFieldType))
                    .markSummaryReturnValueFailedGetKeyFromKey(src, mt, key)
                    .popHV()
            } else if (src.getValue() is AnyNewValue && key is JFieldType) {
                val var14: IVGlobal = getHf().getVg()
                val var6: FieldUtil = FieldUtil.INSTANCE
                val arrayTy: Pair<*, *> = var14.defaultValue((key as JFieldType).type)
                val var10: Constant = arrayTy.first as Constant
                var13 = getHf().push(env, getHf().newConstVal(var10, arrayTy.second as Type))
                    .markOfConstant(var10, "unset null field")
                    .popHV()
            } else {
                var13 = null
            }
            return var13
        }
    }

    fun newSummaryArraySize(env: HeapValuesEnv, allocSite: IHeapValues<IValue>): IHeapValues<IValue> {
        val var10000: AbstractHeapFactory<IValue> = getHf()
        val var10002: AbstractHeapFactory<IValue> = getHf()
        val var10004: IntType = G.v().soot_IntType()
        return var10000.push(env, var10002.newSummaryVal(env, var10004 as Type, "arraySize"))
            .markSummaryArraySize(allocSite)
            .popHV()
    }

    override fun getEmptyFieldSpace(type: RefType): FieldHeapKV<IValue> {
        return FieldSpace(type, ExtensionsKt.persistentHashMapOf(), getHf().empty())
    }

    override fun getEmptyArraySpace(
        env: HeapValuesEnv,
        allocSite: IHeapValues<IValue>,
        type: ArrayType,
        arrayLength: IHeapValues<IValue>?
    ): ArrayHeapKV<IValue> {
        return ArraySpace.Companion.v(
            getHf(),
            env,
            ExtensionsKt.persistentHashMapOf(),
            getHf().empty(),
            type,
            if (arrayLength != null && !arrayLength.isEmpty()) arrayLength else newSummaryArraySize(env, allocSite)
        )
    }

    override fun getType(value: CompanionV<IValue>): Type? {
        return (value.getValue() as IValue).type
    }

    override fun getOfSlot(env: HeapValuesEnv, slot: Any): IHeapValues<IValue> {
        var var10000: IHeapValues<IValue>? = getTargets(slot)
        if (var10000 == null) {
            var10000 = getHf().empty()
        }
        return var10000
    }

    override fun getConstantPoolObjectData(
        env: HeapValuesEnv,
        cv: CompanionV<IValue>,
        mt: Any
    ): IData<IValue>? {
        val value: IValue = cv.getValue() as IValue
        if (value is ConstVal && (value as ConstVal).v is StringConstant) {
            if (!getHeap().containsKey(cv.getValue())) {
                val var10002: Unit = env.node
                val var10003: AbstractHeapFactory<IValue> = getHf()
                val var10004: String = ((value as ConstVal).v as StringConstant).value
                val var36: ByteArray = var10004.toByteArray(Charsets.UTF_8)
                val arrayValue: JStrArrValue = JStrArrValue(var10002, var10003, var36)
                val var16: WString = WString.Companion.v()
                val var10000: AbstractHeapFactory<IValue> = emptyFieldFx().invoke(cv) as AbstractHeapFactory<IValue>
                val members: IHeapKVData.Builder = (var10000 as FieldHeapKV).builder()
                val newArrayValue: IValue = getHf().anyNewVal(getHf().vg.NEW_Env, var16.newValueExpr as AnyNewExpr) as IValue
                val newValue: IHeapValues<IValue> = getHf().empty().plus(getHf().push(env, newArrayValue).dataSequenceToSeq(cv).pop())
                val var24: IHeapValues<IValue> = getHf().empty()
                val newCoder: IHeapValues<IValue> = var24.plus(
                    JOperatorV.DefaultImpls.markOfConstant$default(
                        getHf().push(env, getHf().newConstVal(WString.Companion.LATIN1 as Constant, G.v().soot_ByteType() as Type)),
                        WString.Companion.LATIN1 as Constant,
                        null,
                        2,
                        null
                    ).pop()
                )
                val var25: IHeapValues<IValue> = getHf().empty()
                val newHash: IHeapValues<IValue> = var25.plus(
                    getHf().push(env, getHf().newConstVal(IntConstant.v((value.v as StringConstant).value.hashCode()) as Constant, G.v().soot_IntType() as Type))
                        .markOfOp("string.hash", cv)
                        .pop()
                )
                val var29: IHeapValuesFactory<IValue> = getHf()
                members.set(var29, env, JSootFieldType(var16.valueField), newValue, false)
                members.set(getHf(), env, JSootFieldType(var16.coderField), newCoder, false)
                members.set(getHf(), env, JSootFieldType(var16.hashField), newHash, false)
                setValueData(env, newArrayValue, BuiltInModelT.Array, arrayValue)
                setValueData(env, value, BuiltInModelT.Field, members.build())
                val var33: String = (value.v as StringConstant).value
                IFact.Builder.DefaultImpls.assignNewExpr$default(this, env, var33, getHf().empty().plus(cv), false, 8, null)
            }
            return getValueData(value, mt)
        } else {
            return null
        }
    }

    override fun callEntryFlowFunction(
        context: Context<SootMethod, Unit, IFact<IValue>>,
        callee: SootMethod,
        node: Unit,
        succ: Unit
    ) {
        setCallStack(CallStackContext(getCallStack(), node, callee, getCallStack().deep + 1))
        val receivers: IHeapValues<IValue>? = getSlots()[-1] as IHeapValues<IValue>?
        if (receivers != null) {
            (getSlots() as MutableMap<Any, IHeapValues<IValue>>)[-1] = getHf().canStore(receivers, callee.declaringClass.type as Type)
        }
    }

    private fun activeCalleeReports(
        container: SootMethod,
        env: HeapValuesEnv,
        ctx: AIContext,
        callEdgeValue: IFact<IValue>,
        calleeCtx: AIContext
    ): PathTransfer {
        val calleePathToCallerPath: MutableMap<EntryPath, IPath> = LinkedHashMap()
        val var10001: IDiff<IValue> = object : IDiff<IValue>(calleePathToCallerPath) {
            override fun diff(left: CompanionV<IValue>, right: CompanionV<*>) {
                val calleePath: IPath = (right as PathCompanionV).path
                if (calleePath is EntryPath) {
                    this.calleePathToCallerPath.put(calleePath, (left as PathCompanionV).path)
                }
            }
        }
        callEdgeValue.diff(var10001, calleeCtx.entryValue as IFact<IValue>)
        return PathTransfer(env, calleePathToCallerPath, ctx, calleeCtx)
    }

    override fun updateIntraEdge(
        env: HeapValuesEnv,
        ctx: soot.Context,
        calleeCtx: soot.Context,
        callEdgeValue: IFact<IValue>,
        hasReturnValue: Boolean
    ): IHeapValues<IValue>? {
        if (callEdgeValue !is PointsToGraphAbstract<*>) {
            throw IllegalArgumentException("updateIntraEdge error of fact type: ${callEdgeValue.javaClass} \n$callEdgeValue")
        } else {
            val pathTransfer: PathTransfer = activeCalleeReports(
                (ctx as AIContext).method, env, ctx as AIContext, callEdgeValue, calleeCtx as AIContext
            )
            val exitValue: IFact<IValue> = (calleeCtx as AIContext).exitValue as IFact<IValue>
            if (exitValue !is PointsToGraphAbstract<*>) {
                if (exitValue.isBottom()) {
                    return null
                } else {
                    throw IllegalArgumentException("updateIntraEdge error of fact type: ${exitValue.javaClass} \n$exitValue")
                }
            } else {
                getCalledMethods().addAll((exitValue as PointsToGraphAbstract<IValue>).calledMethods)
                if (FactValuesKt.leastExpr) {
                    val orig: IReNew<IValue> = object : IReNew<IValue>(pathTransfer) {
                        override fun checkNeedReplace(c: CompanionV<IValue>): CompanionV<IValue>? {
                            val var10000: InvokeEdgePath? = this.pathTransfer.transform((c as PathCompanionV).path)
                            if (var10000 == null) {
                                return null
                            } else if (c is CompanionValueOfConst) {
                                return CompanionValueOfConst((c as CompanionValueOfConst).value, var10000, (c as CompanionValueOfConst).attr)
                            } else if (c is CompanionValueImpl1) {
                                return CompanionValueImpl1((c as CompanionValueImpl1).value, var10000)
                            } else {
                                throw NotImplementedError(null, 1, null)
                            }
                        }

                        override fun checkNeedReplace(old: IValue): IValue {
                            return IReNew.DefaultImpls.checkNeedReplace(this, old)
                        }

                        override fun context(value: Any): IReNew<IValue> {
                            return IReNew.DefaultImpls.context(this, value)
                        }
                    }

                    for (returnValue in (exitValue as PointsToGraphAbstract<IValue>).heap.entries) {
                        val k: IValue = returnValue.key as IValue
                        val v: PersistentMap<Any, IData<IValue>> = returnValue.value as PersistentMap<Any, IData<IValue>>
                        val dataMap: PersistentMap<Any, IData<IValue>>? = getHeap()[k] as PersistentMap<Any, IData<IValue>>?
                        val dataMapBuilder: Builder<Any, IData<IValue>> = v.builder()

                        for (rpVal in dataMap.entries) {
                            val mt: Any = rpVal.key
                            val `$this$plus$iv`: IData<IValue> = rpVal.value as IData<IValue>
                            val `map$iv`: IData<IValue> = `$this$plus$iv`.cloneAndReNewObjects(orig)
                            if (`map$iv` != `$this$plus$iv`) {
                                dataMapBuilder[mt] = `map$iv`
                            }
                        }

                        if (k == getHf().vg.GLOBAL_SITE) {
                            val var41: BuiltInModelT = BuiltInModelT.Field
                            val var46: IData<IValue>? = dataMap?.get(BuiltInModelT.Field) as IData<IValue>?
                            val var52: IData<IValue>? = dataMapBuilder.get(BuiltInModelT.Field) as IData<IValue>?
                            if (var46 != null && var52 != null) {
                                val var62: HeapDataBuilder<IValue> = var52.builder() as HeapDataBuilder<IValue>
                                var62.updateFrom(getHf(), var46)
                                dataMapBuilder[var41] = var62.build()
                            }
                        }

                        val var42: PersistentMap<Any, IData<IValue>> = dataMapBuilder.build()
                        if (dataMap == null || dataMap.isEmpty()) {
                            (getHeap() as MutableMap<IValue, PersistentMap<Any, IData<IValue>>>)[k] = var42
                        } else {
                            (getHeap() as MutableMap<IValue, PersistentMap<Any, IData<IValue>>>)[k] = ExtensionsKt.putAll(dataMap, var42)
                        }
                    }

                    (ctx as AIContext).activeReport(calleeCtx as AIContext, pathTransfer)
                    val var23: IHeapValues<IValue>? = (exitValue as PointsToGraphAbstract<IValue>).getTargetsUnsafe(getVg().RETURN_LOCAL)
                    if (hasReturnValue && var23 != null) {
                        return var23.cloneAndReNewObjects(orig)
                    }
                } else {
                    val var22: MutableSet<IValue> = LinkedHashSet()

                    for (var27 in (callEdgeValue as PointsToGraphAbstract<IValue>).slots.entries) {
                        var22.addAll((var27.value as IHeapValues<IValue>).values)
                    }

                    for (var28 in (callEdgeValue as PointsToGraphAbstract<IValue>).heap.entries) {
                        val var32: IValue = var28.key as IValue

                        for (var39 in (var28.value as PersistentMap<Any, IData<IValue>>).entries) {
                            (var39.value as IData<IValue>).reference(var22)
                        }
                    }

                    val var26: IReNew<IValue> = getHf().newReNewInterface(var22)
                    if (!(exitValue as PointsToGraphAbstract<IValue>).heap.isEmpty()) {
                        for (var33 in (exitValue as PointsToGraphAbstract<IValue>).heap.entries) {
                            val var36: IValue = var33.key as IValue
                            val var38: PersistentMap<Any, IData<IValue>> = var33.value as PersistentMap<Any, IData<IValue>>
                            val var40: Builder<Any, IData<IValue>> = var38.builder()

                            for (var50 in var38.entries) {
                                val var59: IData<IValue> = var50.value as IData<IValue>
                                val var64: IData<IValue> = var59.cloneAndReNewObjects(var26)
                                if (var64 != var59) {
                                    var40[var50.key] = var64
                                }
                            }

                            var10000 = getHeap()[var36] as PersistentMap<Any, IData<IValue>>?
                            val var51: IValue? = var26.checkNeedReplace(var36)
                            if (var51 == null) {
                                (getHeap() as MutableMap<IValue, PersistentMap<Any, IData<IValue>>>)[var36] = ExtensionsKt.putAll(var10000 ?: ExtensionsKt.persistentHashMapOf(), var40.build())
                            } else if (var36 != var51) {
                                (getHeap() as MutableMap<IValue, PersistentMap<Any, IData<IValue>>>)[var51] = ExtensionsKt.putAll(var10000 ?: ExtensionsKt.persistentHashMapOf(), var40.build())
                                (getHeap() as MutableMap<IValue, PersistentMap<Any, IData<IValue>>>).remove(var36)
                            }
                        }
                    }

                    if (hasReturnValue) {
                        val returnValue = (exitValue as PointsToGraphAbstract<IValue>).getTargetsUnsafe(getVg().RETURN_LOCAL)
                        if (returnValue == null) {
                            return null
                        }
                        return returnValue.cloneAndReNewObjects(var26)
                    }
                }
                return null
            }
        }
    }

    @SourceDebugExtension(["SMAP\nPointsToGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/PointsToGraphBuilder$PathTransfer\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,612:1\n1230#2,4:613\n808#2,11:617\n865#2,2:628\n*S KotlinDebug\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/PointsToGraphBuilder$PathTransfer\n*L\n446#1:613,4\n459#1:617,11\n460#1:628,2\n*E\n"])
    inner class PathTransfer(
        val env: HeapValuesEnv,
        private val calleePathToCallerPath: Map<EntryPath, IPath>,
        val ctx: AIContext,
        val calleeCtx: AIContext
    ) {
        fun transform(calleePath: IPath, entryHeads: Set<EntryPath>): InvokeEdgePath? {
            if (entryHeads.isEmpty()) {
                return null
            } else {
                val destination: MutableMap<IPath, EntryPath> = HashMap()
                for (element in entryHeads) {
                    var path: IPath? = calleePathToCallerPath[element]
                    if (path == null) {
                        path = UnknownPath.Companion.v(env)
                    }
                    destination[path] = element
                }
                return InvokeEdgePath.Companion.v(env, destination, calleePath, ctx.method, calleeCtx.method)
            }
        }

        fun transform(calleePath: IPath): InvokeEdgePath? {
            val entryHeads2 = PathGenerator.getHeads$default(PathGeneratorImpl.Companion.pathGenerator, calleePath, null, 2, null)
            val filtered = entryHeads2.filterIsInstance<EntryPath>()
            val destination: MutableSet<EntryPath> = LinkedHashSet()
            for (element in filtered) {
                if (calleeCtx.entries.contains(element)) {
                    destination.add(element)
                }
            }
            return if (destination.isEmpty()) null else transform(calleePath, destination)
        }
    }
}