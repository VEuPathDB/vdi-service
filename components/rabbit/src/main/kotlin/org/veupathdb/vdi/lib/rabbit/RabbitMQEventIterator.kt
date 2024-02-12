package org.veupathdb.vdi.lib.rabbit

import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.async.ShutdownSignal
import org.veupathdb.vdi.lib.common.async.SuspendingIterator
import vdi.component.metrics.Metrics
import kotlin.time.Duration
import com.rabbitmq.client.Channel as RChannel

private const val MAX_LOGGABLE_MESSAGE_SIZE_BYTES = 1024 * 16

class RabbitMQEventIterator<T>(
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
    var i = 0

    while (!shutdownSignal.isTriggered()) {
      if (rChan.isOpen) {
        val res = rChan.basicGet(queue, true)

        if (res != null) {
          log.debug("received a message from RabbitMQ, attempting to parse the message as a MinIO event.")

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
          if (i == 5) {
            i = 0
            log.trace("polled RabbitMQ 5x")
          } else {
            i++
          }
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

  private fun RChannel.getMessage() {

  }
}