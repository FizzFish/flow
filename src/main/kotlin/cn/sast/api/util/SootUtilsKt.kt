@file:SourceDebugExtension(["SMAP\nSootUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SootUtils.kt\ncn/sast/api/util/SootUtilsKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,512:1\n1557#2:513\n1628#2,3:514\n1557#2:517\n1628#2,3:518\n1368#2:522\n1454#2,5:523\n1368#2:528\n1454#2,5:529\n1454#2,2:536\n1557#2:538\n1628#2,3:539\n1456#2,3:542\n1368#2:545\n1454#2,5:546\n1#3:521\n183#4,2:534\n*S KotlinDebug\n*F\n+ 1 SootUtils.kt\ncn/sast/api/util/SootUtilsKt\n*L\n52#1:513\n52#1:514,3\n55#1:517\n55#1:518,3\n441#1:522\n441#1:523,5\n442#1:528\n442#1:529,5\n506#1:536,2\n507#1:538\n507#1:539,3\n506#1:542,3\n455#1:545\n455#1:546,5\n474#1:534,2\n*E\n"])

package cn.sast.api.util

import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.ResourceKt
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.cache.analysis.SootRangeKey
import com.feysh.corax.config.api.utils.UtilsKt
import com.google.common.base.Optional
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.lang.reflect.Constructor
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashSet
import kotlin.jvm.internal.CallableReference
import kotlin.jvm.internal.ClassBasedDeclarationContainer
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KDeclarationContainer
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.KCallables
import kotlin.reflect.jvm.KClassesJvm
import kotlin.reflect.jvm.ReflectJvmMapping
import soot.Body
import soot.BooleanType
import soot.ByteType
import soot.CharType
import soot.DoubleType
import soot.FloatType
import soot.G
import soot.IntType
import soot.IntegerType
import soot.Local
import soot.LongType
import soot.Printer
import soot.RefLikeType
import soot.Scene
import soot.ShortType
import soot.SootClass
import soot.SootMethod
import soot.SootMethodRef
import soot.SourceLocator
import soot.Type
import soot.Value
import soot.asm.AsmUtil
import soot.jimple.AddExpr
import soot.jimple.AndExpr
import soot.jimple.ArithmeticConstant
import soot.jimple.ClassConstant
import soot.jimple.CmpExpr
import soot.jimple.CmpgExpr
import soot.jimple.CmplExpr
import soot.jimple.Constant
import soot.jimple.DefinitionStmt
import soot.jimple.DivExpr
import soot.jimple.DoubleConstant
import soot.jimple.EqExpr
import soot.jimple.Expr
import soot.jimple.FloatConstant
import soot.jimple.GeExpr
import soot.jimple.GtExpr
import soot.jimple.InstanceInvokeExpr
import soot.jimple.IntConstant
import soot.jimple.InvokeExpr
import soot.jimple.JasminClass
import soot.jimple.LeExpr
import soot.jimple.LongConstant
import soot.jimple.LtExpr
import soot.jimple.MulExpr
import soot.jimple.NeExpr
import soot.jimple.NullConstant
import soot.jimple.NumericConstant
import soot.jimple.OrExpr
import soot.jimple.RealConstant
import soot.jimple.RemExpr
import soot.jimple.ShlExpr
import soot.jimple.ShrExpr
import soot.jimple.SpecialInvokeExpr
import soot.jimple.Stmt
import soot.jimple.StringConstant
import soot.jimple.SubExpr
import soot.jimple.UshrExpr
import soot.jimple.XorExpr
import soot.jimple.toolkits.callgraph.VirtualCalls
import soot.tagkit.SourceFileTag
import soot.tagkit.Tag
import soot.util.Chain
import soot.util.JasminOutputStream
import soot.util.queue.ChunkedQueue
import soot.util.queue.QueueReader

val KCallable<*>.KClass: KClass<*>
    get() {
        val ownerClass = if (this is CallableReference) {
            owner as? KClass<*>
        } else {
            val instanceParam = KCallables.instanceParameter(this)
            instanceParam?.type?.classifier as? KClass<*>
        }

        return ownerClass ?: tryConstructor(this) ?: throw IllegalStateException("Can't get parent class for $this")
    }

