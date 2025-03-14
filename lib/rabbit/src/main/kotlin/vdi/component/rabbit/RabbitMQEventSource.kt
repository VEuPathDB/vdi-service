package vdi.component.rabbit

import com.rabbitmq.client.Channel
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.util.HostAddress
import vdi.component.async.SuspendingSequence
import vdi.component.metrics.Metrics
import vdi.health.RemoteDependencies
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private const val MaxConnectionRetries = 5
private val RetryDelay = 1.seconds

class RabbitMQEventSource<T : Any>(private val config: RabbitMQConfig, mappingFunction: (ByteArray) -> T)
  : SuspendingSequence<T>
{
  private companion object {
    private val knownHosts = HashSet<HostAddress>(1)

    fun init(address: HostAddress) {
      synchronized(knownHosts) {
        if (address !in knownHosts) {
          knownHosts.add(address)
          RemoteDependencies.register("RabbitMQ ${address.host}", address.host, address.port) {
            if (Metrics.RabbitMQ.lastMessageReceived > 0)
              mapOf("lastMessage" to (System.currentTimeMillis() - Metrics.RabbitMQ.lastMessageReceived).milliseconds.toString())
            else
              emptyMap()
          }
        }
      }
    }
  }

  private val log = LoggerFactory.getLogger(javaClass)

  private val fac = RabbitInstanceFactory(config)

  private var rabbit = runBlocking { fac.newInstance() }

  private var closed = false

  private val iterator = RabbitMQEventIterator(
    config.queueName,
    ::channelProvider,
    config.messagePollingInterval,
    mappingFunction
  )

  init {
    runBlocking { initChannel() }
    init(config.serverAddress)
  }

  override fun iterator() = iterator

  override fun close() {
    closed = true
    rabbit.close()
    runBlocking { iterator.close() }
  }

  private suspend fun channelProvider(): Channel {
    if (closed)
      throw IllegalStateException("attempted to get a rabbitmq channel after the event source was closed")

    return try {
      rabbit.getChannel()
    } catch (e: RabbitConnectionClosedError) {
      log.warn(e.message)
      reconnect()
      rabbit.getChannel()
    }
  }

  private suspend fun reconnect(attempt: Int = 1) {
    if (attempt > MaxConnectionRetries)
      throw IllegalStateException("failed to re-establish connection after $MaxConnectionRetries retries")
    else
      log.info("attempt $attempt/$MaxConnectionRetries to reconnect to rabbitmq")
      try {
        rabbit = fac.newInstance()
        initChannel()
      } catch (e: Throwable) {
        log.warn("reconnect attempt $attempt/$MaxConnectionRetries failed, retrying in $RetryDelay", e)
        delay(RetryDelay)
        reconnect(attempt + 1)
      }
  }

  private suspend fun initChannel() {
    coroutineScope {
      withContext(Dispatchers.IO) {
        log.debug("declaring RabbitMQ exchange ${config.exchangeName}")
        rabbit.getChannel().exchangeDeclare(
          config.exchangeName,
          config.exchangeType,
          config.exchangeDurable,
          config.exchangeAutoDelete,
          config.exchangeArguments
        )

        log.debug("declaring RabbitMQ queue ${config.queueName}")
        rabbit.getChannel().queueDeclare(
          config.queueName,
          config.queueDurable,
          config.queueExclusive,
          config.queueAutoDelete,
          config.queueArguments,
        )

        log.debug("binding RabbitMQ queue ${config.queueName} to exchange ${config.exchangeName} with routing key ${config.routingKey}")
        rabbit.getChannel().queueBind(
          config.queueName,
          config.exchangeName,
          config.routingKey,
          config.routingArguments
        )
      }
    }
  }
}
