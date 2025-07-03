package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl
import com.feysh.corax.config.api.utils.UtilsKt
import kotlin.reflect.KCallable
import mu.KLogger
import mu.KotlinLogging

public class Debug : SummaryHandlePackage<IValue> {
    public override fun ACheckCallAnalysis.register() {
        val logger: KLogger = KotlinLogging.logger { "Debug.register" }
        this.evalCallAtCaller(UtilsKt.getSootSignature(TODO("FIXME — unrepresentable instance") as KCallable<*>), ::register$lambda$2)
        this.evalCallAtCaller(UtilsKt.getSootSignature(TODO("FIXME — unrepresentable instance") as KCallable<*>), ::register$lambda$4)
    }

    @JvmStatic
    fun register$lambda$0() {
    }

    @JvmStatic
    fun register$lambda$2$lambda$1(): Any {
        return "debug break"
    }

    @JvmStatic
    fun register$lambda$2(logger: KLogger, this$evalCallAtCaller: CallerSiteCBImpl.EvalCall) {
        logger.debug { register$lambda$2$lambda$1() }
    }

    @JvmStatic
    fun register$lambda$4$lambda$3(res: IHeapValues): Any {
        return "debug print($res)"
    }

    @JvmStatic
    fun register$lambda$4(logger: KLogger, this$evalCallAtCaller: CallerSiteCBImpl.EvalCall) {
        logger.debug { register$lambda$4$lambda$3(TODO("FIXME — missing res parameter")) }
    }

    public companion object {
        public fun v(): Debug {
            return Debug()
        }
    }
}