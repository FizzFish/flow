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
import soot.SootClass
import soot.SootField
import soot.tagkit.AbstractHost
import soot.tagkit.VisibilityAnnotationTag

class FieldCheckPoint(
    val sootField: SootField,
    private val infoCache: SootInfoCache
) : CheckPoint(), IFieldCheckPoint, SootInfoCache by infoCache {

    val visibilityAnnotationTag: VisibilityAnnotationTag?
        get() = sootField.getTag("VisibilityAnnotationTag") as? VisibilityAnnotationTag

    override val region: Region =
        Region.invoke(this, sootField as AbstractHost) ?: Region.getERROR()

    override val file: IBugResInfo = ClassResInfo(sootField.declaringClass)

    private val envDelegate by lazy(LazyThreadSafetyMode.PUBLICATION) {
        DefaultEnv(
            region.mutable,
            field = sootField,
            clazz = sootField.declaringClass
        )
    }

    override val env: DefaultEnv get() = envDelegate

    override val cache: AnalysisCache get() = infoCache.cache
    override val ext: SootHostExtend? get() = infoCache.getExt(this)
    override val hostKey: Key<SootHostExtend?> get() = infoCache.hostKey

    override val javaNameSourceEndColumnNumber: Int get() =
        infoCache.getJavaNameSourceEndColumnNumber(this)
    override val javaNameSourceEndLineNumber: Int get() =
        infoCache.getJavaNameSourceEndLineNumber(this)
    override val javaNameSourceStartColumnNumber: Int get() =
        infoCache.getJavaNameSourceStartColumnNumber(this)
    override val javaNameSourceStartLineNumber: Int get() =
        infoCache.getJavaNameSourceStartLineNumber(this)

    override fun SootClass.getMemberAtLine(ln: Int): BodyDeclaration<*>? =
        infoCache.getMemberAtLine(this, ln)
}