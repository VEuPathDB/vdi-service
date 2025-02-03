package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetPublication
import org.veupathdb.service.vdi.generated.model.DatasetPublicationImpl
import org.veupathdb.service.vdi.util.ValidationErrors
import org.veupathdb.vdi.lib.common.model.VDIDatasetPublication
import vdi.component.db.app.DatasetPublicationCitationMaxLength
import vdi.component.db.app.DatasetPublicationPubMedIDMaxLength

internal fun DatasetPublication.cleanup() {
  citation = citation?.takeIf { it.isNotBlank() }
    ?.trim()

  pubMedId = pubMedId?.takeIf { it.isNotBlank() }
    ?.trim()
}

internal fun DatasetPublication?.validate(prefix: String, index: Int, validationErrors: ValidationErrors) {
  if (this == null) {
    validationErrors.add("${prefix}publications[$index]", "entries must not be null")
    return
  }

  if (citation != null) {
    if (citation.length < 3)
      validationErrors.add(
        "${prefix}publications[$index].citation",
        "field value must be at least 3 characters in length"
      )
    else if (citation.toByteArray().size > DatasetPublicationCitationMaxLength)
      validationErrors.add(
        "${prefix}publications[$index].citation",
        "field must not be larger than $DatasetPublicationCitationMaxLength bytes"
      )
  }

  if (pubMedId == null)
    validationErrors.add("${prefix}publications[$index].pubMedId", "field is required")
  else if (pubMedId.length < 3)
    validationErrors.add("${prefix}publications[$index].pubMedId", "field value must be at least 3 characters in length")
  else if (pubMedId.toByteArray().size > DatasetPublicationPubMedIDMaxLength)
    validationErrors.add("${prefix}publications[$index].pubMedId", "field must not be larger than $DatasetPublicationPubMedIDMaxLength bytes")
}

internal fun DatasetPublication.toInternal() =
  VDIDatasetPublication(citation, pubMedId)

internal fun VDIDatasetPublication.toExternal(): DatasetPublication =
  DatasetPublicationImpl().also {
    it.citation = citation
    it.pubMedId = pubmedID
  }
