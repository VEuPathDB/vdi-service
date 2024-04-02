package vdi.component.async

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.logging.log4j.kotlin.logger

import kotlinx.coroutines.Job as Coroutine

@OptIn(DelicateCoroutinesApi::class)
class WorkerPool(
  private val name: String,
  queueSize: UInt,
  workerCount: UInt,
  dispatch: CoroutineDispatcher = CachedDispatcher,
) {
  private val jobCount = AtomicULong()

  private val queueCount = ObservableIntImpl()

  private val signal = Signal()

  private val workers: List<Coroutine>

  private val queue: Channel<Job>

  private val pool: Coroutine

  val queueSize: ObservableInt
    get() = queueCount

  init {
    if (queueSize < 1u)
      throw IllegalArgumentException("attempted to create an WorkerPool instance with a queue size of $queueSize")
    if (workerCount < 1u)
      throw IllegalArgumentException("attempted to create an WorkerPool instance with a worker count of $workerCount")


    logger().delegate.info(
      "starting worker pool {} with queue size {} and worker count {}",
      name,
      queueSize,
      workerCount,
    )

    queue = Channel(queueSize.toInt())
    workers = ArrayList(workerCount.toInt())

    pool = GlobalScope.launch(CachedDispatcher) {
      repeat(workerCount.toInt()) { i -> workers.add(launch(dispatch) { Worker("$name-${i+1}") }) }
    }
  }

  suspend fun submit(job: Job) {
    queueCount.inc()
    queue.send(job)
  }

  suspend fun stop() {
    val log = logger().delegate
    log.info("stopping worker pool {}", name)

    signal.trigger()

    queue.close()

    for (worker in workers)
      worker.join()

    pool.join()

    log.info("worker pool {} stopped", name)
  }

  inner class Worker(private val name: String) {
    private val log = logger()

    suspend operator fun invoke() {
      log.delegate.debug("starting worker {}", name)

      for (job in queue) {
        log.delegate.debug("worker {} executing job {}", name, jobCount.incAndGet())
        queueCount.dec()

        if (signal.isTriggered())
          break

        job()

        if (signal.isTriggered())
          break
      }

      log.delegate.debug("worker {} stopping", name)
    }
  }
}
