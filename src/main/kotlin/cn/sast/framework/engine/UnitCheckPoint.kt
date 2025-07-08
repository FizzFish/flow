package cn.sast.framework.engine

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.IUnitCheckPoint
import com.feysh.corax.config.api.report.Region
import mu.KotlinLogging
import soot.SootMethod
import soot.Unit
import soot.ValueBox
import soot.jimple.Expr
import soot.tagkit.Host
import kotlin.LazyThreadSafetyMode

/**
 * A checkpoint that represents a single Jimple [Unit] in a method.
 */
class UnitCheckPoint(
    val info: SootInfoCache,
    val unit: Unit,
    val sootMethod: SootMethod
) : CheckPoint, IUnitCheckPoint {

    private val envDelegate: DefaultEnv by lazy(LazyThreadSafetyMode.PUBLICATION) { buildEnv() }

    /** Region that encloses this statement. */
    val region: Region
        get() = Region.invoke(info, unit as Host) ?: Region.ERROR

    override val file: IBugResInfo = ClassResInfo(sootMethod.declaringClass)

    internal val env: DefaultEnv
        get() = envDelegate

    override fun eachExpr(block: (Expr) -> Unit) {
        // Combine use/def boxes to visit every value produced or consumed by this Unit.
        val boxes: List<ValueBox> = unit.useBoxes + unit.defBoxes
        for (box in boxes) {
            (box.value as? Expr)?.let(block)
        }
    }

    private fun buildEnv(): DefaultEnv = DefaultEnv(
        region.mutable,
        null,
        null,
        null,
        sootMethod,
        null,
        sootMethod.declaringClass,
        null,
        sootMethod
    )

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
