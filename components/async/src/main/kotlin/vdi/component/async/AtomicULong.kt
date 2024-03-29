package vdi.component.async

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AtomicULong(initialValue: ULong = 0uL) {
  private var atm = initialValue
  private val mtx = Mutex()

  suspend operator fun inc(): AtomicULong {
    mtx.withLock { atm++ }
    return this
  }

  suspend fun incAndGet(amt: ULong = 1uL): ULong {
    mtx.withLock {
      atm += amt
      return atm
    }
  }

  suspend operator fun dec(): AtomicULong {
    mtx.withLock { atm-- }
    return this
  }

  suspend operator fun plusAssign(value: ULong) {
    mtx.withLock { atm += value }
  }

  suspend operator fun minusAssign(value: ULong) {
    mtx.withLock { atm -= value }
  }
}