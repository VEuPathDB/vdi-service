package vdi.lib.rabbit

import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import vdi.lib.logging.logger

internal class RabbitInstanceFactory(config: RabbitMQConfig) {
  private val log = logger

  private val fac = ConnectionFactory()
    .apply {
      host = config.connection.address.host
      port = config.connection.address.port.toInt()
      username = config.connection.username
      password = config.connection.password.unwrap()
      isAutomaticRecoveryEnabled = false
      connectionTimeout = config.connection.connectionTimeout.inWholeMilliseconds.toInt()
      useNio()

      if (config.connection.useTLS) {
        useSslProtocol()
      }
    }

  private val connectionName = config.connection.connectionName

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
