package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.util.ValidationErrors

internal fun DatasetPostType.cleanup() {
  name = name?.trim()
  version = version?.trim()
}

internal fun DatasetPostType.validate(validationErrors: ValidationErrors) {
  if (name.isNullOrBlank())
    validationErrors.add("meta.type.name", "field is required")

  if (version.isNullOrBlank())
    validationErrors.add("meta.type.version", "field is required")
}