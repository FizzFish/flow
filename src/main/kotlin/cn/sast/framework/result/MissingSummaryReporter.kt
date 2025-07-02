package cn.sast.framework.result

import cn.sast.api.report.Counter
import cn.sast.api.report.IResultCollector
import cn.sast.common.IResFile
import soot.SootMethod

public class MissingSummaryReporter(outputFile: IResFile? = null) : IMissingSummaryReporter, IResultCollector {
   private final val outputFile: IResFile?
   private final val counter: Counter<SootMethod>

   init {
      this.outputFile = outputFile;
      this.counter = new Counter<>();
   }

   public override fun reportMissingMethod(method: SootMethod) {
      this.counter.count(method);
   }

   public override suspend fun flush() {
      if (this.outputFile != null) {
         this.counter.writeResults(this.outputFile);
      }

      return Unit.INSTANCE;
   }

   fun MissingSummaryReporter() {
      this(null, 1, null);
   }
}
