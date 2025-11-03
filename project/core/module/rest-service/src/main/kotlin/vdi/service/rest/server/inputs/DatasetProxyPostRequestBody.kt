@file:JvmName("DatasetProxyPostRequestInputAdaptor")
package vdi.service.rest.server.inputs

import jakarta.ws.rs.BadRequestException
import org.veupathdb.lib.request.validation.ValidationErrors
import java.net.URI
import vdi.model.data.UserID
import vdi.service.rest.generated.model.*

internal fun DatasetProxyPostRequestBody.cleanup() {
  details?.cleanup()
  url = url.cleanup()
  dataFiles = dataFiles.takeUnless { it.isNullOrEmpty() }
  docFiles = docFiles.takeUnless { it.isNullOrEmpty() }
}

/**
 * Multipart request body validation.
 *
 * The [DatasetPostRequestBody] type does not represent a JSON object, it is a
 * multipart/form-data request body.  The properties on this object are
 * translated from form parts and should not be validated as if they are parts
 * of a syntactically correct JSON body.
 *
 * Errors relating to properties on this object are to be treated as '400 Bad
 * Request' errors.
 *
 * Errors relating to properties within the object returned by
 * [DatasetPostRequestBody.getDetails] should be treated as '422 Unprocessable
 * Entity' errors.
 */
@Suppress("DuplicatedCode") // overlap in generated API pojo field names
fun DatasetProxyPostRequestBody.validate(): ValidationErrors {
  // If the meta field is null, then the request body was incomplete.
  if (details == null)
    throw BadRequestException("missing dataset metadata")

  // If there is no file OR URL, then the request body was incomplete.
  // If there is a file AND URL, then the request body is invalid.
  if (dataFiles == null) {
    if (url == null)
      throw BadRequestException("must provide an upload file or url to a source file")

    try {
      // validate url format
      URI(url).toURL()
    } catch (e: IllegalArgumentException) {
      throw BadRequestException("invalid source URL")
    }
  } else if (url != null) {
    throw BadRequestException("cannot provide both an upload file and a source file URL")
  }


  return ValidationErrors().also { details.validate(it) }
}

internal fun DatasetProxyPostRequestBody.toDatasetMeta(userID: UserID) =
  details.toInternal(userID, url)
