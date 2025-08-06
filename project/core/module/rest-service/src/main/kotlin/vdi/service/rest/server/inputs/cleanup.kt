package vdi.service.rest.server.inputs

import kotlin.reflect.KMutableProperty0

internal inline fun <T> List<T>?.cleanup(fn: T.() -> Unit) =
  this?.ifEmpty { null }
    ?.onEach(fn)
    ?: emptyList()

internal fun String?.cleanupString() =
  this?.trim()

internal fun KMutableProperty0<String?>.cleanupString() =
  set(get()?.trim())