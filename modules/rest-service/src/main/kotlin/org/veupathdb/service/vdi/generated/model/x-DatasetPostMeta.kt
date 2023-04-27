package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.util.ValidationErrors

internal fun DatasetPostMeta.validate(validationErrors: ValidationErrors) {
  if (datasetType == null)
    validationErrors.add("meta.type", "field is required")

  if (name.isNullOrBlank())
    validationErrors.add("meta.name", "field is required")

  if (summary != null && summary.isBlank())
    summary = null

  if (description != null && description.isBlank())
    description = null

  if (projects == null || projects.isEmpty())
    validationErrors.add("meta.projects", "one or more projects is required")
  else
    projects.forEachIndexed { i, it ->
      if (it.isNullOrBlank())
        validationErrors.add("meta.projects[$i]", "must not be null or blank")
    }
}
