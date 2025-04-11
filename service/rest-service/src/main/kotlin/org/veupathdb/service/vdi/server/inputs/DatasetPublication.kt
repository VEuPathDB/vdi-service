package org.veupathdb.service.vdi.server.inputs

import org.veupathdb.lib.request.validation.*
import org.veupathdb.service.vdi.generated.model.DatasetPublication
import org.veupathdb.service.vdi.generated.model.JsonField
import org.veupathdb.vdi.lib.common.model.VDIDatasetPublication

private const val CitationMinLength = 3
private const val CitationMaxLength = 2000
private const val PubMedMinLength = 3
private const val PubMedMaxLength = 30


fun DatasetPublication.cleanup() {
  citation = citation.cleanupString()
  pubMedId = pubMedId.cleanupString()
}

fun Collection<DatasetPublication>.validate(jPath: String, errors: ValidationErrors) {
  if (isEmpty())
    return

  var primaries = 0

  val citField = jPath..JsonField.CITATION
  val pubField = jPath..JsonField.PUB_MED_ID

  forEachIndexed { i, p ->
    p.require(jPath, i, errors) {
      citation.optCheckLength(citField, i, CitationMinLength, CitationMaxLength, errors)
      pubMedId.reqCheckLength(pubField, i, PubMedMinLength, PubMedMaxLength, errors)

      if (isPrimary)
        primaries++
    }
  }

  if (primaries == 0)
    errors.add(jPath, "one publication must be marked as primary")
  else if (primaries > 1)
    errors.add(jPath, "only one publication may be marked as primary")
}

internal fun DatasetPublication.toInternal() =
  VDIDatasetPublication(citation, pubMedId)