val KCallable<*>.paramStringList: List<String>
    get() = if (this is CallableReference) {
        val signature = this.signature
        val desc = AsmUtil.toJimpleDesc(signature.substringAfter("("), Optional.fromNullable(null))
        desc.removeAt(desc.size - 1)
        desc.map { UtilsKt.getTypename(it as Type) }
    } else {
        val params = parameters.drop(if (KCallables.instanceParameter(this) != null) 1 else 0)
        params.map {
            ReflectJvmMapping.getJavaType(it.type).typeName.substringBefore('<')
        }
    }

val KCallable<*>.paramSignature: String
    get() = paramStringList.joinToString(",")

val KCallable<*>.subSignature: String
    get() = if (this is CallableReference) {
        val signature = this.signature
        val sigTypes = AsmUtil.toJimpleDesc(signature.substringAfter("("), Optional.fromNullable(null))
        val returnType = sigTypes.removeAt(sigTypes.size - 1) as Type
        "$returnType ${this.name}(${sigTypes.joinToString(",") { UtilsKt.getTypename(it as Type) }})"
    } else {
        "${ReflectJvmMapping.getJavaType(returnType).typeName} ${name}(${paramSignature})"
    }

val KCallable<*>.returnSootType: Type
    get() {
        val typeName = if (this is CallableReference) {
            val signature = this.signature
            val sigTypes = AsmUtil.toJimpleDesc(signature.substringAfter("("), Optional.fromNullable(null))
            (sigTypes.removeAt(sigTypes.size - 1) as Type).toString()
        } else {
            ReflectJvmMapping.getJavaType(returnType).typeName
        }
        return Scene.v().getTypeUnsafe(typeName, true)
    }

val KCallable<*>.sootClassName: String
    get() {
        val ks = KClass
        return if (ks is ClassBasedDeclarationContainer) {
            ks.jClass.name
        } else {
            ks.qualifiedName ?: throw IllegalStateException("No qualified name for $ks")
        }
    }

val KCallable<*>.sootSignature: String
    get() = "<${sootClassName}: ${subSignature}>"

val KCallable<*>.grabSootMethod: SootMethod?
    get() = Scene.v().grabMethod(sootSignature)

val KCallable<*>.sootMethod: SootMethod
    get() = Scene.v().getMethod(sootSignature)

val KClass<*>.sootClass: SootClass
    get() = Scene.v().getSootClass(KClassesJvm.getJvmName(this))

val KClass<*>.className: String
    get() = KClassesJvm.getJvmName(this)

val KClass<*>.sootClassUnsafe: SootClass?
    get() = Scene.v().getSootClassUnsafe(KClassesJvm.getJvmName(this), false)

val Stmt?.invokeExprOrNull: InvokeExpr?
    get() = (this as? Stmt)?.takeIf { it.containsInvokeExpr() }?.invokeExpr

val Body.invokeExprOrNull: InvokeExpr?
    get() = if (containsInvokeExpr()) getInvokeExpr() else null

val DefinitionStmt?.leftOp: Value?
    get() = (this as? DefinitionStmt)?.leftOp

val SootClass.numCode: Int
    get() {
        var loc = 0
        for (sm in methods) {
            if (sm.hasActiveBody()) {
                val range = AnalysisCache.G.INSTANCE[SootRangeKey(sm)]
                if (range != null) {
                    loc = maxOf(loc, range.first as Int, range.second as Int)
                }
            }
        }
        return loc
    }

val SootClass.sourcePath: String?
    get() = ClassPathUtilKt.getSourcePathModule(this)

val SootClass.possibleSourceFiles: LinkedHashSet<String>
    get() {
        val res = linkedSetOf<String>()
        sourcePath?.let { res.add(it) }

        val list = linkedSetOf<String>()
        list.add(SourceLocator.v().getSourceForClass(name.replace(".", "/")))
        
        if (name.contains("$")) {
            list.add(name.split(".").joinToString("/"))
        }

        for (element in list) {
            res.addAll(ResourceKt.javaExtensions.map { "$element.$it" })
        }
        return res
    }

val SootMethod.activeBodyOrNull: Body?
    get() = if (hasActiveBody()) activeBody else null

