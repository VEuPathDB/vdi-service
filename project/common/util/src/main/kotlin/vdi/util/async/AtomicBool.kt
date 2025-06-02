package vdi.util.async

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AtomicBool(initialValue: Boolean) {
  private val lock = Mutex()
  private var value = initialValue

  suspend fun get() = lock.withLock { value }

  suspend fun set(new: Boolean) = lock.withLock { value = new }

  suspend operator fun compareTo(other: AtomicBool) = get().compareTo(other.get())
  suspend operator fun compareTo(other: Boolean) = get().compareTo(other)

  suspend operator fun not() = !get()

  override fun toString() = runBlocking { get() }.toString()

  override fun equals(other: Any?) = other is AtomicBool && runBlocking { other.get() == get() }

  override fun hashCode() = runBlocking { get() }.hashCode()
}