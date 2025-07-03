@file:Suppress("UNCHECKED_CAST")

package cn.sast.dataflow.interprocedural.check

import cn.sast.api.util.SootUtilsKt
import cn.sast.dataflow.interprocedural.analysis.*
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.check.checker.CheckerModelingKt
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSet
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSetBuilder
import cn.sast.dataflow.interprocedural.check.heapimpl.ObjectValues
import cn.sast.idfa.check.ICallCB
import com.feysh.corax.config.api.*
import com.feysh.corax.config.api.utils.UtilsKt
import soot.*
import soot.jimple.*
import soot.jimple.internal.JIfStmt
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.first
import kotlin.collections.set

@SourceDebugExtension(["SMAP\nHeapFactory.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HeapFactory.kt\ncn/sast/dataflow/interprocedural/check/HeapFactory\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1430:1\n1755#2,3:1431\n1#3:1434\n*S KotlinDebug\n*F\n+ 1 HeapFactory.kt\ncn/sast/dataflow/interprocedural/check/HeapFactory\n*L\n1150#1:1431,3\n*E\n"])
internal class HeapFactory(vg: IVGlobal) : AbstractHeapFactory<IValue> {
    override val vg: IVGlobal
    val empty: HeapValues
    val nullConst: IValue

    init {
        this.vg = vg
        this.empty = HeapValues.Companion.empty$corax_data_flow()
        val constValCompanion = ConstVal.Companion
        val nullConstant = NullConstant.v()
        val nullType = G.v().soot_NullType()
        this.nullConst = constValCompanion.v(nullConstant, nullType as Type)
    }

    override fun empty(): IHeapValues<IValue> {
        return empty
    }

    fun getField(env: HeapValuesEnv, fact: Builder<IValue>, base: IHeapValues<IValue>, field: JFieldType): IHeapValues<IValue> {
        IFact.Builder.DefaultImpls.assignNewExpr$default(fact, env, "@base", base, false, 8, null)
        fact.getField(env, "@res", "@base", field, true)
        val value = fact.getTargetsUnsafe("@res") as IHeapValues<IValue>?
        fact.kill("@res")
        fact.kill("@base")
        return value ?: empty
    }

    override fun canStore(receivers: IHeapValues<IValue>, receiverType: Type): IHeapValues<IValue> {
        val receiversValues = emptyBuilder()
        val hierarchy = Scene.v().orMakeFastHierarchy

        for (receiver in receivers) {
            val v = receiver.value as IValue
            val canStore = if (v.typeIsConcrete()) {
                hierarchy.canStoreType(v.type, receiverType)
            } else if (receiverType !is RefLikeType) {
                true
            } else if (receiverType is RefType && receiverType.sootClass.isPhantom) {
                true
            } else {
                val type = if (v.type is RefType) {
                    AnySubType.v(v.type as RefType) as Type
                } else {
                    v.type
                }
                v.type == receiverType || hierarchy.canStoreType(type, receiverType)
            }

            if (canStore) {
                receiversValues.add(receiver)
            }
        }

        return receiversValues.build()
    }

    override fun resolve(env: HeapValuesEnv, atCall: ICallCB<IHeapValues<IValue>, Builder<IValue>>, iExpr: IExpr): Sequence<Any> {
        return iExpr.accept(object : IModelExpressionVisitor<Sequence<Any>>(this, env, atCall) {
            // TODO("FIXME — Couldn't be decompiled")
        })
    }

    fun anyNewVal(newExprEnv: AnyNewExprEnv, newExr: AnyNewExpr): IValue {
        val node = newExprEnv.node
        val signature = newExprEnv.method.signature
        return AnyNewValue(node, signature, newExr)
    }

    fun newSummaryVal(env: HeapValuesEnv, type: Type, special: Any): IValue {
        return SummaryValue.Companion.v(type, env.node, special)
    }

    fun newConstVal(constant: Constant, type: Type): ConstVal {
        return ConstVal.Companion.v(constant, type)
    }

    override fun env(node: Unit): HeapValuesEnv {
        return HeapValuesEnvImpl(node)
    }

    override fun env(ctx: AIContext, node: Unit): HookEnv {
        return HookEnv(ctx, node)
    }

