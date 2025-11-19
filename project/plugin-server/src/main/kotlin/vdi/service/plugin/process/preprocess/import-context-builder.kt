package vdi.service.plugin.process.preprocess

import io.ktor.http.ContentType
import io.ktor.http.content.PartData
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
import kotlin.io.path.createDirectories
import vdi.io.plugin.requests.FormField
import vdi.io.plugin.requests.ImportRequest
import vdi.io.plugin.responses.ServerErrorResponse
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.context.DataDictionaryDirectoryName
import vdi.service.plugin.server.context.handlePayload
import vdi.service.plugin.server.context.reqNotNull
import vdi.service.plugin.server.context.reqNull
import vdi.service.plugin.util.parseAsJson
import vdi.util.fs.TempFiles

private const val IMPORT_PAYLOAD_FILE_NAME = "import.zip"
private const val IMPORT_DETAILS_MAX_SIZE  = 16384uL

@OptIn(ExperimentalContracts::class)
suspend fun ApplicationCall.withImportContext(
  appCtx: ApplicationContext,
  fn: suspend (importCtx: ImportContext) -> Unit,
) {
  contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }

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
  contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }

  var details: ImportRequest? = null
  var payload: Path? = null
  val dataDictPath: Path = workspace.resolve(DataDictionaryDirectoryName)
  var errored = false

  receiveMultipart().forEachPart { part ->
    try {
      when (part) {
        is PartData.FormItem -> {
          if (part.name == FormField.Details) {
            reqNull(details, FormField.Details)
            details = part.parseAsJson(IMPORT_DETAILS_MAX_SIZE)
          }
        }

        is PartData.FileItem -> when (part.name) {
          FormField.Payload -> {
            reqNull(payload, FormField.Payload)
            payload = part.handlePayload(workspace, IMPORT_PAYLOAD_FILE_NAME)
          }

          FormField.DataDict -> {
            dataDictPath.createDirectories()
            part.handlePayload(dataDictPath, part.originalFileName!!)
          }

          else -> {
            errored = true
            respondJSON500(ServerErrorResponse("received unrecognized multipart/form-data part \"${part.name}\" of type ${part::class}"))
          }
        }

        else -> {
          errored = true
          respondJSON500(ServerErrorResponse("received unrecognized multipart/form-data part \"${part.name}\" of type ${part::class}"))
        }
      }
    } finally {
      part.dispose()
    }
  }

  if (errored)
    return

  reqNotNull(details, FormField.Details)
  reqNotNull(payload, FormField.Payload)

  details.also { deets ->
    deets.validate()
    fn(ImportContext(
      workspace,
      appCtx.config.customPath,
      deets,
      payload,
      dataDictPath,
      appCtx.config.importScript,
    ))
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
