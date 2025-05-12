package vdi.lib.modules

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.slf4j.Logger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class AbstractJobExecutor(
  name: String,
  abortCB: AbortCB,
  log: Logger,
  private val wakeInterval: Duration = 2.seconds
)
  : VDIModule
  , AbstractVDIModule(name, abortCB, log)
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
          log.error("executor $name run failed with exception", e)
          abortCB(e.message)
        }
      }

      log.info("stopping module {}", name)

      close()
      confirmShutdown()
    }
  }
}
