@file:SourceDebugExtension(["SMAP\nPathCompanion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathCompanion.kt\ncn/sast/dataflow/interprocedural/check/PathCompanionKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,994:1\n1628#2,3:995\n*S KotlinDebug\n*F\n+ 1 PathCompanion.kt\ncn/sast/dataflow/interprocedural/check/PathCompanionKt\n*L\n643#1:995,3\n*E\n"])

package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import java.util.Collections
import java.util.HashSet
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension

public final val bindDelegate: IValue
   public final get() {
      return if (`$this$bindDelegate` is CompanionValueOfConst)
         (`$this$bindDelegate` as CompanionValueOfConst).getAttr()
         else
         `$this$bindDelegate`.getValue() as IValue;
   }


private final val short: Map<K, V>
   private final get() {
      var var10000: java.util.Map;
      switch ($this$short.size()) {
         case 0:
            val var6: java.util.Map = Collections.emptyMap();
            var10000 = var6;
            break;
         case 1:
            val var3: Entry = `$this$short`.entrySet().iterator().next() as Entry;
            val var2: java.util.Map = Collections.singletonMap(var3.getKey(), var3.getValue());
            var10000 = var2;
            break;
         default:
            var10000 = `$this$short`;
      }

      return var10000;
   }


private final val short: Set<E>
   private final get() {
      var var10000: java.util.Set;
      switch ($this$short.size()) {
         case 0:
            val var4: java.util.Set = Collections.emptySet();
            var10000 = var4;
            break;
         case 1:
            val var2: java.util.Set = Collections.singleton(`$this$short`.iterator().next());
            var10000 = var2;
            break;
         default:
            var10000 = `$this$short`;
      }

      return var10000;
   }


public fun IHeapValues<IValue>.path(env: HeapValuesEnv): IPath {
   val var10000: MergePath.Companion = MergePath.Companion;
   val `$this$mapTo$iv`: java.lang.Iterable = `$this$path`.getValuesCompanion() as java.lang.Iterable;
   val `destination$iv`: java.util.Collection = new HashSet(`$this$path`.getValuesCompanion().size());

   for (Object item$iv : $this$mapTo$iv) {
      val it: CompanionV = `item$iv` as CompanionV;
      `destination$iv`.add((it as PathCompanionV).getPath());
   }

   return var10000.v(env, `destination$iv` as MutableSet<IPath>);
}

@JvmSynthetic
fun `access$getShort`(`$receiver`: java.util.Map): java.util.Map {
   return getShort(`$receiver`);
}
