package vdi.util.async

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AtomicUInt(initialValue: UInt = 0u) {
  private val lock = Mutex()
  private var value = initialValue


  suspend operator fun inc() = lock.withLock { value++; this }

  suspend inline fun incAndGet() = incAndGet(1L)

  suspend inline fun incAndGet(amt: Short) = incAndGet(amt.toLong())

  suspend inline fun incAndGet(amt: Int) = incAndGet(amt.toLong())

  suspend fun incAndGet(amt: Long) =
    lock.withLock {
      val sub: Boolean
      val chg: UInt

      if (amt < 0) {
        sub = true
        chg = (-amt).toUInt()
      } else {
        sub = false
        chg = amt.toUInt()
      }

      if (sub) {
        if (chg > value)
          throw IllegalArgumentException("refusing to subtract $chg from $value in an unsigned context to avoid rollunder")

        value -= chg
      } else {
        if (UInt.MAX_VALUE - value < chg)
          throw IllegalArgumentException("refusing to add $chg to $value in an unsigned context to avoid rollover")

        value += chg
      }

      value
    }


  suspend operator fun dec() = lock.withLock { value--; this }

  suspend inline fun decAndGet() = incAndGet(-1L)

  suspend inline fun decAndGet(amt: Short) = incAndGet(-amt)

  suspend inline fun decAndGet(amt: Int) = incAndGet(-amt)

  suspend inline fun decAndGet(amt: Long) = incAndGet(-amt)

  suspend operator fun plusAssign(other: UInt) { lock.withLock { value += other } }
  suspend operator fun minusAssign(other: UInt) { lock.withLock { value -= other } }
  suspend operator fun divAssign(other: UInt) { lock.withLock { value /= other } }
  suspend operator fun timesAssign(other: UInt) { lock.withLock { value *= other } }
  suspend operator fun remAssign(other: UInt) { lock.withLock { value %= other } }

  suspend operator fun compareTo(other: AtomicUInt) = get().compareTo(other.get())
  suspend operator fun compareTo(other: UInt) = get().compareTo(other)

  suspend fun get() = lock.withLock { value }

  suspend fun set(new: UInt) = lock.withLock { value = new }

  override fun toString() = runBlocking { get() }.toString()

  override fun equals(other: Any?) = other is AtomicUInt && runBlocking { other.get() == get() }

  override fun hashCode() = runBlocking { get() }.hashCode()
}