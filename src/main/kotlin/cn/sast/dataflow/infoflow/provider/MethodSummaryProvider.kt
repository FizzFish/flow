package cn.sast.dataflow.infoflow.provider

import cn.sast.api.config.MainConfig
import cn.sast.api.config.PreAnalysisCoroutineScope
import cn.sast.api.config.PreAnalysisCoroutineScopeKt
import kotlinx.coroutines.runBlocking
import mu.KLogger
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.jimple.infoflow.methodSummary.data.provider.AbstractMethodSummaryProvider
import soot.jimple.infoflow.methodSummary.data.summary.ClassMethodSummaries
import soot.jimple.infoflow.methodSummary.data.summary.ClassSummaries
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries
import soot.util.MultiMap

open class MethodSummaryProvider(
   val mainConfig: MainConfig,
   val preAnalysisImpl: PreAnalysisCoroutineScope
) : AbstractMethodSummaryProvider() {

   private val loadedClasses: MutableSet<String> = LinkedHashSet()
   val classSummaries: ClassSummaries = ClassSummaries()
   var subsigMethodsWithSummaries: MutableSet<String> = LinkedHashSet()

   private val aiCheckerImpl by lazy { ModelingConfigImpl(this, preAnalysisImpl) }

   override fun getSupportedClasses(): MutableSet<String> = loadedClasses

   override fun getAllClassesWithSummaries(): MutableSet<String> = loadedClasses

   override fun supportsClass(clazz: String): Boolean = loadedClasses.contains(clazz)

   override fun getMethodFlows(className: String, methodSignature: String): ClassMethodSummaries? {
      val classSummaries = getClassSummaries(className)
      return classSummaries?.filterForMethod(methodSignature)
   }

   override fun getMethodFlows(classes: MutableSet<String>, methodSignature: String): ClassSummaries? {
      return classSummaries.filterForMethod(classes, methodSignature)
   }

   override fun getClassFlows(clazz: String?): ClassMethodSummaries? {
      return classSummaries.getClassSummaries(clazz)
   }

   override fun mayHaveSummaryForMethod(subsig: String): Boolean {
      return subsigMethodsWithSummaries.contains(subsig)
   }

   override fun getSummaries(): ClassSummaries = classSummaries

   override fun isMethodExcluded(className: String?, subSignature: String?): Boolean {
      val summaries = getClassSummaries(className)
      return summaries != null && summaries.methodSummaries.isExcluded(subSignature)
   }

   fun getClassSummaries(className: String?): ClassMethodSummaries? {
      return classSummaries.getClassSummaries(className)
   }

   protected fun addSubsigsForMethod(read: MethodSummaries) {
      val flows = read.flows
      val clears = read.clears
      flows?.let { subsigMethodsWithSummaries.addAll(it.keySet()) }
      clears?.let { subsigMethodsWithSummaries.addAll(it.keySet()) }
   }

   fun addMethodSummaries(newSummaries: ClassMethodSummaries) {
      synchronized(this) {
         addSubsigsForMethod(newSummaries.methodSummaries)
         classSummaries.merge(newSummaries)
         loadedClasses.add(newSummaries.className)
      }
   }

   suspend fun initialize() {
      PreAnalysisCoroutineScopeKt.processAIAnalysisUnits(aiCheckerImpl, preAnalysisImpl)
   }

   companion object {
      private val logger: KLogger? = null
   }
}

fun findAllOverrideMethodsOfMethod(method: SootMethod): Set<SootMethod> {
   val clazz: SootClass = method.declaringClass
   return Scene.v().fastHierarchy.resolveAbstractDispatch(clazz, method)
}