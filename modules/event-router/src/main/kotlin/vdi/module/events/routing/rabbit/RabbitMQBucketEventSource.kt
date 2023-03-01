package vdi.module.events.routing.rabbit

import com.rabbitmq.client.ConnectionFactory
import vdi.components.common.ShutdownSignal
import vdi.module.events.routing.config.EventRouterRabbitConfig

/**
 * RabbitMQ implementation for [BucketEventSource].
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
internal class RabbitMQBucketEventSource(
  private val config: EventRouterRabbitConfig
) : BucketEventSource {
  private val rCon = ConnectionFactory()
    .apply {
      host = config.serverHost
      port = config.serverPort.toInt()
      username = config.serverUsername
      password = config.serverPassword.value
    }
    .let {
      if (config.serverConnectionName.isNullOrBlank())
        it.newConnection()
      else
        it.newConnection(config.serverConnectionName)
    }!!

  private val rChan = rCon.createChannel()

  var pollingIntervalMillis: UInt = 500u

  init {
    rChan.exchangeDeclare(
      config.exchangeName,
      config.exchangeType,
      config.exchangeDurable,
      config.exchangeAutoDelete,
      config.exchangeArguments
    )

    rChan.queueDeclare(
      config.queueName,
      config.queueDurable,
      config.queueExclusive,
      config.queueAutoDelete,
      config.queueArguments,
    )

    rChan.queueBind(config.queueName,
      config.exchangeName,
      config.routingKey,
      config.routingArgs
    )
  }

  override fun stream(shutdownSignal: ShutdownSignal) =
    RabbitMQBucketEventStream(config.queueName, rChan, pollingIntervalMillis, shutdownSignal)

  override fun close() {
    rChan.close()
    rCon.close()
  }
}