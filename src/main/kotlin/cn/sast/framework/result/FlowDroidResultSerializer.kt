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

public open class FlowDroidResultSerializer(outputDir: IResDirectory, enableLineNumbers: Boolean = true) : IFlowDroidResultCollector {
   public final val outputDir: IResDirectory
   public final val enableLineNumbers: Boolean

   init {
      this.outputDir = outputDir;
      this.enableLineNumbers = enableLineNumbers;
   }

   public fun serializeResults(results: InfoflowResults, cfg: IInfoflowCFG?) {
      if (results.size() > 0) {
         val resultsFile: IResource = this.outputDir.resolve("infoflow-result.txt");
         val config: InfoflowConfiguration = new InfoflowConfiguration();
         config.setEnableLineNumbers(this.enableLineNumbers);
         val serializer: InfoflowResultsSerializer = new InfoflowResultsSerializer(cfg, config);

         try {
            serializer.serialize(results, resultsFile.toString());
         } catch (var7: IOException) {
            System.err.println("Could not write data flow results to file: ${var7.getMessage()}");
            var7.printStackTrace();
         } catch (var8: XMLStreamException) {
            System.err.println("Could not write data flow results to file: ${var8.getMessage()}");
            var8.printStackTrace();
         }
      }
   }

   public open fun onResultsAvailable(cfg: IInfoflowCFG, results: InfoflowResults) {
      this.serializeResults(results, cfg);
   }

   public open fun onResultAvailable(icfg: IInfoflowCFG?, abs: AbstractionAtSink?): Boolean {
      return true;
   }

   public override suspend fun flush() {
      return flush$suspendImpl(this, `$completion`);
   }
}
