package cn.sast.common

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import mu.KotlinLogging
import java.util.*

/**
 * 已弃用：请用 MultiWorkerQueue 替代。
 */
@Deprecated(
   message = "Use MultiWorkerQueue instead.",
   replaceWith = ReplaceWith("MultiWorkerQueue")
)
class TaskQueue<T>(
   private val name: String,
   private val numberThreads: Int = maxOf(OS.maxThreadNum - 1, 1),
   private val action: suspend (T, Int) -> Unit
) {

   private val queue = Channel<T>(numberThreads * 2)

   /** 添加单个任务 */
   fun addTask(taskData: T, isLast: Boolean = false) {
      queue.trySendBlocking(taskData).getOrThrow()
      if (isLast) queue.close()
   }

   /** 批量添加任务 */
   fun addTask(taskData: Iterable<T>) {
      for (t in taskData) addTask(t)
   }

   /** 明确标记任务发送结束 */
   fun addTaskFinished() {
      queue.close()
   }

   /** 启动多线程消费者执行任务 */
   fun runTask(): Job {
      val scope = CoroutineScope(Dispatchers.Default)
      val jobs = List(numberThreads) { i ->
         scope.launch(CoroutineName("$name-$i")) {
            for (taskData in queue) {
               action(taskData, i)
            }
         }
      }

      // 等待所有消费者完成
      return scope.launch(CoroutineName("$name-joinAll")) {
         jobs.joinAll()
      }
   }
}
private val logger = KotlinLogging.logger {}
suspend fun runInMilliSeconds(
   job: Job,
   milliSeconds: Long,
   name: String,
   timeoutAction: () -> Unit
) {
   val start = System.currentTimeMillis()
   val timer = Timer()

   timer.schedule(object : TimerTask() {
      override fun run() {
         runBlocking {
            if (job.isActive) {
               logger.warn { "$name runInMilliSeconds timeout" }
               val cancelStart = System.currentTimeMillis()
               job.cancelAndJoin()
               val cancelEnd = System.currentTimeMillis()
               if (cancelEnd - cancelStart > 1000) {
                  logger.warn { "$name runInMilliSeconds cancelAndJoin takes ${cancelEnd - cancelStart}" }
               }
               timeoutAction()
            }
         }
      }
   }, milliSeconds)

   job.join()
   timer.cancel()

   val end = System.currentTimeMillis()
   if ((end - start - milliSeconds).toDouble() / milliSeconds > 0.1) {
      logger.warn { "$name runInMilliSeconds cost more than expected expect=$milliSeconds, actual=${end - start}" }
   }
}