    override fun getPathFactory(): PathFactory<IValue> {
        return PathFactoryImpl()
    }

    override fun resolveOp(env: HeapValuesEnv, vararg ops: IHeapValues<IValue>?): IOpCalculator<IValue> {
        return OpCalculator(env, this, ops.copyOf())
    }

    override fun newReNewInterface(orig: MutableSet<IValue>): IReNew<IValue> {
        return object : IReNew<IValue> {
            private val newValueMap = ConcurrentHashMap<IValue, IValue>()
            private val origSet = orig

            override fun checkNeedReplace(old: IValue): IValue? {
                if (FactValuesKt.leastExpr) {
                    return null
                } else if (old !is SummaryValue && old !is AnyNewValue) {
                    return null
                } else if (origSet.contains(old)) {
                    return null
                }

                synchronized(this) {
                    if (newValueMap[old] == null) {
                        newValueMap[old] = old.clone()
                    }
                }
                return null
            }

            override fun checkNeedReplace(c: CompanionV<IValue>): CompanionV<IValue> {
                return IReNew.DefaultImpls.checkNeedReplace(this, c)
            }

            override fun context(value: Any): IReNew<IValue> {
                return IReNew.DefaultImpls.context(this, value)
            }
        }
    }

    fun getBooleanValue(v: IValue, checkType: Boolean): Boolean? {
        return FactValuesKt.getBooleanValue(v, checkType)
    }

    fun getIntValue(v: IValue, checkType: Boolean): Int? {
        return FactValuesKt.getIntValue(v, checkType)
    }

    override fun single(v: CompanionV<IValue>): IHeapValues<IValue> {
        return empty().plus(v)
    }

    fun push(env: HeapValuesEnv, alloc: IValue): JOperatorV<IValue> {
        return OperatorPathFactory(this, env, alloc, null, 8, null)
    }

    override fun push(env: HeapValuesEnv, value: CompanionV<IValue>): JOperatorC<IValue> {
        val res = ObjectRef<CompanionV<IValue>>()
        res.element = value
        return object : JOperatorC<IValue>(res, this) {
            override fun markEntry(): JOperatorC<IValue> = this

            override fun pop(): CompanionV<IValue> = res.element as CompanionV<IValue>

            override fun popHV(): IHeapValues<IValue> = this@HeapFactory.empty.plus(res.element as CompanionV<IValue>)
        }
    }

    override fun push(env: HeapValuesEnv, value: IHeapValues<IValue>): JOperatorHV<IValue> {
        return JOperatorHVImpl(this, env, value)
    }

    private fun checkTypeHierarchyIsPhantom(type: Type): Boolean {
        return when (type) {
            is ArrayType -> checkTypeHierarchyIsPhantom(type.elementType)
            is RefType -> {
                if (!type.hasSootClass()) {
                    false
                } else {
                    UtilsKt.findAncestors(type.sootClass).any { it.isPhantom }
                }
            }
            else -> false
        }
    }

    fun tryCastRef(toType: RefLikeType, it: IValue, h: FastHierarchy, must: Boolean): IValue? {
        val fromType = it.type
        if (fromType is UnknownType) {
            return it
        }

        val fromIsPhantom = checkTypeHierarchyIsPhantom(fromType)
        val toIsPhantom = checkTypeHierarchyIsPhantom(toType)
        val canStore = h.canStoreType(fromType, toType)

        return if (!it.typeIsConcrete() || !canStore && !fromIsPhantom && !toIsPhantom) {
            if (!must && !it.typeIsConcrete()) {
                if (!fromIsPhantom && !toIsPhantom) {
                    if (!canStore && h.canStoreType(toType, fromType)) {
                        it.copy(toType)
                    } else {
                        if (canStore && h.canStoreType(toType, fromType)) it else null
                    }
                } else {
                    it.copy(toType)
                }
            } else {
                null
            }
        } else {
            it
        }
    }

