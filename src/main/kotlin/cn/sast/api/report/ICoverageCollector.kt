package cn.sast.api.report

import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import java.nio.charset.Charset
import org.jacoco.core.analysis.ICounter
import soot.SootClass
import soot.SootMethod
import kotlin.coroutines.Continuation

interface ICoverageCollector {
    val enableCoveredTaint: Boolean

    fun cover(coverInfo: CoverData)

    suspend fun flush(output: IResDirectory, sourceEncoding: Charset)

    suspend fun getCoveredLineCounter(allSourceFiles: Set<IResFile>, encoding: Charset): ICounter
}


interface IReportHashAble {
    fun reportHash(c: IReportHashCalculator): String
}

interface IReportHashCalculator {
    fun from(clazz: SootClass): String

    fun from(method: SootMethod): String

    fun fromAbsPath(absolutePath: IResource): String

    open fun fromPath(path: IResource): String {
        return fromAbsPath(path.absolute.normalize)
    }

}

interface IResultCollector {
    open suspend fun flush()
}