@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST", "UNUSED_PARAMETER")

package cn.sast.api.util

import cn.sast.common.javaExtensions
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.cache.analysis.SootRangeKey
import com.feysh.corax.config.api.IMethodMatch
import com.feysh.corax.config.api.baseimpl.matchSimpleSig
import com.feysh.corax.config.api.baseimpl.matchSoot
import com.feysh.corax.config.api.utils.getTypename
import com.google.common.base.Optional
import mu.KLogger
import mu.KotlinLogging
import org.objectweb.asm.ClassWriter
import soot.*
import soot.Type
import soot.asm.AsmUtil
import soot.jimple.*
import soot.tagkit.SourceFileTag
import sun.misc.Unsafe
import java.io.InputStream
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodHandles.Lookup
import java.lang.reflect.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.*
import kotlin.jvm.internal.CallableReference
import kotlin.math.max
import kotlin.reflect.*
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.jvm.javaType;
import kotlin.reflect.jvm.javaConstructor
import kotlin.jvm.internal.ClassBasedDeclarationContainer
import java.lang.reflect.Modifier

/* ---------- 过滤掉 value 为 null 的 Map ---------- */
fun <K, V : Any> Map<K, V?>.nonNullValue(): Map<K, V> =
    buildMap(capacity = size) {
        for ((k, v) in this@nonNullValue) if (v != null) put(k, v)
    }

/* ---------- 并发 HashSet ---------- */
fun <E> concurrentHashSetOf(vararg elements: E): MutableSet<E> =
    Collections.newSetFromMap(ConcurrentHashMap<E, Boolean>(max(elements.size * 2, 16))).apply {
        elements.forEach { add(it) }
    }

fun <E> concurrentHashSetOf(): MutableSet<E> =
    Collections.newSetFromMap(ConcurrentHashMap())

/* ────────────────────────────────────────────────────────────────────────────
 *  3. Soot 相关扩展
 * ─────────────────────────────────────────────────────────────────────────── */

/* ---------- KCallable & KClass ↔ Soot 映射 ---------- */

/** Return declaring Kotlin class for any `KCallable`. */
val KCallable<*>.kClass: KClass<*>
    get() = when (this) {
        is CallableReference -> owner as? KClass<*> ?: tryConstructor(this) ?: error("Can't get parent class for $this")
        else -> this.instanceParameter?.type?.classifier as? KClass<*> ?: tryConstructor(this) ?: error("Can't get parent class for $this")
    }

/** List of parameter *simple* class names (no generics). */
val KCallable<*>.paramStringList: List<String>
    get() = when (this) {
        is CallableReference -> {
            val desc = AsmUtil.toJimpleDesc(signature.substringAfter("("), Optional.absent())
            // last element is return type → drop()
            desc.dropLast(1).map { it.getTypename() ?: "" }
        }
        else -> parameters
            .drop(if (this.instanceParameter != null) 1 else 0)
            .map { it.type.javaType.typeName.substringBefore('<') }
    }

/** Comma‑joined param list. */
val KCallable<*>.paramSignature: String get() = paramStringList.joinToString()

fun getSootTypeName(clazz: Class<*>): String {
    // Fast path for non-arrays
    if (!clazz.isArray) return clazz.name

    return try {
        var current = clazz
        var dimension = 0

        // Unwrap array dimensions
        while (current.isArray) {
            current = current.componentType
            dimension++
        }

        buildString {
            append(current.name)
            repeat(dimension) { append("[]") }
        }
    } catch (t: Throwable) {
        // Project–specific critical-error handling
        throw t          // Re-throw so callers still see the exception
    }
}

/** Sub‑signature in Soot grammar. */
val KCallable<*>.subSignature: String
    get() = when (this) {
        is CallableReference -> {
            val sigTypes = AsmUtil.toJimpleDesc(signature.substringAfter("("), Optional.absent()).toMutableList()
            val ret = sigTypes.removeLast()     // return type
            val params = sigTypes.joinToString(",") { it.getTypename() ?: "" }
            "$ret $name($params)"
        }
        else -> "${returnType.javaType.typeName} $name($paramSignature)"
    }