    override fun resolveCast(
        env: HeapValuesEnv,
        fact: Builder<IValue>,
        toType: Type,
        fromValues: IHeapValues<IValue>
    ): IOpCalculator<IValue>? {
        val hf = this
        val unop = resolveOp(env, fromValues)
        
        return when {
            toType is PrimType -> {
                unop.resolve { res, var6 ->
                    resolveCastPrimitive(hf, toType, env, fact, res, var6)
                }
                unop
            }
            toType is RefLikeType -> {
                val h = Scene.v().orMakeFastHierarchy
                val replaceMap = LinkedHashMap<IValue, IValue>()
                
                unop.resolve { res, var8 ->
                    resolveCastRefLike(this, toType, h, hf, env, replaceMap, res, var8)
                }
                
                val rpFactory = object : IReNew<IValue> {
                    private val map = replaceMap
                    
                    override fun checkNeedReplace(old: IValue): IValue? = map[old]
                    override fun checkNeedReplace(c: CompanionV<IValue>): CompanionV<IValue> = IReNew.DefaultImpls.checkNeedReplace(this, c)
                    override fun context(value: Any): IReNew<IValue> = IReNew.DefaultImpls.context(this, value)
                }
                
                require(fact is PointsToGraphBuilderAbstract) { "Check failed." }
                fact.apply(rpFactory)
                
                for ((from, to) in replaceMap) {
                    if (toType is ArrayType && from.type !is ArrayType && fact.getArray(to) == null) {
                        val array = fact.getValueData(to, BuiltInModelT.Element)
                        val collection = array as? ObjectValues
                        if (collection != null) {
                            IFact.Builder.DefaultImpls.assignNewExpr$default(
                                fact, env, "@arr", empty.plus(hf.push(env, to).markOfCastTo(toType).pop()), 
                                false, 8, null
                            )
                            fact.setArrayValueNew(env, "@arr", null, collection.values)
                            fact.kill("@arr")
                        }
                    }
                }
                unop
            }
            else -> null
        }
    }

    override fun resolveInstanceOf(
        env: HeapValuesEnv,
        fromValues: IHeapValues<IValue>,
        checkType: Type
    ): IOpCalculator<IValue> {
        val unop = resolveOp(env, fromValues)
        unop.resolve { res, var6 ->
            resolveInstanceOfImpl(Scene.v().orMakeFastHierarchy, checkType, this, env, res, var6)
        }
        unop.putSummaryIfNotConcrete(G.v().soot_BooleanType, "instanceOfValue")
        return unop
    }

    override fun resolveUnop(
        env: HeapValuesEnv,
        fact: IIFact<IValue>,
        opValues: IHeapValues<IValue>,
        expr: UnopExpr,
        resType: Type
    ): IOpCalculator<IValue> {
        val unop = resolveOp(env, opValues)
        when (expr) {
            is NegExpr -> {
                unop.resolve { res, var6 ->
                    resolveNegExpr(this, env, resType, expr, res, var6)
                }
                unop.putSummaryIfNotConcrete(G.v().soot_BooleanType, expr)
            }
            is LengthExpr -> {
                unop.resolve { res, var5 ->
                    resolveLengthExpr(fact, this, env, res, var5)
                }
                unop.putSummaryIfNotConcrete(resType, expr)
            }
            else -> unop.putSummaryValue(resType, expr)
        }
        return unop
    }

    fun resolveBinopOrNull(
        env: HeapValuesEnv,
        expr: BinopExpr,
        clop: CompanionV<IValue>,
        crop: CompanionV<IValue>,
        resType: Type
    ): CompanionV<IValue>? {
        val lop = clop.value as IValue
        val rop = crop.value as IValue
        val c1 = (lop as? ConstVal)?.v as? NumericConstant
        val c2 = (rop as? ConstVal)?.v as? NumericConstant
        
        if (c1 != null && c2 != null) {
            return try {
                val result = SootUtilsKt.evalConstantBinop(expr, c1, c2)
                result?.let { 
                    push(env, newConstVal(it, resType)).markSootBinOp(expr, clop, crop).pop()
                }
            } catch (e: Exception) {
                null
            }
        }
        
        if (expr !is EqExpr && expr !is NeExpr) return null
        
        if ((expr.op1.type is RefLikeType) && (expr.op2.type is RefLikeType)) {
            val equality = when {
                lop is IFieldManager && rop is IFieldManager -> lop == rop
                (lop !is AnyNewValue || rop !is AnyNewValue) && 
                    lop !is SummaryValue && rop !is SummaryValue -> {
                    if (lop !is ConstVal && rop !is ConstVal) lop == rop else null
                }
                else -> if (FactValuesKt.leastExpr) null else lop == rop
            }
            
            return equality?.let {
                val value = if (expr is EqExpr) equality else !equality
                push(env, newConstVal(IntConstant.v(if (value) 1 else 0), resType))
                    .markSootBinOp(expr, clop, crop)
                    .pop()
            }
        }
        return null
    }

