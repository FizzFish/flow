package cn.sast.dataflow.infoflow.provider

import cn.sast.api.config.MainConfig
import cn.sast.api.config.PreAnalysisCoroutineScope
import cn.sast.api.config.PreAnalysisCoroutineScopeKt
import cn.sast.common.GLB
import cn.sast.coroutines.caffine.impl.FastCacheImpl
import cn.sast.dataflow.util.ConfigInfoLogger
import com.feysh.corax.cache.coroutines.FastCache
import com.feysh.corax.config.api.*
import com.feysh.corax.config.api.baseimpl.AIAnalysisBaseImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import mu.KLogger
import soot.Scene
import soot.SootMethod
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2

class BugTypeProvider(
   val config: MainConfig,
   val preAnalysisImpl: PreAnalysisCoroutineScope
) {
   private val methodToCheckType: MutableMap<SootMethod, MutableSet<CheckType>> = LinkedHashMap()
   private val aiCheckerImpl: AIAnalysisBaseImpl = object : AIAnalysisBaseImpl() {
      private val error = ConfigInfoLogger()
      private val preAnalysis = this@BugTypeProvider.preAnalysisImpl

      fun getFastCache(): FastCache = FastCacheImpl.INSTANCE

      fun getError(): ConfigInfoLogger = error

      fun getPreAnalysis(): PreAnalysisApi = preAnalysis

      fun getScope(): CoroutineScope = GlobalScope

      override fun addStmt(
         decl: IJDecl,
         config: Function1<in MethodConfig, Unit>,
         stmt: IStmt
      ) {
         // Implement if needed
      }

      override fun check(
         decl: IJDecl,
         config: Function1<in MethodConfig, Unit>,
         expr: IBoolExpr,
         checkType: CheckType,
         env: Function1<in BugMessage.Env, Unit>
      ) {
         GLB += checkType
         when (decl) {
            is IMethodDecl -> {
               val match = decl.match
               val scene = Scene.v()
               for (sink in match.matched(scene)) {
                  synchronized(methodToCheckType) {
                     val checkSet = methodToCheckType.getOrPut(sink) { LinkedHashSet() }
                     checkSet.add(checkType)
                  }
               }
            }
            is IClassDecl, is IFieldDecl, is ILocalVarDecl -> {
               throw NotImplementedError()
            }
            else -> throw NoWhenBranchMatchedException()
         }
      }

      override fun eval(
         decl: IJDecl,
         config: Function1<in MethodConfig, Unit>,
         expr: IExpr,
         accept: Function1<in Any, Unit>
      ) {
         // Implement if needed
      }

      override fun validate() {
         // Implement if needed
      }
   }

   fun init() = runBlocking {
      PreAnalysisCoroutineScopeKt.processAIAnalysisUnits(aiCheckerImpl, preAnalysisImpl)
   }

   fun lookUpChecker(method: SootMethod): Set<IChecker> {
      return lookUpCheckType(method).flatMap { it.checker }.toSet()
   }

   fun lookUpCheckType(method: SootMethod): Set<CheckType> {
      return methodToCheckType[method] ?: emptySet()
   }

   companion object {
      private val logger: KLogger? = null
   }
}
