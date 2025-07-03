package cn.sast.dataflow.interprocedural.check.printer

import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.dataflow.interprocedural.check.ExitInvoke
import cn.sast.dataflow.interprocedural.check.IPath
import cn.sast.dataflow.interprocedural.check.InvokeEdgePath
import cn.sast.dataflow.interprocedural.check.ModelBind
import cn.sast.idfa.analysis.InterproceduralCFG
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KClass
import kotlinx.collections.immutable.PersistentList
import soot.Local
import soot.SootClass
import soot.SootMethod
import soot.SootMethodRef
import soot.Unit
import soot.Value
import soot.jimple.AssignStmt
import soot.jimple.InvokeExpr
import soot.jimple.Stmt
import soot.tagkit.AbstractHost
import soot.tagkit.Host

@SourceDebugExtension(["SMAP\nPathDiagnosticsGenerator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathDiagnosticsGenerator.kt\ncn/sast/dataflow/interprocedural/check/printer/PathDiagnosticsGenerator\n+ 2 SootUtils.kt\ncn/sast/api/util/SootUtilsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Region.kt\ncom/feysh/corax/config/api/report/Region\n*L\n1#1,211:1\n303#2:212\n1#3:213\n1#3:214\n1#3:216\n59#4:215\n57#4:217\n*S KotlinDebug\n*F\n+ 1 PathDiagnosticsGenerator.kt\ncn/sast/dataflow/interprocedural/check/printer/PathDiagnosticsGenerator\n*L\n40#1:212\n40#1:213\n85#1:216\n85#1:215\n85#1:217\n*E\n"])
class PathDiagnosticsGenerator(info: SootInfoCache?, icfg: InterproceduralCFG, deep: Int) {
    val info: SootInfoCache?
    val icfg: InterproceduralCFG
    val deep: Int
    private val result: ArrayDeque<BugPathEvent> = ArrayDeque()
    private val prefix: String = "[$deep]"
    private val calleePrefix: String = "[${deep + 1}]"

    init {
        this.info = info
        this.icfg = icfg
        this.deep = deep
    }

    internal fun emit(node: IPath, message: EventPrinter): List<BugPathEvent> {
        return emit(node.getNode(), message.getMessage(), message.getRegion())
    }

    fun emit(node: Unit, message: Map<Language, String>?, region: Region?): List<BugPathEvent> {
        if (message == null) {
            return emptyList()
        } else {
            val clazz: SootClass = icfg.getMethodOf(node).declaringClass
            val var10000: InvokeExpr? = (node as? Stmt)?.takeIf { it.containsInvokeExpr() }?.invokeExpr
            val methodRef: SootMethodRef? = var10000?.methodRef
            
            if (methodRef != null
                && (methodRef !is SootMethod || (methodRef as SootMethod).isDeclared)
                && methodRef.declaringClass.name == "java.lang.String") {
                val var18: String = methodRef.name
                if (var18.contains("equals", ignoreCase = true)) {
                    return emptyList()
                }
            }

            if (clazz.isJavaLibraryClass) {
                return emptyList()
            } else {
                var var19: Map<Language, String> = message
                val var10001 = ClassResInfo.Companion
                val var20: IBugResInfo = var10001.of(clazz)
                var var10002: Region? = region
                if (region == null) {
                    if (info != null) {
                        val var12: Region = Region.Companion.invoke(info, node as Host)
                        var19 = message
                        var10002 = var12
                    } else {
                        var10002 = null
                    }

                    if (var10002 == null) {
                        var10002 = Region.Companion.invoke(node)
                            ?: Region.Companion.ERROR
                    }
                }

                return listOf(BugPathEvent(var19, var20, var10002, deep))
            }
        }
    }

    internal fun emit(method: SootMethod, message: EventPrinter?): List<BugPathEvent> {
        return if (message == null) emptyList() else emit(method, message.getMessage(), message.getRegion())
    }

    fun emit(method: SootMethod, message: Map<Language, String>?, region: Region?): List<BugPathEvent> {
        if (message == null) {
            return emptyList()
        } else {
            val clazz: SootClass = method.declaringClass
            if (method.isDeclared && method.declaringClass.name == "java.lang.String") {
                val var10000: String = method.name
                if (var10000.contains("equals", ignoreCase = true)) {
                    return emptyList()
                }
            }

            if (clazz.isJavaLibraryClass) {
                return emptyList()
            } else {
                var var20: Map<Language, String> = message
                val var10001 = ClassResInfo.Companion
                val var21: IBugResInfo = var10001.of(clazz)
                var var10002: Region? = region
                if (region == null) {
                    if (info != null) {
                        val var14: Region = Region.Companion.invoke(info, method as AbstractHost)
                        var20 = message
                        var10002 = var14
                    } else {
                        var10002 = null
                    }

                    if (var10002 == null) {
                        val `this_$iv` = Region(method.javaSourceStartLineNumber, method.javaSourceStartColumnNumber, -1, -1)
                        var10002 = if (`this_$iv`.startLine >= 0) `this_$iv` else null
                        if (var10002 == null) {
                            var10002 = Region.Companion.ERROR
                        }
                    }
                }

                return listOf(BugPathEvent(var20, var21, var10002, deep))
            }
        }
    }

