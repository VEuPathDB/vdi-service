package vdi.service.plugin.process.install.meta

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.UnsupportedMediaTypeException
import io.ktor.server.request.contentType
import io.ktor.server.request.receiveMultipart
import io.ktor.server.request.receiveStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import vdi.io.plugin.requests.FormField
import vdi.io.plugin.requests.InstallMetaRequest
import vdi.json.JSON
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.context.*
import vdi.service.plugin.util.parseAsJson
import vdi.util.io.BoundedInputStream

// Max allowed size of the post body: 32KiB
private const val MAX_INPUT_BYTES = 32768uL

suspend fun ApplicationCall.withInstallMetaContext(
  appCtx: ApplicationContext,
  fn: suspend (InstallMetaContext) -> Unit,
) {
  if (!request.contentType().match(ContentType.Application.Json))
    throw UnsupportedMediaTypeException(request.contentType())

  val body = try {
    withContext(Dispatchers.IO) {
      JSON.readValue<InstallMetaRequest>(BoundedInputStream(this@withInstallMetaContext.receiveStream(), MAX_INPUT_BYTES))
    }
  } catch (e: JacksonException) {
    throw BadRequestException("Could not parse request body as JSON", e)
  }

  body.validate()

  withDoubleTempDirs { workspace, propsDir -> withContext(Dispatchers.IO) {
    withParsedRequest(appCtx, workspace, propsDir, fn)
  } }
}

@OptIn(ExperimentalContracts::class)
private suspend fun ApplicationCall.withParsedRequest(
  appCtx: ApplicationContext,
  workspace: Path,
  propsDir: Path,
  fn: suspend (context: InstallMetaContext) -> Unit,
) {
  contract { callsInPlace(fn, InvocationKind.EXACTLY_ONCE) }

  var details: InstallMetaRequest? = null

  receiveMultipart().forEachPart { part ->
    try {
      when (part.name) {
        FormField.Details -> {
          reqNull(details, FormField.Details)
          details = part.parseAsJson(MAX_INPUT_BYTES)
        }

        FormField.DataDict -> {
          part.handlePayload(propsDir, (part as PartData.FileItem).originalFileName!!)
        }
      }
    } finally {
      part.dispose
    }
  }

  reqNotNull(details, FormField.Metadata)

  withDatabaseDetails(details.installTarget, details.meta.type) {
    fn(InstallMetaContext(
      workspace          = workspace,
      customPath         = appCtx.config.customPath,
      request            = details,
      installPath        = appCtx.pathFactory.makePath(details.installTarget, details.vdiID),
      databaseConfig     = it,
      scriptConfig       = appCtx.config.installMetaScript,
      dataPropertiesPath = propsDir,
    ))
  }
}

private fun InstallMetaRequest.validate() {

  if (installTarget.isBlank())
    throw BadRequestException("projectID cannot be blank")

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
