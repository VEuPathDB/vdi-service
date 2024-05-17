package vdi.component.plugin.client

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.veupathdb.vdi.lib.common.util.AtomicUShort

internal object ImportCounter {
  // Cycle the ID to keep our generated IDs short.
  private const val CycleAt = UShort.MAX_VALUE

  private val mutex = Mutex()
  private val requestCounter = AtomicUShort()

  fun nextIndex() =
    runBlocking {
      mutex.withLock {
        requestCounter.inc()

        if (requestCounter.get() >= CycleAt)
          requestCounter.set(0u)

        requestCounter.get()
      }
    }
}