/** Soot [Type] matching the callable's *return* type. */
val KCallable<*>.returnSootType: Type
    get() {
        val tName = when (this) {
            is CallableReference -> AsmUtil.toJimpleDesc(signature.substringAfter("("), Optional.absent()).last().toString()
            else -> returnType.javaType.typeName
        }
        return Scene.v().getTypeUnsafe(tName, true)
    }

val KCallable<*>.sootClassName: String
    get() = kClass.qualifiedName ?: (kClass as? ClassBasedDeclarationContainer)?.jClass?.name ?: error("Unnamed class")

val KCallable<*>.sootSignature: String get() = "<${sootClassName}: ${subSignature}>"

val KCallable<*>.grabSootMethod: SootMethod? get() = Scene.v().grabMethod(sootSignature)
val KCallable<*>.sootMethod: SootMethod get() = Scene.v().getMethod(sootSignature)
val KCallable<*>.sootClass: SootClass get() = Scene.v().getSootClass(sootClassName)
val KCallable<*>.sootClassUnsafe: SootClass? get() = Scene.v().getSootClassUnsafe(sootClassName, false)
val KCallable<*>.className: String get() = sootClassName

//private fun <R> tryConstructor(function: KCallable<R>): KClass<out Any>? =
//    (function as? KFunction<*>)?.javaConstructor?.declaringClass?.kotlin
private fun <R> tryConstructor(function: KCallable<R>): KClass<out Any>? {
    val kFun = function as? KFunction<*> ?: return null
    val ctor = kFun.javaConstructor ?: return null
    @Suppress("UNCHECKED_CAST")
    return (ctor.declaringClass as Class<Any>).kotlin
}

/* ---------- SootClass / SootMethod 扩展 ---------- */

val KClass<*>.sootClass: SootClass get() = Scene.v().getSootClass(qualifiedName)

val SootClass.isDummy: Boolean
    get() = name.contains("dummy", ignoreCase = true)

val SootClass.isSyntheticComponent: Boolean
    get() = name.contains("synthetic", ignoreCase = true) || isDummy

val SootClass.skipPathSensitive: Boolean
    get() = isDummy || isSyntheticComponent

/** Highest line‑number seen across all active bodies of this class. */
val SootClass.numCode: Int
    get() = methods
        .filter { it.hasActiveBody() }
        .mapNotNull { AnalysisCache.G.get(SootRangeKey(it)) }
        .fold(0) { acc, (start, end) -> max(acc, max(start, end)) }

/** `SourceFile` tag‑based original source path if any (JPMS‑aware). */
val SootClass.sourcePath: String? get() = getSourcePathModule(this)

/** All plausible source files for this class (kts, java, etc.). */
val SootClass.possibleSourceFiles: LinkedHashSet<String>
    get() {
        val res = linkedSetOf<String>()
        sourcePath?.let(res::add)

        val prefixes = linkedSetOf(
            SourceLocator.v().getSourceForClass(name.replace('.', '/'))
        )
        if ('$' in name) {
            prefixes += name.split('.').joinToString("/")
        }

        prefixes.forEach { p ->
            javaExtensions.forEach { ext -> res += "$p.$ext" }
        }
        return res as LinkedHashSet<String>
    }

val SootMethod.activeBodyOrNull: Body? get() = takeIf { hasActiveBody() }?.activeBody

fun classSplit(sc: SootClass): Pair<String, String> = classSplit(sc.name)

fun classSplit(cname: String): Pair<String, String> {
    val pkg = cname.substringBeforeLast('.', "")
    val cls = cname.substringAfterLast('.', cname)
    return pkg to cls
}

