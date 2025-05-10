package vdi.lib.rabbit

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.seconds

private const val MaxChannelRetries = 5
private val RetryDelay = 1.seconds

internal class RabbitInstance(private val con: Connection) {
  private val log = LoggerFactory.getLogger(javaClass)

  private var rChan = runBlocking { retryChannel() }

  suspend fun getChannel(): Channel {
    if (rChan.isOpen)
      return rChan

    return retryChannel().also { rChan = it }
  }

  fun close() {
    try { rChan.close() } catch (e: Throwable) { log.error("encountered error while closing rabbitmq channel", e) }
    try { con.close() } catch (e: Throwable) { log.error("encountered error while closing rabbitmq connection", e) }
  }

  private suspend fun retryChannel(attempt: Int = 1): Channel {
    if (!con.isOpen)
      throw RabbitConnectionClosedError("could not re-open closed channel, base connection is closed")
    else if (attempt > MaxChannelRetries)
      throw Exception("could not re-open closed channel after $MaxChannelRetries attempts")
    else
      return coroutineScope {
        withContext(Dispatchers.IO) {
          log.info("attempt $attempt/$MaxChannelRetries to reopen rabbitmq channel")
          try {
            con.createChannel()
          } catch (e: Throwable) {
            log.warn("failed to open a new channel on attempt $attempt/$MaxChannelRetries, retrying in $RetryDelay", e)
            delay(RetryDelay)
            retryChannel(attempt + 1)
          }
        }
      }
  }
}
