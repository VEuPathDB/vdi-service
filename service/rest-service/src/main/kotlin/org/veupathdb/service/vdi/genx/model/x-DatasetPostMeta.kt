package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetPostMeta
import org.veupathdb.service.vdi.util.ValidationErrors
import vdi.component.db.app.*

@Suppress("DuplicatedCode") // Overlap in generated API types
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

  if (organisms.isNullOrEmpty())
    organisms = emptyList()

  dependencies?.forEach {
    it.resourceVersion = it.resourceVersion?.takeIf { it.isNotBlank() } ?.trim()
    it.resourceIdentifier = it.resourceIdentifier?.takeIf { it.isNotBlank() } ?.trim()
    it.resourceDisplayName = it.resourceDisplayName?.takeIf { it.isNotBlank() } ?.trim()
  }
}

internal fun DatasetPostMeta.validate(errors: ValidationErrors) {
  name.validateName(errors, "meta")
  origin.validateOrigin(errors, "meta")

  if (datasetType == null)
    errors.add("meta.type", "field is required")

  shortName?.validateShortName(errors, "meta")
  shortAttribution?.validateShortAttribution(errors, "meta")
  category?.validateCategory(errors, "meta")
  summary?.validateSummary(errors, "meta")

  description = description?.ifBlank { null }


  if (origin.isNullOrBlank())
    errors.add("meta.origin", "field is required")

  if (projects == null || projects.isEmpty())
    errors.add("meta.projects", "one or more projects is required")
  else
    projects.forEachIndexed { i, it ->
      if (it.isNullOrBlank())
        errors.add("meta.projects[$i]", "must not be null or blank")
    }

  publications.validate(errors, "meta.")

  hyperlinks.forEachIndexed { i, link -> link.validate("meta.", i, errors) }

  var primaries = 0
  contacts.forEachIndexed { i, con ->
    con.validate("meta.", i, errors)
    if (con.isPrimary == true)
      primaries++
  }
  if (primaries > 1)
    errors.add("meta.contacts", "only one contact may be marked as primary")

  organisms.forEachIndexed { i, l ->
    if (l == null)
      errors.add("meta.organisms[$i]", "entries must not be null")

    l.checkLength("meta.organism[$i]", DatasetOrganismAbbrevMaxLength, errors)
  }
}
