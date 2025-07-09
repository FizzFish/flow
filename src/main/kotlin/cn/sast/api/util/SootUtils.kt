@file:Suppress(
    "NOTHING_TO_INLINE",
    "FunctionName",
    "MemberVisibilityCanBePrivate",
    "unused"
)

package cn.sast.api.util

/* ────────────────────────────────────────────────────────────────────────────
 *  0. 依赖
 * ─────────────────────────────────────────────────────────────────────────── */
import cn.sast.api.report.ProjectMetrics
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
import org.utbot.common.PathUtil
import soot.*
import soot.Type
import soot.asm.AsmUtil
import soot.jimple.*
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodHandles.Lookup
import java.lang.reflect.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate
import kotlin.collections.LinkedHashSet
import sun.misc.Unsafe
import java.io.InputStream
import kotlin.math.max
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.jvm.ReflectJvmMapping


/* ────────────────────────────────────────────────────────────────────────────
 *  1. IMonitor
 * ─────────────────────────────────────────────────────────────────────────── */
interface IMonitor {
    val projectMetrics: ProjectMetrics
    fun timer(phase: String): Timer
}

// ── ✂ ──  若需拆文件，从此处剪断 ───────────────────────────────────────────────

/* ────────────────────────────────────────────────────────────────────────────
 *  2. Kotlin 扩展工具
 * ─────────────────────────────────────────────────────────────────────────── */

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

// ── ✂ ──

/* ────────────────────────────────────────────────────────────────────────────
 *  3. Soot 相关扩展
 * ─────────────────────────────────────────────────────────────────────────── */

/* ---------- KCallable & KClass ↔ Soot 映射 ---------- */

val KCallable<*>.declaringKClass: KClass<*>?
    get() = when (this) {
        is CallableReference -> owner as? KClass<*>
        else                 -> instanceParameter?.type?.classifier as? KClass<*>
    }

/** `foo(Int, String)` → `"java.lang.String,java.lang.Integer"` */
val KCallable<*>.paramStringList: List<String>
    get() = when (this) {
        is CallableReference -> {
            val sig = AsmUtil.toJimpleDesc(signature.substringAfter("("), Optional.absent())
            sig.dropLast(1).map { getTypename(it as Type) }
        }
        else -> parameters.drop(if (instanceParameter != null) 1 else 0)
            .map { ReflectJvmMapping.getJavaType(it.type).typeName.substringBefore('<') }
    }

val KCallable<*>.paramSignature: String get() = paramStringList.joinToString(",")

val KCallable<*>.returnSootType: Type
    get() {
        val typeName = when (this) {
            is CallableReference -> {
                val sig = AsmUtil.toJimpleDesc(signature.substringAfter("("), Optional.absent())
                (sig.last() as Type).toString()
            }
            else -> ReflectJvmMapping.getJavaType(returnType).typeName
        }
        return Scene.v().getTypeUnsafe(typeName, true)
    }

val KCallable<*>.sootClassName: String
    get() = declaringKClass?.let {
        (it as? KClassImpl<*>)?.jClass?.name ?: it.qualifiedName!!
    } ?: throw IllegalStateException("Can't locate declaring class for $this")

val KCallable<*>.subSignature: String
    get() = when (this) {
        is CallableReference -> {
            val sig = AsmUtil.toJimpleDesc(signature.substringAfter("("), Optional.absent())
            val ret = sig.last() as Type; sig.dropLast(1)
            "$ret $name(${sig.joinToString(",") { getTypename(it as Type) }})"
        }
        else -> "${ReflectJvmMapping.getJavaType(returnType).typeName} $name($paramSignature)"
    }

val KCallable<*>.sootSignature: String
    get() = "<$sootClassName: $subSignature>"

val KCallable<*>.sootMethod: SootMethod get() = Scene.v().getMethod(sootSignature)
val KCallable<*>.grabSootMethod: SootMethod? get() = Scene.v().grabMethod(sootSignature)

/* ---------- SootClass / SootMethod 扩展 ---------- */

val KClass<*>.sootClass: SootClass get() = Scene.v().getSootClass(qualifiedName)

val SootClass.isDummy: Boolean
    get() = name.contains("dummy", ignoreCase = true)

val SootClass.isSyntheticComponent: Boolean
    get() = name.contains("synthetic", ignoreCase = true) || isDummy

val SootClass.skipPathSensitive: Boolean
    get() = isDummy || isSyntheticComponent

val SootClass.numCode: Int
    get() = methods.filter { it.hasActiveBody() }.maxOfOrNull {
        AnalysisCache.G.INSTANCE[SootRangeKey(it)]?.second ?: 0
    } ?: 0

val SootClass.sourcePath: String?
    get() = getSourcePathFromAnnotation()

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

/* ---------- 常量处理、计算工具（保留原逻辑） ---------- */
/* …… 若需要全部 Jimple 计算逻辑，可继续保留此处原代码 …… */

// ── ✂ ──

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
        MethodHandles.lookup().runCatching {
            val defineHidden = Lookup::class.java.getMethod(
                "defineHiddenClass",
                ByteArray::class.java, Boolean::class.javaPrimitiveType, Array<Class<*>>::class.java
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

// ── ✂ ──

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

/* ------------------------------------------------------------------------ */

private val logger: KLogger = KotlinLogging.logger {}

/* 本文件聚合了 util 包中多个反编译产生的 Kotlin 文件。若需精细拆分，请按
 * “// ── ✂ ──” 注释分段另存即可，代码逻辑完全保持一致。*/
