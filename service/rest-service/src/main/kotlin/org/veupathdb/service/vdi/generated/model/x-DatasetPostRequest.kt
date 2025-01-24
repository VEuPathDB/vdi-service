package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.util.ValidationErrors
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.*
import java.time.OffsetDateTime

internal fun DatasetPostRequest.cleanup() {
  meta?.cleanup()
  url = url?.takeIf { it.isNotBlank() } ?.trim()
}

internal fun DatasetPostRequest.validate(): ValidationErrors {
  val validationErrors = ValidationErrors()

  if (meta == null)
    validationErrors.add("meta", "field is required")

  meta.validate(validationErrors)

  if (file == null && url.isNullOrBlank())
    validationErrors.add("must provide an upload file or url to a source file")
  if (file != null && (url != null && url.isNotBlank()))
    validationErrors.add("cannot provide both an upload file and a source file URL")

  return validationErrors
}

internal fun DatasetPostRequest.toDatasetMeta(userID: UserID) =
  VDIDatasetMeta(
    type             = VDIDatasetType(
      name    = DataType.of(meta.datasetType.name),
      version = meta.datasetType.version,
    ),
    projects         = meta.projects.toSet(),
    owner            = userID,
    name             = meta.name,
    shortName        = meta.shortName,
    shortAttribution = meta.shortAttribution,
    category         = meta.category,
    summary          = meta.summary,
    description      = meta.description,
    visibility       = meta.visibility?.toInternalVisibility() ?: VDIDatasetVisibility.Private,
    origin           = meta.origin,
    sourceURL        = url,
    created          = meta.createdOn ?: OffsetDateTime.now(),
    dependencies     = (meta.dependencies ?: emptyList()).map {
      VDIDatasetDependency(
        identifier  = it.resourceIdentifier,
        version     = it.resourceVersion,
        displayName = it.resourceDisplayName
      )
    },
    publications     = meta.publications.map(DatasetPublication::toInternal),
    hyperlinks       = meta.hyperlinks.map(DatasetHyperlink::toInternal),
    contacts         = meta.contacts.map(DatasetContact::toInternal),
    taxonIDs         = meta.taxonIds
  )
