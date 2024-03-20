package vdi.module.pruner

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import vdi.component.async.ShutdownSignal
import vdi.component.metrics.Metrics
import vdi.component.pruner.Pruner
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class PrunerModuleImpl(private val config: PrunerModuleConfig) : PrunerModule {

  private val log = LoggerFactory.getLogger(javaClass)

  private val shutdownTrigger = ShutdownSignal()
  private val shutdownConfirm = ShutdownSignal()

  @Volatile
  private var started = false

  override val name = "pruner"

  override suspend fun start() {
    if (!started) {
      log.info("starting S3 pruning module")

      started = true
      run()
    }
  }

  override suspend fun stop() {
    log.info("triggering S3 pruning module shutdown")
    shutdownTrigger.trigger()
    shutdownConfirm.await()
    log.info("S3 pruner shutdown confirmed")
  }

  private suspend fun run() {
    var lastPrune  = now()
    var pruneCount = 0uL

    runBlocking {
      while (!shutdownTrigger.isTriggered()) {

        // Wake up on a much shorter interval than the pruning interval to check
        // if a module shutdown has been requested.
        delay(config.wakeupInterval)

        if (shutdownTrigger.isTriggered())
          break

        val start = now()

        log.trace("testing whether we should be pruning S3")

        // If the last prune timestamp + the pruning duration is before (less
        // than) the current timestamp, then it is time to run a prune job.
        if (lastPrune + config.pruningInterval < start) {

          // Increment the prune count (used for logging the start and end of a
          // pruning job)
          pruneCount++

          log.info("attempting to start S3 pruning job {}", pruneCount)

          val end: Duration

          val timer = Metrics.Pruner.duration.startTimer()

          // Attempt to start a pruning job.  If the pruning job executed, this
          // function call will return true after pruning completion.  If a
          // pruning job was already in progress at the time this was called,
          // the method will return false.
          if (Pruner.tryPruneDatasets()) {
            // Since the pruning job executed, get a new timestamp for after the
            // job completed.
            end = now()
            log.info("automatic S3 pruning job {} completed after {}", pruneCount, end - start)
            timer.observeDuration()
          } else {
            // Since the pruning job did not execute, there's no need to get a
            // new timestamp.
            end = start
            log.info("automatic S3 pruning job {} skipped as a pruning job was already in progress", pruneCount)
          }

          // Update the last pruned marker
          lastPrune = end
        }
      }
    }

    shutdownConfirm.trigger()
  }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun now() = System.currentTimeMillis().milliseconds
}