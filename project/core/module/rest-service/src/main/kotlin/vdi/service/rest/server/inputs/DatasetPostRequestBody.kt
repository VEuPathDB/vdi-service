@file:JvmName("DatasetPostRequestInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import java.net.URI
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.meta.UserID
import vdi.service.rest.generated.model.BadRequestError
import vdi.service.rest.generated.model.DatasetPostRequestBody
import vdi.service.rest.generated.model.DatasetVisibility
import vdi.service.rest.server.outputs.BadRequestError
import vdi.util.fn.Either
import vdi.util.fn.Either.Companion.left
import vdi.util.fn.Either.Companion.right

fun DatasetPostRequestBody.cleanup() {
  details?.cleanup()
  url       = url?.takeUnless(String::isBlank)?.trim()
  dataFiles = dataFiles.takeUnless(Collection<*>::isNullOrEmpty)
  docFiles  = docFiles.takeUnless(Collection<*>::isNullOrEmpty)

  dataPropertiesFiles = dataPropertiesFiles.takeUnless(Collection<*>::isNullOrEmpty)
}

@Suppress("DuplicatedCode") // overlap in generated API pojo field names
fun DatasetPostRequestBody.validate(): Either<BadRequestError, ValidationErrors> {
  // DatasetPostRequestBody is not a JSON object, it is a multipart/form-data
  // request.  The "fields" are form parts and are not validated as if they are
  // part of a syntactically correct JSON body.

  // If the meta field is null, then the request body was incomplete.
  if (details == null)
    return left(BadRequestError("missing dataset metadata"))

  // If there is no file or URL, then the request body was incomplete.
  // If there is a file AND URL, then the request body is invalid.
  if (dataFiles == null) {
    if (url == null)
      return left(BadRequestError("must provide an upload file or url to a source file"))

    try {
      URI(url).toURL()
    } catch (_: IllegalArgumentException){
      return left(BadRequestError("invalid source URL"))
    }
  } else if (url != null) {
    return left(BadRequestError("cannot provide both an upload file and a source file URL"))
  }

  val dataTypeConfig = details.type?.toInternal()?.let(PluginRegistry::configDataOrNullFor)

  // If no mapping files were provided
  if (dataPropertiesFiles == null) {
    // AND the dataset is NOT private
    // AND the data type uses variable mapping files
    if (details.visibility != DatasetVisibility.PRIVATE && dataTypeConfig?.usesDataPropertiesFiles == true) {
      return left(BadRequestError("must provide at least one mapping file"))
    }
  // If at least one mapping file was provided
  // AND the data type doesn't use mapping files
  } else if (dataTypeConfig?.usesDataPropertiesFiles == false) {
    return left(BadRequestError("chosen data type does not permit mapping files"))
  }

  return right(ValidationErrors().also { details.validate(it) })
}

fun DatasetPostRequestBody.toDatasetMeta(userID: UserID) =
  details.toInternal(userID, url)
