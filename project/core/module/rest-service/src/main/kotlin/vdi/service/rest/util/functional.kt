package vdi.service.rest.util

fun <T, R> Iterable<T>.reduceTo(init: R, fn: (continuous: R, next: T) -> R): R {
  var out = init
  forEach { out = fn(out, it) }
  return out
}

fun <K, V, R> Map<K, V>.reduceTo(init: R, fn: (continuous: R, key: K, value: V) -> R): R {
  var out = init
  forEach { (k, v) -> out = fn(out, k, v) }
  return out
}
