package vdi.module.sleeper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.Socket
import kotlin.time.Duration
import vdi.lib.config.StackConfig
import vdi.lib.config.vdi.CacheDBConfig
import vdi.lib.config.vdi.KafkaConfig
import vdi.lib.config.vdi.VDIConfig
import vdi.lib.err.StartupException

fun awaitDependencies(config: StackConfig) {
  val logger = LoggerFactory.getLogger("Sleeper")
}

private suspend fun awaitVDI(config: VDIConfig, timeout: Duration, logger: Logger) {
  awaitCacheDB(config.cacheDB, timeout, logger)
  awaitKafka(config.kafka, timeout, logger)
  await
}

private suspend fun awaitCacheDB(config: CacheDBConfig, timeout: Duration, logger: Logger) {
  awaitNamed("cache db", logger) {
    awaitRemote(config.server.host, config.server.port ?: 5432u, timeout)
  }
}

private suspend fun awaitKafka(config: KafkaConfig, timeout: Duration, logger: Logger) {
  awaitNamed("kafka", logger) {
    coroutineScope {
      config.servers.forEach {
        launch { awaitRemote(it.host, it.port ?: 9092u, timeout) }
      }
    }
  }
}

private inline suspend fun awaitNamed(name: String, logger: Logger, fn: () -> Unit) {
  logger.info("polling dependency {}", name)
  fn()
  logger.info("successfully polled dependency {}", name)
}

private suspend fun awaitRemote(host: String, port: UShort, timeout: Duration) {
  withContext(Dispatchers.IO) {
    val threshold = System.currentTimeMillis() + timeout.inWholeMilliseconds

    while (System.currentTimeMillis() < threshold) {
      try {
        Socket(host, port.toInt()).use {
          it.soTimeout = 500
          it.tcpNoDelay = true
          it.getOutputStream().flush()
        }

        return@withContext
      } catch (_: Throwable) {}
    }

    throw StartupException("timed out waiting for $host:$port")
  }
}
