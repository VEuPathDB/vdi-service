package vdi.service.rest.server.inputs

internal inline fun <T> List<T>?.cleanup(fn: T.() -> Unit) =
  this?.ifEmpty { null }
    ?.onEach(fn)
    ?: emptyList()

internal fun String?.cleanupString() =
  this?.trim()
