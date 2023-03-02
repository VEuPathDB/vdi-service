package vdi.components.rabbit

import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlinx.coroutines.delay
import vdi.components.common.ShutdownSignal
import vdi.components.common.util.SuspendingIterator
import com.rabbitmq.client.Channel as RChannel

private const val MAX_LOGGABLE_MESSAGE_SIZE_BYTES = 1024 * 16

internal class RabbitMQBucketEventStream<T>(
  private val queue: String,
  private val rChan: RChannel,
  private val pollingInterval: Duration,
  private val shutdownSignal: ShutdownSignal,
  private val mappingFunction: (ByteArray) -> T
) : SuspendingIterator<T> {

  private val log = LoggerFactory.getLogger(javaClass)

  private var nextValue: T? = null

  override suspend fun hasNext(): Boolean {
    log.trace("hasNext()")

    while (!shutdownSignal.isTriggered()) {
      if (rChan.isOpen) {
        log.trace("polling RabbitMQ for a message.")
        val res = rChan.basicGet(queue, true)

        if (res != null) {
          log.debug("received a message from RabbitMQ, attempting to parse the message as a MinIO event.")

           try {
            nextValue = mappingFunction(res.body)
            return true
          } catch (e: Throwable) {
             log.error("message from RabbitMQ could not be parsed as a MinIO event", e)

             if (res.body.size <= MAX_LOGGABLE_MESSAGE_SIZE_BYTES)
               log.error("message was: {}", res.body.decodeToString())
             else
               log.error("message was too large to print")
          }
        } else {
          delay(pollingInterval)
        }
      } else {
        return false
      }
    }

    return false
  }

  override suspend fun next(): T {
    log.trace("next()")
    return nextValue.also { nextValue = null } ?: throw NoSuchElementException()
  }
}