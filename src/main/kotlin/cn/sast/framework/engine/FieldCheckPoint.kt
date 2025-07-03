package cn.sast.framework.engine

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.cache.AnalysisDataFactory.Key
import com.feysh.corax.cache.analysis.SootHostExtend
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.IFieldCheckPoint
import com.feysh.corax.config.api.report.Region
import com.github.javaparser.ast.body.BodyDeclaration
import kotlin.LazyThreadSafetyMode
import kotlin.lazy
import soot.SootClass
import soot.SootField
import soot.tagkit.AbstractHost
import soot.tagkit.VisibilityAnnotationTag

public class FieldCheckPoint(
    public open val sootField: SootField,
    public val info: SootInfoCache
) : CheckPoint, IFieldCheckPoint, SootInfoCache {

    public open val visibilityAnnotationTag: VisibilityAnnotationTag?
        get() = sootField.getTag("VisibilityAnnotationTag") as? VisibilityAnnotationTag

    public open val region: Region
        get() = Region.Companion.invoke(this, sootField as AbstractHost) ?: Region.Companion.getERROR()

    public open val file: IBugResInfo

    private val env$delegate by lazy(LazyThreadSafetyMode.PUBLICATION) {
        env_delegate$lambda$0(this)
    }

    internal open val env: DefaultEnv
        get() = env$delegate

    public open val cache: AnalysisCache
        get() = TODO("FIXME — cache property implementation missing")

    public open val ext: SootHostExtend?
        get() = info.getExt(this)

    public open val hostKey: Key<SootHostExtend?>
        get() = TODO("FIXME — hostKey property implementation missing")

    public open val javaNameSourceEndColumnNumber: Int
        get() = info.getJavaNameSourceEndColumnNumber(this)

    public open val javaNameSourceEndLineNumber: Int
        get() = info.getJavaNameSourceEndLineNumber(this)

    public open val javaNameSourceStartColumnNumber: Int
        get() = info.getJavaNameSourceStartColumnNumber(this)

    public open val javaNameSourceStartLineNumber: Int
        get() = info.getJavaNameSourceStartLineNumber(this)

    init {
        file = ClassResInfo(sootField.declaringClass)
    }

    public override fun SootClass.getMemberAtLine(ln: Int): BodyDeclaration<*>? {
        return info.getMemberAtLine(this, ln)
    }

    companion object {
        @JvmStatic
        private fun env_delegate$lambda$0(this$0: FieldCheckPoint): DefaultEnv {
            return DefaultEnv(
                this$0.region.mutable,
                null,
                null,
                null,
                null,
                null,
                this$0.sootField.declaringClass,
                this$0.sootField,
                null,
                318,
                null
            )
        }
    }
}