package vdi.component.modules

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class AbstractJobExecutor(name: String, abortCB: AbortCB, private val wakeInterval: Duration = 2.seconds)
  : VDIModule
  , AbstractVDIModule(name, abortCB)
{
  private val log = LoggerFactory.getLogger(javaClass)

  protected open suspend fun init() {}

  protected abstract suspend fun runJob()

  protected open suspend fun close() {}

  final override suspend fun run() {
    init()

    coroutineScope {
      while (!isShutDown()) {
        log.info("pruner")
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