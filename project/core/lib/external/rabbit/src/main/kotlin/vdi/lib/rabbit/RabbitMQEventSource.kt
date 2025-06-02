package vdi.lib.rabbit

import com.rabbitmq.client.Channel
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import vdi.lib.async.SuspendingSequence
import vdi.core.health.RemoteDependencies
import vdi.core.logging.logger
import vdi.core.metrics.Metrics
import vdi.model.field.HostAddress

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
              mapOf("lastMessage" to "n/a")
          }
        }
      }
    }
  }

  private val log = logger()

  private val fac = RabbitInstanceFactory(config)

  private var rabbit = runBlocking { fac.newInstance() }

  private var closed = false

  private val iterator = RabbitMQEventIterator(
    config.queue.name,
    ::channelProvider,
    config.messagePollingInterval,
    mappingFunction
  )

  init {
    runBlocking { initChannel() }
    init(config.connection.address)
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
        rabbit.getChannel().exchangeDeclare(
          config.exchange.name,
          config.exchange.type,
          config.exchange.durable,
          config.exchange.autoDelete,
          config.exchange.arguments,
        )

        rabbit.getChannel().queueDeclare(
          config.queue.name,
          config.queue.durable,
          config.queue.exclusive,
          config.queue.autoDelete,
          config.queue.arguments,
        )

        log.info("binding RabbitMQ queue '{}' to exchange '{}' using key '{}'", config.queue.name, config.exchange.name, config.routing.key)
        rabbit.getChannel().queueBind(
          config.queue.name,
          config.exchange.name,
          config.routing.key,
          config.routing.arguments,
        )
      }
    }
  }
}
