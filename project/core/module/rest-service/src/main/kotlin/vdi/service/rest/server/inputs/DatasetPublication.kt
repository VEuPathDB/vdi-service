@file:JvmName("DatasetPublicationValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.model.data.DatasetPublication
import vdi.service.rest.generated.model.DatasetPublication.TypeType
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.model.DatasetPublication as APIPublication

private val CitationLengthRange = 3..2000
private val PubMedLengthRange = 3..30


fun APIPublication?.cleanup() = this?.apply {
  cleanupString(::getIdentifier)
  cleanupString(::getCitation)

  // trim off id prefix if present
  if (identifier?.startsWith("pmid:", true) == true)
    identifier = identifier.substring(5)
  else if (identifier?.startsWith("doi:", true) == true)
    identifier = identifier.substring(4)

  type = type ?: TypeType.PMID
  isPrimary = isPrimary ?: false
}

fun APIPublication.validate(jPath: String, index: Int, errors: ValidationErrors) {
  identifier.require(jPath, index, errors) {
    when (type!!) {
      TypeType.PMID -> identifier.checkLength(jPath..JsonField.IDENTIFIER, index, PubMedLengthRange, errors)
      TypeType.DOI  -> identifier.checkLength(jPath..JsonField.IDENTIFIER, index, DOIValidLengthRange, errors)
    }
  }

  citation?.checkLength(jPath..JsonField.CITATION, index, CitationLengthRange, errors)
}

fun Collection<APIPublication>.validate(jPath: String, errors: ValidationErrors) {
  if (isEmpty())
    return

  var primaries = 0

  forEachIndexed { i, p ->
    p.require(jPath, i, errors) {
      validate(jPath, i, errors)

      if (isPrimary)
        primaries++
    }
  }

  if (primaries == 0)
    errors.add(jPath, "one publication must be marked as primary")
  else if (primaries > 1)
    errors.add(jPath, "only one publication may be marked as primary")
}

fun APIPublication.toInternal() =
  DatasetPublication(identifier, type.toInternal(), citation, isPrimary)

fun TypeType.toInternal() =
  when (this) {
    TypeType.PMID -> DatasetPublication.PublicationType.PubMed
    TypeType.DOI  -> DatasetPublication.PublicationType.DOI
  }
