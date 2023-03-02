package vdi.components.common.util


interface SuspendingIterator<out T> {
  suspend operator fun hasNext(): Boolean
  suspend operator fun next(): T
}

