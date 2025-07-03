package cn.sast.dataflow.interprocedural.check.printer

import cn.sast.api.util.SootUtilsKt
import cn.sast.dataflow.interprocedural.analysis.EntryParam
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.PhantomField
import cn.sast.dataflow.interprocedural.check.AssignLocalPath
import cn.sast.dataflow.interprocedural.check.ExitInvoke
import cn.sast.dataflow.interprocedural.check.GetEdgePath
import cn.sast.dataflow.interprocedural.check.IPath
import cn.sast.dataflow.interprocedural.check.InvokeEdgePath
import cn.sast.dataflow.interprocedural.check.LiteralPath
import cn.sast.dataflow.interprocedural.check.MergePath
import cn.sast.dataflow.interprocedural.check.ModelBind
import cn.sast.dataflow.interprocedural.check.PrevCallStmtInfo
import cn.sast.dataflow.interprocedural.check.SetEdgePath
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.TaintProperty
import com.feysh.corax.config.api.baseimpl.IexConst
import com.feysh.corax.config.api.report.Region
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import soot.Body
import soot.Local
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.Value
import soot.jimple.Constant
import soot.jimple.DefinitionStmt
import soot.jimple.IdentityStmt
import soot.jimple.InvokeExpr
import soot.jimple.Stmt
import soot.tagkit.ParamNamesTag

@SourceDebugExtension(["SMAP\nEventPrinter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/EventPrinter\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 SootUtils.kt\ncn/sast/api/util/SootUtilsKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,245:1\n1#2:246\n1#2:249\n1#2:251\n1#2:253\n310#3:247\n303#3:248\n303#3:250\n303#3:252\n808#4,11:254\n*S KotlinDebug\n*F\n+ 1 EventPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/EventPrinter\n*L\n101#1:249\n129#1:251\n156#1:253\n100#1:247\n101#1:248\n129#1:250\n156#1:252\n225#1:254,11\n*E\n"])
internal class EventPrinter(prefix: String) {
    private val prefix: String
    private var outputEn: StringBuffer
    private var outputZh: StringBuffer

    var region: Region? = null
        internal set

    val message: Map<Language, String>?
        get() = if (outputEn.isEmpty()) {
            null
        } else {
            mapOf(
                Language.EN to "$prefix ${outputEn}",
                Language.ZH to "$prefix ${outputZh}"
            )
        }

    init {
        this.prefix = prefix
        this.outputEn = StringBuffer()
        this.outputZh = StringBuffer()
    }

    operator fun StringBuffer.plusAssign(string: String) {
        this.append(string)
    }

    fun default(pathNode: IPath, unit: Unit) {
        val keyPath = SimpleUnitPrinter.Companion.getStringOf(unit)
        outputEn += "key path: $keyPath"
        outputZh += "关键路径: $keyPath"
    }

    private fun printModelingBinding(v: ModelBind, unit: Unit, sootMethod: SootMethod) {
        if (v.getMt() is TaintProperty) {
            val call = v.getObj()
            if (call !is EntryParam && call !is PhantomField) {
                outputZh += "污点传递"
                outputEn += "Taint propagate"
            } else {
                outputZh += "污点源 Source 点: "
                outputEn += "Taint source: "
                val msg = v.getObj()
                if (msg is EntryParam && printParameterNameByIndex(msg.method, msg.paramIndex)) {
                    return
                }
            }

            val var11 = SimpleUnitPrinter.Companion.getStringOf(unit)
            var var12 = false
            if (unit !is IdentityStmt) {
                write(": $var11")
                var12 = true
            }

            if (v.getInfo() != null) {
                val params = v.getInfo().getParameterNamesInUnitDefUse(unit)
                if (params.isNotEmpty()) {
                    val paramsStr = params.joinToString(", ")
                    outputZh += ", 关键位置: `$paramsStr`"
                    outputEn += ", key scope: `$paramsStr`"
                    if (v.getInfo() is PrevCallStmtInfo) {
                        region = (v.getInfo() as PrevCallStmtInfo).firstParamIndex?.let {
                            Region.Companion.getParamRegion(sootMethod, it)
                        }
                    }
                    var12 = true
                }
            }

            if (var12) {
                return
            }

            clean()
        }

        default(v, unit)
    }

