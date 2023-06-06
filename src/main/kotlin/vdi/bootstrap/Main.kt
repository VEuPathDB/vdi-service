package vdi.bootstrap

import org.slf4j.LoggerFactory
import org.veupathdb.service.vdi.RestService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.veupathdb.vdi.lib.reconciler.Reconciler
import vdi.module.delete.hard.HardDeleteTriggerHandler
import vdi.module.events.routing.EventRouter
import vdi.module.handler.delete.soft.SoftDeleteTriggerHandler
import vdi.module.handler.imports.triggers.ImportTriggerHandler
import vdi.module.handler.install.data.InstallDataTriggerHandler
import vdi.module.handler.meta.triggers.UpdateMetaTriggerHandler
import vdi.module.handler.share.trigger.ShareTriggerHandler
import vdi.module.pruner.PrunerModule
import vdi.module.reinstaller.DatasetReinstaller

object Main {

  private val log = LoggerFactory.getLogger(javaClass)

  @JvmStatic
  fun main(args: Array<String>) {
    log.info("initializing modules")
    val modules = listOf(
      DatasetReinstaller(),
      EventRouter(),
      HardDeleteTriggerHandler(),
      ImportTriggerHandler(),
      InstallDataTriggerHandler(),
      PrunerModule(),
      ShareTriggerHandler(),
      SoftDeleteTriggerHandler(),
      UpdateMetaTriggerHandler(),
      Reconciler()
    )

    Runtime.getRuntime().addShutdownHook(Thread {
      log.info("shutting down modules")
      runBlocking {
        for (module in modules)
          launch { module.stop() }
      }
    })

    log.info("starting modules")
    runBlocking(Dispatchers.Unconfined) {
      for (module in modules)
        launch { module.start() }

      RestService.main(args)
    }
  }
}
