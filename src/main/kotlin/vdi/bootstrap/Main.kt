package vdi.bootstrap

import org.slf4j.LoggerFactory
import org.veupathdb.service.vdi.RestService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vdi.module.events.routing.EventRouter
import vdi.module.handler.imports.triggers.ImportTriggerHandler


object Main {

  private val log = LoggerFactory.getLogger(javaClass)

  @JvmStatic
  fun main(args: Array<String>) {
    log.info("initializing modules")
    val modules = listOf(
      EventRouter(),
      ImportTriggerHandler()
    )

    val restService = Thread { RestService.main(args) }

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

      restService.start()
    }
  }
}
