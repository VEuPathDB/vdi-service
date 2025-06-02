package vdi.core.async

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.apache.logging.log4j.kotlin.logger
import org.apache.logging.log4j.kotlin.loggingContext
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.milliseconds

class WorkerPool(
  private val name: String,
  private val jobQueueSize: UByte,
  private val workerCount: UByte = 5u,
  private val reportQueueSizeChange: (Int) -> Unit = { }
) {
  private val log      = logger().delegate
  private val shutdown = Trigger()
  private val queue    = Channel<Job>(jobQueueSize.toInt())
  private val count    = CountdownLatch(workerCount.toInt())

  init {
    start()
  }

  suspend fun submit(job: Job) {
    queue.send(job)
    reportQueueSizeChange(1) // Report one more job in queue.
  }

  suspend fun stop() {
    log.debug("worker pool {} received shutdown signal", name)
    queue.close()
    shutdown.trigger()
    count.await()
    log.info("worker pool {} shutdown complete", name)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private fun start() {
    log.info("creating {} workers for pool {}", workerCount, name)

    thread {
      runBlocking(Dispatchers.IO) {
        repeat(workerCount.toInt()) { i ->
          val j = i + 1
          launch (loggingContext(mapOf("workerID" to "$name-$j"))) {
            while (!shutdown.isTriggered()) {

              if (!queue.isEmpty) {
                val job = safeReceive() ?: break
                reportQueueSizeChange(-1) // Report one less job in queue.

                try {
                  job()
                } catch (e: Throwable) {
                  log.error("job failed with exception:", e)
                }
              } else {
                delay(100.milliseconds)
              }
            }

            count.decrement()
          }
        }
      }
    }
  }

  private suspend fun safeReceive() =
    try {
      queue.receive()
    } catch (e: ClosedReceiveChannelException) {
      null
    }
}

