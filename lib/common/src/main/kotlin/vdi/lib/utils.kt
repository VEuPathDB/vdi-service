package vdi.lib

inline fun <T> T?.orElse(fn: () -> Nothing): T =
  this ?: fn()

inline fun discardException(fn: () -> Unit) =
  try { fn() } catch (_: Throwable) {}
