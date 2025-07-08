package cn.sast.framework.engine

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.IInvokeCheckPoint
import com.feysh.corax.config.api.report.Region
import soot.*
import soot.jimple.InvokeExpr
import kotlin.LazyThreadSafetyMode

class InvokeCheckPoint(
    private val infoCache: SootInfoCache,
    override val container: SootMethod,
    override val callSite: Unit?,
    override val declaredReceiverType: Type?,
    override val invokeMethodRef: SootMethodRef?,
    override val callee: SootMethod,
    override val invokeExpr: InvokeExpr?
) : CheckPoint(), IInvokeCheckPoint {

    override val region: Region =
        callSite?.let { Region.invoke(it) }
            ?: Region.invoke(infoCache, container as AbstractHost) ?: Region.getERROR()

    private val fileDelegate by lazy(LazyThreadSafetyMode.PUBLICATION) {
        ClassResInfo(container.declaringClass)
    }

    override val file: IBugResInfo get() = fileDelegate

    private val envDelegate by lazy(LazyThreadSafetyMode.PUBLICATION) {
        DefaultEnv(
            region.mutable,
            stmt = callSite,
            callee = callee,
            method = container,
            invokeExpr = invokeExpr
        )
    }

    internal override val env: DefaultEnv get() = envDelegate

    override fun toString(): String =
        "$container : at ${callSite?.javaSourceStartLineNumber} : $callSite -> $invokeMethodRef -> $callee"
}
