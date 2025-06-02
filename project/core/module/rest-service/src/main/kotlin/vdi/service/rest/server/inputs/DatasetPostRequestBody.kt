@file:JvmName("DatasetPostRequestValidator")
package vdi.service.rest.server.inputs

import jakarta.ws.rs.BadRequestException
import org.veupathdb.lib.request.validation.ValidationErrors
import vdi.model.data.UserID
import vdi.service.rest.generated.model.DatasetPostRequestBody

fun DatasetPostRequestBody.cleanup() {
  details?.cleanup()
  url = url.takeUnless { it.isNullOrBlank() }
    ?.trim()
  dataFiles = dataFiles.takeUnless { it.isNullOrEmpty() }
  docFiles = docFiles.takeUnless { it.isNullOrEmpty() }
}

@Suppress("DuplicatedCode") // overlap in generated API pojo field names
fun DatasetPostRequestBody.validate(): ValidationErrors {
  // DatasetPostRequestBody is not a JSON object, it is a multipart/form-data
  // request.  The "fields" are form parts and are not validated as if they are
  // part of a syntactically correct JSON body.

  // If the meta field is null, then the request body was incomplete.
  if (details == null)
    throw BadRequestException("missing dataset metadata")

  // If there is no file or URL, then the request body was incomplete.
  // If there is a file AND URL, then the request body is invalid.
  if (details == null) {
    if (url == null)
      throw BadRequestException("must provide an upload file or url to a source file")
  } else if (url != null) {
    throw BadRequestException("cannot provide both an upload file and a source file URL")
  }

  return ValidationErrors().also { details.validate(it) }
}

fun DatasetPostRequestBody.toDatasetMeta(userID: UserID) =
  details.toInternal(userID, url)
