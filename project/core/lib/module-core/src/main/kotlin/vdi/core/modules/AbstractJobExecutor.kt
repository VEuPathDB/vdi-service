package vdi.core.modules

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class AbstractJobExecutor(
  abortCB: AbortCB,
  private val wakeInterval: Duration = 2.seconds
)
  : VDIModule
  , AbstractVDIModule(abortCB)
{
  protected open suspend fun init() {}

  protected abstract suspend fun runJob()

  protected open suspend fun close() {}

  final override suspend fun run() {
    init()

    coroutineScope {
      while (!isShutDown()) {
        delay(wakeInterval)

        if (isShutDown())
          break

        try {
          runJob()
        } catch (e: Throwable) {
          logger.error("executor run failed with exception", e)
          abortCB(e.message)
        }
      }

      logger.info("stopping module")

      close()
      confirmShutdown()
    }
  }
}
