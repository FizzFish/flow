package cn.sast.dataflow.infoflow.provider

import cn.sast.api.config.MainConfig
import cn.sast.api.config.PreAnalysisCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import soot.SootMethod
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinition
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider
import java.util.concurrent.ConcurrentHashMap

/**
 * 负责把外部生成的 Source/Sink 定义汇总给 FlowDroid。
 */
class SourceSinkProvider(
   val mainConfig: MainConfig,
   private val preAnalysisImpl: PreAnalysisCoroutineScope
) : ISourceSinkDefinitionProvider {

   /** 方法 → Source 描述 */
   val sourceDefinitions = ConcurrentHashMap<SootMethod, MethodModel>()

   /** 方法 → Sink 描述 */
   val sinkDefinitions   = ConcurrentHashMap<SootMethod, MethodModel>()

   /** Sink 端出现过的 CheckType（可用于按需裁剪） */
   val checkTypesInSink  = mutableSetOf<Any>()

   /** 统一暴露给 FlowDroid 的集合 */
   private val sourceSet = mutableSetOf<ISourceSinkDefinition>()
   private val sinkSet   = mutableSetOf<ISourceSinkDefinition>()
   private val allMethods = mutableSetOf<ISourceSinkDefinition>()

   /** 记录找不到字节码的类，避免重复尝试 */
   private val missClass = mutableSetOf<String>()

   /** 如需异步预分析，可在这里实现 */
   suspend fun initialize(scope: CoroutineScope = GlobalScope) {
      // TODO: 填充 Source/Sink 的实际构造逻辑
   }

   /* ---------- ISourceSinkDefinitionProvider 实现 ---------- */

   override fun getSources(): MutableCollection<out ISourceSinkDefinition> = sourceSet

   override fun getSinks(): MutableCollection<out ISourceSinkDefinition> = sinkSet

   override fun getAllMethods(): MutableCollection<out ISourceSinkDefinition> = allMethods

   /* ---------- 内部模型 ---------- */

   data class MethodModel(
      val method: SootMethod,
      val callType: String      // 仅作示例，可根据需求扩展
   )
}
