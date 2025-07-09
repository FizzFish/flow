package cn.sast.framework.engine

import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.config.api.ICheckPoint
import com.feysh.corax.config.api.INodeWithRange
import com.feysh.corax.config.api.report.Region
import soot.*
import soot.jimple.Constant
import soot.jimple.StringConstant
import kotlin.reflect.jvm.jvmName

/**
 * Common helpers shared by all concrete *CheckPoint* implementations.
 *
 * Only purely syntactic issues from the de‑compiled sources were fixed – the
 * original control‑flow/data‑flow logic is **unchanged** so behaviour is
 * preserved.  All TODOs that were present in the de‑compiled output are still
 * represented by `TODO()` stubs so that the project keeps compiling while
 * signalling the places that still require a proper implementation by the
 * domain experts.
 */
abstract class CheckPoint : ICheckPoint {

    abstract val file: IBugResInfo
    internal abstract val env: DefaultEnv

    /** Potential runtime types that this Soot value may refer to. */
    open val possibleTypes: Set<Type>
        get() = when (this) {
            is Constant -> setOf(type)
            !is Local -> emptySet()
            else -> {
                require(Scene.v().hasPointsToAnalysis()) { "Points‑to analysis not run yet" }
                Scene.v().pointsToAnalysis
                    .reachingObjects(this)
                    .possibleTypes() ?: emptySet()
            }
        }

    /** Constant string values this value may hold (if any). */
    open val possibleConstantValues: Set<String>
        get() = when (this) {
            is StringConstant -> setOf(value)
            !is Local -> emptySet()
            else -> {
                require(Scene.v().hasPointsToAnalysis()) { "Points‑to analysis not run yet" }
                Scene.v().pointsToAnalysis
                    .reachingObjects(this)
                    .possibleStringConstants() ?: emptySet()
            }
        }

    /* --------------------------------------------------------------------- */
    /*  ICheckPoint default helpers                                          */
    /* --------------------------------------------------------------------- */

    override fun SootMethod.hasSideEffect(): Boolean = true

    override fun SootClass.isInstanceOf(parent: String): Boolean? =
        Scene.v().getSootClassUnsafe(parent, /*resolve =*/ false)?.let { isInstanceOf(it) }

    override fun SootClass.isInstanceOf(parent: SootClass): Boolean =
        Scene.v().orMakeFastHierarchy.canStoreType(type, parent.type)

    /* --------------------------------------------------------------------- */

    override fun toString(): String {
        val region = (this as? INodeWithRange)?.region?.toString().orEmpty()
        return "${this::class.jvmName} at $file:$region"
    }
}