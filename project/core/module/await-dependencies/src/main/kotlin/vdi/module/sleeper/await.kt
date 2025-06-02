package vdi.module.sleeper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.Socket
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import vdi.config.raw.StackConfig
import vdi.config.raw.kafka.KafkaConfig
import vdi.config.raw.rabbit.RabbitConfig
import vdi.config.raw.vdi.*
import vdi.core.err.StartupException

private const val OverallTimeoutSeconds = 60

suspend fun AwaitDependencies(config: StackConfig) {
  val logger = LoggerFactory.getLogger("Sleeper")
  awaitVDI(config.vdi, OverallTimeoutSeconds.seconds, logger)
}

private suspend fun awaitVDI(config: VDIConfig, timeout: Duration, logger: Logger) {
  coroutineScope {
    launch { awaitCacheDB(config.cacheDB, timeout, logger) }
    launch { awaitKafka(config.kafka, timeout, logger) }
    launch { awaitRabbit(config.rabbit, timeout, logger) }
    launch { awaitObjectStore(config.objectStore, timeout, logger) }
  }
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

private suspend fun awaitRabbit(config: RabbitConfig, timeout: Duration, logger: Logger) {
  awaitNamed("rabbit", logger) {
    awaitRemote(
      config.connection.server.host,
      config.connection.server.port ?: if (config.connection.tls == false) 5672u else 5671u,
      timeout
    )
  }
}

private suspend fun awaitObjectStore(config: ObjectStoreConfig, timeout: Duration, logger: Logger) {
  awaitNamed("minio", logger) {
    awaitRemote(config.server.host, config.server.port ?: 9000u, timeout)
  }
}

private inline fun awaitNamed(name: String, logger: Logger, fn: () -> Unit) {
  logger.info("polling dependency {}", name)
  fn()
  logger.info("successfully pinged dependency {}", name)
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
