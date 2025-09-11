@file:JvmName("MetadataPatchUtils")
package vdi.service.rest.server.inputs


@Suppress("UNCHECKED_CAST")
internal inline fun <reified C: Any, T> C?.unsafePatch(original: T) =
  when (this) {
    null -> original
    else -> C::class.java.getDeclaredMethod("getValue")
      .invoke(this) as T
  }

@Suppress("UNCHECKED_CAST")
internal inline fun <reified C: Any, E, T> C?.unsafePatch(original: T, transform: (E) -> T) =
  when (this) {
    null -> original
    else -> transform(C::class.java.getDeclaredMethod("getValue")
      .invoke(this) as E)
  }
