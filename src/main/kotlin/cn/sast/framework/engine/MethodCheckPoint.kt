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
import kotlin.LazyThreadSafetyMode
import kotlin.lazy.LazyKt
import soot.SootClass
import soot.SootMethod
import soot.tagkit.AbstractHost
import soot.tagkit.VisibilityAnnotationTag

public class MethodCheckPoint(
    public open val sootMethod: SootMethod,
    public val info: SootInfoCache
) : CheckPoint(), IMethodCheckPoint, SootInfoCache {
    private val env$delegate by lazy(LazyThreadSafetyMode.PUBLICATION) {
        env_delegate$lambda$0(this)
    }

    public open val visibilityAnnotationTag: VisibilityAnnotationTag?
        get() = sootMethod.getTag("VisibilityAnnotationTag") as? VisibilityAnnotationTag

    public open val region: Region
        get() = Region.Companion.invoke(this, sootMethod as AbstractHost) ?: Region.Companion.getERROR()

    public open val file: IBugResInfo = ClassResInfo(sootMethod.declaringClass)

    internal open val env: DefaultEnv
        get() = env$delegate

    public open val cache: AnalysisCache
        get() = TODO("FIXME — original code didn't show implementation")

    public open val ext: SootHostExtend?
        get() = info.getExt(this)

    public open val hostKey: Key<SootHostExtend?>
        get() = TODO("FIXME — original code didn't show implementation")

    public open val javaNameSourceEndColumnNumber: Int
        get() = info.getJavaNameSourceEndColumnNumber(this)

    public open val javaNameSourceEndLineNumber: Int
        get() = info.getJavaNameSourceEndLineNumber(this)

    public open val javaNameSourceStartColumnNumber: Int
        get() = info.getJavaNameSourceStartColumnNumber(this)

    public open val javaNameSourceStartLineNumber: Int
        get() = info.getJavaNameSourceStartLineNumber(this)

    public override fun eachUnit(block: (IUnitCheckPoint) -> Unit) {
        if (sootMethod.hasActiveBody()) {
            sootMethod.activeBody.units.forEach { unit ->
                block(UnitCheckPoint(info, unit, sootMethod))
            }
        }
    }

    public override fun SootClass.getMemberAtLine(ln: Int): BodyDeclaration<*>? {
        return info.getMemberAtLine(this, ln)
    }

    companion object {
        @JvmStatic
        fun env_delegate$lambda$0(this$0: MethodCheckPoint): DefaultEnv {
            return DefaultEnv(
                this$0.region.mutable,
                null,
                null,
                null,
                this$0.sootMethod,
                null,
                this$0.sootMethod.declaringClass,
                null,
                this$0.sootMethod,
                174,
                null
            )
        }
    }
}