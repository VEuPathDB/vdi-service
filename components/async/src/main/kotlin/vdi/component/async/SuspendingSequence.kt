package vdi.component.async

interface SuspendingSequence<T> : AutoCloseable {
  fun iterator(): SuspendingIterator<T>
}