private fun <R> tryConstructor(function: KCallable<R>): KClass<out Any>? {
    return (function as? KFunction<*>)?.let { 
        ReflectJvmMapping.getJavaConstructor(it)?.declaringClass?.kotlin
    }
}

fun convertParameterTypes(paramTypes: List<CharSequence>): List<Type> {
    return paramTypes.map { Scene.v().getTypeUnsafe(it.toString(), true) }
}

fun <R> KCallable<R>.sootMethodRef(isStatic: Boolean): SootMethodRef {
    return Scene.v().makeMethodRef(
        Scene.v().getSootClass(sootClassName),
        name,
        convertParameterTypes(paramStringList),
        returnSootType,
        isStatic
    )
}

fun classSplit(cp: SootClass): Pair<String, String> = classSplit(cp.name)

fun classSplit(cname: String): Pair<String, String> =
    cname.substringBeforeLast(".", "") to cname.substringAfterLast(".")

fun SootClass.getSourcePathFromAnnotation(): String? {
    val fixed = getTag("SourceFileTag") as? SourceFileTag ?: return null
    val source = fixed.sourceFile
    val var5 = source.substringBeforeLast("..")
        .substringBeforeLast("/")
        .substringBeforeLast("\\")
    
    if (!ResourceKt.javaExtensions.contains(var5.substringAfterLast("."))) {
        return null
    }

    val var6 = classSplit(this).first.replace(".", "/")
    return if (var6.isEmpty()) var5 else "$var6/$var5"
}

fun NumericConstant.castTo(toType: Type): Constant? = when {
    toType is BooleanType -> when (this) {
        is IntConstant -> IntConstant.v(if (value != 0) 1 else 0)
        is LongConstant -> IntConstant.v(if (value.toInt() != 0) 1 else 0)
        is FloatConstant -> IntConstant.v(if (value.toInt() != 0) 1 else 0)
        is DoubleConstant -> IntConstant.v(if (value.toInt() != 0) 1 else 0)
        else -> null
    }
    toType is ByteType -> when (this) {
        is IntConstant -> IntConstant.v(value.toByte())
        is LongConstant -> IntConstant.v(value.toInt().toByte())
        is FloatConstant -> IntConstant.v(value.toInt().toByte())
        is DoubleConstant -> IntConstant.v(value.toInt().toByte())
        else -> null
    }
    toType is CharType -> when (this) {
        is IntConstant -> IntConstant.v(value.toChar().code)
        is LongConstant -> IntConstant.v(value.toInt().toChar().code)
        is FloatConstant -> IntConstant.v(value.toInt().toChar().code)
        is DoubleConstant -> IntConstant.v(value.toInt().toChar().code)
        else -> null
    }
    toType is ShortType -> when (this) {
        is IntConstant -> IntConstant.v(value.toShort())
        is LongConstant -> IntConstant.v(value.toInt().toShort())
        is FloatConstant -> IntConstant.v(value.toInt().toShort())
        is DoubleConstant -> IntConstant.v(value.toInt().toShort())
        else -> null
    }
    toType is IntType -> when (this) {
        is IntConstant -> this
        is LongConstant -> IntConstant.v(value.toInt())
        is FloatConstant -> IntConstant.v(value.toInt())
        is DoubleConstant -> IntConstant.v(value.toInt())
        else -> null
    }
    toType is LongType -> when (this) {
        is IntConstant -> LongConstant.v(value.toLong())
        is LongConstant -> this
        is FloatConstant -> LongConstant.v(value.toLong())
        is DoubleConstant -> LongConstant.v(value.toLong())
        else -> null
    }
    toType is FloatType -> when (this) {
        is IntConstant -> FloatConstant.v(value.toFloat())
        is LongConstant -> FloatConstant.v(value.toFloat())
        is FloatConstant -> this
        is DoubleConstant -> FloatConstant.v(value.toFloat())
        else -> null
    }
    toType is DoubleType -> when (this) {
        is IntConstant -> DoubleConstant.v(value.toDouble())
        is LongConstant -> DoubleConstant.v(value.toDouble())
        is FloatConstant -> DoubleConstant.v(value.toDouble())
        is DoubleConstant -> this
        else -> null
    }
    else -> null
}

