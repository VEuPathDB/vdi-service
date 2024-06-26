package vdi.component.plugin.client

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal object ImportCounter {
  // Cycle the ID to keep our generated IDs short.
  private const val CycleAt = UShort.MAX_VALUE

  private val mutex = Mutex()

  private var requestCounter: UShort = 0u

  fun nextIndex() =
    runBlocking {
      mutex.withLock {
        if (requestCounter == CycleAt)
          requestCounter = 0u
        else
          requestCounter++

        requestCounter
      }
    }
}