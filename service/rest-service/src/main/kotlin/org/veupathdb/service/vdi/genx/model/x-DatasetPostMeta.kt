package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetPostMeta
import org.veupathdb.service.vdi.generated.model.JsonField
import org.veupathdb.service.vdi.server.input.*
import org.veupathdb.service.vdi.server.input.validateName
import org.veupathdb.service.vdi.server.input.validateOrigin
import org.veupathdb.service.vdi.server.input.validateShortAttribution
import org.veupathdb.service.vdi.server.input.validateShortName
import org.veupathdb.service.vdi.util.ValidationErrors

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

  // Trim strings and ensure no duplicates.
  projects
    ?.asSequence()
    ?.map { it?.trim() }
    ?.distinct()
    ?.toList()

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
  name.validateName(errors, JsonField.META)
  origin.validateOrigin(errors, JsonField.META)

  if (datasetType == null)
    errors.add("${JsonField.META}.${JsonField.DATASET_TYPE}", "field is required")

  shortName?.validateShortName(errors, JsonField.META)
  shortAttribution?.validateShortAttribution(errors, JsonField.META)
  category?.validateCategory(errors, JsonField.META)
  summary?.validateSummary(errors, JsonField.META)

  description = description?.ifBlank { null }


  if (projects == null || projects.isEmpty())
    errors.add("meta.projects", "one or more projects is required")
  else
    projects.forEachIndexed { i, it ->
      if (it.isNullOrBlank())
        errors.add("meta.projects[$i]", "must not be null or blank")
    }

  publications.validate(errors, JsonField.META)

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
