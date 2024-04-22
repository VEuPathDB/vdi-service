package vdi.component.async

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.logging.log4j.kotlin.CoroutineThreadContext
import org.apache.logging.log4j.kotlin.ThreadContextData
import org.apache.logging.log4j.kotlin.logger
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class WorkerPool(
  private val name: String,
  private val jobQueueSize: Int,
  private val workerCount: Int = 5,
  private val reportQueueSizeChange: (Int) -> Unit = { }
) {
  private val log      = logger()
  private val shutdown = ShutdownSignal()
  private val queue    = Channel<Job>(jobQueueSize)
  private val count    = CountdownLatch(workerCount)
  private val jobs     = AtomicULong()

  @OptIn(ExperimentalCoroutinesApi::class)
  fun start() {
    log.info("starting worker pool $name with queue size $jobQueueSize and worker count $workerCount")

    runBlocking(Dispatchers.IO) {
      repeat(workerCount) { i ->
        val j = i + 1
        launch (CoroutineThreadContext(contextData = ThreadContextData(map = mapOf("workerID" to "$name-$j"), Stack()))) {
          log.debug("worker pool $name starting worker $j")

          while (!shutdown.isTriggered()) {
            if (!queue.isEmpty) {
              log.debug("worker $name-$j executing job ${jobs.incAndGet()}")
              val job = queue.receive()
              reportQueueSizeChange(-1) // Report one less job in queue.

              try {
                job()
              } catch (e: Throwable) {
                log.error("job $jobs failed with exception:", e)
              }

            } else {
              delay(100.milliseconds)
            }
          }

          log.debug("worker pool $name shutting down worker $j")
          count.decrement()
        }
      }
    }
  }

  suspend fun submit(job: Job) {
    queue.send(job)
    reportQueueSizeChange(1) // Report one more job in queue.
  }

  suspend fun stop() {
    log.debug("worker pool $name received shutdown signal")
    shutdown.trigger()
    count.await()
    log.debug("$workerCount workers halted; worker pool $name shutdown complete")
  }
}

