package cn.sast.framework.engine

import java.lang.reflect.Method
import mu.KLogger
import mu.KotlinLogging
import soot.jimple.infoflow.AbstractInfoflow
import soot.jimple.infoflow.InfoflowConfiguration
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration
import soot.jimple.infoflow.sourcesSinks.manager.ISourceSinkManager

public fun InfoflowConfiguration.fix() {
    val logger: KLogger = KotlinLogging.logger(InfoflowConfiguration::class.java.name)
    if (this is InfoflowAndroidConfiguration
        && this.sourceSinkConfig.enableLifecycleSources
        && this.iccConfig.isIccEnabled) {
        logger.warn("ICC model specified, automatically disabling lifecycle sources")
        this.sourceSinkConfig.enableLifecycleSources = false
    }
}

public fun AbstractInfoflow.runAnalysisReflect(sourcesSinks: ISourceSinkManager, additionalSeeds: Set<String>?) {
    val it: Method = AbstractInfoflow::class.java.getDeclaredMethod("runAnalysis", ISourceSinkManager::class.java, Set::class.java)
    it.isAccessible = true
    it.invoke(this, sourcesSinks, additionalSeeds)
}