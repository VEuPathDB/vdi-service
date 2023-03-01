package vdi.module.events.routing.rabbit

import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import kotlinx.coroutines.delay
import vdi.components.common.ShutdownSignal
import vdi.components.json.JSON
import vdi.module.events.routing.model.MinIOEvent
import com.rabbitmq.client.Channel as RChannel

private const val MAX_LOGGABLE_MESSAGE_SIZE_BYTES = 1024 * 16

/**
 * RabbitMQ implementation of [BucketEventStream].
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
internal class RabbitMQBucketEventStream(
  private val queue: String,
  private val rChan: RChannel,
  private val pollingIntervalMillis: UInt = 500u,
  override val shutdownSignal: ShutdownSignal
) : BucketEventStream {

  private val log = LoggerFactory.getLogger(javaClass)

  private var nextValue: MinIOEvent? = null

  override suspend fun hasNext(): Boolean {
    log.trace("hasNext()")

    while (!shutdownSignal.isTriggered()) {
      if (rChan.isOpen) {
        log.trace("polling RabbitMQ for a message.")
        val res = rChan.basicGet(queue, true)

        if (res != null) {
          log.debug("received a message from RabbitMQ, attempting to parse the message as a MinIO event.")

           try {
            nextValue = JSON.readValue(res.body)
            return true
          } catch (e: Throwable) {
             log.error("message from RabbitMQ could not be parsed as a MinIO event", e)

             if (res.body.size <= MAX_LOGGABLE_MESSAGE_SIZE_BYTES)
               log.error("message was: {}", res.body.decodeToString())
             else
               log.error("message was too large to print")
          }
        } else {
          delay(pollingIntervalMillis.toLong())
        }
      } else {
        return false
      }
    }

    return false
  }

  override fun next(): MinIOEvent {
    log.trace("next()")
    return nextValue.also { nextValue = null } ?: throw NoSuchElementException()
  }
}