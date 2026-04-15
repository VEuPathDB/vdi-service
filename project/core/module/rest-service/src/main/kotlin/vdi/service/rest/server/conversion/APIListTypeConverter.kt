package vdi.service.rest.server.conversion

import org.veupathdb.lib.request.validation.ValidationErrors

interface APIListTypeConverter<R, V>: APITypeConverter<List<R>, List<V>> {
  fun cleanupSingle(value: R): R

  fun validateSingle(value: R, jsonPath: String, index: Int, errors: ValidationErrors)

  fun toSingleExternal(value: V): R

  fun toSingleInternal(value: R): V

  override fun cleanup(value: List<R>): List<R> =
    value.toMutable().also { values ->
      for (i in values.indices)
        values[i] = cleanupSingle(value[i])
    }

  override fun validate(value: List<R>, jsonPath: String, errors: ValidationErrors) {
    value.forEachIndexed { i, v -> validateSingle(v, jsonPath, i, errors) }
  }

  override fun toExternal(value: List<V>): List<R> =
    value.map(::toSingleExternal)

  override fun toInternal(value: List<R>): List<V> =
    value.map(::toSingleInternal)
}

private fun <T> List<T>.toMutable() =
  this as? MutableList<T> ?: ArrayList(this)