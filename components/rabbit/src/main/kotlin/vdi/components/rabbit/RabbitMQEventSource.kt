package vdi.components.rabbit

import com.rabbitmq.client.ConnectionFactory
import org.slf4j.LoggerFactory
import vdi.components.common.ShutdownSignal
import vdi.components.common.util.SuspendingSequence

class RabbitMQEventSource<T>(
  private val config: RabbitMQConfig,
  private val shutdownSignal: ShutdownSignal,
  private val mappingFunction: (ByteArray) -> T
) : SuspendingSequence<T> {
  private val log = LoggerFactory.getLogger(javaClass)

  private val rCon = ConnectionFactory()
    .apply {
      host = config.serverAddress.host
      port = config.serverAddress.port.toInt()
      username = config.serverUsername
      password = config.serverPassword.value
    }
    .let {
      if (config.serverConnectionName.isNullOrBlank()) {
        log.info("creating new unnamed RabbitMQ connection")
        it.newConnection()
      } else {
        log.info("creating new RabbitMQ connection named ${config.serverConnectionName}")
        it.newConnection(config.serverConnectionName)
      }
    }!!

  private val rChan = rCon.createChannel()

  init {
    log.debug("declaring RabbitMQ exchange ${config.exchangeName}")
    rChan.exchangeDeclare(
      config.exchangeName,
      config.exchangeType,
      config.exchangeDurable,
      config.exchangeAutoDelete,
      config.exchangeArguments
    )

    log.debug("declaring RabbitMQ queue ${config.queueName}")
    rChan.queueDeclare(
      config.queueName,
      config.queueDurable,
      config.queueExclusive,
      config.queueAutoDelete,
      config.queueArguments,
    )

    log.debug("binding RabbitMQ queue ${config.queueName} to exchange ${config.exchangeName} with routing key ${config.routingKey}")
    rChan.queueBind(
      config.queueName,
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