/** Locate original source path from the `SourceFileTag` (if any). */
fun SootClass.getSourcePathFromAnnotation(): String? {
    // 1⃣  Extract tag
    val tag = getTag("SourceFileTag") as? SourceFileTag ?: return null
    val source = tag.sourceFile

    // 2⃣  Heuristic clean‑ups identical to original byte‑code logic
    val trimmed = source
        .substringBeforeLast("..")
        .substringBeforeLast("/")
        .substringBeforeLast("\\")

    // 3⃣  Validate extension against supported source types
    val ext = trimmed.substringAfterLast('.', "")
    if (!javaExtensions.contains(ext)) return null

    // 4⃣  Prepend package directory if present
    val pkgPath = classSplit(this).first.replace('.', '/')
    return if (pkgPath.isEmpty()) trimmed else "$pkgPath/$trimmed"
}
/* ---------- findMethod 扩展 ---------- */

fun SootClass.findMethodOrNull(subSig: String): Sequence<SootMethod> = sequence {
    fun SootClass.hierarchy(): Sequence<SootClass> =
        generateSequence(this) { if (it.hasSuperclass()) it.superclass else null }

    yieldAll(hierarchy().flatMap { it.methods })
    yieldAll(hierarchy()
        .flatMap { it.interfaces }
        .distinct()
        .flatMap { Scene.v().activeHierarchy.getSuperinterfacesOfIncluding(it) + it }
        .flatMap { it.methods })
}.filter {
    (!it.isStatic && !it.isPrivate) &&
            it.subSignature.substringAfter(" ") == subSig
}

fun NumericConstant.castTo(toType: Type): Constant? {
    fun Int.asInt() = IntConstant.v(this)
    fun Long.asLong() = LongConstant.v(this)
    fun Float.asFloat() = FloatConstant.v(this)
    fun Double.asDouble() = DoubleConstant.v(this)

    return when (toType) {
        is BooleanType -> when (this) {
            is IntConstant    -> (if (value != 0) 1 else 0).asInt()
            is LongConstant   -> (if (value.toInt() != 0) 1 else 0).asInt()
            is FloatConstant  -> (if (value.toInt() != 0) 1 else 0).asInt()
            is DoubleConstant -> (if (value.toInt() != 0) 1 else 0).asInt()
            else              -> null
        }
        is ByteType -> when (this) {
            is IntConstant    -> value.toByte().toInt().asInt()
            is LongConstant   -> value.toInt().toByte().toInt().asInt()
            is FloatConstant  -> value.toInt().toByte().toInt().asInt()
            is DoubleConstant -> value.toInt().toByte().toInt().asInt()
            else              -> null
        }
        is CharType -> when (this) {
            is IntConstant    -> value.toChar().code.asInt()
            is LongConstant   -> value.toInt().toChar().code.asInt()
            is FloatConstant  -> value.toInt().toChar().code.asInt()
            is DoubleConstant -> value.toInt().toChar().code.asInt()
            else              -> null
        }
        is ShortType -> when (this) {
            is IntConstant    -> value.toShort().toInt().asInt()
            is LongConstant   -> value.toInt().toShort().toInt().asInt()
            is FloatConstant  -> value.toInt().toShort().toInt().asInt()
            is DoubleConstant -> value.toInt().toShort().toInt().asInt()
            else              -> null
        }
        is IntType -> when (this) {
            is IntConstant    -> this
            is LongConstant   -> value.toInt().asInt()
            is FloatConstant  -> value.toInt().asInt()
            is DoubleConstant -> value.toInt().asInt()
            else              -> null
        }
        is LongType -> when (this) {
            is IntConstant    -> value.toLong().asLong()
            is LongConstant   -> this
            is FloatConstant  -> value.toLong().asLong()
            is DoubleConstant -> value.toLong().asLong()
            else              -> null
        }
        is FloatType -> when (this) {
            is IntConstant    -> value.toFloat().asFloat()
            is LongConstant   -> value.toFloat().asFloat()
            is FloatConstant  -> this
            is DoubleConstant -> value.toFloat().asFloat()
            else              -> null
        }
        is DoubleType -> when (this) {
            is IntConstant    -> value.toDouble().asDouble()
            is LongConstant   -> value.toDouble().asDouble()
            is FloatConstant  -> value.toDouble().asDouble()
            is DoubleConstant -> this
            else              -> null
        }
        else -> null
    }
}



