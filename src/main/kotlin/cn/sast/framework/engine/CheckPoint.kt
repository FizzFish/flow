package cn.sast.framework.engine

import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.config.api.ICheckPoint
import com.feysh.corax.config.api.INodeWithRange
import com.feysh.corax.config.api.report.Region
import soot.Local
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.Type
import soot.jimple.Constant
import soot.jimple.StringConstant

public abstract class CheckPoint : ICheckPoint {
    public abstract val file: IBugResInfo
    internal abstract val env: DefaultEnv

    public open val possibleTypes: Set<Type>
        get() {
            if (this is Constant) {
                return setOf((this as Constant).type)
            } else if (this !is Local) {
                return emptySet()
            } else if (!Scene.v().hasPointsToAnalysis()) {
                throw IllegalArgumentException("Failed requirement.")
            } else {
                var var10000: Set<Type> = Scene.v().pointsToAnalysis.reachingObjects(this as Local).possibleTypes()
                if (var10000 == null) {
                    var10000 = emptySet()
                }

                return var10000
            }
        }

    public open val possibleConstantValues: Set<String>
        get() {
            if (this is StringConstant) {
                return setOf((this as StringConstant).value)
            } else if (this !is Local) {
                return emptySet()
            } else if (!Scene.v().hasPointsToAnalysis()) {
                throw IllegalArgumentException("Failed requirement.")
            } else {
                var var10000: Set<String> = Scene.v().pointsToAnalysis.reachingObjects(this as Local).possibleStringConstants()
                if (var10000 == null) {
                    var10000 = emptySet()
                }

                return var10000
            }
        }

    public override fun SootMethod.hasSideEffect(): Boolean {
        return true
    }

    public override fun SootClass.isInstanceOf(parent: String): Boolean? {
        val var10000: SootClass? = Scene.v().getSootClassUnsafe(parent, false)
        return if (var10000 == null) null else this.isInstanceOf(var10000)
    }

    public override fun SootClass.isInstanceOf(parent: SootClass): Boolean {
        return Scene.v().orMakeFastHierarchy.canStoreType(this.type, parent.type)
    }

    public override fun toString(): String {
        val className = this::class.java.simpleName
        val file = this.file
        val nodeWithRange = this as? INodeWithRange
        if (nodeWithRange != null) {
            val region = nodeWithRange.region
            if (region != null) {
                val regionStr = region.toString()
                if (regionStr != null) {
                    return "$className at $file:$regionStr"
                }
            }
        }

        return "$className at $file:"
    }
}