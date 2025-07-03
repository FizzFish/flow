package cn.sast.api.report

import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import java.nio.charset.Charset
import org.jacoco.core.analysis.ICounter

public interface ICoverageCollector {
    public val enableCoveredTaint: Boolean

    public fun cover(coverInfo: CoverData)

    public suspend fun flush(output: IResDirectory, sourceEncoding: Charset)

    public suspend fun getCoveredLineCounter(allSourceFiles: Set<IResFile>, encoding: Charset): ICounter
}