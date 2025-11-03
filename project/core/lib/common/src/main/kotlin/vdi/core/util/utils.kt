package vdi.core.util

inline infix fun <T> T?.orElse(fn: () -> T): T =
  this ?: fn()

inline fun discardException(fn: () -> Unit) =
  try { fn() } catch (_: Throwable) {}