fun Constant.equalEqual(b: Constant, isEq: Boolean): NumericConstant? = when {
    this is NumericConstant -> if (b !is NumericConstant) {
        IntConstant.v(0)
    } else {
        if (isEq) equalEqual(b) else notEqual(b)
    }
    this is StringConstant || this is NullConstant || this is ClassConstant -> {
        IntConstant.v(if (isEq == (this == b)) 1 else 0)
    }
    else -> null
}

@Throws(ArithmeticException::class, IllegalArgumentException::class)
fun evalConstantBinop(expr: Expr, c1: Constant, c2: Constant): NumericConstant? = when (expr) {
    is AddExpr -> (c1 as NumericConstant).add(c2 as NumericConstant)
    is SubExpr -> (c1 as NumericConstant).subtract(c2 as NumericConstant)
    is MulExpr -> (c1 as NumericConstant).multiply(c2 as NumericConstant)
    is DivExpr -> (c1 as NumericConstant).divide(c2 as NumericConstant)
    is RemExpr -> (c1 as NumericConstant).remainder(c2 as NumericConstant)
    is EqExpr -> equalEqual(c1, c2, true)
    is NeExpr -> equalEqual(c1, c2, false)
    is GtExpr -> (c1 as NumericConstant).greaterThan(c2 as NumericConstant)
    is GeExpr -> (c1 as NumericConstant).greaterThanOrEqual(c2 as NumericConstant)
    is LtExpr -> (c1 as NumericConstant).lessThan(c2 as NumericConstant)
    is LeExpr -> (c1 as NumericConstant).lessThanOrEqual(c2 as NumericConstant)
    is AndExpr -> (c1 as ArithmeticConstant).and(c2 as ArithmeticConstant) as NumericConstant
    is OrExpr -> (c1 as ArithmeticConstant).or(c2 as ArithmeticConstant) as NumericConstant
    is XorExpr -> (c1 as ArithmeticConstant).xor(c2 as ArithmeticConstant) as NumericConstant
    is ShlExpr -> (c1 as ArithmeticConstant).shiftLeft(c2 as ArithmeticConstant) as NumericConstant
    is ShrExpr -> (c1 as ArithmeticConstant).shiftRight(c2 as ArithmeticConstant) as NumericConstant
    is UshrExpr -> (c1 as ArithmeticConstant).unsignedShiftRight(c2 as ArithmeticConstant) as NumericConstant
    is CmpExpr -> if (c1 is LongConstant && c2 is LongConstant) c1.cmp(c2) else null
    is CmpgExpr, is CmplExpr -> if (c1 is RealConstant && c2 is RealConstant) {
        if (expr is CmpgExpr) c1.cmpg(c2) else c1.cmpl(c2)
    } else null
    else -> null
}

@Throws(NumberFormatException::class)
fun <ToType : Type> StringConstant.cvtNumericConstant(radix: Int, type: ToType): NumericConstant? =
    value.cvtNumericConstant(radix, type)

@Throws(NumberFormatException::class)
fun <ToType : Type> String.cvtNumericConstant(radix: Int, type: ToType): NumericConstant? {
    require(radix in 2..36) { "Invalid radix: $radix" }
    return when (type) {
        is IntegerType -> IntConstant.v(toInt(radix))
        is LongType -> LongConstant.v(toLong(radix))
        is FloatType -> FloatConstant.v(toFloat())
        is DoubleType -> DoubleConstant.v(toDouble())
        else -> null
    }
}

inline fun Constant.accurateType(declareType: () -> Type): Type =
    if (type is RefLikeType) type else declareType()

fun printToSootClass(dir: String, sClass: SootClass) {
    val p = Paths.get("$dir${File.separator}${SourceLocator.v().getFileNameFor(sClass, 1)}").toFile()
    p.parentFile?.mkdirs()

    PrintWriter(OutputStreamWriter(FileOutputStream(p))).use { writer ->
        Printer.v().printTo(sClass, writer)
    }
}

@Throws(Exception::class)
fun sootClass2JasminClass(sClass: SootClass, out: IResDirectory): IResFile {
    val fileName = SourceLocator.v().getFileNameFor(sClass, 14)
    val outClass = out.resolve(fileName).toFile()
    outClass.mkdirs()

    Files.newOutputStream(outClass.path).use { os ->
        JasminOutputStream(os).use { jos ->
            PrintWriter(OutputStreamWriter(jos)).use { writer ->
                JasminClass(sClass).print(writer)
            }
        }
    }
    return outClass
}

