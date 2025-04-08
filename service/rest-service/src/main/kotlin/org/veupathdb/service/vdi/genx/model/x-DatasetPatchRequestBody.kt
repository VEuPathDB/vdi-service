package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetPatchRequestBody
import org.veupathdb.service.vdi.util.ValidationErrors
import vdi.component.db.app.*

@Suppress("DuplicatedCode") // Overlap in generated API types
internal fun DatasetPatchRequestBody.cleanup() {
  name = name?.trim()
  shortName = shortName?.trim()
  shortAttribution = shortAttribution?.trim()
  category = category?.trim()
  summary = summary?.trim()
  description = description?.trim()

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
}

internal fun DatasetPatchRequestBody.validate(errors: ValidationErrors = ValidationErrors()) {
  if (name.isNullOrBlank())
    errors.add("name", "field cannot be blank")
  else
    name.checkLength("meta.name", DatasetMetaMaxNameLength, errors)

  shortName?.validateShortName(errors)
  shortAttribution?.validateShortAttribution(errors)
  category?.validateCategory(errors)
  summary?.validateSummary(errors)
  publications.validate(errors)
  hyperlinks.validate(errors)
  contacts.validate(errors)
  organisms.validateOrganisms(errors)
}
