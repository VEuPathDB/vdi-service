package vdi.module.events.routing.rabbit

import vdi.components.common.ShutdownSignal
import vdi.module.events.routing.model.MinIOEvent

/**
 * Represents an unbounded stream of events from a [BucketEventSource].
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
interface BucketEventStream {

  /**
   * Shutdown signal that may be used to cancel an active [hasNext] call and
   * shut down this [BucketEventStream].
   */
  val shutdownSignal: ShutdownSignal

  /**
   * A suspending that causes the stream to await an event until either one is
   * received or the [shutdownSignal] is triggered.
   *
   * @return `true` when a message is available, `false` if the queue connection
   * has closed or the shutdown signal was triggered.
   */
  suspend operator fun hasNext(): Boolean

  /**
   * Returns the next available event.
   */
  operator fun next(): MinIOEvent
}

