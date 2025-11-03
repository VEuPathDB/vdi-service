package vdi.service.rest.server.inputs

/**
 * Converts a list of values to their internal form using the given function,
 * then, after conversion, ensures the values are distinct when creating a new
 * output list.
 *
 * This function exists to account for the lack of [equals] and [hashCode]
 * methods, making distinct calls ahead of time unreliable.  The output types
 * are expected to implement those methods manually or by way of being data
 * classes to enable a reliable method of ensuring the output list contains only
 * distinct values.
 */
inline fun <I: Any, O: Any> List<I>.toInternalDistinct(crossinline fn: (I) -> O) =
  takeUnless { it.isEmpty() }
    ?.asSequence()
    ?.map { fn(it) }
    ?.distinct()
    ?.toList()
    ?: emptyList()