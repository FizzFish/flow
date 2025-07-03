package cn.sast.framework.engine

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.IInvokeCheckPoint
import com.feysh.corax.config.api.report.Region
import kotlin.jvm.internal.SourceDebugExtension
import soot.SootClass
import soot.SootMethod
import soot.SootMethodRef
import soot.Type
import soot.Unit
import soot.jimple.InvokeExpr
import soot.tagkit.AbstractHost
import kotlin.LazyThreadSafetyMode
import kotlin.lazy

@SourceDebugExtension(["SMAP\nPreAnalysisImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/InvokeCheckPoint\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,760:1\n1#2:761\n*E\n"])
class InvokeCheckPoint(
    info: SootInfoCache,
    container: SootMethod,
    callSite: Unit?,
    declaredReceiverType: Type?,
    invokeMethodRef: SootMethodRef?,
    callee: SootMethod,
    invokeExpr: InvokeExpr?
) : CheckPoint, IInvokeCheckPoint {
    val info: SootInfoCache
    override val container: SootMethod
    override val callSite: Unit?
    override val declaredReceiverType: Type?
    override val invokeMethodRef: SootMethodRef?
    override val callee: SootMethod
    override val invokeExpr: InvokeExpr?

    override val region: Region
        get() {
            val callSite = this.callSite
            if (callSite != null) {
                val region = Region.Companion.invoke(callSite)
                if (region != null) {
                    return region
                }
            }

            var fallbackRegion = Region.Companion.invoke(info, container as AbstractHost)
            if (fallbackRegion == null) {
                fallbackRegion = Region.Companion.getERROR()
            }

            return fallbackRegion
        }

    override val file: IBugResInfo
        get() = file$delegate.value as IBugResInfo

    internal val env: DefaultEnv
        get() = env$delegate.value as DefaultEnv

    private val file$delegate = lazy(LazyThreadSafetyMode.PUBLICATION) { file_delegate$lambda$1(this) }
    private val env$delegate = lazy(LazyThreadSafetyMode.PUBLICATION) { env_delegate$lambda$2(this) }

    init {
        this.info = info
        this.container = container
        this.callSite = callSite
        this.declaredReceiverType = declaredReceiverType
        this.invokeMethodRef = invokeMethodRef
        this.callee = callee
        this.invokeExpr = invokeExpr
    }

    override fun toString(): String {
        val callSite = this.callSite
        return "${container}: at ${callSite?.javaSourceStartLineNumber} : $callSite -> $invokeMethodRef -> $callee"
    }

    companion object {
        @JvmStatic
        private fun file_delegate$lambda$1(this$0: InvokeCheckPoint): ClassResInfo {
            val declaringClass = this$0.container.declaringClass
            return ClassResInfo(declaringClass)
        }

        @JvmStatic
        private fun env_delegate$lambda$2(this$0: InvokeCheckPoint): DefaultEnv {
            return DefaultEnv(
                this$0.region.mutable,
                null,
                this$0.callSite,
                this$0.callee,
                this$0.container,
                this$0.invokeExpr,
                null,
                null,
                null,
                450,
                null
            )
        }
    }
}