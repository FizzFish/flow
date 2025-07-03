package cn.sast.dataflow.interprocedural.check

import cn.sast.api.config.ExtSettings
import cn.sast.api.config.StaticFieldTrackingMode
import cn.sast.api.util.SootUtilsKt
import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AIContext
import cn.sast.dataflow.interprocedural.analysis.AJimpleInterProceduralAnalysisKt
import cn.sast.dataflow.interprocedural.analysis.AbstractBOTTOM
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AbstractTOP
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.CallStackContext
import cn.sast.dataflow.interprocedural.analysis.EntryParam
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IVGlobal
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.PointsToGraphAbstract
import cn.sast.idfa.analysis.FixPointStatus
import cn.sast.idfa.analysis.InterproceduralCFG
import java.util.Collections
import java.util.HashSet
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import mu.KLogger
import soot.Body
import soot.G
import soot.Local
import soot.RefLikeType
import soot.RefType
import soot.Scene
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.Value
import soot.jimple.DynamicInvokeExpr
import soot.jimple.InstanceInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.Jimple
import soot.jimple.NopStmt
import soot.jimple.StaticInvokeExpr

@SourceDebugExtension(["SMAP\nInterProceduralValueAnalysis.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InterProceduralValueAnalysis.kt\ncn/sast/dataflow/interprocedural/check/InterProceduralValueAnalysis\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,315:1\n1#2:316\n1863#3,2:317\n*S KotlinDebug\n*F\n+ 1 InterProceduralValueAnalysis.kt\ncn/sast/dataflow/interprocedural/check/InterProceduralValueAnalysis\n*L\n258#1:317,2\n*E\n"])
abstract class InterProceduralValueAnalysis : ACheckCallAnalysis {
    private val bottom: IFact<IValue>

    constructor(vg: IVGlobal, hf: AbstractHeapFactory<IValue>, icfg: InterproceduralCFG) : super(hf, icfg) {
        val unit: NopStmt = Jimple.v().newNopStmt()
        val method: SootMethod = Scene.v().makeSootMethod("initConstantPoolObjectData", emptyList(), G.v().soot_VoidType() as Type)
        val var10003: Unit = unit as Unit
        hf.setConstantPoolObjectData(getTopState(CallStackContext(null, var10003, method, 0)).builder())
        bottom = object : AbstractBOTTOM<IValue>() {}
    }

    private fun getTopState(callStackContext: CallStackContext): IFact<IValue> {
        return object : AbstractTOP<IValue>(callStackContext, getHf()) {
            override fun builder(): IFact.Builder<IValue> {
                return PointsToGraph(
                    getHf(),
                    getHf().getVg(),
                    callStackContext,
                    ExtensionsKt.persistentHashMapOf(),
                    ExtensionsKt.persistentHashMapOf(),
                    ExtensionsKt.persistentHashSetOf()
                ).builder()
            }
        }
    }

    override fun boundaryValue(entryPoint: SootMethod): IFact<IValue> {
        if (!entryPoint.isConcrete) {
            return bottom
        } else if (!entryPoint.hasActiveBody()) {
            return bottom
        } else {
            val entryUnit: Unit = entryPoint.activeBody.units.first()
            val entryValue = getTopState(CallStackContext(null, entryUnit, entryPoint, 0)).builder()
            var env = 0

            for (argIndex in 0 until entryPoint.parameterCount) {
                val var10000 = getHf()
                val var10001 = entryPoint.activeBody
                var var12 = AJimpleInterProceduralAnalysisKt.getParameterUnit(var10001, env)
                if (var12 == null) {
                    var12 = entryUnit
                }

                val envx = var10000.env(var12)
                IFact.Builder.DefaultImpls.assignNewExpr$default(
                    entryValue, envx, env, getHf().push(envx, EntryParam(entryPoint, env)).markOfEntryMethodParam(entryPoint).popHV(), false, 8, null
                )
            }

            if (!entryPoint.isStatic) {
                val var11 = getHf()
                val var13 = entryPoint.activeBody
                var var14 = AJimpleInterProceduralAnalysisKt.getParameterUnit(var13, -1)
                if (var14 == null) {
                    var14 = entryUnit
                }

                val var9 = var11.env(var14)
                IFact.Builder.DefaultImpls.assignNewExpr$default(
                    entryValue, var9, -1, getHf().push(var9, EntryParam(entryPoint, -1)).markOfEntryMethodParam(entryPoint).popHV(), false, 8, null
                )
            }

            getHf().getVg().setStaticFieldTrackingMode(getStaticFieldTrackingMode())
            if (getStaticFieldTrackingMode() != StaticFieldTrackingMode.None) {
                val var10 = getHf().env(entryUnit)
                IFact.Builder.DefaultImpls.assignNewExpr$default(
                    entryValue,
                    var10,
                    getHf().getVg().getGLOBAL_LOCAL(),
                    getHf().push(var10, getHf().getVg().getGLOBAL_SITE()).markOfEntryMethodParam(entryPoint).popHV(),
                    false,
                    8,
                    null
                )
            }

            return entryValue.build()
        }
    }

