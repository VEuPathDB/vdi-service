package vdi.components.rabbit

import com.rabbitmq.client.ConnectionFactory
import vdi.components.common.ShutdownSignal
import vdi.components.common.util.SuspendingSequence

class RabbitMQEventSource<T>(
  private val config: RabbitMQConfig,
  private val shutdownSignal: ShutdownSignal,
  private val mappingFunction: (ByteArray) -> T
) : SuspendingSequence<T> {
  private val rCon = ConnectionFactory()
    .apply {
      host = config.serverAddress.host
      port = config.serverAddress.port.toInt()
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

  override fun iterator() =
    RabbitMQEventIterator(config.queueName, rChan, config.messagePollingInterval, shutdownSignal, mappingFunction)

  override fun close() {
    rChan.close()
    rCon.close()
  }
}