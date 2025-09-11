package vdi.bootstrap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock
import java.util.logging.LogManager
import kotlin.concurrent.thread
import kotlin.concurrent.withLock
import kotlin.system.exitProcess
import vdi.core.config.loadAndCacheStackConfig
import vdi.core.config.loadManifestConfig
import vdi.core.modules.VDIModule
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
import vdi.logging.MetaLogger
import vdi.module.migrations.migrateInternalDatabase
import vdi.module.sleeper.AwaitDependencies
import vdi.service.rest.RestService

object Main {
  init {
    LogManager.getLogManager()
      .readConfiguration("handlers = org.slf4j.bridge.SLF4JBridgeHandler.level = SEVERE".byteInputStream())
  }

  @JvmStatic
  fun main(args: Array<String>) {
    val manifest = loadManifestConfig()
    val config = loadAndCacheStackConfig()
    MetaLogger.info("=".repeat(80))
    MetaLogger.info("starting VDI service version: {}", manifest.gitTag)

    MetaLogger.info("awaiting external dependencies")
    try {
      runBlocking { AwaitDependencies(config) }
    } catch (e: Throwable) {
      MetaLogger.error("startup exception: ", e)
      exitProcess(1)
    }

    // Apply internal database migrations
    try {
      migrateInternalDatabase(config.vdi.cacheDB)
    } catch (e: Throwable) {
      MetaLogger.error("startup exception: ", e)
      exitProcess(1)
    }

    MetaLogger.info("initializing modules")
    val modules = try {
      listOf(
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
    } catch (e: Throwable) {
      MetaLogger.error("startup exception: ", e)
      exitProcess(1)
    }

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
        MetaLogger.error("rest service startup failed", e)
        exitProcess(1)
      }
    }

    MetaLogger.info("starting background modules")
    runBlocking(Dispatchers.IO) { modules.forEach { launch {
      try {
        it.start()
      } catch (e: Throwable) {
        MetaLogger.error("vdi module startup exception", e)
        exitProcess(1)
      }
    } } }
  }

  /**
   * Log an error and shut down.
   */
  private fun fatality(message: String? = null): Nothing {
    MetaLogger.error(
      "one or more subprocesses encountered a fatal error requiring VDI be shut down"
      + if (message == null) "" else ": $message"
    )
    exitProcess(1)
  }

  private fun shutdownModules(modules: List<VDIModule>) {
    MetaLogger.info("shutting down modules")
    runBlocking { modules.forEach { launch { it.stop() } } }
    MetaLogger.info("modules shut down")
  }
}
