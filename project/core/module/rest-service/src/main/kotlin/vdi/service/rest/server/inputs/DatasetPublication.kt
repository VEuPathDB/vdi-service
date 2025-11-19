@file:JvmName("DatasetPublicationInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.checkLength
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.require
import vdi.model.meta.DatasetPublication
import vdi.service.rest.generated.model.DatasetPublicationType
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.model.DatasetPublication as APIPublication

private val CitationLengthRange = 3..2000
private val PubMedLengthRange = 3..30

private val PubMedIDPattern = Regex("^\\d+$")


fun APIPublication?.cleanup() = this?.apply {
  cleanup(::getIdentifier) { it.cleanup()?.run {
    if (startsWith("pmid:", true)) {
      // if the identifier starts with 'pmid:' then we can assume that type if
      // the user didn't provide one.
      ensureNotNull(::getType, DatasetPublicationType.PMID)
      substring(5)
    } else if (startsWith("doi:", true)) {
      // if the identifier starts with 'pmid:' then we can assume that type if
      // the user didn't provide one.
      ensureNotNull(::getType, DatasetPublicationType.DOI)
      substring(4)
    } else {
      this
    }
  } }
  cleanupString(::getCitation)
  ensureNotNull(::getType, DatasetPublicationType.PMID)
  ensureNotNull(::getIsPrimary, false)
}

fun APIPublication.validate(jPath: String, index: Int, errors: ValidationErrors) {
  identifier.require(jPath, index, errors) {
    when (type!!) {
      DatasetPublicationType.PMID -> {
        val path = jPath..JsonField.IDENTIFIER
        if (identifier.checkLength(path, index, PubMedLengthRange, errors))
          if (!PubMedIDPattern.matches(identifier))
            errors.add(path..index, "invalid PMID format")
      }

      DatasetPublicationType.DOI -> {
        val path = jPath..JsonField.DOI
        if (identifier.checkLength(jPath..JsonField.IDENTIFIER, index, DOIValidLengthRange, errors))
          if (!DOIPattern.matches(identifier))
            errors.add(path..index, "invalid DOI format")
      }
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

fun List<APIPublication>.toInternal() =
  map { DatasetPublication(it.identifier, it.type.toInternal(), it.citation, it.isPrimary) }

fun DatasetPublicationType .toInternal() =
  when (this) {
    DatasetPublicationType.PMID -> DatasetPublication.PublicationType.PubMed
    DatasetPublicationType.DOI  -> DatasetPublication.PublicationType.DOI
  }
