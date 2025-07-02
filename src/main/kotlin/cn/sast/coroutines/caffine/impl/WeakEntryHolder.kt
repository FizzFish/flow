package cn.sast.coroutines.caffine.impl

import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

public class WeakEntryHolder<K, V> {
   private final val referenceQueue: ReferenceQueue<Any> = new ReferenceQueue()
   private final val backingMap: MutableMap<Reference<out Any>, Any>

   public fun put(key: Any, value: Any) {
      this.clean();
      this.backingMap.put(new WeakReference<>((V)value, this.referenceQueue), (K)key);
   }

   public fun clean() {
      for (Reference ref = this.referenceQueue.poll(); ref != null; ref = this.referenceQueue.poll()) {
         this.backingMap.remove(ref);
      }
   }
}
