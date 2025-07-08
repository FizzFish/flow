package cn.sast.coroutines

import kotlinx.atomicfu.atomic
import java.util.concurrent.atomic.AtomicInteger

/**
 * 简单“按需分配”对象池：总容量固定；第一次 `allocate()` 时创建元素。
 */
class OnDemandAllocatingPool<T>(
   private val maxCapacity: Int,
   private val create: (Int) -> T
) {
   /** 当前已创建元素数量（>=0）| CLOSED_FLAG */
   private val control = atomic(0)
   private val CLOSED   = Int.MIN_VALUE        // 最高位标记池已关闭
   private val elements = arrayOfNulls<Any>(maxCapacity)

   /** true 表示池已关闭，不能再创建 */
   private val Int.isClosed get() = this and CLOSED != 0
   private val Int.count    get() = this and CLOSED.inv()

   /**
    * 若未超过 [maxCapacity]，创建并缓存新元素。
    * @return `true` 代表成功或已创建过；`false` 代表已关闭
    */
   fun allocate(): Boolean {
      while (true) {
         val cur = control.value
         if (cur.isClosed) return false
         if (cur.count >= maxCapacity) return true   // 已满

         if (control.compareAndSet(cur, cur + 1)) {
            elements[cur] = create(cur)
            return true
         }
      }
   }

   /**
    * 关闭并取出全部已创建对象，后续 *allocate()* 失效。
    */
   fun close(): List<T> {
      var created = 0
      while (true) {
         val cur = control.value
         created = cur.count
         if (control.compareAndSet(cur, cur or CLOSED)) break
      }
      @Suppress("UNCHECKED_CAST")
      return (0 until created).mapNotNull { elements[it] as T? }
   }

   override fun toString(): String = buildString {
      val c = control.value
      append("OnDemandAllocatingPool(size=${c.count}/${maxCapacity}")
      if (c.isClosed) append(", closed")
      append(')')
   }
}

const val IS_CLOSED_MASK: Int = Int.MIN_VALUE