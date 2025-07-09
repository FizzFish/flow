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

/**
 * A checkpoint bound to a single [SootField].
 */
class FieldCheckPoint(
    override val sootField: SootField,
    private val info: SootInfoCache
) : CheckPoint(),
    IFieldCheckPoint,
    SootInfoCache by info {

    override val visibilityAnnotationTag: VisibilityAnnotationTag?
        get() = sootField.getTag("VisibilityAnnotationTag") as? VisibilityAnnotationTag

    override val region: Region get() =
        Region(this, sootField as AbstractHost) ?: Region.ERROR

    override val file: IBugResInfo = ClassResInfo(sootField.declaringClass)

    private val _env by lazy(LazyThreadSafetyMode.PUBLICATION) {
        DefaultEnv(
            region.mutable,
            clazz = sootField.declaringClass,
            field = sootField
        )
    }
    override val env: DefaultEnv get() = _env

    // Delegations to the backing cache
    override val cache: AnalysisCache get() = info.cache
    override val ext: SootHostExtend? get() = info.getExt(this)
    override val hostKey: Key<SootHostExtend?> get() = info.hostKey

    override val javaNameSourceEndColumnNumber: Int get() = info.javaNameSourceEndColumnNumber
    override val javaNameSourceEndLineNumber: Int get() = info.javaNameSourceEndLineNumber
    override val javaNameSourceStartColumnNumber: Int get() = info.javaNameSourceStartColumnNumber
    override val javaNameSourceStartLineNumber: Int get() = info.javaNameSourceStartLineNumber

    override fun SootClass.getMemberAtLine(ln: Int): BodyDeclaration<*>? =
        info.getMemberAtLine(this, ln)
}
