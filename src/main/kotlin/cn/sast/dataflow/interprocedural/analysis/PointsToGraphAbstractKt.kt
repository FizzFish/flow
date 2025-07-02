package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder
import kotlin.collections.Map.Entry

public inline fun <V> IHeapValues<V>.foreach(transform: (CompanionV<V>) -> Unit) {
   for (CompanionV e : $this$foreach) {
      transform.invoke(e);
   }
}

public inline fun <K, V> Map<out K, IHeapValues<V>>.mapTo(destination: Builder<V>, transform: (Entry<K, IHeapValues<V>>) -> IHeapValues<V>): Builder<V> {
   for (java.util.Map.Entry item : $this$mapTo.entrySet()) {
      destination.add(transform.invoke(item) as IHeapValues);
   }

   return destination;
}
