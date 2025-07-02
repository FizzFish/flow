package cn.sast.framework.engine

import java.lang.reflect.Method
import mu.KLogger
import mu.KotlinLogging
import soot.jimple.infoflow.AbstractInfoflow
import soot.jimple.infoflow.InfoflowConfiguration
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration
import soot.jimple.infoflow.sourcesSinks.manager.ISourceSinkManager

public fun InfoflowConfiguration.fix() {
   val var10000: KotlinLogging = KotlinLogging.INSTANCE;
   val var10001: java.lang.String = InfoflowConfiguration.class.getName();
   val logger: KLogger = var10000.logger(var10001);
   if (`$this$fix` is InfoflowAndroidConfiguration
      && (`$this$fix` as InfoflowAndroidConfiguration).getSourceSinkConfig().getEnableLifecycleSources()
      && (`$this$fix` as InfoflowAndroidConfiguration).getIccConfig().isIccEnabled()) {
      logger.warn("ICC model specified, automatically disabling lifecycle sources");
      (`$this$fix` as InfoflowAndroidConfiguration).getSourceSinkConfig().setEnableLifecycleSources(false);
   }
}

public fun AbstractInfoflow.runAnalysisReflect(sourcesSinks: ISourceSinkManager, additionalSeeds: Set<String>?) {
   val it: Method = AbstractInfoflow.class.getDeclaredMethod("runAnalysis", ISourceSinkManager.class, java.util.Set.class);
   it.setAccessible(true);
   it.invoke(`$this$runAnalysisReflect`, sourcesSinks, additionalSeeds);
}