    fun printModeling(pathEvent: ModelBind, unit: Unit, sootMethod: SootMethod): EventPrinter {
        printModelingBinding(pathEvent, unit, sootMethod)
        return this
    }

    fun normalPrint(node: ExitInvoke): EventPrinter {
        val call = SimpleUnitPrinter.Companion.getStringOf(
            node.invoke.callee,
            (node.invoke.node as? DefinitionStmt)?.leftOp,
            (node.invoke.node as? Stmt)?.takeIf { it.containsInvokeExpr() }?.invokeExpr,
            false
        )
        outputZh += "离开被调用方法: `$call`"
        outputEn += "return from calling: `$call`"
        return this
    }

    fun write(s: String) {
        outputZh += s
        outputEn += s
    }

    private fun clean() {
        outputZh = StringBuffer()
        outputEn = StringBuffer()
    }

    fun calleeBeginPrint(invoke: InvokeEdgePath): EventPrinter {
        val container = invoke.container
        val call = "${container.declaringClass.name}#${container.name}"
        outputZh += "从 `$call` 进入调用"
        outputEn += "calling from: `$call`"
        return this
    }

    fun normalPrint(nodeI: IndexedValue<IPath>, unit: Unit, sootMethod: SootMethod): EventPrinter {
        val node = nodeI.value as IPath
        when {
            node is InvokeEdgePath -> {
                val var5 = SimpleUnitPrinter.Companion.getStringOf(
                    node.callee,
                    null,
                    (unit as? Stmt)?.takeIf { it.containsInvokeExpr() }?.invokeExpr,
                    false
                )
                outputZh += "进入被调用方法: `$var5`"
                outputEn += "calling: `$var5`"
            }
            node !is MergePath -> when (node) {
                is AssignLocalPath -> default(node, unit)
                is ModelBind -> printModelingBinding(node, unit, sootMethod)
                is LiteralPath -> {
                    val const = node.const
                    when (const) {
                        is IexConst -> when (const.type) {
                            IexConst.Type.SOURCE -> {
                                val invokeExpr = (unit as? Stmt)?.takeIf { it.containsInvokeExpr() }?.invokeExpr
                                if (invokeExpr == null || nodeI.index != 0) {
                                    clean()
                                    return this
                                }
                                outputZh += "污点源 Source 点: "
                                outputEn += "Taint source: "
                                write(SimpleUnitPrinter.Companion.getStringOf(null, null, invokeExpr, false))
                            }
                            IexConst.Type.SINK -> {
                                clean()
                                return this
                            }
                            else -> {
                                outputZh += "${const.type} 类型的常量: "
                                outputEn += "${const.type} type constant: "
                                write("`${const.const}`")
                            }
                        }
                        is Constant -> {
                            val ty = EventPrinterKt.getPname(const.type)
                            outputZh += "$ty 类型的常量: "
                            outputEn += "$ty type constant: "
                            write(const.toString())
                        }
                    }
                }
                is GetEdgePath -> default(node, unit)
                is SetEdgePath -> default(node, unit)
                else -> default(node, unit)
            }
        }
        return this
    }

    private fun printParameterNameByIndex(sootMethod: SootMethod, index: Int): Boolean {
        return when {
            index == -1 -> {
                val names = sootMethod.declaringClass.name
                outputZh += "$names类型的参数: this"
                outputEn += "$names type parameter: this"
                region = Region.Companion.getParamRegion(sootMethod, index)
                true
            }
            index in 0 until sootMethod.parameterCount -> {
                val nameTag = sootMethod.tags.filterIsInstance<ParamNamesTag>().firstOrNull()
                val names = nameTag?.names ?: emptyList()
                
                val ty = sootMethod.getParameterType(index)
                var paramName = names.getOrNull(index) ?: run {
                    try {
                        SootUtilsKt.getActiveBodyOrNull(sootMethod)?.getParameterLocal(index)?.let { 
                            "local var $it"
                        }
                    } catch (e: RuntimeException) {
                        null
                    } ?: ""
                }
                
                outputZh += "$ty类型的第${index + 1}个参数$paramName`"
                outputEn += "$ty type parameter${index + 1}$paramName}"
                region = Region.Companion.getParamRegion(sootMethod, index)
                true
            }
            else -> false
        }
    }
}