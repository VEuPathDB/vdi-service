package vdi.components.rabbit


interface SuspendingIterator<out T> {
  suspend operator fun hasNext(): Boolean
  suspend operator fun next(): T
}

