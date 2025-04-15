package vdi.component.rabbit

import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

internal class RabbitInstanceFactory(config: RabbitMQConfig) {
  private val log = LoggerFactory.getLogger(javaClass)

  private val fac = ConnectionFactory()
    .apply {
      host = config.serverAddress.host
      port = config.serverAddress.port.toInt()
      username = config.serverUsername
      password = config.serverPassword.unwrap()
      isAutomaticRecoveryEnabled = false
      connectionTimeout = config.connectionTimeout.inWholeMilliseconds.toInt()
      useNio()

      if (config.serverUseTLS) {
        useSslProtocol()
      }
    }

  private val connectionName = config.serverConnectionName

  suspend fun newInstance(): RabbitInstance {
    return coroutineScope {
      withContext(Dispatchers.IO) {
        val con = if (connectionName.isNullOrBlank()) {
          log.info("creating new unnamed RabbitMQ connection to ${fac.host}:${fac.port}")
          fac.newConnection()
        } else {
          log.info("creating new RabbitMQ connection named $connectionName to ${fac.host}:${fac.port}")
          fac.newConnection(connectionName)
        }

        RabbitInstance(con)
      }
    }
  }
}