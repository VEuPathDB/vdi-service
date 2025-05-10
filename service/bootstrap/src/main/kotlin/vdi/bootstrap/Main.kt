package vdi.bootstrap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock
import kotlin.system.exitProcess
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
import vdi.lib.config.loadAndCacheStackConfig
import vdi.lib.db.cache.patchMetadataTable
import vdi.lib.modules.VDIModule
import vdi.service.rest.RestService

object Main {

  private val log = LoggerFactory.getLogger(javaClass)

  @JvmStatic
  fun main(args: Array<String>) {
    val config = loadAndCacheStackConfig()

    log.info("initializing modules")
    val modules = listOf(
      EventRouter(config.vdi, ::fatality),
      HardDeleteTriggerHandler(config.vdi, ::fatality),
      ImportTriggerHandler(config.vdi, ::fatality),
      InstallDataTriggerHandler(config.vdi, ::fatality),
      PrunerModule(config.vdi.daemons?.pruner, ::fatality),
      ShareTriggerHandler(config.vdi, ::fatality),
      SoftDeleteTriggerHandler(config.vdi, ::fatality),
      UpdateMetaTriggerHandler(config.vdi, ::fatality),
      Reconciler(config.vdi.daemons?.reconciler, ::fatality),
      ReconciliationEventHandler(config.vdi, ::fatality),
    )

    // FIXME: REMOVE THIS ONCE THE EXTENDED METADATA PATCH HAS BEEN APPLIED TO PRODUCTION!!!!
    patchMetadataTable()

    val serviceLock = ReentrantLock()
    val unlockCondition = serviceLock.newCondition()

    Runtime.getRuntime().addShutdownHook(Thread {
      serviceLock.withLock {
        unlockCondition.signal()
      }
      shutdownModules(modules)
    })

    thread {
      try {
        RestService(config).main(args)
        serviceLock.withLock {
          unlockCondition.await()
        }
      } catch (e: Throwable) {
        log.error("rest service startup failed", e)
        exitProcess(1)
      }
    }

    log.info("starting modules")
    runBlocking(Dispatchers.IO) { modules.forEach { launch {
      try {
        it.start()
      } catch (e: Throwable) {
        log.error("vdi module startup exception", e)
        exitProcess(1)
      }
    } } }
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
