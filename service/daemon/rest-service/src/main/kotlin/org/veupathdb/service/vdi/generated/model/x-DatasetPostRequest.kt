package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.util.ValidationErrors

fun DatasetPostRequest.validate(): ValidationErrors {
  val validationErrors = ValidationErrors()

  if (meta == null)
    validationErrors.add("meta", "field is required")

  meta.validate(validationErrors)

  if (file == null && url.isNullOrBlank())
    validationErrors.add("must provide an upload file or url to a source file")
  if (file != null && (url != null && url.isNotBlank()))
    validationErrors.add("cannot provide both an upload file and a source file URL")

  return validationErrors
}

