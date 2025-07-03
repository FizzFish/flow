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
import kotlin.lazy.LazyKt
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.tagkit.AbstractHost

public class ClassCheckPoint(sootClass: SootClass, info: SootInfoCache) : CheckPoint(), IClassCheckPoint, SootInfoCache {
    public open val sootClass: SootClass
    public final val info: SootInfoCache

    public open val className: String
        get() = this.sootClass.name

    public open val region: Region
        get() = Region.Companion.invoke(this, this.sootClass as AbstractHost) ?: Region.Companion.getERROR()

    public open val file: IBugResInfo

    private val env$delegate: kotlin.Lazy<DefaultEnv>

    internal open val env: DefaultEnv
        internal get() = env$delegate.value

    public open val cache: AnalysisCache
        get() = TODO("FIXME - original cache implementation missing")

    public open val ext: SootHostExtend?
        get() = this.info.getExt(this)

    public open val hostKey: Key<SootHostExtend?>
        get() = TODO("FIXME - original hostKey implementation missing")

    public open val javaNameSourceEndColumnNumber: Int
        get() = this.info.getJavaNameSourceEndColumnNumber(this)

    public open val javaNameSourceEndLineNumber: Int
        get() = this.info.getJavaNameSourceEndLineNumber(this)

    public open val javaNameSourceStartColumnNumber: Int
        get() = this.info.getJavaNameSourceStartColumnNumber(this)

    public open val javaNameSourceStartLineNumber: Int
        get() = this.info.getJavaNameSourceStartLineNumber(this)

    init {
        this.sootClass = sootClass
        this.info = info
        this.file = ClassResInfo(this.sootClass)
        this.env$delegate = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION) { env_delegate$lambda$0(this) }
    }

    public override fun eachMethod(block: (IMethodCheckPoint) -> Unit) {
        for (method in this.sootClass.methods) {
            block(MethodCheckPoint(method, this.info))
        }
    }

    public override fun eachField(block: (IFieldCheckPoint) -> Unit) {
        for (field in this.sootClass.fields) {
            block(FieldCheckPoint(field, this.info))
        }
    }

    override fun getSuperClasses(): Sequence<SootClass> {
        return IClassCheckPoint.DefaultImpls.getSuperClasses(this)
    }

    override fun getSuperInterfaces(): Sequence<SootClass> {
        return IClassCheckPoint.DefaultImpls.getSuperInterfaces(this)
    }

    public override fun SootClass.getMemberAtLine(ln: Int): BodyDeclaration<*>? {
        return this.info.getMemberAtLine(this, ln)
    }

    companion object {
        @JvmStatic
        fun env_delegate$lambda$0(this$0: ClassCheckPoint): DefaultEnv {
            return DefaultEnv(
                this$0.region.mutable,
                null, null, null, null, null,
                this$0.sootClass,
                null, null,
                446,
                null
            )
        }
    }
}