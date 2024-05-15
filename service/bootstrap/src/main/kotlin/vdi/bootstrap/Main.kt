package vdi.bootstrap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import vdi.component.modules.VDIModule
import org.veupathdb.service.vdi.RestService
import vdi.daemon.events.routing.EventRouter
import vdi.daemon.pruner.PrunerModule
import vdi.daemon.reconciler.Reconciler
import vdi.lane.delete.hard.HardDeleteTriggerHandler
import vdi.lane.delete.soft.SoftDeleteTriggerHandler
import vdi.lane.imports.ImportTriggerHandler
import vdi.lane.install.InstallDataTriggerHandler
import vdi.lane.meta.UpdateMetaTriggerHandler
import vdi.lane.reconciliation.ReconciliationEventHandler
import vdi.lane.sharing.ShareTriggerHandler
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object Main {

  private val log = LoggerFactory.getLogger(javaClass)

  @JvmStatic
  fun main(args: Array<String>) {
    log.info("initializing modules")
    val modules = listOf(
      EventRouter(::fatality),
      HardDeleteTriggerHandler(::fatality),
      ImportTriggerHandler(::fatality),
      InstallDataTriggerHandler(::fatality),
      PrunerModule(::fatality),
      ShareTriggerHandler(::fatality),
      SoftDeleteTriggerHandler(::fatality),
      UpdateMetaTriggerHandler(::fatality),
      Reconciler(::fatality),
      ReconciliationEventHandler(::fatality),
    )

    Runtime.getRuntime().addShutdownHook(Thread { shutdownModules(modules) })

    thread { RestService.main(args) }

    log.info("starting modules")
    runBlocking(Dispatchers.IO) { modules.forEach { launch { it.start() } } }
  }

  /**
   * Log an error and shut down.
   */
  private fun fatality(message: String? = null): Nothing {
    log.error(if (message == null)
      "one or more subprocesses encountered a fatal error requiring VDI be shut down"
    else
      "one or more subprocesses encountered a fatal error requiring VDI be shut down: $message")
    exitProcess(1)
  }

  private fun shutdownModules(modules: List<VDIModule>) {
    log.info("shutting down modules")
    runBlocking { modules.forEach { launch { it.stop() } } }
    log.info("modules shut down")
  }
}
