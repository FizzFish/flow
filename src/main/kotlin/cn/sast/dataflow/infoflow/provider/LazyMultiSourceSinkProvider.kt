package cn.sast.dataflow.infoflow.provider

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinition
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider

@SourceDebugExtension(["SMAP\nLazyMultiSourceSinkProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LazyMultiSourceSinkProvider.kt\ncn/sast/dataflow/infoflow/provider/LazyMultiSourceSinkProvider\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,35:1\n1454#2,5:36\n1454#2,5:41\n1454#2,5:46\n*S KotlinDebug\n*F\n+ 1 LazyMultiSourceSinkProvider.kt\ncn/sast/dataflow/infoflow/provider/LazyMultiSourceSinkProvider\n*L\n15#1:36,5\n19#1:41,5\n23#1:46,5\n*E\n"])
public class LazyMultiSourceSinkProvider : ISourceSinkDefinitionProvider {
   private final val lazyProviders: MutableList<Lazy<ISourceSinkDefinitionProvider>> = (new ArrayList()) as java.util.List

   public fun add(provider: () -> ISourceSinkDefinitionProvider): Boolean {
      return this.lazyProviders.add(LazyKt.lazy(provider));
   }

   public open fun getSources(): MutableCollection<out ISourceSinkDefinition> {
      val `$this$flatMapTo$iv`: java.lang.Iterable = this.lazyProviders;
      val `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv : $this$flatMapTo$iv) {
         val var10000: java.util.Collection = ((`element$iv` as Lazy).getValue() as ISourceSinkDefinitionProvider).getSources();
         CollectionsKt.addAll(`destination$iv`, var10000);
      }

      return `destination$iv`;
   }

   public open fun getSinks(): MutableCollection<out ISourceSinkDefinition> {
      val `$this$flatMapTo$iv`: java.lang.Iterable = this.lazyProviders;
      val `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv : $this$flatMapTo$iv) {
         val var10000: java.util.Collection = ((`element$iv` as Lazy).getValue() as ISourceSinkDefinitionProvider).getSinks();
         CollectionsKt.addAll(`destination$iv`, var10000);
      }

      return `destination$iv`;
   }

   public open fun getAllMethods(): MutableCollection<out ISourceSinkDefinition> {
      val `$this$flatMapTo$iv`: java.lang.Iterable = this.lazyProviders;
      val `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv : $this$flatMapTo$iv) {
         val var10000: java.util.Collection = ((`element$iv` as Lazy).getValue() as ISourceSinkDefinitionProvider).getAllMethods();
         CollectionsKt.addAll(`destination$iv`, var10000);
      }

      return `destination$iv`;
   }

   public fun size(): Int {
      return this.lazyProviders.size();
   }

   public fun isEmpty(): Boolean {
      return this.lazyProviders.isEmpty();
   }

   public override fun toString(): String {
      return "MultiSourceSinkProvider(${this.lazyProviders.size()})";
   }
}
