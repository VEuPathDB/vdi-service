package vdi.service.plugin.util

import kotlinx.io.RawSink

fun RawSink.safeFlush() =
  try { flush() } catch (_: IllegalStateException) {}