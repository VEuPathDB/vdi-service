package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.util.ValidationErrors
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetDependencyImpl
import org.veupathdb.vdi.lib.common.model.VDIDatasetMetaImpl
import org.veupathdb.vdi.lib.common.model.VDIDatasetTypeImpl
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import java.time.OffsetDateTime

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
  VDIDatasetMetaImpl(
    type         = VDIDatasetTypeImpl(
      name    = meta.datasetType.name.lowercase(),
      version = meta.datasetType.version,
    ),
    projects     = meta.projects.toSet(),
    owner        = userID,
    name         = meta.name,
    summary      = meta.summary,
    description  = meta.description,
    visibility   = meta.visibility?.toInternalVisibility() ?: VDIDatasetVisibility.Private,
    origin       = meta.origin,
    sourceURL    = url,
    created      = meta.createdOn ?: OffsetDateTime.now(),
    dependencies = (meta.dependencies ?: emptyList()).map {
      VDIDatasetDependencyImpl(
        identifier  = it.resourceIdentifier,
        version     = it.resourceVersion,
        displayName = it.resourceDisplayName
      )
    },
  )
