package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.util.ValidationErrors

internal fun String.checkLength(key: String, maxSize: Int, errors: ValidationErrors) {
  if (toByteArray().size > maxSize)
    errors.add(key, "field must not be larger than $maxSize bytes")
}

internal fun String.checkLength(key: () -> String, maxSize: Int, errors: ValidationErrors) {
  if (toByteArray().size > maxSize)
    errors.add(key(), "field must not be larger than $maxSize bytes")
}

internal fun String.checkLength(key: () -> String, minSize: Int, maxSize: Int, errors: ValidationErrors) {
  if (length < minSize)
    errors.add(key(), "field must be at least $maxSize characters in length")
  else if (toByteArray().size > maxSize)
    errors.add(key(), "field must not be larger than $maxSize bytes")
}