/* ────────────────────────────────────────────────────────────────────────────
 *  4. UnsafeProvider & UnsafeUtils
 * ─────────────────────────────────────────────────────────────────────────── */

/** 以最兼容的方式获取 `sun.misc.Unsafe` 实例 */
object UnsafeProvider {
    val unsafe: Unsafe by lazy {
        try {
            Unsafe.getUnsafe()
        } catch (_: SecurityException) {
            Unsafe::class.java.declaredFields
                .firstOrNull { it.type == Unsafe::class.java }
                ?.apply { isAccessible = true }
                ?.get(null) as? Unsafe
                ?: error("Unable to acquire sun.misc.Unsafe")
        }
    }
}

/** 运行时定义 “匿名具体子类” （把抽象类实例化） */
object UnsafeUtils {

    /**
     * 使用 ASM + Unsafe / MethodHandles 定义一个给定抽象类的匿名具体子类。
     *
     * @throws IllegalArgumentException 抽象类非法
     * @throws IllegalStateException    生成或加载失败
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> defineAnonymousConcreteSubclass(abstractClass: Class<T>): Class<out T> {
        require(Modifier.isAbstract(abstractClass.modifiers)) { "$abstractClass is not abstract" }

        val cw = ClassWriter(0)
        val superName = abstractClass.name.replace('.', '/')
        val pkgName = UnsafeUtils::class.java.`package`.name.replace('.', '/')
        cw.visit(52, 0, "$pkgName/Anonymous", null, superName, null)
        cw.visitEnd()
        val bytes = cw.toByteArray()

        // JDK 15+ : MethodHandles.Lookup#defineHiddenClass
        return MethodHandles.lookup().runCatching {
//            val arrayClass = java.lang.reflect.Array.newInstance(Class::class.java, 0).javaClass
            val arrayClass: Class<*> = Class.forName("[Ljava.lang.Class;")
            val defineHidden = Lookup::class.java.getMethod(
                "defineHiddenClass",
                ByteArray::class.java, Boolean::class.javaPrimitiveType, arrayClass
            )
            val classOption = Lookup::class.java.classes
                .first { it.simpleName == "ClassOption" }
            val opts = java.lang.reflect.Array.newInstance(classOption, 0) as Array<*>
            val lookup = defineHidden.invoke(this, bytes, true, opts) as Lookup
            return lookup.lookupClass().asSubclass(abstractClass)
        }.getOrElse {
            // fallback : Unsafe#defineAnonymousClass
            val method = Unsafe::class.java.getMethod(
                "defineAnonymousClass",
                Class::class.java, ByteArray::class.java, Array<Any>::class.java
            )
            val clazz = method.invoke(UnsafeProvider.unsafe, UnsafeUtils::class.java, bytes, null) as Class<*>
            clazz.asSubclass(abstractClass)
        }
    }
}

/* ────────────────────────────────────────────────────────────────────────────
 *  5. 其他零散工具
 * ─────────────────────────────────────────────────────────────────────────── */

fun KClass<*>.asInputStream(): InputStream =
    java.getResourceAsStream("/${java.name.replace('.', '/')}.class")!!

inline fun printMilliseconds(message: String, body: () -> Unit) {
    val start = System.currentTimeMillis(); body()
    println("$message: ${System.currentTimeMillis() - start} ms")
}

fun methodSignatureToMatcher(sig: String): IMethodMatch? = when {
    sig.startsWith('<') && sig.endsWith('>') -> matchSoot(sig)
    ':' in sig                               -> matchSimpleSig(sig)
    else -> null
}
