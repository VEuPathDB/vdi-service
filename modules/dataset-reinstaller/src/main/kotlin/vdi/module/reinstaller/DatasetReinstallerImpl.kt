package vdi.module.reinstaller

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.async.ShutdownSignal
import vdi.component.metrics.Metrics
import kotlin.time.Duration.Companion.milliseconds
import vdi.component.reinstaller.DatasetReinstaller as Reinstaller

internal class DatasetReinstallerImpl(private val config: DatasetReinstallerConfig) : DatasetReinstaller
{
  private val log = LoggerFactory.getLogger(javaClass)

  private val shutdownTrigger = ShutdownSignal()
  private val shutdownConfirm = ShutdownSignal()

  private var started = false

  override suspend fun start() {
    if (!started) {
      log.info("starting dataset-reinstaller module")

      started = true
      run()
    }
  }

  override suspend fun stop() {
    log.info("triggering dataset-reinstaller module shutdown")
    shutdownTrigger.trigger()
    shutdownConfirm.await()
  }

  private suspend fun run() {
    var lastRun = now()

    runBlocking {
      while (!shutdownTrigger.isTriggered()) {
        delay(config.wakeInterval)

        if (shutdownTrigger.isTriggered())
          break

        val now = now()

        if (lastRun + config.runInterval < now) {
          log.info("attempting to start automatic dataset reinstaller run")

          val timer = Metrics.reinstallationTimes.startTimer()

          if (Reinstaller.tryRun()) {
            val end = now()
            log.info("automatic dataset reinstaller run completed after {}", end - now)
            timer.observeDuration()
          } else {
            log.info("automatic dataset reinstaller run skipped as run was already in progress")
          }

          lastRun = now
        }
      }
    }
  }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun now() = System.currentTimeMillis().milliseconds
}