package vdi.components.common.util

interface SuspendingSequence<T> : AutoCloseable {
  fun iterator(): SuspendingIterator<T>
}
