package vdi.core.async

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class Trigger {
  private val mutex = Mutex()
  private val queue = mutableSetOf<Continuation<Unit>>()
  private var triggered = false

  suspend fun trigger() {
    mutex.withLock { triggered = true }
    tryRelease()
  }

  suspend fun isTriggered() = mutex.withLock { triggered }

  suspend fun await() {
    mutex.withLock {
      if (triggered)
        return release()
    }

    suspendCancellableCoroutine { queue.add(it); blockingTryRelease() }
  }

  private fun blockingTryRelease() {
    runBlocking { tryRelease() }
  }

  private suspend fun tryRelease() {
    mutex.withLock {
      if (triggered)
        release()
    }
  }

  private fun release() {
    queue.forEach { it.resume(Unit) }
    queue.clear()
  }
}