    override fun copy(src: IFact<IValue>): IFact<IValue> {
        return src
    }

    override fun meet(op1: IFact<IValue>, op2: IFact<IValue>): IFact<IValue> {
        return when {
            op1.isBottom() -> op2
            op2.isBottom() -> op1
            op1.isTop() -> op2
            op2.isTop() -> op1
            op1 === op2 -> op1
            else -> {
                val var3 = op1.builder()
                var3.union(op2)
                var3.build()
            }
        }
    }

    override fun shallowMeet(op1: IFact<IValue>, op2: IFact<IValue>): IFact<IValue> {
        throw NotImplementedError("An operation is not implemented: Not yet implemented")
    }

    override fun merge(local: IFact<IValue>, ret: IFact<IValue>): IFact<IValue> {
        return ret
    }

    override fun bottomValue(): IFact<IValue> {
        return bottom
    }

    override fun newExprEnv(context: AIContext, node: Unit, inValue: IFact<IValue>): AnyNewExprEnv {
        return AnyNewExprEnv(context.getMethod(), node)
    }

    fun isRecursive(callee: SootMethod, inValue: IFact<IValue>): Boolean {
        var cur: CallStackContext = (inValue as PointsToGraphAbstract).getCallStack()
        val set = LinkedHashSet<SootMethod>()

        while (cur != null) {
            if (!set.add(cur.getMethod())) {
                return true
            }
            cur = cur.getCaller()
        }

        return false
    }

    override fun isAnalyzable(callee: SootMethod, in1: IFact<IValue>): Boolean {
        return super.isAnalyzable(callee, in1) && !isRecursive(callee, in1)
    }

    override fun resolveTargets(callerMethod: SootMethod, ie: InvokeExpr, node: Unit, inValue: IFact<IValue>): Set<SootMethod> {
        if (ie !is StaticInvokeExpr && ie !is DynamicInvokeExpr) {
            val targets = HashSet<SootMethod>()
            val var24 = (ie as InstanceInvokeExpr).base
            val receiver = var24 as Local
            val heapNodes = inValue.getTargetsUnsafe(var24 as Local)
            if (heapNodes != null && heapNodes.isNotEmpty()) {
                for (v in heapNodes.values) {
                    val target = v.getType()
                    val var25 = target as? RefLikeType
                    if (var25 != null) {
                        val var26 = if (v.typeIsConcrete()) {
                            var25
                        } else if (var25 is RefType) {
                            val var14 = receiver.type
                            var var27 = var14 as? RefType
                            if (var27 == null) {
                                var27 = Scene.v().objectType
                            }
                            var27 as RefLikeType
                        } else {
                            var25
                        }

                        if (var26 != null) {
                            val var21 = SootUtilsKt.getCallTargets(var26 as Type, callerMethod, ie, false)
                            while (var21.hasNext()) {
                                targets.add(var21.next() as SootMethod)
                            }
                        }
                    }
                }
            }

            if (targets.isEmpty()) {
                val var15 = setOf((ie as InstanceInvokeExpr).method)
                for (element in var15) {
                    targets.add(element as SootMethod)
                }
            }

            if (targets.isEmpty()) {
                return setOf((ie as InstanceInvokeExpr).method)
            } else {
                val var16 = ExtSettings.INSTANCE.dataFlowResolveTargetsMaxNum
                if (var16 >= 0L && targets.size >= var16) {
                    logger.debug { resolveTargets$lambda$2(node, targets, callerMethod) }
                    return setOf((ie as InstanceInvokeExpr).method)
                } else {
                    return targets
                }
            }
        } else {
            return Collections.singleton(ie.method)
        }
    }

    override fun hasChange(context: AIContext, node: Unit, succ: Unit, old: IFact<IValue>, new: IFact<IValue>): FixPointStatus {
        val var10000 = context.iteratorCount
        val var8 = var10000[node] as? Int ?: 0
        val count = var8 + 1
        val var7 = count
        var10000 = context.iteratorCount
        var10000[node] = var7
        if (!new.isValid()) {
            return FixPointStatus.HasChange
        } else if ((context.method.declaringClass.isApplicationClass || count >= ExtSettings.INSTANCE.dataFlowIteratorCountForLibClasses)
            && (!context.method.declaringClass.isApplicationClass || count >= ExtSettings.INSTANCE.dataFlowIteratorCountForAppClasses)) {
            val var10 = context.widenNode
            return if (var10.add(node to succ)) FixPointStatus.NeedWideningOperators else FixPointStatus.Fixpoint
        } else {
            return old.hasChange(context, new)
        }
    }

    companion object {
        @JvmStatic
        fun resolveTargets$lambda$2(node: Unit, targets: Set<*>, callerMethod: SootMethod): String {
            return "Too many callee at $node. size: ${targets.size}. in $callerMethod line:${node.javaSourceStartLineNumber}"
        }

        @JvmStatic
        fun logger$lambda$3() {
        }

        private lateinit var logger: KLogger
    }
}