package vdi.component.modules

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class AbstractJobExecutor(name: String, private val wakeInterval: Duration = 2.seconds)
  : VDIModule
  , AbstractVDIModule(name)
{
  private val log = LoggerFactory.getLogger(javaClass)

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

        run()
      }
    }

    log.info("stopping module {}", name)

    close()

    confirmShutdown()
  }
}