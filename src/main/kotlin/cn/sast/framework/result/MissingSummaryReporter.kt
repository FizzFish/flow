package cn.sast.framework.result

import cn.sast.api.report.Counter
import cn.sast.api.report.IResultCollector
import cn.sast.common.IResFile
import soot.SootMethod

class MissingSummaryReporter(outputFile: IResFile? = null) : IMissingSummaryReporter, IResultCollector {
    private val outputFile: IResFile?
    private val counter: Counter<SootMethod>

    init {
        this.outputFile = outputFile
        this.counter = Counter()
    }

    override fun reportMissingMethod(method: SootMethod) {
        this.counter.count(method)
    }

    override suspend fun flush() {
        if (this.outputFile != null) {
            this.counter.writeResults(this.outputFile)
        }
    }

    constructor() : this(null)
}