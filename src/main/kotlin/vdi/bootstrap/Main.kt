package vdi.bootstrap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.slf4j.bridge.SLF4JBridgeHandler
import org.veupathdb.service.vdi.RestService
import vdi.daemon.events.routing.EventRouter
import vdi.daemon.pruner.PrunerModule
import vdi.daemon.reconciler.Reconciler
import vdi.daemon.reinstaller.DatasetReinstaller
import vdi.lane.delete.hard.HardDeleteTriggerHandler
import vdi.lane.delete.soft.SoftDeleteTriggerHandler
import vdi.lane.imports.ImportTriggerHandler
import vdi.lane.install.InstallDataTriggerHandler
import vdi.lane.meta.UpdateMetaTriggerHandler
import vdi.lane.reconciliation.ReconciliationEventHandler
import vdi.lane.sharing.ShareTriggerHandler
import kotlin.concurrent.thread

object Main {

  private val log = LoggerFactory.getLogger(javaClass)

  @JvmStatic
  fun main(args: Array<String>) {
    // JUL -> SLF4J
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()

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
      Reconciler(),
      ReconciliationEventHandler(),
    )

    Runtime.getRuntime().addShutdownHook(Thread {
      log.info("shutting down modules")
      runBlocking {
        for (module in modules)
          launch {
            module.stop()
          }
      }
      log.info("modules shut down")
    })

    thread { RestService.main(args) }

    log.info("starting modules")
    runBlocking(Dispatchers.Unconfined) {
      for (module in modules)
        launch { module.start() }
    }
  }
}
