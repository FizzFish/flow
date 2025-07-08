package cn.sast.coroutines.caffeine.impl

import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

/**
 * 保存 *key → 任意弱引用附属对象*，必要时可遍历清理。
 */
class WeakEntryHolder<K, V> {

   private val queue = ReferenceQueue<V>()
   private val map   = ConcurrentHashMap<Reference<V>, K>()

   fun put(key: K, value: V) {
      clean()                                 // 先清理已失效引用
      map[WeakReference(value, queue)] = key
   }

   /** 主动清理已回收条目 */
   fun clean() {
      var ref = queue.poll()
      while (ref != null) {
         map.remove(ref)
         ref = queue.poll()
      }
   }
}
