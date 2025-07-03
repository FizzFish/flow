package cn.sast.framework.result

import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.Report
import cn.sast.dataflow.infoflow.provider.BugTypeProvider
import cn.sast.framework.engine.PreAnalysisReportEnv
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import soot.Scene
import soot.SootMethod
import soot.Unit
import soot.jimple.Stmt
import soot.jimple.infoflow.data.AccessPath
import soot.jimple.infoflow.results.DataFlowResult
import soot.jimple.infoflow.results.ResultSinkInfo
import soot.jimple.infoflow.results.ResultSourceInfo
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinition
import soot.jimple.infoflow.sourcesSinks.definitions.MethodSourceSinkDefinition
import soot.tagkit.AbstractHost

@SourceDebugExtension(["SMAP\nResultConverter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResultConverter.kt\ncn/sast/framework/result/ResultConverter\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,110:1\n1557#2:111\n1628#2,2:112\n1630#2:115\n1557#2:116\n1628#2,3:117\n1#3:114\n*S KotlinDebug\n*F\n+ 1 ResultConverter.kt\ncn/sast/framework/result/ResultConverter\n*L\n78#1:111\n78#1:112,2\n78#1:115\n106#1:116\n106#1:117,3\n*E\n"])
class ResultConverter(info: SootInfoCache?) {
    val info: SootInfoCache? = info

    private fun writeAccessPath(accessPath: AccessPath, simple: Boolean = false): String {
        return if (!simple) {
            accessPath.toString()
        } else {
            val b = StringBuilder()
            if (accessPath.plainValue != null) {
                b.append(accessPath.plainValue.toString())
            }

            if (accessPath.baseType != null) {
                b.append(accessPath.baseType.toString())
            }

            if (accessPath.fragmentCount > 0) {
                for (i in 0 until accessPath.fragmentCount) {
                    b.append(".").append(accessPath.fragments[i].field.toString())
                }
            }

            b.append(if (accessPath.taintSubFields) "*" else "")
            b.toString()
        }
    }

    fun getReport(checkType: CheckType, env: PreAnalysisReportEnv): Report {
        return Report.Companion.of(
            info,
            env.file,
            env.env.region.immutable,
            checkType,
            env.env,
            null,
            32,
            null
        )
    }

    fun getReport(
        icfg: IInfoflowCFG,
        result: DataFlowResult,
        bugTypeProvider: BugTypeProvider,
        serializeTaintPath: Boolean = true
    ): List<Report> {
        val sink = result.sink
        val stmt = sink.stmt
        val method = icfg.getMethodOf(sink.stmt) as SootMethod
        val definition = sink.definition
        if (definition !is MethodSourceSinkDefinition) {
            logger.warn { "Definition: $definition is not a MethodSourceSinkDefinition." }
            return emptyList()
        }

        val sinkMethod = Scene.v().grabMethod((definition as MethodSourceSinkDefinition).method.signature)
            ?: run {
                logger.warn { "Soot can not find method: $definition" }
                return emptyList()
            }

        val source = result.source
        val events = if (serializeTaintPath && source.path != null) {
            source.path.mapIndexed { index, pathStmt ->
                val type = icfg.getMethodOf(pathStmt) as SootMethod
                val ap = writeAccessPath(source.pathAccessPaths[index], false)
                var region = Region(pathStmt as Unit)
                    ?: info?.let { Region(it, type as AbstractHost) }
                    ?: Region.ERROR

                val message = mapOf(Language.EN to "`$ap` is tainted after `$pathStmt`")
                BugPathEvent(message, ClassResInfo.of(type), region, null, 8, null)
            }
        } else {
            emptyList()
        }

        val checkTypes = bugTypeProvider.lookUpCheckType(sinkMethod)
        if (checkTypes.isEmpty()) {
            logger.warn { "could not find any checkTypes from bugTypeProvider at sink method: $sinkMethod" }
        }

        var region = Region(stmt as Unit)
            ?: info?.let { Region(it, method as AbstractHost) }
            ?: Region.ERROR

        val env = DefaultEnv(
            region.mutable,
            null, null, null, null, null, null, null, null, 510, null
        ).apply {
            callSite = stmt as Unit
            clazz = method.declaringClass
            container = method
            callee = sinkMethod
            method = sinkMethod
            invokeExpr = if (stmt.containsInvokeExpr()) stmt.invokeExpr else null
        }

        return checkTypes.map { checkType ->
            Report.of(
                info,
                ClassResInfo.of(method),
                region,
                checkType,
                env,
                events
            )
        }.toMutableList()
    }

    companion object {
        private val logger: KLogger = TODO("Initialize logger properly")
    }
}