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
import kotlin.lazy
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.tagkit.AbstractHost

/**
 * A checkpoint bound to a single [SootClass].
 * Delegates everything that is not explicitly overridden to the provided [info] cache.
 */
class ClassCheckPoint(
    override val sootClass: SootClass,
    private val info: SootInfoCache
) : CheckPoint(),
    IClassCheckPoint,
    SootInfoCache by info {

    override val className: String get() = sootClass.name

    override val region: Region get() =
        Region(this, sootClass as AbstractHost) ?: Region.ERROR

    override val file: IBugResInfo = ClassResInfo(sootClass)

    private val _env by lazy(LazyThreadSafetyMode.PUBLICATION) {
        DefaultEnv(
            region.mutable,
            clazz = sootClass
        )
    }
    override val env: DefaultEnv get() = _env

    // -------------------------------------------------------------------------
    // Short‑hand delegation to the underlying [info] cache for frequently used
    // helpers and source‑position utilities.
    // -------------------------------------------------------------------------
    override val cache: AnalysisCache get() = info.cache
    override val ext: SootHostExtend? get() = info.getExt(this)
    override val hostKey: Key<SootHostExtend?> get() = info.hostKey

    override val javaNameSourceEndColumnNumber: Int get() = info.javaNameSourceEndColumnNumber
    override val javaNameSourceEndLineNumber: Int get() = info.javaNameSourceEndLineNumber
    override val javaNameSourceStartColumnNumber: Int get() = info.javaNameSourceStartColumnNumber
    override val javaNameSourceStartLineNumber: Int get() = info.javaNameSourceStartLineNumber

    // -------------------------------------------------------------------------
    // Iteration helpers
    // -------------------------------------------------------------------------
    override fun eachMethod(block: (IMethodCheckPoint) -> Unit) {
        sootClass.methods.forEach { block(MethodCheckPoint(it, info)) }
    }

    override fun eachField(block: (IFieldCheckPoint) -> Unit) {
        sootClass.fields.forEach { block(FieldCheckPoint(it, info)) }
    }

    // -------------------------------------------------------------------------
    // Convenience extension
    // -------------------------------------------------------------------------
    override fun SootClass.getMemberAtLine(ln: Int): BodyDeclaration<*>? =
        info.getMemberAtLine(this, ln)

    // Keep default implementations for super‑type discovery
    override fun getSuperClasses(): Sequence<SootClass> =
        IClassCheckPoint.DefaultImpls.getSuperClasses(this)

    override fun getSuperInterfaces(): Sequence<SootClass> =
        IClassCheckPoint.DefaultImpls.getSuperInterfaces(this)
}