fun constOf(v: Any): Pair<Constant, Type> = when (v) {
    is Constant -> v to v.type
    is String -> StringConstant.v(v) to Scene.v().getType("java.lang.String")
    is Boolean -> IntConstant.v(if (v) 1 else 0) to G.v().soot_BooleanType
    is Int -> IntConstant.v(v) to G.v().soot_IntType
    is Long -> LongConstant.v(v) to G.v().soot_LongType
    is Double -> DoubleConstant.v(v) to G.v().soot_DoubleType
    is Float -> FloatConstant.v(v) to G.v().soot_FloatType
    is Byte -> IntConstant.v(v.toInt()) to G.v().soot_ByteType
    is Short -> IntConstant.v(v.toInt()) to G.v().soot_ShortType
    else -> throw NotImplementedError()
}

fun getCallTargets(
    type: Type,
    container: SootMethod? = null,
    ie: InvokeExpr,
    appOnly: Boolean = false
): Iterator<SootMethod> {
    val methodRef = ie.methodRef
    val virtualCalls = VirtualCalls.v()
    val targetsQueue = ChunkedQueue()
    val iter = targetsQueue.reader()

    if (ie is SpecialInvokeExpr) {
        virtualCalls.resolveSpecial(methodRef, container, appOnly)?.let { targetsQueue.add(it) }
    } else {
        val baseType = if (ie is InstanceInvokeExpr) {
            (ie.base as Local).type
        } else {
            methodRef.declaringClass.type
        }
        virtualCalls.resolve(type, baseType, methodRef, container, targetsQueue, appOnly)
    }
    return iter
}

@JvmSynthetic
fun getCallTargets$default(
    type: Type,
    container: SootMethod?,
    ie: InvokeExpr,
    appOnly: Boolean,
    mask: Int,
    any: Any?
): Iterator<SootMethod> {
    var actualContainer = container
    var actualAppOnly = appOnly
    
    if (mask and 2 != 0) actualContainer = null
    if (mask and 8 != 0) actualAppOnly = false
    
    return getCallTargets(type, actualContainer, ie, actualAppOnly)
}

fun SootClass.adjustLevel(level: Int) {
    if (resolvingLevel() < level) {
        setResolvingLevel(level)
    }
}

fun SootClass.superClassOrNull(): SootClass? = if (hasSuperclass()) superclass else null

private fun findAncestors(sc: SootClass): List<SootClass> {
    val superClasses = mutableListOf<SootClass>()
    val superInterfaces = mutableListOf<SootClass>()

    if (sc.isInterface) {
        superClasses.add(Scene.v().objectType.sootClass)
        superInterfaces.addAll(Scene.v().activeHierarchy.getSuperinterfacesOfIncluding(sc))
    } else {
        superClasses.addAll(Scene.v().activeHierarchy.getSuperclassesOfIncluding(sc))
        superInterfaces.addAll(superClasses.flatMap { it.interfaces.toList() })
        superInterfaces.addAll(superClasses.flatMap { 
            Scene.v().activeHierarchy.getSuperinterfacesOfIncluding(it) 
        })
    }

    return superClasses + superInterfaces
}

fun SootClass.findMethodOrNull(subSignature: String): Sequence<SootMethod> {
    adjustLevel(2)
    return sequence {
        yieldAll(generateSequence(this@findMethodOrNull, ::superClassOrNull)
            .flatMap { it.methods })
        
        yieldAll(generateSequence(this@findMethodOrNull, ::superClassOrNull)
            .flatMap { it.interfaces }
            .distinct()
            .flatMap { findAncestors(it) }
            .flatMap { it.methods })
    }.filter { method ->
        if (method.declaringClass != this@findMethodOrNull) {
            if (method.isStatic || method.isStaticInitializer) return@filter false
            if (method.isPrivate) return@filter false
        }
        method.subSignature.substringAfter(" ") == subSignature
    }
}

fun SootClass.findMethodOrNull(
    subSignature: String,
    predicate: (SootMethod) -> Boolean
): SootMethod? = findMethodOrNull(subSignature).firstOrNull(predicate)