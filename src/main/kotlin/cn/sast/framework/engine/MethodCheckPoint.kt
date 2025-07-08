package cn.sast.framework.engine

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.cache.AnalysisDataFactory.Key
import com.feysh.corax.cache.analysis.SootHostExtend
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.IMethodCheckPoint
import com.feysh.corax.config.api.IUnitCheckPoint
import com.feysh.corax.config.api.report.Region
import com.github.javaparser.ast.body.BodyDeclaration
import kotlinx.coroutines.sync.Mutex
import soot.SootClass
import soot.SootMethod
import soot.tagkit.AbstractHost
import soot.tagkit.VisibilityAnnotationTag

/**
 * [IMethodCheckPoint] 具体实现，包装单个 [SootMethod] 及其元信息。
 */
class MethodCheckPoint(
    val sootMethod: SootMethod,
    private val info: SootInfoCache,
) : CheckPoint(), IMethodCheckPoint, SootInfoCache by info {

    /* 懒加载 env，线程安全 */
    private val envDelegate by lazy(LazyThreadSafetyMode.PUBLICATION) { createEnv() }

    // region 公开属性 ------------------------------------------------------

    val visibilityAnnotationTag: VisibilityAnnotationTag?
        get() = sootMethod.getTag("VisibilityAnnotationTag") as? VisibilityAnnotationTag

    override val region: Region =
        Region(sootMethod as AbstractHost) ?: Region.ERROR

    override val file: IBugResInfo = ClassResInfo(sootMethod.declaringClass)

    /** 默认返回全局 [AnalysisCache]，可按需求覆盖 */
    val cache: AnalysisCache
        get() = TODO("Provide project-wide AnalysisCache implementation")

    val ext: SootHostExtend?
        get() = info.getExt(this)

    val hostKey: Key<SootHostExtend?>
        get() = TODO("Define AnalysisDataFactory.Key for SootHostExtend")

    override val javaNameSourceEndColumnNumber: Int
        get() = info.getJavaNameSourceEndColumnNumber(this)

    override val javaNameSourceEndLineNumber: Int
        get() = info.getJavaNameSourceEndLineNumber(this)

    override val javaNameSourceStartColumnNumber: Int
        get() = info.getJavaNameSourceStartColumnNumber(this)

    override val javaNameSourceStartLineNumber: Int
        get() = info.getJavaNameSourceStartLineNumber(this)

    /* ------------------------------------------------------------------ */

    override fun eachUnit(block: (IUnitCheckPoint) -> Unit) {
        if (!sootMethod.hasActiveBody()) return
        sootMethod.activeBody.units.forEach { u ->
            block(UnitCheckPoint(info, u, sootMethod))
        }
    }

    override fun SootClass.getMemberAtLine(ln: Int): BodyDeclaration<*>? =
        info.getMemberAtLine(this, ln)

    /* --------------------------- helpers ------------------------------ */

    private fun createEnv(): DefaultEnv = DefaultEnv(
        region = region.mutable,
        fileName = null,
        source = null,
        sink = null,
        method = sootMethod,
        value = null,
        clazz = sootMethod.declaringClass,
        field = null,
        element = sootMethod,
    )

    /* Mutex 可用于并发控制需写操作的扩展逻辑 */
    private val mutex = Mutex()

    /* ... 其它实用扩展 ... */
}
