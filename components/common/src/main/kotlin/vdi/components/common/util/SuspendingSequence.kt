package vdi.components.rabbit

interface SuspendingSequence<T> : AutoCloseable {
  fun iterator(): SuspendingIterator<T>
}
