package cn.sast.dataflow.analysis.unused

import cn.sast.api.config.BuiltinAnalysisConfig
import cn.sast.api.config.MainConfig
import cn.sast.api.util.SootUtilsKt
import cn.sast.dataflow.analysis.IBugReporter
import cn.sast.dataflow.infoflow.provider.MethodSummaryProviderKt
import com.feysh.corax.config.builtin.checkers.DefineUnusedChecker
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import mu.KLogger
import soot.*
import soot.jimple.FieldRef
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import soot.tagkit.AbstractHost
import soot.tagkit.VisibilityAnnotationTag
import java.util.*
import kotlin.Comparator
import kotlin.coroutines.Continuation

class UnusedDetector(
   private val mainConfig: MainConfig,
   private val builtinAnalysisConfig: BuiltinAnalysisConfig,
   private val cg: CallGraph,
   private val reporter: IBugReporter
) {
   private val calleeAndSuperMethods by lazy { buildCalleeAndSuperMethods() }
   private val allBlackMethods by lazy { buildAllBlackMethods() }
   private val blackMethodPatterns: List<Regex> =
      builtinAnalysisConfig.unusedDetectorSootSigPatternBlackList.map { Regex(it) }

   private val enableUnusedMethod = mainConfig.isEnable(DefineUnusedChecker.UnusedMethod)
   private val enableUrfUnreadField = mainConfig.isEnable(DefineUnusedChecker.UrfUnreadField)

   private fun isAnnotated(host: AbstractHost): Boolean {
      return host.getTag("VisibilityAnnotationTag") as? VisibilityAnnotationTag != null
   }

   private fun buildCalleeAndSuperMethods(): Set<SootMethod> {
      val direct = cg.iterator().asSequence().map { it.tgt() }.toMutableSet()
      val indirect = direct.flatMap { tgt ->
         tgt.declaringClass.methods.filter {
            it.subSignature == tgt.subSignature && !it.isConstructor && !it.isPrivate
         }
      }
      return direct + indirect
   }

   private fun buildAllBlackMethods(): Set<SootMethod> {
      val scene = Scene.v()
      val allBlack = mutableSetOf<SootMethod>()
      builtinAnalysisConfig.unusedDetectorMethodSigBlackList.forEach { sig ->
         val matcher = cn.sast.api.util.OthersKt.methodSignatureToMatcher(sig)
            ?: error("Invalid method signature: $sig")
         matcher.matched(scene).forEach {
            allBlack += it
            allBlack += MethodSummaryProviderKt.findAllOverrideMethodsOfMethod(it)
         }
      }
      return allBlack
   }

   private fun isSkipped(method: SootMethod): Boolean {
      if (method in allBlackMethods) return true
      return blackMethodPatterns.any { it.matches(method.signature) }
   }

   private fun findUnusedField(appClass: SootClass): Set<SootField> {
      val candidates = appClass.fields
         .filter { it.isPrivate && !it.isFinal && !isAnnotated(it) }
         .toMutableSet()

      appClass.methods.forEach { m ->
         if (!m.hasActiveBody()) return emptySet()
         m.activeBody.units.forEach { u ->
            u.boxes.forEach { v ->
               if (v.value is FieldRef) {
                  candidates.remove((v.value as FieldRef).field)
               }
            }
         }
      }
      return candidates
   }

   private fun isUnused(method: SootMethod): Boolean {
      if (method.isStaticInitializer || isAnnotated(method)) return false
      if (method.isConstructor) {
         if (method.subSignature == "void <init>()") return false
         if (method.parameterCount == 1) {
            val param = method.parameterType(0).toString()
            val clazz = method.declaringClass.name
            val shortName = method.declaringClass.shortJavaStyleName
            val nested = shortName.substringAfterLast('$')
            if (clazz == "$param\$$nested") return false
         }
      }
      if (method.name.contains('$') || method.name.contains("lambda")) return false
      if (method.subSignature.endsWith("valueOf(java.lang.String)")
         || method.subSignature.endsWith("[] values()")
      ) return false
      if (!method.isStatic && (method.name.startsWith("get") || method.name.startsWith("set") || method.name.startsWith("is"))) return false
      if (method.isMain) return false
      if (cg.edgesInto(method as MethodOrMethodContext).hasNext()) return false
      if (SootUtilsKt.findMethodOrNull(method.declaringClass, method.subSignature.toString()) {
            it != method && !it.isConstructor && !it.isPrivate
         } != null
      ) return false
      if (method in calleeAndSuperMethods) return false
      if (!method.hasActiveBody()) return false
      return true
   }

   fun analyze(clazz: SootClass) {
      if (enableUnusedMethod) {
         var count = 0
         clazz.methods
            .sortedBy { it.signature }
            .filter { isUnused(it) && !isSkipped(it) }
            .take(builtinAnalysisConfig.maximumUnusedMethodReportsEachClass)
            .forEach {
               reporter.report(DefineUnusedChecker.UnusedMethod, it)
            }
      }
      if (enableUrfUnreadField) {
         var count = 0
         findUnusedField(clazz)
            .sortedBy { it.signature }
            .filter { !isSkipped(it.declaringClass.getMethodUnsafe(it.signature)) }
            .take(builtinAnalysisConfig.maximumUnusedFieldReportsEachClass)
            .forEach {
               reporter.report(DefineUnusedChecker.UrfUnreadField, it)
            }
      }
   }

   suspend fun analyze(classes: Collection<SootClass>) = coroutineScope {
      val semaphore = Semaphore(mainConfig.parallelsNum * 2)
      classes.forEach { clazz ->
         launch {
            semaphore.withPermit {
               analyze(clazz)
            }
         }
      }
   }

   companion object {
      lateinit var logger: KLogger
      fun isEnable(mainConfig: MainConfig): Boolean {
         return mainConfig.isAnyEnable(
            DefineUnusedChecker.UnusedMethod,
            DefineUnusedChecker.UrfUnreadField
         )
      }
   }
}
