package cn.sast.framework.result

import cn.sast.common.IResDirectory
import cn.sast.common.IResource
import java.io.IOException
import javax.xml.stream.XMLStreamException
import soot.jimple.infoflow.InfoflowConfiguration
import soot.jimple.infoflow.android.results.xml.InfoflowResultsSerializer
import soot.jimple.infoflow.data.AbstractionAtSink
import soot.jimple.infoflow.results.InfoflowResults
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG

public open class FlowDroidResultSerializer(
    public final val outputDir: IResDirectory,
    public final val enableLineNumbers: Boolean = true
) : IFlowDroidResultCollector {

    public fun serializeResults(results: InfoflowResults, cfg: IInfoflowCFG?) {
        if (results.size > 0) {
            val resultsFile: IResource = this.outputDir.resolve("infoflow-result.txt")
            val config = InfoflowConfiguration()
            config.setEnableLineNumbers(this.enableLineNumbers)
            val serializer = InfoflowResultsSerializer(cfg, config)

            try {
                serializer.serialize(results, resultsFile.toString())
            } catch (e: IOException) {
                System.err.println("Could not write data flow results to file: ${e.message}")
                e.printStackTrace()
            } catch (e: XMLStreamException) {
                System.err.println("Could not write data flow results to file: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    public open fun onResultsAvailable(cfg: IInfoflowCFG, results: InfoflowResults) {
        this.serializeResults(results, cfg)
    }

    public open fun onResultAvailable(icfg: IInfoflowCFG?, abs: AbstractionAtSink?): Boolean {
        return true
    }

    public override suspend fun flush() {
        return flush$suspendImpl(this, `$completion`)
    }
}