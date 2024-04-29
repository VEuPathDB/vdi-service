package vdi.component.rabbit

import com.rabbitmq.client.Channel
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import vdi.component.async.SuspendingIterator
import vdi.component.async.Trigger
import vdi.component.metrics.Metrics
import kotlin.time.Duration

private const val MAX_LOGGABLE_MESSAGE_SIZE_BYTES = 1024 * 16

class RabbitMQEventIterator<T>(
  private val queue: String,
  private val channelProvider: suspend () -> Channel,
  private val pollingInterval: Duration,
  private val mappingFunction: (ByteArray) -> T
) : SuspendingIterator<T> {

  private val log = LoggerFactory.getLogger(javaClass)

  private val abort = Trigger()

  private var nextValue: T? = null

  /**
   * Suspends until a next value is available or an exception occurs.
   */
  override suspend fun hasNext(): Boolean {
    while (!abort.isTriggered()) {
      val res = channelProvider().basicGet(queue, true)

      if (res != null) {
         try {
          nextValue = mappingFunction(res.body)
          return true
         } catch (e: Throwable) {
           Metrics.unparseableRabbitMessage.inc()
           log.error("message from RabbitMQ could not be parsed as a MinIO event", e)

           if (res.body.size <= MAX_LOGGABLE_MESSAGE_SIZE_BYTES)
             log.error("message was: {}", res.body.decodeToString())
           else
             log.error("message was too large to print")
         }
      } else {
        delay(pollingInterval)
      }
    }

    return false
  }

  override suspend fun next(): T {
    return nextValue.also { nextValue = null } ?: throw NoSuchElementException()
  }

  internal suspend fun close() {
    abort.trigger()
  }
}
