package vdi.daemon.reconciler

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.apache.logging.log4j.kotlin.logger
import vdi.component.modules.AbortCB
import vdi.component.modules.AbstractJobExecutor
import vdi.lib.reconciler.Reconciler as Recon
import kotlin.time.Duration.Companion.milliseconds

internal class ReconcilerImpl(private val config: ReconcilerDaemonConfig, abortCB: AbortCB)
  : Reconciler
  , AbstractJobExecutor("reconciler", abortCB)
{
  private val logger = logger().delegate

  private var lastSlimRun = 0L

  private var lastFullRun = 0L

  init {
    Recon.initialize(abortCB)
  }

  override suspend fun runJob() {
    // If the reconciler thread is disabled, just log a reminder every once in a
    // while.
    if (!config.reconcilerEnabled) {
      coroutineScope {
        logger.info("reconciler disabled by config")
        delay(config.fullRunInterval)
      }
      return
    }

    val now = System.currentTimeMillis()

    when {
      // delta between last full run and now is greater than or equal to the
      // full reconciler run interval
      (now - lastFullRun).milliseconds >= config.fullRunInterval -> {
        lastFullRun = now
        Recon.runFull()
      }

      // delta between last slim run and now is greater than or equal to the
      // slim reconciler run interval
      (now - lastSlimRun).milliseconds >= config.slimRunInterval -> {
        lastSlimRun = now
        Recon.runSlim()
      }
    }
  }
}