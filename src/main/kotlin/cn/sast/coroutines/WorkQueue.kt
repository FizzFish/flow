package cn.sast.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.atomic.AtomicInteger

/**
 * *协程版* 工作队列：用固定数量协程并发消费 Channel。
 */
class WorkQueue<T>(
   private val dispatcher: CoroutineDispatcher,
   private val doWork: suspend (WorkQueue<T>, T) -> Unit
) {
   val channel: Channel<T> = Channel(Channel.UNLIMITED)

   /**
    * 启动 [n] 个协程并阻塞直到 Channel 为空且全部协程空闲。
    */
   suspend fun start(n: Int) = coroutineScope {
      val idle = AtomicInteger(0)

      repeat(n) {
         launch(dispatcher) {
            var reportedIdle = false
            for (item in channel) {
               if (reportedIdle) {
                  idle.decrementAndGet()
                  reportedIdle = false
               }
               doWork(this@WorkQueue, item)
            }
            if (!reportedIdle) idle.incrementAndGet()
         }
      }

      /* 轮询等待所有任务完成 */
      while (idle.get() < n) delay(10)
   }
}

/**
 * 顶层便捷函数：把 [initial] 任务批量塞入队列并启动 N 个协程。
 */
suspend fun <T> runWork(
   dispatcher: CoroutineDispatcher,
   n: Int,
   initial: Collection<T>,
   doWork: suspend (WorkQueue<T>, T) -> Unit
) = coroutineScope {
   val queue = WorkQueue(dispatcher, doWork)

   /* 预填任务 */
   initial.forEach { queue.channel.send(it) }
   queue.channel.close()         // 不再有新任务

   /* 启动并阻塞 */
   queue.start(n)
}