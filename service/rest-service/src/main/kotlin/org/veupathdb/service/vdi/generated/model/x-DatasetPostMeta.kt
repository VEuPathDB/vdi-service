package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.util.ValidationErrors
import vdi.component.db.app.DatasetMetaMaxCategoryLength
import vdi.component.db.app.DatasetMetaMaxShortAttributionLength
import vdi.component.db.app.DatasetMetaMaxShortNameLength
import vdi.component.db.app.DatasetMetaMaxSummaryFieldLength

internal fun DatasetPostMeta.cleanup() {
  datasetType?.cleanup()

  name = name?.trim()

  shortName = shortName?.takeIf { it.isNotBlank() }
    ?.trim()

  shortAttribution = shortAttribution?.takeIf { it.isNotBlank() }
    ?.trim()

  category = category?.takeIf { it.isNotBlank() }
    ?.trim()

  summary = summary?.takeIf { it.isNotBlank() }
    ?.trim()

  description = description?.takeIf { it.isNotBlank() }
    ?.trim()

  origin = origin?.trim()

  projects?.forEachIndexed { i, s -> projects[i] = s?.takeIf { it.isNotBlank() } ?.trim() }

  if (publications.isNullOrEmpty())
    publications = emptyList()
  else
    publications.forEach { it?.cleanup() }

  if (hyperlinks.isNullOrEmpty())
    hyperlinks = emptyList()
  else
    hyperlinks.forEach { it?.cleanup() }

  if (contacts.isNullOrEmpty())
    contacts = emptyList()
  else
    contacts.forEach { it?.cleanup() }

  if (taxonIds.isNullOrEmpty())
    taxonIds = emptyList()

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

  shortName?.checkLength("meta.shortName", DatasetMetaMaxShortNameLength, validationErrors)
  shortAttribution?.checkLength("meta.shortAttribution", DatasetMetaMaxShortAttributionLength, validationErrors)
  category?.checkLength("meta.category", DatasetMetaMaxCategoryLength, validationErrors)

  if (summary != null) {
    if (summary.length > DatasetMetaMaxSummaryFieldLength)
      validationErrors.add("meta.summary", "must be $DatasetMetaMaxSummaryFieldLength characters or less")
    if (summary.isBlank())
      summary = null
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

  publications.forEachIndexed { i, pub -> pub.validate("meta.", i, validationErrors) }
  hyperlinks.forEachIndexed { i, link -> link.validate("meta.", i, validationErrors) }
  contacts.forEachIndexed { i, con -> con.validate("meta.", i, validationErrors) }
  taxonIds.forEachIndexed { i, l -> if (l == null) validationErrors.add("meta.taxonIds[$i]", "entries must not be null") }
}
