package vdi.service.plugin.server.context

import io.ktor.http.ContentType
import io.ktor.http.content.forEachPart
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.UnsupportedMediaTypeException
import io.ktor.server.request.contentType
import io.ktor.server.request.receiveMultipart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import vdi.model.api.internal.FormField
import vdi.model.api.internal.ImportRequest
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.util.parseAsJson
import vdi.util.fs.TempFiles

private const val IMPORT_PAYLOAD_FILE_NAME = "import.zip"
private const val IMPORT_DETAILS_MAX_SIZE  = 16384uL

@OptIn(ExperimentalContracts::class)
suspend fun ApplicationCall.withImportContext(
  appCtx: ApplicationContext,
  fn: suspend (importCtx: ImportContext) -> Unit,
) {
  contract { callsInPlace(fn, InvocationKind.EXACTLY_ONCE) }

  if (!request.contentType().match(ContentType.MultiPart.FormData))
    throw UnsupportedMediaTypeException(request.contentType())

  TempFiles.withTempDirectory { workspace -> withContext(Dispatchers.IO) { withParsedRequest(appCtx, workspace, fn) } }
}

@OptIn(ExperimentalContracts::class)
private suspend fun ApplicationCall.withParsedRequest(
  appCtx: ApplicationContext,
  workspace: Path,
  fn: suspend (context: ImportContext) -> Unit
) {
  contract { callsInPlace(fn, InvocationKind.EXACTLY_ONCE) }

  var details: ImportRequest? = null
  var payload: Path? = null

  receiveMultipart().forEachPart { part ->
    try {
      when (part.name) {
        FormField.Details -> {
          reqNull(details, FormField.Details)
          details = part.parseAsJson(IMPORT_DETAILS_MAX_SIZE)
        }

        FormField.Payload -> {
          reqNull(payload, FormField.Payload)
          payload = part.handlePayload(workspace, IMPORT_PAYLOAD_FILE_NAME)
        }
      }
    } finally {
      part.dispose()
    }
  }

  reqNotNull(details, FormField.Details)
  reqNotNull(payload, FormField.Payload)

  details!!.also { deets ->
    deets.validate()
    fn(ImportContext(workspace, appCtx.config.customPath, deets, payload!!, appCtx.config.importScript))
  }
}

/**
 * Validates the posted details about a dataset being import processed.
 */
private fun ImportRequest.validate() {
  if (meta.type.name.toString().isBlank())
    throw BadRequestException("type.name cannot be blank")
  if (meta.type.version.isBlank())
    throw BadRequestException("type.version cannot be blank")

  if (meta.installTargets.isEmpty())
    throw BadRequestException("projects cannot be empty")
  for (project in meta.installTargets)
    if (project.isBlank())
      throw BadRequestException("projects cannot contain a blank string")

  if (meta.name.isBlank())
    throw BadRequestException("name cannot be blank")

  for (dependency in meta.dependencies) {
    if (dependency.identifier.isBlank())
      throw BadRequestException("dependency.resourceIdentifier cannot be blank")
    if (dependency.version.isBlank())
      throw BadRequestException("dependency.resourceVersion cannot be blank")
    if (dependency.displayName.isBlank())
      throw BadRequestException("dependency.resourceDisplayName cannot be blank")
  }
}
