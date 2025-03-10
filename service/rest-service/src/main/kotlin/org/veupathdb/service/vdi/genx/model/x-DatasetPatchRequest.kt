package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetPatchRequest
import org.veupathdb.service.vdi.util.ValidationErrors
import vdi.component.db.app.*

internal fun DatasetPatchRequest.cleanup() {
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

internal fun DatasetPatchRequest.validate() {
  val errors = ValidationErrors()

  if (name.isNullOrBlank())
    errors.add("name", "field cannot be blank")
  else
    name.checkLength("meta.name", DatasetMetaMaxNameLength, errors)

  shortName?.checkLength("shortName", DatasetMetaMaxShortNameLength, errors)
  shortAttribution?.checkLength("shortAttribution", DatasetMetaMaxShortAttributionLength, errors)
  category?.checkLength("category", DatasetMetaMaxCategoryLength, errors)

  if (summary != null) {
    if (summary.length > DatasetMetaMaxSummaryFieldLength)
      errors.add("summary", "must be $DatasetMetaMaxSummaryFieldLength characters or less")
  }

  publications.forEachIndexed { i, pub -> pub.validate("", i, errors) }
  hyperlinks.forEachIndexed { i, link -> link.validate("", i, errors) }
  contacts.forEachIndexed { i, con -> con.validate("", i, errors) }
  organisms.forEachIndexed { i, l ->
    if (l == null)
      errors.add("meta.organisms[$i]", "entries must not be null")

    l.checkLength("meta.organism[$i]", DatasetOrganismAbbrevMaxLength, errors)
  }
}