    fun resolveBinop(
        env: HeapValuesEnv,
        expr: BinopExpr,
        clop: CompanionV<IValue>,
        crop: CompanionV<IValue>,
        resType: Type
    ): CompanionV<IValue> {
        return resolveBinopOrNull(env, expr, clop, crop, resType)
            ?: push(env, newSummaryVal(env, resType, expr)).markSootBinOp(expr, clop, crop).pop()
    }

    override fun resolveBinop(
        env: HeapValuesEnv,
        fact: Builder<IValue>,
        op1Values: IHeapValues<IValue>,
        op2Values: IHeapValues<IValue>,
        expr: BinopExpr,
        resType: Type
    ): IOpCalculator<IValue> {
        val binop = resolveOp(env, op1Values, op2Values)
        val types = LinkedHashSet<ImmutableElementSet<*>>()
        
        binop.resolve { res, var8 ->
            resolveBinopImpl(this, env, expr, resType, fact, types, res, var8)
        }
        
        binop.putSummaryIfNotConcrete(resType, expr)
        
        if (env.node is JIfStmt) {
            return binop
        }
        
        resolveBinopTaint(this, env, fact, binop.res.build(), types, TaintProperty.INSTANCE, false)
        return binop
    }

    // Helper functions for resolve operations
    private fun resolveCastPrimitive(
        hf: HeapFactory,
        toType: Type,
        env: HeapValuesEnv,
        fact: IFact.Builder<IValue>,
        res: IHeapValues.Builder<IValue>,
        var6: Array<CompanionV<IValue>>
    ): Boolean {
        val op = var6[0].value as IValue
        val casted = (op as? ConstVal)?.v as? NumericConstant
        
        val result = if (casted != null) {
            SootUtilsKt.castTo(casted, toType)?.let { hf.newConstVal(it, toType) }
        } else {
            hf.newSummaryVal(env, toType, "castValue")
        } ?: return false
        
        fact.copyValueData(op, result)
        res.add(hf.push(env, result).markOfCastTo(toType as PrimType).pop())
        return true
    }

    private fun resolveCastRefLike(
        hf: HeapFactory,
        toType: RefLikeType,
        h: FastHierarchy,
        factory: HeapFactory,
        env: HeapValuesEnv,
        replaceMap: MutableMap<IValue, IValue>,
        res: IHeapValues.Builder<IValue>,
        var8: Array<CompanionV<IValue>>
    ): Boolean {
        val op = var8[0].value as IValue
        val casted = hf.tryCastRef(toType, op, h, false) ?: return true
        
        val to = factory.push(env, casted).markOfCastTo(toType).pop()
        if (casted != op) {
            replaceMap[op] = casted
        }
        res.add(to)
        return true
    }

    private fun resolveInstanceOfImpl(
        h: FastHierarchy,
        checkType: Type,
        hf: HeapFactory,
        env: HeapValuesEnv,
        res: IHeapValues.Builder<IValue>,
        var6: Array<CompanionV<IValue>>
    ): Boolean {
        val op = var6[0].value as IValue
        val opType = op.type ?: return false
        if (opType is UnknownType) return false
        
        val canStore = h.canStoreType(opType, checkType)
        
        return when {
            op.typeIsConcrete() || canStore -> {
                res.add(hf.push(env, hf.toConstVal(canStore)).markOfInstanceOf().pop())
                true
            }
            h.canStoreType(checkType, opType) -> false
            else -> {
                res.add(hf.push(env, hf.toConstVal(false)).markOfInstanceOf().pop())
                true
            }
        }
    }

