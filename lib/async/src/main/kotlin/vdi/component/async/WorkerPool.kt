package vdi.component.async

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.apache.logging.log4j.kotlin.CoroutineThreadContext
import org.apache.logging.log4j.kotlin.ThreadContextData
import org.apache.logging.log4j.kotlin.logger
import org.veupathdb.vdi.lib.common.util.AtomicULong
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.milliseconds

class WorkerPool(
  private val name: String,
  private val jobQueueSize: UInt,
  private val workerCount: UInt = 5u,
  private val reportQueueSizeChange: (Int) -> Unit = { }
) {
  private val log      = logger().delegate
  private val shutdown = Trigger()
  private val queue    = Channel<Job>(jobQueueSize.toInt())
  private val count    = CountdownLatch(workerCount.toInt())
  private val jobs     = AtomicULong()

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
    log.debug("{} workers halted; worker pool {} shutdown complete", workerCount, name)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private fun start() {
    log.info("starting worker pool {} with queue size {} and worker count {}", name, jobQueueSize, workerCount)

    thread {
      runBlocking(Dispatchers.IO) {
        repeat(workerCount.toInt()) { i ->
          val j = i + 1
          launch (CoroutineThreadContext(ThreadContextData(mapOf("workerID" to "$name-$j")))) {
            while (!shutdown.isTriggered()) {
              val jobNumber = jobs.incAndGet()

              if (!queue.isEmpty) {
                log.debug("executing job {}", jobNumber)
                val job = safeReceive() ?: break
                reportQueueSizeChange(-1) // Report one less job in queue.

                try {
                  job()
                } catch (e: Throwable) {
                  log.error("job $jobNumber failed with exception:", e)
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

