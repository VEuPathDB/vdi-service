package vdi.component.async

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ObservableInt {
  suspend fun get(): Int
  suspend fun subscribe(observer: suspend (Int) -> Unit)
}

internal class ObservableIntImpl(initialValue: Int = 0) : ObservableInt {
  private val mtx = Mutex()
  private var cur = initialValue
  private val obs = mutableListOf<suspend (Int) -> Unit>()

  override suspend fun get(): Int {
    return mtx.withLock { cur }
  }

  suspend operator fun inc(): ObservableIntImpl {
    mtx.lock()
    updateAndObserve(cur + 1)
    return this
  }

  suspend operator fun dec(): ObservableIntImpl {
    mtx.lock()
    updateAndObserve(cur - 1)
    return this
  }

  suspend operator fun plusAssign(rhs: Int) {
    mtx.lock()
    updateAndObserve(cur + rhs)
  }

  suspend operator fun minusAssign(rhs: Int) {
    mtx.lock()
    updateAndObserve(cur - rhs)
  }

  suspend operator fun divAssign(rhs: Int) {
    mtx.lock()
    updateAndObserve(cur / rhs)
  }

  suspend operator fun timesAssign(rhs: Int) {
    mtx.lock()
    updateAndObserve(cur * rhs)
  }

  suspend fun update(to: Int) {
    mtx.lock()
    updateAndObserve(to)
  }

  override suspend fun subscribe(observer: suspend (Int) -> Unit) {
    mtx.withLock { obs.add(observer) }
  }

  private suspend fun updateAndObserve(to: Int) {
    cur = to

    try {
      for (fn in obs)
        fn(to)
    } finally {
      mtx.unlock()
    }
  }
}