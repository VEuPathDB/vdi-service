package vdi.service.rest.server.inputs

import jakarta.ws.rs.BadRequestException
import org.veupathdb.lib.request.validation.*
import vdi.service.generated.model.*
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.*
import java.time.OffsetDateTime

internal fun vdi.service.rest.generated.model.DatasetPostRequestBody.cleanup() {
  meta?.cleanup()
  url = url?.ifBlank { null }
    ?.trim()
}

@Suppress("DuplicatedCode") // overlap in generated API pojo field names
fun vdi.service.rest.generated.model.DatasetPostRequestBody.validate(): ValidationErrors {
  // DatasetPostRequestBody is not a JSON object, it is a multipart/form-data
  // request.  The "fields" are form parts and are not validated as if they are
  // part of a syntactically correct JSON body.

  // If the meta field is null, then the request body was incomplete.
  if (meta == null)
    throw BadRequestException("missing dataset metadata")

  // If there is no file or URL, then the request body was incomplete.
  // If there is a file AND URL, then the request body is invalid.
  if (file == null) {
    if (url == null)
      throw BadRequestException("must provide an upload file or url to a source file")
  } else if (url != null) {
    throw BadRequestException("cannot provide both an upload file and a source file URL")
  }

  return ValidationErrors().also { meta.validate(it) }
}

internal fun vdi.service.rest.generated.model.DatasetPostRequestBody.toDatasetMeta(userID: UserID) =
  VDIDatasetMeta(
    type             = meta.datasetType.toInternal(),
    projects         = meta.projects.toSet(),
    owner            = userID,
    name             = meta.name,
    shortName        = meta.shortName,
    shortAttribution = meta.shortAttribution,
    category         = meta.category,
    summary          = meta.summary,
    description      = meta.description,
    visibility       = meta.visibility?.toInternal() ?: VDIDatasetVisibility.Private,
    origin           = meta.origin,
    sourceURL        = url,
    created          = meta.createdOn ?: OffsetDateTime.now(),
    dependencies     = meta.dependencies.map(vdi.service.rest.generated.model.DatasetDependency::toInternal),
    publications     = meta.publications.map(vdi.service.rest.generated.model.DatasetPublication::toInternal),
    hyperlinks       = meta.hyperlinks.map(vdi.service.rest.generated.model.DatasetHyperlink::toInternal),
    contacts         = meta.contacts.map(vdi.service.rest.generated.model.DatasetContact::toInternal),
    organisms        = meta.organisms,
  )
