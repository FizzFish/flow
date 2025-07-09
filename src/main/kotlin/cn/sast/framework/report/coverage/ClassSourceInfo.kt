package cn.sast.framework.report.coverage

import cn.sast.common.IResFile
import org.jacoco.core.internal.data.CRC64
import org.jacoco.core.internal.instr.InstrSupport

/**
 * 一段已加载 **class 字节码** 的三元组：
 * 1. `className` —— *com.example.Foo*（点分）
 * 2. `byteArray` —— 原始 bytecode
 * 3. `classSource` —— 若存在，源码文件
 */
data class ClassSourceInfo(
    val className:  String,
    val byteArray:  ByteArray,
    val classSource: IResFile? = null
) {
    /** Jacoco 用于唯一标识 class 的 CRC64 */
    val jacocoClassId: Long by lazy { CRC64.classId(byteArray) }

    companion object {
        /**
         * 仅凭字节码构造（常见于从 JVM ClassLoader 兜底加载的类）
         */
        operator fun invoke(byteArray: ByteArray): ClassSourceInfo {
            val name = InstrSupport.classReaderFor(byteArray)
                .className
                .replace('/', '.')
            return ClassSourceInfo(name, byteArray, null)
        }
    }
}
