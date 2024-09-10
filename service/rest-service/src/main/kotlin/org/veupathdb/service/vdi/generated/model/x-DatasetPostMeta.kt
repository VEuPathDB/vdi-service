package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.util.ValidationErrors

internal fun DatasetPostMeta.cleanup() {
  datasetType?.cleanup()

  name = name?.trim()
  summary = summary?.takeIf { it.isNotBlank() }
    ?.trim()
  description = description?.takeIf { it.isNotBlank() }
    ?.trim()
  origin = origin?.trim()

  projects?.forEachIndexed { i, s -> projects[i] = s?.takeIf { it.isNotBlank() } ?.trim() }

  dependencies?.forEach {
    it.resourceVersion = it.resourceVersion?.takeIf { it.isNotBlank() } ?.trim()
    it.resourceIdentifier = it.resourceIdentifier?.takeIf { it.isNotBlank() } ?.trim()
    it.resourceDisplayName = it.resourceDisplayName?.takeIf { it.isNotBlank() } ?.trim()
  }
}

internal fun DatasetPostMeta.validate(validationErrors: ValidationErrors) {
  if (datasetType == null)
    validationErrors.add("meta.type", "field is required")

  if (name.isNullOrBlank())
    validationErrors.add("meta.name", "field is required")

  if (summary != null) {
    if (summary.length > 4000) validationErrors.add("meta.summary", "must be 4000 characters or less")
    if (summary.isBlank()) summary = null
  }

  if (description != null && description.isBlank())
    description = null

  if (origin.isNullOrBlank())
    validationErrors.add("meta.origin", "field is required")

  if (projects == null || projects.isEmpty())
    validationErrors.add("meta.projects", "one or more projects is required")
  else
    projects.forEachIndexed { i, it ->
      if (it.isNullOrBlank())
        validationErrors.add("meta.projects[$i]", "must not be null or blank")
    }
}
