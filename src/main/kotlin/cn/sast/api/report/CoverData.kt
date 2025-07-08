package cn.sast.api.report

import soot.SootClass
import soot.SootMethod
import soot.Unit
/**
 * 覆盖数据（CoverInst / CoverTaint 等）的共同父类。
 */
sealed class CoverData


/**
 * 绑定到 *具体 Soot 指令* 的覆盖数据。
 *
 * @property method 所在方法
 * @property unit   具体指令
 */
sealed class CoverSootCode(
    open val method: SootMethod,
    open val unit: Unit
) : CoverData() {

    /** 所在类 */
    val clazz: SootClass get() = method.declaringClass

    /** 类全限定名 */
    val className: String get() = clazz.name

    /** 代码行号（基于 `LineNumberTag`） */
    val lineNumber: Int get() = unit.javaSourceStartLineNumber
}

/**
 * 覆盖 **普通指令**。
 */
data class CoverInst(
    override val method: SootMethod,
    override val unit: Unit
) : CoverSootCode(method, unit)

/**
 * 覆盖 **污点值**。
 *
 * @property value 任意携带对象（如 Soot Value、符号表达式等）
 */
data class CoverTaint(
    override val method: SootMethod,
    override val unit: Unit,
    val value: Any
) : CoverSootCode(method, unit)