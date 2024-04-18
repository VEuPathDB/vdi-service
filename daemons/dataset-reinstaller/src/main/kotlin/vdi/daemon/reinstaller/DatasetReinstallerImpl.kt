package vdi.daemon.reinstaller

import org.slf4j.LoggerFactory
import vdi.component.metrics.Metrics
import vdi.component.modules.AbstractJobExecutor
import kotlin.time.Duration.Companion.milliseconds
import vdi.component.reinstaller.DatasetReinstaller as Reinstaller

internal class DatasetReinstallerImpl(private val config: DatasetReinstallerConfig)
  : DatasetReinstaller
  , AbstractJobExecutor("dataset-reinstaller", config.wakeInterval)
{
  private val log = LoggerFactory.getLogger(javaClass)

  private var lastRun = 0.milliseconds

  override suspend fun runJob() {
    val now = 0.milliseconds

    if (lastRun + config.runInterval < now) {
      log.info("attempting to start automatic dataset reinstaller run")

      val timer = Metrics.Reinstaller.reinstallationTimes.startTimer()

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

  @Suppress("NOTHING_TO_INLINE")
  private inline fun now() = System.currentTimeMillis().milliseconds
}