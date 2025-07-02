package cn.sast.dataflow.infoflow.provider

import cn.sast.api.config.MainConfig
import cn.sast.api.config.PreAnalysisCoroutineScope
import cn.sast.api.config.PreAnalysisCoroutineScopeKt
import java.util.LinkedHashSet
import kotlin.coroutines.intrinsics.IntrinsicsKt
import mu.KLogger
import soot.jimple.infoflow.methodSummary.data.provider.AbstractMethodSummaryProvider
import soot.jimple.infoflow.methodSummary.data.summary.ClassMethodSummaries
import soot.jimple.infoflow.methodSummary.data.summary.ClassSummaries
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries
import soot.util.MultiMap

public open class MethodSummaryProvider(mainConfig: MainConfig, preAnalysisImpl: PreAnalysisCoroutineScope) : AbstractMethodSummaryProvider {
   public final val mainConfig: MainConfig
   public final val preAnalysisImpl: PreAnalysisCoroutineScope
   private final val loadedClasses: MutableSet<String>
   public final val classSummaries: ClassSummaries

   public final var subsigMethodsWithSummaries: MutableSet<String>
      internal set

   private final val aiCheckerImpl: ModelingConfigImpl
      private final get() {
         return this.aiCheckerImpl$delegate.getValue() as ModelingConfigImpl;
      }


   init {
      this.mainConfig = mainConfig;
      this.preAnalysisImpl = preAnalysisImpl;
      this.loadedClasses = new LinkedHashSet<>();
      this.classSummaries = new ClassSummaries();
      this.subsigMethodsWithSummaries = new LinkedHashSet<>();
      this.aiCheckerImpl$delegate = LazyKt.lazy(MethodSummaryProvider::aiCheckerImpl_delegate$lambda$1);
   }

   public open fun getSupportedClasses(): MutableSet<String> {
      return this.loadedClasses;
   }

   public open fun getAllClassesWithSummaries(): MutableSet<String> {
      return this.loadedClasses;
   }

   public open fun supportsClass(clazz: String): Boolean {
      return this.loadedClasses.contains(clazz);
   }

   public open fun getMethodFlows(className: String, methodSignature: String): ClassMethodSummaries? {
      val classSummaries: ClassMethodSummaries = this.getClassSummaries(className);
      return if (classSummaries != null) classSummaries.filterForMethod(methodSignature) else null;
   }

   public open fun getMethodFlows(classes: MutableSet<String>, methodSignature: String): ClassSummaries? {
      return this.classSummaries.filterForMethod(classes, methodSignature);
   }

   public open fun getClassFlows(clazz: String?): ClassMethodSummaries? {
      return this.classSummaries.getClassSummaries(clazz);
   }

   public open fun mayHaveSummaryForMethod(subsig: String): Boolean {
      return this.subsigMethodsWithSummaries.contains(subsig);
   }

   public open fun getSummaries(): ClassSummaries {
      return this.classSummaries;
   }

   public open fun isMethodExcluded(className: String?, subSignature: String?): Boolean {
      val summaries: ClassMethodSummaries = this.getClassSummaries(className);
      return summaries != null && summaries.getMethodSummaries().isExcluded(subSignature);
   }

   public open fun getClassSummaries(className: String?): ClassMethodSummaries? {
      return this.classSummaries.getClassSummaries(className);
   }

   protected open fun addSubsigsForMethod(read: MethodSummaries) {
      val flows: MultiMap = read.getFlows();
      val clears: MultiMap = read.getClears();
      if (flows != null) {
         val var10000: java.util.Set = this.subsigMethodsWithSummaries;
         val var10001: java.util.Set = flows.keySet();
         var10000.addAll(var10001);
      }

      if (clears != null) {
         val var4: java.util.Set = this.subsigMethodsWithSummaries;
         val var5: java.util.Set = clears.keySet();
         var4.addAll(var5);
      }
   }

   public open fun addMethodSummaries(newSummaries: ClassMethodSummaries) {
      synchronized (this) {
         val var10001: MethodSummaries = newSummaries.getMethodSummaries();
         this.addSubsigsForMethod(var10001);
         this.classSummaries.merge(newSummaries);
         val var10000: java.util.Set = this.loadedClasses;
         val var6: java.lang.String = newSummaries.getClassName();
         val var5: Boolean = var10000.add(var6);
      }
   }

   public suspend fun initialize() {
      val var10000: Any = PreAnalysisCoroutineScopeKt.processAIAnalysisUnits(this.getAiCheckerImpl(), this.preAnalysisImpl, `$completion`);
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   @JvmStatic
   fun `aiCheckerImpl_delegate$lambda$1`(`this$0`: MethodSummaryProvider): ModelingConfigImpl {
      return new ModelingConfigImpl(`this$0`, `this$0`.preAnalysisImpl);
   }

   @JvmStatic
   fun `logger$lambda$2`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
