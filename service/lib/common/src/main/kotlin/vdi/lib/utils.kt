package vdi.lib

inline fun <T> T?.orElse(fn: () -> Nothing): T =
  this ?: fn()
