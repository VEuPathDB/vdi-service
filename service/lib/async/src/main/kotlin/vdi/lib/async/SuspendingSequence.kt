package vdi.lib.async

interface SuspendingSequence<T> : AutoCloseable {
  fun iterator(): SuspendingIterator<T>
}
