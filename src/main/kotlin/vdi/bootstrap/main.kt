package vdi.bootstrap

import org.slf4j.LoggerFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vdi.components.common.SecretString
import vdi.components.common.VDIServiceModule
import vdi.module.events.routing.EventRouter
import vdi.module.events.routing.config.EventRouterConfig
import vdi.module.events.routing.config.EventRouterRabbitConfig


object Main {

  private val log = LoggerFactory.getLogger(javaClass)

  // FIXME: This is for testing only!!!
  private val eventRouterConfig = EventRouterConfig(
    EventRouterRabbitConfig(
      serverUsername = "someUser",
      serverPassword = SecretString("somePassword"),
      exchangeName = "vdi-bucket-notifications",
      queueName = "vdi-bucket-notifications",
      routingKey = "vdi-bucket-notifications",
    )
  )

  @JvmStatic
  fun main(args: Array<String>) {
    log.info("initializing modules")
    val modules = listOf<VDIServiceModule>(
      EventRouter(eventRouterConfig)
    )

    Runtime.getRuntime().addShutdownHook(Thread {
      log.info("shutting down modules")
      runBlocking {
        for (module in modules)
          launch { module.stop() }
      }
    })

    log.info("starting modules")
    runBlocking {
      for (module in modules)
        launch { module.start() }
    }
  }
}