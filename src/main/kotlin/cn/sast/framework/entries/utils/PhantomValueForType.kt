package cn.sast.framework.entries.utils

import mu.KLogger
import mu.KotlinLogging
import soot.*
import soot.jimple.Jimple
import utils.BaseBodyGenerator
import utils.INewUnits
import utils.NewUnitsAtLastImmediately

class PhantomValueForType(private val dummyClassName: String = "dummyMainClass") {
    private val logger: KLogger = KotlinLogging.logger {}

    private val summaryClass: SootClass
        get() = Scene.v().getSootClassUnsafe(dummyClassName, false)
            ?: Scene.v().makeSootClass(dummyClassName).apply {
                resolvingLevel = SootClass.BODIES
                setApplicationClass()
            }

    private fun simpleName(tp: Type): String = when (tp) {
        is PrimType -> tp.toString()
        is RefType -> tp.className.substringAfterLast('.')
        is ArrayType -> "${simpleName(tp.elementType)}Array"
        else -> throw IllegalArgumentException("Unsupported parameter type: $tp")
    }

    fun getOrMakeSootMethodForType(tp: Type, base: String, idx: Int = 1): SootMethod {
        val clazz = summaryClass
        clazz.getMethodByNameUnsafe(base)?.let { found ->
            return if (found.returnType == tp) found else getOrMakeSootMethodForType(tp, base + idx, idx + 1)
        }
        return Scene.v().makeSootMethod(base, emptyList(), tp, SootMethod.STATIC).also {
            clazz.addMethod(it)
            it.isPhantom = true
        }
    }

    fun getValueForType(units: INewUnits, generator: LocalGenerator, tp: Type): Local? = runCatching {
        val localName = simpleName(tp)
        val getter = getOrMakeSootMethodForType(tp, "get$localName")
        generator.generateLocal(tp).also { local ->
            units.add(Jimple.v().newAssignStmt(local, Jimple.v().newStaticInvokeExpr(getter.makeRef())))
        }
    }.getOrElse {
        logger.warn(it) { "Failed to create phantom value for type $tp" }
        null
    }

    fun getValueForType(body: Body, generator: LocalGenerator, tp: Type): Local? =
        getValueForType(NewUnitsAtLastImmediately(body.units), generator, tp)

    fun getValueForType(body: BaseBodyGenerator, tp: Type): Local? =
        getValueForType(NewUnitsAtLastImmediately(body.units), body.generator, tp)

    fun getValueForType(newUnits: INewUnits, body: BaseBodyGenerator, tp: Type): Local? =
        getValueForType(newUnits, body.generator, tp)
}
