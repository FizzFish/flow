package cn.sast.coroutines

import cn.sast.common.OS
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.concurrent.thread

/**
 * *线程版* 工作队列：按需创建 [Thread]（而非协程）处理任务。
 *
 * @param workersCount 最大线程数，默认=CPU-1
 * @param action  具体任务逻辑 (suspend)
 */
class MultiWorkerQueue<T>(
   private val name: String,
   private val workersCount: Int = maxOf(OS.maxThreadNum - 1, 1),
   private val action: suspend (T) -> Unit
) : AutoCloseable {

   private val tasks   = Channel<T>(Channel.UNLIMITED)
   private val pool    = OnDemandAllocatingPool(workersCount) { idx -> spawnWorker(idx) }

   /** 正在运行或排队的任务数，用于 join()  */
   private val active  = atomic(0)

   /* ─────────────────────────── Public API ─────────────────────────── */

   /** 提交单个任务 */
   fun dispatch(task: T) {
      check(!tasks.isClosedForSend) { "$name already closed" }
      active.incrementAndGet()
      pool.allocate()                      // 确保至少有 1 个线程
      tasks.trySend(task).getOrThrow()
   }

   /** 批量提交 */
   fun dispatch(tasks: Iterable<T>) = tasks.forEach(::dispatch)

   /** 关闭队列：不再接受新任务、等待现有任务结束 */
   override fun close() {
      tasks.close()
      pool.close().forEach { it.join() }   // 等待线程退出
   }

   /** 挂起直至所有已提交任务完成（队列可继续 dispatch） */
   suspend fun join() = withContext(Dispatchers.IO) {
      while (active.value != 0) delay(10)
   }

   /* ─────────────────────────── Worker Thread ───────────────────────── */

   private fun spawnWorker(idx: Int): Thread =
      thread(
         name = "$name-$idx",
         start = true,
         isDaemon = true
      ) {
         runBlocking {
            for (task in tasks) {
               runCatching { action(task) }
                  .onFailure { println("[$name] Worker error: $it") }
               active.decrementAndGet()
            }
         }
      }
}
