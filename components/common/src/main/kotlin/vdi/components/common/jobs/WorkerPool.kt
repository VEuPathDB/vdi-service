package vdi.components.common.jobs

import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vdi.components.common.ShutdownSignal

class WorkerPool(
  val jobQueueSize: Int,
  val workerCount: Int = 5,
) {
  private val shutdown = ShutdownSignal()
  private val queue    = Channel<Job>(jobQueueSize)
  private val count    = CountdownLatch(workerCount)

  fun start(ctx: CoroutineScope) {
    repeat(workerCount) {
      ctx.launch {
        while (!shutdown.isTriggered()) {
          queue.tryReceive()
            .getOrNull()
            ?.invoke()
            ?: delay(100.milliseconds)
        }

        count.decrement()
      }
    }
  }

  suspend fun submit(job: Job) {
    queue.send(job)
  }

  suspend fun stop() {
    shutdown.trigger()
    count.await()
  }
}

