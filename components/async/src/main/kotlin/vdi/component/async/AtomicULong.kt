package vdi.component.async

import kotlinx.coroutines.runBlocking
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

  suspend fun get() = mtx.withLock { atm }

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

  override fun toString() = runBlocking { mtx.withLock { atm.toString() } }

  override fun hashCode() = runBlocking { mtx.withLock { atm.hashCode() } }

  override fun equals(other: Any?) = other is AtomicULong && runBlocking { mtx.withLock { atm } == other.get() }
}