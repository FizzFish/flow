package cn.sast.framework.engine

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.IUnitCheckPoint
import com.feysh.corax.config.api.report.Region
import kotlin.jvm.internal.SourceDebugExtension
import soot.SootClass
import soot.SootMethod
import soot.Unit
import soot.Value
import soot.ValueBox
import soot.jimple.Expr
import soot.tagkit.Host
import kotlin.LazyThreadSafetyMode
import kotlin.lazy

@SourceDebugExtension(["SMAP\nPreAnalysisImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/UnitCheckPoint\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,760:1\n1863#2,2:761\n*S KotlinDebug\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/UnitCheckPoint\n*L\n662#1:761,2\n*E\n"])
public class UnitCheckPoint(info: SootInfoCache, unit: Unit, sootMethod: SootMethod) : CheckPoint, IUnitCheckPoint {
    public val info: SootInfoCache
    public open val unit: Unit
    public val sootMethod: SootMethod

    private val env$delegate by lazy(LazyThreadSafetyMode.PUBLICATION) { env_delegate$lambda$1(this) }

    public open val region: Region
        get() {
            var result = Region.Companion.invoke(info, unit as Host)
            return result ?: Region.Companion.ERROR
        }

    public open val file: IBugResInfo

    internal open val env: DefaultEnv
        internal get() = env$delegate

    init {
        this.info = info
        this.unit = unit
        this.sootMethod = sootMethod
        val declaringClass = sootMethod.declaringClass
        file = ClassResInfo(declaringClass)
    }

    public override fun eachExpr(block: (Expr) -> Unit) {
        val valueBoxes: Iterable<ValueBox> = TODO("FIXME - determine source of ValueBoxes")
        for (box in valueBoxes) {
            val value = box.value
            if (value is Expr) {
                block(value)
            }
        }
    }

    companion object {
        @JvmStatic
        private fun env_delegate$lambda$1(this$0: UnitCheckPoint): DefaultEnv {
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