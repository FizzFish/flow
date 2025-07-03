package cn.sast.framework.report.coverage

import cn.sast.common.IResFile
import org.jacoco.core.internal.data.CRC64
import org.jacoco.core.internal.instr.InstrSupport
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.lazy

public class ClassSourceInfo(className: String, byteArray: ByteArray, classSource: IResFile?) {
    public val className: String = className
    public val byteArray: ByteArray = byteArray
    public val classSource: IResFile? = classSource

    private val jacocoClassId$delegate by lazy(NONE) { CRC64.classId(byteArray) }

    public val jacocoClassId: Long
        get() = jacocoClassId$delegate

    public companion object {
        @JvmStatic
        public operator fun invoke(byteArray: ByteArray): ClassSourceInfo {
            val className = InstrSupport.classReaderFor(byteArray).className.replace("/", ".")
            return ClassSourceInfo(className, byteArray, null)
        }
    }
}