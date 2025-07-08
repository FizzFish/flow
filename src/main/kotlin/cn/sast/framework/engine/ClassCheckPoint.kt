package cn.sast.framework.engine

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.cache.AnalysisDataFactory.Key
import com.feysh.corax.cache.analysis.SootHostExtend
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.IClassCheckPoint
import com.feysh.corax.config.api.IFieldCheckPoint
import com.feysh.corax.config.api.IMethodCheckPoint
import com.feysh.corax.config.api.report.Region
import com.github.javaparser.ast.body.BodyDeclaration
import kotlin.LazyThreadSafetyMode
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.tagkit.AbstractHost

class ClassCheckPoint(
    val sootClass: SootClass,
    private val infoCache: SootInfoCache
) : CheckPoint(), IClassCheckPoint, SootInfoCache by infoCache {

    override val className: String get() = sootClass.name

    override val region: Region =
        Region.invoke(this, sootClass as AbstractHost) ?: Region.getERROR()

    override val file: IBugResInfo = ClassResInfo(sootClass)

    private val envDelegate by lazy(LazyThreadSafetyMode.PUBLICATION) {
        DefaultEnv(
            region.mutable,
            clazz = sootClass
        )
    }

    override val env: DefaultEnv get() = envDelegate
    // ---------------------------------------------------------------------
    //  Delegated helpers to information cache
    // ---------------------------------------------------------------------

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

    // ---------------------------------------------------------------------
    //  Child checkpoints
    // ---------------------------------------------------------------------

    override fun eachMethod(block: (IMethodCheckPoint) -> Unit) =
        sootClass.methods.forEach { block(MethodCheckPoint(it, infoCache)) }

    override fun eachField(block: (IFieldCheckPoint) -> Unit) =
        sootClass.fields.forEach { block(FieldCheckPoint(it, infoCache)) }

    // ---------------------------------------------------------------------

    override fun getSuperClasses(): Sequence<SootClass> =
        IClassCheckPoint.DefaultImpls.getSuperClasses(this)

    override fun getSuperInterfaces(): Sequence<SootClass> =
        IClassCheckPoint.DefaultImpls.getSuperInterfaces(this)

    override fun SootClass.getMemberAtLine(ln: Int): BodyDeclaration<*>? =
        infoCache.getMemberAtLine(this, ln)
}
