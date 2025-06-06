@file:JvmName("DatasetProxyPostRequestValidator")
package vdi.service.rest.server.inputs

import jakarta.ws.rs.BadRequestException
import org.veupathdb.lib.request.validation.ValidationErrors
import java.net.URI
import java.net.URISyntaxException
import vdi.model.data.UserID
import vdi.model.data.DatasetMetadata
import vdi.model.data.DatasetVisibility
import java.time.OffsetDateTime
import vdi.service.rest.generated.model.*

internal fun DatasetProxyPostRequestBody.cleanup() {
  details?.cleanup()
  url = url.takeUnless { it.isNullOrBlank() }
    ?.trim()
  dataFiles = dataFiles.takeUnless { it.isNullOrEmpty() }
  docFiles = docFiles.takeUnless { it.isNullOrEmpty() }
}

@Suppress("DuplicatedCode") // overlap in generated API pojo field names
fun DatasetProxyPostRequestBody.validate(): ValidationErrors {
  // DatasetPostRequestBody is not a JSON object, it is a multipart/form-data
  // request.  The "fields" are form parts and are not validated as if they are
  // part of a syntactically correct JSON body.

  // If the meta field is null, then the request body was incomplete.
  if (details == null)
    throw BadRequestException("missing dataset metadata")

  // If there is no file or URL, then the request body was incomplete.
  // If there is a file AND URL, then the request body is invalid.
  if (dataFiles == null) {
    if (url == null)
      throw BadRequestException("must provide an upload file or url to a source file")

    try {
      URI(url)
    } catch (e: URISyntaxException) {
      throw BadRequestException("invalid source URL")
    }
  } else if (url != null) {
    throw BadRequestException("cannot provide both an upload file and a source file URL")
  }

  return ValidationErrors().also { details.validate(it) }
}

internal fun DatasetProxyPostRequestBody.toDatasetMeta(userID: UserID) =
  details.toInternal(userID, url)
