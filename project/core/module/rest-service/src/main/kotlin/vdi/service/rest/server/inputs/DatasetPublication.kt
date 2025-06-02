@file:JvmName("DatasetPublicationValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.model.data.DatasetPublication
import vdi.model.data.DatasetPublication
import vdi.service.rest.generated.model.DatasetPublication
import vdi.service.rest.generated.model.JsonField

private const val CitationMinLength = 3
private const val CitationMaxLength = 2000
private const val PubMedMinLength = 3
private const val PubMedMaxLength = 30


fun DatasetPublication.cleanup() {
  citation  = citation.cleanupString()
  pubMedId  = pubMedId.cleanupString()
  isPrimary = isPrimary ?: false
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
  DatasetPublication(pubMedId, citation, isPrimary)
