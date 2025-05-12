package vdi.daemon.reconciler

import org.apache.logging.log4j.kotlin.logger
import kotlin.time.Duration.Companion.milliseconds
import vdi.lib.modules.AbortCB
import vdi.lib.modules.AbstractJobExecutor
import vdi.lib.reconciler.Reconciler as Recon

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
    val now = System.currentTimeMillis()

    when {
      // delta between last full run and now is greater than or equal to the
      // full reconciler run interval
      (now - lastFullRun).milliseconds >= config.fullRunInterval -> {
        lastFullRun = now

        // If the reconciler thread is disabled, just log a reminder.
        if (!config.reconcilerEnabled) {
          logger.info("full reconciler disabled by config")
        } else {
          Recon.runFull()
        }
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
