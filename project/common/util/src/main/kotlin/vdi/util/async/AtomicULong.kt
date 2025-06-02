package vdi.util.async

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AtomicULong(initialValue: ULong = 0uL) {
  private val lock = Mutex()
  private var value = initialValue


  suspend operator fun inc() = lock.withLock { value++; this }

  suspend inline fun incAndGet() = incAndGet(1L)

  suspend inline fun incAndGet(amt: Short) = incAndGet(amt.toLong())

  suspend inline fun incAndGet(amt: Int) = incAndGet(amt.toLong())

  suspend fun incAndGet(amt: Long) =
    lock.withLock {
      val sub: Boolean
      val chg: ULong

      if (amt < 0) {
        sub = true
        chg = (-amt).toULong()
      } else {
        sub = false
        chg = amt.toULong()
      }

      if (sub) {
        if (chg > value)
          throw IllegalArgumentException("refusing to subtract $chg from $value in an unsigned context to avoid rollunder")

        value -= chg
      } else {
        if (ULong.MAX_VALUE - value < chg)
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

  suspend operator fun plusAssign(other: ULong) { lock.withLock { value += other } }
  suspend operator fun minusAssign(other: ULong) { lock.withLock { value -= other } }
  suspend operator fun divAssign(other: ULong) { lock.withLock { value /= other } }
  suspend operator fun timesAssign(other: ULong) { lock.withLock { value *= other } }
  suspend operator fun remAssign(other: ULong) { lock.withLock { value %= other } }

  suspend operator fun compareTo(other: AtomicULong) = get().compareTo(other.get())
  suspend operator fun compareTo(other: ULong) = get().compareTo(other)

  suspend fun get() = lock.withLock { value }

  suspend fun set(new: ULong) = lock.withLock { value = new }

  override fun toString() = runBlocking { get() }.toString()

  override fun equals(other: Any?) = other is AtomicULong && runBlocking { other.get() == get() }

  override fun hashCode() = runBlocking { get() }.hashCode()
}

