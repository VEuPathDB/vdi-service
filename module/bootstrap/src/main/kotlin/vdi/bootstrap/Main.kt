package vdi.bootstrap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.concurrent.locks.ReentrantLock
import java.util.jar.Manifest
import kotlin.concurrent.thread
import kotlin.concurrent.withLock
import kotlin.system.exitProcess
import vdi.daemon.events.routing.EventRouter
import vdi.daemon.pruner.DatasetPruner
import vdi.daemon.reconciler.Reconciler
import vdi.lane.hard_delete.HardDeleteLane
import vdi.lane.imports.ImportLane
import vdi.lane.install.InstallDataLane
import vdi.lane.meta.UpdateMetaLane
import vdi.lane.reconciliation.ReconciliationLane
import vdi.lane.sharing.ShareLane
import vdi.lane.soft_delete.SoftDeleteLane
import vdi.lib.config.loadAndCacheStackConfig
import vdi.lib.config.loadManifestConfig
import vdi.lib.db.cache.patchMetadataTable
import vdi.lib.modules.VDIModule
import vdi.module.sleeper.AwaitDependencies
import vdi.service.rest.RestService

object Main {
  private val log = LoggerFactory.getLogger(javaClass)

  init {
    java.util.logging.LogManager.getLogManager().readConfiguration("""
      handlers = org.slf4j.bridge.SLF4JBridgeHandler
      .level = SEVERE
    """.trimIndent().byteInputStream())
  }

  @JvmStatic
  fun main(args: Array<String>) {
    val manifest = loadManifestConfig()
    val config = loadAndCacheStackConfig()
    log.info("================================================================")
    log.info("Starting VDI Service Version: {}", manifest.gitTag)

    log.info("awaiting external dependencies")
    runBlocking { AwaitDependencies(config) }

    log.info("initializing modules")
    val modules = listOf(
      EventRouter(config.vdi, ::fatality),
      HardDeleteLane(config.vdi, ::fatality),
      ImportLane(config.vdi, ::fatality),
      InstallDataLane(config.vdi, ::fatality),
      DatasetPruner(config.vdi.daemons?.pruner, ::fatality),
      ShareLane(config.vdi, ::fatality),
      SoftDeleteLane(config.vdi, ::fatality),
      UpdateMetaLane(config.vdi, ::fatality),
      Reconciler(config.vdi.daemons?.reconciler, ::fatality),
      ReconciliationLane(config.vdi, ::fatality),
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
        RestService(config, manifest).main(args)
        serviceLock.withLock {
          unlockCondition.await()
        }
      } catch (e: Throwable) {
        log.error("rest service startup failed", e)
        exitProcess(1)
      }
    }

    log.info("starting background modules")
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
