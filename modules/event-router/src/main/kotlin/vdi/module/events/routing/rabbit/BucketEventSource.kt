package vdi.module.events.routing.rabbit

import vdi.components.common.ShutdownSignal

/**
 * Represents the source from which MinIO bucket events will be streamed.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
interface BucketEventSource : AutoCloseable {

  /**
   * Returns an unbounded stream of bucket events in the form of a Kotlin
   * iterator.
   *
   * NOTE: Calling this method additional times will produce competing event
   * streams.
   *
   * @param shutdownSignal Signal that may be used to cause the iterator to halt
   * and return.
   */
  fun stream(shutdownSignal: ShutdownSignal): BucketEventStream
}
