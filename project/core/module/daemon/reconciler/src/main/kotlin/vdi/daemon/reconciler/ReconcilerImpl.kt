package vdi.daemon.reconciler

import kotlin.time.Duration.Companion.milliseconds
import vdi.logging.logger
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractJobExecutor
import vdi.core.reconciler.Reconciler as Recon

internal class ReconcilerImpl(private val config: ReconcilerDaemonConfig, abortCB: AbortCB)
  : Reconciler
  , AbstractJobExecutor(abortCB, logger<Reconciler>())
{
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
          log.warn("full reconciler disabled by config")
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