    fun inlineAssignment(pathEvents: List<IndexedValue<IPath>>, index: Int, inlined: MutableSet<Int>, type: KClass<out IPath>) {
        val pathEvent: IPath = pathEvents[index].value
        return object : GrimpInline(index, pathEvents, pathEvent, type, inlined) {
            private var visited: MutableSet<Value> = LinkedHashSet()

            override fun newExpr(value: Value): Value {
                var result: Value? = null
                if (!visited.add(value)) {
                    return value
                } else {
                    if (value is Local) {
                        var reverseIndex: Int = this.index

                        while (--reverseIndex >= 0) {
                            val defUnitPathNode: IPath = pathEvents[reverseIndex].value
                            val defUnit: Unit = defUnitPathNode.getNode()
                            if (defUnit != pathEvent.getNode()
                                && type.isInstance(defUnitPathNode)
                                && defUnit is AssignStmt
                                && defUnit.javaSourceStartLineNumber == pathEvent.getNode().javaSourceStartLineNumber
                                && defUnit.javaSourceStartLineNumber > 0) {
                                val var10000: Value = defUnit.leftOp
                                if (var10000 as Local == value) {
                                    result = defUnit.rightOp
                                    inlined.add(reverseIndex)
                                    break
                                }
                            }
                        }
                    }

                    return if (result != null) super.newExpr(result) else super.newExpr(value)
                }
            }
        }.inline(pathEvent.getNode())
    }

    private fun inlinePathEvents(pathEvents: List<IndexedValue<IPath>>, index: Int, inlined: MutableSet<Int>): List<BugPathEvent> {
        val pathEventI = pathEvents[index]
        val pathEvent = pathEventI.value
        val lineUnit = inlineAssignment(pathEvents, index, inlined, pathEvent::class)
        val sootMethod = icfg.getMethodOf(pathEvent.getNode())
        
        return if (pathEvent is ModelBind) {
            emit(pathEvent, EventPrinter(prefix).printModeling(pathEvent, lineUnit, sootMethod))
        } else {
            val n = emit(pathEvent, EventPrinter(prefix).normalPrint(pathEventI, lineUnit, sootMethod))
            if (pathEvent is InvokeEdgePath) {
                n + emit(pathEvent.callee, EventPrinter(calleePrefix).calleeBeginPrint(pathEvent)
            } else {
                n
            }
        }
    }

    fun inlineBugPathEvents(pathEvents: List<BugPathEvent>) {
        result.addAll(pathEvents)
    }

    fun inlinePathEvents(pathEvents: List<IndexedValue<IPath>>) {
        val inlined = LinkedHashSet<Int>()
        val insertIndex = result.size
        var index = pathEvents.size

        while (--index >= 0) {
            if (!inlined.contains(index)) {
                result.addAll(insertIndex, inlinePathEvents(pathEvents, index, inlined))
            }
        }
    }

    fun inlineEvents(pathEvents: PersistentList<Any>): List<BugPathEvent> {
        val curEvents = ArrayList<BugPathEvent>()
        val curPathEvents = ArrayList<IndexedValue<IPath>>()
        var i = 0

        for (event in pathEvents) {
            when (event) {
                is BugPathEvent -> {
                    if (curPathEvents.isNotEmpty()) {
                        inlinePathEvents(curPathEvents)
                        curPathEvents.clear()
                    }
                    curEvents.add(event)
                }
                is IPath -> {
                    if (curEvents.isNotEmpty()) {
                        inlineBugPathEvents(curEvents)
                        curEvents.clear()
                    }
                    curPathEvents.add(IndexedValue(i, event))
                }
                is ExitInvoke -> {
                    if (curPathEvents.isNotEmpty()) {
                        inlinePathEvents(curPathEvents)
                        curPathEvents.clear()
                    }
                    if (curEvents.isNotEmpty()) {
                        inlineBugPathEvents(curEvents)
                        curEvents.clear()
                    }
                    result.addAll(emit(event.invoke, EventPrinter(prefix).normalPrint(event)))
                }
                else -> throw IllegalStateException("internal error")
            }
            i++
        }

        if (curEvents.isNotEmpty()) {
            inlineBugPathEvents(curEvents)
        }

        if (curPathEvents.isNotEmpty()) {
            inlinePathEvents(curPathEvents)
        }

        return result.toList()
    }
}