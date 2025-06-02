package vdi.core.async

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.math.max

class CountdownLatch(initial: Int) {
  private var count = initial
  private val mutex = Mutex()
  private val queue = mutableSetOf<Continuation<Unit>>()

  suspend fun decrement() {
    mutex.withLock { count = max(0, count - 1) }
    tryRelease()
  }

  suspend fun await() {
    mutex.withLock {
      if (count == 0)
        return
    }

    suspendCancellableCoroutine { queue.add(it); blockingTryRelease() }
  }

  private fun blockingTryRelease() {
    runBlocking { tryRelease() }
  }

  private suspend fun tryRelease() {
    mutex.withLock {
      if (count == 0)
        release()
    }
  }

  private fun release() {
    queue.forEach { it.resume(Unit) }
    queue.clear()
  }
}
