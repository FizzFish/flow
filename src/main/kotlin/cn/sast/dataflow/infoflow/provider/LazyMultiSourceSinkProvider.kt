package cn.sast.dataflow.infoflow.provider

import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinition
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider

class LazyMultiSourceSinkProvider : ISourceSinkDefinitionProvider {

   private val lazyProviders: MutableList<Lazy<ISourceSinkDefinitionProvider>> = ArrayList()

   fun add(provider: () -> ISourceSinkDefinitionProvider): Boolean {
      return lazyProviders.add(lazy(provider))
   }

   override fun getSources(): MutableCollection<out ISourceSinkDefinition> {
      val result = ArrayList<ISourceSinkDefinition>()
      for (lazyProvider in lazyProviders) {
         result.addAll(lazyProvider.value.sources)
      }
      return result
   }

   override fun getSinks(): MutableCollection<out ISourceSinkDefinition> {
      val result = ArrayList<ISourceSinkDefinition>()
      for (lazyProvider in lazyProviders) {
         result.addAll(lazyProvider.value.sinks)
      }
      return result
   }

   override fun getAllMethods(): MutableCollection<out ISourceSinkDefinition> {
      val result = ArrayList<ISourceSinkDefinition>()
      for (lazyProvider in lazyProviders) {
         result.addAll(lazyProvider.value.allMethods)
      }
      return result
   }

   fun size(): Int = lazyProviders.size

   fun isEmpty(): Boolean = lazyProviders.isEmpty()

   override fun toString(): String = "MultiSourceSinkProvider(${lazyProviders.size})"
}