    private fun resolveNegExpr(
        hf: HeapFactory,
        env: HeapValuesEnv,
        resType: Type,
        expr: NegExpr,
        res: IHeapValues.Builder<IValue>,
        var6: Array<CompanionV<IValue>>
    ): Boolean {
        val cop = var6[0]
        val op = var6[0].value as IValue
        val numericConst = (op as? ConstVal)?.v as? NumericConstant ?: return false
        
        res.add(hf.push(env, hf.newConstVal(numericConst.negate(), resType))
            .markOfNegExpr(expr, cop)
            .pop())
        return true
    }

    private fun resolveLengthExpr(
        fact: IIFact<IValue>,
        hf: HeapFactory,
        env: HeapValuesEnv,
        res: IHeapValues.Builder<IValue>,
        var5: Array<CompanionV<IValue>>
    ): Boolean {
        val cop = var5[0]
        val arrayLength = fact.getArrayLength(var5[0].value) ?: return false
        
        res.add(hf.push(env, arrayLength).markOfArrayLength(cop).pop())
        return true
    }

    private fun resolveBinopImpl(
        hf: HeapFactory,
        env: HeapValuesEnv,
        expr: BinopExpr,
        resType: Type,
        fact: IFact.Builder<IValue>,
        types: MutableSet<ImmutableElementSet<*>>,
        res: IHeapValues.Builder<IValue>,
        var8: Array<CompanionV<IValue>>
    ): Boolean {
        val clop = var8[0]
        val crop = var8[1]
        val r = hf.resolveBinop(env, expr, clop, crop, resType)
        
        (fact.getValueData(clop.value, CheckerModelingKt.keyTaintProperty) as? ImmutableElementSet<*>)?.let { types.add(it) }
        (fact.getValueData(crop.value, CheckerModelingKt.keyTaintProperty) as? ImmutableElementSet<*>)?.let { types.add(it) }
        
        if (r == null) return false
        res.add(r)
        return true
    }

    private fun resolveBinopTaint(
        hf: HeapFactory,
        env: HeapValuesEnv,
        fact: IFact.Builder<IValue>,
        bases: IHeapValues<IValue>,
        values: MutableSet<ImmutableElementSet<*>>,
        field: IClassField,
        append: Boolean
    ) {
        hf.resolveOp(env, bases).resolve { var7, var8 ->
            resolveTaintOperation(append, fact, field, values, env, hf, var7, var8)
        }
    }

    private fun resolveTaintOperation(
        append1: Boolean,
        fact: IFact.Builder<IValue>,
        field: IClassField,
        values: Set<ImmutableElementSet<*>>,
        env: HeapValuesEnv,
        hf: HeapFactory,
        var7: IHeapValues.Builder<IValue>,
        var8: Array<CompanionV<IValue>>
    ): Boolean {
        val base = var8[0]
        val baseValue = base.value
        
        val elementSet = when {
            append1 -> fact.getValueData(baseValue, field) as? ImmutableElementSet<*>
            else -> null
        }?.takeIf { it.isNotEmpty() } ?: when {
            values.size == 1 -> values.first()
            else -> null
        } ?: ImmutableElementSet<Any?>(null, null, 3, null)
        
        val setBuilder = elementSet.builder()
        
        for (typeValues in values) {
            (typeValues as? ImmutableElementSet<*>)?.let { set ->
                set.element.forEach { e ->
                    setBuilder.set(hf, env, e, set.get(hf, e), append1)
                }
            }
        }
        
        val resultSet = setBuilder.build()
        
        if (baseValue is ConstVal) return false
        
        fact.setValueData(env, baseValue, field, resultSet)
        return true
    }

    private inner class HeapValuesEnvImpl(node: Unit) : HeapValuesEnv(node)
    private inner class PathFactoryImpl : PathFactory<IValue>()
    private inner class OperatorPathFactory(
        factory: HeapFactory,
        env: HeapValuesEnv,
        alloc: IValue,
        special: Any?,
        flags: Int,
        privateData: Any?
    ) : JOperatorV<IValue>(factory, env, alloc, special, flags, privateData)
}