package vdi.component.async

import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CountdownLatch(initial: Int) {
  private var count = initial
  private var mutex = Mutex()
  private var queue = mutableSetOf<Continuation<Unit>>()

  suspend fun decrement() {
    if (count == 0)
      return lockRelease()

    mutex.withLock {
      if (count == 0)
        return lockRelease()

      count--

      if (count == 0)
        release()
    }

    return
  }

  suspend fun await() {
    if (count == 0)
      return lockRelease()

    mutex.withLock {
      if (count == 0)
        return lockRelease()
    }

    suspendCancellableCoroutine { queue.add(it) }
  }

  private fun release() {
    queue.forEach { it.resume(Unit) }
    queue.clear()
  }

  private suspend fun lockRelease() {
    mutex.withLock {
      release()
    }
  }
}